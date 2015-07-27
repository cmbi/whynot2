import logging
import os
from copy import deepcopy
from sets import Set

from flask import Blueprint, jsonify, render_template, request, redirect, url_for
from utils import get_databank_hierarchy, search_results_for

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
    return render_template ('about/AboutPage.html', db_tree=db_tree)

@bp.route('/comment/')
def comment ():
    return render_template ('comment/CommentPage.html', db_tree=db_tree)

@bp.route('/databanks/')
@bp.route('/databanks/name/<name>/')
def databanks (name):
    return render_template ('databank/DatabankPage.html', db_tree=db_tree, dbname=name)

@bp.route('/entries/')
def entries ():

    source = 'No entries selected'
    entries = []
    files = []
    comments = []

    return render_template ('entries/EntriesPage.html', db_tree=db_tree, source=source, entries=entries, files=files, comments=comments)

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
                            db_tree=db_tree,
                            total_databanks=ndb,
                            total_entries=ne,
                            total_files=nf,
                            total_annotations=na,
                            total_comments=len(comments))

@bp.route('/list/')
def list ():
    return ''
