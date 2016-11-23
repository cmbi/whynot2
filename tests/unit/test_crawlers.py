from mock import mock_open, patch

from nose.tools import eq_

from whynot.default_settings import DATABANKS
from whynot.crawlers import DirCrawler, FileCrawler


@patch('os.path.exists', return_value=True)
@patch('os.path.isdir', return_value=True)
@patch('os.path.getmtime', return_value=123456)
def test_dir_crawler(mock_exists, mock_isdir, mock_getmtime):
    pdb_databank = DATABANKS[1]
    assert pdb_databank['name'] == 'pdb'

    with patch('os.walk') as mock_walk:
        mock_walk.return_value = [
            (
                '/srv/data/pdb/flat',
                ('obsolete',),
                ('pdb100d.ent', 'pdb104d.ent', 'pdb9rat.ent', 'pdb9rnt.ent',
                 'nothis.html')
            ),
        ]

        entries = DirCrawler.crawl(pdb_databank['source'],
                                   pdb_databank['regex'])
        mock_walk.assert_called_with(pdb_databank['source'])
        eq_(len(entries), 4)


@patch('os.path.exists', return_value=True)
@patch('os.path.getmtime', return_value=123456)
def test_file_crawler(mock_exists, mock_getmtime):
    pdbfinder_databank = DATABANKS[5]
    assert pdbfinder_databank['name'] == 'pdbfinder'

    lines = 'ID           : 100D\nID           : 101D'
    with patch('whynot.crawlers.open', mock_open(read_data=lines)):
        entries = FileCrawler.crawl(pdbfinder_databank['source'],
                                    pdbfinder_databank['regex'])
        eq_(len(entries), 2)
