/**
 * This plugin manages the horizontal and vertical scroll of a table with fixed columns and rows.
 * Because the browser do not offer this possibility directly, the techniques involve the usage of separate tables
 * for the columns and rows and synchronize then the widths and heights. Using only this technique, when having many rows
 * the synchronization of row heights is time consuming. 
 * This plugin uses a mixed technique: for fixed rows it uses a separate table and for fixed columns uses a technique insired by
 * Google Spreadsheet: the fixed columns are in the main table but the non-fixed columns are displayed or hidden as the user scrolls.
 * The performance problem is fixed but this adds another complication when dealing with colspans (merged cells). 
 */

(function($) {

$.widget("nx.nxtable", {
	_init: function(){
		var that = this, o = $.extend({}, this.defaults, this.options);
		this.fixedRowsTable = $(o.fixedRowsDivSelector, this.element).find("table");
		this.cellsDiv = $(o.cellsDivSelector, this.element);
		this.table = $(o.cellsDivSelector, this.element).find("table");
		this.rows = this.table.find("tr");
		this.horizScroll = $(o.horizontalScrollSelector, this.element);
		this.fixedCols = 1;
		this.firstVisibleCol = this.fixedCols;
		
		this.horizScroll.scroll(function(ev){
			var x = $(this).scrollLeft();
			that.scrollLeft(x);
		});
	},
	
	/**
	 * set the col column's display for all the rows
	 */
	_setColDisplay: function(col, display){
		var crtDisplay = $($(this.rows[0]).tdAtIndex(col)).css("display");
		if (crtDisplay == display)
			return;
		var that = this;
		this.rows.each(function(idx){
			var $td = $($(this).tdAtIndex(col));
			
			if ($td.attr("colSpan") == 1 && !$td.hasClass(".mergeEmptyCell")) {
				//regular cells
				$td.css("display", display);
			}
			else {
				//merged cells
				if (display == "none") {
					//hide
					$td.before("<td class='mergeEmptyCell' style='display:none'></td>");
				} else {
					//show
					//find the first td with the merge div (with the colspan)
					var tdMerged = $td.next(":has(.merge)");
					$td.remove();
					$td = tdMerged;
				}
				var dir = (display == "none" ? -1 : 1);
				var $divMerge = $td.find(".merge");
				$divMerge.css("left", parseInt($divMerge.css("left")) + dir * that.columnWidths[col]);
				$divMerge.width($divMerge.width() - dir * that.columnWidths[col]);
				$td.colSpan($td.attr("colSpan") + dir);
			}
		});
	},
	
	columnCount: function(){
		return $("tr:first", this.table).children().length;
	},
	
	_buildColumnWidths: function(){
		if (this.columnWidths)
			return;
		this.columnWidths = [];
		for(var c = 0; c < this.columnCount();++c){
			var $td;
			if (c < this.fixedCols){
				$td = $('.cw th:eq('+ c +')', this.table);
			} else {
				var tdId = c - this.fixedCols;
				$td = $('.cw td:eq('+ tdId +')', this.table);
			}
			var tdw = $td[0].offsetWidth;//$td.width();
			this.columnWidths.push(tdw);
		}
	},
	
	_columnLeft: function(col) {
		var w = 0;
		this._buildColumnWidths();
		for(var c = 0; c < col; ++c)
			w += this.columnWidths[c];
		return w;
	},
	
	/**
	 * scroll horizontally and vertically to make sure the given cell is visible.
	 * row, col are 0-based and take into account row and column headers
	 */
	makeVisible: function(row, col) {
		//scroll if necessary
		var cell = $(this.rows[row]).tdAtIndex(col);		
		var sel = $(cell).bounds('parent');
		var div = this.cellsDiv.scrollBounds();
		if (sel.b + 10 >= div.b ) {
			this.cellsDiv.scrollTop(sel.b + 10 - div.h);
		}
		
		sel.l = this._columnLeft(col) - this._columnLeft(1); //the fixed columns don't scroll
		sel.r = sel.l + this.columnWidths[col];
		
		if (sel.r + 10 >= div.r || col < this.firstVisibleCol) {
			this.scrollLeft(sel.l);
		}

	},
	
	scrollLeft:function(x) {
		if (arguments.length == 0)
			return this.horizScroll.scrollLeft();
	
		this.horizScroll.scrollLeft(x);
		//let the scroll bar manage min and amx
		var rx = this.horizScroll.scrollLeft();
		
		//TODO optimize this
		this._buildColumnWidths();
		
		var that = this;
		var fullWidth = this.horizScroll.find("div").width();
		var tw = 0;
		this.firstVisibleCol = this.fixedCols;
		for(var c = this.fixedCols; c < that.columnCount();++c){
			var tdw = this.columnWidths[c];
			tw += tdw;
			if (tw - tdw / 2 >= rx) {
				this.firstVisibleCol = c;
				break;
			}
			fullWidth -= tdw; 
		}
		
		this.table.width(fullWidth);
		this.fixedRowsTable.width(fullWidth);
		
		//now hide cols on the left of the given position
		for(var c = this.fixedCols; c < this.columnCount();++c){
			var tdId = c - 1;
			var display = c < this.firstVisibleCol ? "none" : "";
			var tdi = 'td:eq('+tdId+')';
			var thi = 'th:eq('+(tdId + 1)+')';
			if (c >= this.firstVisibleCol && that.rows.find(tdi).css("display") != "none")
				break;
			
			that._setColDisplay(c, display);
			//fixed rows table does not exists for summary sheet!
			that.fixedRowsTable.find("tr " + thi).css("display", display);
		}
		
		this._trigger("bodyScroll");		
	},
	
	refreshTotalWidth:function() {
		var fullWidth = 0;
		this.columnWidths = null;
		this._buildColumnWidths();

		var hiddenColsWidth = 0;
		//extract the hidden columns
		for(var c = this.fixedCols; c < this.columnCount();++c){
			var tdw = this.columnWidths[c];
			if (c < this.firstVisibleCol) {
				hiddenColsWidth += tdw;
			}
			fullWidth += tdw;
		}
		
		this.horizScroll.width(fullWidth - hiddenColsWidth);
		this.table.width(fullWidth - hiddenColsWidth);
		this.fixedRowsTable.width(fullWidth);
	},
			
	totalWidth:function() {
		return this.horizScroll.width();
	},
	
	fixedColsWidth: function() {
		var w = 0;
		for(var c = 0; c < this.fixedCols; ++c)
			w += this.columnWidths[c]; 
		return w;
	},
	
	synchronize: function(otherNxTable){
		var other = otherNxTable.data("nxtable");
		this.horizScroll = other.horizScroll;
		var w = other.table.width();
		this.table.width(w);
		$('.cw', this.table).html($('.cw', other.table).html());
	},
	

	defaults: {
		cellsDivSelector:".cellsDiv",
		fixedRowsDivSelector:".fixedRowsDiv",
		horizontalScrollSelector: ".horizSheetScroll"
	}

});

$.extend($.nx.nxtable, {
	version: "1.0",
	getter: ["scrollLeft", "totalWidth", "fixedColsWidth"],
	defaults: {
		cellsDivSelector:".cellsDiv",
		fixedRowsDivSelector:".fixedRowsDiv",
		horizontalScrollSelector: ".horizSheetScroll"
	}
});

})(jQuery);
