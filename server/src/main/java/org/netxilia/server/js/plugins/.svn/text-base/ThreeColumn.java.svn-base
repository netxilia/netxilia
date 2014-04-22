package org.netxilia.server.js.plugins;

import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.jquery.GlobalJQuery.$;

import org.stjs.javascript.Map;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.JQueryAndPlugins;
import org.stjs.javascript.jquery.impl.UIMousePlugin;

public class ThreeColumn<FullJQuery extends JQueryAndPlugins<?>> extends UIMousePlugin<FullJQuery> {
	private FullJQuery element;
	private FullJQuery colmid;

	private FullJQuery colleft;
	private FullJQuery col1wrap;
	private FullJQuery col1;
	private FullJQuery col2;
	private FullJQuery col3;
	private FullJQuery handleRight;
	private FullJQuery handleLeft;

	private ThreeColumnOptions options;
	private int position;
	private FullJQuery resizeMask;

	protected Map<String, Object> plugins;
	private Callback1<Integer> incrementer;

	@Override
	@SuppressWarnings("unchecked")
	protected void _init() {
		plugins = $map();
		// o = this.options;
		this.colmid = (FullJQuery) $("> .colmid", this.element);
		this.colleft = (FullJQuery) $("> .colleft", this.colmid);
		this.col1wrap = (FullJQuery) $("> .col1wrap", this.colleft);
		this.col1 = (FullJQuery) $("> .col1", this.col1wrap);
		this.col2 = (FullJQuery) $("> .col2", this.colleft);
		this.col3 = (FullJQuery) $("> .col3", this.colleft);

		this.handleRight = this.handleLeft = null;
		if (options.resizeRight) {
			this.handleRight = (FullJQuery) $(
					"<div class='ui-resizable-handle ui-resizable-w' unselectable='on' style='position:absolute'></div>")
					.appendTo(this.col3);
		}
		if (options.resizeLeft) {
			this.handleLeft = (FullJQuery) $(
					"<div class='ui-resizable-handle ui-resizable-e' unselectable='on' style='position:absolute'></div>")
					.appendTo(this.col2);
		}

		// Initialize the mouse interaction
		this._mouseInit();
	}

	private int _pixels(FullJQuery $elem, String property) {
		String crt = (String) $elem.css(property);
		int px = 0;
		if (crt != null) {
			px = parseInt(crt.substring(0, crt.length() - 2));
		}
		return px;
	}

	private void _incr(FullJQuery $elem, String property, int diff) {
		$elem.css(property, (this._pixels($elem, property) + diff) + "px");
	}

	protected void incrRight(int diff) {
		this._incr(this.colmid, "margin-left", diff);
		this._incr(this.colleft, "left", -diff);
		this._incr(this.col1, "margin-right", -diff);
		this._incr(this.col3, "width", -diff);
	}

	protected void incrLeft(int diff) {
		this._incr(this.colleft, "left", diff);
		this._incr(this.col1wrap, "right", diff);
		this._incr(this.col1, "margin-left", diff);
		this._incr(this.col2, "width", diff);
	}

	protected void left(int x) {
		int oldVal = this._pixels(this.col2, "width");
		if (oldVal == x) {
			return;
		}
		this.incrLeft(-oldVal + x);
	}

	protected void right(int x) {
		int oldVal = this._pixels(this.col3, "width");
		if (oldVal == x) {
			return;
		}
		this.incrRight(oldVal - x);
	}

	@Override
	protected boolean _mouseCapture(Event event) {
		return this.options.disabled || this.handleLeft != null && this.handleLeft.get(0) == event.target
				|| this.handleRight != null && this.handleRight.get(0) == event.target;

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void _mouseStart(Event event) {
		final ThreeColumn<FullJQuery> that = this;
		this.position = event.pageX;
		if (this.handleLeft != null && this.handleLeft.get(0) == event.target) {
			this.incrementer = new Callback1<Integer>() {
				public void $invoke(Integer i) {
					that.incrLeft(i);
				}
			};
		} else if (this.handleRight != null && this.handleRight.get(0) == event.target) {
			this.incrementer = new Callback1<Integer>() {
				public void $invoke(Integer i) {
					that.incrRight(i);
				}
			};
		}
		this.resizeMask = (FullJQuery) $("<div class='ui-resizable-mask'></div>").appendTo(this.col1);
	}

	@Override
	protected void _mouseDrag(Event event) {
		this.incrementer.$invoke(event.pageX - this.position);
		this.position = event.pageX;
	}

	@Override
	protected void _mouseStop(Event event) {
		this.position = 0;
		if (this.resizeMask != null) {
			this.resizeMask.remove();
			this.resizeMask = null;
		}
	}

	protected ThreeColumnUI<FullJQuery> ui() {
		final ThreeColumn<FullJQuery> that = this;
		return new ThreeColumnUI<FullJQuery>() {
			{
				element = that.element;
				helper = that.helper;
				position = that.position;
			}
		};
	}

	public static void main(String[] args) {
		// $.widget("nx.threeColumn", $.extend($map(), $.ui.mouse, new ThreeColumn<JQueryAndPlugins<?>>()));
		$.widget("nx.threeColumn", new ThreeColumn<JQueryAndPlugins<?>>());
		// $.extend($.nx.splitter, $map("version", "1.7.2", "eventPrefix", "drag", "defaults", new ThreeColumnOptions()
		// {
		// {
		// resizeRight = false;
		// resizeLeft = false;
		// distance = 1;
		// }
		// }));
	}

}
