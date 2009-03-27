<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />
<s:div id="main">

<h2><s:text name="whynot.comments"/></h2>

<s:if test="hasErrors()==false">
<table border>
<tr>
	<th>ID</th>
	<th>Comment</th>
	<th>Entries</th>
</tr>

<s:iterator value="comments">
	<s:action name="comments!getCommentStats" executeResult="false">
		<s:param name="scomid"><s:property value="comid"/></s:param>
	</s:action>

	<s:url id="hrefwithcomment" action="comments_withcomment"><s:param name="scomid"><s:property value="comid"/></s:param></s:url>

	<tr>
		<td><s:property value="comid"/></td>
		<td><s:property value="comment"/></td>
		<td align="right"><s:a href="%{hrefwithcomment}"><s:property value="#attr.withcomment"/></s:a></td>
	</tr>
</s:iterator>

</table>
</s:if>
</s:div>
<s:include value="../page_footer.jsp" />