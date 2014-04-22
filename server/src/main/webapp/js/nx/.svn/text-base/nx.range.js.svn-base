/**
 * 
 * @param sheet
 */
function CellRange(sheet) {
	this.sheet = sheet;
	this.start = this.end = null;
	//true if the range is used for replication
	this.replicated = false;
	this.fullRow = false;
	this.fullCol = false;
}

CellRange.prototype = {
	/**
	 * @param cell1 can be a string: C20 or can be an object {col, row}
	 * @param cell2 same as cell1
	 * @param replicated - if true only one line or column is selected - as in replication mode
	 * start can be null, to move only the end part
	 */
	setRange: function (start, end, replicated, fullRow, fullCol){
		this.select(false);
		this.replicated = replicated;
		this.fullRow = fullRow;
		this.fullCol = fullCol;
		
		var c1 = start != null ? this.sheet.cell(start) : this.start; 
		var c2 = end != null ? this.sheet.cell(end): c1;
		if (c1 == null || c2 == null)
			return;
		//start is top-left, end is bottom-right
		if (c1.col < c2.col || c1.col == c2.col && c1.row <= c2.row) {
			this.start = c1;
			this.end = c2;
		} else {
			this.start = c2;
			this.end = c1;
		}
		
		if (replicated && this.start != this.end) {
			if (this.end.col - this.start.col > this.end.row - this.start.row) {//horizontal
				this.end = this.sheet.cell(this.start.row, this.end.col);
			} else {//vertical
				this.end = this.sheet.cell(this.end.row, this.start.col);
			}
		}
		
		this.select(true);
	},
	
	select: function (sel) {
		
	},
	
	/**
	 * rebuild the cells array as the underlying table may have changed
	 */
	refresh:function() {
		this.setRange(this.start, this.end);
	},
	
	drow: function(r, dr, defaultValue) {
		var tr = this.sheet.rowIndex(r, false);
		var ret = this.sheet.rowIndex(tr + dr, true);
		return ret != null ? ret : defaultValue;
	},
	
	dcol: function(c, dc, defaultValue) {
		var td = this.sheet.colIndex(c, false);		
		var ret = this.sheet.colIndex(td + dc, true);
		return ret < 0 || ret >= this.sheet.columnCount() ? defaultValue : ret;
	},
	
	move: function(dc, dr) {
		var s = {row: this.drow(this.start.row, dr, this.start.row), col:this.dcol(this.start.col, dc, this.start.col)};
		var e = null;
		if (this.end != this.start)
			e = {row: this.drow(this.end.row , dr, this.end.row), col: this.dcol(this.end.col, dc, this.end.col)};
		this.setRange(s, e);
	}, 
	

	borders: function(styles) {
		//special cases row 0 and col 0
		var updates = [];
		if (styles['h']) {
			var refs = intervals(styles['h'], this.start.col, this.end.col);
			for(var r in refs) 
				updates.push({ref:this.sheet.areaRef(this.start.row, refs[r].start, this.end.row, refs[r].end, true), style: "br"});			
		}

		if (styles['v']) {
			var refs = intervals(styles['v'], this.start.row, this.end.row);
			for(var r in refs) 
				updates.push({ref:this.sheet.areaRef(refs[r].start, this.start.col, refs[r].end, this.end.col, true), style: "bb"});			
		}
		return updates;		
	},
	

	editableValue: function() {
		var s = "";
		var idx = 0;
		for(var r = this.start.row; r != null && r <= this.end.row; r = this.drow(r, 1)) {
			if (r != this.start.row)
				s += "\n";
			for(var c = this.start.col; c <= this.end.col; c = this.dcol(c, 1)) {
				if (c != this.start.col)
					s += "\t";
				s += this.sheet.cell(r,c).value();
				++idx;
			}				
		}
			
		return s;
	},
	
	ref: function(addSheetName) {
		return this.sheet.areaRef(this.fullCol ? null : this.start.row, this.fullRow ? null : this.start.col, 
				this.fullCol ? null : this.end.row, this.fullRow ? null : this.end.col, addSheetName );
	},
	
	mergeCss: function(css1, css2) {
		if (!css1)
			return css2;
		if (!css2)
			return css1;
		var entries1 = css1.split(" ");
		var entries2 = css2.split(" ");
		var entries = {};
		for(var e in entries1)
			entries[entries1[e]]=true;
		for(var e in entries2)
			entries[entries2[e]]=true;
		var css = "";
		for(var e in entries)
			css += e + " ";
		return css;
	},
	
	/**
	 * 
	 * @return {css: [all the css classes found], partial: [true if not all the cells share the css]}
	 */
	css: function() {
		var ret = {css: "", partial: false};
		for(var r = this.start.row; r != null && r <= this.end.row; r = this.drow(r, 1)) {
			for(var c = this.start.col; c <= this.end.col; c = this.dcol(c, 1)) {
				var cellCss = this.sheet.cell(r,c).css();
				ret.css = this.mergeCss(ret.css, cellCss);
				ret.partial = ret.partial || (ret.css != cellCss);
			}				
		}
		return ret;
	}

};