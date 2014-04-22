<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
<link href="theme/jquery-ui-1.7.2.custom.css" rel="stylesheet">
<link href="css/nx.css" rel="stylesheet">
<link href="css/nx.sheet.css" rel="stylesheet">
<link href="css/nx.threeColumn.css" rel="stylesheet">
<link href="css/jquery.treeview.css" rel="stylesheet">

<script src="js/jquery-1.3.2.js"></script>
<script src="js/ui.core.js"></script>
<script src="js/ui.resizable.js"></script>
<script src="js/ui.draggable.js"></script>
<script src="js/nx.utils.js"></script>
<script src="js/nx.table.js"></script>

<style>
	.cellsDiv2 {
	overflow-x: hidden;
	overflow-y: auto;
	height:200px;
	}
</style>
<script>
	$(document).ready(function(){
		$(".nxTable").nxtable({
			cellsDivSelector:".cellsDiv2",
			fixedRowsDivSelector:".fixedRowsDiv2"});
	});
</script>
</head>
<body style="overflow:auto">
		<%=System.getProperty("java.io.tmpdir") %>
	<div class="nxTable" style="width:250px;height:250px;border:solid 1px;overflow:hidden;position:relative">
		<div class="fixedRowsDiv2">
			<table class="fixedRows" style="width:460px">
				<tbody>
					<tr>
						<th style="width:60px">XXX</th>
						<th style="width:100px">column 1</th>
						<th style="width:100px">column 2</th>
						<th style="width:100px">column 3</th>
						<th style="width:100px">column 4</th>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="cellsDiv2">
		<table class="cells" style="width:460px">
			<tbody>
				<tr class="cw hidden">
					<th style="width: 60px;"></th>
					<td style="width: 100px;"></td>
					<td style="width: 100px;"></td>
					<td style="width: 100px;"></td>
					<td style="width: 100px;"></td>
				</tr>
				<tr><th>1</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>2</th><td colspan="2"><div class="merge">column 1 and column2</div></td><td>column3</td><td>column4</td></tr>
				<tr><th>3</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>4</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>5</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>6</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>7</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>8</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>9</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>10</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>11</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>12</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>

				<tr><th>13</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>14</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>15</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>16</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>17</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>18</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>19</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>20</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>21</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
				<tr><th>22</th><td>column 1</td><td>column2</td><td>column3</td><td>column4</td></tr>
			</tbody>
		</table>
		</div>
		<div class="horizSheetScroll">
	  		<div style="width: 460px;">.</div>
	  	</div>
	</div>
</body>
</html>