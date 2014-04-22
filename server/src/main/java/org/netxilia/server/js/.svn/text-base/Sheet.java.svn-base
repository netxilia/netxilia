package org.netxilia.server.js;

import static org.netxilia.server.js.NX.nx;
import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.$or;
import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.encodeURIComponent;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.Global.setTimeout;
import static org.stjs.javascript.Global.window;
import static org.stjs.javascript.JSStringAdapter.fromCharCode;
import static org.stjs.javascript.JSStringAdapter.match;
import static org.stjs.javascript.JSStringAdapter.replace;

import org.netxilia.server.js.TreeView.TreeNode;
import org.netxilia.server.js.data.ChartDescription;
import org.netxilia.server.js.data.EventData;
import org.netxilia.server.js.data.JsAreaReference;
import org.netxilia.server.js.data.JsCellReference;
import org.netxilia.server.js.data.NetxiliaEvent;
import org.netxilia.server.js.data.SheetDescription;
import org.netxilia.server.js.data.WindowIndex;
import org.netxilia.server.js.data.WindowInfo;
import org.netxilia.server.js.editors.EditingContext;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.netxilia.server.jslib.OpenFlashChart;
import org.stjs.javascript.Array;
import org.stjs.javascript.Date;
import org.stjs.javascript.Map;
import org.stjs.javascript.RegExp;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.dom.Input;
import org.stjs.javascript.dom.TableCell;
import org.stjs.javascript.dom.TableRow;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Callback2;
import org.stjs.javascript.functions.Callback3;
import org.stjs.javascript.functions.Function1;
import org.stjs.javascript.jquery.AjaxParams;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.EventHandler;
import org.stjs.javascript.jquery.JQueryXHR;
import org.stjs.javascript.jquery.Position;
import org.stjs.javascript.jquery.plugins.DraggableOptions;
import org.stjs.javascript.jquery.plugins.DraggableUI;
import org.stjs.javascript.jquery.plugins.ResizableOptions;
import org.stjs.javascript.jquery.plugins.ResizeableUI;
import org.stjs.javascript.jquery.plugins.UIEventHandler;

public class Sheet {
	public NetxiliaJQuery container; // this is the div in the main window
	private NetxiliaJQuery cellContainer; // this is the div in the cell iframe
	public boolean loaded = false; // becomes true when the iframe containing the cells finished loading
	public NetxiliaJQuery table;
	private NetxiliaJQuery fixedRowsTable;
	public CellRange selection;
	public NetxiliaJQuery selectionContent;
	private NetxiliaJQuery selectedArea;

	private TableCell selectionStartTd; // the TD where the user clicked when he starts selecting more cells
	// sheet ;
	private NetxiliaJQuery selector;
	private NetxiliaJQuery replicator;
	private boolean mouseDown = false;
	private boolean replicatorDown = false;
	private boolean hasFormulaCells = false;

	private CellRange captureSelection;

	private Map<Long, ConnectedWindow> windows; // these are the other windows editing the same sheet

	private int resizedCol = -1;
	private boolean resizingCol = false;
	public EditingContext editingContext;

	private TreeView treeView;
	public String filter;
	public int pageNo = 0;
	private int pageCount = 1;

	private int fixedRows = 0;
	private int fixedCols = 0;
	public SheetDescription desc;
	private NetxiliaJQuery rows;

	private Map<Integer, Integer> mapTrToRow;
	private Map<Integer, Integer> mapRowToTr;
	private int minRow = 0;
	private int maxRow = 0;
	// these are the indices of non-hidden TR
	private int firstTr = 0;
	private int lastTr = 0;

	public Map<String, String> aliases;
	private long lastChangeTime = 0;
	public boolean waitForKeypress = false;

	private NetxiliaJQuery aliasName;
	private NetxiliaJQuery colAliasNames;
	private NetxiliaJQuery aliasRef;

	private NetxiliaJQuery formulaTip;
	private boolean hasMarkedCells;
	private Element focusElement;
	public boolean filterFormula;

	private Array<ChartDescription> charts;
	private Array<String> spans;
	private NetxiliaJQuery layout;
	public Shortcuts shortcuts;
	protected NetxiliaJQuery colResizer;
	protected int resizerStart;
	private NetxiliaJQuery nxtable;
	private NetxiliaJQuery rowResizer;

	public int columnCount() {
		// return this.cols.length;
		return this.colIndex($(this.rows.get(0)).children().size(), true);
	}

	/**
	 * 
	 * @param row
	 * @return the rowIndex in the sheet's table by offseting with the hidden rows
	 */
	public Integer rowIndex(int row, boolean trToRow) {
		Integer ret = trToRow ? this.mapTrToRow.$get(row) : this.mapRowToTr.$get(row);
		// if (ret == null)
		// ret = trToRow ? this.mapTrToRow[1] : 1;
		return ret;
	}

	public int colIndex(int col, boolean tdToCol) {
		return tdToCol ? col - 1 : col + 1;
	}

	/**
	 * rows and cols are 0-based
	 */
	public Cell cell(Object p1, Object p2) {
		// TODO need to handle fixed rows and cells also
		// this.rows, this.table contain only non-fixed rows
		Integer c = (Integer) p2, r = (Integer) p1;
		if (p2 == null) {
			c = ((JsCellReference) p1).col;
			r = ((JsCellReference) p1).row;
		}

		Integer trId = this.rowIndex(r, false);
		if (trId == null) {
			trId = this.fixedRows + 1;
		}
		// r = bind(r, 0, this.rows.length - 1);
		Element tr = this.rows.get(trId);
		TableCell td = $(tr).tdAtIndex(this.colIndex(c, false));
		return new Cell(this, r, c, td);
	}

