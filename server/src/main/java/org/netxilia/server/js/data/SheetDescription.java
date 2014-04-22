package org.netxilia.server.js.data;

import org.stjs.javascript.Array;
import org.stjs.javascript.Map;

public class SheetDescription {

	public String name;
	public String workbook;
	public String context;
	public int pageSize;
	public String username;
	public Array<String> spans;
	public Array<ChartDescription> charts;
	public Map<String, String> aliases;
	public Map<String, String> editors;

}
