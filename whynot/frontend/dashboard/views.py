import logging
import os
from time import strftime, gmtime, time
from copy import deepcopy

from flask import Response, Blueprint, jsonify, render_template, request, redirect, url_for

from whynot.storage import storage
from whynot.domain.databank import get_databank_tree, get_databank, databanks as dbs
from whynot.parsers.comment import comments_to_tree
from whynot.models.status import Status, VALID, OBSOLETE, MISSING, PRESENT, ANNOTATED, UNANNOTATED

_log = logging.getLogger(__name__)

bp = Blueprint('dashboard', __name__)

date_format = '%d/%m/%Y %H:%M'


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

    start_time = time()
    results = search_results_for(pdbid)
    end_time = time()

    return render_template('search/ResultsPage.html', db_tree=db_tree, db_order=dbs, pdbid=pdbid, results=results)

@bp.route('/about/')
def about():
    return render_template('about/AboutPage.html', db_tree=db_tree, nav_disabled='about')

@bp.route('/load_comments/')
def load_comments():
    _log.info("request for comment summary")

    # TODO: speed up this method

    start_time = time()
    comments = comment_summary()
    end_time = time()

    _log.info("transaction finished in %d seconds" %(end_time - start_time))

    for i in range(len(comments)):
        comments [i]['latest'] = strftime(date_format, gmtime(comments [i]['mtime']))

    return jsonify({'comments':comments})

@bp.route('/comment/')
def comment():
    return render_template('comment/CommentPage.html', db_tree=db_tree, nav_disabled='comments')

@bp.route('/count/<databank_name>/')
def count(databank_name):
    "Called by the databank page, while the loading icon is displayed"

    # TODO: speed up this method

    _log.info("request for databank %s summary" % databank_name)

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
        entries = storage.find_entries_by_comment('.*', comment_text)
        title = comment_text

    databank = get_databank(databank_name)
    for entry in entries:
        if entry['status'] in ['VALID', 'OBSOLETE']:
            url = databank.get_entry_url(entry['pdbid'])
            f = {'name': os.path.basename(url), 'url': url}
            files.append(f)
        elif 'comment' in entry:
            if entry['comment'] not in comments:
                comments[entry['comment']] = []
            comments[entry['comment']].append('%s,%s' %(entry['databank_name'], entry['pdbid']))

    comment_tree = comments_to_tree(comments)

    return render_template('entries/EntriesPage.html', db_tree=db_tree, nav_disabled='entries',
                            status=status, databank_name=databank_name, comment=comment_text,
                            title=title, entries=entries, files=files, comment_tree=comment_tree)

@bp.route('/load_statistics/')
def load_statistics():
    _log.info("request for statistics")

    #TODO: speed up this method

    ndb = storage.count('databanks', {})

    ne = 0
    na = 0
    nf = 0
    nc = 0

    unique_comments = set()
    recent_files = top_highest(10)
    recent_annotations = top_highest(10)
    for entry in storage.find('entries', {}):

        ne += 1
        if 'mtime' in entry:
            if 'filepath' in entry:
                nf += 1
                recent_files.add(entry ['mtime'], entry)
            elif 'comment' in entry:
                na += 1
                unique_comments.add(entry ['comment'])
                recent_annotations.add(entry ['mtime'], entry)

    # Perform time-consuming operations only on the last 10 files and annotations
    files = []
    for f in recent_files.get():
        files.append({'path': f['filepath'], 'date': strftime(date_format, gmtime(f['mtime']))})

    annotations = []
    for a in recent_annotations.get():
        annotations.append({'comment': a ['comment'], 'pdbid': a ['pdbid'],
                            'databank_name': a ['databank_name'],
                            'date':  strftime(date_format, gmtime(a['mtime']))})

    nc = len(unique_comments)

    statistics = {}
    statistics ['total_databanks'] = ndb
    statistics ['total_entries'] = ne
    statistics ['total_files'] = nf
    statistics ['total_annotations'] = na
    statistics ['total_comments'] = nc
    statistics ['annotations'] = annotations
    statistics ['files'] = files

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
    for entry in get_entries_from_collection(databank_name, status):
        text += entry ['pdbid'] + '\n'

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
                c = entry ['comment']
                if c not in d:
                    d [c] = ''
                d [c] += '%s,%s\n' %(entry['databank_name'], entry['pdbid'])

        for comment in d:
            text += comment + ":\n" + d [comment]
    else:
        for entry in entries:

            if listing == 'pdbids':
                text += entry ['pdbid'] + '\n'
            elif listing == 'entries':
                text += '%s,%s\n' %(entry['databank_name'], entry ['pdbid'])
            elif listing == 'files' and 'filepath' in entry:
                text += '%s,%s,%s\n' %(entry['databank_name'], entry ['pdbid'], entry ['filepath'])

    response = Response(text, mimetype='text/plain')
    response.headers["Content-Disposition"] = "attachment; filename=%s" %('%s_%s' %(name, listing))

    return response

