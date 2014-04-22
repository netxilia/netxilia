package org.netxilia.server.js.editors;

import static org.netxilia.server.js.NX.nx;
import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.setTimeout;

import org.netxilia.server.js.Bounds2;
import org.netxilia.server.js.Cell;
import org.netxilia.server.js.CellRange;
import org.netxilia.server.jslib.CaretPosition;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.RegExp;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.EventHandler;

public class TextEditor implements Editor {
	private Map<String, Object> params;
	private NetxiliaJQuery editorElement;
	private EditingContext context;

	public TextEditor(EditingContext context, Map<String, Object> params) {
		this.params = params;
		this.context = context;
	}

	@Override
	public String value() {
		return this.editorElement != null ? (String) this.editorElement.val() : null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void edit(Cell cell, Bounds2 pos, String value) {
		final TextEditor that = this;
		this.editorElement = this.context.elements.$get("default-editor");
		if (this.editorElement == null) {
			this.editorElement = $(
					"<textarea id='default-editor' autocapitalize='off' class='editor-visible-index'></textarea>")
					.appendTo(this.context.container);
			// enlarge editor as needed
			this.editorElement.keydown(new EventHandler() {

				@Override
				public boolean onEvent(Event ev, Element THIS) {
					if (that.context.valueChanged != null && ev.keyCode != 13) {
						final NetxiliaJQuery $elm = $(THIS);
						setTimeout(new Callback0() {
							@Override
							public void $invoke() {
								that.context.valueChanged.$invoke((String) $elm.val());
							}
						}, 1);
					}
					if (ev.keyCode < 32) {
						return true;
					}
					if ($(THIS).height() < THIS.scrollHeight) {
						$(THIS).height(THIS.scrollHeight);
					}
					return false;
				}
			});

			this.context.elements.$put("default-editor", this.editorElement);
		}
		int pwidth = this.editorElement.parent().width();
		this.editorElement.css($map("top", pos.top, "left", pos.left, "width",
				org.stjs.javascript.Math.min(pos.width + 150, pwidth - pos.left), "height", pos.height));
		this.editorElement.attr("minHeight", "" + pos.height);

		// cell->editor
		if (value != null) {
			this.editorElement.val(value);
		} else {
			this.editorElement.val(cell.getValue());
		}

		setTimeout(new Callback0() {
			@Override
			public void $invoke() {
				if (that.editorElement.height() < that.editorElement.get(0).scrollHeight) {
					that.editorElement.height(that.editorElement.get(0).scrollHeight);
				}
				that.editorElement.putCursorAtEnd();
			}
		}, 1);
	}

	@Override
	public void setCaptureSelection(CellRange selection) {
		// TODO modify selection also
		int cursor = this.editorElement.caret().start;
		String v = (String) this.editorElement.val();
		String leftString = v.substring(0, cursor);
		RegExp endWithRefRegex = new RegExp(nx.utils.regexRef.source + "$");
		final int keepPos;
		Array<String> m = endWithRefRegex.exec(leftString);
		if (m != null) {
			// replace the matching part with the new reference
			this.editorElement.val(v.substring(0, cursor - m.$get(0).length()) + selection.start.ref(false)
					+ v.substring(cursor));
			keepPos = cursor - m.$get(0).length() + selection.start.ref(false).length();
		} else {
			// add the new reference
			this.editorElement.val(v.substring(0, cursor) + selection.start.ref(false) + v.substring(cursor));
			keepPos = cursor + selection.start.ref(false).length();
		}

		this.editorElement.caret(new CaretPosition() {
			{
				start = keepPos;
				end = keepPos;
			}
		});
		if (this.context.valueChanged != null) {
			this.context.valueChanged.$invoke((String) this.editorElement.val());
		}
	}

	@Override
	public void show(boolean show) {
		if (show) {
			this.editorElement.show();
			this.editorElement.focus();

		} else {
			this.editorElement.hide();
		}
	}
}
