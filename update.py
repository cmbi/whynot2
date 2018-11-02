import logging
import sys

from whynot.update import update_all
from whynot.storage import storage
from whynot.settings import settings


logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)


storage.connect(settings['MONGODB_URI'], settings['MONGODB_DB_NAME'])


update_all()
