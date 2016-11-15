import os, re

from urllib2 import urlopen
from storage import storage
import pymongo
from sets import Set
from httplib import HTTPConnection

def databanks_by_name (databanks):

    d = {}
    for databank in databanks:
        d [databank ['name']] = databank

    return d

databanks = databanks_by_name (storage.find ('databanks', {}))

databank_regexes = {}
for name in databanks:
    databank_regexes [name] = databanks [name]['regex'].try_compile ()


# Verifies that the path contains the regex that the databank prescribes.
# For a file path, checks that the file exists
# For an url, checks that the url points to a vallid location. (hssp response 200)
def valid_path (databank_name, path):

    if not databank_regexes [databank_name].search (path):
        return False

    if path.startswith ('http://') or path.startswith ('ftp://'):

        host = path.split ('/')[2]
        location = path [path.find ('/', path.find (host)):]

        # Request just the head, it's faster
        conn = HTTPConnection (host)
        conn.request('HEAD', location)
        response = conn.getresponse()
        conn.close()

        return response.status == 200

    else:
        return os.path.exists (path)

# A data structure that remembers the N highest objects added
class top_highest (object):

    def __init__ (self, size):

        self.size = size
        self.d = {}
        self.order = []

    def add (self, rank, obj):

        if len (self.order) >= self.size and rank < self.order [0]:
            return # no place in list

        # Place the rank number in the ordered list
        i = 0
        while i < len (self.order):

            if rank < self.order [i]:
                self.order.insert (i, rank)
                break

            i += 1

        if i >= len (self.order): # larger than any of them
            self.order.append (rank)

        self.d [rank] = obj

        if len (self.order) > self.size:
            self.order = self.order [-self.size:]

    def get (self):

        l = []
        for k in self.order:
            l.append (self.d [k])

        return l

def parse_regex (mongoRegex):

    return re.compile (mongoRegex.pattern.replace ('\\\\', '\\'))

def read_http (url):

    s = ''
    stream = urlopen (url)
    while True:
        data = stream.read ()
        if len (data) <= 0:
            break
        s += data

    return s

def get_parent (databank_name, pdbid):

    databank = storage.find_one ('databanks', {'name': databank_name})
    if not databank:
        raise Exception ("no such databank: " + databank_name)

    if 'parent_name' in databank:

        parent_name = databank ['parent_name']

        parent = storage.find_one ('databanks', {'name': parent_name})
        if not parent:
            raise Exception ("no such databank: " + parent_name)

        parent_entry = storage.find_one ('entries', {'databank_name': parent_name, 'pdbid': pdbid})

        return parent_entry

    return None

def entries_by_databank (entries):

    d = {}
    for entry in entries:
        d [entry['databank_name']] = entry

    return d

def entries_by_pdbid (entries):

    d = {}
    for entry in entries:
        d [entry['pdbid']] = entry

    return d

def get_file_link (databank, pdbid):

    part = pdbid [1:3]

    return databank ['filelink'].replace ('${PDBID}', pdbid).replace ('${PART}', part)

# Searches all databanks for an entry with given pdb id.
# Returns a dictionary with a key for every databank.
# Value is either a link to a file, or a comment if the file is missing.
def search_results_for (pdbid):

    part = pdbid [1:3]

    entries = entries_by_databank (storage.find ('entries', {'pdbid': pdbid}))
    databanks = databanks_by_name (storage.find ('databanks', {}))

    results = {}
    for databank_name in databanks.keys():

        databank = databanks [databank_name]

        if databank_name in entries:

            entry = entries [databank_name]

            if 'filepath' in entry:
                results [databank_name] = get_file_link (databank, pdbid)
            elif 'comment' in entry:
                results [databank_name] = entry ['comment']
        else:
            results [databank_name] = 'Not available'

            if 'parent_name' in databank:
                parent_name = databank ['parent_name']
                if parent_name not in entries or 'comment' in entries [parent_name]:
                    results [databank_name] += ', depends on %s' % parent_name

    return results

# Each databank has a parent, except the root.
# This function returns a dictionary of diectionaries.
# Each key is a databank name, each value is a branch.
# Branch is empty if a databank has no children.
def get_databank_hierarchy (name = None):

    if name is None:

        databanks = storage.find ('databanks', {'parent_name': {'$exists': False}}, {'name': 1, '_id': 0})
    else:
        databanks = storage.find ('databanks', {'parent_name': name}, {'name': 1, '_id': 0})

    tree = {}
    for databank in databanks:
        name = databank ['name']
        branch = get_databank_hierarchy (name)

        tree [name] = branch

    return tree

