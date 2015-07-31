import logging
import os
from time import strftime, gmtime
from copy import deepcopy
from sets import Set

from flask import Response, Blueprint, jsonify, render_template, request, redirect, url_for
from utils import (get_databank_hierarchy, search_results_for, get_entries_from_collection,
                   get_all_entries_with_comment, get_entries_with_comment, remove_tags,
                   get_file_link, comments_to_tree, count_summary, comment_summary, top_highest)

_log = logging.getLogger(__name__)

bp = Blueprint('dashboard', __name__)

date_format = '%d/%m/%Y %H:%M'

from storage import storage

def names_from_hierarchy (d):

    names = []
    for key in sorted (d.keys ()):
        names.append (key)
        names.extend (names_from_hierarchy (d [key]))

    return names

db_tree = get_databank_hierarchy ()
db_order = names_from_hierarchy (db_tree)

@bp.route('/')
def index ():
    return render_template ('home/HomePage.html', db_tree=db_tree)

@bp.route('/search/pdbid/<pdbid>/')
def search (pdbid):
    results = search_results_for (pdbid)

    return render_template ('search/ResultsPage.html', db_tree=db_tree, db_order=db_order, pdbid=pdbid, results=results)

@bp.route('/about/')
def about ():
    return render_template ('about/AboutPage.html', db_tree=db_tree, nav_disabled='about')

@bp.route('/load_comments/')
def load_comments ():

    # TODO: speed up this method

    comments = comment_summary ()

    for i in range (len (comments)):
        comments [i]['latest'] = strftime (date_format, gmtime (comments [i]['mtime']))

    return jsonify ({'comments':comments})

@bp.route('/comment/')
def comment ():

    return render_template ('comment/CommentPage.html', db_tree=db_tree, nav_disabled='comments')

@bp.route('/count/<databank_name>/')
def count (databank_name):
    "Called by the databank page, while the loading icon is displayed"

    # TODO: speed up this method

    return jsonify (count_summary (databank_name))

@bp.route('/databanks/')
@bp.route('/databanks/name/<name>/')
def databanks (name=None):

    if name is None:
        databanks = storage.find ('databanks', {})
    else:
        databanks = [ storage.find_one ('databanks', {'name': name}) ]

    return render_template ('databank/DatabankPage.html', db_tree=db_tree, nav_disabled='databanks', databanks=databanks)

@bp.route('/entries/')
def entries ():

    collection = request.args.get('collection')
    databank_name = request.args.get('databank')
    comment_text = request.args.get('comment')

    title = 'No entries selected'
    entries = []
    files = []
    comments = {}

    if databank_name and collection:

        entries = get_entries_from_collection (databank_name, collection)
        title = "%s %s" % (databank_name, collection)

    elif databank_name and comment_text:

        entries = get_entries_with_comment (databank_name, comment_text)
        title = comment_text

    elif comment_text:

        entries = get_all_entries_with_comment (comment_text)
        title = comment_text

    databank = storage.find_one ('databanks', {'name': databank_name})
    for entry in entries:
        if databank and 'filepath' in entry:

            f = {'name': os.path.basename (entry ['filepath']),
                 'url': get_file_link (databank, entry ['pdbid'])}
            files.append (f)

        elif 'comment' in entry:

            if entry ['comment'] not in comments:
                comments [entry ['comment']] = []
            comments [entry ['comment']].append ('%s,%s' % (entry ['databank_name'], entry ['pdbid']))

    comment_tree = comments_to_tree (comments)

    return render_template ('entries/EntriesPage.html', db_tree=db_tree, nav_disabled='entries',
                            collection=collection, databank_name=databank_name, comment=comment_text,
                            title=title, entries=entries, files=files, comment_tree=comment_tree)

@bp.route('/load_statistics/')
def load_statistics ():

    #TODO: speed up this method

    ndb = storage.count ('databanks', {})

    ne = 0
    na = 0
    nf = 0
    nc = 0

    unique_comments = Set ()
    recent_files = top_highest (10)
    recent_annotations = top_highest (10)
    for entry in storage.find ('entries', {}):

        ne += 1
        if 'mtime' in entry:
            if 'filepath' in entry:
                nf += 1
                recent_files.add (entry ['mtime'], entry)
            elif 'comment' in entry:
                na += 1
                unique_comments.add (entry ['comment'])
                recent_annotations.add (entry ['mtime'], entry)

    # Perform time-consuming operations only on the last 10 files and annotations
    files = []
    for f in recent_files.get ():
        files.append ({'path': f['filepath'], 'date': strftime (date_format, gmtime (f['mtime']))})

    annotations = []
    for a in recent_annotations.get ():
        annotations.append ({'comment': a ['comment'], 'pdbid': a ['pdbid'],
                             'databank_name': a ['databank_name'],
                             'date':  strftime (date_format, gmtime (a['mtime']))})

    nc = len (unique_comments)

    statistics = {}
    statistics ['total_databanks'] = ndb
    statistics ['total_entries'] = ne
    statistics ['total_files'] = nf
    statistics ['total_annotations'] = na
    statistics ['total_comments'] = nc
    statistics ['annotations'] = annotations
    statistics ['files'] = files

    return jsonify (statistics)

@bp.route('/statistics/')
def statistics ():

    return render_template ('statistics/StatisticsPage.html',
                            nav_disabled='statistics',
                            db_tree=db_tree)

@bp.route('/resources/list/<tolist>/')
def resources (tolist):

    if '_' not in tolist:
        return ''

    # TODO: speed up this method

    last = tolist.rfind ('_')
    databank_name = tolist [:last]
    collection = tolist [last + 1:]

    text = ''
    for entry in get_entries_from_collection (databank_name, collection):
        text += entry ['pdbid'] + '\n'

    response = Response (text, mimetype='text/plain')
    response.headers["Content-Disposition"] = "attachment; filename=%s" % tolist

    return response

@bp.route('/entries_file/')
def entries_file ():

    # TODO: speed up this method

    collection = request.args.get ('collection')
    databank_name = request.args.get ('databank')
    comment_text = request.args.get ('comment')

    # listing determines what is shown per entry (pdb ids, databank names, comments, file names, etc.)
    listing = request.args.get ('listing')

    if not listing:
        return ''

    listing = listing.lower ()

    entries = []
    name="0"
    if databank_name and collection:

        entries = get_entries_from_collection (databank_name, collection)
        name = "%s%s" % (databank_name, collection)

    elif databank_name and comment_text:

        entries = get_entries_with_comment (databank_name, comment_text)
        name = "%s%s" % (databank_name, remove_tags (comment_text))

    elif comment_text:

        entries = get_all_entries_with_comment (comment_text)
        name = remove_tags (comment_text)

    text = ''
    if listing == 'comments':

        d = {}
        for entry in entries:
            if 'comment' in entry:
                c = entry ['comment']
                if c not in d:
                    d [c] = ''
                d [c] += '%s,%s\n' % (entry['databank_name'], entry['pdbid'])

        for comment in d:
            text += comment + ":\n" + d [comment]
    else:
        for entry in entries:

            if listing == 'pdbids':
                text += entry ['pdbid'] + '\n'
            elif listing == 'entries':
                text += '%s,%s\n' % (entry['databank_name'], entry ['pdbid'])
            elif listing == 'files' and 'filepath' in entry:
                text += '%s,%s,%s\n' % (entry['databank_name'], entry ['pdbid'], entry ['filepath'])

    response = Response (text, mimetype='text/plain')
    response.headers["Content-Disposition"] = "attachment; filename=%s" % ('%s_%s' % (name, listing))

    return response

