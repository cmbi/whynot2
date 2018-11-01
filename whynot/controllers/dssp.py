import os
import tempfile
import subprocess

from whynot.settings import settings


def has_complete_backbone(pdbid):
    inputfile = os.path.join(settings['DATADIR'], 'mmCIF', '%s.cif.gz' % pdbid)
    dsspfile = tempfile.mktemp()

    try:
        lines = subprocess.check_output('%s %s %s 2>&1 >/dev/null || true' % (settings['MKDSSP'], inputfile, dsspfile), shell=True).decode('ascii').split('\n')

        return lines[1].strip().lower() != 'empty protein, or no valid complete residues'
    finally:
        if os.path.isfile(dsspfile):
            os.remove(dsspfile)
