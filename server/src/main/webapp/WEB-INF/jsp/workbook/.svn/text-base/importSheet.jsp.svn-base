<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>

<html>
<head>
<script>
onload=function(){
	if (parent && parent.nx) {
		parent.nx.workbook.refreshTree();
	}
}
</script>
</head>
<body>
Created sheets:<br/>
<ul>
<c:forEach items="${model.sheetNames}" var="sheetName">
	<li><a href="${pageContext.request.contextPath}/rest/sheets/${sheetName}/edit" target="_blank">${sheetName}</a></li>
</c:forEach>
</ul>
Time:${model.time} ms<br/>
Messages during import:<br/>
${model.console}
</body>
</html>