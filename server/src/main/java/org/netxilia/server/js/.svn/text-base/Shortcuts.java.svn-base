package org.netxilia.server.js;

import static org.stjs.javascript.Global.$castArray;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.JSStringAdapter.charCodeAt;

import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Function1;
import org.stjs.javascript.jquery.Event;

public class Shortcuts {
	private static final int CTRL = 1;
	private static final int SHIFT = 2;
	private static final int ALT = 4;
	private static final int META = 8;

	private static final Map<String, Integer> meta = $map("ctrl", CTRL, "shift", SHIFT, "alt", ALT, "meta", META);

	// Special Keys - and their codes
	private static final Map<String, Integer> special_keys = $map("esc", 27, "escape", 27, "tab", 9, "space", 32,
			"return", 13, "enter", 13, "backspace", 8,

			"scrolllock", 145, "scroll_lock", 145, "scroll", 145, "capslock", 20, "caps_lock", 20, "caps", 20,
			"numlock", 144, "num_lock", 144, "num", 144,

			"pause", 19, "break", 19,

			"insert", 45, "home", 36, "delete", 46, "end", 35,

			"pageup", 33, "page_up", 33, "pu", 33,

			"pagedown", 34, "page_down", 34, "pd", 34,

			"left", 37, "up", 38, "right", 39, "down", 40,

			"f1", 112, "f2", 113, "f3", 114, "f4", 115, "f5", 116, "f6", 117, "f7", 118, "f8", 119, "f9", 120, "f10",
			121, "f11", 122, "f12", 123);
	private Map<Integer, Handler> handlers;
	private Handler defaultHandler;

	public Shortcuts() {
		this.handlers = $map();
		this.defaultHandler = null;
	}

	public void addPropagate(String combination, final Callback0 handler) {
		addInternal(combination, handler, true);
	}

	public void addSimple(String combination, final Callback0 handler) {
		addInternal(combination, handler, false);
	}

	private void addInternal(String combination, final Callback0 handler, boolean propagate) {
		add(combination, new Function1<Event, Boolean>() {
			@Override
			public Boolean $invoke(Event p1) {
				handler.$invoke();
				return false;
			}
		}, propagate);
	}

	public void add(String combination, Function1<Event, Boolean> handler, boolean propagate) {
		Array<String> keys = $castArray(combination.toLowerCase().split("+"));
		int metaCode = 0, code = 0;
		for (int k : keys) {
			String key = keys.$get(k);
			if (meta.$get(key) != null) {
				metaCode += meta.$get(key);
			} else if (special_keys.$get(key) != null) {
				code = special_keys.$get(key);
			} else {
				code = charCodeAt(key.toUpperCase(), 0);
			}// the upper case letter
		}

		Handler h = new Handler(handler, propagate, combination, metaCode * 1000 + code);
		this.handlers.$put(metaCode * 1000 + code, h);
	}

	public void addDefault(Function1<Event, Boolean> handler, boolean propagate) {
		this.defaultHandler = new Handler(handler, propagate, null, 0);
	}

	public boolean handleEvent(Event e) {
		// if (e.keyCode == 17) {
		// e.preventDefault();
		// return;
		// }

		int code = e.keyCode;
		int metaCode = 0;
		if (e.ctrlKey) {
			metaCode += CTRL;
		}
		if (e.shiftKey) {
			metaCode += SHIFT;
		}
		if (e.altKey) {
			metaCode += ALT;
		}
		// if(e.metaKey) metaCode += mask.META;

		Handler h = this.handlers.$get(metaCode * 1000 + code);
		if (h == null) {
			h = this.defaultHandler;
		}
		if (h != null) {
			boolean ret = h.handler.$invoke(e);
			if (!ret && !h.propagate) {
				e.preventDefault();
			}
			return h.propagate && ret;
		}
		return true;
	}

	private class Handler {
		public final Function1<Event, Boolean> handler;
		public final boolean propagate;
		public final String combination;
		public final int code;

		public Handler(Function1<Event, Boolean> handler, boolean propagate, String combination, int code) {
			this.handler = handler;
			this.propagate = propagate;
			this.combination = combination;
			this.code = code;
		}

	}

}
