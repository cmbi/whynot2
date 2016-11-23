import logging
import os
from sets import Set
from time import strftime, gmtime, time

from flask import Response, Blueprint, jsonify, render_template, request

from utils import (search_results_for, get_databank_hierarchy, comment_summary,
                   get_entries_from_collection, get_all_entries_with_comment,
                   get_entries_with_comment, top_highest, count_summary,
                   remove_tags, get_file_link, comments_to_tree)
from whynot.storage import storage


_log = logging.getLogger(__name__)

bp = Blueprint('frontend', __name__)

date_format = '%d/%m/%Y %H:%M'


def names_from_hierarchy(d):
    names = []
    for key in sorted(d.keys()):
        names.append(key)
        names.extend(names_from_hierarchy(d[key]))

    return names


db_tree = get_databank_hierarchy()
db_order = names_from_hierarchy(db_tree)


@bp.route('/')
def index():
    return render_template('index.html', db_tree=db_tree)


@bp.route('/search/pdb_id/<pdb_id>/')
def search(pdb_id):
    _log.info("request for pdb_id " + pdb_id)

    # On old browsers, the pdb id might end up in the url parameters:
    urlparam = request.args.get('pdb_id', '')
    if len(urlparam) == 4:
        pdb_id = urlparam

    results = search_results_for(pdb_id)

    return render_template('search_results.html', db_tree=db_tree,
                           db_order=db_order, pdb_id=pdb_id, results=results)


@bp.route('/about/')
def about():
    return render_template('about.html', db_tree=db_tree, nav_disabled='about')


@bp.route('/load_comments/')
def load_comments():
    _log.info("request for comment summary")

    # TODO: speed up this method

    start_time = time()
    comments = comment_summary()
    end_time = time()

    _log.info("transaction finished in %d seconds" % (end_time - start_time))

    for i in range(len(comments)):
        comments[i]['latest'] = strftime(date_format,
                                         gmtime(comments[i]['mtime']))

    return jsonify({'comments': comments})


@bp.route('/comment/')
def comment():
    return render_template('comments.html', db_tree=db_tree,
                           nav_disabled='comments')


@bp.route('/count/<databank_name>/')
def count(databank_name):
    "Called by the databank page, while the loading icon is displayed"

    # TODO: speed up this method

    _log.info("request for databank %s summary" % databank_name)

    cs = count_summary(databank_name)

    return jsonify(cs)


@bp.route('/databanks/')
@bp.route('/databanks/name/<name>/')
def databanks(name=None):
    if name is None:
        databanks = storage.db.databanks.find({})
    else:
        databanks = [storage.db.databanks.find_one({'name': name})]

    return render_template('databank.html', db_tree=db_tree,
                           nav_disabled='databanks', databanks=databanks)


@bp.route('/entries/')
def entries():
    collection = request.args.get('collection')
    databank_name = request.args.get('databank')
    comment_text = request.args.get('comment')

    _log.info("request for entries %s %s %s" % (collection, databank_name,
                                                comment_text))

    title = 'No entries selected'
    entries = []
    files = []
    comments = {}

    if databank_name and collection:
        entries = get_entries_from_collection(databank_name, collection)
        title = "%s %s" % (databank_name, collection)
    elif databank_name and comment_text:
        entries = get_entries_with_comment(databank_name, comment_text)
        title = comment_text
    elif comment_text:
        entries = get_all_entries_with_comment(comment_text)
        title = comment_text

    databank = storage.db.databanks.find_one({'name': databank_name})
    for entry in entries:
        if databank and 'filepath' in entry:
            f = {'name': os.path.basename(entry['filepath']),
                 'url': get_file_link(databank, entry['pdb_id'])}
            files.append(f)
        elif 'comment' in entry:
            if entry['comment'] not in comments:
                comments[entry['comment']] = []
            comments[entry['comment']].append(
                '%s,%s' % (entry['databank_name'], entry['pdb_id']))

    comment_tree = comments_to_tree(comments)

    return render_template('entries.html', db_tree=db_tree,
                           nav_disabled='entries', collection=collection,
                           databank_name=databank_name, comment=comment_text,
                           title=title, entries=entries, files=files,
                           comment_tree=comment_tree)


