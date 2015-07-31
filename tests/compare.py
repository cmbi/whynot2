#!/usr/bin/python

import sys
import os
import commands

sys.path.append (os.path.join(os.path.dirname (__file__), '..'))
from storage import storage
from sets import Set

psql_defs = 'export PGPASSWORD=oon6oo4J'
psql_call = 'psql whynot2 -h cmbi11 -U whynotuser'

# Compare entries

entries_mongo = Set ()
entries_psql = Set ()

for entry in storage.find ('entries', {}):
    entries_mongo.add ('%s,%s' % (entry ['databank_name'], entry ['pdbid']))

query='SELECT name, pdbid FROM databank, entry WHERE databank_id=databank.id;'

for line in commands.getoutput ('%s; echo \'%s\' | %s' % (psql_defs, query, psql_call)).split ('\n'):
    if '|' in line:

        databank_name, pdbid = line.split ('|')
        entry = '%s,%s' % (databank_name.strip (), pdbid.strip ())
        entries_psql.add (entry)

        if entry not in entries_mongo:
            print 'entry absent in mongo:', entry

for entry in entries_mongo:
    if entry not in entries_psql:
        print 'entry absent in psql:', entry
