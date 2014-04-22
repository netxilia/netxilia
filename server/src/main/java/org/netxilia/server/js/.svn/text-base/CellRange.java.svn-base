package org.netxilia.server.js;

import static org.netxilia.server.js.NX.nx;
import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$castArray;
import static org.stjs.javascript.Global.$map;

import org.netxilia.server.js.Utils.StartEnd;
import org.netxilia.server.js.data.JsCellReference;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;

public class CellRange {
	private final Sheet sheet;
	public Cell start;
	public Cell end;
	public boolean replicated;
	public boolean fullRow;
	public boolean fullCol;

	public CellRange(Sheet sheet) {
		this.sheet = sheet;
		this.start = this.end = null;
		// true if the range is used for replication
		this.replicated = false;
		this.fullRow = false;
		this.fullCol = false;
	}

	/**
	 * @param cell1
	 *            can be a string: C20 or can be an object {col, row}
	 * @param cell2
	 *            same as cell1
	 * @param replicated
	 *            - if true only one line or column is selected - as in replication mode start can be null, to move only
	 *            the end part
	 */
	public void setRange(JsCellReference start, JsCellReference end, boolean replicated, boolean fullRow,
			boolean fullCol) {
		this.select(false);
		this.replicated = replicated;
		this.fullRow = fullRow;
		this.fullCol = fullCol;

		Cell c1 = start != null ? this.sheet.cell(start, null) : this.start;
		Cell c2 = end != null ? this.sheet.cell(end, null) : c1;
		if (c1 == null || c2 == null) {
			return;
		}
		// start is top-left, end is bottom-right
		if (c1.col < c2.col || c1.col == c2.col && c1.row <= c2.row) {
			this.start = c1;
			this.end = c2;
		} else {
			this.start = c2;
			this.end = c1;
		}

		if (replicated && this.start != this.end) {
			if (this.end.col - this.start.col > this.end.row - this.start.row) {// horizontal
				this.end = this.sheet.cell(this.start.row, this.end.col);
			} else {// vertical
				this.end = this.sheet.cell(this.end.row, this.start.col);
			}
		}

		this.select(true);
	}

	public void select(boolean sel) {

	}

	/**
	 * rebuild the cells array as the underlying table may have changed
	 */
	public void refresh() {
		this.setRange(this.start, this.end, replicated, fullRow, fullCol);
	}

	public int drow(int r, int dr, int defaultValue) {
		Integer tr = this.sheet.rowIndex(r, false);
		Integer ret = this.sheet.rowIndex(tr + dr, true);
		return ret != null ? ret : defaultValue;
	}

	public int dcol(int c, int dc, int defaultValue) {
		Integer td = this.sheet.colIndex(c, false);
		Integer ret = this.sheet.colIndex(td + dc, true);
		return ret < 0 || ret >= this.sheet.columnCount() ? defaultValue : ret;
	}

	public void move(final int dc, final int dr) {
		JsCellReference s = new JsCellReference(null, drow(start.row, dr, start.row), dcol(start.col, dc, start.col));
		JsCellReference e = null;
		if (this.end != this.start) {
			e = new JsCellReference(null, drow(end.row, dr, end.row), dcol(end.col, dc, end.col));
		}
		this.setRange(s, e, replicated, fullRow, fullCol);
	}

	public Array<CellWithStyle> borders(Map<String, Array<String>> styles) {
		// special cases row 0 and col 0
		Array<CellWithStyle> updates = $array();
		if (styles.$get("h") != null) {
			Array<StartEnd> refs = nx.utils.intervals(styles.$get("h"), this.start.col, this.end.col);
			for (int r : refs) {
				updates.push(new CellWithStyle(this.sheet.areaRef(this.start.row, refs.$get(r).start, this.end.row,
						refs.$get(r).end, true), "br"));
			}
		}

		if (styles.$get("v") != null) {
			Array<StartEnd> refs = nx.utils.intervals(styles.$get("v"), this.start.row, this.end.row);
			for (int r : refs) {
				updates.push(new CellWithStyle(this.sheet.areaRef(refs.$get(r).start, this.start.col, refs.$get(r).end,
						this.end.col, true), "bb"));
			}
		}
		return updates;
	}

	public String editableValue() {
		String s = "";
		for (Integer r = this.start.row; r != null && r <= this.end.row; r = this.drow(r, 1, 0)) {
			if (r != this.start.row) {
				s += "\n";
			}
			for (int c = this.start.col; c <= this.end.col; c = this.dcol(c, 1, 0)) {
				if (c != this.start.col) {
					s += "\t";
				}
				s += this.sheet.cell(r, c).getValue();
			}
		}

		return s;
	}

	public String ref(boolean addSheetName) {
		return this.sheet.areaRef(this.fullCol ? null : this.start.row, this.fullRow ? null : this.start.col,
				this.fullCol ? null : this.end.row, this.fullRow ? null : this.end.col, addSheetName);
	}

	public String mergeCss(String css1, String css2) {
		if (css1 == null) {
			return css2;
		}
		if (css2 == null) {
			return css1;
		}
		Array<String> entries1 = $castArray(css1.split(" "));
		Array<String> entries2 = $castArray(css2.split(" "));
		Map<String, Boolean> entries = $map();
		for (int e : entries1) {
			entries.$put(entries1.$get(e), true);
		}
		for (int e : entries2) {
			entries.$put(entries2.$get(e), true);
		}
		String css = "";
		for (String e : entries) {
			css += e + " ";
		}
		return css;
	}

	/**
	 * 
	 * @return {css: [all the css classes found], partial: [true if not all the cells share the css]}
	 */
	public StyleRange css() {
		StyleRange ret = new StyleRange();
		for (Integer r = this.start.row; r != null && r <= this.end.row; r = this.drow(r, 1, 0)) {
			for (int c = this.start.col; c <= this.end.col; c = this.dcol(c, 1, 0)) {
				String cellCss = this.sheet.cell(r, c).getCss();
				ret.css = this.mergeCss(ret.css, cellCss);
				ret.partial = ret.partial || (ret.css != cellCss);
			}
		}
		return ret;
	}

	public static class StyleRange {
		public String css = "";
		public boolean partial = false;
	}
}
