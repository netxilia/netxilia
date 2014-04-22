package org.netxilia.server.js;

import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.netxilia.server.js.NX.nx;
import static org.stjs.javascript.Global.$castArray;

import org.netxilia.server.js.data.JsCellReference;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.TableCell;

/**
 * This class represents a cell in a sheet. In a cell we can have: - the content: either the value as a text or the
 * formula's body - the value: this is the typed value - either a number, text, date, boolean. it can be calculated -
 * the formattedValue: this is the value as is displayed in the cell: e.g. $110.00. it can be calculated - the formula :
 * the formula's body - the formulaFunction: the compiled formula
 * 
 * @param sheet
 * @param coords
 * @param td
 * @return
 */
public class Cell extends JsCellReference {
	private final Sheet sheet;
	public final NetxiliaJQuery $td;
	public final TableCell td;
	private final NetxiliaJQuery $divMerge;

	public Cell(Sheet sheet, int row, int col, TableCell td) {
		super(null, row, col);
		this.sheet = sheet;
		this.td = td;
		this.$td = $(td);
		this.$divMerge = this.$td.find(".merge");
	}

	public String valueAsString(String v) {
		Element elm = this.$divMerge.size() > 0 ? this.$divMerge.get(0) : this.td;
		if (v != null) {
			elm.innerHTML = v;
		}
		return elm.innerHTML;
	}

	public void setValue(String v) {
		this.$td.attr("title", v != null ? v : "");
	}

	public String getValue() {
		String t = this.$td.attr("title");
		return t != null && t != "" ? t : this.valueAsString(null);

	}

	public String formula() {
		String v = this.$td.attr("title");
		return v != null && v.length() > 1 && v.charAt(0) == '=' ? v : null;
	}

	public String getCss() {
		return this.td.className;
	}

	public void setCss(String style) {
		this.td.className = style;
	}

	public boolean hasCss(String style) {
		return this.$td.hasClass(style);
	}

	public String editorInfo() {
		String css = this.getCss();
		if (css == null) {
			return "";
		}
		Array<String> cls = $castArray(css.split(" "));
		for (int c : cls) {
			String editor = nx.app.desc.editors.$get(cls.$get(c));
			if (editor != null) {
				return editor;
			}
		}
		return "";
	}

	public String ref(boolean addSheetName) {
		return this.sheet.cellRef(this.row, this.col, false, false, addSheetName);
	}

	public String absoluteRef(boolean addSheetName) {
		return this.sheet.cellRef(this.row, this.col, true, true, addSheetName);
	}

	public void span(int rowSpan, int colSpan) {
		int oldColSpan = td.colSpan != 0 ? td.colSpan : 1;

		if (colSpan >= 1) {
			// add supp cells if colSpan is reduced
			for (int i = colSpan; i < oldColSpan; ++i) {
				this.$td.after("<td></td>");
			}
			// remove cells if colSpan is augmented
			for (int i = oldColSpan; i < colSpan; ++i) {
				this.$td.next().remove();
			}
			this.$td.colSpan(colSpan);
		}
	}

}