def get_entries_from_collection (databank_name, collection):

    collection = collection.lower ()

    if collection == 'obsolete':
        return get_obsolete_entries (databank_name)

    elif collection == 'valid':
        return get_valid_entries (databank_name)

    elif collection == 'missing':
        return get_missing_entries (databank_name)

    elif collection == 'present':
        return get_present_entries (databank_name)

    elif collection == 'annotated':
        return get_annotated_entries (databank_name)

    elif collection == 'unannotated':
        return get_unannotated_entries (databank_name)

    else:
        return []

def get_all_entries_with_comment (comment):

    # ordering was found to make it take longer!

    return storage.find ('entries', {'comment': comment}, order=[("pdbid", pymongo.ASCENDING)])

def get_entries_with_comment (databank_name, comment):

    # ordering was found to make it take longer!

    return storage.find ('entries', {'databank_name': databank_name, 'comment': comment}, order=[("pdbid", pymongo.ASCENDING)])

def get_entries_with_pdbid (databank_name, pdbid):

    # ordering was found to make it take longer!

    return storage.find ('entries', {'databank_name': databank_name, 'pdbid': pdbid}, order=[("pdbid", pymongo.ASCENDING)])


# Obsolete entries are present entries of which the parent is NOT present.
def get_obsolete_entries (databank_name):

    databank = storage.find_one ('databanks', {'name': databank_name})
    if not databank:
        raise Exception ("no such databank: " + databank_name)

    if 'parent_name' in databank:

        obsolete = []
        parent_entries = entries_by_pdbid (get_present_entries (databank ['parent_name']))
        for entry in get_present_entries (databank_name):
            if entry ['pdbid'] not in parent_entries:
                obsolete.append (entry)
        return obsolete
    else:
        return []

