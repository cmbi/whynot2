import inspect
import logging
import os
import re
import time

from flask import Response, Blueprint, jsonify, render_template, request

from utils import (search_results_for, get_databank_hierarchy, comment_summary,
                   get_entries_from_collection, get_all_entries_with_comment,
                   get_entries_with_comment, count_summary,
                   remove_tags, get_file_link, comments_to_tree,
                   names_from_hierarchy)
from whynot.storage import storage


_log = logging.getLogger(__name__)

bp = Blueprint('frontend', __name__)


@bp.route('/')
def index():
    db_tree = get_databank_hierarchy()
    return render_template('index.html', db_tree=db_tree)


@bp.route('/search/pdb_id/<pdb_id>/')
def search(pdb_id):
    _log.info("request for pdb_id " + pdb_id)

    # On old browsers, the pdb id might end up in the url parameters:
    urlparam = request.args.get('pdb_id', '')
    if len(urlparam) == 4:
        pdb_id = urlparam

    db_tree = get_databank_hierarchy()
    results = search_results_for(pdb_id)
    db_order = names_from_hierarchy(db_tree)

    return render_template('search_results.html', db_tree=db_tree,
                           db_order=db_order, pdb_id=pdb_id, results=results)


@bp.route('/about/')
def about():
    db_tree = get_databank_hierarchy()
    return render_template('about.html', db_tree=db_tree, nav_disabled='about')


@bp.route('/comment/')
def comment():
    db_tree = get_databank_hierarchy()
    return render_template('comments.html', db_tree=db_tree,
                           nav_disabled='comments')


@bp.route('/databanks/')
@bp.route('/databanks/name/<name>/')
def databanks(name=None):
    if name is None:
        databanks = list(storage.db.databanks.find({}))
    else:
        databanks = [storage.db.databanks.find_one({'name': name})]

    db_tree = get_databank_hierarchy()
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
        entries = list(get_entries_from_collection(databank_name, collection))
        title = "%s %s" % (databank_name, collection)
    elif databank_name and comment_text:
        entries = list(get_entries_with_comment(databank_name, comment_text))
        title = comment_text
    elif comment_text:
        entries = list(get_all_entries_with_comment(comment_text))
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

    db_tree = get_databank_hierarchy()
    return render_template('entries.html', db_tree=db_tree,
                           nav_disabled='entries', collection=collection,
                           databank_name=databank_name, comment=comment_text,
                           title=title, entries=entries, files=files,
                           comment_tree=comment_tree)


@bp.route('/statistics/')
def statistics():
    db_tree = get_databank_hierarchy()
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

    _log.info("request for entries file %s %s %s %s" % (
        collection, databank_name, comment_text, listing))

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


@bp.route('/api/docs')
def docs():
    from whynot.api.routes import annotations, entries

    p = re.compile(r"\@bp\.route\s*\(\'([\w\/\<\>]*)\'\)")
    fs = [annotations, entries]
    docs = {}
    for f in fs:
        src = inspect.getsourcelines(f)
        m = p.search(src[0][0])
        if not m:  # pragma: no cover
            _log.debug("Unable to document function '{}'".format(f))
            continue

        url = m.group(1)
        docstring = inspect.getdoc(f)
        docs[url] = docstring

    db_tree = get_databank_hierarchy()
    return render_template('docs.html', docs=docs, db_tree=db_tree)


# TODO: All routes below this comment are used in AJAX calls on the page. These
#       should be integrated into the API.

@bp.route('/load_statistics/')
def load_statistics():
    _log.info("Loading statistics")

    stats = {}
    stats['total_databanks'] = storage.db.databanks.count()
    stats['total_entries'] = storage.db.entries.count()
    stats['total_files'] = storage.db.entries.count({
        'file_path': {'$ne': None}
    })
    stats['total_annotations'] = storage.db.entries.count({
        'comment': {'$ne': None}
    })
    stats['total_comments'] = len(storage.db.entries.distinct('comment'))
    stats['annotations'] = list(storage.db.entries.aggregate([
        {'$match': {'comment': {'$ne': None}}},
        {'$sort': {'mtime': -1}},
        {'$limit': 10},
        {'$project': {
            '_id': 0,
            'comment': 1,
            'mtime': 1,
            'pdb_id': 1,
            'databank_name': 1
        }}
    ]))
    stats['files'] = list(storage.db.entries.aggregate([
        {'$match': {'file_path': {'$ne': None}}},
        {'$sort': {'mtime': -1}},
        {'$limit': 10},
        {'$project': {'_id': 0, 'file_path': 1, 'mtime': 1}}
    ]))

    return jsonify(stats)


@bp.route('/count/<databank_name>/')
def count(databank_name):
    "Called by the databank page, while the loading icon is displayed"

    _log.info("request for databank %s summary" % databank_name)

    cs = count_summary(databank_name)
    cs = {l: d for l, d in list(cs.items()) if l not in ['missing', 'present']}

    return jsonify({
      'labels': list(sorted(cs.keys())),
      'data': list(sorted(cs.values()))
    })


@bp.route('/load_comments/')
def load_comments():
    _log.info("request for comment summary")

    # TODO: speed up this method

    start_time = time.time()
    comments = comment_summary()
    end_time = time.time()

    _log.info("transaction finished in %d seconds" % (end_time - start_time))

    for i in range(len(comments)):
        comments[i]['latest'] = time.strftime(
                '%d/%m/%Y %H:%M', time.gmtime(comments[i]['mtime']))

    return jsonify({'comments': comments})
