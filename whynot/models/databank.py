import os
import re

from whynot.settings import settings
from whynot.services.wwpdb import get_content_types, get_method_types
from whynot.controllers.dssp import has_complete_backbone


class Databank:
    def __init__(self, name, reference_url, parent=None):
        self.name = name
        self.reference_url = reference_url
        self.parent = parent

    def get_entry_url(self, pdbid):
        return None

    def find_all_present(self):
        return []

    def find_all_missing(self):
        if self.parent is None:
            return []

        parent_pdbids = self.parent.find_all_present()
        present_pdbids = self.find_all_present()

        missing_pdbids = []
        for pdbid in parent_pdbids:
            if pdbid not in present_pdbids:
                missing_pdbids.append(pdbid)
        return missing_pdbids

    def find_all_annotations(self):
        return []


P_WHYNOT = re.compile(r'^.*\.whynot$')

P_MMCIF = re.compile(r'^([0-9][a-z0-9]{3})\.cif\.gz$', re.IGNORECASE)

class MmCifDatabank(Databank):
    def __init__(self):
        Databank.__init__(self, 'MMCIF', "http://www.wwpdb.org/", None)

    def get_entry_url(self, pdbid):
        part = pdbid[1:3]
        return "ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/mmCIF/%s/%s.cif.gz" % (part, pdbid)

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'mmCIF')):
            m = P_MMCIF.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids


P_PDB = re.compile(r'^pdb([0-9][a-z0-9]{3})\.ent\.gz$', re.IGNORECASE)

class PdbDatabank(Databank):
    def __init__(self, mmcif_databank):
        Databank.__init__(self, 'PDB', "http://www.wwpdb.org/", mmcif_databank)

    def get_entry_url(self, pdbid):
        part = pdbid[1:3]
        return "ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/pdb/%s/pdb%s.ent.gz" % (part, pdbid)

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'pdb')):
            m = P_PDB.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids


P_BDB = re.compile(r'^([0-9][a-z0-9]{3})\.bdb$', re.IGNORECASE)

class BdbDatabank(Databank):
    def __init__(self, pdb_databank):
        Databank.__init__(self, 'BDB', "http://www.cmbi.umcn.nl/bdb/", pdb_databank)

    def get_entry_url(self, pdbid):
        part = pdbid[1:3]
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/bdb/cr/%s/%s.bdb" % (part, pdbid)

    def find_all_present(self):
        present_pdbids = []
        for dirname in os.listdir(os.path.join(settings["DATADIR"], 'bdb')):
            for filename in os.listdir(os.path.join(settings["DATADIR"], 'bdb', dirname)):
                m = P_BDB.match(filename)
                if m:
                    present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        for dirname in os.listdir(os.path.join(settings["DATADIR"], 'bdb')):
            for filename in os.listdir(os.path.join(settings["DATADIR"], 'bdb', dirname)):
                if P_WHYNOT.match(filename):
                    annotations.extend(parse_whynot(os.path.join(settings["DATADIR"], 'bdb', dirname, filename)))
        return annotations


P_DSSP = re.compile(r'^([0-9][a-z0-9]{3})\.dssp$', re.IGNORECASE)

class DsspDatabank(Databank):
    def __init__(self, mmcif_databank):
        Databank.__init__(self, 'DSSP', "http://swift.cmbi.umcn.nl/gv/dssp/", mmcif_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/dssp/%s.dssp" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'dssp')):
            m = P_DSSP.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        content_types = get_content_types()
        annotations = []
        for pdbid in self.find_all_missing():
            if pdbid in content_types and content_types[pdbid] == "nuc":
                annotations.append((pdbid, "Nucleic acids only"))
            elif pdbid in content_types and content_types[pdbid] == "carb":
                annotations.append((pdbid, "Carbohydrates only"))
            elif not has_complete_backbone(pdbid):
                annotations.append((pdbid, "No residues with complete backbone"))
        return annnotations


P_HSSP = re.compile(r'^([0-9][a-z0-9]{3})\.hssp.bz2$', re.IGNORECASE)
P_HSSP_ERR = re.compile(r'^([0-9][a-z0-9]{3})\.err$', re.IGNORECASE)

class HsspDatabank(Databank):
    def __init__(self, dssp_databank):
        Databank.__init__(self, 'DSSP', "http://swift.cmbi.umcn.nl/gv/hssp/", dssp_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/hssp/%s.hssp.bz2" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'hssp')):
            m = P_HSSP.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'scratch/whynot2/hssp')):
            m = P_HSSP_ERR.match(filename)
            if m:
                pdbid = m.group(1)
                with open(os.path.join(settings["DATADIR"], 'scratch/whynot2/hssp', filename), 'r') as f:
                    for line in f:
                        line = line.strip()
                        if line in ['Not enough sequences in PDB file of length 25',
                                    'multiple occurrences',
                                    'No hits found',
                                    'empty protein, or no valid complete residues']:
                            annotations.append((pdbid, line))
        return annotations


