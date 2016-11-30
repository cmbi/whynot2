import logging
import os
import re
from urllib.request import urlopen

import pymongo

from whynot.storage import storage


_log = logging.getLogger(__name__)

# TODO: Eventually all of this will be somewhere else


p_tag_enclosed = re.compile("\\<(\\w+)(\\s+.+?|\\s+\".+?\")*\\>(.*)\\<\\/\\1\\>")  # NOQA
p_single_tag = re.compile("\\<\\w+(\\s+\\w+\\=.+)*\\/\\>")


def databanks_by_name(databanks):
    d = {}
    for databank in databanks:
        _log.debug(databank)
        d[databank['name']] = databank
    return d


def parse_regex(mongoRegex):
    return re.compile(mongoRegex.pattern.replace('\\\\', '\\'))


def read_http(url):
    s = ''
    stream = urlopen(url)
    while True:
        data = stream.read()
        if len(data) <= 0:
            break
        s += data

    return s


def download(url, destdir):
    destpath = os.path.join(destdir, url.split('/')[-1])
    open(destpath, 'w').write(urlopen(url).read())
    return destpath


def has_annotated_parent(databank_name, pdb_id):
    parent = get_parent(databank_name, pdb_id)
    if parent:
        return 'comment' in parent
    return False


def has_present_parent(databank_name, pdb_id):
    parent = get_parent(databank_name, pdb_id)
    if parent:
        return 'filepath' in parent
    return False


def get_parent(databank_name, pdb_id):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:
        parent_name = databank['parent_name']

        parent = storage.db.databanks.find_one({'name': parent_name})
        if not parent:
            raise Exception("no such databank: " + parent_name)

        parent_entry = storage.db.entries.find_one({
            'databank_name': parent_name,
            'pdb_id': pdb_id
        })
        return parent_entry

    return None


def get_entry(databank_name, pdb_id):
    return storage.db.entries.find_one({
        'databank_name': databank_name,
        'pdb_id': pdb_id
    })


def get_parent_name(databank_name):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:
        return databank['parent_name']
    else:
        return ''


def entries_by_databank(entries):
    d = {}
    for entry in entries:
        d[entry['databank_name']] = entry
    return d


def entries_by_pdb_id(entries):
    d = {}
    for entry in entries:
        d[entry['pdb_id']] = entry
    return d


def get_file_link(databank, pdb_id):
    part = pdb_id[1:3]
    return databank['filelink'].replace('${PDBID}', pdb_id).replace('${PART}', part)  # NOQA


# Searches all databanks for an entry with given pdb id.
# Returns a dictionary with a key for every databank.
# Value is either a link to a file, or a comment if the file is missing.
def search_results_for(pdb_id):
    entries = entries_by_databank(storage.db.entries.find({'pdb_id': pdb_id}))
    databanks = databanks_by_name(storage.db.databanks.find({}))

    results = {}
    for databank_name in list(databanks.keys()):
        databank = databanks[databank_name]

        if databank_name in entries:
            entry = entries[databank_name]

            if 'filepath' in entry:
                results[databank_name] = get_file_link(databank, pdb_id)
            elif 'comment' in entry:
                results[databank_name] = entry['comment']
        else:
            results[databank_name] = 'Not available'

            if 'parent_name' in databank:
                parent_name = databank['parent_name']
                if parent_name not in entries or \
                   'comment' in entries[parent_name]:
                    results[databank_name] += ', depends on %s' % parent_name

    return results


# Each databank has a parent, except the root.
# This function returns a dictionary of diectionaries.
# Each key is a databank name, each value is a branch.
# Branch is empty if a databank has no children.
def get_databank_hierarchy(name=None):
    if name is None:
        databanks = storage.db.databanks.find(
            {'parent_name': None},
            {'name': 1, '_id': 0})
    else:
        databanks = storage.db.databanks.find({
            'parent_name': name
        }, {'name': 1, '_id': 0})

    tree = {}
    for databank in databanks:
        name = databank['name']
        branch = get_databank_hierarchy(name)
        tree[name] = branch
    return tree


def names_from_hierarchy(d):
    names = []
    for key in sorted(d.keys()):
        names.append(key)
        names.extend(names_from_hierarchy(d[key]))
    return names


