#!/usr/bin/python

import os,sys,re

from sets import Set

from utils import (get_obsolete_entries, get_present_entries, get_valid_entries, get_entries_with_comment,
                   get_missing_entries, get_annotated_entries, get_unannotated_entries, get_entries_with_pdbid)
from storage import storage

# This is simply a commandline tool for quick listing of the database contents.

usage='Usage: %s [DB] [present|missing|valid|obsolete|annotated|unannotated|comment:*|pdbid:????]'%sys.argv[0]
if len(sys.argv) < 2:
    print usage
    sys.exit(0)

dbname=sys.argv[1]

if len(sys.argv) == 3:
    category=sys.argv[2]

    entries=[]
    if category.lower()=='present':
        entries=get_present_entries(dbname)
    elif category.lower()=='missing':
        entries=get_missing_entries(dbname)
    elif category.lower()=='valid':
        entries=get_valid_entries(dbname)
    elif category.lower()=='obsolete':
        entries=get_obsolete_entries(dbname)
    elif category.lower()=='annotated':
        entries=get_annotated_entries(dbname)
    elif category.lower()=='unannotated':
        entries=get_unannotated_entries(dbname)
    elif category.lower().startswith('comment:'):
        entries=get_entries_with_comment(dbname,category[8:].strip())
    elif category.lower().startswith('pdbid:'):
        print get_entries_with_pdbid(dbname,category[6:].strip())
    else:
        print usage
        sys.exit(0)

    for entry in entries:
        print entry['pdbid']

else:
    print 'valid', len(get_valid_entries(dbname))
    print 'obsolete', len(get_obsolete_entries(dbname))
    print 'present', len(get_present_entries(dbname))
    print 'annotated', len(get_annotated_entries(dbname))
    print 'unannotated', len(get_unannotated_entries(dbname))
    print 'missing', len(get_missing_entries(dbname))