@bp.route('/load_statistics/')
def load_statistics():
    _log.info("request for statistics")

    # TODO: speed up this method

    ndb = storage.db.databanks.count({})

    ne = 0
    na = 0
    nf = 0
    nc = 0

    unique_comments = Set()
    recent_files = top_highest(10)
    recent_annotations = top_highest(10)
    for entry in storage.db.entries.find({}):

        ne += 1
        if 'mtime' in entry:
            if 'filepath' in entry:
                nf += 1
                recent_files.add(entry['mtime'], entry)
            elif 'comment' in entry:
                na += 1
                unique_comments.add(entry['comment'])
                recent_annotations.add(entry['mtime'], entry)

    # Perform time-consuming operations only on the last 10 files and
    # annotations
    files = []
    for f in recent_files.get():
        files.append({
            'path': f['filepath'],
            'date': strftime(date_format, gmtime(f['mtime']))
        })

    annotations = []
    for a in recent_annotations.get():
        annotations.append({'comment': a['comment'], 'pdb_id': a['pdb_id'],
                            'databank_name': a['databank_name'],
                            'date': strftime(date_format, gmtime(a['mtime']))})

    nc = len(unique_comments)

    statistics = {}
    statistics['total_databanks'] = ndb
    statistics['total_entries'] = ne
    statistics['total_files'] = nf
    statistics['total_annotations'] = na
    statistics['total_comments'] = nc
    statistics['annotations'] = annotations
    statistics['files'] = files

    return jsonify(statistics)


@bp.route('/statistics/')
def statistics():
    return render_template('stats.html', nav_disabled='statistics',
                           db_tree=db_tree)


@bp.route('/resources/list/<tolist>/')
def resources(tolist):
    _log.info("request for resources " + tolist)

    if '_' not in tolist:
        return '', 400

    # TODO: speed up this method
    last = tolist.rfind('_')
    databank_name = tolist[:last]
    collection = tolist[last + 1:]

    text = ''
    for entry in get_entries_from_collection(databank_name, collection):
        text += entry['pdb_id'] + '\n'

    response = Response(text, mimetype='text/plain')
    header_val = "attachment; filename=%s" % tolist
    response.headers["Content-Disposition"] = header_val
    return response


@bp.route('/entries_file/')
def entries_file():
    # TODO: speed up this method
    collection = request.args.get('collection')
    databank_name = request.args.get('databank')
    comment_text = request.args.get('comment')

    # listing determines what is shown per entry(pdb ids, databank names,
    # comments, file names, etc.)
    listing = request.args.get('listing')

    _log.info("request for entries file %s %s %s %s" % (collection,
                                                        databank_name,
                                                        comment_text,
                                                        listing))

    if not listing:
        return ''

    listing = listing.lower()

    entries = []
    name = "0"
    if databank_name and collection:
        entries = get_entries_from_collection(databank_name, collection)
        name = "%s%s" % (databank_name, collection)
    elif databank_name and comment_text:
        entries = get_entries_with_comment(databank_name, comment_text)
        name = "%s%s" % (databank_name, remove_tags(comment_text))
    elif comment_text:
        entries = get_all_entries_with_comment(comment_text)
        name = remove_tags(comment_text)

    text = ''
    if listing == 'comments':
        d = {}
        for entry in entries:
            if 'comment' in entry:
                c = entry['comment']
                if c not in d:
                    d[c] = ''
                d[c] += '%s,%s\n' % (entry['databank_name'], entry['pdb_id'])
        for comment in d:
            text += comment + ":\n" + d[comment]
    else:
        for entry in entries:

            if listing == 'pdb_ids':
                text += entry['pdb_id'] + '\n'
            elif listing == 'entries':
                text += '%s,%s\n' % (entry['databank_name'], entry['pdb_id'])
            elif listing == 'files' and 'filepath' in entry:
                text += '%s,%s,%s\n' % (entry['databank_name'],
                                        entry['pdb_id'],
                                        entry['filepath'])

    response = Response(text, mimetype='text/plain')
    header_val = "attachment; filename=%s_%s" % (name, listing)
    response.headers["Content-Disposition"] = header_val

    return response
