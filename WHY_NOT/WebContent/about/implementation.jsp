<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about.implementation"/></h2>

<p class="MsoNormal">In the solution we outlined the steps that have to be taken
to provide the proposed functionality. The WHY_NOT system was custom-built to implement
this functionality. This chapter discusses the choices made in designing WHY_NOT,
the subsystems that make up WHY_NOT and how these subsystems combine to offer
the above described functionality.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Aside from the functional requirements outlined in the
solution the WHY_NOT system also would have to meet a number of requirements
related to presentation, extendibility and maintainability. These requirements are
the direct result of the expressed wish to alter and extend the system at a
later moment in time. As such WHY_NOT would have to be:</p>

<ul style="margin-top: 0cm;" type="disc">
 <li class="MsoNormal">Highly flexible in both the accepted input and the
     presentation of output</li>
 <li class="MsoNormal">Easily extendible to support additional databases and
     functionality</li>
 <li class="MsoNormal">Easy to maintain and extend by someone other then the original
     author</li>

</ul>

<p class="MsoNormal" style="page-break-after: avoid;">&nbsp;</p>

<p class="MsoNormal" style="page-break-after: avoid;">Figure 2 shows the various
subsystems that together make up WHY_NOT. The arrows indicate the flow of
information between the PDB-related databases, the database experts, the
end-users and WHY_NOT, as well as the flow of information among the subsystems.</p>

<p class="MsoNormal" style="page-break-after: avoid;">&nbsp;</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image002.jpg" height="415" width="576"></p>

<p class="MsoCaption">Figure 2: An illustration of the flow of information among
the various WHY_NOT subsystems.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">A brief description of all of the subsystems in the above
figure:</p>

<ul style="margin-top: 0cm;" type="disc">
 <li class="MsoNormal">Relational Database (RDB)<br>
     The RDB stores all the information gathered and used by the various
     subsystems.</li>
 <li class="MsoNormal">Data Access Object (DAO)<br>
     The DAO is the only part of the system that directly interacts with the RDB.</li>
 <li class="MsoNormal">Crawler<br>

     The Crawler indexes all the entries in the local PDB-related database
     mirror(s) and asks the DAO to store this information in the RDB.</li>
 <li class="MsoNormal">Commenter<br>
     The commenter parses the reason(s) for database inconsistencies provided
     by the database experts and asks the DAO to store this information in the
     RDB.</li>
 <li class="MsoNormal">Web Server<br>
     The web server asks the DAO for either information related to a single
     PDBID or for information about collections of PDBIDs and presents this information
     on a webpage.</li>
</ul>

<p class="MsoNormal">We'll discuss each of these subsystems in more detail
throughout the rest of this chapter.</p>

<b><i><span style="font-size: 13pt; font-family: Arial;"><br style="page-break-before: always;" clear="all">
</span></i></b>

<h2><a name="_Toc170791002">Relational database</a></h2>

<p class="MsoNormal">The design of the system started with analyzing the data the
system will store and choosing a method to store this information. The data
analysis indicated the following information will have to be stored:</p>

<ul style="margin-top: 0cm;" type="disc">
 <li class="MsoNormal">Information about every entry in the PDB and the
     PDB-related databases.<br>
     Both the PDB and PDB-related databases store all entries in flat files;
     one for each entry. The only exception to this rule is the PDBFinder
     database which stores all entries in a single file. All entries related to
     the same PDB entry share the PDB entry's PDBID for identification. For
     each entry the PDBID, the corresponding database name, path to the flat
     file and flat file's timestamp will be stored.</li>

 <li class="MsoNormal">When needed: the reason for database inconsistencies.<br>
     A database expert can specify a reason for database inconsistencies, for
     instance when a PDB-related database entry is missing. The reason will be
     stored along with details on who submitted the reason and when the reason
     was submitted. There is no ontology for these reasons.</li>