	private Cell cellFromTd(TableCell td) {
		TableRow tr = (TableRow) td.parentNode;
		Integer tdIndex = $(td).getNonColSpanIndex();
		return new Cell(this, this.rowIndex(tr.rowIndex, true), this.colIndex(tdIndex, true), td);
	}

	private void showRows(boolean show) {
		if (show) {
			$(".collapsed", this.table).removeClass("collapsed");
		} else {
			$("tr", this.table).addClass("collapsed");
		}
	}

	private void showRow(int row, boolean show) {
		Integer r = this.rowIndex(row, false);// nth-child is 1-based - but it seems tha jquery does not take hidden !?

		if (show) {
			$("tr:nth(" + r + ")", this.table).removeClass("collapsed");
		} else {
			$("tr:nth(" + r + ")", this.table).addClass("collapsed");
		}
	}

	private void setRowNums(int startRow, int startTr) {
		int row = startRow;
		for (int r = startTr; r < this.rows.size(); ++r, ++row) {
			$(this.rows.get(r)).find("th").text(row + 1);
		}
	}

	private void insertRow(int row) {
		String s = "<tr>";
		s += "<th>" + (row + 1) + "</th>";
		int colCount = this.columnCount();
		for (int i = 0; i < colCount; ++i) {
			s += "<td></td>";
		}
		s += "</tr>";

		int tr = this.rowIndex(row, false);
		$(this.rows.get(tr)).before(s);
		this.setRowNums(row + 1, tr);
		this.initTable();
	}

	private void deleteRow(int row) {
		int tr = this.rowIndex(row, false);// nth-child is 1-based - but it seems tha jquery does not take hidden !?
		$(this.rows.get(tr)).remove();
		this.setRowNums(row - 1, tr);
		this.initTable();
	}

	private void insertColumn(int col) {
		this.viewPage(this.pageNo, false);
		int c = this.colIndex(col, false) + 1;// nth-child is 1-based
		// !! because the first column in the fixedRowsTable has a TH not a column header, use -1 on indexes

		this.rows.find("td:nth-child(" + c + ")").before("<td></td>");
		this.fixedRowsTable.find("tr.labels th:nth-child(" + c + ")").before("<th style='width:80px'></th>");
		this.fixedRowsTable.find("tr.aliases th:nth-child(" + c + ")").before("<th><input type='text' value=''></th>");

		this.container.nxtable("refreshTotalWidth");
		this.rebuildColumnHeaders(c - 1);
	}

	private void deleteColumn(int col) {
		this.viewPage(this.pageNo, false);
		int c = this.colIndex(col, false) + 1;// nth-child is 1-based
		// !! because the first column in the fixedRowsTable has a TH not a column header, use -1 on indexes

		NetxiliaJQuery $td = $(".cells .cw td:eq(" + c + ")", this.cellContainer);
		this.container.nxtable("refreshTotalWidth");

		$("tr td:nth-child(" + c + ")", this.table).remove();
		$("tr th:nth-child(" + (c - 1) + ")", this.fixedRowsTable).remove();
		this.rebuildColumnHeaders(c - 1);
	}

	private void resizeColumn(int col, int w) {
		int tdIndex = this.colIndex(col, false);
		NetxiliaJQuery $col = $("tr:first th:eq(" + tdIndex + ")", this.fixedRowsTable);
		NetxiliaJQuery $td = $("tr.cw td:eq(" + (tdIndex - 1) + ")", this.table);
		$td.width(w);
		$col.width(w);
		this.container.nxtable("refreshTotalWidth");
		nx.app.onResizeColumns(this);
	}

	private void rebuildColumnHeaders(int start) {
		for (int c = start; c < this.columnCount(); ++c) {
			String label = this.columnLabel(this.colIndex(c - 1, true));
			$("tr.labels th:nth-child(" + c + ")", this.fixedRowsTable).text(label);
			$("tr.aliases th:nth-child(" + c + ") input", this.fixedRowsTable).attr("id", "alias-" + label);
		}
		this.aliasName = $("tr.aliases th.alias input", this.fixedRowsTable);
		this.colAliasNames = $("tr.aliases th input", this.fixedRowsTable);
	}

	private String columnLabel(Integer col) {
		// TODO convert to more than one-letter code
		return col != null ? fromCharCode(String.class, 65 + col) : "";
	}

	private String rowLabel(Integer row) {
		return row != null ? "" + (row + 1) : "";
	}

	public String cellRef(int row, int col, boolean fixedCol, boolean fixedRow, boolean addSheetName) {
		return (addSheetName ? this.desc.name + "!" : "") + (fixedCol ? "$" : "") + this.columnLabel(col)
				+ (fixedRow ? "$" : "") + (row + 1);
	}

	public String areaRef(int startRow, int startCol, int endRow, int endCol, boolean addSheetName) {
		return (addSheetName ? this.desc.name + "!" : "") + this.columnLabel(startCol) + this.rowLabel(startRow) + ":"
				+ this.columnLabel(endCol) + this.rowLabel(endRow);
	}

	private void setSpans(Array<String> areas, boolean merged) {
		for (Integer a : areas) {
			JsAreaReference area = nx.utils.parseAreaReference(areas.$get(a));
			for (int r = area.topLeft.row; r <= area.bottomRight.row; ++r) {
				for (int c = area.topLeft.col; c <= area.bottomRight.col; ++c) {
					boolean topLeftCell = r == area.topLeft.row && c == area.topLeft.col;
					int rowSpan = merged ? (topLeftCell ? area.bottomRight.row - area.topLeft.row + 1 : -1) : 1;
					int colSpan = merged ? (topLeftCell ? area.bottomRight.col - area.topLeft.col + 1 : -1) : 1;
					this.cell(r, c).span(rowSpan, colSpan);
				}
			}
		}
	}