def get_entries_from_collection(databank_name, collection):
    collection = collection.lower()

    if collection == 'obsolete':
        return get_obsolete_entries(databank_name)
    elif collection == 'valid':
        return get_valid_entries(databank_name)
    elif collection == 'missing':
        return get_missing_entries(databank_name)
    elif collection == 'present':
        return get_present_entries(databank_name)
    elif collection == 'annotated':
        return get_annotated_entries(databank_name)
    elif collection == 'unannotated':
        return get_unannotated_entries(databank_name)
    else:
        return []


def get_all_entries_with_comment(comment):
    # ordering was found to make it take longer!
    return storage.db.entries.find({
        'comment': comment
    }, sort=[("pdb_id", pymongo.ASCENDING)])


def get_entries_with_comment(databank_name, comment):
    # ordering was found to make it take longer!
    return storage.db.entries.find({
        'databank_name': databank_name,
        'comment': comment
    }, sort=[("pdb_id", pymongo.ASCENDING)])


def get_entries_with_pdb_id(databank_name, pdb_id):
    # ordering was found to make it take longer!
    return storage.db.entries.find({
        'databank_name': databank_name,
        'pdb_id': pdb_id
    }, sort=[("pdb_id", pymongo.ASCENDING)])


# Obsolete entries are present entries of which the parent is NOT present.
def get_obsolete_entries(databank_name):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:
        obsolete = []
        parent_entries = entries_by_pdb_id(get_present_entries(databank['parent_name']))  # NOQA
        for entry in get_present_entries(databank_name):
            if entry['pdb_id'] not in parent_entries:
                obsolete.append(entry)
        return obsolete
    else:
        return []


# Valid entries are present entries of which the parent is ALSO present.
def get_valid_entries(databank_name):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:
        valid = []
        parent_entries = entries_by_pdb_id(get_present_entries(databank['parent_name']))  # NOQA
        for entry in get_present_entries(databank_name):
            if entry['pdb_id'] in parent_entries:
                valid.append(entry)
        return valid
    else:
        return get_present_entries(databank_name)


# Iterates through all existing comments.
# Returns a list of dictionaries with each having:
# - comment text
# - number of entries that have the comment
# - mtime, the last time the comment was annotated
def comment_summary():
    comments = {}
    query = {
        'comment': None,
        'mtime': None
    }

    for entry in storage.db.entries.find(query, {'mtime': 1, 'comment': 1}):
        text = entry['comment']

        if text not in comments:
            comments[text] = {
                'text': text,
                'n_entries': 0,
                'mtime': entry['mtime']
            }

        comments[text]['n_entries'] += 1
        if comments[text]['mtime'] < entry['mtime']:
            comments[text]['mtime'] = entry['mtime']
    return list(comments.values())


# Counts present, missing, annotated, etc. entries for a single databank.
# Returns a dictionary with six numbers.
def count_summary(databank_name):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    projection = {'pdb_id': 1, '_id': 0}

    count = {}

    pdb_ids = set()
    query = {'databank_name': databank_name, 'filepath': None}
    for entry in storage.db.entries.find(query, projection):
        pdb_ids.add(entry['pdb_id'])

    count['present'] = len(pdb_ids)

    if 'parent_name' in databank:
        parent_name = databank['parent_name']

        parent_pdb_ids = set()
        missing_pdb_ids = set()

        parent_entries = storage.db.entries.find({
            'databank_name': parent_name,
            'filepath': None
        }, projection)

        comment_entries = storage.db.entries.find({
            'databank_name': databank_name,
            'comment': None
        }, projection)

        for entry in parent_entries:
            parent_pdb_ids.add(entry['pdb_id'])
            if entry['pdb_id'] not in pdb_ids:
                missing_pdb_ids.add(entry['pdb_id'])

        count['missing'] = len(missing_pdb_ids)
        count['annotated'] = 0
        for entry in comment_entries:
            if entry['pdb_id'] in missing_pdb_ids:
                count['annotated'] += 1

        # missing = annotated + unannotated
        count['unannotated'] = count['missing'] - count['annotated']

        count['obsolete'] = 0
        for pdb_id in pdb_ids:
            if pdb_id not in parent_pdb_ids:
                count['obsolete'] += 1

        count['valid'] = count['present'] - count['obsolete']
    else:  # no parent, so nothing is missing or obsolete
        count['missing'] = 0
        count['valid'] = count['present']
        count['obsolete'] = 0
        count['annotated'] = 0
        count['unannotated'] = 0
    return count


