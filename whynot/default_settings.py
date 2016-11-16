import datetime
import os

from celery.schedules import crontab
from kombu import Exchange, Queue

# TODO: Don't do this
from whynot.annotators import *
from whynot.crawlers import *

# Celery
default_exchange = Exchange('whynot', type='direct')

CELERY_TASK_SERIALIZER = 'json'
CELERY_ACCEPT_CONTENT = ['json']
CELERY_BROKER_URL = 'amqp://guest@whynot_rabbitmq_1'
CELERYBEAT_SCHEDULE = {
    # Every day at midnight
    'update': {
        'task': 'whynot.tasks.update',
        #'schedule': crontab(hour=0, minute=0),
        'schedule': crontab(minute='*/5'),
    },
}

# Mongo
MONGODB_URI = "mongodb://whynot_mongo_1"
MONGODB_DB_NAME = "whynot"

# URLs
URL_WWPDB = 'ftp://ftp.wwpdb.org/pub/pdb/derived_data/pdb_entry_type.txt'

# Databanks
#
# TODO: describe fields here
#
# name: name of the databank
# crawler: FILE, DIR
#             source=file (parse file content)
#             source=dir (parse filenames)
DATABANK_ROOT = '/srv/data'
DATABANKS = [
    {
        'name': 'mmcif',
        'annotator': None,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/mmCIF/${PART}/${PDBID}.cif.gz',
        'parent': None,
        'reference': 'http://www.wwpdb.org/',
        'regex': r'.*/([\w]{4})\.cif(\.gz)?',
        'source': os.path.join(DATABANK_ROOT, 'mmCIF'),
    },
    {
        'name': 'pdb',
        'annotator': None,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/pdb/${PART}/pdb${PDBID}.ent.gz',
        'parent': 'mmcif',
        'reference': 'http://www.wwpdb.org/',
        'regex': r'.*/pdb([\w]{4})\.ent(\.gz)?',
        'source': os.path.join(DATABANK_ROOT, 'pdb', 'flat'),
    },
    {
        'name': 'bdb',
        'annotator': BdbAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/bdb/${PART}/${PDBID}/${PDBID}.bdb',
        'reference': 'http://www.cmbi.ru.nl/bdb/',
        'regex': r'.*/([\w]{4})\.bdb',
        'parent': 'pdb',
        'source': os.path.join(DATABANK_ROOT, 'bdb'),
    },
    {
        'name': 'dssp',
        'annotator': DsspAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp',
        'reference': 'http://swift.cmbi.ru.nl/gv/dssp/',
        'regex': r'.*/([\w]{4})\.dssp',
        'parent': 'mmcif',
        'source': os.path.join(DATABANK_ROOT, 'dssp'),
    },
    {
        'name': 'hssp',
        'annotator': HsspAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/hssp/${PDBID}.hssp.bz2',
        'reference': 'http://swift.cmbi.ru.nl/gv/hssp/',
        'regex': r'.*/([\w]{4})\.hssp.bz2',
        'parent': 'dssp',
        'source': os.path.join(DATABANK_ROOT, 'hssp'),
    },
    {
        'name': 'pdbfinder',
        'annotator': None,
        'crawler': FileCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder/PDBFIND.TXT.gz',
        'reference': 'http://swift.cmbi.ru.nl/gv/pdbfinder/',
        'regex': r'ID           : ([\w]{4})',
        'parent': 'pdb',
        'source': os.path.join(DATABANK_ROOT, 'pdbfinder', 'PDBFIND.TXT'),
    },
    {
        'name': 'pdbfinder2',
        'annotator': None,
        'crawler': FileCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder2/PDBFIND2.TXT.gz',
        'reference': 'http://swift.cmbi.ru.nl/gv/pdbfinder/',
        'regex': r'ID           : ([\w]{4})',
        'parent': 'pdbfinder',
        'source': os.path.join(DATABANK_ROOT, 'pdbfinder2', 'PDBFIND2.TXT'),
    },
    {
        'name': 'nmr',
        'annotator': NmrAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/nmr_restraints/${PDBID}.mr.gz',
        'reference': 'http://www.bmrb.wisc.edu/',
        'regex': r'.*/([\w]{4}).mr.gz',
        'parent': 'pdb',
        'source': os.path.join(DATABANK_ROOT, 'nmr_restraints'),
    },
    {
        'name': 'structurefactors',
        'annotator': StructureFactorsAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/structure_factors/${PART}/r${PDBID}sf.ent.gz',
        'reference': 'http://swift.cmbi.ru.nl/gv/pdbreport/',
        'regex': r'.*/r([\w]{4})sf\.ent\.gz',
        'parent': 'mmcif',
        'source': os.path.join(DATABANK_ROOT, 'structure_factors'),
    },
    {
        'name': 'pdbreport',
        'annotator': None,
        'crawler': DirCrawler,
        'filelink': 'http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=${PDBID}',
        'reference': 'http://swift.cmbi.ru.nl/gv/pdbreport/',
        'regex': r'pdbreport\/\w{2}\/(\w{4})\/pdbout\.txt',
        'parent': 'pdb',
        'source': os.path.join(DATABANK_ROOT, 'pdbreport'),
    },
    {
        'name': 'pdb_redo',
        'annotator': None,
        'crawler': DirCrawler,
        'filelink': 'http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=${PDBID}',
        'reference': 'http://www.cmbi.ru.nl/pdb_redo/',
        'regex': r'\/\w{2}\/\w{4}\/(\w{4})_final\.pdb',
        'parent': 'structurefactors',
        'source': os.path.join(DATABANK_ROOT, 'pdb_redo'),
    },
    {
        'name': 'dssp_redo',
        'annotator': DsspAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp_redo/${PDBID}.dssp',
        'reference': 'http://swift.cmbi.ru.nl/gv/dssp/',
        'regex': r'.*/([\w]{4})\.dssp',
        'parent': 'pdb_redo',
        'source': os.path.join(DATABANK_ROOT, 'dssp_redo'),
    }
]


