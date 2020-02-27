#!/usr/bin/python3

import tempfile
import logging
import sys
import os
import subprocess
from ftplib import FTP
from argparse import ArgumentParser

# Must import storage before utils
import update_settings as settings
from storage import storage

storage.uri = settings.MONGODB_URI
storage.db_name = settings.MONGODB_DB_NAME
storage.connect()

from utils import entries_by_pdbid, get_unannotated_entries, get_missing_entries, read_http

from time import time

mkdssp = '/usr/local/bin/mkdssp'
mkhssp = '/usr/local/bin/mkhssp'

logging.basicConfig(stream=sys.stdout, level=logging.DEBUG)
_log = logging.getLogger(__name__)


# whynot comment files look like this:
# COMMENT: <text 1>
# <databank name>, <pdbid 1>
# <databank name>, <pdbid 2>
# <databank name>, <pdbid 3>
# COMMENT: <text 2>
# <databank name>, <pdbid 4>
# <databank name>, <pdbid 5>
# etc.


# Returns a list of triples: (comment, databank name, pdbid)
def parse_comments(file_):

    comment = None
    d = []

    for line in file_:
        line = line.strip()
        if len(line) <= 0:
            continue

        elif line.startswith('COMMENT:'):
            comment = lines[0][8:].strip()

        else:
            if comment is None:
                _log.error("comment expected, got '{}'".format(line))
            else:
                if ',' in line:
                    databank_name, pdbid = line.strip().replace(' ','').split(',')
                    databank_name.replace('-', '_')
                    d.append((comment, databank_name, pdbid))
                else:
                    raise ValueError("not the right format: '{}'".format(line))
    return d


# Searches for a specific entry in the comments.
# Returns the comment text for this entry.
def parse_comment(file_, entry):

    comment = None

    for line in file_:
        line = line.strip()
        if len(line) <= 0:
            continue

        elif comment is None:
            if not line.startswith('COMMENT:'):
                _log.error("Expecting comment on first line, got '{}'".format(line))
                return ''

            comment = lines[0][8:].strip()
        else:
            line = line.replace(' ','').replace('-', '_').strip()
            if line == '%s,%s' % (entry['databank_name'], entry['pdbid']):
                return comment

    return ''


def update_entry(entry):

    databank_name = entry['databank_name']
    pdbid = entry['pdbid']

    if storage.find_one('entries', {'databank_name': databank_name, 'pdbid': pdbid}):

        storage.update('entries', {'databank_name': databank_name, 'pdbid': pdbid}, entry)
    else:
        storage.insert('entries', entry)


def comment_entry(entry, comment):
    entry['comment'] = comment
    entry['mtime'] = time()

    update_entry(entry)


# This function gets all comment information from a whynot
# file and updates the corresponding entries with it.
def annotate_from_file(path):

    with open(path,'r') as f:
        comments = parse_comments(f)

        for text, databank_name, pdbid in comments:

            entry = {'databank_name': databank_name,
                     'pdbid': pdbid,
                     'comment': text, 'mtime': time()}

            update_entry(entry)


class EntryTypeData:
    def __init__(self):
        self.pdbids_carb = set([])
        self.pdbids_nuc = set([])

        self.pdbids_nmr = set([])
        self.pdbids_em = set([])
        self.pdbids_diffraction = set([])
        self.pdbids_other = set([])


