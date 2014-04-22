<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<nx:defaultPage>
	<jsp:attribute name="title">Login</jsp:attribute>
	<jsp:attribute name="head">
	<script language="javascript">
	onload=function() {
		document.f.j_username.focus();
	};	
	</script>
</jsp:attribute>
	
	<jsp:body>	
		<div class="centerSmallBox">
		<h3>Login with Username and Password</h3>
		
		<form name='f' action='${pageContext.request.contextPath}/j_spring_security_check' method='POST'>
		 <table class="form">
		 	<tr><td>User:</td><td><input type='text' name='j_username' value=''></td></tr>
		    <tr><td>Password:</td><td><input type='password' name='j_password'/></td></tr>
		    <tr><td colspan="2"><input type='checkbox' name='_spring_security_remember_me'/> Remember me on this computer.</td></tr>
		    <tr><td colspan='2' align="center"><input name="submit" type="submit" value="Login"/></td></tr>
		  </table>
		</form>
		</div>
	</jsp:body>
</nx:defaultPage>