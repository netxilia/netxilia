<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<%@page import="org.netxilia.api.chart.Type"%>
<c:set var="sheet" value="${model.sheetModel}"/>
<c:set var="summarySheet" value="${model.summarySheetModel}"/>
<c:set var="privateSheet" value="${model.privateSheetModel}"/>
<c:set var="chartTypes" value="<%=Type.values()%>"/>

<% long t1 = System.currentTimeMillis(); %>
<nx:defaultPage>
	<jsp:attribute name="title">${sheet.name}${sheet.readOnly ? " (read-only)" : ""}</jsp:attribute>
	<jsp:attribute name="head">
		<jwr:style src="/bundles/sheet.css"/>
		<link href="${pageContext.request.contextPath}/rest/styles/${sheet.workbookName}" rel="stylesheet">

<script src="${pageContext.request.contextPath}/js/nx/nx.utils.js"></script>
<script src="${pageContext.request.contextPath}/js/nx/nx.splitter.js"></script>
<script src="${pageContext.request.contextPath}/js/nx/nx.threeColumn.js"></script>
<script src="${pageContext.request.contextPath}/js/nx/nx.table.js"></script>

<script src="${pageContext.request.contextPath}/generated-js/stjs.js"></script>

<%--
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/SplitterOptions.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/ThreeColumnOptions.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/Splitter.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/ThreeColumn.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/NXTable.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/ThreeColumnUI.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/SplitterUI.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/NXTableOptions.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/plugins/NetxiliaDialogOptions.js"></script>
 --%></script>

<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/WorkbookClientEvent.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/ChartDescription.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/EventData.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/WindowInfo.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/HomeDescription.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/DataSourceConfiguration.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/ClientEventType.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/NetxiliaEvent.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/WindowIndex.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/DataSourceConfigurationId.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/AdminDescription.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/JsCellReference.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/JsAreaReference.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/StringHolder.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/SheetDescription.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/data/SheetFullName.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Bounds.js"></script>

<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Utils.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Resources.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Application.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Home.js"></script>


<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/editors/Editor.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/editors/TextEditor.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/editors/SelectEditor.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/editors/DateEditor.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/editors/EditingContext.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/ConnectedWindow.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Cell.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/CellRange.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Sheet.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Diff.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/TreeView.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Bounds2.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/CellWithStyle.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/Shortcuts.js"></script>
<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/DialogBounds2.js"></script>