	/******* charts ***********/
	private void chartMoved(Element chartDiv) {
		NetxiliaJQuery $chart = $(chartDiv);
		int id = parseInt($chart.attr("id").substring(5));
		nx.resources.charts.move(this.desc.workbook, this.desc.name, id, parseInt($chart.css("left")),
				parseInt($chart.css("top")), $chart.width(), $chart.height(), null, null);
	}

	private void chartRefresh(int id) {
		NetxiliaJQuery swf = $("#chartFlash" + id, this.cellContainer);
		((OpenFlashChart) swf.get(0)).reload(nx.app.desc.context + "/rest/charts/" + this.desc.workbook + "/"
				+ this.desc.name + "/" + id);
	}

	private void chartDelete(int id) {
		final Sheet that = this;
		nx.resources.charts.del(this.desc.workbook, this.desc.name, id, new Callback1<Void>() {
			@Override
			public void $invoke(Void v) {
				that.reload();
			}
		}, null);
	}

	private void chartSettings(int id) {
		nx.app.setActiveSheet(this);
		nx.app.dlgChart(id, this.charts.$get(id));
	}

	/*********** interaction *************/
	private boolean editMode() {
		return this.editingContext.editor != null;
	}

	private void moveMode(Shortcuts sh, String key, final Callback0 f) {
		final Sheet that = this;
		sh.add(key, new Function1<Event, Boolean>() {
			@Override
			public Boolean $invoke(Event p1) {
				if (that.editMode() && !that.editingContext.defaultEditor && that.captureSelection == null) {
					return true;
				}
				f.$invoke();
				return false;
			}
		}, true);
	}

	private void moveSelection(int dc, int dr, boolean ignoreEditorDefault) {
		// CHECK HERE
		if (!ignoreEditorDefault && this.captureSelection != null) {
			this.captureSelection.move(dc, dr);
			this.editingContext.setCaptureSelection(this.captureSelection);
			return;
		}
		if (!ignoreEditorDefault && !this.editingContext.defaultEditor) {
			return;
		}
		this.beforeSelectionChanged();
		this.editingContext.hide();
		this.selection.move(dc, dr);
		this.selectionChanged();

		this.container.nxtable("makeVisible", this.rowIndex(this.selection.start.row, false),
				this.colIndex(this.selection.start.col, false));
	}

	private void moveSelectionToLimits(boolean firstRow, boolean firstCol, boolean lastRow, boolean lastCol) {
		this.editingContext.hide();
		int r = firstRow ? this.minRow : this.selection.start.row;
		r = lastRow ? this.maxRow : r;
		int c = firstCol ? 0 : this.selection.start.col;
		c = lastCol ? this.columnCount() - 1 : c;

		this.selectionRange(r, c, r, c, false, false);
		this.container.nxtable("makeVisible", this.rowIndex(this.selection.start.row, false),
				this.colIndex(this.selection.start.col, false));
	}

	private int rowFromPosition(int y) {
		for (int r = this.minRow + 1; r <= this.maxRow; ++r) {
			int rt = $(this.rows.get(this.rowIndex(r, false))).offset().top;
			if (rt > y) {
				return r - 1;
			}
		}
		return this.maxRow;
	}

	private void moveSelectionPage(String dir) {
		NetxiliaJQuery cellsDiv = $(".cellsDiv", this.container);
		Bounds div = cellsDiv.scrollBounds();
		int r = this.rowFromPosition(dir == "down" ? (int) org.stjs.javascript.Math.min(div.b, this.table.height()
				- div.h) : div.t - div.h);
		int rt = $(this.rows.get(this.rowIndex(r, false))).offset().top;
		this.selectionRange(r, this.selection.start.col, r, this.selection.start.col, false, false);
		cellsDiv.scrollTop(rt);
	}

	public void selectionRange(int startRow, int startCol, int endRow, int endCol, boolean fullRow, boolean fullCol) {
		this.beforeSelectionChanged();
		this.selection.setRange(this.cell(startRow, startCol), this.cell(endRow, endCol), false, fullRow, fullCol);
		this.selectionChanged();
	}

	private void selectionRangeTd(TableCell startTd, TableCell endTd, boolean withReplicator) {
		if (this.captureSelection != null) {
			this.captureSelection.setRange(startTd != null ? this.cellFromTd(startTd) : null, //
					endTd != null ? this.cellFromTd(endTd) : null, false, false, false);
			this.editingContext.setCaptureSelection(this.captureSelection);
			return;
		}

		this.beforeSelectionChanged();
		this.selection.setRange(startTd != null ? this.cellFromTd(startTd) : null, //
				endTd != null ? this.cellFromTd(endTd) : null, withReplicator, false, false);
		this.selectionChanged();
	}

	private void positionElementOnCell(NetxiliaJQuery elem, Cell cell) {
		Position parentPos = this.table.parent().offset();
		Position pos = cell.$td.offset();
		pos.top -= parentPos.top;
		pos.left -= parentPos.left;
		int w = cell.$td.innerWidth(), h = cell.$td.innerHeight();
		elem.css($map("top", pos.top, "left", pos.left, "width", w, "height", h));
	}

