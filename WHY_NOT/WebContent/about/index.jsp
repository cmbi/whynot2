<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about"/></h2>

<p class="MsoNormal">The WHY_NOT project was started to add quality control to the
PDB and PDB related databases and to solve the problems occurring when a user
is referred to non-existing database entry. To accomplish this all PDB and
PDB-related database entries are indexed and stored in a relational database.
Based on an evaluation of database inconsistencies a database expert is
consulted to either resolve or explain the database inconsistencies, the
results of which are stored in the RDB. This Web server aims to make
the information gathered above available to a wide audience.</p>

<ul><li><s:a href="problem.action"><s:text name="about.problem"/></s:a>
	<li><s:a href="solution.action"><s:text name="about.solution"/></s:a>
	<li><s:a href="databases.action"><s:text name="about.databases"/></s:a>
	<li><s:a href="implementation.action"><s:text name="about.implementation"/></s:a>
	<li><s:a href="futurework.action"><s:text name="about.futurework"/></s:a>
	<li><s:a href="references.action"><s:text name="about.references"/></s:a>
	<li><s:a href="software.action"><s:text name="about.software"/></s:a></ul>

</s:div>

<s:include value="../page_footer.jsp" />