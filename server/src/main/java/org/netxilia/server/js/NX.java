package org.netxilia.server.js;

import org.stjs.javascript.annotation.GlobalScope;

@GlobalScope
public class NX {
	public Utils utils;
	public Application app;
	public Resources resources;
	protected Home workbook;

	public NX() {
		utils = new Utils();
		app = new Application();
		resources = new Resources();
		workbook = new Home();
	}

	public static NX nx = new NX();

}
