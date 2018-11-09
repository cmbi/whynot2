import logging
from time import strftime, gmtime

from pymongo import MongoClient, ASCENDING, DESCENDING

from whynot.models.status import PRESENT, VALID, OBSOLETE, ANNOTATED, UNANNOTATED, MISSING
from whynot.models.entry import Entry
from whynot.domain.databank import get_databank
from whynot.settings import settings

_log = logging.getLogger(__name__)

class Storage:
    def __init__(self, db_uri=None, db_name=None):
        self.db_uri = db_uri
        self.db_name = db_name

    def get_db(self):
        client = MongoClient(self.db_uri)
        assert client is not None

        db = client[self.db_name]
        assert db is not None

        return db

    def replace_entries(self, databank, entries):
        self.get_db().entries.create_index([("databank_name", ASCENDING), ("status", ASCENDING)])
        self.get_db().entries.create_index("pdbid")
        self.get_db().entries.create_index([("mtime", ASCENDING)])

        pdbids = []
        for entry in entries:
            _log.debug("replacing {} entry {}".format(entry.databank_name, entry.pdbid))

            self.get_db().entries.replace_one({'databank_name': entry.databank_name,
                                          'pdbid': entry.pdbid}, entry.__dict__(), upsert=True)
            pdbids.append(entry.pdbid)

        # Remove everything that's not in the inserted set.
        for entry in list(self.get_db().entries.find({'databank_name': databank.name})) :
            if entry['pdbid'] not in pdbids:
                _log.debug("removing {} entry {}".format(databank.name, entry['pdbid']))
                self.get_db().entries.remove({'databank_name': databank.name, 'pdbid': entry['pdbid']})

    def get_unique_comments(self):
        return list(self.get_db().entries.find({'comment': {'$exists': True}}).distinct('comment'))

    def get_recent_annotations(self, count):
        return [{'comment': e['comment'],
                 'date': strftime(settings['DATE_FORMAT'], gmtime(float(e['mtime']))),
                 'databank_name': e['databank_name'],
                 'pdbid': e['pdbid']} for e in self.get_db().entries.find({'comment': {'$exists': True}})
                                                               .sort('mtime', DESCENDING)
                                                               .limit(count)]

    def get_recent_files(self, count):
        unique_files = {}
        for e in self.get_db().entries.find({'$or': [{'status': "VALID"}, {'status': "OBSOLETE"}]}).sort('mtime', DESCENDING):
            databank = get_databank(e['databank_name'])
            path = databank.get_entry_url(e['pdbid'])
            unique_files[path] = {'path': path,
                                  'date': strftime(settings['DATE_FORMAT'], gmtime(float(e['mtime'])))}
            if len(unique_files) >= count:
                break

        return list(unique_files.values())

    def get_all_entries(self):
        return [Entry.from_dict(e) for e in self.get_db().entries.find({})]

    def find_entry_by_pdbid(self, databank_name, pdbid):
        return Entry.from_dict(self.get_db().entries.find_one({'databank_name': databank_name, 'pdbid': pdbid}))

    def find_entries_by_pdbid(self, pdbid):
        return [Entry.from_dict(e) for e in self.get_db().entries.find({'pdbid': pdbid})]

    def find_entries_by_status(self, databank_name, status):
        if status == PRESENT:
            return self.find_entries_by_status(databank_name, VALID) + \
                   self.find_entries_by_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.find_entries_by_status(databank_name, ANNOTATED) + \
                   self.find_entries_by_status(databank_name, UNANNOTATED)
        else:
            return [Entry.from_dict(e) for e in self.get_db().entries.find({'databank_name': databank_name,
                                                                       'status': str(status)})]

    def find_entries_by_comment(self, databank_name, comment):
        return [Entry.from_dict(e) for e in self.get_db().entries.find({'databank_name': databank_name,
                                                                   'comment': comment})]

    def count_entries_with_status(self, databank_name, status):
        if status == PRESENT:
            return self.count_entries_with_status(databank_name, VALID) + \
                   self.count_entries_with_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.count_entries_with_status(databank_name, ANNOTATED) + \
                   self.count_entries_with_status(databank_name, UNANNOTATED)
        else:
            return self.get_db().entries.count({'databank_name': databank_name,
                                           'status': str(status)})
    def count_all_entries(self):
        return self.get_db().entries.count({})


storage = Storage()
