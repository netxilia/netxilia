<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<c:set var="sheet" value="${model}"/>
<c:if test="${sheet.overviewMode}">
<nx:sheet sheet="${sheet}" showFixedRows="true"/>
</c:if>
<c:if  test="${not sheet.overviewMode}">
<nx:sheetTable sheet="${sheet}" showFixedRows="false}"/>
</c:if>
