<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />
<s:div id="main">

<h2><s:text name="whynot.reports"/></h2>

<s:if test="hasErrors()==false">
<s:iterator value="databases">
	<s:action name="reports!getDatabaseStats" executeResult="false">
		<s:param name="sdatabase"><s:property value="name"/></s:param>
	</s:action>

	<s:url id="hrefindball" action="reports_indball"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>
	<s:url id="hrefindbcorrect" action="reports_indbcorrect"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>
	<s:url id="hrefindbincorrect" action="reports_indbincorrect"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>

	<s:url id="hrefnotindball" action="reports_notindball"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>
	<s:url id="hrefnotindbcorrect" action="reports_notindbcorrect"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>
	<s:url id="hrefnotindbincorrect" action="reports_notindbincorrect"><s:param name="sdatabase"><s:property value="name"/></s:param></s:url>

<table border>
	<tr>
		<th colspan=3>
			<s:property value="name"/><br>
			<a style="font-size:75%" href="<s:property value="reference"/>">reference</a>
		</th>
	</tr>
	<tr>
		<td rowspan=6>
		<img src="http://chart.apis.google.com/chart?
		cht=p&amp;
		chs=500x200&amp;
		chl=
			Correct (<s:property value="#attr.dbstats.indbcorrect"/>)|
			Obsolete (<s:property value="#attr.dbstats.indbincorrect"/>)|
			Missing annotated (<s:property value="#attr.dbstats.notindbcorrect"/>)|
			Missing unannotated (<s:property value="#attr.dbstats.notindbincorrect"/>)&amp;
		chd=t:
			<s:property value="#attr.dbstats.indbcorrect"/>,
			<s:property value="#attr.dbstats.indbincorrect"/>,
			<s:property value="#attr.dbstats.notindbcorrect"/>,
			<s:property value="#attr.dbstats.notindbincorrect"/>&amp;
		chds=0,100000"/>
		</td>
		<td class="correct">Correct</td>
		<td align="right"><s:a href="%{hrefindbcorrect}"><s:property value="#attr.dbstats.indbcorrect"/></s:a></td>
	</tr>
	<tr>
		<td class="incorrect">Obsolete</td>
		<td align="right"><s:a href="%{hrefindbincorrect}"><s:property value="#attr.dbstats.indbincorrect"/></s:a></td>
	</tr>
	<tr>
		<td>Total Stored</td>
		<td align="right"><s:a href="%{hrefindball}"><s:property value="#attr.dbstats.indball"/></s:a></td>
	</tr>
	<tr>
		<td class="correct">Annotated</td>
		<td align="right"><s:a href="%{hrefnotindbcorrect}"><s:property value="#attr.dbstats.notindbcorrect"/></s:a></td>
	</tr>
	<tr>
		<td class="incorrect">Unannotated</td>
		<td align="right"><s:a href="%{hrefnotindbincorrect}"><s:property value="#attr.dbstats.notindbincorrect"/></s:a></td>
	</tr>
	<tr>
		<td>Total Missing</td>
		<td align="right"><s:a href="%{hrefnotindball}"><s:property value="#attr.dbstats.notindball"/></s:a></td>
	</tr>
</table>
<br>
</s:iterator>
</s:if>
</s:div>
<s:include value="../page_footer.jsp" />