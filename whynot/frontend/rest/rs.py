import logging
import re
import inspect

_log = logging.getLogger(__name__)

from flask import Blueprint, render_template, Response
from whynot.storage import storage

bp = Blueprint('rest', __name__, url_prefix='/webservice/rs')

@bp.route ('/annotations/<databank_name>/<pdbid>/')
def annotations (databank_name, pdbid):
    """
    Request all annotations for a given entry.

    :param databank_name: Name of the whynot databank.
    :param pdbid: pdb id of the entry.
    :return: a text string with all the comments in it.
    """

    entry = storage.find_one ('entries', {'pdbid': pdbid, 'databank_name': databank_name})

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
        text += entry ['pdbid'] + '\n'

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