</ul>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The relational nature of the above data quickly pointed
toward using a relational database to store this information. Although the
nature of the problem would allow using a spreadsheet to store the same
information, using a relational database introduces certain benefits. These
benefits include high performance and a high level of flexibility in accessing the
data. Normalizing the manner in which the data is stored in the RDB can also
eliminate data redundancy. Using a RDB also greatly increases the ease with
which the system can be extended, especially when compared to using a
spreadsheet. An evaluation of these benefits ultimately resulted in choosing a
relational database to store the above data.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Having made the decision to use a relational database, the next
step in designing the system was designing the database. For this we used the
modeling technique Fully Communication Oriented Information Modeling. This
resulted in the database scheme described in appendix A. A simplified graphical
representation of relevant database characteristics can be seen in Figure 3.<br>

Here we'll describe some of the relevant database characteristics to explain
how the information is stored.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Both the PDB and PDB- databases use the PDBID to identify
database entries. All the entries related to a single PDB entry share the PDBID
of that entry. Therefore it made sense to utilize this PDBID to uniquely
identify entries within the PDB and the PDB-related databases.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">At this point we could have used multiple tables to store
the entries for each of the databases separately. However, due to the large
similarities in the stored data and the desire to create a flexible and
extensible system, this setup would not have been optimal, as it would require
an additional table for each database added to the WHY_NOT system. </p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Seeing how each entry within a database can be uniquely
identified using a PDBID, and that each database can be uniquely identified
using the database name, the combination of these two can uniquely describe any
database entry in the system. It is this exact combination that is used
throughout the system to identify database entries, as demonstrated in figure 3.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image003.jpg" height="586" width="415"></p>

<p class="MsoCaption">Figure 3: A simplified representation of relevant database
characteristics.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Two separate tables "<i>PDBIDs</i> and "<i>Databases</i> are
used to store the PDBIDs and the database names. The cross table "<i>Entries</i>
between these two is used to store the information about the files associated
with database entries. For identification this table uses a combination of
foreign key references to the "<i>PDBIDs</i> and "<i>Databases</i> tables.
The additional fields store the path to the file and the timestamp.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Identifying PDB-related database entries by using a
combination of PDBID and Database name rather than the flat file associated
with the entry allows us to link additional information about an entry to this
combination, even when there is no file associated with this entry.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">We already established that the reason for database
inconsistencies would also have to be stored in the RDB. We could have stored
this reason by adding a "<i>comment</i> field to the "<i>Entries</i> table
and making the "<i>file path</i> and "<i>timestamp</i> fields optional. Any
remarks about database inconsistencies could then be stored in this field. However,
this approach would have harmed the database's flexibility and extensibility,
which were two key requirements to begin with. Therefore we chose an
alternative means of storing the reason for database inconsistencies, as
explained in the remainder of this section.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">We first set out to store database inconsistencies related
to individual PDB-related database entries. For this we added two tables; one
called "Properties and another called "Entry Properties.</p>

<p class="MsoNormal">The "<i>Properties</i> table stores the inconsistency as a property
an entry either does or does not have. For instance: the database inconsistency
"entry missing is formulated as that entry not having the property "<i>Exists</i>.
This approach allows other information that can be formulated as a binary
property of an entry to be stored in a similar fashion.</p>

<p class="MsoNormal">The "<i>Entry Properties</i> table is a cross table between
the tables "<i>PDBIDs</i>, "<i>Databases</i> and "<i>Properties</i>. For
selected combinations of these three this table can store whether or not the described
entry has the property by setting the boolean in the "<i>value</i> field to
true or false.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Having first stored the database inconsistencies we then set
out to store the reason for these database inconsistencies separately. For this
we used an approach similar to the one used to store the database
inconsistencies. This required another to tables called "<i>Comments</i> and "<i>Entry
Property Comments</i>.</p>

<p class="MsoNormal">The "<i>Comments</i> table stores the reason for database
inconsistencies as flat text. As there is no formal ontology for the reason for
database inconsistencies, this approach proved sufficiently flexible. When the
same reason is given to explain multiple database inconsistencies, the same
record in the "<i>Comments</i> table is utilized. As such database experts are
advised to reuse existing comments and terminology.</p>