# An entry is considered present if it has a file path.
def get_present_entries(databank_name, ordered=False):
    # ordering was found to make it take longer!
    return storage.db.entries.find({
        'databank_name': databank_name,
        'filepath': None,
        'mtime': None
    }, sort=[("pdb_id", pymongo.ASCENDING)])


# An entry is considered missing if no file was found for it, but its parent is
# present.
# Missing entries are not neccesarily records in the database.
# Its only the absence of the file that counts here.
def get_missing_entries(databank_name):
    databank = storage.db.databanks.find_one({'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    # Needs a parent to determine what's missing
    if 'parent_name' not in databank:
        return []

    entries = entries_by_pdb_id(storage.db.entries.find({
        'databank_name': databank_name
    }))

    missing = []
    for entry in get_present_entries(databank['parent_name']):
        pdb_id = entry['pdb_id']
        if pdb_id in entries:
            if 'filepath' not in entries[pdb_id] or 'mtime' not in entries[pdb_id]:  # NOQA
                missing.append(entries[pdb_id])
        else:
            entry = {'pdb_id': pdb_id, 'databank_name': databank_name}
            missing.append(entry)
    return missing


# Annotated entries are always missing and they have a comment.
def get_annotated_entries(databank_name):
    return storage.db.entries.find({
        'databank_name': databank_name,
        'comment': None,
        'filepath': None
    }, sort=[("pdb_id", pymongo.ASCENDING)])


# Entries that are missing but not annotated:
def get_unannotated_entries(databank_name):
    unannotated = []
    for entry in get_missing_entries(databank_name):
        if 'comment' not in entry or 'mtime' not in entry:
            unannotated.append(entry)
    return unannotated


# Removes xml tags from text, some comments have them.
def remove_tags(s):
    while True:
        m = p_tag_enclosed.search(s)
        if m:
            s = s[:m.start()] + m.group(3) + s[m.end():]
        else:
            break

    while True:
        m = p_single_tag.search(s)
        if m:
            s = s[:m.start()] + s[m.end():]
        else:
            break

    return s


class comment_node (object):
    def __init__(self, title):
        self.title = title
        self.entries = []
        self.subtree = {}

    def list_entries(self):
        entries = self.entries
        for child in list(self.subtree.values()):
            entries.extend(child.list_entries())
        return entries


def build_tree(root_string, comments_entries_dict):
    tree = {}

    prefix = root_string + ':'

    for key in comments_entries_dict:
        full_text = remove_tags(key)

        if full_text.startswith(prefix):
            i = full_text.find(':', len(prefix))

            if i == -1:
                # No further subdivision possible
                root_text = full_text
            else:
                root_text = full_text[:i]

            if root_text not in tree:
                title = root_text
                if root_text == full_text:
                    title = key  # with tags

                tree[root_text] = comment_node(title)
                tree[root_text].subtree = build_tree(title,
                                                     comments_entries_dict)

                if len(tree[root_text].subtree) <= 0:
                    tree[root_text].entries = comments_entries_dict[key]

    return tree


def remove_unbranched_comment_nodes(tree):
    for key in tree:
        if len(tree[key].subtree) == 1:
            subtree = tree[key].subtree
            tree.pop(key)
            key = list(subtree.keys())[0]
            tree[key] = subtree[key]
        tree[key].subtree = remove_unbranched_comment_nodes(tree[key].subtree)
    return tree


# The comments_to_tree function builds a hierarchical data structure for the
# given comments.  Here, comments are split up in nodes, based on the colon
# characters they contain.
# For example: the comments "Experimental method: SOLUTION NMR" and
# "Experimental method: ELECTRON MICROSCOPY" both have the same parent,
# namely "Experimental method"
def comments_to_tree(comments_entries_dict):
    tree = {}

    # keys are comments, values are entries
    for key in comments_entries_dict:

        full_text = remove_tags(key)  # don't count colons in xml tags

        if ':' in full_text:
            prfx = full_text[:full_text.find(':')]
            tree[prfx] = comment_node(prfx)
            tree[prfx].subtree = build_tree(prfx, comments_entries_dict)
        else:
            tree[key] = comment_node(key)
            tree[key].entries = comments_entries_dict[key]

    return remove_unbranched_comment_nodes(tree)
