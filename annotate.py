import logging
from argparse import ArgumentParser

from whynot.storage import storage
from whynot.annotators import annotate
from whynot.default_settings import DATABANKS, MONGODB_URI, MONGODB_DB_NAME, URL_WWPDB
from whynot.services.wwpdb import wwpdb

logging.basicConfig(level=logging.DEBUG)


argument_parser = ArgumentParser("whynot annotate a databank")
argument_parser.add_argument('databank')

arguments = argument_parser.parse_args()

databanks = [db for db in DATABANKS if db['name'] == arguments.databank]
if len(databanks) <= 0:
    raise RuntimeError("No such databank: %s" % arguments.databank)

storage.uri = MONGODB_URI
storage.db_name = MONGODB_DB_NAME
wwpdb.url = URL_WWPDB

annotate(databanks[0])
