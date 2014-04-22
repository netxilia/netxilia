package org.netxilia.server.js.editors;

import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$map;

import org.netxilia.server.js.Bounds2;
import org.netxilia.server.js.Cell;
import org.netxilia.server.js.CellRange;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Map;

public class DateEditor implements Editor {

	private Map<String, Object> params;
	private NetxiliaJQuery editorElement;
	private EditingContext context;

	public DateEditor(EditingContext context, Map<String, Object> params) {
		this.params = params;
		this.context = context;
	}

	@Override
	public void setCaptureSelection(CellRange selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public String value() {
		return this.editorElement != null ? (String) this.editorElement.val() : null;
	}

	@Override
	public void show(boolean show) {
		if (show) {
			this.editorElement.show();
			this.editorElement.focus();
			this.editorElement.trigger("click");
			// this.editorElement.datepicker('show');
		} else {
			this.editorElement.hide();
			// this.editorElement.datepicker('hide');
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void edit(Cell cell, Bounds2 pos, String value) {
		this.editorElement = this.context.elements.$get("date-editor");
		if (this.editorElement == null) {
			this.editorElement = $("<input type='text' id='date-editor' class='editor-visible-index'>").appendTo(
					this.context.container);
			this.context.elements.$put("date-editor", this.editorElement);
			this.editorElement.simpleDatepicker((Map) $map("startdate", 1970, "enddate", 2020));
		}
		this.editorElement.css((Map) $map("top", pos.top, "left", pos.left, "width", pos.width + 4, "height",
				pos.height));

		if (value != null) {
			this.editorElement.val(value);
		} else {
			this.editorElement.val(cell.getValue());
		}

	}
}
