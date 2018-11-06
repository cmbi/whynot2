import logging
from threading import Thread

from whynot.models.entry import Entry
from whynot.domain.databank import databanks, get_databank
from whynot.storage import storage


_log = logging.getLogger(__name__)

def update(databank):
    parent_present = []
    if databank.parent is not None:
        _log.debug("finding parent pdbids for {}".format(databank.name))

        parent_present = databank.parent.find_all_present()

    _log.debug("finding present pdbids for {}".format(databank.name))
    present = databank.find_all_present()

    _log.debug("finding annotations for {}".format(databank.name))
    annotations = databank.find_all_annotations()

    entries = []

    _log.debug("determining present for {}".format(databank.name))
    for pdbid in present:
        if pdbid in parent_present or databank.parent is None:
            entries.append(Entry(databank.name, pdbid, 'VALID', databank.get_file_mtime(pdbid)))
        else:
            entries.append(Entry(databank.name, pdbid, 'OBSOLETE', databank.get_file_mtime(pdbid)))

    _log.debug("determining missing for {}".format(databank.name))
    for pdbid in parent_present:
        if pdbid not in present:
            if pdbid in annotations:
                entries.append(Entry(databank.name, pdbid, 'ANNOTATED', databank.get_comment_mtime(pdbid), annotations[pdbid]))
            else:
                entries.append(Entry(databank.name, pdbid, 'UNANNOTATED'))

    _log.debug("replacing entries for {}".format(databank.name))
    storage.replace_entries(databank, entries)


class UpdateThread(Thread):
    def __init__(self, databank):
        self.databank = databank
        Thread.__init__(self)

    def run(self):
        update(self.databank)


def update_all(db_names=[]):
    dbs = databanks
    if len(db_names) > 0:
        dbs = [get_databank(name) for name in db_names]

    threads = []
    for databank in dbs:
        t = UpdateThread(databank)
        t.start()
        threads.append(t)

    for t in threads:
        t.join()
