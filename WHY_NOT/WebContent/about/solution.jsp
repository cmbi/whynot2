<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about.solution"/></h2>

<p class="MsoNormal">The WHY_NOT project was started to solve the above problems
associated with missing entries and other database inconsistencies. The project
aims to set up a system that introduces both quality control and accountability
for missing entries to the PDB-related databases by checking cross-database integrity
and storing the reason why an entry is missing. This information should then be
made available to end-users. Providing the above functionality requires
executing the following steps for each of the PDB-related databases:</p>

<ol style="margin-top: 0cm;" start="1" type="1">
 <li class="MsoNormal">Locate the database (mirror).<br>
     A system administrator will have to provide a locally readable mirror of
     the database.</li>

 <li class="MsoNormal">Index all entries in the database.<br>
     A subsystem will index and store information about all the entries in the
     local mirror.</li>
 <li class="MsoNormal">Check cross-database consistency.<br>
     Based upon dependencies between the PDB and PDB-related databases a
     subsystem will evaluate which entries are obsolete and which are missing.</li>
 <li class="MsoNormal">Consult database expert about inconsistencies.<br>
     A database expert can determine the reason for database inconsistencies
     and either store the reason or in some cases resolve the database
     inconsistencies.</li>

 <li class="MsoNormal">Iterate steps 3 and 4.<br>
     Repeat until there are either no more database inconsistencies or until a
     reason has been provided for all existing database inconsistencies.</li>
 <li class="MsoNormal">Provide a graphical interface to end-user.<br>
     An internet accessible graphical interface allows end-users to view which
     entries are missing and the reason why a given entry is missing.</li>
</ol>

<p class="MsoNormal">With the above system in place end-users, external
applications and web services can refer to this system when confronted with a
non-existing database entry. This eliminates the need to contact a database
expert, saving valuable time and effort while increasing response time.</p>

</s:div>

<s:include value="../page_footer.jsp" />