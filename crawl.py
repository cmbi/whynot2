#!/usr/bin/python

import sys,os,re,shutil

from time import time
from ftplib import FTP
from storage import storage
from defs import CRAWLTYPE_LINE as LINE, CRAWLTYPE_FILE as FILE
from utils import download, entries_by_pdbid, get_present_entries, get_missing_entries, read_http, parse_regex

def remove_changed(databank):

    pattern = parse_regex(databank['regex'])
    for entry in get_present_entries(databank['name']):

        path = entry['filepath']
        if not os.path.isfile(path) or os.path.getmtime(path)!=entry['mtime'] or \
                databank['crawltype']==FILE and not pattern.search(path):
            storage.remove('entries',{'databank_name':databank['name'],'pdbid':entry['pdbid']})

def crawl_files(databank, path):

    present_entries_bypdbid = entries_by_pdbid(get_present_entries(databank['name']))
    record_pdbids = entries_by_pdbid(storage.find('entries',{'databank_name':databank['name']}, {'pdbid':1}))
    pattern = parse_regex(databank['regex'])

    for root, dirs, files in os.walk(path):
        for f in files:
            f = os.path.join(root,f)

            m = pattern.search(f)
            if not m:
                continue

            entry = {
                'databank_name': databank['name'],
                'pdbid': m.group(1).lower(),
                'filepath': f,
                'mtime': os.path.getmtime(f)
            }
            if entry['pdbid'] in present_entries_bypdbid:
                continue

            if entry['pdbid'] in record_pdbids:
                storage.update('entries', {'databank_name':databank['name'], 'pdbid':entry['pdbid']}, entry)
            else:
                storage.insert('entries', entry)

def crawl_lines(databank, path):

    present_entries_bypdbid = entries_by_pdbid(get_present_entries(databank['name']))
    record_pdbids = entries_by_pdbid(storage.find('entries',{'databank_name':databank['name']}, {'pdbid':1}))
    pattern = parse_regex(databank['regex'])

    mtime = time()
    if path.startswith('http://'):
        h = read_http(path).split('\n')
    elif path.startswith('ftp://'):
        if path.endswith('/'):
            host = path.split('/')[2]
            _dir = path[7 + len(host): ]
            ftp = FTP(host)
            ftp.login()
            h = []
            for f in ftp.nlst(_dir):
                h.append(path + f)
            ftp.quit()
        else:
            h = read_http(path).split('\n')
    else:
        mtime = os.path.getmtime(path)
        h = open(path,'r')

    for line in h:

        m = pattern.match(line)
        if not m:
            continue

        entry = {
            'databank_name': databank['name'],
            'pdbid': m.group(1).lower(),
            'filepath': path,
            'mtime': mtime
        }
        if entry['pdbid'] in present_entries_bypdbid:
            continue

        if entry['pdbid'] in record_pdbids:
            storage.update('entries', {'databank_name':databank['name'], 'pdbid':entry['pdbid']}, entry)
        else:
            storage.insert('entries', entry)

    if 'close' in dir(h):
        h.close()

if not len(sys.argv) == 3:
    print 'Usage: %s [databank name] [source]' % sys.argv[0]
    sys.exit(0)

databank_name = sys.argv[1]
source = sys.argv[2]

databank = storage.find_one('databanks', {'name':databank_name, 'crawltype':{'$in':[LINE,FILE]}})
if not databank:
    raise Exception('not found or unknown crawl type: ' + databank_name)

remove_changed(databank)

if source.startswith('http://') or source.startswith('ftp://') or os.path.isfile(source):
    crawl_lines(databank, source)
elif os.path.isdir(source):
    crawl_files(databank, source)
