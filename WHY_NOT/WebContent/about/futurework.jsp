<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about.futurework"/></h2>

<p class="MsoNormal">Due to the project's limited scope and time span WHY_NOT is
currently limited to the above described functionality. There are however a
number of ways in which the current functionality can be extended or improved
upon. We will briefly name and explain a number of possible extensions to the
WHY_NOT system which might be implements at a later point in time:</p>

<ol style="margin-top: 0cm;" start="1" type="1">
 <li class="MsoNormal">Web service<br>
     The information in the RDB is only accessible via the WHY_NOT Web server.
     Adding a Web service would allow external applications to access this information
     directly rather then referring users to the WHY_NOT Web server.</li>
 <li class="MsoNormal">Additional databases<br>
     Additional databases could be added as long as they use the PDBID for
     identification and are stored in a manner similar to the databases already
     stored in WHY_NOT.</li>
 <li class="MsoNormal">Web administration<br>

     The current manner for database experts to add comments to WHY_NOT requires
     them to use the Commenter command line application. Adding a web interface
     to manage comments is highly preferential in this day and age.</li>
 <li class="MsoNormal">PDBID collection refinement<br>
     Adding the option for users to self define a collection of PDBIDs and/or
     refine this collection based on a certain set of properties could make it
     easier to get information about a custom collection of PDBIDs. </li>
 <li class="MsoNormal">Additional properties in Web server<br>
     Currently the RDB, DAO, Crawler and Commenter all support multiple
     properties per database entry. However the Web server does not yet show
     this additional information. Adding this to the Web server would allow us
     to take advantage of this functionality.</li>
 <li class="MsoNormal">Store reason for failed entry creation automatically<br>

     The Commenter command line application is able to read all the comment
     files in a directory. By altering the external applications to write
     comment files to a preset directory when failing to compute a PDB-related
     database entry, one can automate adding the reason why a database entry
     could not be created rather then having to rely on a database expert to
     enter this information.</li>
</ol>

</s:div>

<s:include value="../page_footer.jsp" />