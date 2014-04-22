/**
 * This class represents a cell in a sheet.
 * In a cell we can have:
 * - the content: either the value as a text or the formula's body
 * - the value: this is the typed value - either a number, text, date, boolean. it can be calculated
 * - the formattedValue: this is the value as is displayed in the cell: e.g. $110.00. it can be calculated
 * - the formula : the formula's body
 * - the formulaFunction: the compiled formula
 * @param sheet
 * @param coords
 * @param td
 * @return
 */
function Cell(sheet, row, col, td) {
	this.sheet = sheet;
	this.row = row;
	this.col = col;
	this.td = td;
	this.$td = $(td);
	this.$divMerge = this.$td.find(".merge");
}

Cell.prototype = {
	
	
	valueAsString: function(v) {
		var elm = this.$divMerge.length > 0 ? this.$divMerge[0] : this.td;
		return arguments.length == 0 ? elm.innerHTML : elm.innerHTML = v;
	},
	
	value: function(v) {
		if (arguments.length == 0) {
			var t = this.$td.attr("title");
			return  t != null && t != "" ? t : this.valueAsString();
		}
		this.$td.attr("title", v != null ? v : "");
	},

	formula: function() {
		var v = this.$td.attr("title");
		return v && v.length > 1 && v.charAt(0) == "=" ? v : null;
	},

	
	css: function(style) {
		return arguments.length == 0 ?this.td.className : this.td.className = style;
	},
	
	hasCss: function(style){
		return this.$td.hasClass(style);
	},
	
	editorInfo: function() {
		var css = this.css();
		if (!css)
			return "";
		var cls = css.split(" ");
		for(var c in cls){
			var editor = nx.app.desc.editors[cls[c]];
			if (editor)
				return editor;
		}
		return "";
	},
	
	
	ref: function(addSheetName) {
		return this.sheet.cellRef(this.row, this.col, false, false, addSheetName);
	},
	
	absoluteRef:function(addSheetName) {
		return this.sheet.cellRef(this.row, this.col, true, true, addSheetName);
	},
	
	span: function(rowSpan, colSpan) {
		var oldColSpan = this.td.colSpan || 1;
					
		if (colSpan >= 1) {
			//add supp cells if colSpan is reduced
			for(var i = colSpan; i < oldColSpan; ++i)
				this.$td.after("<td></td>");
			//remove cells if colSpan is augmented
			for(var i = oldColSpan; i < colSpan; ++i)
				this.$td.next().remove();
			this.$td.colSpan(colSpan);
		}
//		else {
//			this.$td.remove();
//		}
		
		//if (rowSpan > 1)
		//	this.td.rowSpan = rowSpan;
	}
	
	
}