	private void placeSelectors() {
		NetxiliaJQuery startCell = this.selection.start.$td;
		Position parentPos = this.table.parent().offset();
		parentPos.top -= this.table.parent().scrollTop();
		parentPos.left -= this.table.parent().scrollLeft();
		Position pos = startCell.offset();
		pos.top -= parentPos.top;
		pos.left -= parentPos.left;

		int w = 60, h = 16;
		// because of chrome that behaves strangely when cells are hidden
		if (startCell.css("display") != "none") {
			w = startCell.innerWidth();
			h = startCell.innerHeight();
		}

		NetxiliaJQuery endCell = this.selection.end.$td;
		Position epos = pos;
		int ew = w, eh = h;
		if (startCell != endCell) {
			epos = endCell.offset();
			epos.top -= parentPos.top;
			epos.left -= parentPos.left;
			if (endCell.css("display") != "none") {
				ew = endCell.outerWidth();
				eh = endCell.outerHeight();
			}
		}

		this.selector.css($map("top", pos.top, "left", pos.left, "width", w, "height", h));
		this.selectionContent.css($map("top", pos.top, "left", pos.left));
		this.selectedArea.css($map("top", pos.top, "left", pos.left, "width", epos.left - pos.left + ew, "height",
				epos.top - pos.top + eh));

		if (this.selection.start.formula() != null) {
			this.formulaTip.css($map("top", pos.top + 2 * h, "left", pos.left + 40));
		}

		if (this.selection.replicated) {
			this.replicator.css($map("top", epos.top + eh, "left", epos.left + ew));
		} else {
			this.replicator.css($map("top", pos.top + h, "left", pos.left + w));
		}
	}

	private void beforeSelectionChanged() {
		if (this.selection.start != null) {
			// value from editor -> cell
			if (this.editingContext.hasValueChanged()) {
				String value = this.editingContext.value();
				if (this.editingContext.defaultEditor) {
					// check for smart-edit
					String crtFormula = this.selection.start.formula();
					if (crtFormula != null) {
						RegExp re = new RegExp("([\\(+\\-*%=\\/]+\\s*)\\(\\d+\\)", "g");
						if (match(crtFormula, re) != null) {
							value = replace(crtFormula, re, "$1(" + value + ")");
						}
					}
				}
				this.lastChangeTime = new Date().getTime();
				nx.resources.cells.setValue(this.desc.workbook, this.selection.start.ref(true), value, null);
			}
		}
	}

	/**
	 * called anytime the selection changed. TODO use events
	 */
	private void selectionChanged() {

		this.selectionContent.val(this.selection.editableValue());
		this.editingContext.hide();
		this.markFormulaCells(null);

		if (this.selection.start.formula() != null) {
			this.formulaTip.html(this.selection.start.formula());
			this.formulaTip.show();
		} else {
			this.formulaTip.hide();
		}

		this.placeSelectors();
		this.focusSelectionContent();

		// select row & column headers
		$(".sel", this.fixedRowsTable).removeClass("sel");
		$(".sel", this.table).removeClass("sel");

		if (!this.selection.fullCol) {
			for (int r = this.selection.start.row; r <= this.selection.end.row; ++r) {
				$("tr:nth(" + (this.rowIndex(r, false)) + ") th", this.table).addClass("sel");
			}
		}
		if (!this.selection.fullRow) {
			for (int c = this.selection.start.col; c <= this.selection.end.col; ++c) {
				$("tr.labels th:nth(" + (this.colIndex(c, false)) + ")", this.fixedRowsTable).addClass("sel");
			}
		}
		if (!nx.utils.isEmptyObject(this.windows)) {
			nx.resources.windows.notifySelection(nx.app.windowId, this.selection.ref(true), null, null);
		}

		String ref = this.selection.ref(false);
		this.aliasRef.text(ref);
		this.aliasName.val($or(this.aliases.$get(ref), ""));

		nx.app.menuStatus();

	}

	/**
	 * called to display the selection made in another window opened to the same sheet
	 */
	private void markSelection(String areaRef, WindowIndex windowId) {
		ConnectedWindow s = this.windows.$get(windowId.id);
		if (s == null) {
			return;
		}
		JsAreaReference ref = nx.utils.parseAreaReference(areaRef);
		if (ref == null || ref.topLeft == null) {
			return;
		}
		this.positionElementOnCell(s.selector, this.cell(ref.topLeft, null));
	}

	/**
	 * display the editor for the current selection. and mark the areas in the formula
	 */
	private void showEditor() {
		this.editingContext.edit(this.selection.start, null, null);
		this.markFormulaCells(this.selection.start.formula());
	}

	private void toggleMarkArea(JsAreaReference ref, String css) {
		this.markArea(this.hasMarkedCells ? null : ref, css);
	}

	private void markArea(JsAreaReference ref, String css) {
		if (ref != null) {
			if (ref.topLeft.sheet == null || ref.topLeft.sheet == this.desc.name) {
				for (int r = ref.topLeft.row; r <= ref.bottomRight.row; ++r) {
					for (int c = ref.topLeft.col; c <= ref.bottomRight.col; ++c) {
						Cell cell = this.cell(r, c);
						cell.$td.addClass(css);
					}
				}
			}
			this.hasMarkedCells = true;
		} else {
			this.table.find("td." + css).removeClass(css);
			this.hasMarkedCells = false;
		}
	}

	private void markFormulaCells(String formula) {
		if (this.hasMarkedCells) {
			this.markArea(null, "formula");
		}
		if (formula == null || formula.length() < 1 || formula.charAt(0) != '=') {
			this.captureSelection = null;
			return;
		}

		Array<JsAreaReference> refs = nx.utils.findReferencesInFormula(formula);
		for (Integer i : refs) {
			JsAreaReference ref = refs.$get(i);
			this.markArea(ref, "formula");
		}
	}

	private void addWindow(WindowInfo windowInfo) {
		long wid = windowInfo.windowId.id;
		ConnectedWindow s = new ConnectedWindow(wid, windowInfo.username, $("<div class='selector-other'></div>")
				.appendTo(this.cellContainer));
		this.windows.$put(s.id, s);
	}

	private void removeWindow(WindowInfo windowInfo) {
		long wid = windowInfo.windowId.id;
		ConnectedWindow s = this.windows.$get(wid);
		if (s == null) {
			return;
		}
		s.selector.remove();
		this.windows.$delete(s.id);
	}

	private void clearCells() {
		nx.resources.cells.setValue(this.desc.workbook, this.selection.ref(true), "", null);
	}

