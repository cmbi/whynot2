import logging
import os
import time

from whynot.services.wwpdb import wwpdb
from whynot.storage import storage


_log = logging.getLogger(__name__)


def annotate(databank):
    """
    Annotates the given databank using its annotator.
    """
    _log.info("Annotating %s" % databank['name'])

    annotator = databank['annotator']
    if not annotator:
        _log.debug("No annotation required for %s" % databank['name'])
        return

    _log.info("Annotating '{}' with '{}'".format(
        databank['name'], annotator.__name__))
    annotator.annotate(databank)


class Annotator:
    # TODO: The get methods should be on a Databank model object
    @classmethod
    def get_missing_entries(cls, databank):
        """
        Gets the missing entries for the given databank.

        An entry is considered missing when there exists an entry for the
        parent but no entry for the given databank.
        """
        result = storage.db.entries.aggregate([
            # Match only entries in the current databank and the parent
            # databank.
            {'$match': {
                '$or': [
                    {'databank_name': databank['name']},
                    {'databank_name': databank['parent_name']},
                ]
            }},

            # Group over all records, storing the parent database pdb ids in
            # parent_ids and the current databank pdb ids in pdb_ids.
            {'$group': {
                '_id': None,
                'parent_pdb_ids': {
                    '$addToSet': {
                        '$cond': {
                            'if': {'$eq': ['$databank_name',
                                           databank['parent_name']]},
                            'then': '$pdb_id',
                            'else': None
                        }
                    }
                },
                'pdb_ids': {
                    '$addToSet': {
                        '$cond': {
                            'if': {'$eq': ['$databank_name',
                                           databank['name']]},
                            'then': '$pdb_id',
                            'else': None
                        }
                    }
                }
            }},

            # Project the difference between those in the parent and those in
            # the current databank.
            {'$project': {
                'pdb_ids': {
                    '$setDifference': ['$parent_pdb_ids', '$pdb_ids']
                }
            }}
        ])

        missing_entries = storage.db.entries.find({
            'databank_name': databank['name'],
            'pdb_id': {'$in': result['pdb_ids']}
        })

        return missing_entries

    @classmethod
    def get_unannotated_entries(cls, databank):
        """
        Gets the unannotated entries for the given databank.

        An entry is considered unannotated when the `comment` field is empty.
        """
        unannotated_entries = storage.db.entries.find({
            'databank_name': databank['name'],
            'comment': None
        })

        return unannotated_entries

    @classmethod
    def update_entry(cls, entry):
        storage.db.entries.replace_one({
            'databank_name': entry['databank_name'],
            'pdb_id': entry['pdb_id'],
        }, entry)

    @classmethod
    def _parse_comment(cls, lines, entry):
        if len(lines) < 2:
            _log.debug("Not enough lines")
            return None

        if not lines[0].startswith('COMMENT:'):
            _log.debug("No comments found")
            return None

        comment = lines[0][8:].strip()

        for line in lines[1:]:
            line = line.replace(' ', '').strip().upper()
            db_entry = '%s,%s' % (entry['databank_name'].upper(),
                                  entry['pdb_id'].upper())

            if line == db_entry:
                return comment

        return None


class StructureFactorsAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        wwpdb_data = wwpdb.get()
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'NMR']:
                entry['comment'] = 'NMR experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'EM']:
                entry['comment'] = 'Electron microscopy experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'other']:
                entry['comment'] = 'Not a diffraction experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue


class NmrAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        wwpdb_data = wwpdb.get()
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'diffraction']:
                entry['comment'] = 'Diffraction experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'EM']:
                entry['comment'] = 'Electron microscopy experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['method'] == 'other']:
                entry['comment'] = 'Not an NMR experiment'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue


class HsspAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        for entry in cls.get_unannotated_entries(databank):
            # TODO: hardcoded paths
            err_file = '/srv/data/scratch/whynot2/hssp/%s.err' % entry['pdb_id']  # NOQA
            if not os.path.isfile(err_file):
                continue

            with open(err_file, 'r') as f:
                comment = f.read().strip()

            # We filter for a set of commonly ocurring errors:
            if comment in ['Not enough sequences in PDB file of length 25',
                           'multiple occurrences',
                           'No hits found',
                           'empty protein, or no valid complete residues']:
                entry['comment'] = comment
                entry['mtime'] = time.time()
                cls.update_entry(entry)


class DsspAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        wwpdb_data = wwpdb.get()
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']

            if pdb_id in [d['pdb_id'] for d in wwpdb_data
                          if d['c_type'] == 'nuc']:
                entry['comment'] = 'Nucleic acids only'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue
            elif pdb_id in [d['pdb_id'] for d in wwpdb_data
                            if d['c_type'] == 'carb']:
                entry['comment'] = 'Carbohydrates only'
                entry['mtime'] = time.time()
                cls.update_entry(entry)
                continue


class BdbAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']
            # TODO: hardcoded path
            whynot_file = '/srv/data/bdb/%s/%s/%s.whynot' % (pdb_id[1:3],
                                                             pdb_id, pdb_id)

            if not os.path.isfile(whynot_file):
                continue

            with open(whynot_file, 'r') as f:
                lines = f.readlines()

            comment = cls._parse_comment(lines, entry)
            if comment:
                entry['comment'] = comment
                entry['mtime'] = time.time()
                cls.update_entry(entry)


class WhatifListAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']
            _, src, lis = entry['databank_name'].split('_')
            # TODO: hardcoded path
            whynot_file = '/srv/data/wi-lists/%s/%s/%s/%s.%s.whynot' % (
                src, lis, pdb_id, pdb_id, lis)

            if not os.path.isfile(whynot_file):
                continue

            with open(whynot_file, 'r') as f:
                lines = f.readlines()

            comment = cls._parse_comment(lines, entry)
            if comment:
                entry['comment'] = comment
                entry['mtime'] = time.time()
                cls.update_entry(entry)


class WhatifSceneAnnotator(Annotator):
    @classmethod
    def annotate(cls, databank):
        for entry in cls.get_unannotated_entries(databank):
            pdb_id = entry['pdb_id']
            src, _, lis = entry['databank_name'].split('_')
            # TODO: hardcoded path
            whynot_file = '/srv/data/wi-lists/%s/scenes/%s/%s/%s.%s.whynot' % (
                src, lis, pdb_id, pdb_id, lis)
            if not os.path.isfile(whynot_file):
                continue

            with open(whynot_file, 'r') as f:
                lines = f.readlines()

            comment = cls._parse_comment(lines, entry)
            if comment:
                entry['comment'] = comment
                entry['mtime'] = time.time()
                cls.update_entry(entry)


class CommentFileAnnotator:
    def __init__(cls, comments_dir):
        cls._comments_dir = comments_dir

    def annotate(cls):
        if not os.path.exists(cls._comments_dir):
            raise ValueError("Comments folder '%s' doesn't exist" %
                             cls._comments_dir)

        if not os.path.isdir(cls._comments_dir):
            raise ValueError("'%s' is not a folder" % cls._comments_dir)

        entries = []
        for f in os.listdir(cls._comments_dir):
            if f.endswith('.txt'):
                p = os.path.join(cls._comments_dir, f)
                comments = cls._parse_file(p)
                for text, name, pdb_id in comments:
                    entries.append({
                        'databank_name': name,
                        'pdb_id': pdb_id.lower(),
                        'comment': text,
                        'mtime': os.path.getmtime(p)
                    })
                os.rename(p, p + '.done')
        return entries

    def _parse_file(cls, path):
        d = []
        comment = None
        with open(path, 'r') as f:
            for line in f:
                if line.startswith('COMMENT:'):
                    comment = line[8:].strip()
                elif ',' in line:
                    line = line.strip().replace(' ', '')
                    databank_name, pdb_id = line.split(',')
                    d.append((comment, databank_name, pdb_id))
                elif len(line.strip()) > 0:
                    raise Exception("Invalid format: '%s'" % line)
                else:
                    pass

        return d
