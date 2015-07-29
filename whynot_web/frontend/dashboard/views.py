import logging
import os
from copy import deepcopy
from sets import Set

from flask import Response, Blueprint, jsonify, render_template, request, redirect, url_for
from utils import (get_databank_hierarchy, search_results_for, get_entries_from_collection,
                   get_file_link, comments_to_tree, count_summary)

_log = logging.getLogger(__name__)

bp = Blueprint('dashboard', __name__)

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

@bp.route('/comment/')
def comment ():
    return render_template ('comment/CommentPage.html', db_tree=db_tree, nav_disabled='comments')

@bp.route('/count/<databank_name>/')
def count (databank_name):

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

    source = 'No entries selected'
    entries = []
    files = []
    comments = {}

    if collection and databank_name:

        databank = storage.find_one ('databanks', {'name':databank_name})

        if databank:

            entries = get_entries_from_collection (databank_name, collection)

            source = "%s %s" % (databank_name, collection) 

            for entry in entries:
                if 'filepath' in entry:

                    f = {'name': os.path.basename (entry ['filepath']),
                         'url': get_file_link (databank, entry ['pdbid'])}
                    files.append (f)
                elif 'comment' in entry:
                    if entry ['comment'] not in comments:
                        comments [entry ['comment']] = []
                    comments [entry ['comment']].append ('%s,%s' % (entry ['databank_name'], entry ['pdbid']))

            comments = comments_to_tree (comments)

    return render_template ('entries/EntriesPage.html', db_tree=db_tree, nav_disabled='entries',
                            collection=collection, databank_name=databank_name,
                            source=source, entries=entries, files=files, comments=comments)

@bp.route('/statistics/')
def statistics ():

    ndb = storage.count ('databanks', {})

    ne = 0
    na = 0
    nf = 0

    comments = Set ()
    for entry in storage.find ('entries', {}):
        ne += 1
        if 'mtime' in entry:
            if 'filepath' in entry:
                nf += 1
            elif 'comment' in entry:
                na += 1
                comments.add (entry['comment'])

    return render_template ('statistics/StatisticsPage.html',
                            nav_disabled='statistics',
                            db_tree=db_tree,
                            total_databanks=ndb,
                            total_entries=ne,
                            total_files=nf,
                            total_annotations=na,
                            total_comments=len(comments))

@bp.route('/resources/list/<list>/')
def resources (list):
    return Response('', mimetype='text/plain')

@bp.route('/list/')
def list ():

    collection = request.args.get('collection')
    databank_name = request.args.get('databank')
    listing = request.args.get('listing')

    if type (collection) != str or type (databank_name) != str or type (listing) != str:
        return ''

    listing = listing.lower ()

    entries = get_entries_from_collection (databank_name, collection)

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
                text += entry ['filepath'] + '\n'

    return Response(text, mimetype='text/plain')

