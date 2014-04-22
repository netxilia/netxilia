/**
 * this class represents an entire sheet. Data is lazily loaded from the underlying html table.
 * @param table
 * @return
 */
function Sheet() {	
}

Sheet.prototype = {
	container: null, //this is the div in the main window
	cellContainer: null, //this is the div in the cell iframe
	loaded: false, //becomes true when the iframe containing the cells finished loading
	table: null,
	fixedRowsTable: null,
	selection: null,
	selectionStartTd: null, //the TD where the user clicked when he starts selecting more cells
	sheet : null,
	selector: null,		
	replicator : null,
	mouseDown: false,
	replicatorDown: false,
	hasFormulaCells: false,
	
	captureSelection: null,
	
	windows: null, //these are the other windows editing the same sheet

	resizedCol: -1,
	resizingCol: false,
	editingContext: null,

	
	treeView: null,
	filter: null,
	pageNo: 0,	
	pageCount : 1,
	
	fixedRows: 0,
	fixedCols: 0,
	desc: null,
	rows: null,

	mapTrToRow: null,
	mapRowToTr: null,
	minRow: 0,
	maxRow: 0,
	//these are the indices of non-hidden TR
	firstTr: 0,
	lastTr: 0,
	
	aliases: null,
	lastChangeTime: 0,
	waitForKeypress: false,
	
	
	columnCount: function() {
		//return this.cols.length;
		return this.colIndex($(this.rows[0]).children().length, true);
	},
		
	/**
	 * 
	 * @param row
	 * @return the rowIndex in the sheet's table by offseting with the hidden rows
	 */
	rowIndex: function(row, trToRow) {
		var ret = trToRow ? this.mapTrToRow[row] : this.mapRowToTr[row];
		//if (ret == null)
		//	ret = trToRow ? this.mapTrToRow[1] : 1;
		return ret;
	},
	
	colIndex: function(col, tdToCol) {
		return tdToCol ? col - 1 : col + 1;
	},
	
	
	/**
	 * rows and cols are 0-based
	 */
	cell: function (p1, p2) {		
		//TODO need to handle fixed rows and cells also
		//this.rows, this.table contain only non-fixed rows 
		var c = p2, r = p1;
		if (p2 == null) {
			c = p1.col;
			r = p1.row;
		}
		
		var trId = this.rowIndex(r);
		if (trId == null)
			trId = this.fixedRows + 1;
		//r = bind(r, 0, this.rows.length - 1);
		var tr = this.rows[trId];
		var td = $(tr).tdAtIndex(this.colIndex(c));
		return new Cell(this, r, c, td);
	},
	
	cellFromTd: function (td) {
		var tr = td.parentNode;
		var tdIndex = $(td).getNonColSpanIndex();
		return new Cell(this, this.rowIndex(tr.rowIndex, true), this.colIndex(tdIndex, true), td);
	},
	
	showRows:function(show) {
		if (show) {
			$(".collapsed", this.table).removeClass("collapsed");
		} else {
			$("tr", this.table).addClass("collapsed");
		}
	},
	
	showRow:function(row, show) {
		var r = this.rowIndex(row);//nth-child is 1-based - but it seems tha jquery does not take hidden !?
		
		if (show) {
			$("tr:nth(" + r + ")", this.table).removeClass("collapsed");
		} else {
			$("tr:nth(" + r + ")", this.table).addClass("collapsed");
		}
	},
	
	setRowNums: function(startRow, startTr) {
		var row = startRow;
		for(var r = startTr; r < this.rows.length; ++r, ++row) {
			$(this.rows[r]).find("th").text(row + 1);
		}
	},
	
	insertRow: function(row) {
		var s='<tr>';
		s += "<th>" + (row + 1) + "</th>";
		var colCount = this.columnCount();
		for(var i = 0; i< colCount; ++i)
			s += "<td></td>";
		s += "</tr>";

		var tr = this.rowIndex(row);
		$(this.rows[tr]).before(s);
		this.setRowNums(row + 1, tr);
		this.initTable();
	},
		
	deleteRow: function(row) {
		var tr = this.rowIndex(row);//nth-child is 1-based  - but it seems tha jquery does not take hidden !?
		$(this.rows[tr]).remove();
		this.setRowNums(row - 1, tr);
		this.initTable();
	},
	
	insertColumn: function(col) {
		this.viewPage(this.pageNo);
		var c = this.colIndex(col) + 1;//nth-child is 1-based
		//!! because the first column in the fixedRowsTable has a TH not a column header, use -1 on indexes
		
		this.rows.find('td:nth-child('+c+')').before("<td></td>");
		this.fixedRowsTable.find('tr.labels th:nth-child('+c+')').before("<th style='width:80px'></th>");
		this.fixedRowsTable.find('tr.aliases th:nth-child('+c+')').before("<th><input type='text' value=''></th>");

		this.container.nxtable("refreshTotalWidth");
		this.rebuildColumnHeaders(c - 1);
	},
	
	deleteColumn: function(col) {
		this.viewPage(this.pageNo);
		var c = this.colIndex(col) + 1;//nth-child is 1-based		
		//!! because the first column in the fixedRowsTable has a TH not a column header, use -1 on indexes
			
		var $td = $('.cells .cw td:eq('+ c +')', this.cellContainer);
		this.container.nxtable("refreshTotalWidth");

		$('tr td:nth-child('+c+')', this.table).remove();		
		$('tr th:nth-child('+(c-1)+')', this.fixedRowsTable).remove();
		this.rebuildColumnHeaders(c - 1);
	},
	
	resizeColumn: function(col, w) {				
		var tdIndex = this.colIndex(col);
		var $col = $('tr:first th:eq('+tdIndex+')', this.fixedRowsTable);			
		var $td = $('tr.cw td:eq('+ (tdIndex-1) +')', this.table);
		$td.width(w);
		$col.width(w);
		this.container.nxtable("refreshTotalWidth");
		nx.app.onResizeColumns(this);
	},
	
	rebuildColumnHeaders: function(start) {
		for(var c = start; c < this.columnCount(); ++c){
			var label = this.columnLabel(this.colIndex(c - 1, true));
			$('tr.labels th:nth-child('+c+')', this.fixedRowsTable).text(label);
			$('tr.aliases th:nth-child('+c+') input', this.fixedRowsTable).attr("id", "alias-" + label);
		}
		this.aliasName = $("tr.aliases th.alias input", this.fixedRowsTable);
		this.colAliasNames = $("tr.aliases th input", this.fixedRowsTable);
	},
	
	columnLabel: function(col) {
		//TODO convert to more than one-letter code
		return col != null ? String.fromCharCode(65 + col) : "";
	},
	
	rowLabel: function(row) {
		return row != null ? "" + (row + 1) : "";
	},
	
	cellRef : function(row, col, fixedCol, fixedRow, addSheetName) {		
		return (addSheetName ? this.desc.name + "!" : "") + (fixedCol ? "$" : "") + this.columnLabel(col) + (fixedRow ? "$" : "") + (row+1);
	},
	
	areaRef: function (startRow, startCol, endRow, endCol, addSheetName) {
		return (addSheetName ? this.desc.name + "!" : "") + this.columnLabel(startCol)
			+ this.rowLabel(startRow) + ":" + this.columnLabel(endCol) + this.rowLabel(endRow);
	},
	
	setSpans: function(areas, merged){
		for(var a in areas){
			var area = parseAreaReference(areas[a]);
			for(var r = area.topLeft.row; r <= area.bottomRight.row; ++r)
				for(var c = area.topLeft.col; c <= area.bottomRight.col; ++c){
					var topLeftCell = r == area.topLeft.row && c == area.topLeft.col;
					var rowSpan = merged ? (topLeftCell ? area.bottomRight.row - area.topLeft.row + 1: -1) : 1;
					var colSpan = merged ? (topLeftCell ? area.bottomRight.col - area.topLeft.col + 1: -1) : 1;
					this.cell(r, c).span(rowSpan, colSpan);				
				}
		}
	},
	/******* charts ***********/
	chartMoved: function(chartDiv){
		var $chart = $(chartDiv);
		var id = $chart.attr("id").substring(5);
		nx.resources.charts.move(this.desc.workbook, this.desc.name, id, parseInt($chart.css("left")), parseInt($chart.css("top")), $chart.width(), $chart.height());
	},
	
	chartRefresh: function(id){
		var swf = $("#chartFlash" + id, this.cellContainer);
		swf[0].reload(nx.app.desc.context + "/rest/charts/" + this.desc.workbook + "/" + this.desc.name + "/" + id);
	},

	chartDelete: function(id){
		var that = this;
		nx.resources.charts.del(this.desc.workbook, this.desc.name, id, function() {
			that.reload();
		});
	},

	chartSettings: function(id){
		nx.app.setActiveSheet(this);
		nx.app.dlgChart(id, this.charts[id]);
	},
	/*********** interaction *************/
	editMode: function(){
		return this.editingContext.editor != null;
	},
	
	moveMode: function(sh, key, f){
		var that = this;
		sh.add(key, function(){
			if (that.editMode() && !that.editingContext.defaultEditor && that.captureSelection == null)
				return true;
			f();
			return false;
		}, true);
	},
	
	moveSelection: function(dc, dr, ignoreEditorDefault) {
		//CHECK HERE
		if (!ignoreEditorDefault && this.captureSelection){
			this.captureSelection.move(dc, dr);
			this.editingContext.setCaptureSelection(this.captureSelection);
			return;
		}
		if (!ignoreEditorDefault && !this.editingContext.defaultEditor)
			return;
		this.beforeSelectionChanged();
		this.editingContext.hide();
		this.selection.move(dc, dr);
		this.selectionChanged();
		
		this.container.nxtable("makeVisible", this.rowIndex(this.selection.start.row), 
				this.colIndex(this.selection.start.col));		
	},
	
	moveSelectionToLimits: function(firstRow, firstCol, lastRow, lastCol) {
		this.editingContext.hide();
		var r = firstRow ? this.minRow : this.selection.start.row;
		r = lastRow ? this.maxRow : r;
		var c = firstCol ? 0 : this.selection.start.col;
		c = lastCol ? this.columnCount() - 1 : c;
		
		this.selectionRange(r, c, r, c);
		this.container.nxtable("makeVisible", this.rowIndex(this.selection.start.row), 
				this.colIndex(this.selection.start.col));		
	},
	
	rowFromPosition: function(y){
		for(var r = this.minRow + 1; r <= this.maxRow; ++r){
			var rt = $(this.rows[this.rowIndex(r, false)]).offset().top;
			if (rt > y)
				return r - 1;
		}
		return this.maxRow;
	},
	
	moveSelectionPage: function(dir) {
		var cellsDiv = $(".cellsDiv", this.container);
		var div = cellsDiv.scrollBounds();
		var r = this.rowFromPosition(dir == "down" ? Math.min(div.b, this.table.height() - div.h) : div.t - div.h);
		var rt = $(this.rows[this.rowIndex(r, false)]).offset().top;
		this.selectionRange(r, this.selection.start.col, r, this.selection.start.col);
		cellsDiv.scrollTop(rt);
	},	
	
	selectionRange: function(startRow, startCol, endRow, endCol, fullRow, fullCol) {
		this.beforeSelectionChanged();
		this.selection.setRange(this.cell(startRow, startCol), this.cell(endRow, endCol), false, fullRow, fullCol);
		this.selectionChanged();
	},
	
	selectionRangeTd: function(startTd, endTd, withReplicator) {
		if (this.captureSelection){
			this.captureSelection.setRange(startTd ? this.cellFromTd(startTd) : null, // 
					endTd ? this.cellFromTd(endTd) : null);
			this.editingContext.setCaptureSelection(this.captureSelection);
			return;
		}
		
		this.beforeSelectionChanged();
		this.selection.setRange(startTd ? this.cellFromTd(startTd) : null, // 
									endTd ? this.cellFromTd(endTd) : null, withReplicator);
		this.selectionChanged();
	},
	
	positionElementOnCell: function(elem, cell) {
		var parentPos = this.table.parent().offset();
		var pos = cell.$td.offset();
		pos.top -= parentPos.top;
		pos.left -= parentPos.left;
		var w = cell.$td.innerWidth(), h = cell.$td.innerHeight();
		elem.css({top:pos.top, left: pos.left, width:w, height:h});
	},

	placeSelectors: function() {
		var startCell = this.selection.start.$td; 
		var parentPos = this.table.parent().offset();
		parentPos.top -= this.table.parent().scrollTop();
		parentPos.left -= this.table.parent().scrollLeft();
		var pos = startCell.offset();
		pos.top -= parentPos.top;
		pos.left -= parentPos.left;
		
		var w = 60, h=16;
		//because of chrome that behaves strangely when cells are hidden
		if (startCell.css("display") != "none") {
			w = startCell.innerWidth();
			h = startCell.innerHeight();
		}
		
		var endCell = this.selection.end.$td;
		var epos = pos;
		var ew = w, eh = h;
		if (startCell != endCell) {
			epos = endCell.offset();
			epos.top -= parentPos.top;
			epos.left -= parentPos.left;
			if (endCell.css("display") != "none") {
				ew = endCell.outerWidth();
				eh = endCell.outerHeight();
			}
		}
		
		this.selector.css({top:pos.top , left: pos.left, width:w, height:h});
		this.selectionContent.css({top:pos.top, left: pos.left}); 
		this.selectedArea.css({top:pos.top, left: pos.left, width:epos.left - pos.left + ew, height:epos.top - pos.top + eh});
		
		if (this.selection.start.formula()) {
			this.formulaTip.css({top: pos.top + 2 * h, left: pos.left + 40});			
		}
			
		if (this.selection.replicated) {
			this.replicator.css({top:epos.top + eh, left: epos.left + ew});
		} else {
			this.replicator.css({top:pos.top + h, left: pos.left + w});
		}
	},
	
	beforeSelectionChanged : function () {
		if (this.selection.start) {
			//value from editor -> cell
			if (this.editingContext.hasValueChanged()) {
				var value = this.editingContext.value();
				if (this.editingContext.defaultEditor) {
					//check for smart-edit
					var crtFormula = this.selection.start.formula();
					if (crtFormula){
						var re = /([\(+\-*%=\/]+\s*)\(\d+\)/g;
						if (crtFormula.match(re)) {
							value = crtFormula.replace(re, "$1(" + value + ")");
						}
					}
				}
				this.lastChangeTime = new Date().getTime();
				nx.resources.cells.setValue(this.desc.workbook, this.selection.start.ref(true), value);
			}
		}
	},
	
	
	/**
	 * called anytime the selection changed.
	 * TODO use events
	 */
	selectionChanged: function() {
		
		this.selectionContent.val(this.selection.editableValue());
		this.editingContext.hide();
		this.markFormulaCells(null);

		if (this.selection.start.formula()) {
			this.formulaTip.html(this.selection.start.formula());
			this.formulaTip.show();
		} else {
			this.formulaTip.hide();
		}

		this.placeSelectors();
		this.focusSelectionContent();
		
		//select row & column headers
		$(".sel", this.fixedRowsTable).removeClass("sel");
		$(".sel", this.table).removeClass("sel");
		
		if (!this.selection.fullCol) {
			for(var r = this.selection.start.row; r <= this.selection.end.row; ++r)
				$("tr:nth(" + (this.rowIndex(r)) + ") th", this.table).addClass("sel");
		}
		if (!this.selection.fullRow) {
			for(var c = this.selection.start.col; c <= this.selection.end.col; ++c)
				$("tr.labels th:nth(" + (this.colIndex(c)) + ")", this.fixedRowsTable).addClass("sel");
		}
		if(!isEmptyObject(this.windows))
			nx.resources.windows.notifySelection(nx.app.windowId, this.selection.ref(true));
		
		var ref = this.selection.ref(false);
		this.aliasRef.text(ref);
		this.aliasName.val(this.aliases[ref] || "");
		
		nx.app.menuStatus();
		
	},
	
	/**
	 * called to display the selection made in another window opened to the same sheet
	 */
	markSelection: function(areaRef, windowId) {
		var s = this.windows[windowId.id];
		if (!s)
			return;
		var ref = parseAreaReference(areaRef);
		if (!ref || !ref.topLeft)
			return;
		this.positionElementOnCell(s.selector, this.cell(ref.topLeft));
	},
	
	/**
	 * display the editor for the current selection. and mark the areas in the formula
	 */
	showEditor: function(){
		this.editingContext.edit(this.selection.start);
		this.markFormulaCells(this.selection.start.formula());
	},
	
	toggleMarkArea: function(ref, css) {
		this.markArea(this.hasMarkedCells ? null : ref, css);
	},
	
	markArea: function(ref, css) {
		if (ref){
			if (ref.topLeft.sheet == null || ref.topLeft.sheet == this.desc.name) {
				for(var r = ref.topLeft.row; r <= ref.bottomRight.row; ++r)
					for(var c = ref.topLeft.col; c <= ref.bottomRight.col; ++c) {
						var cell = this.cell(r, c);
						cell.$td.addClass(css);
					}
			}
			this.hasMarkedCells = true;
		} else {
			this.table.find("td." + css).removeClass(css);
			this.hasMarkedCells = false;
		}
	},
	
	markFormulaCells: function(formula) {
		if(this.hasMarkedCells) {
			this.markArea(null, "formula");
		}
		if (!formula || formula.length < 1 || formula.charAt(0) != "=") {
			this.captureSelection = null;
			return;
		}
				
		var refs = findReferencesInFormula(formula);
		for(var i in refs) {
			var ref = refs[i];
			this.markArea(refs[i], "formula");
		}
	},
	
	addWindow: function(windowInfo) {
		var wid = windowInfo.windowId.id;
		var s = {id: wid, username: windowInfo.username};
		s.selector=$("<div class='selector-other'></div>").appendTo(this.cellContainer);
		this.windows[s.id]=s;
	},
	
	removeWindow: function(windowInfo) {
		var wid = windowInfo.windowId.id;
		var s = this.windows[wid];
		if (!s)
			return;
		s.selector.remove();
		delete this.windows[s.id];
	},
	
	clearCells: function() {
		nx.resources.cells.setValue(this.desc.workbook, this.selection.ref(true), "");		
	},
	cancelEdit: function(){
		this.editingContext.hide();
		this.markFormulaCells(null);
	},
	
	borders: function(borderStyle) {
		if (borderStyle == null) {
			//clear all cells
			nx.resources.cells.applyStyle(this.desc.workbook, 
					this.areaRef(Math.max(this.selection.start.row - 1, 0), Math.max(this.selection.start.col - 1, 0), this.selection.end.row, this.selection.end.col, true),
							"br bb bt bl", "clear");
			return;
		}
		var updates = this.selection.borders(borderStyle);
		for(var u in updates) {
			nx.resources.cells.applyStyle(this.desc.workbook, updates[u].ref, updates[u].style, "add");
		}
		this.focusSelectionContent();
	},
	
	checkAutoInsertRow:function() {
		if (nx.app.autoInsertRow)
			this.moveSelection(0, 1, true);
		
	},

	buildTreeView: function(refresh) {
		var controller = this;
		if (refresh)
			$(".tree", this.table).removeClass("tree");
		this.treeView = new TreeView();
		 for(var r = this.minRow; r <= this.maxRow; ++r) {
			 var level = -1;
			 for(var c = 0; c < this.columnCount(); ++c) {
				 var v = this.cell(r, c).valueAsString();
				 if (v != null && v.length > 0) {
					 level = c;
					 break;
				 }
			 }
			 if (level >= 0)
				 this.treeView.node(level, r);
		 }
		 this.treeView.walk(function(n) {
			 if (n.children.length > 0)
				 controller.cell(n.key, n.level).$td.addClass("tree");
		 });
	},
	
	/**
	 * toggles to tree view
	 */
	toggleTreeView: function() {
		if (!this.treeView) {
			 this.buildTreeView(false);
		} else	 {
			 //clear all nodes
			this.showRows(true);
			$(".tree", this.table).removeClass("tree");
			 this.treeView = null;
		}
	},
	
	toggleTreeNode: function(td) {
		var controller = this;
		var cell = this.cellFromTd(td);
		var n = this.treeView.nodes[cell.row];
		var expand = cell.$td.hasClass("collapsed");
		if (expand) {
			cell.$td.removeClass("collapsed");
		} else {
			cell.$td.addClass("collapsed");
		}
		this.treeView.walk(function(nc) {
			controller.showRow(nc.key, expand);
		}, n);
	},
		
	/**
	 * toggle the filter-by-cell.
	 */
	toggleFilter: function(useFormula) {
		this.filterFormula = useFormula;
		if (this.filter)
			this.filter = null;
		else
			this.filter = useFormula ? this.selection.start.formula() : "=" + this.cellRef(0, this.selection.start.col) + "=" + this.selection.start.absoluteRef();
		
		this.reload();		
	},
	
	/**
	 * display the column aliases in the input boxes
	 */
	displayAliases: function(){
		//set column aliases
		var colCount = this.columnCount();
		for(var i = 0; i < colCount; ++i){
			var colLabel = this.columnLabel(i);
			$("#alias-" + colLabel, this.fixedRowsTable).val(this.aliases[colLabel + ":" + colLabel] || "");
		}
	},
	
	/**
	 * events
	 */
	processEvents: function(events) {
		var refreshSelection = false;
		var rowsToResize = [];
		
		var t1 = new Date().getTime();
		//this id for cell event
		for(var e in events) {
			var ev = events[e];
			if (ev.type == "cellModified") {
				for(var d in ev.data) {
					var evd = ev.data[d];
					var cell = this.cell(evd.row, evd.column);
					cell.valueAsString(evd.formattedValue || "");
					cell.css(evd.style);
					cell.value(evd.value);
					
					rowsToResize.push(this.rowIndex(cell.row));
				}
			} else if (ev.type == "rowInserted") {
				this.insertRow(ev.row);
				refreshSelection = true;
			} else if (ev.type == "rowDeleted") {
				this.deleteRow(ev.row);
				refreshSelection = true;
			} else if (ev.type == "columnInserted") {
				this.insertColumn(ev.column);
				refreshSelection = true;
			} else if (ev.type == "columnDeleted") {
				this.deleteColumn(ev.column);
				refreshSelection = true;			
			} else if (ev.type == "columnModified") {
				this.resizeColumn(ev.column, ev.width);
				refreshSelection = true;			
			} else if (ev.type == "cellSelected") {
				this.markSelection(ev.selectedArea, ev.windowInfo.windowId);
			} else if (ev.type == "sheetModified") {
				//this.reload();
				this.aliases = $.reverseMap(ev.aliases) || {};
				this.displayAliases();
				
				//set spans spans
				var diff = $.diff(this.spans, ev.spans);
				this.setSpans(diff.deleted, false);
				this.setSpans(diff.added, true);
				
				this.spans = ev.spans;
			}
		}

		if (refreshSelection)
			this.selection.refresh();
		
		this.selectionContent.val(this.selection.editableValue());
		if (nx.app.activeSheet == this)
			this.focusSelectionContent();
		this.placeSelectors();
	
		if (this.treeView)
			this.buildTreeView(true);
		var t2 = new Date().getTime();
		//console.info("event time: " + (t2-t1) + " start since lastChange:" + (t1 - this.lastChangeTime));
	},
	
	focusSelectionContent: function() {		
		this.selectionContent.focus();
		this.selectionContent.select();
	},
	
	
	syncScroll: function(sheet) {
		this.container.nxtable("scrollLeft", sheet.container.nxtable("scrollLeft"));
	},

	syncColumnSizes: function(sheet) {
		var w = sheet.table.width();
		this.table.width(w);
		$('.cells .cw', this.cellContainer).html($('.cells .cw', sheet.cellContainer).html());
	},

	viewPage: function(p, showPageOnly) {
		var that = this;
		$(".pager table tbody tr.crt", this.container).removeClass("crt");
		this.pageNo = p;
		$(".pager table tbody tr:nth(" + this.pageNo + ")", this.container).addClass("crt");
		if (!showPageOnly) {
			var url = this.desc.context + "/rest/sheets/" + this.desc.workbook + "/" + this.desc.name + "?start=" + (this.pageNo* nx.app.pageSize);
			if (this.filter)
				url += "&filter=" + encodeURIComponent(this.filter);
			$.ajax({
				url: url,
				success: function(html){
					that.table.replaceWith(html);
					that.initTable();
					that.initSelection();
					that.handleMouse(true);
				},
				error: function(){
					alert("Could not change the page!");
				}
			});			
		}		
	},
	
	buildPager: function(){
		var $pager = $(".pager", this.container);
		
		if (this.pageCount <= 1){
			this.layout.threeColumn("right", 0);
			return;
		}
		
		var content = "<table style='width:100%;'>"; 
		content += "<thead><tr><th>ROWS</th></tr></thead>";
		
		content += "<tbody>";
		for(var p = 1; p <= this.pageCount; ++p ) {
			content += "<tr><td>";
			content += (nx.app.pageSize * (p - 1) + 1);
			content += "</td></tr>";
		}
		content += "</tbody>";
		content += "</table>";
		$pager.html(content);
		this.layout.threeColumn("right", 60);
	},
	
	reload: function() {		
		this.viewPage(this.pageNo);
	},
	
	
	changeAlias: function(oldAlias, newAlias, ref){
		oldAlias = oldAlias || "";
		newAlias = newAlias || "";
		if (oldAlias != newAlias) {
			if (oldAlias != "")
				nx.resources.sheets.deleteAlias(this.desc.workbook, this.desc.name, oldAlias);
		
			if (newAlias != "")
				nx.resources.sheets.setAlias(this.desc.workbook, this.desc.name, newAlias, ref);
		}
	},
	
	
	/**
	 * handlers for mouse interaction
	 */
	handleMouse:function(tableOnly) {
		var that = this;
		if (tableOnly) {
			$(this.table).mousedown(function(ev) {
				nx.app.setActiveSheet(that);
				//trigger first the blur event that is canceled by "return false" from this function
				if (that.focusElement)
					$(that.focusElement).blur();
				var $td = null;
				if (ev.target.tagName.toLowerCase() == "div" && ev.target.className=="merge") {
					$td = $(ev.target).parent();
				} else if (ev.target.tagName.toLowerCase() == "td") {
					$td = $(ev.target);
				} else if (ev.target.tagName.toLowerCase() == "th") {
					//row headers
					var $th = $(ev.target);
					var r = that.rowIndex($th[0].parentNode.rowIndex, true);
					that.selectionRange(r, 0, r,  that.columnCount() - 1, true, false);
					return false;
				} else if (ev.target.className == "ck-format"){
					$td = $(ev.target).parent();
					var ck = !ev.target.checked;//the checked flag will change after this method
					nx.resources.cells.setValue(that.desc.workbook, that.cellFromTd($td[0]).ref(true), ck ? true : false);
					return true;
				}
				if ($td) {
					if (that.treeView && $td.hasClass("tree") && ev.pageX - $td.offset().left <= 10) {
						that.toggleTreeNode(ev.target);
						return false;
					}
					that.mouseDown = true;
					if (ev.shiftKey) {
						that.selectionRangeTd(that.selectionStartTd, $td[0], false);
					} else {
						that.selectionStartTd = $td[0];
						that.selectionRangeTd($td[0], null);
					}
					if($.browser.safari) {
						//just to take the focus
						//that.editingContext.showDefaultEditor(that.selection.start);
						//that.editingContext.hide();
					}
				}
				
				return false;
			});
			
			$(this.table).mousemove(function(ev) {
				if (ev.target.tagName.toLowerCase() == "td") {
					if (that.replicatorDown)
						that.selectionRangeTd(that.selectionStartTd, ev.target, true);
					else if (that.mouseDown)
						that.selectionRangeTd(that.selectionStartTd, ev.target, false);
				} 
			});
	
			return;
		}
		
			
		this.selector.mousedown(function(ev){
			nx.app.setActiveSheet(that);
			//trigger first the blur event that is canceled by "return false" from this function
			if (that.focusElement)
				$(that.focusElement).blur();
			that.selectionRange(that.selection.start.row, that.selection.start.col, 
					that.selection.end.row, that.selection.end.col);
		});
		
		/** double click puts up directly the edit text */
		this.selector.dblclick(function(ev) {
			that.showEditor();	
		});
		
		//column headers
		$("tr.labels th", this.fixedRowsTable).live("mousemove", function(ev) {
			if (that.resizingCol)
				return;
			
			var $th = $(this);
			var pos = $th.offset();
			var parentPos = that.colResizer.parent().offset();
			var w = $th.outerWidth();
			var d = 0;
			
			if (Math.abs(pos.left - ev.pageX) < 20) { 
				d = 0;
			} else if (Math.abs(pos.left + w - ev.pageX) < 20) {
				d = 1;					
			} else {
				that.colResizer.hide();
				return;
			}
			var h = $th.outerHeight();
			//this is the column that has its right edge moving
			that.resizedCol = this.cellIndex - 2 + d;
			
			that.colResizer.css({left: pos.left + d * w- 5 - parentPos.left, height: h});
			//that.colResizer.draggable('option', 'containment', [0, pos.top - 1, 10000, pos.top + h + 1]);
			that.colResizer.show();
		}).live("mousedown", function(ev) {
			nx.app.setActiveSheet(that);
			//TODO should use full column selectors. ex: C:C
			var c = that.colIndex(this.cellIndex, true);
			that.selectionRange(that.minRow, c, that.maxRow, c, false, true);
		});
		
		this.colResizer.bind("dragstart", function(ev) {
			that.resizingCol = true;
			that.resizerStart = ev.pageX;
		});
		
		this.colResizer.bind("dragstop", function(ev) {	
			var cw = $('.cw td:eq('+ that.resizedCol +')', that.table)[0].offsetWidth;
			nx.resources.columns.modify(that.desc.workbook, that.desc.name, that.resizedCol, cw + (ev.pageX - that.resizerStart));
			that.placeSelectors();
			that.resizedCol = -1;
			that.resizingCol = false;
			that.colResizer.hide();				
		});
		
		this.selector.mousedown(function(ev) {
			that.mouseDown = true;
			return false;
		});
		
		this.replicator.mousedown(function(ev) {
			that.replicatorDown = true;
			return false;
		});
		
		$(this.cellContainer).mouseup(function(ev) {
			if (that.replicatorDown)
				nx.app.replicate();
			that.mouseDown = false;				
			that.replicatorDown = false;
		});
		
		
		$(".pager", this.container).click(function(ev) {
			if (ev.target.tagName.toLowerCase() == "td")
				that.viewPage(ev.target.parentNode.rowIndex - 1);
		});
		
		
		this.aliasName.add(this.colAliasNames).focus(function(){
			that.focusElement = this;
		});
		this.aliasName.blur(function(){
			that.focusElement = null;
			var oldAlias = that.aliases[that.aliasRef.text()];
			var newAlias = $(this).val();
			
			that.changeAlias(oldAlias, newAlias, that.aliasRef.text());
			that.focusSelectionContent();
		});
		this.colAliasNames.blur(function(){
			that.focusElement = null;
			var colLabel = this.id.substring("alias-".length);
			var ref = colLabel + ":" + colLabel;
			var oldAlias = that.aliases[ref];
			var newAlias = $(this).val();
			that.changeAlias(oldAlias, newAlias, ref);
			that.focusSelectionContent();
		});
		
		this.container.bind("nxtablebodyScroll", function(){
			that.placeSelectors();
			nx.app.onScroll(that);
		});
	
	},
	
	/**
	 * handlers for keyboard interaction
	 */
	handleKeyboard: function() {
		var controller = this;
		var sh = new Shortcuts();
		sh.add("ctrl+alt+S", function() {
			nx.app.sort();
		});
		sh.add("ctrl+c", function() {
			nx.app.cbCopy(false);
		}, true);
		sh.add("ctrl+x", function() {
			nx.app.cbCut(false);
		}, true);
		sh.add("ctrl+v", function() {
			setTimeout(function(){nx.app.cbPaste(false);}, 50);
		}, true);
		
		sh.add("ctrl+z", function(){nx.app.undo();});
		sh.add("ctrl+y", function(){nx.app.redo();});
		
		//cell movements
		this.moveMode(sh, "up", function() {controller.moveSelection(0, -1);});
		this.moveMode(sh, "down", function() {controller.moveSelection(0, 1);});
		this.moveMode(sh, "right", function() {controller.moveSelection(1, 0);});
		this.moveMode(sh, "left", function() {controller.moveSelection(-1, 0);});
		
		this.moveMode(sh, "home", function() {controller.moveSelectionToLimits(false, true, false, false);});
		this.moveMode(sh, "end", function() {controller.moveSelectionToLimits(false, false, false, true);});
		this.moveMode(sh, "ctrl+home", function() {controller.moveSelectionToLimits(true, true, false, false);});
		this.moveMode(sh, "ctrl+end", function() {controller.moveSelectionToLimits(false, true, true, false);});
		
		this.moveMode(sh, "pagedown", function() {controller.moveSelectionPage("down");});
		this.moveMode(sh, "pageup", function() {controller.moveSelectionPage("up");});
		
		sh.add("enter", function() {
			if (nx.app.autoInsertRow)
				nx.app.insertRow(true);
			else
				controller.moveSelection(0, 1, true);
		});//same as down
		sh.add("tab", function() {controller.moveSelection(1, 0, true);});//moves right
		
		this.moveMode(sh, "delete", function() { controller.clearCells();});
		sh.add("escape", function() {controller.cancelEdit();});
		sh.add("f2", function() {controller.selector.dblclick();});
		
		sh.add("f7", function() {controller.toggleFilter(false);});
		sh.add("ctrl+f7", function() {controller.toggleFilter(true);});
		sh.add("f5", function() {window.location.reload();});
		
		sh.addDefault(function(ev){
			if (!controller.editMode()) {
				if (ev.which < 32 || ev.ctrlKey)
					return true;
				controller.waitForKeypress = true;
				return;
			}
			return true;
		}, true);
		
		this.shortcuts = sh;
	}, 
			

	/** 
	 * initializing the sheet
	 */
	init: function(desc, container) {
		this.container = container;
		
		this.desc = desc;
		return this;
	},
	
	
	/**
	 * called when the sheet's frame was loaded. it calculates the size of the sheet's table container
	 */
	frameLoaded: function(frmWindow, pageCount, sheetData) {
		var that = this;
		this.layout = $(".threeColumnFixed", this.container).threeColumn();
		
		this.pageNo = 0;
		this.pageCount = pageCount;
		this.buildPager();
		this.viewPage(0, true);
		//alias on the server are alias -> ref. on the client side are store ref->alias
		if (sheetData){
			this.aliases = $.reverseMap(sheetData.aliases) || {};	
			this.charts = sheetData.charts;
			this.spans = sheetData.spans;
		}	
		
		this.windows={};
		this.nxtable = this.container.nxtable();
		
		this.cellContainer = $(".cellsDiv", this.container);		
		this.fixedRowsTable = $(".fixedRows", this.container);
		
		
		this.selector=$("<div id='selector'></div>").appendTo(this.cellContainer);
		this.formulaTip=$("<div id='formulaTip'></div>").appendTo(this.cellContainer);
		this.selectedArea=$("<div id='selectedArea'></div>").appendTo(this.cellContainer);
		this.replicator=$("<div id='replicator'></div>").appendTo(this.cellContainer);	
		this.rowResizer=$("<div id='rowResizer'></div>").appendTo(this.container);
		this.colResizer=$("<div id='colResizer'></div>").appendTo(this.fixedRowsTable.parent());
		this.colResizer.draggable({axis:'x'});
		this.selectionContent=$("<textarea id='selectionContent' autocapitalize='off'></textarea>").appendTo(this.cellContainer);
		this.aliasRef = $("tr.labels th.ref", this.fixedRowsTable);
		this.aliasName = $("tr.aliases th.alias input", this.fixedRowsTable);
		this.colAliasNames = $("tr.aliases th input", this.fixedRowsTable).not("th.alias input");
		
		this.initTable();
		
		this.editingContext = new EditingContext(this);
		this.editingContext.valueChanged = function(value){
			if (!that.captureSelection) {
				that.captureSelection = new CellRange(that);
				that.captureSelection.setRange(that.selection.start);
			}
			that.markFormulaCells(value);
		};
		this.handleMouse(false);
		this.handleMouse(true);
		this.handleKeyboard();		
	
		$(".chart", this.container).draggable({stop: function(){
			that.chartMoved(this);
		}}).resizable({stop: function(){
			that.chartMoved(this);
		}});
		
		this.initSelection();
		
		this.loaded = true;
		//this.displayAliases();
		
		nx.app.onFrameLoaded(this);
	},
	
	initTable: function(){
		var that = this;
		this.table = $(".cells", this.cellContainer);
		this.rows = $("tbody tr", this.table);

		this.mapTrToRow = {};
		this.mapRowToTr = {};
		
		this.minRow = 1000000;
		this.maxRow = 0;
		this.firstTr = 1;
		this.lastTr = this.rows.length - 1;
		
		$("tbody th", this.table).each(function(idx){
			if (idx == 0)//this first hidden row
				return;
			var rowId = parseInt($(this).text()) - 1;
			that.mapTrToRow[idx + that.firstTr - 1] = rowId;
			that.mapRowToTr[rowId] = idx + that.firstTr - 1;
			that.maxRow = Math.max(that.maxRow, rowId);
			that.minRow = Math.min(that.minRow, rowId);
		});
		
	},
	
	initSelection: function(){
		if (this.columnCount() > 0) {
			if (this.selection){
				//already a selection 
				this.selectionRange(this.selection.start.row, this.selection.start.col, this.selection.end.row, this.selection.end.col);
			}
			else {
				this.selection = new CellRange(this);
				this.selectionRangeTd(this.cell(this.rowIndex(1, true),this.colIndex(1, true)).td);
			}
		}
	}
};