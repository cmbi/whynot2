<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">

<h2><s:text name="about.databases"/></h2>

<table>
<tr>
	<th>Database</th>
	<th>Reference</th>
</tr>
<s:iterator value="databases">
	<tr>
		<td><s:property value="name"/></td>
		<td><a href="<s:property value="reference"/>">reference</a></td>
	</tr>
</s:iterator>
</table>

</s:div>

<s:include value="../page_footer.jsp" />