<p class="MsoNormal">The "<i>Entry Property Comments</i> table is a cross table
between the tables "<i>Comments</i>, "<i>PDBIDs</i>, "<i>Databases</i> and "<i>Properties</i>.
The first field references a record in the comment table. The last three fields
reference a record in the "<i>Entry Properties</i> table. This approach allows
storing multiple reasons for a single database inconsistency related to an
entry while preventing duplicate entries. </p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">We've chosen to implement the Relational Database using PostgreSQL
as our Relational Database Management Server. The choice for PostgreSQL was
based on past experiences with PostgreSQL and the fact that this RDBMS is
available free of cost while supporting all the required functionality.</p>

<b><i><span style="font-size: 13pt; font-family: Arial;"><br style="page-break-before: always;" clear="all">
</span></i></b>

<h2><a name="_Toc170791003">Data Access Object</a></h2>

<p class="MsoNormal">All subsystems in the WHY_NOT system will have to enter,
alter or access information in the RDB. To access this information a subsystem
would have be familiar with how the data is stored internally, i.e.: know the
relevant table names, fields, foreign key references, primary keys, etc.
Implementing this functionality into each of the various subsystems can create
problems when some of these details change, as all the programs using these
details will then have to be altered.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">To prevent all subsystems from having to know how the data
is stored in the relational database, we've set up a Data Access Object to
handle all the interaction with the RDB. The subsystems can interact with the
DAO via a predefined set of methods to access, enter and alter the information
stored in the database, as shown in figure 2.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The benefit of this approach is that only one application
needs to know how the data is stored internally. This means that when the manner
in which the information is stored changes, only one application will have to
be altered. Having only one application access the database also allows us to
easily add additional checks on the validity of the entered data; increasing
data integrity.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">One additional feature of the DAO is that it automatically
sets the binary property "<i>Exists</i> for all possible PDB or PDB-related
database entries. This behavior manifests itself when adding a new database,
adding a new PDBID, adding an entry or removing an existing entry.</p>

<p class="MsoNormal">When a new database is added to the RDB a new record is
added to the "<i>Entry Properties</i> table for each combination of a PDBID,
the database's name and the property "<i>Exists</i>. The "<i>value</i>
boolean for all these records has the initial value of <i>false</i>.</p>

<p class="MsoNormal">When a new PDBID is added to the RDB a new record is added
to the "<i>Entry Properties</i> table for each combination of this PDBID, any
database's name and the property "<i>Exists</i>. The "<i>value</i> boolean
for all these records has the initial value of <i>false</i>.</p>

<p class="MsoNormal">When an entry is added to the RDB the&nbsp; "<i>value</i>
boolean of the record in the "<i>Entry Properties</i> table for this entry's
PDBID, database name and the property "<i>Exists</i> is set to <i>true</i>.</p>

<p class="MsoNormal">When an entry is removed from the RDB the&nbsp; "<i>value</i>
boolean of the record in the "<i>Entry Properties</i> table for this entry's
PDBID, database name and the property "<i>Exists</i> is set to <i>false</i>.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The DAO connects to the RDB through a Java Database
Connection. The settings the DAO should use in connecting to the RDB can be
defined when invoking the DAO. Both the crawler and the commenter utilize the "<i>jdbc.xml</i>
configuration file to read these settings. An example of such a file can be
found in Appendix B.<br style="page-break-before: always;" clear="all">

<a name="_Toc170791004"><span class="Heading2Char"><span style="font-size: 13pt;">Crawler</span></span></a></p>

<p class="MsoNormal">The Crawler is the part of WHY_NOT that indexes all the
entries in the PDB and PDB-related databases. A system administrator will have
to undertake a number of steps to index all the flat files associated with the
entries in a database:</p>

<ol style="margin-top: 0cm;" start="1" type="1">
 <li class="MsoNormal">Provide a unique name to identify the database</li>
 <li class="MsoNormal">Point the Crawler towards either the database or a locally
     accessible mirror</li>
 <li class="MsoNormal">Provide a regular expression that only matches the flat
     files associated with this database</li>
 <li class="MsoNormal">Specify at which character the PDBID starts in the
     filename of the flat files</li>

 <li class="MsoNormal">Optionally: Provide a hyperlink to both the database and
     the entries in the database</li>
 <li class="MsoNormal">Run the Crawler application either specifically for this
     database or for all databases</li>
</ol>

