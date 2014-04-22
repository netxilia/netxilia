package org.netxilia.server.js.plugins;

import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.$object;
import static org.stjs.javascript.Global.$properties;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.jquery.GlobalJQuery.$;

import org.netxilia.server.js.Bounds;
import org.netxilia.server.jslib.BoundsPlugin;
import org.netxilia.server.jslib.TDJQueryHelpers;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback2;
import org.stjs.javascript.jquery.JQueryAndPlugins;
import org.stjs.javascript.jquery.impl.JQueryPlugin;

/**
 * This plugin manages the horizontal and vertical scroll of a table with fixed columns and rows. Because the browser do
 * not offer this possibility directly, the techniques involve the usage of separate tables for the columns and rows and
 * synchronize then the widths and heights. Using only this technique, when having many rows the synchronization of row
 * heights is time consuming. This plugin uses a mixed technique: for fixed rows it uses a separate table and for fixed
 * columns uses a technique inspired by Google Spreadsheet: the fixed columns are in the main table but the non-fixed
 * columns are displayed or hidden as the user scrolls. The performance problem is fixed but this adds another
 * complication when dealing with colspans (merged cells).
 */
public class NXTable<FullJQuery extends JQueryAndPlugins<?>> extends JQueryPlugin {
	private NXTableOptions options;
	private FullJQuery fixedRowsTable;
	private FullJQuery cellsDiv;
	private FullJQuery table;
	private FullJQuery rows;
	private FullJQuery horizScroll;
	private int fixedCols;
	private int firstVisibleCol;
	private FullJQuery element;
	protected Array<Integer> columnWidths;

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void _init() {
		final NXTable<FullJQuery> that = this;
		NXTableOptions o = (NXTableOptions) $object($.extend($map(), (Map) $properties(defaults),
				(Map) $properties(this.options)));
		this.fixedRowsTable = (FullJQuery) $(o.fixedRowsDivSelector, this.element).find("table");
		this.cellsDiv = (FullJQuery) $(o.cellsDivSelector, this.element);
		this.table = (FullJQuery) $(o.cellsDivSelector, this.element).find("table");
		this.rows = (FullJQuery) this.table.find("tr");
		this.horizScroll = (FullJQuery) $(o.horizontalScrollSelector, this.element);
		this.fixedCols = 1;
		this.firstVisibleCol = this.fixedCols;

		/*
		 * this.horizScroll.scroll(new EventHandler() {
		 * 
		 * @Override public boolean onEvent(Event ev, Element THIS) { int x = $(THIS).scrollLeft(); that.scrollLeft(x);
		 * return false; } });
		 */
	}

	/**
	 * set the col column's display for all the rows
	 */
	@SuppressWarnings("unchecked")
	private void _setColDisplay(final int col, final String display) {
		String crtDisplay = (String) $(((TDJQueryHelpers) $(this.rows.get(0))).tdAtIndex(col)).css("display");
		if (crtDisplay == display) {
			return;
		}
		final NXTable<FullJQuery> that = this;
		this.rows.each(new Callback2<Integer, Element>() {
			@Override
			public void $invoke(Integer idx, Element THIS) {

				FullJQuery $td = (FullJQuery) $(((TDJQueryHelpers) $(THIS)).tdAtIndex(col));

				if ($td.attr("colSpan") == "1" && !$td.hasClass(".mergeEmptyCell")) {
					// regular cells
					$td.css("display", display);
				} else {
					// merged cells
					if (display == "none") {
						// hide
						$td.before("<td class='mergeEmptyCell' style='display:none'></td>");
					} else {
						// show
						// find the first td with the merge div (with the colspan)

						FullJQuery tdMerged = (FullJQuery) $td.next(":has(.merge)");
						$td.remove();
						$td = tdMerged;
					}
					int dir = (display == "none" ? -1 : 1);
					FullJQuery $divMerge = (FullJQuery) $td.find(".merge");
					$divMerge.css("left", parseInt($divMerge.css("left")) + dir * that.columnWidths.$get(col));
					$divMerge.width($divMerge.width() - dir * that.columnWidths.$get(col));
					((TDJQueryHelpers) $td).colSpan(parseInt($td.attr("colSpan")) + dir);
				}
			}
		});
	}

	protected int columnCount() {
		return $("tr:first", this.table).children().size();
	}

	@SuppressWarnings("unchecked")
	private void _buildColumnWidths() {
		if (this.columnWidths != null) {
			return;
		}
		this.columnWidths = $array();
		for (int c = 0; c < this.columnCount(); ++c) {
			FullJQuery $td;
			if (c < this.fixedCols) {
				$td = (FullJQuery) $(".cw th:eq(" + c + ")", this.table);
			} else {
				int tdId = c - this.fixedCols;
				$td = (FullJQuery) $(".cw td:eq(" + tdId + ")", this.table);
			}
			int tdw = $td.get(0).offsetWidth;// $td.width();
			this.columnWidths.push(tdw);
		}
	}

