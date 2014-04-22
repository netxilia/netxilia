var nx = this.nx || {};

nx.workbook = {
	
	init:function(desc) {
	
	this.desc = desc;
	$('.container').threeColumn({resizeLeft:true});
	
	
	var that = this;
	$(window).resize(function(){nx.workbook.resize();});
	$(document).ready(function() {
		that.bindTreeview(true);
		$("img.logo").click(function(){
			that.viewMain();
		});
	});
	this.viewMain();
	},
	
	selectNode: function(node){
		$(".filetree .selected").removeClass("selected");
		$(node).parent().addClass("selected");
	},
	
	bindTreeview: function(ok){
		var that = this;
		$(".filetree").treeview({
			persist: "cookie"
		});
		$(".filetree .sheet").click(function(){
			that.selectNode(this);
			that.datasource = null;
			that.sheet = $(this).text();
			that.workbook = $(this).parents("li").find(".workbook").text();
			that.viewSheet();
		});
		$(".filetree .workbook").click(function(){
			that.selectNode(this);
			that.datasource = null;
			that.sheet = null;
			that.workbook = $(this).text();
			that.viewWorkbook();
		});
		$(".filetree .datasources").click(function(){
			that.selectNode(this);
			that.sheet = that.workbook = that.datasource = null;
			that.viewDatasources();
		});
		$(".filetree .datasource").click(function(){
			that.selectNode(this);
			that.sheet = that.workbook = null;
			that.datasource = $(this).attr("id");
			that.viewDatasource();
		});
		
		$(".filetree .modules").click(function(){
			that.selectNode(this);
			that.sheet = that.workbook = that.datasource = null;
			that.viewModules();
		});
		
		$(".filetree .build").click(function(){
			that.selectNode(this);
			that.sheet = that.workbook = that.datasource = null;
			that.viewRequests();
		});
	},

	/**
	 * called to calculate the size of the application's elements
	 */
	resize: function() {
	},
	
	viewMain: function(){
		var that = this;
		that.sheet = that.workbook = that.datasource = null;
		$("#display").load(that.desc.context + "/readme.jsp");
		that.showMenu("main");
	},
	
	newSheet: function() {
		var that = this;
		$.nxdialog("newSheet", {
			height: 240,
			buttons: {
				Create: function(){
					var name = $("#sheetName", this).val();
					var $dlg = $(this);
					$dlg.find("#error").text("");
					nx.resources.sheets.newSheet(that.workbook, name, 
						function(sheet) {
							window.open(that.desc.context + "/rest/sheets/" + that.workbook + "/" + name + "/edit", "_blank");
							that.refreshTree();
							$dlg.dialog('close');
						}, 
						function(error, xhr, nxError){
							var err = nxError || xhr.statusText;
							$dlg.find("#error").text(err);
						}
					);
				}
			}
		}); 
	},
	
	newWorkbook: function() {
		var that = this;
		//fill the datasource select
		 nx.resources.ds.list(function(cfgs) {
			 var $ds = $("#datasources");
			 var setVal = $ds.val();
			 if (setVal == null)
				 setVal = that.desc.mostUsedDataSource;
			 $ds.html("");
			 for(var c in cfgs) {
				 $("<option value='" + cfgs[c].id.id + "'>" + cfgs[c].name + "</option>").appendTo($ds);
			 }
			 $ds.val(setVal);
		 });					 
		
		 
		$.nxdialog("newWorkbook", {
			height: 260,
			buttons: {
				Create: function(){
					var name = $("#workbookName", this).val();
					var datasource = $("#datasources", this).val();
					var $dlg = $(this);
					$dlg.find("#error").text("");
					
					nx.resources.workbooks.newWorkbook(name, datasource, 
							function(){		
								that.refreshTree();
								$dlg.dialog('close');
							},							
							function(error, xhr, nxError){
								var err = nxError || xhr.statusText;
								$dlg.find("#error").text(err);
							}
					);				
				}
			}
		}); 
	},
	
	
	importSheets: function(){
		var that = this;
		$("#display").html("<iframe name='importResults' frameborder='no' width='100%' height='600' src=''></iframe>");

		$.nxdialog("importSheets", {
			height: 250,
			width: 340,
			buttons: {
				Import: function(){
					var $frm = $("form", this);
					var $dlg = $(this);
					$frm.attr("action", that.desc.context + "/rest/workbooks/" + that.workbook + "/import");
					$frm.submit();
					$dlg.dialog('close');

//					$frm.ajaxSubmit({
//						url: that.desc.context + "/rest/workbooks/" + that.workbook + "/import",
//						dataType: "json",
//						success: function(responseText){
//							var sheet = eval("(" + responseText + ")");
//							that.refreshTree();
//							$dlg.dialog('close');
//						}
//					});				
				}
			}
		}); 
	},
	
	
	showMenu: function(type){
		if (type == "sheet")
			$("#workbookStyles").attr("href", this.desc.context + "/rest/styles/" + this.workbook );
		else
			$("#workbookStyles").attr("href", "");

		$("#menu-sheet").toggle(type=="sheet");
		$("#menu-workbook").toggle(type=="workbook");
		$("#menu-main").toggle(type=="main");
		$("#menu-datasources").toggle(type=="datasources");
		$("#menu-datasource").toggle(type=="datasource");
		
		$("#menu-requests").toggle(type=="requests");
		$("#menu-modules").toggle(type=="modules");
	},
	
	refreshTree: function(){
		var that = this;
		nx.resources.home.treeview(function(tv) {
			setTimeout(function() {
				$(".filetree").html(tv.value);
				that.bindTreeview();
			}, 1);
		});
	},
	
	viewSheet: function() {
		this.showMenu("sheet");
		if (this.sheet) {
			$("#display").load($.url(this.desc.context + "/rest/sheets/{}/{}/overview", this.workbook, this.sheet));
		}
	},
	
	viewWorkbook: function() {
		this.showMenu("workbook");
		if (this.workbook)
			$("#display").load(this.desc.context + "/rest/workbooks/" + this.workbook);
	},
	
	deleteWorkbook: function() {
		var that = this;
		if (this.workbook) {
			if (confirm("Are you sure you want to delete this workbook?")){
				nx.resources.workbooks.deleteWorkbook(this.workbook, function(){
					that.refreshTree();
					that.viewDatasources();
				});
			}
		}
	},
	
	editSheet: function() {
		if (this.sheet)
			window.open(this.desc.context + "/rest/sheets/" + this.workbook + "/" + this.sheet + "/edit",  "_blank");
	},
	
	deleteSheet: function() {
		var that = this;
		if (this.sheet) {
			if (confirm("Are you sure you want to delete this sheet?")){
				nx.resources.sheets.del(this.workbook, this.sheet, 
				function(){
					that.refreshTree();
					that.viewWorkbook();
				},
				function(error, xhr, nxError){
					var err = nxError || xhr.statusText;
					alert(err);
				});
			}
		}
	},
	
	pdfSheet: function() {
		if (this.sheet)
			$("#display").html("<iframe frameborder='no' width='100%' height='600' src='" + this.desc.context + "/rest/sheets/" + this.workbook + "/" + this.sheet + "/pdf" + "'></iframe>");
	},
	
	exportSheet: function() {
		if (this.sheet) {
			var that = this;
			$.nxdialog("exportSheets", {
				height: 150,
				width: 300,
				buttons: {
					Export: function(){
						var $frm = $("form", this);
						var format = $("#format", $frm);
						var $dlg = $(this);

						//$("#display").html("<iframe frameborder='no' width='100%' height='600' src='" + that.desc.context + "/rest/sheets/" + that.workbook + "/" + that.sheet + "/" + format.val() + "'></iframe>");
						window.open(that.desc.context + "/rest/sheets/" + that.workbook + "/" + that.sheet + "/" + format.val(), "_blank");
						$dlg.dialog('close');
					}
				}
			}); 
		}
	},
	
	viewDatasources: function(){
		this.showMenu("datasources");
		$("#display").html("");
	},
	
	viewModules: function(){
		this.showMenu("modules");
		$("#display").load(this.desc.context + "/temp/modules.jsp");
	},
	
	viewRequests: function(){
		this.showMenu("requests");
		$("#display").load(this.desc.context + "/temp/requests.jsp");
	},
	
	viewDatasource: function(ds){
		var that = this;
		this.showMenu("datasource");
		if (ds)
			this.datasource = ds;
		
		nx.resources.ds.edit(this.datasource, function(html){
			$("#display").html(html);
			$("#form-datasource").ajaxForm(function(){
				that.refreshTree();
			});
		});
	},
	
	deleteDatasource: function(){
		var that = this;
		if (this.datasource) {
			if (confirm("Are you sure you want to delete this datasource?")){
				nx.resources.ds.remove(this.datasource, function() {
					that.refreshTree();
					that.viewDatasources();
				});
			}
		}
	},
	
	testDatasource: function() {
		if (this.datasource) {
			nx.resources.ds.test(this.datasource, function(msg){
				alert(msg);
			});
		}
	},
	
	newDatasource: function() {
		var that = this;
		this.datasource = "";
		this.showMenu("datasource");
		$("#display").load(this.desc.context + "/rest/ds/editNew", function(){
			$("#form-datasource").ajaxForm(function(responseText){
				var ds = eval("(" + responseText + ")");
				that.datasource = ds.id;
				that.refreshTree();
				that.viewDatasource();
			});
		});
	}
	
};