import logging
import os
import re

from celery import current_app as celery_app

from whynot.storage import storage


_log = logging.getLogger(__name__)


@celery_app.task
def annotate_from_comments():
    """
    Annotates database entries from comment files placed in
    WHYNOT_COMMENTS_DIR.

    This is run as a separate task because it is not dependant on databank
    updates.
    """
    _log.info("Annotating databanks from comments")

    annotator = CommentFileAnnotator(celery_app.config['WHYNOT_COMMENTS_DIR'])
    entries = annotator.annotate()


@celery_app.task
def update():
    _log.info("Updating databanks")

    # There are two loops because crawling must finish first. This can be
    # changed by ensuring the parent databanks are done first, but requires
    # setting up a dependency graph.

    # TODO: Add last_update field to databank entry

    for databank in celery_app.config['DATABANKS']:
        crawl(databank)

    for databank in celery_app.config['DATABANKS']:
        annotate(databank)


def annotate(databank):
    """
    Annotates the given databank using its annotator.
    """
    _log.info("Annotating %s" % databank['name'])

    annotator = databank['annotator']
    if not annotator:
        _log.debug("No annotation required for '{}'".format(databank['databank_name']))
        return

    _log.info("Annotating '{}' with '{}'".format(databank['databank_name'], annotator.__class__.__name__))
    annotator.annotate(databank)


def crawl(databank):
    """
    Crawls the given databank using its crawler.
    """
    _log.info("Crawling %s" % databank['name'])

    # Get the raw entries using the crawler
    Crawler = databank['crawler']
    _log.info("Using '{}'".format(Crawler.__name__))
    entries = Crawler.crawl(databank['source'], databank['regex'])

    # Update the entries in the database
    _log.info("Updating {} entries in database".format(len(entries)))
    for entry in entries:
        entry['databank_name'] = databank['name']
        storage.db.update_one({
            'databank_name': databank['name'],
            'pdb_id': entry['pdb_id'],
        }, entry, upsert=True)

    # Delete entries removed since the last update
    _log.info("Deleting removed entries from database")
    raw_entry_pdb_ids = [e['pdb_id'] for e in entries]
    storage.db.delete_many({
        'databank_name': databank['name'],
        'pdb_id': { '$nin': raw_entry_pdb_ids },
    })
