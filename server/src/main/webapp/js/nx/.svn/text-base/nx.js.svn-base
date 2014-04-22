var nx = this.nx || {};

nx.app={
	sheets: {},
	exiting: false,
	copySource: null,
	copyContent: null,
	cutSource: null,
	activeSheet:null,
	autoInsertRow: false,
	pazeSize : 0,
	lastSortSpec : null,
	dialogs: {},
	
	init: function(desc) {
		var cookie = this.getCookie();
		var that = this;
		this.pageSize = desc.pageSize;
		//$('.sheetEditor').threeColumn({resizeRight:true});
		$('.sheetEditor').splitter();
		
		$(".tabs").tabs();
				
		positionUnder('#foreground-menu', "#foreground-popup");
		positionUnder('#background-menu', "#background-popup");
		positionUnder('#borders-menu', "#borders-popup");
		positionUnder('#fontSizes-menu', "#fontSizes-popup");
		positionUnder('#formatters-menu', "#formatters-popup");
		
		$('#foreground-menu').popupmenu({target: "#foreground-popup",time: 300 });
		$('#background-menu').popupmenu({target: "#background-popup",time: 300 });
		$('#borders-menu').popupmenu({target: "#borders-popup",time: 300 });
		$('#fontSizes-menu').popupmenu({target: "#fontSizes-popup",time: 300 });
		$('#formatters-menu').popupmenu({target: "#formatters-popup",time: 300, closeOnClick:true });
		
		this.desc = desc;
		
		//now init the sheets
		this.sheets[this.desc.name]=new Sheet().init(this.desc, $(".mainCells"));
		this.sheets[this.desc.name + ".summary"]=new Sheet().init($.extend({}, this.desc, {name:this.desc.name + ".summary"}), $(".summaryCells"));
		this.sheets[this.desc.name + "." + this.desc.username]=new Sheet().init($.extend({}, this.desc, {name:this.desc.name + "." + this.desc.username}), $(".privateCells"));
		this.activeSheet = this.sheets[this.desc.name];	

		window.onbeforeunload=function(){
			that.exiting = true;
			that.terminateWindow();
			that.setCookie();			
		};
		
		nx.resources.getHeaders = function() {
			return {nxwindow: that.windowId};
		};
		
		nx.resources.disconnected = function() {
			that.dlgDisconnected();
		};
		this.resize();
		$(window).resize(function(){nx.app.resize();});
		
		$(document).keydown(function(ev) {
			if (ev.target.tagName.toLowerCase() == "input")
				return;
			return that.activeSheet.shortcuts.handleEvent(ev);
		}).keypress(function(ev){
			if (that.activeSheet.waitForKeypress){
				that.activeSheet.editingContext.showDefaultEditor(that.activeSheet.selection.start, String.fromCharCode(ev.which));
				that.activeSheet.waitForKeypress = false;
				return false;
			}
		});
		
		
		$(".privateCellsDiv").dialog({
			resizeable:true,
			width: cookie.pdlg.w,
			height: cookie.pdlg.h,
			position: [cookie.pdlg.l,cookie.pdlg.t], 
			autoOpen: cookie.pdlg.o,
			close:function() {
				that.menuStatus();
			}
		});

		$(".topline .first").after('<li><select id="users"><option id="count">Other users (0)</option></select></li>');
		

	},
	
	getCookie: function() {
		var cookieString = $.cookie("nx");
		var cookie = cookieString ? JSON.parse(cookieString) : {};
		var wwindow = $(window).width();
		cookie = $.extend({
				pdlg: {w:480, h:280, t: 120, l:wwindow - 480 - 50 , o: true}
			},
			cookie
		);
		return cookie;
	},
	
	setCookie: function(){
		var pdlg = $(".ui-dialog:has(.privateCellsDiv)");
		pdlg.show();
		var pdlgBounds = pdlg.bounds();
		pdlg.hide();
		pdlgBounds.o = $(".privateCellsDiv").dialog("isOpen");
		var cookieData = JSON.stringify({
			pdlg: pdlgBounds});
		$.cookie("nx", cookieData, {expires:300});
	},
	
	layout: function() {
		//$('.sheetEditor').layout({resize: false});
	},
	/**
	 * called to calculate the size of the application's elements
	 */
	resize: function() {
		var editor = $('.sheetEditor');
		var editorOff = editor.offset();	
	},
	
	sheet: function(name) {
		return this.sheets[name];
	},
	
	setActiveSheet:function(sheet) {
		if (sheet == this.activeSheet)
			return;
		//make scrollbars for cell divs appear only when needed
		if (this.activeSheet)
			$(".cellsDiv", this.activeSheet.container).addClass("no-overflow");
		if (sheet)
			$(".cellsDiv", sheet.container).removeClass("no-overflow");
		
		this.activeSheet = sheet;
		this.menuStatus();
	},
	
	insertRow: function(below) {
		var controller = this;
		nx.resources.rows.insert(this.desc.workbook, this.activeSheet.desc.name, below ? this.activeSheet.selection.start.row + 1 : this.activeSheet.selection.start.row, 
				function(){controller.activeSheet.checkAutoInsertRow();});
	},

	deleteRow: function() {
		nx.resources.rows.del(this.desc.workbook, this.activeSheet.desc.name, this.activeSheet.selection.start.row);
	},
	
	insertCol: function(right) {
		nx.resources.columns.insert(this.desc.workbook, this.activeSheet.desc.name, this.activeSheet.selection.start.col);
	},
	
	deleteCol: function() {
		nx.resources.columns.del(this.desc.workbook, this.activeSheet.desc.name, this.activeSheet.selection.start.col);
	},
	/**
	 * merge selected cells
	 */
	mergeCells: function() {
		nx.resources.cells.merge(this.desc.workbook, this.activeSheet.selection.ref(true));
	},
	
	/**
	 * validationErros contains one entry per wrong parameter. 
	 * Each entry contains a list of the errors concerning the field.
	 */
	processEventErrors: function(validationErrors) {
		for(var f in validationErrors)
			for(var e in validationErrors[f]) {
				var err = validationErrors[f][e];
				if (err.type.name == "wrongWindowId") {
					this.acquireWindowId();
					return;
				}
			}								
	},
	
	/**
	 * acquire a new window id for the first time or if the current window id is no longer valid.
	 */
	acquireWindowId: function() {
		if (this.exiting)
			return;
		var controller = this;
		
	
		nx.resources.windows.register(this.desc.workbook, this.desc.name, 
			function(response){
				controller.windowId = response.id;
				nx.resources.windows.getWindowsForSheet(controller.desc.workbook, controller.desc.name, function(windows) {
					for(var s in windows)
						if (windows[s].windowId.id != controller.windowId)
							controller.forAllSheets(Sheet.prototype.addWindow, windows[s]);
				});
				controller.pollEvents();
			},
			function(status) {
				setTimeout(function(){controller.acquireWindowId();}, 1000);
			});
	},
	
	/**
	 * called to terminate the current window (usually when the user quit the page)
	 */
	terminateWindow: function() {
		nx.resources.windows.terminate(this.windowId);
	},
	
	forAllSheets: function(f, params) {
		var v = [];
		for(var i = 1; i < arguments.length; ++i)
			v.push(arguments[i]);
		for(var s in this.sheets) {
			f.apply(this.sheets[s], v);
		}
	},
	
	addWindow: function(windowInfo) {
		var wid = windowInfo.windowId.id;
		$("#users").append("<option id='" + wid + "'>" + windowInfo.username + "</option>");
		$("#users #count").text("Other users (" + ($("#users option").length - 1) + ")");
	},
	
	removeWindow: function(windowInfo) {
		var wid = windowInfo.windowId.id;
		$("#users #" + wid).remove();
		$("#users #count").text("Other users (" + ($("#users option").length - 1) + ")");
	},
	
	processEvents: function(events) {
		if (!events || events.length == 0) 
			return;
		
		if (events.errors) {
			this.processEventErrors(events.errors);
			return;
		}
		
		var evBySheet = {};
		for(var e in events) {
			var ev = events[e];
			if (ev.type == "windowCreated") {
				this.addWindow(ev.windowInfo);
				this.forAllSheets(Sheet.prototype.addWindow, ev.windowInfo);
				continue;
			} else if (ev.type == "windowTerminated") {
				this.removeWindow(ev.windowInfo);
				this.forAllSheets(Sheet.prototype.removeWindow, ev.windowInfo);
				continue;
			} else if (ev.type == "windowUndoChanged") {
				$(".toolbar .ToolIcon_undo").toggleClass("disabled", !ev.windowInfo.undoEnabled);
				$(".toolbar .ToolIcon_redo").toggleClass("disabled", !ev.windowInfo.redoEnabled);
				continue;
			}
			var evs = evBySheet[ev.sheetName];
			if (!evs) {
				evs = evBySheet[ev.sheetName] = [];
			}
			evs.push(ev);
		}
		for(var s in evBySheet)
			this.sheets[s].processEvents(evBySheet[s]);
		this.menuStatus();
	},
		
	
	//poll for events
	pollEvents: function() {
		var controller = this;
		nx.resources.events.poll(controller.windowId, 
				function(response) {
					try{
						controller.processEvents(response);
					} catch(e){
					}
					controller.pollEvents();
				}, 
				function(status) {
					//TODO check for error
					setTimeout(function(){controller.acquireWindowId();}, 1000);
				});
	},
	
	
	
	/**
	 * replicates the value of the first cell in the selection to the rest
	 */
	replicate: function() {
		nx.resources.cells.replicate(this.desc.workbook, this.activeSheet.selection.start.ref(true), this.activeSheet.selection.ref());
	},
	
	
	css: function(style, group) {
		var isSet = this.activeSheet.selection.start.hasCss(style);
		
		nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), style, isSet ? "clear" : "add");
		this.activeSheet.selectionContent.focus();
	},
	
	clearCss: function(group) {
		if (group == null)
			nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), null, "set");
		else
			nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), group, "clear");
		this.activeSheet.selectionContent.focus();
	},
	
	/**
	 * receives an object with the place where border has to be set. The "h" property contains the setting for the edges left and right, "v" for top and bottom.
	 * A setting is expressed as an array of any of "f" (first), "m" (middle), "l" (last) 
	 *  
	 */
	borders: function(borderStyle) {
		var updates = this.activeSheet.selection.borders(borderStyle || {h:['f','m','l'], v:['f','m','l']});
		for(var u in updates) {
			nx.resources.cells.applyStyle(this.desc.workbook, updates[u].ref, updates[u].style, borderStyle ? "add" : "clear");
		}
		this.activeSheet.selectionContent.focus();
	},
	
		

	/**
	 * called after a paste operation
	 */
	cbPaste: function(fromMenu) {
		if (this.cutSource) {
			//cut & paste
			nx.resources.cells.move(this.desc.workbook, this.cutSource, this.activeSheet.selection.start.ref(true));
			this.cutSource = null;
			return;
		}
		//copy & paste
		if (fromMenu) {
			if (this.copyContent != null)
				nx.resources.cells.paste(this.desc.workbook, this.copySource ? this.copySource : this.activeSheet.selection.start.ref(true), this.activeSheet.selection.start.ref(true),  this.copyContent);
		} else {
			var content = this.copyContent != null ? this.copyContent : this.activeSheet.selectionContent.val();
			nx.resources.cells.paste(this.desc.workbook, this.copySource ? this.copySource : this.activeSheet.selection.start.ref(true), this.activeSheet.selection.start.ref(true), content);
			if (this.copyContent != this.activeSheet.selectionContent.val()) {
				//this is a paste coming from outside
				this.copyContent = null;
				this.copySource = null;
			}
			this.activeSheet.selectionContent.select();
		}
	},
			
	cbCopy: function(fromMenu) {
		this.copySource=this.activeSheet.selection.start.ref(true);
		this.copyContent=this.activeSheet.selectionContent.val();
		this.cutSource=null;
	},

	cbCut: function(fromMenu) {
		this.cutSource=this.activeSheet.selection.ref(true);
		//mark cut area
	},
	
	toggleTreeView: function() {
		this.activeSheet.toggleTreeView();
	},
	
	/**
	 * called when the container of a sheet was scrolled
	 */
	onScroll: function(sheet) {
		if (sheet.desc.name == this.desc.name) {//synchronize summary sheet with main sheet
			this.sheet(this.desc.name + ".summary").syncScroll(sheet);
		}
	},
		
	/**
	 * called when a column was resized
	 */
	onResizeColumns: function(sheet) {
		if (sheet.desc.name = this.desc.name) {//synchronize summary sheet with main sheet
			this.sheet(this.desc.name + ".summary").syncColumnSizes(sheet);
		}
	},
	
	allSheetsLoaded: function() {
		for(var s in this.sheets)
			if (!this.sheets[s].loaded)
				return false;
		return true;
	},
	
	onAllFramesLoaded:function() {
		if (this.windowId)
			return;
		this.acquireWindowId();
		
		this.sheet(this.desc.name + ".summary").container.nxtable("synchronize", this.sheet(this.desc.name).container);
//		this.sheet(this.desc.name).scrollLeft(260);
//		this.sheet(this.desc.name).scrollLeft(0);
//		this.sheet(this.desc.name).scrollLeft(160);
	},
	onFrameLoaded: function(sheet) {
		if (this.allSheetsLoaded()) {
			this.onAllFramesLoaded();
		}
		
		if (this.activeSheet != sheet)
			$(".cellsDiv", sheet.container).addClass("no-overflow");
	},
	
	info: function(msg){
		$("#message").toggle(msg);
		$("#message").text(msg);
	},
	
	print: function() {
		window.open(this.desc.context + "/rest/sheets/" + this.desc.workbook + "/" + this.desc.name + "/pdf", "_blank");
	},
	
	sort: function() {
		var sortSpec = "+" + columnLabel(this.sheet(this.desc.name).selection.start.col);
		if (this.lastSortSpec == sortSpec)
			sortSpec = "-" + columnLabel(this.sheet(this.desc.name).selection.start.col);
		this.lastSortSpec = sortSpec;
		nx.resources.sheets.sort(this.desc.workbook, this.desc.name, sortSpec);
	},
	
	
	/**
	 * popup find dialog and selects the first cell matching.
	 */
	dlgDisconnected: function() {
		var that = this;
		$.nxdialog("disconnected", {
			height:250,
			closable:false,
			buttons: {
				Login: function(){
					$(this).dialog('close');
					window.location.reload();
				}
			}
		});
	},
	
	dlgFind: function() {
		var that = this;
		$("#find #findMessage").text("");
		$.nxdialog("find", {
			modal: false,
			height:200,
			buttons: {
				Search: function() {
					var searchFormula = "=A1=value(\"" + $("#searchText", this).val() + "\")";
					var lastFindRef = that.sheet(that.desc.name).selection.start.ref(false);
					if (lastFindRef == "A1")
						lastFindRef = null;
					nx.resources.cells.find(that.desc.workbook, that.desc.name, lastFindRef, searchFormula, 
						function(refString) {
							if (refString == null) {
								$("#find #findMessage").text("Not found");
								return;
							}
							$("#find #findMessage").text("");
							var ref = parseCellReference(refString);
							//XXX:page works correctly only without filter!
							var page = parseInt(ref.row / nx.app.pageSize);
							var sheet = that.sheet(that.desc.name);
							if (page != sheet.pageNo) {
								sheet.selection.start.row = sheet.selection.end.row = ref.row;
								sheet.selection.start.col= sheet.selection.end.col = ref.col;
								sheet.viewPage(page);
							} else {
								sheet.selectionRange(ref.row, ref.col, ref.row, ref.col);
							}
							
						}		
					);
					
				}
			}
		});
		
	},
	
	dlgStyles: function() {
		var that = this;
		$.nxdialog("styles", {
			modal: false,
			height:200,
			buttons: {
				Apply: function() {
					var $dlg = $(this);
					var styles = $dlg.find("#selectedStyles").val();
					
					nx.resources.cells.applyStyle(that.desc.workbook, that.activeSheet.selection.ref(true), styles, "set");
				}
			}
		});
		
	},
	
	dlgEditAliases: function(){
		var that = this;
		var defs = "";
		var x = 0;
		for(var ref in this.activeSheet.aliases) {
			var alias = this.activeSheet.aliases[ref];
			defs += "<label>" + alias + "</label> ";
			defs += "<input type='text' id='alias" + x + "' value='" + ref + "'>"; 
			defs += "<a href='#' onclick='nx.app.setAlias(\"" + alias + "\",$(\"#alias" + x + "\").val())'>Save</a> "; 
			defs += "<a href='#' onclick='nx.app.deleteAlias(\"" + alias + "\")'>Delete</a> "; 
			defs += "<br>";
			++x;
		}
		$("#aliases #aliasDefinitions").html(defs);
		$.nxdialog("aliases", {
			modal: true,
			height:250,
			closable:false,
			buttons: {
				Done: function() {	
					$(this).dialog('close');
				}
			}
		});
	},
	
	dlgChart: function(chartId, chart){
		var that = this;
		$("#chart #chartId").val(chartId != null ? chartId : -1);
		if (chart) {
			$("#chart #chartArea").val(chart.areaReference);
			$("#chart #chartType").val(chart.type);
			$("#chart #chartTitle").val(chart.title.text);
		} else {
			$("#chart #chartArea").val(this.activeSheet.selection.ref(false));
		}
		$.nxdialog("chart", {
			modal: true,
			height:250,
			buttons: {
				Save: function() {	
					var title = $("#chart #chartTitle").val();
					var areaRef = $("#chart #chartArea").val();
					var type = $("#chart #chartType").val();
					var id = $("#chart #chartId").val();
					if (id < 0){
						nx.resources.charts.add(that.desc.workbook, that.activeSheet.desc.name, areaRef, title, type, 
							function() {
								that.activeSheet.reload();
							}
						);
					} else {
						nx.resources.charts.set(that.desc.workbook, that.activeSheet.desc.name, id, areaRef, title, type, 
								function() {
									that.activeSheet.reload();
								}
							);
					}
					$(this).dialog('close');
				}
			}
		});
	},
	
	deleteAlias:function(alias) {
		if (!confirm("Are you sure you want to delete the alias '" + alias + "'?"))
			return;
		nx.resources.sheets.deleteAlias(this.activeSheet.desc.workbook, this.activeSheet.desc.name, alias);
	},
	
	setAlias: function(alias, ref){
		nx.resources.sheets.setAlias(this.activeSheet.desc.workbook, this.activeSheet.desc.name, alias, ref);
	},
	
	undo: function() {
		nx.resources.windows.undo(this.windowId);
	},
	
	redo: function() {
		nx.resources.windows.redo(this.windowId);
	},
	
	toggleFilter: function(withFormula) {
		this.activeSheet.toggleFilter(withFormula);
		this.menuStatus();
	},
	
	dlgPrivateNotes: function(){
		$(".privateCellsDiv").dialog("open");
	},
	
	dlgAddFormatter: function(){
		var that = this;
		$("#addFormatter #formatterSourceWorkbook").val(this.activeSheet.desc.workbook);
		
		$.nxdialog("addFormatter", {
			modal: true,
			height:250,
			width: 350,
			buttons: {
				Apply: function() {
					var $dlg = $(this);
					var name = $("#addFormatter #formatterName").val();
					var sourceWorkbook = $("#addFormatter #formatterSourceWorkbook").val();
					var nameRef = $("#addFormatter #formatterNameRange").val();
					var valueRef = $("#addFormatter #formatterValueRange").val();					
					
					nx.resources.formatters.setFormatter(that.desc.workbook, name, sourceWorkbook, nameRef, valueRef);
					//TODO should refresh the formatters list
					$(this).dialog('close');
				}
			}
		});
	},
	
	menuStatus: function() {
		var that = this;
		setTimeout(function() {
			if (!that.activeSheet || !that.activeSheet.selection)
				return;
			//styling - TODO do for entire selection
			$(".toolbar .ToolIcon_bold").toggleClass("active", that.activeSheet.selection.start.hasCss("b"));
			$(".toolbar .ToolIcon_italic").toggleClass("active", that.activeSheet.selection.start.hasCss("i"));
			$(".toolbar .ToolIcon_underline").toggleClass("active", that.activeSheet.selection.start.hasCss("u"));
			$(".toolbar .ToolIcon_strikethrough").toggleClass("active", that.activeSheet.selection.start.hasCss("s"));
	
			$(".toolbar .ToolIcon_alignleft").toggleClass("active", that.activeSheet.selection.start.hasCss("a-l"));
			$(".toolbar .ToolIcon_aligncenter").toggleClass("active", that.activeSheet.selection.start.hasCss("a-c"));
			$(".toolbar .ToolIcon_alignright").toggleClass("active", that.activeSheet.selection.start.hasCss("a-r"));
			$(".toolbar .ToolIcon_alignjustify").toggleClass("active", that.activeSheet.selection.start.hasCss("a-j"));
			$(".toolbar .ToolIcon_wordwrap").toggleClass("active", that.activeSheet.selection.start.hasCss("wp"));
			//$(".toolbar .ToolIcon_fontSize").text(that.activeSheet.selection.start.$td.css("font-size"));
			
			//filters
			$(".toolbar .ToolIcon_filter").toggleClass("active", that.activeSheet.filter != null && !that.activeSheet.filterFormula);
			$(".toolbar .ToolIcon_filterFormula").toggleClass("active", that.activeSheet.filter != null && that.activeSheet.filterFormula);
			
			//structural
			$(".toolbar .ToolIcon_mergecells").toggleClass("disabled", that.activeSheet.filter != null);	
			$(".toolbar .ToolIcon_insertrow").toggleClass("disabled", that.activeSheet.filter != null);	
			$(".toolbar .ToolIcon_autoinsertrow").toggleClass("disabled", that.activeSheet.filter != null);	
			$(".toolbar .ToolIcon_deleterow").toggleClass("disabled", that.activeSheet.filter != null);	
			$(".toolbar .ToolIcon_insertcol").toggleClass("disabled", that.activeSheet.filter != null);	
			$(".toolbar .ToolIcon_deletecol").toggleClass("disabled", that.activeSheet.filter != null);	
			
			var selectedStyles = that.activeSheet.selection.css(); 
			$("#styles #selectedStyles").val(selectedStyles.css);
			$("#styles #selectedStyles").toggleClass("partial", selectedStyles.partial);
			
			$(".toolbar .ToolIcon_privateNotes").toggleClass("disabled", $(".privateCellsDiv").dialog("isOpen"));	
		}, 50);

	}
	
};