	private void cancelEdit() {
		this.editingContext.hide();
		this.markFormulaCells(null);
	}

	private void borders(Map<String, Array<String>> borderStyle) {
		if (borderStyle == null) {
			// clear all cells
			nx.resources.cells.applyStyle(this.desc.workbook, this.areaRef(
					(int) org.stjs.javascript.Math.max(this.selection.start.row - 1, 0),
					(int) org.stjs.javascript.Math.max(this.selection.start.col - 1, 0), this.selection.end.row,
					this.selection.end.col, true), "br bb bt bl", "clear", null);
			return;
		}
		Array<CellWithStyle> updates = this.selection.borders(borderStyle);
		for (int u : updates) {
			nx.resources.cells.applyStyle(this.desc.workbook, updates.$get(u).ref, updates.$get(u).style, "add", null);
		}
		this.focusSelectionContent();
	}

	public void checkAutoInsertRow() {
		if (nx.app.autoInsertRow) {
			this.moveSelection(0, 1, true);
		}

	}

	private void buildTreeView(boolean refresh) {
		final Sheet controller = this;
		if (refresh) {
			$(".tree", this.table).removeClass("tree");
		}
		this.treeView = new TreeView();
		for (int r = this.minRow; r <= this.maxRow; ++r) {
			int level = -1;
			for (int c = 0; c < this.columnCount(); ++c) {
				String v = this.cell(r, c).valueAsString(null);
				if (v != null && v.length() > 0) {
					level = c;
					break;
				}
			}
			if (level >= 0) {
				this.treeView.node(level, "" + r, null);
			}
		}
		this.treeView.walk(new Callback1<TreeView.TreeNode>() {
			@Override
			public void $invoke(TreeNode n) {
				if (n.children.$length() > 0) {
					controller.cell(n.key, n.level).$td.addClass("tree");
				}

			}
		}, null);
	}

	/**
	 * toggles to tree view
	 */
	public void toggleTreeView() {
		if (this.treeView == null) {
			this.buildTreeView(false);
		} else {
			// clear all nodes
			this.showRows(true);
			$(".tree", this.table).removeClass("tree");
			this.treeView = null;
		}
	}

	private void toggleTreeNode(TableCell td) {
		final Sheet controller = this;
		Cell cell = this.cellFromTd(td);
		TreeView.TreeNode n = this.treeView.nodes.$get("" + cell.row);
		final boolean expand = cell.$td.hasClass("collapsed");
		if (expand) {
			cell.$td.removeClass("collapsed");
		} else {
			cell.$td.addClass("collapsed");
		}
		this.treeView.walk(new Callback1<TreeView.TreeNode>() {
			@Override
			public void $invoke(TreeNode nc) {
				controller.showRow(parseInt(nc.key), expand);
			}
		}, n);
	}

	/**
	 * toggle the filter-by-cell.
	 */
	public void toggleFilter(boolean useFormula) {
		this.filterFormula = useFormula;
		if (this.filter == null) {
			this.filter = null;
		} else {
			this.filter = useFormula ? this.selection.start.formula() : "="
					+ this.cellRef(0, this.selection.start.col, false, false, false) + "="
					+ this.selection.start.absoluteRef(false);
		}

		this.reload();
	}

	/**
	 * display the column aliases in the input boxes
	 */
	private void displayAliases() {
		// set column aliases
		int colCount = this.columnCount();
		for (int i = 0; i < colCount; ++i) {
			String colLabel = this.columnLabel(i);
			$("#alias-" + colLabel, this.fixedRowsTable).val($or(this.aliases.$get(colLabel + ":" + colLabel), ""));
		}
	}

