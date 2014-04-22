<%@ attribute name="containerClass" %>
<%@ attribute name="sheet" type="org.netxilia.server.rest.html.sheet.SheetModel"%>
<%@ attribute name="showFixedRows" type="java.lang.Boolean" %>
<%@ attribute name="showFixedRowsWithAliases" type="java.lang.Boolean" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>

<div class="${containerClass}">
	<nx:threeColumnLayout>
		<jsp:attribute name="containerClass"></jsp:attribute>
		
		<jsp:attribute name="leftWidth">0</jsp:attribute>
		<jsp:attribute name="leftPadding">0</jsp:attribute>
		<jsp:attribute name="leftClass"></jsp:attribute>
		
		<jsp:attribute name="rightWidth">20</jsp:attribute>
		<jsp:attribute name="rightPadding">0</jsp:attribute>
		<jsp:attribute name="rightClass">pager</jsp:attribute>
		
		<jsp:attribute name="centerClass"></jsp:attribute>
		
		<jsp:attribute name="center">
			<%-- fixed rows --%>
			<c:if test="${showFixedRowsWithAliases}">
			<div class="fixedRowsDiv">
					<%-- fixed rows --%>
					<table class="fixedRows" cellspacing="0" cellpadding="0" style="width:${sheet.totalWidth + 60}px">
						<tr class="labels">
							<th class="ref">&nbsp;</th>
							<c:forEach items="${sheet.columns}" var="col">
								<th style="width:${col.width}px">${col.label}</th>
							</c:forEach>
						</tr>
						<tr class="aliases">
							<th class="alias"><input type="text" autocomplete="off" ></th>
							<c:forEach items="${sheet.columns}" var="col">
								<th><input id="alias-${col.label}" type="text" value="${col.alias}" autocomplete="off" ></th>
							</c:forEach>
						</tr>
					</table>
			</div>	
			</c:if>				
			
			<%-- content --%>
			<div class="cellsDiv">		
				<nx:sheetTable sheet="${sheet}" showFixedRows="${showFixedRows}"/>
				
				<%-- charts --%>
				<c:forEach items="${sheet.charts}" var="chart" varStatus="index">
					<nx:chart id="${index.index}" chart="${chart}" sheetName="${sheet.name}" workbook="${sheet.workbookName}"/>
				</c:forEach>
	  		</div>
	  		<c:if test="${showFixedRowsWithAliases}">
	  		<div class="horizSheetScroll">
	  			<div style="width:${sheet.totalWidth + 60}px;">.</div>
	  		</div>
	  		</c:if>
		</jsp:attribute>
		
		<jsp:attribute name="right"><%-- pager --%></jsp:attribute>
	</nx:threeColumnLayout>
</div>
