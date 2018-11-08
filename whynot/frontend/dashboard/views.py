import logging
import os
import re
from time import strftime, gmtime, time
from copy import deepcopy

from flask import Response, Blueprint, jsonify, render_template, request, redirect, url_for

from whynot.storage import storage
from whynot.domain.databank import get_databank_tree, get_databank, databanks as dbs
from whynot.parsers.comment import comments_to_tree
from whynot.models.status import Status, VALID, OBSOLETE, MISSING, PRESENT, ANNOTATED, UNANNOTATED
from whynot.settings import settings

_log = logging.getLogger(__name__)

bp = Blueprint('dashboard', __name__)


def names_from_hierarchy(d):
    names = []
    for key in sorted(d.keys()):
        names.append(key)
        names.extend(names_from_hierarchy(d [key]))

    return names

db_tree = get_databank_tree()

@bp.route('/')
def index():
    return render_template('home/HomePage.html', db_tree=db_tree)

@bp.route('/search/pdbid/<pdbid>/')
def search(pdbid):
    _log.info("request for pdbid " + pdbid)

    # On old browsers, the pdb id might end up in the url parameters:
    urlparam = request.args.get('pdbid', '')
    if len(urlparam) == 4:
        pdbid = urlparam

    results = {}
    present = []
    for entry in storage.find_entries_by_pdbid(pdbid):
        databank = get_databank(entry.databank_name)
        if entry.status.is_present():
            results[databank.name] = databank.get_entry_url(pdbid)
            present.append(databank.name)
        elif entry.comment is not None:
            results[databank.name] = entry.comment

    for databank in dbs:
        if databank.name not in results:
            if databank.parent is not None and databank.parent.name not in present:
                results[databank.name] = "Not available, depends on %s" % databank.parent.name
            else:
                results[databank.name] = "Not available"

    return render_template('search/ResultsPage.html', db_tree=db_tree, db_order=dbs, pdbid=pdbid, results=results)

@bp.route('/about/')
def about():
    return render_template('about/AboutPage.html', db_tree=db_tree, nav_disabled='about')

@bp.route('/load_comments/')
def load_comments():
    entries = storage.find_entries_by_status(re.compile('.*'), ANNOTATED)

    comments = {}
    for entry in entries:
        text = entry.comment
        if text not in comments:
            comments[text] = {'text':text, 'n_entries': 0, 'mtime': entry.mtime}

        comments[text]['n_entries'] += 1
        if comments[text]['mtime'] < entry.mtime:
            comments[text]['mtime'] = entry.mtime

    for key in comments:
        comments[key]['latest'] = strftime(settings['DATE_FORMAT'], gmtime(comments[key]['mtime']))

    return jsonify({'comments': list(comments.values())})

@bp.route('/comment/')
def comment():
    return render_template('comment/CommentPage.html', db_tree=db_tree, nav_disabled='comments')

@bp.route('/count/<databank_name>/')
def count(databank_name):
    "Called by the databank page, while the loading icon is displayed"

    cs = {
        'valid': storage.count_entries_with_status(databank_name, VALID),
        'obsolete': storage.count_entries_with_status(databank_name, OBSOLETE),
        'annotated': storage.count_entries_with_status(databank_name, ANNOTATED),
        'unannotated': storage.count_entries_with_status(databank_name, UNANNOTATED),
    }
    cs['present'] = cs['valid'] + cs['obsolete']
    cs['missing'] = cs['annotated'] + cs['unannotated']

    return jsonify(cs)

@bp.route('/databanks/')
@bp.route('/databanks/name/<name>/')
def databanks(name=None):
    db_list = dbs
    if name is not None:
        db_list = [db for db in dbs if db.name == name]

    return render_template('databank/DatabankPage.html', db_tree=db_tree, nav_disabled='databanks', databanks=db_list)