	/**
	 * events
	 */
	public void processEvents(Array<NetxiliaEvent> events) {
		boolean refreshSelection = false;
		Array<Integer> rowsToResize = $array();

		long t1 = new Date().getTime();
		// this id for cell event
		for (int e : events) {
			NetxiliaEvent ev = events.$get(e);
			if (ev.type == "cellModified") {
				for (int d : ev.data) {
					EventData evd = ev.data.$get(d);
					Cell cell = this.cell(evd.row, evd.column);
					cell.valueAsString($or(evd.formattedValue, ""));
					cell.setCss(evd.style);
					cell.setValue(evd.value);

					rowsToResize.push(this.rowIndex(cell.row, false));
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
				// this.reload();
				this.aliases = (Map) $or(nx.utils.reverseMap(ev.aliases), $map());
				this.displayAliases();

				// set spans spans
				Diff<String> diff = nx.utils.diff(this.spans, ev.spans);
				this.setSpans(diff.deleted, false);
				this.setSpans(diff.added, true);

				this.spans = ev.spans;
			}
		}

		if (refreshSelection) {
			this.selection.refresh();
		}

		this.selectionContent.val(this.selection.editableValue());
		if (nx.app.activeSheet == this) {
			this.focusSelectionContent();
		}
		this.placeSelectors();

		if (this.treeView != null) {
			this.buildTreeView(true);
		}
		long t2 = new Date().getTime();
		// console.info("event time: " + (t2-t1) + " start since lastChange:" + (t1 - this.lastChangeTime));
	}

	public void focusSelectionContent() {
		this.selectionContent.focus();
		this.selectionContent.select();
	}

	public void syncScroll(Sheet sheet) {
		this.container.nxtable("scrollLeft", sheet.container.nxtable("scrollLeft"));
	}

	public void syncColumnSizes(Sheet sheet) {
		int w = sheet.table.width();
		this.table.width(w);
		$(".cells .cw", this.cellContainer).html($(".cells .cw", sheet.cellContainer).html());
	}

	public void viewPage(int p, boolean showPageOnly) {
		final Sheet that = this;
		$(".pager table tbody tr.crt", this.container).removeClass("crt");
		this.pageNo = p;
		$(".pager table tbody tr:nth(" + this.pageNo + ")", this.container).addClass("crt");
		if (!showPageOnly) {
			String u = this.desc.context + "/rest/sheets/" + this.desc.workbook + "/" + this.desc.name + "?start="
					+ (this.pageNo * nx.app.pageSize);
			if (this.filter != null) {
				u += "&filter=" + encodeURIComponent(this.filter);
			}
			final String uu = u;
			$.ajax(new AjaxParams() {
				{
					url = uu;
					success = new Callback3<String, String, JQueryXHR>() {
						@Override
						public void $invoke(String html, String status, JQueryXHR request) {
							that.table.replaceWith(html);
							that.initTable();
							that.initSelection();
							that.handleMouse(true);
						}
					};
					error = new Callback3<JQueryXHR, String, String>() {
						@Override
						public void $invoke(JQueryXHR p1, String p2, String p3) {
							alert("Could not change the page!");
						}
					};
				}
			});
		}
	}

	private void buildPager() {
		NetxiliaJQuery $pager = $(".pager", this.container);

		if (this.pageCount <= 1) {
			this.layout.threeColumn("right", 0);
			return;
		}

		String content = "<table style='width:100%;'>";
		content += "<thead><tr><th>ROWS</th></tr></thead>";

		content += "<tbody>";
		for (int p = 1; p <= this.pageCount; ++p) {
			content += "<tr><td>";
			content += (nx.app.pageSize * (p - 1) + 1);
			content += "</td></tr>";
		}
		content += "</tbody>";
		content += "</table>";
		$pager.html(content);
		this.layout.threeColumn("right", 60);
	}

	public void reload() {
		this.viewPage(this.pageNo, false);
	}

	private void changeAlias(String oldAlias, String newAlias, String ref) {
		oldAlias = $or(oldAlias, "");
		newAlias = $or(newAlias, "");
		if (oldAlias != newAlias) {
			if (oldAlias != "") {
				nx.resources.sheets.deleteAlias(this.desc.workbook, this.desc.name, oldAlias, null, null);
			}

			if (newAlias != "") {
				nx.resources.sheets.setAlias(this.desc.workbook, this.desc.name, newAlias, ref, null, null);
			}
		}
	}

	/**
	 * handlers for mouse interaction
	 */
	private void handleMouse(boolean tableOnly) {
		final Sheet that = this;
		if (tableOnly) {
			$(this.table).mousedown(new EventHandler() {
				@Override
				public boolean onEvent(Event ev, Element THIS) {
					nx.app.setActiveSheet(that);
					// trigger first the blur event that is canceled by "return false" from this function
					if (that.focusElement != null) {
						$(that.focusElement).blur();
					}
					NetxiliaJQuery $td = null;
					if (ev.target.tagName.toLowerCase() == "div" && ev.target.className == "merge") {
						$td = $(ev.target).parent();
					} else if (ev.target.tagName.toLowerCase() == "td") {
						$td = $(ev.target);
					} else if (ev.target.tagName.toLowerCase() == "th") {
						// row headers
						NetxiliaJQuery $th = $(ev.target);
						int r = that.rowIndex(((TableRow) $th.get(0).parentNode).rowIndex, true);
						that.selectionRange(r, 0, r, that.columnCount() - 1, true, false);
						return false;
					} else if (ev.target.className == "ck-format") {
						$td = $(ev.target).parent();
						boolean ck = !((Input) ev.target).checked;// the checked flag will change after this method
						nx.resources.cells.setValue(that.desc.workbook,
								that.cellFromTd((TableCell) $td.get(0)).ref(true), ck ? "true" : "false", null);
						return true;
					}
					if ($td != null) {
						if (that.treeView != null && $td.hasClass("tree") && ev.pageX - $td.offset().left <= 10) {
							that.toggleTreeNode((TableCell) ev.target);
							return false;
						}
						that.mouseDown = true;
						if (ev.shiftKey) {
							that.selectionRangeTd(that.selectionStartTd, (TableCell) $td.get(0), false);
						} else {
							that.selectionStartTd = (TableCell) $td.get(0);
							that.selectionRangeTd((TableCell) $td.get(0), null, false);
						}
						if ($.browser.safari) {
							// just to take the focus
							// that.editingContext.showDefaultEditor(that.selection.start);
							// that.editingContext.hide();
						}
					}

					return false;
				}
			});

			$(this.table).mousemove(new EventHandler() {
				@Override
				public boolean onEvent(Event ev, Element THIS) {
					if (ev.target.tagName.toLowerCase() == "td") {
						if (that.replicatorDown) {
							that.selectionRangeTd(that.selectionStartTd, (TableCell) ev.target, true);
						} else if (that.mouseDown) {
							that.selectionRangeTd(that.selectionStartTd, (TableCell) ev.target, false);
						}
					}
					return false;
				}
			});

			return;
		}

		this.selector.mousedown(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				nx.app.setActiveSheet(that);
				// trigger first the blur event that is canceled by "return false" from this function
				if (that.focusElement != null) {
					$(that.focusElement).blur();
				}
				that.selectionRange(that.selection.start.row, that.selection.start.col, that.selection.end.row,
						that.selection.end.col, false, false);
				return false;
			}
		});