# Valid entries are present entries of which the parent is ALSO present.
def get_valid_entries (databank_name):

    databank = storage.find_one('databanks',{'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:

        valid=[]
        parent_entries = entries_by_pdbid(get_present_entries(databank['parent_name']))
        for entry in get_present_entries(databank_name):
            if entry['pdbid'] in parent_entries:
                valid.append(entry)
        return valid
    else:
        return get_present_entries(databank_name)

# Iterates through all existing comments.
# Returns a list of dictionaries with each having:
# - comment text
# - number of entries that have the comment
# - mtime, the last time the comment was annotated
def comment_summary ():

    comments = {}
    for entry in storage.find ('entries', {'comment': {'$exists': True}, 'mtime': {'$exists': True}}, {'mtime':1, 'comment':1, '_id':0}):
        text = entry ['comment']

        if text not in comments:
            comments [text] = {'text':text, 'n_entries': 0, 'mtime': entry ['mtime']}

        comments [text]['n_entries'] += 1
        if comments [text]['mtime'] < entry ['mtime']:
            comments [text]['mtime'] = entry ['mtime']

    return comments.values ()

# Counts present, missing, annotated, etc. entries for a single databank.
# Returns a dictionary with six numbers.
def count_summary (databank_name):

    databank = storage.find_one('databanks',{'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    projection = {'pdbid':1, '_id':0}

    count = {}

    pdbids = Set ()
    for entry in storage.find ('entries', {'databank_name': databank_name,'filepath': {'$exists': True}}, projection):
        pdbids.add (entry ['pdbid'])

    count ['present'] = len (pdbids)

    if 'parent_name' in databank:

        parent_name = databank ['parent_name']

        parent_pdbids = Set()
        missing_pdbids = Set ()

        parent_entries = storage.find ('entries', {'databank_name': parent_name,'filepath': {'$exists': True}}, projection)
        comment_entries = storage.find('entries', {'databank_name': databank_name, 'comment': {'$exists': True}}, projection)

        for entry in parent_entries:
            parent_pdbids.add (entry ['pdbid'])
            if entry ['pdbid'] not in pdbids:
                missing_pdbids.add (entry ['pdbid'])

        count ['missing'] = len (missing_pdbids)
        count ['annotated'] = 0
        for entry in comment_entries:
            if entry ['pdbid'] in missing_pdbids:

                count ['annotated'] += 1

        # missing = annotated + unannotated
        count ['unannotated'] = count ['missing'] - count ['annotated']

        count ['obsolete'] = 0
        for pdbid in pdbids:
            if pdbid not in parent_pdbids:
                count ['obsolete'] += 1

        count ['valid'] = count ['present'] - count ['obsolete']

    else: # no parent, so nothing is missing or obsolete

        count ['missing'] = 0
        count ['valid'] = count ['present']
        count ['obsolete'] = 0
        count ['annotated'] = 0
        count ['unannotated'] = 0

    return count


# An entry is considered present if it has a file path.
def get_present_entries (databank_name, ordered=False):

    # ordering was found to make it take longer!

    return storage.find('entries', {'databank_name': databank_name,'filepath': {'$exists': True}, 'mtime': {'$exists': True}},
                        order=[("pdbid", pymongo.ASCENDING)])


# An entry is considered missing if no file was found for it, but its parent is present.
# Missing entries are not neccesarily records in the database.
# Its only the absence of the file that counts here.
def get_missing_entries (databank_name):

    databank = storage.find_one('databanks',{'name':databank_name})
    if not databank:
        raise Exception ("no such databank: " + databank_name)

    # Needs a parent to determine what's missing
    if 'parent_name' not in databank:
        return []

    entries = entries_by_pdbid (storage.find('entries', {'databank_name': databank_name}))

    missing = []
    for entry in get_present_entries(databank['parent_name']):
        pdbid = entry['pdbid']
        if pdbid in entries:
            if 'filepath' not in entries[pdbid] or 'mtime' not in entries[pdbid]:
                missing.append(entries[pdbid])
        else:
            entry = {'pdbid':pdbid, 'databank_name': databank_name}
            missing.append(entry)

    return missing


# Annotated entries are always missing and they have a comment.
def get_annotated_entries (databank_name):

    return storage.find('entries', {'databank_name': databank_name, 'comment': {'$exists': True}, 'filepath': {'$exists': False}},
                        order=[("pdbid", pymongo.ASCENDING)])

# Entries that are missing but not annotated:
def get_unannotated_entries (databank_name):

    unannotated = []
    for entry in get_missing_entries(databank_name):
        if 'comment' not in entry or 'mtime' not in entry:
            unannotated.append(entry)

    return unannotated

p_tag_enclosed = re.compile("\\<(\\w+)(\\s+.+?|\\s+\".+?\")*\\>(.*)\\<\\/\\1\\>")
p_single_tag = re.compile("\\<\\w+(\\s+\\w+\\=.+)*\\/\\>")

# Removes xml tags from text, some comments have them.
def remove_tags (s):

    while True:

        m = p_tag_enclosed.search (s)
        if m:
            s = s[:m.start()] + m.group (3) + s [m.end():]
        else:
            break

    while True:

        m = p_single_tag.search (s)
        if m:
            s = s[:m.start()] + s [m.end():]
        else:
            break

    return s

class comment_node (object):

    def __init__(self, title):

        self.title = title
        self.entries = []
        self.subtree = {}

    def list_entries (self):

        entries = self.entries
        for child in self.subtree.values ():
            entries.extend (child.list_entries())

        return entries

def build_tree (root_string, comments_entries_dict):

    tree = {}

    prefix = root_string + ':'

    for key in comments_entries_dict:

        full_text = remove_tags (key)

        if full_text.startswith (prefix):

            i = full_text.find (':', len (prefix))
            if i == -1:
                # No further subdivision possible
                root_text = full_text
            else:
                root_text = full_text [:i]

            if root_text not in tree:

                title = root_text
                if root_text == full_text:
                    title = key # with tags

                tree [root_text] = comment_node (title)
                tree [root_text].subtree = build_tree (title, comments_entries_dict)

                if len (tree [root_text].subtree) <= 0:
                    tree [root_text].entries = comments_entries_dict [key]

    return tree

def remove_unbranched_comment_nodes (tree):

    for key in tree:

        if len (tree [key].subtree) == 1:

            subtree = tree [key].subtree
            tree.pop (key)
            key = subtree.keys () [0]
            tree [key] = subtree [key]

        tree [key].subtree = remove_unbranched_comment_nodes (tree [key].subtree)

    return tree

# The comments_to_tree function builds a hierarchical data structure for the given comments.
# Here, comments are split up in nodes, based on the colon characters they contain.
# For example: the comments "Experimental method: SOLUTION NMR" and
# "Experimental method: ELECTRON MICROSCOPY" both have the same parent,
# namely "Experimental method"
def comments_to_tree (comments_entries_dict):

    tree = {}

    # keys are comments, values are entries
    for key in comments_entries_dict:

        full_text = remove_tags (key) # don't count colons in xml tags

        if ':' in full_text:

            prfx = full_text [:full_text.find (':')]
            tree [prfx] = comment_node (prfx)
            tree [prfx].subtree = build_tree (prfx, comments_entries_dict)

        else:
            tree [key] = comment_node (key)
            tree [key].entries = comments_entries_dict [key]

    return remove_unbranched_comment_nodes (tree)

