#!/usr/bin/python

import sys
import os
import commands

sys.path.append (os.path.join(os.path.dirname (__file__), '..'))
from storage import storage
from sets import Set

from httplib import HTTPConnection

from utils import has_annotated_parent, has_present_parent, databanks_by_name, valid_path, get_entry

databanks = databanks_by_name (storage.find ('databanks', {}))

regexes = {}
for name in databanks:
    regexes [name] = databanks [name]['regex'].try_compile ()

psql_defs = 'export PGPASSWORD=oon6oo4J'
psql_call = 'psql whynot2 -h cmbi11 -U whynotuser'

# Compare entries

entries_mongo = {}
files_mongo = {}
comments_mongo = {}
entries_psql = {}
comments_psql = {}
files_psql = {}

for entry in storage.find ('entries', {}):
    ID = '%s,%s' % (entry ['databank_name'], entry ['pdb_id'])
    entries_mongo [ID] = entry

    if 'filepath' in entry:
        files_mongo [ID] = entry ['filepath']

    if 'comment' in entry:
        comments_mongo [ID] = entry ['comment']

entry_query='SELECT name, pdb_id FROM databank, entry WHERE databank_id=databank.id;'
for line in commands.getoutput ('%s; echo \'%s\' | %s' % (psql_defs, entry_query, psql_call)).split ('\n') [2:]:

    if '|' in line:

        databank_name, pdb_id = line.split ('|')
        databank_name = databank_name.strip ()
        pdb_id = pdb_id.strip ()

        ID = '%s,%s' % (databank_name, pdb_id)
        entries_psql [ID] = {'databank_name':databank_name, 'pdb_id':pdb_id}


file_query = "SELECT name, pdb_id, path FROM entry, file, databank WHERE " + \
             "databank_id=databank.id AND file.id=file_id;"

for line in commands.getoutput ('%s; echo \'%s\' | %s' % (psql_defs, file_query, psql_call)).split ('\n') [2:]:

    if '|' in line:

        databank_name, pdb_id, path = line.split ('|')
        databank_name = databank_name.strip ()
        pdb_id = pdb_id.strip ()
        path = path.strip ()

        ID = '%s,%s' % (databank_name, pdb_id)
        entries_psql [ID]['filepath'] = path
        files_psql [ID] = path


comment_query = "SELECT name, pdb_id, text FROM comment, annotation, entry, databank WHERE " + \
                "databank_id=databank.id AND entry_id=entry.id AND comment_id=comment.id"

for line in commands.getoutput ('%s; echo \'%s\' | %s' % (psql_defs, comment_query, psql_call)).split ('\n') [2:]:

    if '|' in line:

        databank_name, pdb_id, text = line.split ('|')
        databank_name = databank_name.strip ()
        pdb_id = pdb_id.strip ()
        text = text.strip ()

        ID = '%s,%s' % (databank_name, pdb_id)

        if 'filepath' not in entries_psql [ID]:

            entries_psql [ID]['comment'] = text
            comments_psql [ID] = text


for ID in files_psql:

    databank_name, pdb_id = ID.split (',')
    path = files_psql [ID]

    if ID in files_mongo:

        if files_mongo [ID] != files_psql [ID] and \
                not valid_path (databank_name, files_mongo [ID]):

            print "different file path in mongo", ID, ':', files_mongo [ID], 'vs.', files_psql [ID]
    else:

        if has_present_parent (databank_name, pdb_id) and \
                valid_path (databank_name, path):

            print 'file absent in mongo:', ID, path

for ID in comments_psql:

    databank_name, pdb_id = ID.split (',')
    comment = comments_psql [ID]

    if ID in comments_mongo:

        if comments_mongo [ID] != comments_psql [ID]:

            # Ignore these:
            if comments_mongo [ID].lower().startswith ("too many") and comments_psql [ID].lower().startswith ("more than 20"):

                continue

            if databank_name == 'DSSP_REDO':

                DSSPID = 'DSSP,%s' % pdb_id
                if DSSPID in comments_psql and comments_psql [DSSPID].lower () == comments_mongo [ID].lower ():

                    continue

            print ID, ': mongo\'s comment differs: \'%s\' vs. \'%s\'' % (comments_mongo [ID], comments_psql [ID])

    else:

        if has_present_parent (databank_name, pdb_id):

            print 'comment absent in mongo', ID, comment

for ID in files_mongo:

    databank_name, pdb_id = ID.split (',')
    path = files_mongo [ID]

    if ID not in files_psql and not valid_path (databank_name, path):

        print "incorrect path in mongo:", ID, path, "absent in psql"

for ID in comments_mongo:

    databank_name, pdb_id = ID.split (',')
    comment = comments_mongo [ID]

    if ID not in comments_psql:

        if ID in files_psql:

            # psql has a file path instead
            path = files_psql [ID]
            if has_present_parent (databank_name, pdb_id) and \
                    valid_path (databank_name, path):

                print 'comment absent in psql:', ID, comment, "it has instead", path

