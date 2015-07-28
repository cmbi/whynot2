import os, re

from urllib2 import urlopen
from storage import storage
from sets import Set

def parse_regex(mongoRegex):

    return re.compile(mongoRegex.pattern.replace('\\\\','\\'))

def read_http(url):

    s = ''
    stream = urlopen(url)
    while True:
        data = stream.read()
        if len(data) <= 0:
            break
        s += data

    return s

def download(url,destdir):

    destpath = os.path.join(destdir,url.split('/')[-1])
    open(destpath,'w').write(urlopen(url).read())
    return destpath

def entries_by_databank (entries):

    d = {}
    for entry in entries:
        d [entry['databank_name']] = entry

    return d

def databanks_by_name (databanks):

    d = {}
    for databank in databanks:
        d [databank ['name']] = databank

    return d

def entries_by_pdbid (entries):

    d = {}
    for entry in entries:
        d [entry['pdbid']] = entry

    return d

def update_entries(entries):

    insert=[]
    for entry in entries:
        if '_id' in entry:
            storage.update('entries',{'_id':entry['_id']},(entry))
        else:
            insert.append(entry)

    if len (insert) > 0:
        print 'now inserting %i entries' % len (insert)
        storage.insert ('entries', insert)

def get_file_link (databank, pdbid):

    part = pdbid [1:3]

    return databank ['filelink'].replace ('${PDBID}', pdbid).replace ('${PART}', part)

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

            parent_name = databank ['parent_name']
            if parent_name not in entries or 'comment' in entries [parent_name]:
                results [databank_name] += ', depends on %s' % parent_name

    return results

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

def get_entries_with_comment (databank_name, comment):

    return storage.find ('entries', {'databank_name': databank_name, 'comment': comment})

def get_entries_with_pdbid (databank_name, pdbid):

    return storage.find ('entries', {'databank_name': databank_name, 'pdbid': pdbid})

def get_obsolete_entries (databank_name):

    databank = storage.find_one('databanks', {'name': databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    if 'parent_name' in databank:

        obsolete=[]
        parent_entries = entries_by_pdbid(get_present_entries(databank['parent_name']))
        for entry in get_present_entries(databank_name):
            if entry['pdbid'] not in parent_entries:
                obsolete.append(entry)
        return obsolete
    else:
        return []

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

        count ['unannotated'] = count ['missing'] - count ['annotated']

        count ['obsolete'] = 0
        for pdbid in pdbids:
            if pdbid not in parent_pdbids:
                count ['obsolete'] += 1

        count ['valid'] = count ['present'] - count ['obsolete']

    else: # no parent

        count ['missing'] = 0
        count ['valid'] = count ['present']
        count ['obsolete'] = 0
        count ['annotated'] = 0
        count ['unannotated'] = 0

    return count

def get_present_entries(databank_name):

    return storage.find('entries', {'databank_name': databank_name,'filepath': {'$exists': True}, 'mtime': {'$exists': True}})

def get_missing_entries(databank_name):

    databank = storage.find_one('databanks',{'name':databank_name})
    if not databank:
        raise Exception("no such databank: " + databank_name)

    # Needs a parent to determine what's missing
    if 'parent_name' not in databank:
        return []

    entries = entries_by_pdbid(storage.find('entries', {'databank_name': databank_name}))

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


def get_annotated_entries (databank_name):

    return storage.find('entries', {'databank_name': databank_name, '$and': [{'comment': {'$exists': True}}, {'mtime': {'$exists': True}}],
                                    'filepath': {'$exists': False}})
def get_unannotated_entries (databank_name):

    unannotated = []
    for entry in get_missing_entries(databank_name):
        if 'comment' not in entry or 'mtime' not in entry:
            unannotated.append(entry)

    return unannotated

p_tag_enclosed = re.compile("\\<(\\w+)(\\s+.+?|\\s+\".+?\")*\\>(.*)\\<\\/\\1\\>")
p_single_tag = re.compile("\\<\\w+(\\s+\\w+\\=.+)*\\/\\>")

def remove_tags (s):

    while True:

        m = p_tag_enclosed.search (s)
        if m:
            s = s[:m.begin()] + m.group (3) + s [m.end():]
        else:
            break

    while True:

        m = p_single_tag.search (s)
        if m:
            s = s[:m.begin()] + s [m.end():]
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
                root_text = full_text
            else:
                root_text = full_text [:i]

            if root_text not in tree:

                tree [root_text] = comment_node (root_string)
                tree [root_text].subtree = build_tree (root_text, comments_entries_dict)

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

def comments_to_tree (comments_entries_dict):

    tree = {}
    for key in comments_entries_dict:

        full_text = remove_tags (key)

        if ':' in full_text:

            prfx = full_text [:full_text.find (':')]
            tree [prfx] = comment_node (prfx)
            tree [prfx].subtree = build_tree (prfx, comments_entries_dict)

        else:
            tree [full_text] = comment_node (full_text)
            tree [full_text].entries = comments_entries_dict [key]

    return remove_unbranched_comment_nodes (tree)

