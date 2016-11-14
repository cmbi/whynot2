import logging
import os
import re

from celery import current_app as celery_app

from whynot.storage import storage


_log = logging.getLogger(__name__)


@celery_app.task
def update():
    _log.info("Updating databanks")
    for databank in storage.find('databanks', {}):
        crawl(databank)
        annotate(databank)
    annotate_from_comments(celery_app.config['WHYNOT_COMMENTS_DIR'])


def crawl(databank):
    """
    Crawls the given databank's source for changes.

    New entries are inserted into the database and changes are updated. Any
    entries that have been removed are also removed from the database.

    If the `crawl_type` is `FILE`, the contents of the source that match
    `regex` are parsed for entries. If the `crawl_type` is `DIR`, the filenames
    that match `regex` are parsed for entries.
    """

    _log.info("Crawling %s (%s)" % (databank['name'], databank['crawl_type']))

    if databank['crawl_type'] == 'FILE':
        entries = _crawl_file(databank['source'], databank['regex'])
    elif databank['crawl_type'] == 'DIR':
        entries = _crawl_dir(databank['source'], databank['regex'])
    else:
        raise ValueError("Unexpected crawl type: %s" % databank['crawl_type'])

    _log.debug("Found %d entries" % len(entries))

    for entry in entries:
        entry['databank_name'] = databank['name']

    # TODO: upsert the entries into the database
    # TODO: remove deleted entries


def _crawl_file(path, regex):
    """
    Crawls the file given by `path` for lines that match `regex`.

    Returns a list of entries.
    """

    _log.info("Crawling file '%s'" % path)

    if not os.path.exists(path):
        raise ValueError("Source '%s' not found" % path)

    entries = []
    r = re.compile(databank['regex'])
    with open(path, 'r') as f:
        for line in f:
            m = r.search(line)
            if not m:
                continue

            entries.append({
                'pdbid': m.group(1).lower(),
                'filepath': filepath,
                'mtime': mtime
            })
    return entries


def _crawl_dir(path, regex):
    """
    Crawls the directory given by `path` for files that match `regex`.

    Returns a list of entries.
    """

    _log.info("Crawling dir '%s'" % path)

    if not os.path.exists(path):
        raise ValueError("Source '%s' not found" % path)

    if not os.path.isdir(path):
        raise ValueError("Source '%s' must be a directory" % path)

    entries = []
    r = re.compile(databank['regex'])
    for root, dirs, files in os.walk(path):
        dirs = [d for d in dirs if d not in ['obsolete']]

        for f in files:
            if os.path.splitext(f)[1] in ['.gif', '.html']:
                continue

            p = os.path.join(root, f)
            m = r.search(p)
            if not m:
                continue

            entries.append({
                'pdbid': m.group(1).lower(),
                'filepath': f,
                'mtime': os.path.getmtime(p),
            })
    return entries


def annotate(databank):
    _log.info("Annotating %s" % databank['name'])

    annotator = databank['annotator']
    if not annotator:
        _log.info("No annotator for '%s'" % databank['name'])

    _log.info("Using annotator '%s'" % annotator.__name__

    annotator.annotate(databank)


def annotate_from_comments(comments_dir):
    annotator = CommentFileAnnotator(comments_dir)
    entries = annotator.annotate()
    # TODO: Update database entries
