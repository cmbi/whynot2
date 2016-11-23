import logging
import os
import re

from celery import current_app as celery_app
from celery.signals import setup_logging, task_failure, task_prerun

from whynot.annotators import annotate
from whynot.crawlers import crawl
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

    for databank in celery_app.conf['DATABANKS']:
        crawl(databank)

    for databank in celery_app.conf['DATABANKS']:
        annotate(databank)
