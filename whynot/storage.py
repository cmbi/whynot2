import logging
from time import strftime, gmtime

from pymongo import MongoClient, ASCENDING, DESCENDING

from whynot.models.status import PRESENT, VALID, OBSOLETE, ANNOTATED, UNANNOTATED, MISSING
from whynot.models.entry import Entry
from whynot.domain.databank import get_databank
from whynot.settings import settings

_log = logging.getLogger(__name__)


class _DBObject:
    def __init__(self, db_uri, db_name):
        self.db_uri = db_uri
        self.db_name = db_name

    def __enter__(self):
        self._client = MongoClient(self.db_uri)
        assert self._client is not None

        self._db = self._client[self.db_name]
        assert self._db is not None

        return self._db

    def __exit__(self, type_, value, traceback):
        self._client.close()

class Storage:
    def __init__(self, db_uri=None, db_name=None):
        self.db_uri = db_uri
        self.db_name = db_name

    def replace_entries(self, databank, entries):
        with _DBObject(self.db_uri, self.db_name) as db:
            db.entries.create_index([("databank_name", ASCENDING), ("status", ASCENDING)])
            db.entries.create_index("pdbid")
            db.entries.create_index([("mtime", ASCENDING)])

            pdbids = []
            for entry in entries:
                _log.debug("replacing {} entry {}".format(entry.databank_name, entry.pdbid))

                db.entries.replace_one({'databank_name': entry.databank_name,
                                        'pdbid': entry.pdbid}, entry.__dict__(), upsert=True)
                pdbids.append(entry.pdbid)

            # Remove everything that's not in the inserted set.
            for entry in list(db.entries.find({'databank_name': databank.name})) :
                if entry['pdbid'] not in pdbids:
                    _log.debug("removing {} entry {}".format(databank.name, entry['pdbid']))
                    db.entries.remove({'databank_name': databank.name, 'pdbid': entry['pdbid']})

    def get_unique_comments(self):
        with _DBObject(self.db_uri, self.db_name) as db:
            client = MongoClient(self.db_uri)
            assert client is not None

            db = client[self.db_name]
            assert db is not None

            return list(db.entries.find({'comment': {'$exists': True}}).distinct('comment'))

    def get_recent_annotations(self, count):
        with _DBObject(self.db_uri, self.db_name) as db:
            return [{'comment': e['comment'],
                     'date': strftime(settings['DATE_FORMAT'], gmtime(float(e['mtime']))),
                     'databank_name': e['databank_name'],
                     'pdbid': e['pdbid']} for e in db.entries.find({'comment': {'$exists': True}})
                                                             .sort('mtime', DESCENDING)
                                                             .limit(count)]

    def get_recent_files(self, count):
        with _DBObject(self.db_uri, self.db_name) as db:
            unique_files = {}
            for e in db.entries.find({'$or': [{'status': "VALID"}, {'status': "OBSOLETE"}]}).sort('mtime', DESCENDING):
                databank = get_databank(e['databank_name'])
                path = databank.get_entry_url(e['pdbid'])
                unique_files[path] = {'path': path,
                                      'date': strftime(settings['DATE_FORMAT'], gmtime(float(e['mtime'])))}
                if len(unique_files) >= count:
                    break

            return list(unique_files.values())

    def get_all_entries(self):
        with _DBObject(self.db_uri, self.db_name) as db:
            return [Entry.from_dict(e) for e in db.entries.find({})]

    def find_entry_by_pdbid(self, databank_name, pdbid):
        with _DBObject(self.db_uri, self.db_name) as db:
            d = db.entries.find_one({'databank_name': databank_name, 'pdbid': pdbid})
            if d is None:
                return Entry(databank_name, pdbid, UNANNOTATED)

            return Entry.from_dict(d)

    def find_entries_by_pdbid(self, pdbid):
        with _DBObject(self.db_uri, self.db_name) as db:
            return [Entry.from_dict(e) for e in db.entries.find({'pdbid': pdbid})]

    def find_entries_by_status(self, databank_name, status):
        if status == PRESENT:
            return self.find_entries_by_status(databank_name, VALID) + \
                   self.find_entries_by_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.find_entries_by_status(databank_name, ANNOTATED) + \
                   self.find_entries_by_status(databank_name, UNANNOTATED)
        else:
            with _DBObject(self.db_uri, self.db_name) as db:
                return [Entry.from_dict(e) for e in db.entries.find({'databank_name': databank_name,
                                                                     'status': str(status)})]

    def find_entries_by_comment(self, databank_name, comment):
        with _DBObject(self.db_uri, self.db_name) as db:
            return [Entry.from_dict(e) for e in self.db.entries.find({'databank_name': databank_name,
                                                                      'comment': comment})]

    def count_entries_with_status(self, databank_name, status):
        if status == PRESENT:
            return self.count_entries_with_status(databank_name, VALID) + \
                   self.count_entries_with_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.count_entries_with_status(databank_name, ANNOTATED) + \
                   self.count_entries_with_status(databank_name, UNANNOTATED)
        else:
            with _DBObject(self.db_uri, self.db_name) as db:
                return db.entries.count({'databank_name': databank_name,
                                         'status': str(status)})

    def count_all_entries(self):
        with _DBObject(self.db_uri, self.db_name) as db:
            return db.entries.count({})


storage = Storage()
