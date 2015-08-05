#!/usr/bin/python

import sys,os,re,shutil

from time import time
from ftplib import FTP
from storage import storage
from defs import CRAWLTYPE_LINE as LINE, CRAWLTYPE_FILE as FILE
from utils import download, entries_by_pdbid, get_present_entries, get_missing_entries, read_http, parse_regex

def get_pathnames (path):

    if path.startswith ("ftp://"):

        if not path.endswith ('/'):
            path = path + '/'

        host = path.split('/')[2]
        _dir = path [7 + len(host): ]
        ftp = FTP (host)
        ftp.login ()
        h = []
        for f in ftp.nlst(_dir):
            h.append (path + f)

        ftp.quit()

        return h

    elif os.path.isdir (path):

        h = []
        for root, dirs, files in os.walk (path):
            for f in files:
                if 'obsolete' in f or os.path.splitext (f)[1] in ['.gif', '.html']:
                    continue # skip images and web pages to save time

                f = os.path.join (root, f)
                h.append (f)

        return h
    else:
        raise Exception ("invalid path to get files from: " + path)

def get_lines (path):

    if path.startswith ('http://') or path.startswith ('ftp://'):

        return read_http (path).split ('\n')

    elif os.path.isfile (path):

        return open (path, 'r').readlines ()

    else:
        raise Exception ("invalid path to get lines from: " + path)

def remove_changed (databank, lines=[]):

    pattern = parse_regex(databank['regex'])

    line_matches = {}
    if databank ['crawltype'] == LINE:

        for line in lines:
            m = pattern.search (line)
            if m:
                line_matches [m.group (1)] = line

    for entry in get_present_entries (databank['name']):

        path = entry ['filepath']
        if databank ['crawltype'] == FILE and \
                (os.path.getmtime (path) != entry['mtime'] or \
                 not pattern.search (path)):

            storage.remove ('entries', {'databank_name': databank['name'], 'pdbid': entry['pdbid']})

        elif databank ['crawltype'] == LINE and \
                (os.path.getmtime (path) != entry['mtime'] or \
                 entry ['pdbid'] not in line_matches):

            storage.remove ('entries', {'databank_name': databank['name'], 'pdbid': entry['pdbid']})

def crawl_files (databank, pathnames):

    present_entries_bypdbid = entries_by_pdbid (get_present_entries(databank['name']))
    record_pdbids = entries_by_pdbid(storage.find('entries',{'databank_name':databank['name']}, {'pdbid':1}))
    pattern = parse_regex (databank['regex'])

    for f in pathnames:

        m = pattern.search(f)
        if not m:
            continue

        mtime = time()
        if os.path.isfile (f):
            mtime = os.path.getmtime (f)

        entry = {
            'databank_name': databank['name'],
            'pdbid': m.group(1).lower(),
            'filepath': f,
            'mtime': mtime
        }
        if entry['pdbid'] in present_entries_bypdbid:
            continue

        if entry['pdbid'] in record_pdbids:
            storage.update('entries', {'databank_name':databank['name'], 'pdbid':entry['pdbid']}, entry)
        else:
            storage.insert('entries', entry)

def crawl_lines (databank, filepath, lines):

    present_entries_bypdbid = entries_by_pdbid(get_present_entries(databank['name']))
    record_pdbids = entries_by_pdbid(storage.find('entries',{'databank_name':databank['name']}, {'pdbid':1}))
    pattern = parse_regex(databank['regex'])

    mtime = time()
    if os.path.isfile (filepath):
        mtime = os.path.getmtime (filepath)

    for line in lines:

        m = pattern.search (line)
        if not m:
            continue

        entry = {
            'databank_name': databank['name'],
            'pdbid': m.group(1).lower(),
            'filepath': filepath,
            'mtime': mtime
        }
        if entry['pdbid'] in present_entries_bypdbid:
            continue

        if entry['pdbid'] in record_pdbids:
            storage.update('entries', {'databank_name':databank['name'], 'pdbid':entry['pdbid']}, entry)
        else:
            storage.insert('entries', entry)

if not len(sys.argv) == 3:
    print 'Usage: %s [databank name] [source]' % sys.argv[0]
    sys.exit(0)

databank_name = sys.argv[1]
source = sys.argv[2]

databank = storage.find_one('databanks', {'name':databank_name, 'crawltype':{'$in':[LINE,FILE]}})
if not databank:
    raise Exception('not found or unknown crawl type: ' + databank_name)

if source.startswith('http://') or source.startswith('ftp://') or os.path.isfile(source):

    lines = get_lines (source)

    remove_changed (databank, lines)
    crawl_lines (databank, source, lines)

elif os.path.isdir(source):

    files = get_pathnames (source)

    remove_changed (databank)
    crawl_files (databank, files)
