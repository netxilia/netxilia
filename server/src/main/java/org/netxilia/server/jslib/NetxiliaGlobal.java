package org.netxilia.server.jslib;

import org.netxilia.server.js.NX;
import org.stjs.javascript.Global;
import org.stjs.javascript.annotation.GlobalScope;
import org.stjs.javascript.dom.Element;

@GlobalScope
public class NetxiliaGlobal extends Global {
	public static NetxiliaGlobalJQuery $;

	/**
	 * jquery constructors
	 */
	public static NetxiliaJQuery $(String path) {
		return null;
	}

	public static NetxiliaJQuery $(Object path) {
		return null;
	}

	public static NetxiliaJQuery $(Object path, Element context) {
		return null;
	}

	public static NetxiliaJQuery $(Object path, NetxiliaJQuery context) {
		return null;
	}

	public static NX nx;
}
