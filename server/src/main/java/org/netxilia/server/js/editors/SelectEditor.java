package org.netxilia.server.js.editors;

import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.netxilia.server.js.NX.nx;
import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$castArray;
import static org.stjs.javascript.Global.$map;

import org.netxilia.server.js.Bounds2;
import org.netxilia.server.js.Cell;
import org.netxilia.server.js.CellRange;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.utils.NameValue;

public class SelectEditor implements Editor {

	private Map<String, Object> params;
	private NetxiliaJQuery editorElement;
	private EditingContext context;

	public SelectEditor(EditingContext context, Map<String, Object> params) {
		this.params = params;
		this.context = context;
	}

	@Override
	public void setCaptureSelection(CellRange selection) {
		// TODO Auto-generated method stub

	}

	@Override
	public String value() {
		return this.editorElement != null ? this.editorElement.selectedValuesString() : null;
	}

	@Override
	public void show(boolean show) {
		if (show) {
			this.editorElement.show();
			this.editorElement.focus();
			this.editorElement.multiSelectOptionsShow();
		} else {
			this.editorElement.hide();
			this.editorElement.multiSelectOptionsHide();
		}

	}

	@Override
	public void edit(Cell cell, Bounds2 pos, String value) {
		final SelectEditor that = this;
		this.editorElement = this.context.elements.$get("select-editor");
		if (this.editorElement == null) {
			String selectHtml = "<select id='select-editor' >";
			selectHtml += "</select>";
			this.editorElement = $(selectHtml).appendTo(this.context.container);

			this.editorElement.multiSelect($map("selectAll", false, "noneSelected", "", "oneOrMoreSelected", ""));
			this.editorElement = $("#select-editor", this.context.container);
			this.editorElement.addClass("editor-visible-index");
			this.context.elements.$put("select-editor", this.editorElement);
		}
		this.editorElement.css($map("top", pos.top, "left", pos.left, "width", pos.width, "height", pos.height));

		final String valuesString = value != null ? value : cell.getValue();
		nx.resources.formatters.getFormatValues(nx.app.desc.workbook, cell.getCss(), new Callback1<Array<NameValue>>() {

			@Override
			public void $invoke(Array<NameValue> values) {
				Array<String> selValues = $castArray(valuesString.split(","));
				Array<Option> options = $array();
				for (int v : values) {
					options.push(new Option(values.$get(v).name, values.$get(v).value));
				}
				that.select(options, selValues);
				that.editorElement.multiSelectOptionsUpdate(options);

			}
		});
		// this.editorElement.val(this.valuesString);
	}

	private void select(Array<Option> options, Array<String> values) {
		for (int o : options) {
			for (int v : values) {
				if (options.$get(o).value == values.$get(v)) {
					options.$get(o).selected = true;
				}
			}
		}
	}

}