# List the pdbids for pdb entries by category. For many missing entries,
# the category is the reason why they are missing. We base the comment on that.
#
# A pdb entry can have experimental methods: nmr, em, diffraction or other.
# Only nmr entries can have nmr-related data, only diffraction entries can have
# structure_factors data.
#
# A pdb entry can contain only carbohydrates or only nucleic acids, in
# which case no DSSP can be made.
def get_entry_types():
    data = EntryTypeData()

    _log.info('Parse wwpdb entry type record')
    for line in read_http('ftp://ftp.wwpdb.org/pub/pdb/derived_data/pdb_entry_type.txt').split('\n'):
        if len(line.strip()) <= 0:
            continue

        pdbid, content, method = line.split()

        if content=='nuc':
            data.pdbids_nuc.add(pdbid)
        elif content=='carb':
            data.pdbids_carb.add(pdbid)

        if method=='diffraction':
            data.pdbids_diffraction.add(pdbid)
        elif method=='NMR':
            data.pdbids_nmr.add(pdbid)
        elif method=='EM':
            data.pdbids_em.add(pdbid)
        elif method=='other':
            data.pdbids_other.add(pdbid)

    _log.info("{} nucleic acid only, {} carbohydrates only".format(len(data.pdbids_nuc), len(data.pdbids_carb)))
    _log.info("{} diffraction, {} nmr, {} em, {} other".format(len(data.pdbids_diffraction), len(data.pdbids_nmr),
                                                               len(data.pdbids_em), len(data.pdbids_other)))
    return data


def get_structure_factors_pdbids(check_pdbids=None):
    pdbids = set([])
    with FTP('ftp.wwpdb.org') as ftp:
        ftp.login()

        _log.info('Listing deposited structure factor files')
        ftp.cwd('/pub/pdb/data/structures/divided/structure_factors/')

        if check_pdbids is None:
            for part in ftp.nlst():
                for filename in ftp.nlst(part):
                    pdbid = filename[1: 5]
                    pdbids.add(pdbid)
        else:
            for pdbid in check_pdbids:
                part = pdbid[1: 3]
                filename = "r%ssf.ent.gz" % pdbid
                if len(ftp.nlst("%s/%s" % (part, filename))) > 0:
                    pdbids.add(pdbid)
        return pdbids


def get_nmr_restraints_pdbids(check_pdbids=None):
    pdbids = set([])
    with FTP('ftp.wwpdb.org') as ftp:
        ftp.login()

        _log.info('Listing deposited nmr restraints files')
        ftp.cwd('/pub/pdb/data/structures/divided/nmr_restraints/')

        if check_pdbids is None:
            for part in ftp.nlst():
                for filename in ftp.nlst(part):
                    pdbid = filename[0: 4]
                    pdbids.add(pdbid)
        else:
            for pdbid in check_pdbids:
                part = pdbid[1: 3]
                filename = "%s.mr.gz" % pdbid
                if len(ftp.nlst("%s/%s" % (part, filename))) > 0:
                    pdbids.add(pdbid)

    return pdbids


def get_entries_of_interest(databank_name, check_pdbids=None):
    entries = []
    if check_pdbids is not None:
        for pdbid in check_pdbids:
            entry = storage.find_one('entries', {'databank_name': databank_name, 'pdbid': pdbid})
            if entry is None:
                entry = {'pdbid': pdbid, 'databank_name': databank_name}
            entries.append(entry)
    else:
        entries = get_missing_entries(databank_name)

    return entries


