import logging
from argparse import ArgumentParser

from whynot.storage import storage
from whynot.crawlers import crawl
from whynot.default_settings import DATABANKS, MONGODB_URI, MONGODB_DB_NAME


logging.basicConfig(level=logging.DEBUG)


argument_parser = ArgumentParser("whynot crawl a databank")
argument_parser.add_argument('databank')

arguments = argument_parser.parse_args()

databanks = [db for db in DATABANKS if db['name'] == arguments.databank]
if len(databanks) <= 0:
    raise RuntimeError("No such databank: %s" % arguments.databank)

storage.uri = MONGODB_URI
storage.db_name = MONGODB_DB_NAME

crawl(databanks[0])
