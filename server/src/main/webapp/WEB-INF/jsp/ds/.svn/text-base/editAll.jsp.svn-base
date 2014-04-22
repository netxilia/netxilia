<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8"%>
<%@ include file="/WEB-INF/jsp/taglibs.jsp"%>
<nx:defaultPage>
	<jsp:attribute name="title">Datasource Administration</jsp:attribute>
	<jsp:attribute name="head">
	
		<style type="text/css">
			#id {
				border:none;
				width:30px;
			}
			#save {
				display:none;
			}
			.new #delete, .new #test, .new #view{
				display:none;
			}
		</style>
	<script language="javascript">
	$(document).ready(function () {	
		$(".tabs").tabs();

		<%-- both --%>
		$("input,select").live("change", function(ev) {
			var $tr = $(this).parent("td").parent();
			if ($tr.hasClass("new")) {	
				var $newTr = $tr.clone();
				$("input", $newTr).val("");
				$newTr.appendTo($tr.parent());
				
				$tr.removeClass("new");
			}
			$("#save", $tr).show(true);
		});
		
		<%-- datasources --%>
		$("#datasources #save").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			var id = $("#id", $tr).val();
			if (id == "")
				nx.resources.ds.add(
						$("#name", $tr).val(), $("#description", $tr).val(),
						$("#driver", $tr).val(), $("#url", $tr).val(), 
						$("#username", $tr).val(),$("#password", $tr).val(), 
						function(newid){
								$("#id", $tr).val(newid.id);
								$("#save", $tr).hide();
						});
			else
				nx.resources.ds.save(id,
						$("#name", $tr).val(), $("#description", $tr).val(), 
						$("#driver", $tr).val(), $("#url", $tr).val(), 
						$("#username", $tr).val(),$("#password", $tr).val(), 
						function(){
								$("#save", $tr).hide();
						});
		});

		$("#datasources #delete").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			var id = $("#id", $tr).val();
			if (id == "")
				$tr.remove();
			else
				nx.resources.ds.remove(id, function() {
					$tr.remove();
				});
		});

		$("#datasources #test").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			var id = $("#id", $tr).val();
			nx.resources.ds.test(id, function(msg){
				alert(msg);
			});
		});
		<%-- workbooks --%>
		$("#workbooks #save").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			nx.resources.ds.setConfigurationForWorkbook($("#workbook", $tr).val(),
					$("#datasource", $tr).val(), 
					function(){
							$("#save", $tr).hide();
					});
		});

		$("#workbooks #delete").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			var id = $("#workbook", $tr).val();
			if (id == "")
				$tr.remove();
			else
				nx.resources.ds.deleteConfigurationForWorkbook(id, function() {
					$tr.remove();
				});
		});

		$("#workbooks #view").live("click", function(ev){
			var $tr = $(this).parent("td").parent();
			var id = $("#workbook", $tr).val();
			window.open("${pageContext.request.contextPath}/rest/workbooks/" + id, "_blank");
		});

		$( ".tabs" ).bind( "tabsselect", function(event, ui) {
			 if (ui.index == 1) {
				 nx.resources.ds.list(function(cfgs) {
					 $("select.datasource").each(function(){
						 var $this = $(this);
						 $this.html("");
						 for(var c in cfgs) {
							 $("<option value='" + cfgs[c].id.id + "'>" + cfgs[c].name + "</option>").appendTo($this);
						 }
						 $this.val($this.attr("val"));
					 });					 
				 });
			 }
		});
	});
	</script>
</jsp:attribute>
	
	<jsp:body>	
	
		<div class="tabs">
	
		<ul>
			<li><a href="#tabs-datasources">Datasources</a></li>
			<li><a href="#tabs-workbooks">Workbooks</a></li>
		</ul>
	
		<%-- datasources --%>
		<div id="tabs-datasources">
		<p>This is the list with all the datasources available to Netxilia.</p> 
		<p>At least one is needed to be able to have Netxilia running.</p>
		
		<table id="datasources">
			<thead>
				<tr><th>ID</th><th>Name</th><th>Description</th><th>Driver</th><th>URL</th>
				<th>Username</th><th>Password</th></tr>
			</thead>
			<tbody>
			<c:forEach items="${model.configurations}" var="ds">
				<tr>
					<td><input type='text' id='id' value='${ds.id}' readonly></td>
					<td><input type='text' id='name' value='${ds.name}' size="20"></td>
					<td><input type='text' id='description' value='${ds.description}' size="30"></td>
					<td><input type='text' id='driver' value='${ds.driverClassName}' size="30"></td>
					<td><input type='text' id='url' value='${ds.url}' size="50"></td>
					<td><input type='text' id='username' value='${ds.username}' size="15"></td>
					<td><input type='text' id='password' value='${ds.password}' size="15"></td>	
					
					<td><a id="save" href="#">Save</a></td>
					<td><a id="delete" href="#">Delete</a></td>
					<td><a id="test" href="#">Test</a></td>
				</tr>
			</c:forEach>
			
				<tr class="new">
					<td><input type='text' id='id' value='' readonly></td>
					<td><input type='text' id='name' value='' size="20"></td>
					<td><input type='text' id='description' value='' size="30"></td>
					<td><input type='text' id='driver' value='' size="30"></td>
					<td><input type='text' id='url' value='' size="50"></td>
					<td><input type='text' id='username' value='' size="15"></td>
					<td><input type='text' id='password' value='' size="15"></td>	
					
					
					<td><a id="save" href="#">Save</a></td>
					<td><a id="delete" href="#">Delete</a></td>
					<td><a id="test" href="#">Test</a></td>
				</tr>
			</tbody> 
		</table>
		</div>
		
		<%-- workbooks --%>
		<div id="tabs-workbooks">
		<table id="workbooks">
			<thead>
				<tr><th>Workbook</th><th>Datasource</th></tr>
			</thead>
			<tbody>
			<c:forEach items="${model.workbooks}" var="wk">
				<tr>
					<td><input type='text' id='workbook' value='${wk.first}'  size="20"></td>
					<td><select class="datasource"  val="${wk.second}" id='datasource' size="1"></select></td>
					<td><a id="save" href="#">Save</a></td>
					<td><a id="delete" href="#">Delete</a></td>
					<td><a id="view" href="#">View</a></td>
				</tr>
			</c:forEach>
			
				<tr class="new">
					<td><input type='text' id='workbook' value=''  size="20"></td>
					<td><select class="datasource" id='datasource' size="1"></select></td>
					<td><a id="save" href="#">Save</a></td>
					<td><a id="delete" href="#">Delete</a></td>
					<td><a id="view" href="#">View</a></td>
				</tr>
			</tbody> 
		</table>
		</div>
		
		</div>
	</jsp:body>
</nx:defaultPage>