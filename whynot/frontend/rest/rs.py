import logging
import re
import inspect

from flask import Blueprint, render_template, Response

from utils import get_entries_from_collection
from whynot.storage import storage


_log = logging.getLogger(__name__)

bp = Blueprint('rest', __name__, url_prefix='/webservice/rs')


@bp.route ('/annotations/<databank_name>/<pdb_id>/')
def annotations (databank_name, pdb_id):
    """
    Request all annotations for a given entry.

    :param databank_name: Name of the whynot databank.
    :param pdb_id: pdb id of the entry.
    :return: a text string with all the comments in it.
    """

    entry = storage.db.entries.find_one({'pdb_id': pdb_id, 'databank_name': databank_name})

    comment = ''
    if entry:
        comment = entry ['comment']

    return Response (comment, mimetype='text/plain')

@bp.route ('/entries/<databank_name>/<collection>/')
def entries (databank_name, collection):
    """
    Request all entries in a given databank and collection.

    :param databank_name: Name of the whynot databank.
    :param collection: Name of the collection within the databank. Either PRESENT, VALID, OBSOLETE, MISSING, ANNOTATED or UNANNOTATED.
    :return: a text string with all pdb ids of entries that match the selection.
    """

    # TODO: SPEED THIS UP
    text = ""
    for entry in get_entries_from_collection (databank_name, collection):
        text += entry ['pdb_id'] + '\n'

    return Response (text, mimetype='text/plain')

@bp.route ('/')
def docs ():

    p = re.compile (r"\@bp\.route\s*\(\'([\w\/\<\>]*)\'\)")

    fs = [annotations, entries]
    docs = {}
    for f in fs:
        src = inspect.getsourcelines (f)
        m = p.search (src[0][0])
        if not m:  # pragma: no cover
            _log.debug("Unable to document function '{}'".format(f))
            continue

        url = m.group(1)
        docstring = inspect.getdoc(f)
        docs [url] = docstring

    return render_template('rest/docs.html', docs=docs)
