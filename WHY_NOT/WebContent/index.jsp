<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="page_header.jsp" />

<s:div id="main">

<h2><s:text name="home"/></h2>

WHY_NOT aims to provide useful annotation for missing Protein Data Bank derived and related database entries. To accomplish this all PDB and PDB-derived and -related database entries are indexed and stored in a relational database. A database expert then either resolves or explains the missing entries, the results of which are made available through this webpage.<br><br>
Enter a PDBID into the form above or click on Comments or Reports to get started. Click on About to read more about this project.<br><br>
The WHY_NOT project currently holds:

<s:action name="home!getRelationalDatabaseStats" executeResult="false"></s:action>
<ul>
<li><s:property value="#attr.rdbstats.pdbids"/> Protein Data Bank Identifiers</li>
<li>in <s:property value="#attr.rdbstats.databases"/> different data banks and databases,</li>
<li>containing <s:property value="#attr.rdbstats.entries"/> files and records.</li>
<li><s:property value="#attr.rdbstats.comments"/> comments have been provided by database experts</li>
<li>to explain <s:property value="#attr.rdbstats.entrypropertycomments"/> database inconsistencies.</li>
</ul>

</s:div>

<s:include value="page_footer.jsp" />