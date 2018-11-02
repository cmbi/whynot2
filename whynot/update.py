import logging
from threading import Thread

from whynot.models.entry import Entry
from whynot.domain.databank import databanks
from whynot.storage import storage


_log = logging.getLogger(__name__)

def update(databank):
    parent_pdbids = []
    if databank.parent is not None:
        _log.debug("finding parent pdbids for {}".format(databank.name))

        parent_pdbids = databank.parent.find_all_present()

    _log.debug("finding present pdbids for {}".format(databank.name))
    present_pdbids = databank.find_all_present()

    _log.debug("finding annotations for {}".format(databank.name))
    annotations = databank.find_all_annotations()

    entries = []

    # Determine what is present.
    for pdbid in present_pdbids:
        if pdbid in parent_pdbids or databank.parent is None:
            entries.append(Entry(databank.name, pdbid, 'VALID'))
        else:
            entries.append(Entry(databank.name, pdbid, 'OBSOLETE'))

    # Determine what is missing:
    for pdbid in parent_pdbids:
        if pdbid not in present_pdbids:
            if pdbid in annotations:
                entries.append(Entry(databank.name, pdbid, 'ANNOTATED', annotations[pdbid]))
            else:
                entries.append(Entry(databank.name, pdbid, 'UNANNOTATED'))

    storage.replace_entries(entries)


class UpdateThread(Thread):
    def __init__(self, databank):
        self.databank = databank
        Thread.__init__(self)

    def run(self):
        update(self.databank)


def update_all():
    threads = []
    for databank in databanks:
        t = UpdateThread(databank)
        t.start()
        threads.append(t)

    for t in threads:
        t.join()
