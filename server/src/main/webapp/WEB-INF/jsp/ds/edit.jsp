<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<nx:emptyPage>
	<jsp:body>	
		<c:set var="ds" value="${model.configuration}"/>
		<c:if test="${empty ds.id}">		
		<form id="form-datasource" action="${pageContext.request.contextPath}/rest/ds" method="put">
		</c:if>
		<c:if test="${not empty ds.id}">		
		<form id="form-datasource" action="${pageContext.request.contextPath}/rest/ds/${ds.id}" method="post">
		</c:if>
			
			<table>
				<tr><td><label>ID</label></td><td><input type='text' name="id"  id='id' value='${ds.id}' readonly></td></tr>
				<tr><td><label>Name</label></td><td><input type='text' id='name' name="name"  value='${ds.name}' size="20"></td></tr>
				<tr><td><label>Description</label></td><td><input type='text' name="description" id='description' value='${ds.description}' size="30"></td></tr>
				<tr><td><label>Driver</label></td><td><input type='text' name="driver" id='driver' value='${ds.driverClassName}' size="30"></td></tr>
				<tr><td><label>URL</label></td><td><input type='text' name="url" id='url' value='${ds.url}' size="50"></td></tr>
				<tr><td><label>Username</label></td><td><input type='text' name="username" id='username' value='${ds.username}' size="15"></td></tr>
				<tr><td><label>Password</label></td><td><input type='text' name="password" id='password' value='${ds.password}' size="15"></td></tr>
			</table>
			<input type="submit" name="save" value="Save">
			<hr>
			<h2>Workbooks</h2> 
			The workbooks using this datasource (you can delete a datasource only if no workbook is using it):<br>
			<ul>
				<c:forEach items="${model.workbookIds}" var="wk">
					<li>${wk}</li>
				</c:forEach>
			</ul>
		</form>
	</jsp:body>
</nx:emptyPage>