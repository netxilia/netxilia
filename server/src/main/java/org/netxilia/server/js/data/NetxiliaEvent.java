package org.netxilia.server.js.data;

import org.stjs.javascript.Array;
import org.stjs.javascript.Map;

public class NetxiliaEvent {

	public String type;
	public Array<EventData> data;
	public int row;
	public int column;
	public int width;
	public String selectedArea;
	public WindowInfo windowInfo;
	public Map<String, String> aliases;
	public Array<String> spans;
	public String sheetName;

}
