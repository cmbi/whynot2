<%@ taglib prefix="s" uri="/struts-tags" %>
<s:div id="menu">
<ul>
	<li><s:a href="home.action"><s:text name="home"/></s:a></li>
	<li><s:a href="about.action"><s:text name="about"/></s:a>
		<ul><li><s:a href="problem.action"><s:text name="about.problem"/></s:a>
			<li><s:a href="solution.action"><s:text name="about.solution"/></s:a>
			<li><s:a href="databases.action"><s:text name="about.databases"/></s:a>
			<li><s:a href="implementation.action"><s:text name="about.implementation"/></s:a>
			<li><s:a href="futurework.action"><s:text name="about.futurework"/></s:a>
			<li><s:a href="references.action"><s:text name="about.references"/></s:a>
			<li><s:a href="software.action"><s:text name="about.software"/></s:a></ul>
	<li><s:a href="whynot.action"><s:text name="whynot"/></s:a>
		<ul><li><s:a href="reports.action"><s:text name="whynot.reports"/></s:a>
			<li><s:a href="comments.action"><s:text name="whynot.comments"/></s:a>
			<li><s:a href="collection.action"><s:text name="whynot.collection"/></s:a></ul>
</ul>
</s:div>
