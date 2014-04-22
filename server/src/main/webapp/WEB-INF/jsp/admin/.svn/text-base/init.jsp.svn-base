<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<nx:defaultPage>
	<jsp:attribute name="title">Admin creation</jsp:attribute>
	<jsp:attribute name="head">

	<jwr:style src="/bundles/admin.css"/>	
	
	
	<script language="javascript">
	onload=function() {
		nx.resources.restContext = "${pageContext.request.contextPath}/rest";
		var desc = {context: "${pageContext.request.contextPath}", datasources:[]};
		<c:forEach items="${model.dataSourceConfigurations}" var="ds">
			desc.datasources.push({id:${ds.id}, name:'${ds.name}', driver:'${ds.driverClassName}', url:'${ds.url}', username:'${ds.username}', password:'${ds.password}'});
		</c:forEach>
		nx.admin.init(desc);
		
		document.f.password.focus();
	};	
	</script>
</jsp:attribute>
	
	<jsp:body>	
		<div class="centerBigBox">
		<h3>Admin account creation</h3>
		<p>An administration user was not found for this installation. Please create one here.</p> 
		<p>Further users can be added by editing the <b>users</b> sheet from the <b>system</b> workbook.</p>
		<br/>
		<br/>
		<form id="createAdminForm" name='f' action='${pageContext.request.contextPath}/rest/admin/create' method='POST'>
		 <table class="form">
		    <tr><td colspan="2">Datasource:</td>
		    </tr>
		    <tr>
		    <td colspan="2">
			    <div id="datasources">
				     <ul>
				     	<c:forEach items="${model.dataSourceConfigurations}" var="ds">
				        	<li><a href="#display"><span>${ds.name}</span></a></li>
				        </c:forEach>
				        <li><a href="#display"><span>New</span></a></li>
				     </ul>
				</div>
				<div id="display">
			    	<input type='hidden' name="ds.id"  id='ds-id'>
					<table>
						<tr><td><label for="ds-driver">Driver</label></td><td><input type='text' name="ds.driver" id='ds-driver'  size="30" class="required"></td></tr>
						<tr><td><label for="ds-url">URL</label></td><td><input type='text' name="ds.url" id='ds-url'  size="50" class="required"></td></tr>
						<tr><td><label for="ds-username">Username</label></td><td><input type='text' name="ds.username" id='ds-username'  size="15" ></td></tr>
						<tr><td><label for="ds-password">Password</label></td><td><input type='text' name="ds.password" id='ds-password' size="15" ></td></tr>
					</table>	
				</div>
			</td>
			</tr>
		    <tr><td><label for="login">User</label>:</td><td><input type='text' name='login' id="login" value='admin' class="required" minlength="2" ></td></tr>
		    <tr><td><label for="password">Password</label>:</td><td><input type='password' name='password' id="password" class="required" minlength="2"/></td></tr>
		    <tr><td><label for="createDemo">Create demo workbook</label>:</td><td><input type="checkbox" name='createDemo' id="createDemo" value="true" checked/></td></tr>
		    <tr><td colspan='2' align="center"><input name="submit" type="submit" value="Create"/></td></tr>
		  </table>
		</form>
		</div>
	</jsp:body>
</nx:defaultPage>