P_PDBF = re.compile(r'^ID\s+\:\s+([0-9][a-z0-9]{3})\s*$', re.IGNORECASE)

class PdbFinderDatabank(Databank):
    def __init__(self, mmcif_databank):
        Databank.__init__(self, 'PDBFINDER', "http://swift.cmbi.umcn.nl/gv/pdbfinder/", mmcif_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/pdbfinder/PDBFIND.TXT.gz"

    def find_all_present(self):
        present_pdbids = []
        with open(os.path.join(settings["DATADIR"], 'pdbfinder/PDBFIND.TXT'), 'r') as f:
            for line in f:
                m = P_PDBF.match(line)
                if m:
                    present_pdbids.append(m.group(1))
        return present_pdbids


class PdbFinder2Databank(Databank):
    def __init__(self, pdbf_databank):
        Databank.__init__(self, 'PDBFINDER2', "http://swift.cmbi.umcn.nl/gv/pdbfinder/", pdbf_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/pdbfinder2/PDBFIND2.TXT.gz"

    def find_all_present(self):
        present_pdbids = []
        with open(os.path.join(settings["DATADIR"], 'pdbfinder2/PDBFIND2.TXT'), 'r') as f:
            for line in f:
                m = P_PDBF.match(line)
                if m:
                    present_pdbids.append(m.group(1))
        return present_pdbids


P_STRUCTUREFACTOR = re.compile(r'^r([0-9][a-z0-9]{3})sf\.ent\.gz$', re.IGNORECASE)

class StructureFactorsDatabank(Databank):
    def __init__(self, mmcif_databank):
        Databank.__init__(self, 'STRUCTUREFACTORS', "http://www.pdb.org/", mmcif_databank)

    def get_entry_url(self, pdbid):
        part = pdbid[1:3]
        return "ftp://ftp.wwpdb.org/pub/pdb/data/structures/divided/structure_factors/%s/r%ssf.ent.gz" % (part, pdbid)

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'structure_factors')):
            m = P_PDBF.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        method_types = get_method_types()

        for pdbid in self.find_all_missing():
            if pdbid in method_types and method_types[pdbid] == "NMR":
                annotations.append((pdbid, "NMR experiment"))
            elif pdbid in method_types and method_types[pdbid] == "EM":
                annotations.append((pdbid, "Electron microscopy experiment"))
            elif pdbid in method_types and method_types[pdbid] == "other":
                annotations.append((pdbid, "Not a Diffraction experiment"))
            else:
                annotations.append((pdbid, "Not deposited"))
        return annotations


P_NMR = re.compile(r'^([0-9][a-z0-9]{3})\.mr\.gz$', re.IGNORECASE)

