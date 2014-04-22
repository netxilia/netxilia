package org.netxilia.server.js.plugins;

import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback1;

public class NetxiliaDialogOptions {
	public boolean modal;
	public int height;
	public int width;
	public boolean closable;
	public Map<String, ? extends Callback1<Element>> buttons;
}