<script src="${pageContext.request.contextPath}/generated-js/org/netxilia/server/js/NX.js"></script>

		<script>
		var sheetUrl = "${pageContext.request.contextPath}/rest/sheets/${sheet.workbookName}/${sheet.name}";
		$(document).ready(function () {
				nx.resources.restContext = "${pageContext.request.contextPath}/rest";
				var desc = {
						context : "${pageContext.request.contextPath}",
						workbook: "${sheet.workbookName}", name: "${sheet.name}",
						username: "${user.login}", pageSize: ${sheet.pageSize},
						editors:{}
				};
			<c:forEach items="${model.editors}" var="e">
				desc.editors['${e.id}']='${e.attributes.editor.value}';
			</c:forEach>
				nx.app.init(desc);

				nx.app.sheet('${sheet.name}').frameLoaded(self, ${sheet.pageCount},
						{aliases: ${sheet.aliasesJson}, charts: ${sheet.chartsJson}, spans: ${sheet.spansJson}});
				nx.app.sheet('${summarySheet.name}').frameLoaded(self, ${summarySheet.pageCount},
						{aliases: ${summarySheet.aliasesJson}, charts: ${summarySheet.chartsJson}, spans: ${summarySheet.spansJson}});
				nx.app.sheet('${privateSheet.name}').frameLoaded(self, ${privateSheet.pageCount},
						{aliases: ${privateSheet.aliasesJson}, charts: ${privateSheet.chartsJson}, spans: ${privateSheet.spansJson}});
		});
		</script>
	</jsp:attribute>

	<jsp:body>
	<%-- toolbar --%>
	<div class="toolbar">
	<ul class="toolbar">
		<li class="ToolIcon_print" title="Print" onclick="nx.app.print()"></li>

		<li class="ToolIcon_undo disabled" title="Undo, Ctrl+Z" onclick="nx.app.undo()"></li>
		<li class="ToolIcon_redo disabled" title="Redo, Ctrl+Y" onclick="nx.app.redo()"></li>

		<li class="ToolIcon_cut" title="Cut, Ctrl+X" onclick="nx.app.cbCut(true)"></li>
		<li class="ToolIcon_copy" title="Copy, Ctrl+C" onclick="nx.app.cbCopy(true)"></li>
		<li class="ToolIcon_paste disabled" title="Paste, Ctrl+V" onclick="nx.app.cbPaste(true)"></li>

		<li class="separator"></li>

		<li class="ToolIcon_numformat dropdown" id="formatters-menu">$ %</li>

		<li class="ToolIcon_bold" title="Bold, Ctrl+B" onclick="nx.app.css('b')"></li>
		<li class="ToolIcon_italic" title="Italic, Ctrl+I" onclick="nx.app.css('i')"></li>
		<li class="ToolIcon_underline" title="Underline, Ctrl+U" onclick="nx.app.css('u')"></li>
		<li class="ToolIcon_strikethrough" title="Strike, Ctrl+5" onclick="nx.app.css('s')"></li>

		<li class="ToolIcon_fontSize dropdown" id="fontSizes-menu">10pt</li>

		<li class="ToolIcon_alignleft" title="Align Left, Ctrl+L" onclick="nx.app.css('a-l')"></li>
		<li class="ToolIcon_aligncenter" title="Align Center, Ctrl+E" onclick="nx.app.css('a-c')"></li>
		<li class="ToolIcon_alignright" title="Align Right" onclick="nx.app.css('a-r')"></li>
		<li class="ToolIcon_alignjustify" title="Justify" onclick="nx.app.css('a-j')"></li>
		<li class="ToolIcon_wordwrap" title="Word wrap" onclick="nx.app.css('wp')"></li>


		<li class="ToolIcon_cellcolor" title="Background Color" id="background-menu"></li>
		<li class="ToolIcon_fontcolor" title="Font Color" id="foreground-menu"></li>

		<li class="ToolIcon_border"  title="Borders" id="borders-menu"></li>
		<li class="ToolIcon_clearFormatting"  title="Clear formatting" onclick="nx.app.clearCss()"></li>
		<li class="ToolIcon_formatting"  title="Set formatting" onclick="nx.app.dlgStyles()"></li>

		<li class="separator"></li>

		<li class="ToolIcon_mergecells" title="Merge Cells" onclick="nx.app.mergeCells()"></li>

		<li class="ToolIcon_insertrow" title="Insert Row" onclick="nx.app.insertRow(false)"></li>
		<li class="ToolIcon_autoinsertrow" title="Toggle Auto Insert Row (after Enter at the current position)" onclick="nx.app.autoInsertRow = !nx.app.autoInsertRow;"></li>
		<li class="ToolIcon_deleterow" title="Delete Row" onclick="nx.app.deleteRow()"></li>
		<li class="ToolIcon_insertcol" title="Insert Column" onclick="nx.app.insertCol(true)"></li>
		<li class="ToolIcon_deletecol" title="Delete Column" onclick="nx.app.deleteCol()"></li>

		<li class="separator"></li>

		<li class="ToolIcon_find" title="Find" onclick="nx.app.dlgFind()"></li>

		<li class="ToolIcon_filter" title="Filter (F7)" onclick="nx.app.toggleFilter(false)"></li>
		<li class="ToolIcon_filterFormula" title="Filter with Formula (Ctrl+F7)" onclick="nx.app.toggleFilter(true)"></li>

		<li class="ToolIcon_sort" title="Sort, Ctrl+Alt+S" onclick="nx.app.sort()"></li>
		<li class="ToolIcon_tree" title="Toggle Tree view" onclick="nx.app.toggleTreeView()"></li>

		<li class="ToolIcon_alias" title="Edit Aliases" onclick="nx.app.dlgEditAliases()"></li>
		<li class="ToolIcon_chart" title="Add chart" onclick="nx.app.dlgChart()"></li>

		<li class="ToolIcon_privateNotes" title="Show private notes" onclick="nx.app.dlgPrivateNotes()"></li>
	</ul>
	</div>
		<%-- menu popups --%>
		<div id="foreground-popup" class="menu-popup">
			<table class="palette cells">
			<tbody>
				<c:forEach items="${model.foregrounds}" var="fg" varStatus="index">
					<c:if test="${index.index % model.foregroundColumns == 0}">
						<tr>
					</c:if>
						<td onclick="nx.app.css('${fg.id}')" class="${fg.id}">#</td>
					<c:if test="${index.index % model.foregroundColumns == model.foregroundColumns - 1}">
						</tr>
					</c:if>
				</c:forEach>
				<tr><td onclick="nx.app.clearCss('${model.firstForeground.id}')" colspan="${model.foregroundColumns}" style="text-align:center">Clear</td></tr>
			</tbody>
			</table>
		</div>

		<div id="background-popup" class="menu-popup">
			<table class="palette cells">
			<tbody>
				<c:forEach items="${model.backgrounds}" var="bg" varStatus="index">
					<c:if test="${index.index % model.backgroundColumns == 0}">
						<tr>
					</c:if>
						<td onclick="nx.app.css('${bg.id}')" class="${bg.id}"></td>
					<c:if test="${index.index % model.backgroundColumns == model.backgroundColumns - 1}">
						</tr>
					</c:if>
				</c:forEach>
				<tr><td onclick="nx.app.clearCss('${model.firstBackground.id}')" colspan="${model.backgroundColumns}" style="text-align:center">Clear</td></tr>
			</tbody>
			</table>
		</div>


		<div id="borders-popup" class="menu-popup">
			<table class="palette cells">
			<tbody>
				<tr>
					<td class="ToolIcon_borderAround" onclick="nx.app.borders({h:['f','l'], v:['f','l']})"></td>
					<td class="ToolIcon_borderNone" onclick="nx.app.borders()"></td>
					<td class="ToolIcon_borderBetween" onclick="nx.app.borders({h:['m'], v:['m']})"></td>
					<td class="ToolIcon_borderAll" onclick="nx.app.borders({h:['f','m','l'], v:['f','m','l']})"></td>
				</tr>
				<tr>
					<td class="ToolIcon_borderTop" onclick="nx.app.borders({v:['f']})"></td>
					<td class="ToolIcon_borderBottom" onclick="nx.app.borders({v:['l']})"></td>
					<td class="ToolIcon_borderLeft" onclick="nx.app.borders({h:['f']})"></td>
					<td class="ToolIcon_borderRight" onclick="nx.app.borders({h:['l']})"></td>
				</tr>
			</tbody>
			</table>
		</div>

		<div id="fontSizes-popup" class="menu-popup">
			<ul>
				<c:forEach items="${model.fontSizes}" var="s">
					<li onclick="nx.app.css('${s.id}')">${s.name}</li>
				</c:forEach>
			</ul>
		</div>

		<div id="formatters-popup" class="menu-popup">
			<ul>
				<c:forEach items="${model.formatters}" var="f">
					<li onclick="nx.app.css('${f.id}')">${f.name}</li>
				</c:forEach>
				<li onclick="nx.app.dlgAddFormatter()">New Ref Formatter</li>
			</ul>
		</div>

		<%-- dialogs used in the application --%>
		<div id="dialogs">
			<div id="find" title="Find">
				<input type="text" id="searchText" value="">
				<div id="findMessage"></div>
			</div>

			<div id="aliases" title="Aliases">
				<div id="aliasDefinitions"></div>
			</div>

			<div id="chart" title="Chart">
				<input type="hidden" id="chartId" value="">
				<ul>
				<li><label for="chartTitle">Title: </label><input type="text" id="chartTitle" value=""></li>
				<li><label for="chartArea">Cells: </label><input type="text" id="chartArea" value=""></li>
				<li><label for="chartType">Type: </label><select id="chartType">
					<c:forEach items="${chartTypes}" var="type">
						<option>${type}</option>
					</c:forEach>
					</select>
				</li>
				</ul>
			</div>

			<div id="disconnected" title="Disconnected">
				You have been disconnected. You'll be prompted to enter again your user and password to access this sheet.
			</div>

			<div id="styles" title="Styles">
				<input type="text" id="selectedStyles" value="">
			</div>

			<div id="addFormatter" title="Add Formatter">
			<ul>
				<li><label for="formatterName">Formatter Name:</label><input type="text" id="formatterName" value=""></li>
				<li><label for="formatterSourceWorkbook">Source Workbook:</label><input type="text" id=formatterSourceWorkbook value=""></li>
				<li><label for="formatterNameRange">Name Range:</label><input type="text" id="formatterNameRange" value=""></li>
				<li><label for="formatterValueRange">Value Range:</label><input type="text" id="formatterValueRange" value=""></li>
			</ul>
			</div>
		</div>


		<%-- body --%>
		<div class="sheetEditor">
			<nx:sheet containerClass="mainCells" sheet="${sheet}" showFixedRowsWithAliases="true"/>

			<%-- these are the summary cells as a separate sheet --%>
			<nx:sheet containerClass="summaryCells" sheet="${summarySheet}" showFixedRowsWithAliases="false"/>

			<div class="privateCellsDiv" title="Private notes">
				<nx:sheet containerClass="privateCells" sheet="${privateSheet}" showFixedRowsWithAliases="true"/>
			</div>
		</div>

	</jsp:body>
</nx:defaultPage>
<% long t2 = System.currentTimeMillis(); %>
<%System.out.println("PAGE:" + (t2-t1));%>
