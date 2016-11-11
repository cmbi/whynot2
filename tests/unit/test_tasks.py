from mock import call, MagicMock, patch
from nose.tools import assert_raises, eq_, raises, with_setup

from whynot.default_settings import DATABANKS
from whynot.tasks import update, crawl, annotate


@patch('whynot.tasks.annotate')
@patch('whynot.tasks.crawl')
@patch('whynot.tasks.storage')
def test_update(mock_storage, mock_crawl, mock_annotate):
    mock_storage.find.return_value = DATABANKS[0:1]
    update()
    mock_crawl.assert_called_with(DATABANKS[0])
    mock_annotate.assert_called_with(DATABANKS[0])


@raises(ValueError)
def test_crawl_invalid_crawl_type():
    crawl({ 'name': 'test', 'crawl_type': 'not-allowed' })


@patch('whynot.tasks._crawl_file')
def test_crawl_type_file(mock_crawl_file):
    crawl(DATABANKS[5])
    mock_crawl_file.assert_called_with(
        DATABANKS[5]['source'], DATABANKS[5]['regex'])


@patch('whynot.tasks._crawl_dir')
def test_crawl_type_dir(mock_crawl_dir):
    crawl(DATABANKS[0])
    mock_crawl_dir.assert_called_with(
        DATABANKS[0]['source'], DATABANKS[0]['regex'])


@raises(ValueError)
def test_crawl_file_invalid_path():
    crawl({ 'name': 'test', 'crawl_type': 'FILE', 'regex': r'', 'source': '' })


def test_crawl_dir_invalid_path():
    assert_raises(ValueError, crawl, { 'name': 'test', 'crawl_type': 'DIR',
                                      'regex': r'', 'source': ''})
    assert_raises(ValueError, crawl, { 'name': 'test', 'crawl_type': 'DIR',
                                      'regex': r'', 'source': '/bin/ls'})