class NmrDatabank(Databank):
    def __init__(self, mmcif_databank):
        Databank.__init__(self, 'NMR', "http://www.bmrb.wisc.edu/", mmcif_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.wwpdb.org/pub/pdb/data/structures/all/nmr_restraints/%s.mr.gz" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(os.path.join(settings["DATADIR"], 'nmr_restraints')):
            m = P_NMR.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        method_types = get_method_types()

        for pdbid in self.find_all_missing():
            if pdbid in method_types and method_types[pdbid] == "diffraction":
                annotations.append((pdbid, "Diffraction experiment"))
            elif pdbid in method_types and method_types[pdbid] == "EM":
                annotations.append((pdbid, "Electron microscopy experiment"))
            elif pdbid in method_types and method_types[pdbid] == "other":
                annotations.append((pdbid, "Not an NMR experiment"))
            else:
                annotations.append((pdbid, "Not deposited"))
        return annotations


class PdbReportDatabank(Databank):
    def __init__(self, pdb_databank):
        Databank.__init__(self, 'PDBREPORT', "http://swift.cmbi.umcn.nl/gv/pdbreport/", pdb_databank)

    def get_entry_url(self, pdbid):
        return "http://www.cmbi.umcn.nl/pdbreport/cgi-bin/nonotes?PDBID=%s" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for dirname in os.listdir(os.path.join(settings["DATADIR"], 'pdbreport')):

            dirpath = os.path.join(settings["DATADIR"], 'pdbreport', dirname)
            if not os.path.isdir(dirpath) or len(dirname) > 2:
                continue

            for pdbid in os.listdir(dirpath):
                m = P_PDBID.match(pdbid)
                if m:
                    if os.path.isfile(os.path.join(dirpath, pdbid, "index.html")):
                        present_pdbids.append(pdbid)
        return present_pdbids


P_PDBID = re.compile(r'^([0-9][a-z0-9]{3})$', re.IGNORECASE)

class PdbRedoDatabank(Databank):
    def __init__(self, structurefactors_databank):
        Databank.__init__(self, 'PDB_REDO', "http://www.cmbi.umcn.nl/pdb_redo/", structurefactors_databank)

    def get_entry_url(self, pdbid):
        return "http://www.cmbi.umcn.nl/pdb_redo/cgi-bin/redir2.pl?pdbCode=%s" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for dirname in os.listdir(os.path.join(settings["DATADIR"], 'pdb_redo')):

            dirpath = os.path.join(settings["DATADIR"], 'pdb_redo', dirname)
            if not os.path.isdir(dirpath) or len(dirname) > 2:
                continue

            for pdbid in os.listdir(dirpath):
                m = P_PDBID.match(pdbid)
                if m:
                    if os.path.isfile(os.path.join(dirpath, pdbid, "%s_final.pdb" % pdbid)):
                        present_pdbids.append(pdbid)
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        for pdbid in self.find_all_missing():
            path = os.path.join(settings["DATADIR"], "pdb_redo/whynot/%s.txt" % pdbid)
            if os.path.isfile(path):
                annotations.extend(parse_whynot(path))
        return annotations


class DsspRedoDatabank(Databank):
    def __init__(self, pdbredo_databank):
        Databank.__init__(self, 'DSSP_REDO', "http://swift.cmbi.umcn.nl/gv/dssp/", pdbredo_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/dssp_redo/%s.dssp" % pdbid

    def find_all_present(self):
        present_pdbids = []
        for filename in os.listdir(settings["DATADIR"], 'dssp_redo'):
            m = P_DSSP.match(filename)
            if m:
                present_pdbids.append(m.group(1))
        return present_pdbids

    def find_all_annotations(self):
        content_types = get_content_types()
        annotations = []
        for pdbid in self.find_all_missing():
            if pdbid in content_types and content_types[pdbid] == "nuc":
                annotations.append((pdbid, "Nucleic acids only"))
            elif pdbid in content_types and content_types[pdbid] == "carb":
                annotations.append((pdbid, "Carbohydrates only"))
            elif not has_complete_backbone(pdbid):
                annotations.append((pdbid, "No residues with complete backbone"))
        return annnotations


class WhatifDatabank(Databank):
    def __init__(self, parent, list_type):
        if parent.name == "PDB":
            self.input_type = 'pdb'
        elif parent.name == 'PDB_REDO':
            self.input_type = 'redo'
        else:
            raise ValueError("initialized a whatif databank with %s as parent" % parent.name)

        self.list_type = list_type

        Databank.__init__(self, "WHATIF_%s_%s" % (self.input_type.upper(), self.list_type), "http://swift.cmbi.umcn.nl/whatif/", parent)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/wi-lists/%s/%s/%s/%s.%s.bz2" % (self.input_type, self.list_type, pdbid, pdbid, self.list_type)

    def find_all_present(self):
        present_pdbids = []
        for pdbid in os.listdir(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, self.list_type)):
            if os.path.isfile(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, self.list_type, pdbid, '%s.%s.bz2' % (pdbid, self.list_type))):
                 present_pdbids.append(pdbid)
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        for pdbid in os.listdir(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, self.list_type)):
            whynot_path = os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, self.list_type, pdbid, '%s.whynot' % pdbid)
            if os.path.isfile(whynot_path):
                annotations.extend(parse_whynot(whynot_path))
        return annotations