	private int _columnLeft(int col) {
		int w = 0;
		this._buildColumnWidths();
		for (int c = 0; c < col; ++c) {
			w += this.columnWidths.$get(c);
		}
		return w;
	}

	/**
	 * scroll horizontally and vertically to make sure the given cell is visible. row, col are 0-based and take into
	 * account row and column headers
	 */
	protected void makeVisible(int row, int col) {
		// scroll if necessary
		Element cell = ((TDJQueryHelpers) $(this.rows.get(row))).tdAtIndex(col);
		Bounds sel = ((BoundsPlugin) $(cell)).bounds("parent");
		Bounds div = ((BoundsPlugin) this.cellsDiv).scrollBounds();
		if (sel.b + 10 >= div.b) {
			this.cellsDiv.scrollTop(sel.b + 10 - div.h);
		}

		sel.l = this._columnLeft(col) - this._columnLeft(1); // the fixed columns don't scroll
		sel.r = sel.l + this.columnWidths.$get(col);

		if (sel.r + 10 >= div.r || col < this.firstVisibleCol) {
			this.scrollLeft(sel.l);
		}

	}

	public int scrollLeft(Integer x) {
		if (x == null) {
			return this.horizScroll.scrollLeft();
		}

		this.horizScroll.scrollLeft(x);
		// let the scroll bar manage min and amx
		int rx = this.horizScroll.scrollLeft();

		// TODO optimize this
		this._buildColumnWidths();

		int fullWidth = this.horizScroll.find("div").width();
		int tw = 0;
		this.firstVisibleCol = this.fixedCols;
		for (int c = this.fixedCols; c < columnCount(); ++c) {
			int tdw = this.columnWidths.$get(c);
			tw += tdw;
			if (tw - tdw / 2 >= rx) {
				this.firstVisibleCol = c;
				break;
			}
			fullWidth -= tdw;
		}

		this.table.width(fullWidth);
		this.fixedRowsTable.width(fullWidth);

		// now hide cols on the left of the given position
		for (int c = this.fixedCols; c < this.columnCount(); ++c) {
			int tdId = c - 1;
			String display = c < this.firstVisibleCol ? "none" : "";
			String tdi = "td:eq(" + tdId + ")";
			String thi = "th:eq(" + (tdId + 1) + ")";
			if (c >= this.firstVisibleCol && rows.find(tdi).css("display") != "none") {
				break;
			}

			_setColDisplay(c, display);
			// fixed rows table does not exists for summary sheet!
			fixedRowsTable.find("tr " + thi).css("display", display);
		}

		_trigger("bodyScroll");
		return x;
	}

	protected void refreshTotalWidth() {
		int fullWidth = 0;
		this.columnWidths = null;
		this._buildColumnWidths();

		int hiddenColsWidth = 0;
		// extract the hidden columns
		for (int c = this.fixedCols; c < this.columnCount(); ++c) {
			int tdw = this.columnWidths.$get(c);
			if (c < this.firstVisibleCol) {
				hiddenColsWidth += tdw;
			}
			fullWidth += tdw;
		}

		this.horizScroll.width(fullWidth - hiddenColsWidth);
		this.table.width(fullWidth - hiddenColsWidth);
		this.fixedRowsTable.width(fullWidth);
	}

	protected int totalWidth() {
		return this.horizScroll.width();
	}

	protected int fixedColsWidth() {
		int w = 0;
		for (int c = 0; c < this.fixedCols; ++c) {
			w += this.columnWidths.$get(c);
		}
		return w;
	}

	@SuppressWarnings("unchecked")
	protected void synchronize(FullJQuery otherNxTable) {
		NXTable<FullJQuery> other = (NXTable<FullJQuery>) otherNxTable.data("nxtable");
		this.horizScroll = other.horizScroll;
		int w = other.table.width();
		this.table.width(w);
		$(".cw", this.table).html($(".cw", other.table).html());
	}

	private final static NXTableOptions defaults = new NXTableOptions() {
		{
			cellsDivSelector = ".cellsDiv";
			fixedRowsDivSelector = ".fixedRowsDiv";
			horizontalScrollSelector = ".horizSheetScroll";
		}
	};

	public static void main(String[] args) {
		$.widget("nx.nxtable", new NXTable<JQueryAndPlugins<?>>());
		// $.extend(
		// $.nx.splitter,
		// $map("version", "1.0", "getter", $array("scrollLeft", "totalWidth", "fixedColsWidth"), "defaults",
		// defaults));
	}

}
