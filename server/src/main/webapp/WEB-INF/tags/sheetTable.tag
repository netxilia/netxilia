<%@ attribute name="sheet" type="org.netxilia.server.rest.html.sheet.SheetModel"%>
<%@ attribute name="showFixedRows" type="java.lang.Boolean" %>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>

<table class="cells ${sheet.readOnly ? 'read-only' : ''}" style="width:${sheet.totalWidth + 60}px" cellspacing="0" cellpadding="0">
	<c:if test="${showFixedRows}">
		<thead>
		<tr class="labels">
				<th class="ref">&nbsp;</th>
				<c:forEach items="${sheet.columns}" var="col">
					<th style="width:${col.width}px">${col.label}</th>
				</c:forEach>
			</tr>
		</thead>
	</c:if>
		<tbody>
			<%-- used for column width --%>
			<tr class="cw hidden">
				<th style="width:60px"></th>
				<c:forEach items="${sheet.columns}" var="col">
					<td style="width:${col.width}px"></td>
				</c:forEach>
			</tr>
			<%-- all the other rows --%>
			<c:forEach items="${sheet.rows}" var="row">
				<tr>
					<th>${row.index + 1}</th>
					<c:forEach items="${row.cells}" var="cell">${cell.td}</c:forEach>
				</tr>
			</c:forEach>
		</tbody>
	</table>