for lis in ['dsp','iod','sbh','sbr','ss1','ss2','tau','acc','cal','wat',
            'cc1','cc2','cc3','chi']:
    DATABANKS.append({
        'name': 'whatif_pdb_%s' % lis,
        'annotator': WhatifListAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/pdb/%s/${PDBID}/${PDBID}.%s.bz2' % (lis, lis),
        'reference': 'http://swift.cmbi.ru.nl/whatif/',
        'regex': r'.*/([\w]{4})\.' + lis + r'(\.bz2)?$',
        'parent': 'pdb',
        'source': os.path.join(DATABANK_ROOT, 'wi-lists', 'pdb', lis),
    })
    DATABANKS.append({
        'name': 'whatif_redo_%s' % lis,
        'annotator': WhatifListAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/redo/%s/${PDBID}/${PDBID}.%s.bz2' % (lis, lis),
        'reference': 'http://swift.cmbi.ru.nl/whatif/',
        'regex': r'.*/([\w]{4})\.' + lis + r'(\.bz2)?$',
        'parent': 'pdb_redo',
        'source': os.path.join(DATABANK_ROOT, 'wi-lists', 'redo', lis),
    })


for lis, name in { 'ss2': 'sym-contacts', 'iod': 'ion-sites'}.iteritems():
    DATABANKS.append({
        'name': 'pdb_scenes_%s' % lis,
        'annotator': WhatifSceneAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/pdb/scenes/%s/${PDBID}/${PDBID}_%s.sce' % (lis, name),
        'reference': 'http://www.cmbi.ru.nl/pdb-vis/',
        'regex': r'.*/([\w]{4})_' + name + r'\.sce',
        'parent': 'whatif_pdb_%s' % lis,
        'source': os.path.join(DATABANK_ROOT, 'wi-lists', 'pdb', 'scenes', lis),
    })
    DATABANKS.append({
        'name': 'redo_scenes_%s' % lis,
        'annotator': WhatifSceneAnnotator,
        'crawler': DirCrawler,
        'filelink': 'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/redo/scenes/%s/${PDBID}/${PDBID}_%s.sce' % (lis, name),
        'reference': 'http://www.cmbi.ru.nl/pdb-vis/',
        'regex': r'.*/([\w]{4})_' + name + r'\.sce',
        'parent': 'whatif_redo_%s' % lis,
        'source': os.path.join(DATABANK_ROOT, 'wi-lists', 'redo', 'scenes', lis),
    })
