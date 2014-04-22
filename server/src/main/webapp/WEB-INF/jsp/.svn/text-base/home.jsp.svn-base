<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>

<nx:defaultPage>
	<jsp:attribute name="title">Home</jsp:attribute>
	<jsp:attribute  name="head">
		<jwr:style src="/bundles/home.css"/>

		<link href="" rel="stylesheet" id="workbookStyles">

		<script>
			$(document).ready(function () {
				nx.resources.restContext = "${pageContext.request.contextPath}/rest";
				var desc = {context: "${pageContext.request.contextPath}", username: "${user.login}", mostUsedDataSource: "${model.mostUsedDataSource.id}"};			
				nx.workbook.init(desc);
		});
		</script>
	</jsp:attribute>
	<jsp:body>
		<nx:threeColumnLayout>
			<jsp:attribute name="containerClass">container</jsp:attribute>
			<jsp:attribute name="leftWidth">250</jsp:attribute>
			<jsp:attribute name="leftPadding">0</jsp:attribute>
			<jsp:attribute name="left">
				<div class="treeview-container">
				<ul class="filetree treeview-gray">
					${model.treeView}
				</ul>
				</div>
			</jsp:attribute>
	
			<jsp:attribute name="centerClass">center</jsp:attribute>			
			<jsp:attribute name="center">
				<div class="menus">		
					<div id="menu-sheet">
						<a href="javascript:nx.workbook.newWorkbook()">New workbook</a>
						<a href="javascript:nx.workbook.newSheet()">New spreadsheet</a>
						<a href="javascript:nx.workbook.importSheets()">Import</a>
						
						<a href="javascript:nx.workbook.viewSheet()">View</a>
						<a href="javascript:nx.workbook.editSheet()">Edit</a>
						<a href="javascript:nx.workbook.pdfSheet()">PDF</a>
						<a href="javascript:nx.workbook.exportSheet()">Export</a>
						<a href="javascript:nx.workbook.deleteSheet()">Delete</a>					
					</div>
					<div id="menu-workbook">
						<a href="javascript:nx.workbook.newWorkbook()">New workbook</a>
						<a href="javascript:nx.workbook.newSheet()">New spreadsheet</a>
						<a href="javascript:nx.workbook.importSheets()">Import</a>
						<a href="javascript:nx.workbook.deleteWorkbook()">Delete</a>					
					</div>
					<div id="menu-main">
						<a href="javascript:nx.workbook.newWorkbook()">New workbook</a>
					</div>
					<div id="menu-datasources">
						<a href="javascript:nx.workbook.newDatasource()">New datasource</a>
					</div>
					<div id="menu-datasource">
						<a href="javascript:nx.workbook.newDatasource()">New datasource</a>
						<a href="javascript:nx.workbook.testDatasource()">Test</a>
						<a href="javascript:nx.workbook.deleteDatasource()">Delete</a>
					</div>
					
					<div id="menu-modules">
						<a href="#">Accounting</a>
						<a href="#">Human Resources</a>
						<a href="#">CRM</a>
						<a href="#">Sales</a>
						<a href="#">Planning</a>
						<a href="#">Manufacturing</a>
					</div>
					
					<div id="menu-requests">
						<a href="#">Search</a>
						<a href="#">Finished</a>
						<a href="#">In Progress</a>
						<a href="#">Not Started</a>
						<a href="#">Propose new module</a>
					</div>
				</div>
				<div id="display"></div>							
			</jsp:attribute>			
			<jsp:attribute name="rightClass"></jsp:attribute>
			<jsp:attribute name="rightWidth">0</jsp:attribute>	
			<jsp:attribute name="rightPadding">0</jsp:attribute>	
		</nx:threeColumnLayout>
		
		<%-- dialogs used in the application --%>
		<div id="dialogs">
			<div id="newSheet" title="New spreadsheet">
				Name: <input type="text" id="sheetName" value=""  autocomplete="off"><br>
				
				<div id="error" class="error"></div>
			</div>
			
			<div id="newWorkbook" title="New workbook">
				Name: <input type="text" id="workbookName" value="" autocomplete="off"><br>
				Datasource: <select id="datasources">
				</select>
				
				<div id="error" class="error"></div>
			</div>

			<div id="importSheets" title="Import spreadsheets">
				<form method="post" action="${pageContext.request.contextPath}/rest/workbooks/main/import" enctype="multipart/form-data" target="importResults">
					File: <input type="file" id="importFile" name="file" value=""><br>
					Format: <select name="format">
						<option value="excel">Excel</option>
						<option value="json">JSON</option>
					</select>
				</form>
			</div>

		<div id="exportSheets" title="Export spreadsheets">
				<form method="get" action="#">
					Format: <select id="format">
						<option value="excel">Excel</option>
						<option value="json">JSON</option>
					</select>
				</form>
			</div>

		</div>
		
	</jsp:body>
</nx:defaultPage>