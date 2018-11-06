import logging

from pymongo import MongoClient, ASCENDING

from whynot.models.status import PRESENT, VALID, OBSOLETE, ANNOTATED, UNANNOTATED, MISSING
from whynot.models.entry import Entry

_log = logging.getLogger(__name__)

class Storage:
    def __init__(self):
        self._db = None

    def connect(self, db_uri, db_name):
        client = MongoClient(db_uri)
        assert client is not None

        self._db = client[db_name]
        assert self._db is not None

    def replace_entries(self, databank, entries):
        self._db.entries.create_index([("databank_name", ASCENDING), ("status", ASCENDING)])
        self._db.entries.create_index("pdbid")

        pdbids = []
        for entry in entries:
            _log.debug("replacing {} entry {}".format(entry.databank_name, entry.pdbid))

            self._db.entries.replace_one({'databank_name': entry.databank_name,
                                          'pdbid': entry.pdbid}, entry.__dict__(), upsert=True)
            pdbids.append(entry.pdbid)

        # Remove everything that's not in the inserted set.
        for entry in self._db.entries.find({'databank_name': databank.name}).noCursorTimeout() :
            if entry['pdbid'] not in pdbids:
                _log.debug("removing {} entry {}".format(databank.name, entry['pdbid']))
                self._db.entries.remove({'databank_name': databank.name, 'pdbid': entry['pdbid']})

    def get_all_entries(self):
        return [Entry.from_dict(e) for e in self._db.entries.find({})]

    def find_entry_by_pdbid(self, databank_name, pdbid):
        return Entry.from_dict(self._db.entries.find_one({'databank_name': databank_name, 'pdbid': pdbid}))

    def find_entries_by_pdbid(self, pdbid):
        return [Entry.from_dict(e) for e in self._db.entries.find({'pdbid': pdbid})]

    def find_entries_by_status(self, databank_name, status):
        if status == PRESENT:
            return self.find_entries_by_status(databank_name, VALID) + \
                   self.find_entries_by_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.find_entries_by_status(databank_name, ANNOTATED) + \
                   self.find_entries_by_status(databank_name, UNANNOTATED)
        else:
            return [Entry.from_dict(e) for e in self._db.entries.find({'databank_name': databank_name,
                                                                       'status': str(status)})]

    def find_entries_by_comment(self, databank_name, comment):
        return [Entry.from_dict(e) for e in self._db.entries.find({'databank_name': databank_name,
                                                                   'comment': comment})]

    def count_entries_with_status(self, databank_name, status):
        if status == PRESENT:
            return self.count_entries_with_status(databank_name, VALID) + \
                   self.count_entries_with_status(databank_name, OBSOLETE)
        elif status == MISSING:
            return self.count_entries_with_status(databank_name, ANNOTATED) + \
                   self.count_entries_with_status(databank_name, UNANNOTATED)
        else:
            return self._db.entries.count({'databank_name': databank_name,
                                           'status': str(status)})


storage = Storage()