<p class="MsoNormal">Using this input the Crawler can crawl through the directory
provided in step 2 for files matching the regular expression given in step 3.
Based on the number provided in step 4 the Crawler will extract the PDBID from
the filename and ask the DAO to store this PDBID along with the database name,
the path to the file and the file's timestamp. For diagnostic purposes the stored
entries are also written to a flat file identified by the database name.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">After the Crawler has indexed and stored all the entries in
the database it turns its attention to the DAO and requests all the entries
stored in the RDB for that database. For each of these entries the Crawler
evaluates if the associated file can still be found on the local file system. If
such a file can no longer be found on the local file system the Crawler asks
the DAO to remove the entry from the RDB. The intention of this step is to
remove entries that have been marked obsolete and have therefore been removed
from the database.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The behavior of the Crawler program is largely customizable
through both configuration files and command line parameters. Multiple
instances of the same program can be run consecutively or concurrently using
the same configuration files and RDB, for instance when multiple separate source
folders need to be crawled for each type of file. For more information see
Appendix C. The crawler could best be run after each weekly update of the PDB
and PDB-related databases. </p>

<b><i><span style="font-size: 13pt; font-family: Arial;"><br style="page-break-before: always;" clear="all">
</span></i></b>

<h2><a name="_Toc170791005">Commenter</a></h2>

<p class="MsoNormal">The Commenter is the part of WHY_NOT that allows database
experts to provide a reason for database inconsistencies. Through the web
server the database experts can see which database inconsistencies currently
exists and request to see the affected PDBIDs. External applications can then
be used to determine the reason for these database inconsistencies. </p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The current implementation of the Commenter is a command
line application that reads strictly formatted flat files. To store the
reason(s) for database inconsistencies a system administrator needs to create
such a formatted flat file and have the Commenter parse that file. Such a file
should have the following format:</p>

<p class="MsoNormal">&nbsp;</p>

<table class="MsoTableGrid" style="border: medium none ; border-collapse: collapse;" border="1" cellpadding="0" cellspacing="0">
 <tbody><tr>
  <td style="border: 1pt solid windowtext; padding: 0cm 5.4pt; width: 442.8pt;" valign="top" width="590">
  <p class="MsoNormal"><span style="font-family: &quot;Courier New&quot;;">PDBID&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :
  1XJ9</span></p>
  <p class="MsoNormal"><span style="font-family: &quot;Courier New&quot;;">Database&nbsp;&nbsp;&nbsp;&nbsp; :
  DSSP</span></p>

  <p class="MsoNormal"><span style="font-family: &quot;Courier New&quot;;">Property&nbsp;&nbsp;&nbsp;&nbsp; :
  Exists</span></p>
  <p class="MsoNormal"><span style="font-family: &quot;Courier New&quot;;">Boolean&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; :
  false   (this line is optional)</span></p>
  <p class="MsoNormal"><span style="font-family: &quot;Courier New&quot;;">Comment&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; : Not
  enough amino acids</span></p>
  <p class="MsoNormal" style="page-break-after: avoid;"><span style="font-family: &quot;Courier New&quot;;">//</span></p>

  </td>
 </tr>
</tbody></table>

