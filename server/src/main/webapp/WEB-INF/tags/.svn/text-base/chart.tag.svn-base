<%@ attribute name="chart" type="org.netxilia.api.chart.Chart" %>
<%@ attribute name="id" %>
<%@ attribute name="workbook"%>
<%@ attribute name="sheetName"%>

<div id="chart${id}" class="chart" style="left: ${chart.left}px; top: ${chart.top}px; width:${chart.width}px; height:${chart.height}px">
	<script type="text/javascript">
	swfobject.embedSWF(
	  "${scripts}/open-flash-chart.swf", "chartFlash${id}", "100%", "95%",
	  "9.0.0", "expressInstall.swf",
	  {"data-file":"${pageContext.request.contextPath}/rest/charts/${workbook}/${sheetName}/${id}"},
	  {wmode:"transparent"}
	  );
	</script>
	<div class="chart-area"><a href="javascript:nx.app.sheet('${sheetName}').toggleMarkArea(parseAreaReference('${chart.areaReference}'), 'formula')">${chart.areaReference}</a></div>
	<div class="chart-menu">
		<a href="javascript:nx.app.sheet('${sheetName}').chartRefresh(${id})">Refresh</a>
		<a href="javascript:nx.app.sheet('${sheetName}').chartSettings(${id})">Settings</a>
		<a href="javascript:nx.app.sheet('${sheetName}').chartDelete(${id})">Delete</a>
	</div>
	<div id="chartFlash${id}"></div>
</div>