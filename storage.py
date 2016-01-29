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

    def connect(self):
        if self.uri is None or self._db_name is None:
            raise Exception("Storage hasn't been configured")

        _log.info("Connecting to '{}'".format(self.uri))
        self._client = MongoClient(self._uri)
        assert self._client is not None
        self._db = self._client[self._db_name]
        assert self._db is not None

    def insert(self, collection, documents):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        if len(documents) == 0:
            raise ValueError("Document list is empty. Nothing to insert.")

        _log.info("Inserting documents into '{}'".format(collection))
        return self._db[collection].insert(documents)

    def update(self, collection, selector, options):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Updating document in '{}'".format(collection))
        return self._db[collection].update(selector, options)

    def remove(self, collection, spec_or_id=None):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Removing documents from '{}'".format(collection))
        return self._db[collection].remove(spec_or_id)

    def aggregate(self, collection, stages):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Aggregating documents in '{}'".format(collection))
        cursor = self._db[collection].aggregate(stages)
        return [d for d in cursor['result']]

    def count(self, collection, selector):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Counting documents in '{}'".format(collection))
        return self._db[collection].count(selector)

    def find(self, collection, selector, projection=None, order=None):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Querying documents in '{}'".format(collection))
        cursor = self._db[collection].find(selector,projection)

        if order:
            cursor = cursor.sort (order)

        return [d for d in cursor]

    def find_one(self, collection, selector, projection=None):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Querying single document in '{}'".format(collection))
        return self._db[collection].find_one(selector,projection)

    def distinct (self, collection, field, query=None):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Querying distinct {} documents in '{}'".format(field, collection))
        return self._db[collection].distinct (field, query) 

    def create_index(self, collection, selector):
        if self._db is None:
            raise Exception("Not connected to storage. Did you call connect()?")

        _log.info("Creating index in '{}'".format(collection))
        return self._db[collection].create_index(selector)

storage = Storage(uri='mongodb://chelonium.cmbi.umcn.nl', db_name='whynot',)
