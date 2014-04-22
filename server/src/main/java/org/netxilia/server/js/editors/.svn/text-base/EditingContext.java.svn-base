package org.netxilia.server.js.editors;

import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.eval;

import org.netxilia.server.js.Bounds2;
import org.netxilia.server.js.Cell;
import org.netxilia.server.js.CellRange;
import org.netxilia.server.js.Sheet;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Map;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.jquery.Position;

/**
 * this is used to cache formatter instances. usually there is one per sheet. in a context there is only, one active (in
 * edit mode) formatter at any given moment
 */
public class EditingContext {
	public Editor editor;
	public boolean editorVisible = false;
	public Map<String, NetxiliaJQuery> elements;
	public boolean defaultEditor = true;
	public Callback1<String> valueChanged;
	private final Sheet sheet;
	public NetxiliaJQuery container;
	private Cell editedCell;
	private Bounds2 editorPosition;

	public EditingContext(Sheet sheet) {
		this.editorVisible = false;
		this.elements = $map();
		this.defaultEditor = true;
		this.sheet = sheet;
		this.container = sheet.table.parent();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Editor buildEditor(String editorInfo) {
		// do caching
		if (editorInfo == null || editorInfo.length() == 0 || editorInfo == "null") {
			return new TextEditor(this, (Map) $map());
		}
		Map<String, Object> params = null;
		String id = editorInfo;
		if (editorInfo.charAt(0) == '{') {
			params = eval("(" + editorInfo + ")");
			id = (String) params.$get("id");
		}
		if ("text".equals(id)) {
			return new TextEditor(this, params);
		}
		if ("date".equals(id)) {
			return new DateEditor(this, params);
		}
		if ("select".equals(id)) {
			return new SelectEditor(this, params);
		}
		// TODO should have a registry instead
		return null;
	}

	public void edit(Cell cell, String useEditorInfo, String value) {
		this.editor = this.buildEditor(useEditorInfo != null ? useEditorInfo : cell.editorInfo());

		Position pos = cell.$td.offset();
		Position containerPos = this.container.offset();
		containerPos.top -= this.container.scrollTop();
		containerPos.left -= this.container.scrollLeft();
		pos.top -= containerPos.top;
		pos.left -= containerPos.left;

		int w = cell.$td.width(), h = cell.$td.height();

		this.editedCell = cell;
		this.editorPosition = new Bounds2(pos.left, pos.top, w, h);
		this.editor.edit(cell, this.editorPosition, value);
		this.editor.show(true);
		this.defaultEditor = false;
	}

	public void cancelEdit() {
		this.editor.edit(this.editedCell, editorPosition, editedCell.getValue());
	}

	public void hide() {
		if (this.editor == null) {
			return;
		}
		this.editor.show(false);
		this.editorVisible = false;
		this.editor = null;
		this.defaultEditor = true;
		this.sheet.focusSelectionContent();
	}

	public void showDefaultEditor(Cell cell, String text) {
		this.edit(cell, "text", text);
		this.defaultEditor = true;
	}

	public boolean hasValueChanged() {
		return this.editor != null && this.editor.value() != this.editedCell.getValue();
	}

	public String value() {
		return this.editor != null ? this.editor.value() : null;
	}

	public void setCaptureSelection(CellRange captureSelection) {
		if (this.editor != null) {
			this.editor.setCaptureSelection(captureSelection);
		}
	}

};