def update_comments(check_pdbids=None):
    data = get_entry_types()
    data.pdbids_sf = get_structure_factors_pdbids(check_pdbids)
    data.pdbids_nr = get_nmr_restraints_pdbids(check_pdbids)

    _log.info('Generate comments for missing structure factors')
    for entry in get_entries_of_interest('STRUCTUREFACTORS', check_pdbids):

        pdbid = entry['pdbid']
        if check_pdbids is not None and pdbid not in check_pdbids:
            continue

        comment = None
        if pdbid in data.pdbids_nr:
            comment = 'NMR experiment'

        elif pdbid in data.pdbids_em:
            comment = 'Electron microscopy experiment'

        elif pdbid in data.pdbids_other:
            comment = 'Not a Diffraction experiment'

        elif pdbid not in data.pdbids_sf:
            comment = 'Not deposited'

        if comment is not None:
            comment_entry(entry, comment)


    _log.info('Generate comments for missing nmr data')
    for entry in get_entries_of_interest('NMR', check_pdbids):

        pdbid = entry['pdbid']
        if check_pdbids is not None and pdbid not in check_pdbids:
            continue

        comment = None
        if pdbid in data.pdbids_diffraction:
            comment = 'Diffraction experiment'

        elif pdbid in data.pdbids_em:
            comment = 'Electron microscopy experiment'

        elif pdbid in data.pdbids_other:
            comment = 'Not an NMR experiment'

        elif pdbid not in data.pdbids_nr:
            comment = 'Not deposited'

        if comment is not None:
            comment_entry(entry, comment)


    _log.info('Generate comments for missing hssp files')
    # To find out why HSSP entries are missing, one must check the error output of
    # mkhssp when it ran. It's been stored in a reserved directory:
    for entry in get_entries_of_interest('HSSP', check_pdbids):

        pdbid = entry['pdbid']
        if check_pdbids is not None and pdbid not in check_pdbids:
            continue

        inputfile = '/srv/data/pdb/all/pdb%s.ent.gz' % pdbid
        if not os.path.isfile(inputfile):
            inputfile = '/srv/data/mmCIF/%s.cif.gz' % pdbid

        # Get hssp error from log file.
        # If the log is missing, run mkhssp.
        errfile = os.path.join(settings.WHYNOT_HSSP_DIRECTORY, '%s.err' % pdbid)
        if os.path.isfile(errfile):
            with open(errfile, 'r') as f:
                lines = f.readlines()
        else:
            hsspfile = tempfile.mktemp()
            cmd = [mkhssp, '-a1', '-i', inputfile, '-o', hsspfile, '-d', settings.SPROT_FASTA, '-d', settings.TREMBL_FASTA]
            p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
            try:
                stdout, stderr = p.communicate(timeout=5)
            except subprocess.TimeoutExpired:
                _log.error("timeout on {}".format(cmd))
                continue

            if os.path.isfile(hsspfile):
                os.remove(hsspfile)

            lines = stderr.decode('ascii').split('\n')

        # We filter for a set of commonly ocurring errors:
        for line in lines:
            line = line.strip()
            _log.debug("{}: '{}'".format(cmd, line))

            if line in ['Not enough sequences in PDB file of length 25', 'multiple occurrences', 'No hits found', 'empty protein, or no valid complete residues']:
                comment_entry(entry, line)


    _log.info('Generate comments for missing dssp files')
    # DSSP files can be missing for multiple reasons:
    # 1 the structure has no protein, carbohydrates/nucleic acids only
    # 2 the structure hase no backbone, only alpha carbon atoms
    #
    # 1 can be found, using the predefined sets pdbidsnuconly and pdbidscarbonly.
    # 2 can be found by running dsspcmbi and catching its error output.
    for dbname in ['DSSP', 'DSSP_REDO']:
        for entry in get_entries_of_interest(dbname, check_pdbids):

            pdbid = entry['pdbid']
            if check_pdbids is not None and pdbid not in check_pdbids:
                continue

            comment = None
            if pdbid in data.pdbids_nuc:
                _log.debug("{} is nucleic acid only".format(pdbid))

                comment = 'Nucleic acids only'

            elif pdbid in data.pdbids_carb:
                _log.debug("{} is carbohydrates only".format(pdbid))

                comment = 'Carbohydrates only'
            else:
                # DSSP uses pdb files as input, DSSP_REDO uses pdb_redo files:
                if dbname == 'DSSP':
                    inputfile = '/srv/data/pdb/all/pdb%s.ent.gz' % pdbid
                    if not os.path.isfile(inputfile):
                        inputfile = '/srv/data/mmCIF/%s.cif.gz' % pdbid
                else:
                    inputfile = '/srv/data/pdb_redo/%s/%s/%s_final.pdb' % (pdbid[1:3], pdbid, pdbid)
                    if not os.path.isfile(inputfile):
                        continue

                # Run dsspcmbi and capture stderr:
                dsspfile = tempfile.mktemp()
                cmd = [mkdssp, inputfile, dsspfile]

                p = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
                stdout, stderr = p.communicate()

                if os.path.isfile(dsspfile):
                    os.remove(dsspfile)

                lines = stderr.decode('ascii').split('\n')
                for line in lines:
                    line = line.strip().lower()
                    _log.debug("{}: '{}'".format(cmd, line))

                    if line == 'empty protein, or no valid complete residues':
                        comment = 'No residues with complete backbone'  # for backwards compatibility

            if comment is not None:
                comment_entry(entry, comment)


    _log.info('Generate comments for missing pdbredo entries')
    for entry in get_entries_of_interest('PDB_REDO', check_pdbids):

        pdbid = entry['pdbid']
        if check_pdbids is not None and pdbid not in check_pdbids:
            continue

        whynotfile = '/srv/data/pdb_redo/whynot/%s.txt' % pdbid
        if not os.path.isfile(whynotfile):
            continue

        with open(whynotfile, 'r') as f:
            comment = parse_comment(f, entry)
            if len(comment) > 0:
                comment_entry(entry, comment)


    _log.info('Generate comments for missing bdb files')
    # BDB comments are simply stored in a file, generated by the bdb script.
    for entry in get_entries_of_interest('BDB', check_pdbids):

        pdbid = entry['pdbid']
        if check_pdbids is not None and pdbid not in check_pdbids:
            continue

        part = pdbid[1:3]
        whynotfile = '/srv/data/bdb/%s/%s/%s.whynot' % (part, pdbid, pdbid)
        if not os.path.isfile(whynotfile):
            continue

        with open(whynotfile, 'r') as f:
            comment = parse_comment(f, entry)
            if len(comment) > 0:
                comment_entry(entry, comment)


    _log.info('Generate comments for whatif lists')
    # WHATIF list comments are simply stored in a file, generated by the script.
    for lis in ['acc', 'cal', 'cc1', 'cc2', 'cc3', 'chi', 'dsp', 'iod', 'sbh', 'sbr', 'ss1', 'ss2', 'tau', 'wat']:
        for src in ['pdb', 'redo']:
            dbname = 'WHATIF_%s_%s' % (src.upper(), lis)

            for entry in get_entries_of_interest(dbname, check_pdbids):

                pdbid = entry['pdbid']
                if check_pdbids is not None and pdbid not in check_pdbids:
                    continue

                whynotfile = '/srv/data/wi-lists/%s/%s/%s/%s.%s.whynot' % (src, lis, pdbid, pdbid, lis)
                if not os.path.isfile(whynotfile):
                    continue

                with open(whynotfile, 'r') as f:
                    comment = parse_comment(f, entry)
                    if len(comment) > 0:
                        comment_entry(entry, comment)


    _log.info('Generate comments for scenes')
    # WHATIF scene comments are simply stored in a file, generated by the script.
    for lis in ['iod', 'ss2']:
        for src in ['pdb', 'redo']:
            dbname = '%s_SCENES_%s' % (src.upper(), lis)

            for entry in get_entries_of_interest(dbname, check_pdbids):

                pdbid = entry['pdbid']
                if check_pdbids is not None and pdbid not in check_pdbids:
                    continue

                whynotfile = '/srv/data/wi-lists/%s/scenes/%s/%s/%s.%s.whynot' % (src, lis, pdbid, pdbid, lis)
                if not os.path.isfile(whynotfile):
                    continue

                with open(whynotfile, 'r') as f:
                    comment = parse_comment(f, entry)
                    if len(comment) > 0:
                        comment_entry(entry, comment)


arg_parser = ArgumentParser(description="annotate all or some of the missing whynot entries")
arg_parser.add_argument('-c', '--comment', metavar='FILE', type=str, help="a comment file")
arg_parser.add_argument('-i', '--pdbid', metavar='ID', type=str, help="a pdbid to annotate")
args = arg_parser.parse_args()

databanks = None
annotate_all = True
annotate_pdbids = []


if args.comment is not None:

    annotate_from_file(args.comment)
    annotate_all = False
else:
    # Just check all other sources of information...
    _log.info('Check the files in the whynot comments directory')

    for filename in os.listdir(settings.COMMENTS_DIRECTORY):
        if filename.endswith('.txt'):
            file_path = os.path.join(settings.COMMENTS_DIRECTORY, filename)
            annotate_from_file(filepath)
            os.rename(filepath, filepath + ".done")

if args.pdbid is not None:
    annotate_pdbids = [args.pdbid]
    annotate_all = False


if annotate_all:
    update_comments()
else:
    update_comments(annotate_pdbids)