@bp.route('/entries/')
def entries():
    status = request.args.get('collection')
    databank_name = request.args.get('databank')
    comment_text = request.args.get('comment')

    if status is not None:
        status = Status.from_string(status)

    _log.info("request for entries {} {} {}".format(status, databank_name, comment_text))

    title = 'No entries selected'
    entries = []
    files = []
    comments = {}

    if databank_name is not None and status is not None:
        entries = storage.find_entries_by_status(databank_name, status)
        title = "%s %s" % (databank_name, status)
    elif databank_name is not None and comment_text is not None:
        entries = storage.find_entries_by_comment(databank_name, comment_text)
        title = comment_text
    elif comment_text is not None:
        entries = storage.find_entries_by_comment(re.compile('.*'), comment_text)
        title = comment_text

    for entry in entries:
        databank = get_databank(entry.databank_name)

        if entry.status.is_present():
            url = databank.get_entry_url(entry.pdbid)
            f = {'name': os.path.basename(url), 'url': url}
            files.append(f)
        elif entry.comment is not None:
            if entry.comment not in comments:
                comments[entry.comment] = []
            comments[entry.comment].append('%s,%s' %(entry.databank_name, entry.pdbid))

    comment_tree = comments_to_tree(comments)

    return render_template('entries/EntriesPage.html', db_tree=db_tree, nav_disabled='entries',
                            status=status, databank_name=databank_name, comment=comment_text,
                            title=title, entries=entries, files=files, comment_tree=comment_tree)

@bp.route('/load_statistics/')
def load_statistics():

    statistics = {}
    statistics['total_databanks'] = len(dbs)
    statistics['total_entries'] = storage.count_all_entries()
    statistics['total_files'] = storage.count_entries_with_status(re.compile('.*'), PRESENT)
    statistics['total_annotations'] = storage.count_entries_with_status(re.compile('.*'), ANNOTATED)
    statistics['total_comments'] = len(storage.get_unique_comments())
    statistics['annotations'] = storage.get_recent_annotations(10)
    statistics['files'] = storage.get_recent_files(10)

    return jsonify(statistics)

@bp.route('/statistics/')
def statistics():

    return render_template('statistics/StatisticsPage.html',
                            nav_disabled='statistics',
                            db_tree=db_tree)

@bp.route('/resources/list/<tolist>/')
def resources(tolist):
    _log.info("request for resources " + tolist)

    if '_' not in tolist: # syntax error
        return '', 400

    # TODO: speed up this method
    last = tolist.rfind('_')
    databank_name = tolist [:last]
    status = tolist [last + 1:]

    text = ''
    for entry in storage.find_entries_by_status(databank_name, status):
        text += entry.pdbid + '\n'

    response = Response(text, mimetype='text/plain')
    response.headers["Content-Disposition"] = "attachment; filename=%s" % tolist

    return response

@bp.route('/entries_file/')
def entries_file():
    # TODO: speed up this method
    status = request.args.get('status')
    databank_name = request.args.get('databank')
    comment_text = request.args.get('comment')

    # listing determines what is shown per entry(pdb ids, databank names, comments, file names, etc.)
    listing = request.args.get('listing')

    _log.info("request for entries file %s %s %s %s" %(status, databank_name, comment_text, listing))

    if not listing:
        return ''

    listing = listing.lower()

    entries = []
    name="0"
    if databank_name and status:

        entries = get_entries_from_collection(databank_name, status)
        name = "%s%s" %(databank_name, status)

    elif databank_name and comment_text:

        entries = get_entries_with_comment(databank_name, comment_text)
        name = "%s%s" %(databank_name, remove_tags(comment_text))

    elif comment_text:

        entries = get_all_entries_with_comment(comment_text)
        name = remove_tags(comment_text)

    text = ''
    if listing == 'comments':

        d = {}
        for entry in entries:
            if 'comment' in entry:
                c = entry.comment
                if c not in d:
                    d [c] = ''
                d [c] += '%s,%s\n' %(entry.databank_name, entry.pdbid)

        for comment in d:
            text += comment + ":\n" + d [comment]
    else:
        for entry in entries:

            if listing == 'pdbids':
                text += entry.pdbid + '\n'
            elif listing == 'entries':
                text += '%s,%s\n' %(entry.databank_name, entry.pdbid)
            elif listing == 'files' and entry.status.is_present():
                databank = get_databank(entry.databank_name)
                text += '%s,%s,%s\n' %(entry.databank_name, entry.pdbid, os.path.basename(databank.get_entry_url(entry.pdbid)))

    response = Response(text, mimetype='text/plain')
    response.headers["Content-Disposition"] = "attachment; filename=%s" %('%s_%s' %(name, listing))

    return response

