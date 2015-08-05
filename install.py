#!/usr/bin/python

from storage import storage
import re

from defs import CRAWLTYPE_LINE as LINE, CRAWLTYPE_FILE as FILE

def createDatabank(name,reference,filelink,regex,crawltype,parent_name=None):

    doc = {
        'name':name,
        'crawltype':crawltype,
        'filelink':filelink,
        'reference':reference,
        'regex':regex
    }

    if parent_name:
        doc['parent_name'] = parent_name

    return doc


docs = []

docs.append(createDatabank('MMCIF','http://www.wwpdb.org/',
    'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/mmCIF/${PART}/${PDBID}.cif.gz',
    re.compile(r'.*/([\w]{4})\.cif(\.gz)?'),FILE))
docs.append(createDatabank('PDB','http://www.wwpdb.org/',
    'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/pdb/${PART}/pdb${PDBID}.ent.gz',
    re.compile(r'.*/pdb([\w]{4})\.ent(\.gz)?'),FILE,'MMCIF'))
docs.append(createDatabank('BDB','http://www.cmbi.ru.nl/bdb/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/bdb/${PART}/${PDBID}/${PDBID}.bdb',
    re.compile(r'.*/([\w]{4})\.bdb'),FILE,'PDB'))
docs.append(createDatabank('DSSP','http://swift.cmbi.ru.nl/gv/dssp/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp/${PDBID}.dssp',
    re.compile(r'.*/([\w]{4})\.dssp'),FILE,'MMCIF'))
docs.append(createDatabank('HSSP','http://swift.cmbi.ru.nl/gv/hssp/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/hssp/${PDBID}.hssp.bz2',
    re.compile(r'.*/([\w]{4})\.hssp.bz2'),FILE,'DSSP'))
docs.append(createDatabank('PDBFINDER','http://swift.cmbi.ru.nl/gv/pdbfinder/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder/PDBFIND.TXT.gz',
    re.compile(r'ID           : ([\w]{4})'),LINE,'PDB'))
docs.append(createDatabank('PDBFINDER2','http://swift.cmbi.ru.nl/gv/pdbfinder/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/pdbfinder2/PDBFIND2.TXT.gz',
    re.compile(r'ID           : ([\w]{4})'),LINE,'PDBFINDER'))
docs.append(createDatabank('NMR','http://www.bmrb.wisc.edu/',
    'ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/nmr_restraints/${PDBID}.mr.gz',
    re.compile(r'.*/([\w]{4}).mr.gz'),FILE,'PDB'))
docs.append(createDatabank('NRG','http://www.rcsb.org/pdb/statistics/holdings.do',
    'http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?pdb_id=${PDBID}',
    re.compile(r'([0-9][0-9a-zA-Z]{3})'),LINE,'NMR'))
docs.append(createDatabank('NRG-DOCR','http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet',
    'http://restraintsgrid.bmrb.wisc.edu/NRG/MRGridServlet?block_text_type=3-converted-DOCR&pdb_id=${PDBID}',
    re.compile(r'([0-9][0-9a-zA-Z]{3})'),LINE,'NRG'))
docs.append(createDatabank('RECOORD','http://www.ebi.ac.uk/msd-srv/docs/NMR/recoord/main.html',
    'http://www.ebi.ac.uk/msd/NMR/recoord/entries/${PDBID}.html',
    re.compile(r'.*/([\w]{4})_cns_w\.pdb'),FILE,'NRG'))
docs.append(createDatabank('NRG-CING','http://nmr.cmbi.ru.nl/NRG-CING/',
    'http://nmr.cmbi.ru.nl/NRG-CING/data/${PART}/${PDBID}/${PDBID}.cing',
    re.compile(r'([0-9][0-9a-zA-Z]{3})'),LINE,'NMR'))
docs.append(createDatabank('STRUCTUREFACTORS','http://www.pdb.org/',
    'ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/structure_factors/${PART}/r${PDBID}sf.ent.gz',
    re.compile(r'.*/r([\w]{4})sf\.ent\.gz'),FILE,'MMCIF'))
docs.append(createDatabank('PDBREPORT','http://swift.cmbi.ru.nl/gv/pdbreport/',
    'http://www.cmbi.ru.nl/pdbreport/cgi-bin/nonotes?PDBID=${PDBID}',
    re.compile(r'pdbreport\/\w{2}\/(\w{4})\/pdbout\.txt'),FILE,'PDB'))
docs.append(createDatabank('PDB_REDO','http://www.cmbi.ru.nl/pdb_redo/',
    'http://www.cmbi.ru.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=${PDBID}',
    re.compile(r'(\w{4})_final\.pdb'),FILE,'STRUCTUREFACTORS'))
docs.append(createDatabank('DSSP_REDO','http://swift.cmbi.ru.nl/gv/dssp/',
    'ftp://ftp.cmbi.ru.nl/pub/molbio/data/dssp_redo/${PDBID}.dssp',
    re.compile(r'.*/([\w]{4})\.dssp'),FILE,'PDB_REDO'))

for lis in ['dsp','iod','sbh','sbr','ss1','ss2','tau','acc','cal','wat','cc1','cc2','cc3','chi']:

    docs.append(createDatabank('WHATIF_PDB_%s' % lis, 'http://swift.cmbi.ru.nl/whatif/',
        'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/pdb/%s/${PDBID}/${PDBID}.%s.bz2' % (lis, lis),
        re.compile(r'.*/([\w]{4})\.' + lis + r'(\.bz2)?$'),FILE,'PDB'))
    docs.append(createDatabank('WHATIF_REDO_%s' % lis, 'http://swift.cmbi.ru.nl/whatif/',
        'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/redo/%s/${PDBID}/${PDBID}.%s.bz2' % (lis, lis),
        re.compile(r'.*/([\w]{4})\.' + lis + r'(\.bz2)?$'),FILE,'PDB_REDO'))

scenames = { 'ss2': 'sym-contacts', 'iod': 'ion-sites'}
for lis in scenames:

    docs.append(createDatabank('PDB_SCENES_%s' % lis, 'http://www.cmbi.ru.nl/pdb-vis/',
        'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/pdb/scenes/%s/${PDBID}/${PDBID}_%s.sce' % (lis, scenames[lis]),
        re.compile(r'.*/([\w]{4})_' + scenames[lis] + r'\.sce'),FILE,'WHATIF_PDB_%s' % lis))
    docs.append(createDatabank('REDO_SCENES_%s' % lis, 'http://www.cmbi.ru.nl/pdb-vis/',
        'ftp://ftp.cmbi.ru.nl/pub/molbio/data/wi-lists/redo/scenes/%s/${PDBID}/${PDBID}_%s.sce' % (lis, scenames[lis]),
        re.compile(r'.*/([\w]{4})_' + scenames[lis] + r'\.sce'),FILE,'WHATIF_REDO_%s' % lis))

storage.create_index('databanks','name')
storage.create_index('entries','databank_name')
storage.create_index('entries','pdbid')
storage.create_index('entries','comment')
storage.insert('databanks',docs)
