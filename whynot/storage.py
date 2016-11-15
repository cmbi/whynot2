import logging

from bson.objectid import ObjectId
from pymongo import MongoClient


_log = logging.getLogger(__name__)


class Storage(object):
    def __init__(self, uri=None, db_name=None):
        self._db = None
        self._db_name = db_name
        self._client = None
        self._uri = uri

        if self._uri is not None and self._db_name is not None:
            self.connect()
            assert self._db is not None

    @property
    def db(self):
        if self._db is None:
            self.connect()
            assert self._db is not None
        return self._db

    @property
    def db_name(self):
        return self._db_name

    @db_name.setter
    def db_name(self, db_name):
        self._db_name = db_name

    @property
    def uri(self):
        return self._uri

    @uri.setter
    def uri(self, uri):
        self._uri = uri

    def connect (self):
        if self.uri is None or self._db_name is None:
            raise Exception("Storage hasn't been configured")

        _log.info("Connecting to '{}'".format(self.uri))
        self._client = MongoClient(self._uri)
        assert self._client is not None

        self._db = self._client[self._db_name]
        assert self._db is not None


storage = Storage()
