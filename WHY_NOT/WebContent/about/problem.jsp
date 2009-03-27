<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about.problem"/></h2>

<p class="MsoNormal">The <a href="http://www.pdb.org/">Protein Data Bank</a> is the single worldwide repository for
the storage and distribution of experimentally
derived 3D structure data of large molecules like proteins and nucleic acids. Each
structure published in the PDB is assigned a unique four-character alphanumeric
identifier. The structures in the PDB are stored in flat files, which use the
above PDB ID to identify the structure contained in each file. These flat files
are distributed by the members of the World Wide Protein Data Bank.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Over the years numerous derived databases have been
developed to integrate and classify the PDB in terms of protein structure,
protein function and protein evolution. These databases are used by
bioinformaticians all around the world for research into the structures of
biological macromolecules and their relationships to sequence, function, and
disease. All these derived databases contain at most one entry for each PDB
entry. These entries are normally also stored in flat files identified by the
PDB ID of the corresponding PDB entry.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image001.gif" height="259" width="385"></p>

<p class="MsoCaption">Figure 1: A selection of PDB related and derived databases
and their correlation.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Figure 1 shows a selection of PDB related and derived
databases and their correlation. A brief description for each of these databases:</p>

<ul>
	<li><a href="http://www.pdb.org/">PDB</a><br>
	The Protein Data Bank is the single worldwide repository for the storage and
	distribution of experimentally derived 3D structure data of large molecules like
	proteins and nucleic acids.</li>

	<li><a href="http://www.uniprot.org/">UniProt</a><br>
	The Universal Protein database is a central repository of protein sequence data.
	Storing expansive and meaningful protein annotation (such as the description of
	the function of a protein, its domains structure, post-translational
	modifications, variants, etc.)</li>

	<li><a href="http://swift.cmbi.ru.nl/gv/dssp/">DSSP</a><br>
	The DSSP contains secondary structure assignments to the amino acids of a
	protein, based on the atomic-resolution coordinates of the protein.</li>

	<li><a href="http://swift.cmbi.kun.nl/gv/hssp/">HSSP</a><br>
	The HSSP database is a database of multiple sequence alignments of UniProt on
	PDB.</li>

	<li><a href="http://swift.cmbi.ru.nl/gv/pdbfinder/">PDBFinder</a><br>
	For each PDB entry the PDBFinder database contains a search-engine-friendly-formatted
	entry containing information found in the PDB, DSSP, HSSP and PDBReport databases.</li>

	<li><a href="http://swift.cmbi.ru.nl/gv/pdbreport/">PDBReport</a><br>
	A collection of reports describing structural problems in PDB entries.</li>
</ul>

<p class="MsoNormal">Both the PDB and UniProt contain experimental data, whereas the
DSSP, HSSP, PDBReport and PDBFinder databases contain computational data based
on entries in the PDB and UniProt.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">New PDB entries are added and distributed by the members of
the Worldwide Protein Data Bank on a weekly basis. Because entries for the
derived databases are created in a separate process that runs after each weekly
update, it is inevitable that the creation of derived database entries lags
behind. In such cases the PDB entry is present, but the derived database
entries can not be found yet.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Due to differing content and quality of PDB entries
something occasionally goes wrong when trying to create a derived database
entry for a given PDB entry. For instance: the DSSP algorithm is not able to determine
the secondary structure of nucleic acids. If the DSSP algorithm determines that
a PDB entry only contains nucleic acids, the computation terminates and the
DSSP entry is not created. In such cases the PDB entry is present, but the
derived database entry can not be found. Similar problems can occur for a
variety of reasons and algorithms.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Occasionally a PDB entry is removed from the PDB. When a PDB
entry is removed, the derived database entries related to this PDB entry should
also be removed. Similar to the creation of derived database entries, the
derived database entries are also removed in a separate process that runs after
each weekly update. This causes the removal of derived database entries to lag
behind. In such cases the PDB entry is no longer present, but the derived
database entries can still be found.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">In all the above three cases problems can occur when
external applications refer to a non-existing database entry. When a
non-existing file is referred, the user receives an error saying the database
entry can not be found. Without any system in place to account for missing
files, users turn to the database maintainer(s) to ask why an entry can not be
found. For each such request a database maintainer has to determine why an
entry can not be found and report this information back to the user. As there
is no form of quality control or accounting for missing database entries, these
rather simple requests take up valuable time.</p>

</s:div>

<s:include value="../page_footer.jsp" />