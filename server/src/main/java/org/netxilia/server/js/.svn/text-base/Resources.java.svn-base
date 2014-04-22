package org.netxilia.server.js;

import static org.netxilia.server.js.NX.nx;
import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.eval;

import org.netxilia.server.js.data.DataSourceConfiguration;
import org.netxilia.server.js.data.DataSourceConfigurationId;
import org.netxilia.server.js.data.NetxiliaEvent;
import org.netxilia.server.js.data.StringHolder;
import org.netxilia.server.js.data.WindowIndex;
import org.netxilia.server.js.data.WindowInfo;
import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Callback3;
import org.stjs.javascript.functions.Function0;
import org.stjs.javascript.jquery.AjaxParams;
import org.stjs.javascript.jquery.JQueryXHR;
import org.stjs.javascript.utils.NameValue;

public class Resources {
	private String restContext = "/netxilia/rest";
	public Function0<Map<String, ? extends Object>> getHeaders;
	public Callback0 disconnected;

	public Resources() {
		home = new Home();
		events = new Events();
		windows = new Windows();
		cells = new Cells();
		formatters = new Formatters();
		workbooks = new Workbooks();
		sheets = new Sheets();
		ds = new Datasources();
		rows = new Rows();
		columns = new Columns();
		charts = new Charts();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private <T> void ajax(final String paramType, final String paramUrl,
			Map<? extends Object, ? extends Object> params, final Callback1<T> callback,
			final Callback3<String, JQueryXHR, String> errorCallback, Map<? extends Object, ? extends Object> ajaxParams) {
		@SuppressWarnings({ "rawtypes", "unchecked" })
		final Map<String, Object> paramsWithMethod = params != null ? params : (Map) $map();

		// cleanup params
		for (String p : paramsWithMethod) {
			if (paramsWithMethod.$get(p) == null) {
				paramsWithMethod.$delete(p);
			}
		}

		// this is the JBOSS RESTEasy way. is this standard !?
		/*
		 * if (type=="PUT") { paramsWithMethod["_method"] = "put"; type="POST"; } else if (type=="DELETE") {
		 * paramsWithMethod["_method"] = "delete"; type="POST"; }
		 */
		final Resources that = this;
		AjaxParams ajaxCall = new AjaxParams() {
			{
				url = that.restContext + paramUrl;
				type = paramType;
				data = paramsWithMethod;
				dataType = "text";
				beforeSend = new Callback1<JQueryXHR>() {
					@Override
					public void $invoke(JQueryXHR xhr) {
						if (that.getHeaders != null) {
							Map<String, ? extends Object> headers = that.getHeaders.$invoke();
							if (headers != null) {
								for (String h : headers) {
									xhr.setRequestHeader(h, headers.$get(h));
								}
							}
							xhr.setRequestHeader("ajax", "true");
							xhr.setRequestHeader("Accept", "application/json");
						}
					}
				};
				success = new Callback3<Object, String, JQueryXHR>() {
					@Override
					public void $invoke(Object d, String status, JQueryXHR request) {
						String data = (String) d;
						if (callback != null) {
							if (data == null || data == "") {
								callback.$invoke(null);
							} else if (data.charAt(0) != '{' && data.charAt(0) != '[') {
								callback.$invoke((T) data);
							} else {
								callback.$invoke((T) eval("(" + data + ")"));
							}
						}
					}
				};
				error = new Callback3<JQueryXHR, String, String>() {
					@Override
					public void $invoke(JQueryXHR request, String textStatus, String error) {
						if (errorCallback != null) {
							errorCallback.$invoke(textStatus, request, request.getResponseHeader("nx-error"));
						}
						if (request.status == 401 && nx.resources.disconnected != null) {
							nx.resources.disconnected.$invoke();
						}
					}
				};
			}
		};
		if (ajaxParams != null) {
			Map<Object, Object> mapAjaxCall = (Map) ajaxCall;
			for (Object p : (Map) ajaxParams) {
				mapAjaxCall.$put(p, ((Map) ajaxParams).$get(p));
			}
		}

		$.ajax(ajaxCall);
	}

	<T> void get(String url, Map<? extends Object, ? extends Object> params, Callback1<T> callback,
			Callback3<String, JQueryXHR, String> errorCallback, Map<? extends Object, ? extends Object> ajaxParams) {
		this.ajax("GET", url, params, callback, errorCallback, ajaxParams);
	}

	<T> void post(String url, Map<? extends Object, ? extends Object> params, Callback1<T> callback,
			Callback3<String, JQueryXHR, String> errorCallback, Map<? extends Object, ? extends Object> ajaxParams) {
		this.ajax("POST", url, params, callback, errorCallback, ajaxParams);
	}

	<T> void put(String url, Map<? extends Object, ? extends Object> params, Callback1<T> callback,
			Callback3<String, JQueryXHR, String> errorCallback, Map<? extends Object, ? extends Object> ajaxParams) {
		this.ajax("PUT", url, params, callback, errorCallback, ajaxParams);
	}

	<T> void del(String url, Map<Object, Object> params, Callback1<T> callback,
			Callback3<String, JQueryXHR, String> errorCallback, Map<? extends Object, ? extends Object> ajaxParams) {
		this.ajax("DELETE", url, params, callback, errorCallback, ajaxParams);
	}

	// home
	public Home home;

	public class Home {
		public void treeview(Callback1<StringHolder> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.get("/home/treeview", $map(), callback, errorCallback, null);
		}
	}

	// events
	public Events events;

	public class Events {
		public void poll(Long windowId, Callback1<Array<NetxiliaEvent>> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/events/" + windowId, $map(), callback, errorCallback, null);
		}
	}

	// windows
	public Windows windows;

	public class Windows {
		public void register(String workbook, String sheet, Callback1<WindowIndex> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/windows/" + workbook + "/" + sheet, $map(), callback, errorCallback, null);
		}

		public void terminate(Long windowId, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.del("/windows/" + windowId, $map(), callback, errorCallback, $map("async", false));
		}

		public void notifySelection(Long windowId, String areaRef, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/windows/" + windowId + "/notifySelection/" + areaRef, null, callback, errorCallback,
					null);
		}

		public void getWindowsForSheet(String workbook, String sheet, Callback1<Array<WindowInfo>> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.get("/windows/" + workbook + "/" + sheet, $map(), callback, errorCallback, null);
		}

		public void undo(Long windowId, Callback1<Void> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/windows/" + windowId + "/undo", $map(), callback, errorCallback, null);
		}

		public void redo(Long windowId, Callback1<Void> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/windows/" + windowId + "/redo", $map(), callback, errorCallback, null);
		}
	}

