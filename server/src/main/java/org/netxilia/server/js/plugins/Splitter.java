package org.netxilia.server.js.plugins;

import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.jquery.GlobalJQuery.$;

import org.stjs.javascript.Map;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.JQueryAndPlugins;
import org.stjs.javascript.jquery.impl.UIMousePlugin;

public class Splitter<FullJQuery extends JQueryAndPlugins<?>> extends UIMousePlugin<FullJQuery> {
	private FullJQuery element;

	private FullJQuery top;
	private FullJQuery bottom;
	private FullJQuery handle;
	private int position;
	private int initialHeight;
	private FullJQuery resizeMask;
	private SplitterOptions options;
	protected Map<String, Object> plugins;

	@Override
	@SuppressWarnings("unchecked")
	protected void _init() {
		plugins = $map();
		// o = this.options;
		this.top = (FullJQuery) $(this.element.children().get(0));
		this.bottom = (FullJQuery) $(this.element.children().get(1));

		this.handle = (FullJQuery) $(
				"<div class='ui-resizable-handle ui-resizable-n' unselectable='on' style='position:absolute'></div>")
				.appendTo(this.bottom);
		// Initialize the mouse interaction
		this._mouseInit();
	}

	protected void moveDown(int pos) {
		this.top.css("bottom", pos + "px");
		this.bottom.css("height", pos + "px");
	}

	@Override
	protected boolean _mouseCapture(Event event) {
		return this.options.disabled || this.handle.get(0) == event.target;

	}

	@Override
	@SuppressWarnings("unchecked")
	protected void _mouseStart(Event event) {
		this.position = event.pageY;
		this.initialHeight = this.bottom.height();
		this.resizeMask = (FullJQuery) $("<div class='ui-resizable-mask'></div>").appendTo(this.top.add(this.bottom));
	}

	@Override
	protected void _mouseDrag(Event event) {
		this.moveDown(this.initialHeight - event.pageY + this.position);
	}

	@Override
	protected void _mouseStop(Event event) {
		this.position = 0;
		if (this.resizeMask != null) {
			this.resizeMask.remove();
			this.resizeMask = null;
		}
	}

	protected SplitterUI<FullJQuery> ui() {
		final Splitter<FullJQuery> that = this;
		return new SplitterUI<FullJQuery>() {
			{
				element = that.element;
				helper = that.helper;
				position = that.position;
			}
		};
	}

	public static void main(String[] args) {
		$.widget("nx.splitter", new Splitter<JQueryAndPlugins<?>>());
		// $.widget("nx.splitter", $.extend($map(), $.ui.mouse, new Splitter<JQueryAndPlugins<?>>()));
		// $.extend($.nx.splitter, $map("version", "1.7.2", "eventPrefix", "drag", "defaults", new SplitterOptions() {
		// {
		// distance = 1;
		// }
		// }));
	}
}
