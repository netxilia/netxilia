package org.netxilia.server.js;

import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$castArray;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.encodeURIComponent;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.JSStringAdapter.charCodeAt;
import static org.stjs.javascript.JSStringAdapter.fromCharCode;
import static org.stjs.javascript.JSStringAdapter.replace;

import org.netxilia.server.js.data.JsAreaReference;
import org.netxilia.server.js.data.JsCellReference;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.Reference;
import org.stjs.javascript.RegExp;
import org.stjs.javascript.RegExpMatch;
import org.stjs.javascript.functions.Function1;
import org.stjs.javascript.jquery.Position;

public class Utils {
	public static RegExp regexRef = new RegExp("(?:([\\w.]+)!)?([$]?)([A-Za-z]+)([$]?)([0-9]+)");
	public static RegExp regexAreaOrRef = new RegExp(regexRef.source + "(:" + regexRef.source + ")?");

	public static class StartEnd {
		public int start;
		public int end;

		public StartEnd(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}

	public void positionUnder(String src, String target) {
		NetxiliaJQuery $src = $(src);
		Position pos = $src.offset();
		Position parentPos = $src.offsetParent().offset();
		int h = $src.outerHeight();
		$(target).css($map("top", pos.top - parentPos.top + h, "left", pos.left - parentPos.left));
	}

	/**
	 * 
	 * @param spec
	 *            is an array of any of 'f', 'm', 'l' (for 'first', 'middle', 'last')
	 * @param start
	 * @param end
	 * @return tests console.info(intervals(['f'], 1, 2)); console.info(intervals(['f'], 1, 1));
	 *         console.info(intervals(['f', 'm'], 1, 2)); console.info(intervals(['f', 'l'], 1, 2));
	 *         console.info(intervals(['f', 'l', 'm'], 1, 2));
	 */
	public Array<StartEnd> intervals(Array<String> spec, final int s, final int e) {
		Array<StartEnd> r = $array();

		if ($.inArray("f", spec) >= 0 && s != 0) {
			r.push(new StartEnd(s - 1, e - 1));
		}
		if ($.inArray("m", spec) >= 0 && e - 1 >= s) {
			r.push(new StartEnd(s, e - 1));
		}
		if ($.inArray("l", spec) >= 0) {
			r.push(new StartEnd(s, e));
		}
		// collapse intervals
		for (int i = r.$length() - 1; i > 0; --i) {
			if (r.$get(i).start == r.$get(i - 1).end + 1) {
				r.$set(i - 1, new StartEnd(r.$get(i - 1).start, r.$get(i).end));
				r.splice(i, 1);
			}
		}
		return r;
	}

	public JsAreaReference parseAreaReference(String areaRef) {
		final Array<String> refs = $castArray(areaRef.split(":"));
		if (refs.$length() != 2) {
			return null;
		}
		return new JsAreaReference(parseCellReference(refs.$get(0)), parseCellReference(refs.$get(1)));
	}

	public JsCellReference parseCellReference(String ref) {
		RegExpMatch m = regexRef.exec(ref);
		if (m == null) {
			return null;
		}

		return new JsCellReference(m.$get(1), parseInt(m.$get(5)) - 1, columnLabelIndex(m.$get(3)));
	}

	public Array<JsAreaReference> findReferencesInFormula(String formula) {
		RegExpMatch m = regexAreaOrRef.exec(formula);
		Array<JsAreaReference> refs = $array();
		String crtText = formula;
		while (m != null) {
			JsCellReference tl = new JsCellReference(m.$get(1), parseInt(m.$get(5)) - 1, columnLabelIndex(m.$get(3)));
			JsCellReference br = tl;
			if (m.$get(9) != null) {
				br = new JsCellReference(m.$get(7), parseInt(m.$get(11)) - 1, columnLabelIndex(m.$get(9)));
				refs.push(new JsAreaReference(tl, br));
				crtText = crtText.substring(m.index + m.$get(0).length());
				m = regexAreaOrRef.exec(crtText);
			}
		}
		return refs;
	}

	public int columnLabelIndex(String str) {
		// Converts A to 1, B to 2, Z to 26, AA to 27.
		int num = 0;
		str = str.toUpperCase();
		for (int i = 0; i < str.length(); i++) {
			int digit = charCodeAt(str, i) - 65; // 65 == 'A'.
			num = (num * 26) + digit;
		}
		return num;
	}

	public String columnLabel(int col) {
		// TODO convert to more than one-letter code
		return fromCharCode(String.class, 65 + col);
	}

	@SuppressWarnings("unchecked")
	public boolean isEmptyObject(Object obj) {
		for (String name : (Map<String, Object>) obj) {
			return false;
		}
		return true;
	}

	public Map<String, String> reverseMap(Map<String, String> map) {
		if (map == null) {
			return null;
		}
		Map<String, String> newMap = $map();
		for (String f : map) {
			newMap.$put(map.$get(f), f);
		}
		return newMap;
	}

	public Diff<String> diff(final Array<String> oldList, final Array<String> newList) {
		if (oldList == null) {
			return new Diff<String>() {
				{
					added = newList;
					deleted = $array();
				}
			};
		}
		if (newList == null) {
			return new Diff<String>() {
				{
					added = $array();
					deleted = newList;
				}
			};
		}

		Diff<String> ret = new Diff<String>() {
			{
				added = $array();
				deleted = $array();
			}
		};
		for (int i : oldList) {
			if ($.inArray(oldList.$get(i), newList) < 0) {
				ret.deleted.push(oldList.$get(i));
			}
		}
		for (int i : newList) {
			if ($.inArray(newList.$get(i), oldList) < 0) {
				ret.added.push(newList.$get(i));
			}
		}
		return ret;
	};

	public String url(Object... arguments) {
		String url = (String) arguments[0];
		final Reference<Integer> p = new Reference<Integer>() {
			{
				value = 0;
			}
		};

		final Array<Object> params = $castArray(arguments);
		return replace(url, new RegExp("\\{\\}", "g"), new Function1<String, String>() {
			public String $invoke(String m) {
				p.value++;
				return encodeURIComponent((String) params.$get(p.value));
			}
		});
	}

}
