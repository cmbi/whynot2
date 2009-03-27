<%@ taglib prefix="s" uri="/struts-tags" %>
<s:include value="../page_header.jsp" />

<s:div id="main">
<h2><s:text name="whynot.collection"/></h2>
<s:property value="collectionsource"/>

<s:url id="hrefdetail0" action="collection_setLevelOfDetail"><s:param name="sdetail">0</s:param></s:url>
<s:url id="hrefdetail1" action="collection_setLevelOfDetail"><s:param name="sdetail">1</s:param></s:url>
<s:url id="hrefdetail2" action="collection_setLevelOfDetail"><s:param name="sdetail">2</s:param></s:url>

<s:if test="0 < collection.length">
	<s:if test="levelOfDetail==0">
		<table>
		<tr><th>PDBIDs (<s:property value="collection.length"/>)</th></tr>
		<tr><td id="PDBID"><s:iterator value="collection"><s:property value="PDBID"/> </s:iterator></td></tr>
		</table>
		Show <s:a href="%{hrefdetail1}">links</s:a> / <s:a href="%{hrefdetail2}">files</s:a>
	</s:if>

	<s:if test="levelOfDetail==1">
		<table>
		<tr><th>PDBIDs (<s:property value="collection.length"/>)</th></tr>
		<tr><td id="PDBID">
			<s:iterator value="collection">
				<a href="form.action?spdbid=<s:property value="PDBID"/>"><s:property value="PDBID"/></a>
			</s:iterator></td></tr>
		</table>
		Show <s:a href="%{hrefdetail0}">PDBIDs</s:a> / <s:a href="%{hrefdetail2}">files</s:a>
	</s:if>

	<s:if test="levelOfDetail==2">
		<table border>
		<tr>
			<th>PDBIDs (<s:property value="collection.length"/>)</th>
			<s:iterator value="databases">
				<th><s:property value="name"/></th>
			</s:iterator>
		</tr>
		<s:iterator value="collection">
			<tr>
				<td id="PDBID"><a href="form.action?spdbid=<s:property value="PDBID"/>"><s:property value="PDBID"/></a></td>
				<s:iterator value="databases">
					<s:action name="form!setEntry" executeResult="false" >
						<s:param name="spdbid"><s:property value="PDBID"/></s:param>
						<s:param name="sdatabase"><s:property value="name"/></s:param>
					</s:action>
					<td>
						<table>
						<tr>
							<td align="center" rowspan=2>
								<s:if test="#attr.entry!=null">
									<a href="<s:property value="filelink"/><s:property value="PDBID"/>">
									<img src="images/file-present.png">
									</a>
								</s:if>
								<s:else>
									<img src="images/file-missing.png">
								</s:else>
							</td>

							<td style="font-size:75%"><nobr>
								<s:if test="#attr.entry!=null">
									<a href="<s:property value="filelink"/><s:property value="PDBID"/>">
									<s:text name="whynot.file_present"/>
									</a>
								</s:if>
								<s:else>
									<s:text name="whynot.file_missing"/>
								</s:else>
							</nobr></td>
						</tr>
						<tr>
							<td style="font-size:75%"><nobr>
								<s:if test="#attr.entry!=null">
									<s:date name="#attr.entry.datetimestamp" format="dd/MM/yyyy hh:mm" />
								</s:if>
								<s:else>
									-
								</s:else>
							</nobr></td>
						</tr>
						</table>
					</td>
				</s:iterator>
			</tr>
		</s:iterator>
		</table>
		Show <s:a href="%{hrefdetail0}">PDBIDs</s:a> / <s:a href="%{hrefdetail1}">links</s:a>
	</s:if>
</s:if>
<s:else>
	<table>
		<tr><th>PDBIDs (0)</th></tr>
		<tr><td>Collection empty.</td></tr>
	</table>
	Click on Comments or Reports to fill collection.
</s:else>
</s:div>

<s:include value="../page_footer.jsp" />