SCENE_NAMES = {'ss2': 'sym-contacts', 'iod': 'ion-sites'}

class SceneDatabank(Databank):
    def __init__(self, whatif_databank):
        s, i, l = whatif_databank.name.split('_')
        if s != 'WHATIF':
            raise ValueError("scene was given %s as parent" % whatif_databank.name)

        self.input_type = i
        self.list_type = l

        Databank.__init__(self, '%s_SCENES_%s' % (self.input_type.upper(), self.list_type), "http://www.cmbi.umcn.nl/pdb-vis/", whatif_databank)

    def get_entry_url(self, pdbid):
        return "ftp://ftp.cmbi.umcn.nl/pub/molbio/data/wi-lists/redo/scenes/%s/%s/%s_%s.sce" % (self.list_type, pdbid, pdbid, SCENE_NAMES[self.list_type])

    def find_all_present(self):
        present_pdbids = []
        for pdbid in os.listdir(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, 'scenes', self.list_type)):
            if os.path.isfile(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, 'scenes', self.list_type, pdbid, '%s_%s.sce' % pdbid, SCENE_NAMES[self.list_type])):
                present_pdbids.append(pdbid)
        return present_pdbids

    def find_all_annotations(self):
        annotations = []
        for pdbid in os.listdir(os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, 'scenes', self.list_type)):
            whynot_path = os.path.join(settings["DATADIR"], 'wi-lists', self.input_type, 'scenes', self.list_type, pdbid, '%s.%s.whynot' % (pdbid, self.list_type))
            if os.path.isfile(whynot_path):
                annotations.extend(parse_whynot(whynot_path))
        return annotations


mmcif = MmCifDatabank()
pdb = PdbDatabank(mmcif)
dssp = DsspDatabank(mmcif)
hssp = HsspDatabank(dssp)
bdb = BdbDatabank(pdb)
pdbfinder = PdbFinderDatabank(mmcif)
pdbfinder2 = PdbFinder2Databank(pdbfinder)
structurefactors = StructureFactorsDatabank(mmcif)
nmr = NmrDatabank(mmcif)
pdb_redo = PdbRedoDatabank(structurefactors)
dssp_redo = DsspRedoDatabank(pdb_redo)
pdbreport = PdbReportDatabank(pdb)

databanks = [
    mmcif, dssp, hssp, pdb, bdb, nmr, pdbreport, pdbfinder, pdbfinder2, structurefactors, pdb_redo, dssp_redo
]

whatif_pdb_databanks = []
whatif_redo_databanks = []
scene_pdb_databanks = []
scene_redo_databanks = []
for lis in ['acc', 'cal', 'cc1', 'cc2', 'cc3', 'chi', 'dsp', 'iod', 'sbh', 'sbr', 'ss1', 'ss2', 'tau', 'wat']:
    pdb_databank = WhatifDatabank(pdb, lis)
    redo_databank = WhatifDatabank(pdb_redo, lis)

    whatif_pdb_databanks.append(pdb_databank)
    whatif_redo_databanks.append(redo_databank)

    if lis in ['iod', 'ss2']:
        scene_pdb_databanks.append(SceneDatabank(pdb_databank))
        scene_redo_databanks.append(SceneDatabank(redo_databank))

databanks.extend(whatif_pdb_databanks)
databanks.extend(whatif_redo_databanks)
databanks.extend(scene_pdb_databanks)
databanks.extend(scene_redo_databanks)
