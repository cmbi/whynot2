<%@ taglib prefix="s" uri="/struts-tags" %>
<s:if test="hasErrors()">
<s:div id="errors">
	<s:actionerror/>
</s:div>
</s:if>