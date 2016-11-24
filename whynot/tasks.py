import logging

from apscheduler.schedulers.background import BackgroundScheduler

from whynot import default_settings as config
from whynot.annotators import annotate, CommentFileAnnotator
from whynot.crawlers import crawl


_log = logging.getLogger(__name__)

scheduler = BackgroundScheduler()


@scheduler.scheduled_job('cron', hour=5, minute=0)
def annotate_from_comments():
    """
    Annotates database entries from comment files placed in
    WHYNOT_COMMENTS_DIR.

    This is run as a separate task because it is not dependant on databank
    updates.
    """
    _log.info("Annotating databanks from comments")

    annotator = CommentFileAnnotator(config.WHYNOT_COMMENTS_DIR)
    annotator.annotate()


@scheduler.scheduled_job('cron', hour=0, minute=0)
def update():
    """
    Updates the database entries by crawling the filesystem for changes.

    There is nothing intelligent about this process. First the filesystem is
    crawled and all files are added to the database. This is done for all
    databanks. Finally each entry in the database is annotated.
    """
    _log.info("Updating databanks")

    # There are two loops because crawling must finish first. This can be
    # changed by ensuring the parent databanks are done first, but requires
    # setting up a dependency graph and sorting topologically.

    # TODO: Add last_update field to databank entry

    for databank in config.DATABANKS:
        try:
            crawl(databank)
        except Exception as e:
            _log.error("Error crawling '{}': {}".format(
                databank['name'], str(e)))

    for databank in config.DATABANKS:
        try:
            annotate(databank)
        except Exception as e:
            _log.error("Error annotating '{}': {}".format(
                databank['name'], str(e)))

    _log.info("Finished updating databanks")
