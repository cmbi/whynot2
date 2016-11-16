from mock import ANY, call, Mock, mock_open, patch
from nose.tools import assert_raises, eq_, raises, with_setup

from whynot.default_settings import DATABANKS
from whynot.annotators import *


class TestStructureFactorsAnnotator:
    @patch('whynot.annotators.wwpdb')
    def test_annotate(self, mock_wwpdb):
        mock_wwpdb.get.return_value = [
            { 'pdb_id': '1crn', 'c_type': 'prot', 'method': 'diffraction' },
            { 'pdb_id': '1crp', 'c_type': 'prot', 'method': 'NMR' },
            { 'pdb_id': '1d3e', 'c_type': 'prot', 'method': 'EM' },
            { 'pdb_id': '1dfw', 'c_type': 'prot', 'method': 'other' },
        ]

        entries = [
            { 'databank_name': 'structurefactors',
              'pdb_id': '1crn', 'comment': None },
            { 'databank_name': 'structurefactors',
              'pdb_id': '1crp', 'comment': None },
            { 'databank_name': 'structurefactors',
              'pdb_id': '1d3e', 'comment': None },
            { 'databank_name': 'structurefactors',
              'pdb_id': '1dfw', 'comment': None },
        ]

        annotator = StructureFactorsAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        annotator.annotate({ 'databank_name': 'structurefactors' })

        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'structurefactors',
                'pdb_id': '1crp',
                'comment': 'NMR experiment',
                'mtime': ANY,
            }),
            call({
                'databank_name': 'structurefactors',
                'pdb_id': '1d3e',
                'comment': 'Electron microscopy experiment',
                'mtime': ANY,
            }),
            call({
                'databank_name': 'structurefactors',
                'pdb_id': '1dfw',
                'comment': 'Not a diffraction experiment',
                'mtime': ANY,
            }),
        ]);


class TestNmrAnnotator:
    @patch('whynot.annotators.wwpdb')
    def test_annotate(self, mock_wwpdb):
        mock_wwpdb.get.return_value = [
            { 'pdb_id': '1crn', 'c_type': 'prot', 'method': 'diffraction' },
            { 'pdb_id': '1crp', 'c_type': 'prot', 'method': 'NMR' },
            { 'pdb_id': '1d3e', 'c_type': 'prot', 'method': 'EM' },
            { 'pdb_id': '1dfw', 'c_type': 'prot', 'method': 'other' },
        ]

        entries = [
            { 'databank_name': 'nmr', 'pdb_id': '1crn', 'comment': None },
            { 'databank_name': 'nmr', 'pdb_id': '1crp', 'comment': None },
            { 'databank_name': 'nmr', 'pdb_id': '1d3e', 'comment': None },
            { 'databank_name': 'nmr', 'pdb_id': '1dfw', 'comment': None },
        ]

        annotator = NmrAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        annotator.annotate({ 'databank_name': 'structurefactors' })

        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'nmr',
                'pdb_id': '1crn',
                'comment': 'Diffraction experiment',
                'mtime': ANY,
            }),
            call({
                'databank_name': 'nmr',
                'pdb_id': '1d3e',
                'comment': 'Electron microscopy experiment',
                'mtime': ANY,
            }),
            call({
                'databank_name': 'nmr',
                'pdb_id': '1dfw',
                'comment': 'Not an NMR experiment',
                'mtime': ANY,
            }),
        ]);


class TestHsspAnnotator:
    @patch('os.path.isfile', return_value=True)
    def test_annotate(self, mock_isfile):
        entries = [
            { 'databank_name': 'hssp', 'pdb_id': '1crn', 'comment': None },
        ]
        annotator = HsspAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        comment = 'Not enough sequences in PDB file of length 25'
        with patch('whynot.annotators.open', mock_open(read_data=comment)):
            annotator.annotate({ 'databank_name': 'hssp' })

        err_file_path = '/srv/data/scratch/whynot2/hssp/1crn.err'
        mock_isfile.assert_called_once_with(err_file_path)

        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'hssp',
                'pdb_id': '1crn',
                'comment': 'Not enough sequences in PDB file of length 25',
                'mtime': ANY,
            }),
        ])


