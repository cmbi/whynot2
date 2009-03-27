<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />
<s:div id="main">

<h2><s:text name="whynot"/>: <s:property value="PDBID.PDBID"/></h2>

<table border>
<tr>
	<th>Database</th>
	<th>Entry file</th>
	<th>Comments</th>
</tr>

<s:iterator value="databases">
	<s:action name="form!setEntry" executeResult="false" >
		<s:param name="spdbid"><s:property value="PDBID.PDBID"/></s:param>
		<s:param name="sdatabase"><s:property value="name"/></s:param>
	</s:action>
	<s:action name="form!setComments" executeResult="false" >
		<s:param name="spdbid"><s:property value="PDBID.PDBID"/></s:param>
		<s:param name="sdatabase"><s:property value="name"/></s:param>
		<s:param name="sproperty">Exists</s:param>
	</s:action>

	<tr>
		<td>
			<table width="100%">
			<tr><td><s:property value="name"/></td></tr>
			<tr><td align="right" style="font-size:75%"><a href="<s:property value="reference"/>">reference</a></td></tr>
			</table>
		</td>

		<td>
			<table>
			<tr>
				<td align="center" rowspan=2>
					<s:if test="#attr.entry!=null">
						<a href="<s:property value="filelink"/><s:property value="PDBID.PDBID"/>">
						<img src="images/file-present.png">
						</a>
					</s:if>
					<s:else>
						<img src="images/file-missing.png">
					</s:else>
				</td>

				<td style="font-size:75%">
					<s:if test="#attr.entry!=null">
						<a href="<s:property value="filelink"/><s:property value="PDBID.PDBID"/>">
						<s:text name="whynot.file_present"/>
						</a>
					</s:if>
					<s:else>
						<s:text name="whynot.file_missing"/>
					</s:else>
				</td>
			</tr>
			<tr>
				<td style="font-size:75%">
					<s:if test="#attr.entry!=null">
						<s:date name="#attr.entry.datetimestamp" format="dd/MM/yyyy hh:mm" />
					</s:if>
					<s:else>
						-
					</s:else>
				</td>
			</tr>
			</table>
		</td>

		<td>
			<table width="100%">
			<s:iterator value="#attr.comments">
				<s:url id="hrefwithcomment" action="whynot_setCollectionToPDBIDsWithComment">
					<s:param name="scomid"><s:property value="comid"/></s:param>
				</s:url>

				<tr>
					<td><s:a href="%{hrefwithcomment}"><s:property value="comment"/></s:a></td>
				</tr>
				<tr>
					<td align="right" style="font-size:75%">
						<a href="mailto:<s:property value="author.email"/>">
							<s:property value="author.name"/>
						</a>
						-
						<s:date name="datetimestamp" format="dd/MM/yyyy hh:mm" />
					</td>
				</tr>
			</s:iterator>
			</table>
		</td>
	</tr>
</s:iterator>
</table>

</s:div>
<s:include value="../page_footer.jsp" />