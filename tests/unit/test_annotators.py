from mock import ANY, call, Mock, mock_open, patch
from nose.tools import assert_raises, eq_, raises, with_setup

from whynot.default_settings import DATABANKS
from whynot.annotators import (StructureFactorsAnnotator, HsspAnnotator,
                               NmrAnnotator)


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