	// cells
	public Cells cells;

	public class Cells {
		public void setValue(String workbook, String areaRef, String value, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/" + areaRef + "/value", $map("value", value), callback, null,
					null);
		}

		public void replicate(String workbook, String fromCell, String toArea, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/replicate/" + fromCell + "/" + toArea, null, callback, null,
					null);
		}

		public void paste(String workbook, String fromCell, String toCell, String value, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/paste/" + fromCell + "/" + toCell, $map("value", value),
					callback, null, null);
		}

		public void move(String workbook, String fromArea, String toCell, Callback1<Void> callback) {
			nx.resources.put("/cells/" + workbook + "/move/" + fromArea + "/" + toCell, null, callback, null, null);
		}

		public void setStyle(String workbook, String areaRef, String style, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/" + areaRef + "/style", $map("style", style), callback, null,
					null);
		}

		public void applyStyle(String workbook, String areaRef, String style, String mode, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/" + areaRef + "/style/apply",
					$map("style", style, "mode", mode), callback, null, null);
		}

		public void setFormat(String workbook, String areaRef, String format, Callback1<Void> callback) {
			nx.resources.post("/cells/" + workbook + "/" + areaRef + "/format", $map("format", format), callback, null,
					null);
		}

		public void merge(String workbook, String areaRef, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/cells/" + workbook + "/" + areaRef + "/merge", null, callback, errorCallback, null);
		}

		public void append(String workbook, String sheetName, String value, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/cells/" + workbook + "/" + sheetName + "/append", $map("value", value), callback,
					errorCallback, null);
		}

		public void find(String workbook, String sheetName, String startRef, String searchText,
				Callback1<String> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.get("/cells/" + workbook + "/" + sheetName + "/find",
					$map("startRef", startRef, "searchText", searchText), callback, errorCallback, null);
		}

	}

	// formatters
	public Formatters formatters;

	public class Formatters {
		public void getFormatValues(String workbook, String fmt, Callback1<Array<NameValue>> callback) {
			nx.resources.get(nx.utils.url("/formatters/{}/{}/formatValues", workbook, fmt), null, callback, null, null);
		}

		public void getValues(String workbook, String formatterName, Callback1<Array<NameValue>> callback) {
			nx.resources.get(nx.utils.url("/formatters/{}/{}/values", workbook, formatterName), null, callback, null,
					null);
		}

		public void setFormatter(String workbook, String formatterName, String sourceWorkbookName, String nameRef,
				String valueRef, Callback1<Void> callback) {
			nx.resources.post(nx.utils.url("/formatters/{}/{}/{}/{}/{}", workbook, formatterName, sourceWorkbookName,
					nameRef, valueRef), null, callback, null, null);
		}
	}

	// workbooks
	public Workbooks workbooks;

	public class Workbooks {
		public void newWorkbook(String workbook, String config, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/workbooks/" + workbook, $map("config", config), callback, errorCallback, null);
		}

		public void deleteWorkbook(String workbook, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.del("/workbooks/" + workbook, null, callback, errorCallback, null);
		}
	}

	// sheets
	public Sheets sheets;

	public class Sheets {
		public void newSheet(String workbook, String name, Callback1<String> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/sheets/" + workbook + "/" + name, null, callback, errorCallback, null);
		}

		public void del(String workbook, String name, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.del("/sheets/" + workbook + "/" + name, null, callback, errorCallback, null);
		}

		public void sort(String workbook, String name, String sortSpec, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/sheets/" + workbook + "/" + name + "/sort/" + sortSpec, null, callback, errorCallback,
					null);
		}

		public void setAlias(String workbook, String name, String aliasName, String ref, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/sheets/" + workbook + "/" + name + "/alias/" + aliasName + "/" + ref, null, callback,
					errorCallback, null);
		}

		public void deleteAlias(String workbook, String name, String aliasName, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.del("/sheets/" + workbook + "/" + name + "/alias/" + aliasName, null, callback, errorCallback,
					null);
		}

	}

	// rows
	public Rows rows;

	public class Rows {
		public void insert(String workbook, String sheetName, int pos, Callback1<Void> callback) {
			nx.resources.put("/rows/" + workbook + "/" + sheetName + "/" + pos, null, callback, null, null);
		}

		public void del(String workbook, String sheetName, int pos, Callback1<Void> callback) {
			nx.resources.del("/rows/" + workbook + "/" + sheetName + "/" + pos, null, callback, null, null);
		}
	}

	// columns
	public Columns columns;

	public class Columns {
		public void insert(String workbook, String sheetName, int pos, Callback1<Void> callback) {
			nx.resources.put("/columns/" + workbook + "/" + sheetName + "/" + pos, null, callback, null, null);
		}

		public void del(String workbook, String sheetName, int pos, Callback1<Void> callback) {
			nx.resources.del("/columns/" + workbook + "/" + sheetName + "/" + pos, null, callback, null, null);
		}

		public void modify(String workbook, String sheetName, int pos, int width, Callback1<Void> callback) {
			nx.resources.post("/columns/" + workbook + "/" + sheetName + "/" + pos, $map("width", width), callback,
					null, null);
		}

	}

	// charts
	public Charts charts;

	public class Charts {
		public void add(String workbook, String sheetName, String areaRef, String title, String type,
				Callback1<Void> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.put("/charts/" + workbook + "/" + sheetName,
					$map("areaRef", areaRef, "title", title, "type", type), callback, errorCallback, null);
		}

		public void set(String workbook, String sheetName, int index, String areaRef, String title, String type,
				Callback1<Void> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/charts/" + workbook + "/" + sheetName + "/" + index,
					$map("areaRef", areaRef, "title", title, "type", type), callback, errorCallback, null);
		}

		public void del(String workbook, String sheetName, int index, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources
					.del("/charts/" + workbook + "/" + sheetName + "/" + index, null, callback, errorCallback, null);
		}

		public void move(String workbook, String sheetName, int index, int left, int top, int width, int height,
				Callback1<Void> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/charts/" + workbook + "/" + sheetName + "/" + index + "/move",
					$map("left", left, "top", top, "width", width, "height", height), callback, errorCallback, null);
		}
	}

	// data sources
	public Datasources ds;

	public class Datasources {
		public void list(Callback1<Array<DataSourceConfiguration>> callback) {
			nx.resources.get("/ds", null, callback, null, null);
		}

		public void add(String name, String description, String driver, String url, String username, String password,
				Callback1<DataSourceConfigurationId> callback) {
			nx.resources.put(
					"/ds",
					$map("name", name, "description", description, "driver", driver, "url", url, "username", username,
							"password", password), callback, null, null);
		}

		public void save(long id, String name, String description, String driver, String url, String username,
				String password, Callback1<Void> callback) {
			nx.resources.post(
					"/ds/" + id,
					$map("name", name, "description", description, "driver", driver, "url", url, "username", username,
							"password", password), callback, null, null);
		}

		public void remove(long id, Callback1<Void> callback) {
			nx.resources.del("/ds/" + id, null, callback, null, null);
		}

		public void test(long id, Callback1<String> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.get("/ds/" + id + "/test", null, callback, errorCallback, null);
		}

		public void setConfigurationForWorkbook(String key, String config, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.post("/ds/workbooks/" + key, $map("config", config), callback, errorCallback, null);
		}

		public void deleteConfigurationForWorkbook(String key, Callback1<Void> callback,
				Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.del("/ds/workbooks/" + key, null, callback, errorCallback, null);
		}

		public void edit(long id, Callback1<String> callback, Callback3<String, JQueryXHR, String> errorCallback) {
			nx.resources.get("/ds/" + id + "/edit", null, callback, errorCallback, null);
		}
	}
}
