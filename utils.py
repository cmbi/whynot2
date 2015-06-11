import os, re

from urllib2 import urlopen
from storage import storage

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

def entries_by_pdbid(entries):

    d={}
    for entry in entries:
        d[entry['pdbid']] = entry

    return d

def update_entries(entries):

    insert=[]
    for entry in entries:
        if '_id' in entry:
            storage.update('entries',{'_id':entry['_id']},(entry))
        else:
            insert.append(entry)

    if len(insert)>0:
        print 'now inserting %i entries' % len(insert)
        storage.insert('entries',insert)

def get_entries_with_comment(databank_name, comment):

    return storage.find('entries',{'databank_name':databank_name, 'comment':comment})

def get_entries_with_pdbid(databank_name, pdbid):

    return storage.find('entries',{'databank_name':databank_name, 'pdbid':pdbid})

def get_obsolete_entries(databank_name):

    databank = storage.find_one('databanks', {'name': databank_name})
    if 'parent_name' in databank:

        obsolete=[]
        parent_entries = entries_by_pdbid(get_present_entries(databank['parent_name']))
        for entry in get_present_entries(databank_name):
            if entry['pdbid'] not in parent_entries:
                obsolete.append(entry)
        return obsolete
    else:
        return get_present_entries(databank_name)

def get_valid_entries(databank_name):

    databank = storage.find_one('databanks',{'name': databank_name})
    if 'parent_name' in databank:

        valid=[]
        parent_entries = entries_by_pdbid(get_present_entries(databank['parent_name']))
        for entry in get_present_entries(databank_name):
            if entry['pdbid'] in parent_entries:
                valid.append(entry)
        return valid
    else:
        return get_present_entries(databank_name)

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

def get_annotated_entries(databank_name):

    return storage.find('entries', {'databank_name': databank_name, '$and': [{'comment': {'$exists': True}}, {'mtime': {'$exists': True}}],
                                    'filepath': {'$exists': False}})

def get_unannotated_entries(databank_name):

    unannotated = []
    for entry in get_missing_entries(databank_name):
        if 'comment' not in entry or 'mtime' not in entry:
            unannotated.append(entry)

    return unannotated
