<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<%@ attribute name="head" fragment="true"%>
<%@ attribute name="header" fragment="true"%>
<%@ attribute name="footer" fragment="true"%>
<%@ attribute name="title"%>


	<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
	<html>
	<head>
	<title>Netxilia - ${title}</title>
		<jwr:script src="/bundles/jquery.js" useRandomParam="false"/>
		<jwr:script src="/bundles/other.js" useRandomParam="false"/>
<%--		<jwr:script src="/bundles/gen-nx.js" useRandomParam="false"/>  --%>

		<jsp:invoke fragment="head" />

		<jwr:style src="/bundles/base.css"/>

		<jsp:include page="/WEB-INF/jsp/custom-header.jsp"/>
	</head>
	<body>
		<div class="topline">
			<ul>
				<li id="message" class="first"></li>
				<c:if test="${not empty user}">
					<li>${user.login}</li>
					<li> <a href="${pageContext.request.contextPath}/j_spring_security_logout">Logout</a></li>
				</c:if>
			</ul>
			<img class="logo" src="${css}/img/logo.jpg"/>
		</div>

		<div class="content">
			<jsp:doBody />
		</div>
		<div class="footer">
			<jsp:invoke fragment="footer" />
		</div>

	</body>
	</html>


