import logging

from pymongo import MongoClient, ASCENDING


_log = logging.getLogger(__name__)

class Storage:
    def __init__(self):
        self._db = None

    def connect(self, db_uri, db_name):
        client = MongoClient(db_uri)
        assert client is not None

        self._db = client[db_name]
        assert self._db is not None

    def replace_entries(self, entries):
        self._db['entries'].create_index([("databank_name", ASCENDING), ("status", ASCENDING)])
        self._db['entries'].create_index("pdbid")

        for entry in entries:
            _log.debug("replacing {} entry {}".format(entry.databank_name, entry.pdbid))

            self._db['entries'].replace_one({'databank_name': entry.databank_name,
                                             'pdbid': entry.pdbid}, entry.__dict__(), upsert=True)
    def find_entries_by_pdbid(self, pdbid):
        return self._db.find({'pdbid': pdbid})

    def find_entries_by_status(self, databank_name, status):
        return self._db.find({'databank_name': databank_name,
                              'status': status})


storage = Storage()