		/** double click puts up directly the edit text */
		this.selector.dblclick(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.showEditor();
				return false;
			}
		});

		// column headers
		$("tr.labels th", this.fixedRowsTable).live("mousemove", new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				if (that.resizingCol) {
					return false;
				}

				NetxiliaJQuery $th = $(THIS);
				Position pos = $th.offset();
				Position parentPos = that.colResizer.parent().offset();
				int w = $th.outerWidth();
				int d = 0;

				if (Math.abs(pos.left - ev.pageX) < 20) {
					d = 0;
				} else if (Math.abs(pos.left + w - ev.pageX) < 20) {
					d = 1;
				} else {
					that.colResizer.hide();
					return false;
				}
				int h = $th.outerHeight();
				// this is the column that has its right edge moving
				that.resizedCol = ((TableCell) THIS).cellIndex - 2 + d;

				that.colResizer.css($map("left", pos.left + d * w - 5 - parentPos.left, "height", h));
				// that.colResizer.draggable('option', 'containment', [0, pos.top - 1, 10000, pos.top + h + 1]);
				that.colResizer.show();
				return false;
			}
		}).live("mousedown", new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				nx.app.setActiveSheet(that);
				// TODO should use full column selectors. ex: C:C
				int c = that.colIndex(((TableCell) THIS).cellIndex, true);
				that.selectionRange(that.minRow, c, that.maxRow, c, false, true);
				return false;
			}
		});

		this.colResizer.bind("dragstart", new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.resizingCol = true;
				that.resizerStart = ev.pageX;
				return false;
			}
		});

		this.colResizer.bind("dragstop", new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				int cw = $(".cw td:eq(" + that.resizedCol + ")", that.table).width();
				nx.resources.columns.modify(that.desc.workbook, that.desc.name, that.resizedCol, cw
						+ (ev.pageX - that.resizerStart), null);
				that.placeSelectors();
				that.resizedCol = -1;
				that.resizingCol = false;
				that.colResizer.hide();
				return false;
			}
		});

		this.selector.mousedown(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.mouseDown = true;
				return false;
			}
		});

		this.replicator.mousedown(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.replicatorDown = true;
				return false;
			}
		});

		$(this.cellContainer).mouseup(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				if (that.replicatorDown) {
					nx.app.replicate();
				}
				that.mouseDown = false;
				that.replicatorDown = false;
				return false;
			}
		});

		$(".pager", this.container).click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				if (ev.target.tagName.toLowerCase() == "td") {
					that.viewPage(((TableRow) ev.target.parentNode).rowIndex - 1, false);
				}
				return false;
			}
		});

		this.aliasName.add(this.colAliasNames).focus(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.focusElement = THIS;
				return false;
			}
		});
		this.aliasName.blur(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.focusElement = null;
				String oldAlias = that.aliases.$get(that.aliasRef.text());
				String newAlias = (String) $(THIS).val();

				that.changeAlias(oldAlias, newAlias, that.aliasRef.text());
				that.focusSelectionContent();
				return false;
			}
		});
		this.colAliasNames.blur(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.focusElement = null;
				String colLabel = THIS.id.substring("alias-".length());
				String ref = colLabel + ":" + colLabel;
				String oldAlias = that.aliases.$get(ref);
				String newAlias = (String) $(THIS).val();
				that.changeAlias(oldAlias, newAlias, ref);
				that.focusSelectionContent();
				return false;
			}
		});

		this.container.bind("nxtablebodyScroll", new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.placeSelectors();
				nx.app.onScroll(that);
				return false;
			}
		});

	}

	/**
	 * handlers for keyboard interaction
	 */
	private void handleKeyboard() {
		final Sheet controller = this;
		Shortcuts sh = new Shortcuts();
		sh.addSimple("ctrl+alt+S", new Callback0() {
			@Override
			public void $invoke() {
				nx.app.sort();
			}
		});
		sh.addPropagate("ctrl+c", new Callback0() {
			@Override
			public void $invoke() {
				nx.app.cbCopy(false);
			}
		});
		sh.addPropagate("ctrl+x", new Callback0() {
			@Override
			public void $invoke() {
				nx.app.cbCut(false);
			}
		});
		sh.addPropagate("ctrl+v", new Callback0() {
			@Override
			public void $invoke() {
				setTimeout(new Callback0() {
					public void $invoke() {
						nx.app.cbPaste(false);
					}
				}, 50);
			}
		});

		sh.addSimple("ctrl+z", new Callback0() {
			@Override
			public void $invoke() {
				nx.app.undo();
			}
		});
		sh.addSimple("ctrl+y", new Callback0() {
			@Override
			public void $invoke() {
				nx.app.redo();
			}
		});

		// cell movements
		this.moveMode(sh, "up", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelection(0, -1, false);
			}
		});
		this.moveMode(sh, "down", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelection(0, 1, false);
			}
		});
		this.moveMode(sh, "right", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelection(1, 0, false);
			}
		});
		this.moveMode(sh, "left", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelection(-1, 0, false);
			}
		});

		this.moveMode(sh, "home", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionToLimits(false, true, false, false);
			}
		});
		this.moveMode(sh, "end", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionToLimits(false, false, false, true);
			}
		});
		this.moveMode(sh, "ctrl+home", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionToLimits(true, true, false, false);
			}
		});
		this.moveMode(sh, "ctrl+end", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionToLimits(false, true, true, false);
			}
		});

		this.moveMode(sh, "pagedown", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionPage("down");
			}
		});
		this.moveMode(sh, "pageup", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelectionPage("up");
			}
		});

		sh.addSimple("enter", new Callback0() {
			@Override
			public void $invoke() {
				if (nx.app.autoInsertRow) {
					nx.app.insertRow(true);
				} else {
					controller.moveSelection(0, 1, true);
				}
			}
		});// same as down
		sh.addSimple("tab", new Callback0() {
			@Override
			public void $invoke() {
				controller.moveSelection(1, 0, true);
			}
		});// moves right

		this.moveMode(sh, "delete", new Callback0() {
			@Override
			public void $invoke() {
				controller.clearCells();
			}
		});
		sh.addSimple("escape", new Callback0() {
			@Override
			public void $invoke() {
				controller.cancelEdit();
			}
		});
		sh.addSimple("f2", new Callback0() {
			@Override
			public void $invoke() {
				controller.selector.dblclick();
			}
		});

		sh.addSimple("f7", new Callback0() {
			@Override
			public void $invoke() {
				controller.toggleFilter(false);
			}
		});
		sh.addSimple("ctrl+f7", new Callback0() {
			@Override
			public void $invoke() {
				controller.toggleFilter(true);
			}
		});
		sh.addSimple("f5", new Callback0() {
			@Override
			public void $invoke() {
				window.location.reload();
			}
		});

		sh.addDefault(new Function1<Event, Boolean>() {
			@Override
			public Boolean $invoke(Event ev) {
				if (!controller.editMode()) {
					if (ev.which < 32 || ev.ctrlKey) {
						return true;
					}
					controller.waitForKeypress = true;
					return false;
				}
				return true;
			}
		}, true);

		this.shortcuts = sh;
	}

	/**
	 * initializing the sheet
	 */
	public Sheet init(SheetDescription desc, NetxiliaJQuery container) {
		this.container = container;

		this.desc = desc;
		return this;
	}

	/**
	 * called when the sheet's frame was loaded. it calculates the size of the sheet's table container
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void frameLoaded(NetxiliaJQuery frmWindow, int pageCount, SheetDescription sheetData) {
		final Sheet that = this;
		this.layout = $(".threeColumnFixed", this.container).threeColumn();

		this.pageNo = 0;
		this.pageCount = pageCount;
		this.buildPager();
		this.viewPage(0, true);
		// alias on the server are alias -> ref. on the client side are store ref->alias
		if (sheetData != null) {
			this.aliases = $or((Map) nx.utils.reverseMap(sheetData.aliases), $map());
			this.charts = sheetData.charts;
			this.spans = sheetData.spans;
		}

		this.windows = $map();
		this.nxtable = this.container.nxtable();

		this.cellContainer = $(".cellsDiv", this.container);
		this.fixedRowsTable = $(".fixedRows", this.container);

		this.selector = $("<div id='selector'></div>").appendTo(this.cellContainer);
		this.formulaTip = $("<div id='formulaTip'></div>").appendTo(this.cellContainer);
		this.selectedArea = $("<div id='selectedArea'></div>").appendTo(this.cellContainer);
		this.replicator = $("<div id='replicator'></div>").appendTo(this.cellContainer);
		this.rowResizer = $("<div id='rowResizer'></div>").appendTo(this.container);
		this.colResizer = $("<div id='colResizer'></div>").appendTo(this.fixedRowsTable.parent());
		this.colResizer.draggable(new DraggableOptions<NetxiliaJQuery>() {
			{
				axis = "x";
			}
		});
		this.selectionContent = $("<textarea id='selectionContent' autocapitalize='off'></textarea>").appendTo(
				this.cellContainer);
		this.aliasRef = $("tr.labels th.ref", this.fixedRowsTable);
		this.aliasName = $("tr.aliases th.alias input", this.fixedRowsTable);
		this.colAliasNames = $("tr.aliases th input", this.fixedRowsTable).not("th.alias input");

		this.initTable();

		this.editingContext = new EditingContext(this);
		this.editingContext.valueChanged = new Callback1<String>() {
			@Override
			public void $invoke(String value) {
				if (that.captureSelection == null) {
					that.captureSelection = new CellRange(that);
					that.captureSelection.setRange(that.selection.start, null, false, false, false);
				}
				that.markFormulaCells(value);
			}
		};
		this.handleMouse(false);
		this.handleMouse(true);
		this.handleKeyboard();

		$(".chart", this.container).draggable(new DraggableOptions<NetxiliaJQuery>() {
			{
				stop = new UIEventHandler<DraggableUI<NetxiliaJQuery>>() {
					@Override
					public boolean onEvent(Event ev, DraggableUI<NetxiliaJQuery> ui, Element THIS) {
						that.chartMoved(THIS);
						return false;
					}
				};
			}
		});
		$(".chart", this.container).resizable(new ResizableOptions<NetxiliaJQuery>() {
			{
				stop = new UIEventHandler<ResizeableUI<NetxiliaJQuery>>() {
					@Override
					public boolean onEvent(Event ev, ResizeableUI<NetxiliaJQuery> ui, Element THIS) {
						that.chartMoved(THIS);
						return false;
					}
				};
			}
		});

		this.initSelection();

		this.loaded = true;
		// this.displayAliases();

		nx.app.onFrameLoaded(this);
	}

	private void initTable() {
		final Sheet that = this;
		this.table = $(".cells", this.cellContainer);
		this.rows = $("tbody tr", this.table);

		this.mapTrToRow = $map();
		this.mapRowToTr = $map();

		this.minRow = 1000000;
		this.maxRow = 0;
		this.firstTr = 1;
		this.lastTr = this.rows.size() - 1;

		$("tbody th", this.table).each(new Callback2<Integer, Element>() {

			@Override
			public void $invoke(Integer idx, Element elm) {
				if (idx == 0) {
					return;
				}
				int rowId = parseInt($(this).text()) - 1;
				that.mapTrToRow.$put(idx + that.firstTr - 1, rowId);
				that.mapRowToTr.$put(rowId, idx + that.firstTr - 1);
				that.maxRow = Math.max(that.maxRow, rowId);
				that.minRow = Math.min(that.minRow, rowId);
			}
		});

	}

	private void initSelection() {
		if (this.columnCount() > 0) {
			if (this.selection != null) {
				// already a selection
				this.selectionRange(this.selection.start.row, this.selection.start.col, this.selection.end.row,
						this.selection.end.col, false, false);
			} else {
				this.selection = new CellRange(this);
				this.selectionRangeTd(this.cell(this.rowIndex(1, true), this.colIndex(1, true)).td, null, false);
			}
		}
	}

}
