import logging

from celery import current_app as celery_app

from whynot.annotators import annotate, CommentFileAnnotator
from whynot.crawlers import crawl


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
    annotator.annotate()


@celery_app.task
def update():
    _log.info("Updating databanks")

    # There are two loops because crawling must finish first. This can be
    # changed by ensuring the parent databanks are done first, but requires
    # setting up a dependency graph.

    # TODO: Add last_update field to databank entry

    for databank in celery_app.conf['DATABANKS']:
        try:
            crawl(databank)
        except Exception as e:
            _log.error("Error crawling '{}': {}".format(
                databank['name'], str(e)))

    for databank in celery_app.conf['DATABANKS']:
        try:
            annotate(databank)
        except Exception as e:
            _log.error("Error annotating '{}': {}".format(
                databank['name'], str(e)))
