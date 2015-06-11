#!/usr/bin/python

import sys,os,commands

from utils import entries_by_pdbid, get_unannotated_entries, get_missing_entries, update_entries, read_http

from storage import storage
from time import time

def parse_comment(lines, entry):

    if len(lines) < 2:
        return ''

    if not lines[0].startswith('COMMENT:'):
        print lines[0], 'does not have comment'
        return ''
    comment = lines[0][8:].strip()
    for line in lines[1:]:
        line = line.replace(' ','').strip()
        if line == '%s,%s' % (entry['databank_name'], entry['pdbid']):
            return comment
        else:
            print 'not on', line
    return ''

pdbidscarbonly=[]
pdbidsnuconly=[]
pdbidsnmr=[]
pdbidsem=[]
pdbidsdiff=[]

entries_update=[]
for line in []:#read_http('ftp://ftp.wwpdb.org/pub/pdb/derived_data/pdb_entry_type.txt').split('\n'):
    if len(line.strip()) <= 0:
        continue

    pdbid, content, method = line.split()

    if content=='nuc':
        pdbidsnuconly.append(pdbid)
    elif content=='carb':
        pdbidscarbonly.append(pdbid)

    if method=='diffraction':
        pdbidsdiff.append(pdbid)
    elif method=='NMR':
        pdbidsnmr.append(pdbid)
    elif method=='EM':
        pdbidsem.append(pdbid)

for entry in get_unannotated_entries('STRUCTUREFACTORS'):

    pdbid = entry['pdbid']
    if pdbid in pdbidsnmr:
        entry['comment'] = 'NMR experiment'
        entry['mtime'] = time()
    elif pdbid in pdbidsem:
        entry['comment'] = 'Electron microscopy experiment'
        entry['mtime'] = time()

    if 'comment' in entry:
        entries_update.append(entry)

for entry in get_unannotated_entries('NMR'):

    pdbid = entry['pdbid']
    if pdbid in pdbidsdiff:
        entry['comment'] = 'Diffraction experiment'
        entry['mtime'] = time()
    elif pdbid in pdbidsem:
        entry['comment'] = 'Electron microscopy experiment'
        entry['mtime'] = time()

    if 'comment' in entry:
        entries_update.append(entry)

for entry in get_unannotated_entries('HSSP'):

    pdbid = entry['pdbid']

    inputfile = '/data/pdb/all/pdb%s.ent.gz' % pdbid
    if not os.path.isfile(inputfile):
        inputfile = '/data/mmCIF/%s.cif.gz' % pdbid

    line = commands.getoutput('/usr/bin/timeout 1s /usr/local/bin/mkhssp -a1 %s /tmp/%s.hssp.bz2 2>&1 >/dev/null' % (inputfile,pdbid))
    line = line.strip()
    if line in ['Not enough sequences in PDB file of length 25', 'No hits found', 'empty protein, or no valid complete residues']:
        entry['comment'] = line
        entry['mtime'] = time()
        entries_update.append(entry)

for dbname in ['DSSP', 'DSSP_REDO']:
    for entry in get_unannotated_entries(dbname):

        pdbid = entry['pdbid']

        if pdbid in pdbidsnuconly:
            entry['comment'] = 'Nucleic acids only'
            entry['mtime'] = time()
        elif pdbid in pdbidscarbonly:
            entry['comment'] = 'Carbohydrates only'
            entry['mtime'] = time()
        else:

            if dbname == 'DSSP':
                inputfile = '/data/pdb/all/pdb%s.ent.gz' % pdbid
                if not os.path.isfile(inputfile):
                    inputfile = '/data/mmCIF/%s.cif.gz' % pdbid
            else:
                inputfile = '/data/pdb_redo/%s/%s/%s_final.pdb' % (pdbid[1:3], pdbid, pdbid)
                if not os.path.isfile(inputfile):
                    continue

            lines = commands.getoutput('./dsspcmbi %s /tmp/%s.dssp 2>&1 >/dev/null' % (inputfile,pdbid)).split('\n')
            statement = ''
            for line in lines:

                line = line.strip()
                if line.startswith('!!!'):
                    statement = line[3:].strip()

                if line.endswith('!!!'):

                    if statement.endswith('!!!'):
                        statement = statement[:-3].strip()
                    else:
                        statement += ' ' + line[:-3].strip()

                    if statement == 'No residues with complete backbone':
                        entry['comment'] = statement
                        entry['mtime'] = time()
                        break

        if 'comment' in entry:
            entries_update.append(entry)

for entry in get_missing_entries('BDB'):

    pdbid = entry['pdbid']
    part = pdbid[1:3]
    whynotfile = '/data/bdb/%s/%s/%s.whynot' % (part, pdbid, pdbid)
    if not os.path.isfile(whynotfile):
        continue

    lines = open(whynotfile, 'r').readlines()
    comment = parse_comment(lines, entry)
    if len(comment) > 0:
        entry['comment'] = comment
        entry['mtime'] = time()
        entries_update.append(entry)

for lis in ['acc', 'cal', 'cc1', 'cc2', 'cc3', 'chi', 'dsp', 'iod', 'sbh', 'sbr', 'ss1', 'ss2', 'tau', 'wat']:
    for src in ['pdb', 'redo']:
        dbname = 'WHATIF_%s_%s' % (src.upper(), lis)

        print 'checking', dbname
        for entry in get_missing_entries(dbname):

            print 'checking',entry['pdbid']

            pdbid = entry['pdbid']
            whynotfile = '/data/wi-lists/%s/%s/%s/%s.%s.whynot' % (src, lis, pdbid, pdbid, lis)
            if not os.path.isfile(whynotfile):
                print 'not found:', whynotfile
                continue

            lines = open(whynotfile, 'r').readlines()
            comment = parse_comment(lines, entry)
            if len(comment) > 0:
                entry['comment'] = comment
                entry['mtime'] = time()
                entries_update.append(entry)

for lis in ['iod', 'ss2']:
    for src in ['pdb', 'redo']:
        dbname = '%s_SCENES_%s' % (src.upper(), lis)

        for entry in get_missing_entries(dbname):

            pdbid = entry['pdbid']
            whynotfile = '/data/wi-lists/%s/scenes/%s/%s/%s.%s.whynot' % (src, lis, pdbid, pdbid, lis)
            if not os.path.isfile(whynotfile):
#                print 'not found:', whynotfile
                continue

            lines = open(whynotfile, 'r').readlines()
            comment = parse_comment(lines, entry)
            if len(comment) > 0:
                entry['comment'] = comment
                entry['mtime'] = time()
                entries_update.append(entry)

update_entries(entries_update)