class TestDsspAnnotator:
    @patch('whynot.annotators.wwpdb')
    def test_annotate(self, mock_wwpdb):
        mock_wwpdb.get.return_value = [
            { 'pdb_id': '1hua', 'c_type': 'carb', 'method': 'NMR' },
            { 'pdb_id': '1ht7', 'c_type': 'nuc', 'method': 'NMR' },
        ]

        entries = [
            { 'databank_name': 'dssp', 'pdb_id': '1hua', 'comment': None },
            { 'databank_name': 'dssp', 'pdb_id': '1ht7', 'comment': None },
        ]

        annotator = DsspAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        annotator.annotate({ 'databank_name': 'dssp' })

        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'dssp',
                'pdb_id': '1hua',
                'comment': 'Carbohydrates only',
                'mtime': ANY,
            }),
            call({
                'databank_name': 'dssp',
                'pdb_id': '1ht7',
                'comment': 'Nucleic acids only',
                'mtime': ANY,
            }),
        ]);


class TestBdbAnnotator:
    @patch('os.path.isfile', return_value=True)
    def test_annotate(self, mock_isfile):
        entries = [
            { 'databank_name': 'bdb', 'pdb_id': '1crn', 'comment': None },
        ]
        annotator = BdbAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        lines = 'COMMENT: test comment\nBDB,1crn'
        with patch('whynot.annotators.open', mock_open(read_data=lines)):
            annotator.annotate({ 'databank_name': 'hssp' })

        whynot_file_path = '/srv/data/bdb/cr/1crn/1crn.whynot'
        mock_isfile.assert_called_once_with(whynot_file_path)
        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'bdb',
                'pdb_id': '1crn',
                'comment': 'test comment',
                'mtime': ANY,
            }),
        ])


class TestWhatifListAnnotator:
    @patch('os.path.isfile', return_value=True)
    def test_annotate(self, mock_isfile):
        entries = [
            { 'databank_name': 'whatif_pdb_iod', 'pdb_id': '1crn', 'comment': None },
        ]
        annotator = WhatifListAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        lines = 'COMMENT: test comment\nwhatif_pdb_iod,1crn'
        with patch('whynot.annotators.open', mock_open(read_data=lines)):
            annotator.annotate({ 'databank_name': 'whatif_pdb_iod' })

        whynot_file_path = '/srv/data/wi-lists/pdb/iod/1crn/1crn.iod.whynot'
        mock_isfile.assert_called_once_with(whynot_file_path)
        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'whatif_pdb_iod',
                'pdb_id': '1crn',
                'comment': 'test comment',
                'mtime': ANY,
            }),
        ])


class TestWhatifSceneAnnotator:
    @patch('os.path.isfile', return_value=True)
    def test_annotate(self, mock_isfile):
        entries = [
            { 'databank_name': 'pdb_scenes_iod', 'pdb_id': '1crn', 'comment': None },
        ]
        annotator = WhatifSceneAnnotator
        annotator.get_unannotated_entries = Mock(return_value=entries)
        mock_update_entry = Mock()
        annotator.update_entry = mock_update_entry

        lines = 'COMMENT: test comment\npdb_scenes_iod,1crn'
        with patch('whynot.annotators.open', mock_open(read_data=lines)):
            annotator.annotate({ 'databank_name': 'pdb_scenes_iod' })

        whynot_file_path = '/srv/data/wi-lists/pdb/scenes/iod/1crn/1crn.iod.whynot'
        mock_isfile.assert_called_once_with(whynot_file_path)
        mock_update_entry.assert_has_calls([
            call({
                'databank_name': 'pdb_scenes_iod',
                'pdb_id': '1crn',
                'comment': 'test comment',
                'mtime': ANY,
            }),
        ])
