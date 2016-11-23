import time

from mock import call, Mock, patch

from whynot.default_settings import DATABANKS
from whynot.tasks import crawl


@patch('whynot.tasks.storage')
def test_crawl(mock_storage):
    entries = [
        {
            'pdb_id': '1crn', 'file_path': '/srv/data/pdb/flat/pdb1crn.ent',
            'mtime': time.time(), 'comment': None,
        },
        {
            'pdb_id': '2ltw', 'file_path': '/srv/data/pdb/flat/pdb2ltw.ent',
            'mtime': time.time(), 'comment': None,
        },
    ]
    mock_dir_crawler = Mock()
    mock_dir_crawler.crawl.return_value = entries
    mock_dir_crawler.__name__ = 'MockDirCrawler'
    databank = DATABANKS[1]
    databank['crawler'] = mock_dir_crawler

    crawl(databank)

    mock_storage.db.entries.replace_one.assert_has_calls([
        call({'databank_name': 'pdb', 'pdb_id': '1crn'}, entries[0],
             upsert=True),
        call({'databank_name': 'pdb', 'pdb_id': '2ltw'}, entries[1],
             upsert=True),
    ])
    mock_storage.db.entries.delete_many.assert_has_calls([
        call({'databank_name': 'pdb', 'pdb_id': {'$nin': ['1crn', '2ltw']}})
    ])