<p class="MsoCaption">Table 1: Example comments file record</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Multiple records can be stored in the same file by repeating
the above pattern one after the other. The Commenter will read each individual
record and ask the DAO to store both the database inconsistency ("<i>Property</i>)
and reason ("<i>Comment</i>) in the database. The value behind "<i>Boolean</i>
in the above example (which can be either <i>true</i> or <i>false</i>) will be
used to indicate if the Entry either does or does not have the specified binary
property.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The Commenter application can be asked to either parse a
single comments file or to parse all the comments files inside a given
directory. Being able to parse all the comment files inside a given directory
enables the system administrator to set up a folder that contains all the
comment files. This directory can serve as a destination for external application
to write comment files to. Parsing the same comments file a second time does
not alter the data inside the RDB in anyway, so parsed files can remain in this
directory to serve as a form of backup. Appendix D gives some details on using
the Commenter application.</p>

<b><i><span style="font-size: 13pt; font-family: Arial;"><br style="page-break-before: always;" clear="all">
</span></i></b>

<h2><a name="_Toc170791006">Web server</a></h2>

<p class="MsoNormal">All the aforementioned subsystems serve to gather and store
information about PDB-related database entries, properties and comments. The
Web server intends to make this information available to a wide range of users.
Implemented using the Apache Struts2 framework, the Web server interacts with
the DAO to retrieve information from the RDB. The Web server has three levels
of detail to display information about PDB-related database entries, properties
and comments:</p>

<ul style="margin-top: 0cm;" type="disc">
 <li class="MsoNormal">For each database a report about the number of entries
     stored, obsolete, missing, with and without comment</li>

 <li class="MsoNormal">For each of the collections mentioned above the PDBIDs in
     this collection</li>
 <li class="MsoNormal">For each PDBID an overview of which entries exist and
     comments related to this property</li>
</ul>

<p class="MsoNormal">We'll discuss each of these pages that display this information
in more detail here, which are also accessible from <a href="http://www.cmbi.ru.nl/whynot/">http://www.cmbi.ru.nl/whynot/</a>.</p>

<h3><a name="_Toc170791007">Reports</a></h3>

<p class="MsoNormal">The focus of the WHY_NOT project and website is on providing
a meaningful explanation for why PDB-related database entries do not exist. As
such it seemed only logical to provide an overview of which PDB-related entries
exist as well as for which of the non-existing entries an explanation has been
provided. For each of the databases an evaluation is made of which entries are
present in this database, compared to the entries present in the database this
database is derived from. In almost all cases the PDB is the database the
evaluated database is derived from, with the exception of the HSSP database
which is derived from the DSSP database and UniProt. This evaluation divides
all the PDBIDs in four distinct categories:</p>

<ol style="margin-top: 0cm;" start="1" type="1">
 <li class="MsoNormal">PDBIDs present in both the database the evaluated database
     is derived from and in the evaluated database</li>
 <li class="MsoNormal">PDBIDs present in the database the evaluated database is
     derived from but missing in the evaluated database</li>
 <li class="MsoNormal">PDBIDs missing in the database the evaluated database is
     derived from but present in the evaluated database</li>
 <li class="MsoNormal">PDBIDs missing in both the database the evaluated database
     is derived from and in the evaluated database</li>
</ol>

<p class="MsoNormal">An example of these categories is illustrated in table 2 for
the database DSSP, with checks indicating correct collections and crosses
indicating database inconsistencies. </p>

<p class="MsoNormal">&nbsp;</p>

<table class="MsoTableGrid" style="border-collapse: collapse;" border="0" cellpadding="0" cellspacing="0">
 <tbody><tr style="height: 12.25pt;">
  <td style="padding: 0cm 5.4pt; width: 46.35pt; height: 12.25pt;" width="62">
  <p class="MsoNormal" style="text-align: center;" align="center">&nbsp;</p>
  </td>
  <td colspan="3" style="border-style: none none solid; border-color: -moz-use-text-color -moz-use-text-color windowtext; border-width: medium medium 1pt; padding: 0cm 5.4pt; width: 148.05pt; height: 12.25pt;" width="197">
  <p class="MsoNormal" style="text-align: center;" align="center"><b>DSSP</b></p>

  </td>
 </tr>
 <tr style="height: 40.75pt;">
  <td rowspan="3" style="border-style: none solid none none; border-color: -moz-use-text-color windowtext -moz-use-text-color -moz-use-text-color; border-width: medium 1pt medium medium; padding: 0cm 5.4pt; width: 46.35pt; height: 40.75pt;" width="62">
  <p class="MsoNormal" style="text-align: center;" align="center"><b>PDB</b></p>
  </td>
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 46.35pt; height: 40.75pt;" width="62">
  <p class="MsoNormal" style="text-align: center;" align="center">&nbsp;</p>

  </td>
  <td style="border-style: solid solid solid none; border-color: windowtext windowtext windowtext -moz-use-text-color; border-width: 1pt 1pt 1pt medium; padding: 0cm 5.4pt; width: 47.7pt; height: 40.75pt;" width="64">
  <p class="MsoNormal" style="text-align: center;" align="center">Entry<br>
  Present</p>
  </td>
  <td style="border-style: solid solid solid none; border-color: windowtext windowtext windowtext -moz-use-text-color; border-width: 1pt 1pt 1pt medium; padding: 0cm 5.4pt; width: 54pt; height: 40.75pt;" width="72">
  <p class="MsoNormal" style="text-align: center;" align="center">Entry<br>

  Missing</p>
  </td>
 </tr>
 <tr style="height: 44.4pt;">
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 46.35pt; height: 44.4pt;" width="62">
  <p class="MsoNormal" style="text-align: center;" align="center">Entry<br>
  Present</p>

  </td>
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 47.7pt; height: 44.4pt;" width="64">
  <p class="MsoNormal" style="text-align: center;" align="center">1<br>
  <span style="font-size: 20pt; color: rgb(51, 153, 102);">V</span></p>
  </td>
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 54pt; height: 44.4pt;" width="72">
  <p class="MsoNormal" style="text-align: center;" align="center">2<br>

  <span style="font-size: 20pt; color: red;">X</span></p>
  </td>
 </tr>
 <tr style="height: 42.6pt;">
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 46.35pt; height: 42.6pt;" width="62">
  <p class="MsoNormal" style="text-align: center;" align="center">Entry<br>
  Missing</p>

  </td>
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 47.7pt; height: 42.6pt;" width="64">
  <p class="MsoNormal" style="text-align: center;" align="center">3<br>
  <span style="font-size: 20pt; font-family: Wingdings; color: red;">X</span></p>
  </td>
  <td style="border-style: none solid solid none; border-color: -moz-use-text-color windowtext windowtext -moz-use-text-color; border-width: medium 1pt 1pt medium; padding: 0cm 5.4pt; width: 54pt; height: 42.6pt;" width="72">
  <p class="MsoNormal" style="text-align: center; page-break-after: avoid;" align="center">4<br>

  <span style="font-size: 20pt; color: rgb(51, 153, 102);">V</span></p>
  </td>
 </tr>
</tbody></table>

<p class="MsoCaption">Table 2: PDBIDs categories in evaluating present and
missing database entries for DSSP.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">Both the collection 1 and 4 are correct and do not need any
further attention. The collections 2 and 3 however show PDBIDs related to
database inconsistencies that will have to either be resolved or explained by a
database expert. Once a database inconsistency has been explained it is no
longer requires attention from the data expert. As such we've divided collection
2 into two distinct groups: those with a comment explaining the inconsistency
and those without a comment.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal">The reports page of the Web server is shows the number of
items in collections 1, 2 and 3 for each of the databases evaluated by the
WHY_NOT system. For collection 2 details are also given about how many PDBIDs in
this collection have comments explaining why the entry does not exist. This
information is divided into two columns, one for stored entries and one for
missing entries. For each of these columns the total number of items is given.
For stored entries this number is split up into correct entries (category 1)
and obsolete entries (category 3). For missing entries this number is split up
into entries with a comment and entries without a comment explaining why the
entry is missing. An example output for the reports page is shown in figure 4.</p>

<p class="MsoNormal">&nbsp;</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image004.jpg" border="0" height="481" width="576"></p>

<p class="MsoCaption">Figure 4: Example output for the reports page.</p>

<h3><a name="_Toc170791008">Collection</a></h3>

<p class="MsoNormal">The numbers given on the reports page represent collections
of PDBIDs meeting a certain set of requirements. The collection page shows the
PDBIDs in this collection and is accessible by clicking on any of the numbers
on the reports page.</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image005.gif" border="0" height="206" width="522"></p>

<p class="MsoCaption">Figure 5: Example output for the collection page.</p>

<h3><a name="_Toc170791009">WHY_NOT</a></h3>

<p class="MsoNormal">For selected PDBIDs the Web server can show which database
entries are present and any comments related to the existence of the database
entry. For entries that are present a hyperlink to view the file is given, as
well as the timestamp on which the file was created. This page is accessible by
entering a PDBID at the top of the page and pressing enter. An example output
for the PDBID <i>101D</i> is given in figure 6.</p>

<p class="MsoNormal" style="page-break-after: avoid;"><img src="images/image006.gif" border="0" height="503" width="554"></p>

<p class="MsoCaption">Figure 6: Example output for database entries present and
missing for PDBID 101D.</p>

</s:div>

<s:include value="../page_footer.jsp" />