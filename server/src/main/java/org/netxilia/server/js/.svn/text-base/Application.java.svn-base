package org.netxilia.server.js;

import static org.netxilia.server.js.NX.nx;
import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$array;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.$object;
import static org.stjs.javascript.Global.$or;
import static org.stjs.javascript.Global.$properties;
import static org.stjs.javascript.Global.confirm;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.Global.setTimeout;
import static org.stjs.javascript.Global.window;
import static org.stjs.javascript.JSObjectAdapter.$prototype;
import static org.stjs.javascript.JSStringAdapter.fromCharCode;

import org.netxilia.server.js.data.ChartDescription;
import org.netxilia.server.js.data.JsCellReference;
import org.netxilia.server.js.data.NetxiliaEvent;
import org.netxilia.server.js.data.SheetDescription;
import org.netxilia.server.js.data.WindowIndex;
import org.netxilia.server.js.data.WindowInfo;
import org.netxilia.server.js.plugins.NetxiliaDialogOptions;
import org.netxilia.server.jslib.JSON;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.JsFunction;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Callback3;
import org.stjs.javascript.functions.Function0;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.EventHandler;
import org.stjs.javascript.jquery.JQueryXHR;
import org.stjs.javascript.jquery.Position;
import org.stjs.javascript.jquery.plugins.DialogOptions;
import org.stjs.javascript.jquery.plugins.DialogUI;
import org.stjs.javascript.jquery.plugins.UIEventHandler;

public class Application {
	private Map<String, Sheet> sheets;
	private boolean exiting = false;
	private String copySource = null;
	private String copyContent = null;
	private String cutSource = null;
	public Sheet activeSheet;
	public boolean autoInsertRow = false;
	public int pageSize = 0;
	private String lastSortSpec = null;
	private Map<String, NetxiliaJQuery> dialogs;
	public SheetDescription desc;
	public Long windowId;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void init(SheetDescription desc) {
		sheets = $map();
		dialogs = $map();
		final Map<String, Object> cookie = this.getCookie();
		final Application that = this;
		pageSize = desc.pageSize;
		// $('.sheetEditor').threeColumn({resizeRight:true});
		$(".sheetEditor").splitter();

		$(".tabs").tabs();

		nx.utils.positionUnder("#foreground-menu", "#foreground-popup");
		nx.utils.positionUnder("#background-menu", "#background-popup");
		nx.utils.positionUnder("#borders-menu", "#borders-popup");
		nx.utils.positionUnder("#fontSizes-menu", "#fontSizes-popup");
		nx.utils.positionUnder("#formatters-menu", "#formatters-popup");

		$("#foreground-menu").popupmenu($map("target", "#foreground-popup", "time", 300));
		$("#background-menu").popupmenu($map("target", "#background-popup", "time", 300));
		$("#borders-menu").popupmenu($map("target", "#borders-popup", "time", 300));
		$("#fontSizes-menu").popupmenu($map("target", "#fontSizes-popup", "time", 300));
		$("#formatters-menu").popupmenu($map("target", "#formatters-popup", "time", 300, "closeOnClick", true));

		this.desc = desc;

		// now init the sheets
		this.sheets.$put(this.desc.name, new Sheet().init(this.desc, $(".mainCells")));
		this.sheets.$put(
				this.desc.name + ".summary",
				new Sheet().init(
						(SheetDescription) $object($.extend((Map) $map(), (Map) $properties(this.desc),
								(Map) $map("name", this.desc.name + ".summary"))), $(".summaryCells")));
		this.sheets.$put(
				this.desc.name + "." + this.desc.username,
				new Sheet().init(
						(SheetDescription) $object($.extend((Map) $map(), (Map) $properties(this.desc),
								(Map) $map("name", this.desc.name + "." + this.desc.username))), $(".privateCells")));
		this.activeSheet = this.sheets.$get(this.desc.name);

		window.onbeforeunload = new Callback0() {
			public void $invoke() {
				that.exiting = true;
				that.terminateWindow();
				that.setCookie();
			}
		};

		nx.resources.getHeaders = new Function0<Map<String, ? extends Object>>() {
			public Map<String, ? extends Object> $invoke() {
				return $map("nxwindow", that.windowId);
			}
		};

		nx.resources.disconnected = new Callback0() {
			public void $invoke() {
				that.dlgDisconnected();
			}
		};
		this.resize();
		$(window).resize(new EventHandler() {

			public boolean onEvent(Event ev, Element THIS) {
				nx.app.resize();
				return false;
			}
		});

		$(window.document).keydown(new EventHandler() {
			public boolean onEvent(Event ev, Element THIS) {
				if (ev.target.tagName.toLowerCase() == "input") {
					return false;
				}
				return that.activeSheet.shortcuts.handleEvent(ev);
			}
		}).keypress(new EventHandler() {
			public boolean onEvent(Event ev, Element THIS) {
				if (that.activeSheet.waitForKeypress) {
					that.activeSheet.editingContext.showDefaultEditor(that.activeSheet.selection.start,
							fromCharCode(String.class, ev.which));
					that.activeSheet.waitForKeypress = false;
				}
				return false;
			}
		});

		final DialogBounds2 pdlg = (DialogBounds2) cookie.$get("pdlg");
		$(".privateCellsDiv").dialog(new DialogOptions<NetxiliaJQuery>() {
			{
				resizable = true;
				width = pdlg.w;
				height = "" + pdlg.h;
				position = $array(pdlg.l, pdlg.t);
				autoOpen = pdlg.o;
				close = new UIEventHandler<DialogUI<NetxiliaJQuery>>() {
					public boolean onEvent(Event ev, DialogUI<NetxiliaJQuery> ui, Element THIS) {
						that.menuStatus();
						return false;
					}
				};
			}
		});

		$(".topline .first").after("<li><select id='users'><option id='count'>Other users (0)</option></select></li>");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<String, Object> getCookie() {
		String cookieString = $.cookie("nx");
		Map<String, Object> cookie = cookieString != null ? $properties(JSON.parse(cookieString)) : (Map) $map();
		int wwindow = $(window).width();
		cookie = $.extend((Map) $map("pdlg", new DialogBounds2(wwindow - 480 - 50, 120, 480, 280, true)),
				(Map) $properties(cookie));
		return cookie;
	}

	private void setCookie() {
		NetxiliaJQuery pdlg = $(".ui-dialog:has(.privateCellsDiv)");
		pdlg.show();
		DialogBounds2 pdlgBounds = (DialogBounds2) pdlg.bounds(null);
		pdlg.hide();
		pdlgBounds.o = (Boolean) $(".privateCellsDiv").dialog("isOpen");
		String cookieData = JSON.stringify($map("pdlg", pdlgBounds));
		$.cookie("nx", cookieData, $map("expires", 300));
	}

	private void layout() {
		// $('.sheetEditor').layout({resize: false});
	}

	/**
	 * called to calculate the size of the application's elements
	 */
	private void resize() {
		NetxiliaJQuery editor = $(".sheetEditor");
		Position editorOff = editor.offset();
	}

	public Sheet sheet(String name) {
		return this.sheets.$get(name);
	}

	public void setActiveSheet(Sheet sheet) {
		if (sheet == this.activeSheet) {
			return;
		}
		// make scrollbars for cell divs appear only when needed
		if (this.activeSheet != null) {
			$(".cellsDiv", this.activeSheet.container).addClass("no-overflow");
		}
		if (sheet != null) {
			$(".cellsDiv", sheet.container).removeClass("no-overflow");
		}

		this.activeSheet = sheet;
		this.menuStatus();
	}

	public void insertRow(boolean below) {
		final Application controller = this;
		nx.resources.rows.insert(this.desc.workbook, this.activeSheet.desc.name,
				below ? this.activeSheet.selection.start.row + 1 : this.activeSheet.selection.start.row,
				new Callback1<Void>() {
					public void $invoke(Void v) {
						controller.activeSheet.checkAutoInsertRow();
					}
				});
	}

	private void deleteRow() {
		nx.resources.rows.del(this.desc.workbook, this.activeSheet.desc.name, this.activeSheet.selection.start.row,
				null);
	}

	private void insertCol(boolean right) {
		nx.resources.columns.insert(this.desc.workbook, this.activeSheet.desc.name,
				this.activeSheet.selection.start.col, null);
	}

	private void deleteCol() {
		nx.resources.columns.del(this.desc.workbook, this.activeSheet.desc.name, this.activeSheet.selection.start.col,
				null);
	}

	/**
	 * merge selected cells
	 */
	private void mergeCells() {
		nx.resources.cells.merge(this.desc.workbook, this.activeSheet.selection.ref(true), null, null);
	}

	/**
	 * validationErros contains one entry per wrong parameter. Each entry contains a list of the errors concerning the
	 * field.
	 */
	private void processEventErrors(Array<Array<String>> validationErrors) {
		for (int f : validationErrors) {
			for (int e : validationErrors.$get(f)) {
				String err = validationErrors.$get(f).$get(e);
				// if (err.type.name == "wrongWindowId") {
				// this.acquireWindowId();
				// return;
				// }
			}
		}
	}

	/**
	 * acquire a new window id for the first time or if the current window id is no longer valid.
	 */
	private void acquireWindowId() {
		if (this.exiting) {
			return;
		}
		final Application controller = this;

		nx.resources.windows.register(this.desc.workbook, this.desc.name, new Callback1<WindowIndex>() {
			public void $invoke(WindowIndex response) {
				controller.windowId = response.id;
				nx.resources.windows.getWindowsForSheet(controller.desc.workbook, controller.desc.name,
						new Callback1<Array<WindowInfo>>() {
							public void $invoke(Array<WindowInfo> windows) {
								for (int s : windows) {
									if (windows.$get(s).windowId.id != controller.windowId) {
										controller.forAllSheets($properties($prototype(Sheet.class)).$get("addWindow"),
												windows.$get(s));
									}
								}
							}
						}, null);
				controller.pollEvents();
			}
		}, new Callback3<String, JQueryXHR, String>() {
			public void $invoke(String status, JQueryXHR xhr, String err) {
				setTimeout(new Callback0() {
					public void $invoke() {
						controller.acquireWindowId();
					}
				}, 1000);
			}
		});
	}

	/**
	 * called to terminate the current window (usually when the user quit the page)
	 */
	private void terminateWindow() {
		nx.resources.windows.terminate(this.windowId, null, null);
	}

	private void forAllSheets(Object... arguments) {
		Array<Object> v = $array();
		JsFunction<Void> f = (JsFunction<Void>) arguments[0];
		for (int i = 1; i < arguments.length; ++i) {
			v.push(arguments[i]);
		}
		for (String s : this.sheets) {
			f.apply(this.sheets.$get(s), v);
		}
	}

	private void addWindow(WindowInfo windowInfo) {
		long wid = windowInfo.windowId.id;
		$("#users").append("<option id='" + wid + "'>" + windowInfo.username + "</option>");
		$("#users #count").text("Other users (" + ($("#users option").size() - 1) + ")");
	}

	private void removeWindow(WindowInfo windowInfo) {
		long wid = windowInfo.windowId.id;
		$("#users #" + wid).remove();
		$("#users #count").text("Other users (" + ($("#users option").size() - 1) + ")");
	}

	private void processEvents(Array<NetxiliaEvent> events) {
		if (events == null || events.$length() == 0) {
			return;
		}

		// if (events.errors) {
		// this.processEventErrors(events.errors);
		// return;
		// }

		Map<String, Array<NetxiliaEvent>> evBySheet = $map();
		for (int e : events) {
			NetxiliaEvent ev = events.$get(e);
			if (ev.type == "windowCreated") {
				this.addWindow(ev.windowInfo);
				this.forAllSheets($properties($prototype(Sheet.class)).$get("addWindow"), ev.windowInfo);
				continue;
			} else if (ev.type == "windowTerminated") {
				this.removeWindow(ev.windowInfo);
				this.forAllSheets($properties($prototype(Sheet.class)).$get("removeWindow"), ev.windowInfo);
				continue;
			} else if (ev.type == "windowUndoChanged") {
				$(".toolbar .ToolIcon_undo").toggleClass("disabled", !ev.windowInfo.undoEnabled);
				$(".toolbar .ToolIcon_redo").toggleClass("disabled", !ev.windowInfo.redoEnabled);
				continue;
			}
			Array<NetxiliaEvent> evs = evBySheet.$get(ev.sheetName);
			if (evs == null) {
				evs = $array();
				evBySheet.$put(ev.sheetName, evs);
			}
			evs.push(ev);
		}
		for (String s : evBySheet) {
			this.sheets.$get(s).processEvents(evBySheet.$get(s));
		}
		this.menuStatus();
	}

	// poll for events
	private void pollEvents() {
		final Application controller = this;
		nx.resources.events.poll(controller.windowId, new Callback1<Array<NetxiliaEvent>>() {
			public void $invoke(Array<NetxiliaEvent> response) {
				try {
					controller.processEvents(response);
				} catch (Exception e) {
				}
				controller.pollEvents();
			}
		}, new Callback3<String, JQueryXHR, String>() {
			public void $invoke(String status, JQueryXHR xhr, String err) {
				// TODO check for error
				setTimeout(new Callback0() {
					public void $invoke() {
						controller.acquireWindowId();
					}
				}, 1000);
			}
		});
	}

	/**
	 * replicates the value of the first cell in the selection to the rest
	 */
	public void replicate() {
		nx.resources.cells.replicate(this.desc.workbook, this.activeSheet.selection.start.ref(true),
				this.activeSheet.selection.ref(false), null);
	}

	private void css(String style, String group) {
		boolean isSet = this.activeSheet.selection.start.hasCss(style);

		nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), style, isSet ? "clear"
				: "add", null);
		this.activeSheet.selectionContent.focus();
	}

	private void clearCss(String group) {
		if (group == null) {
			nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), null, "set", null);
		} else {
			nx.resources.cells.applyStyle(this.desc.workbook, this.activeSheet.selection.ref(true), group, "clear",
					null);
		}
		this.activeSheet.selectionContent.focus();
	}

	/**
	 * receives an object with the place where border has to be set. The "h" property contains the setting for the edges
	 * left and right, "v" for top and bottom. A setting is expressed as an array of any of "f" (first), "m" (middle),
	 * "l" (last)
	 * 
	 */
	private void borders(Map<String, Array<String>> borderStyle) {
		Array<CellWithStyle> updates = this.activeSheet.selection.borders($or(borderStyle,
				$map("h", $array("f", "m", "l"), "v", $array("f", "m", "l"))));
		for (int u : updates) {
			nx.resources.cells.applyStyle(this.desc.workbook, updates.$get(u).ref, updates.$get(u).style,
					borderStyle != null ? "add" : "clear", null);
		}
		this.activeSheet.selectionContent.focus();
	}

	/**
	 * called after a paste operation
	 */
	public void cbPaste(boolean fromMenu) {
		if (this.cutSource != null) {
			// cut & paste
			nx.resources.cells.move(this.desc.workbook, this.cutSource, this.activeSheet.selection.start.ref(true),
					null);
			this.cutSource = null;
			return;
		}
		// copy & paste
		if (fromMenu) {
			if (this.copyContent != null) {
				nx.resources.cells.paste(this.desc.workbook, this.copySource != null ? this.copySource
						: this.activeSheet.selection.start.ref(true), this.activeSheet.selection.start.ref(true),
						this.copyContent, null);
			}
		} else {
			String content = this.copyContent != null ? this.copyContent : (String) this.activeSheet.selectionContent
					.val();
			nx.resources.cells.paste(this.desc.workbook, this.copySource != null ? this.copySource
					: this.activeSheet.selection.start.ref(true), this.activeSheet.selection.start.ref(true), content,
					null);
			if (this.copyContent != this.activeSheet.selectionContent.val()) {
				// this is a paste coming from outside
				this.copyContent = null;
				this.copySource = null;
			}
			this.activeSheet.selectionContent.select();
		}
	}

	public void cbCopy(boolean fromMenu) {
		this.copySource = this.activeSheet.selection.start.ref(true);
		this.copyContent = (String) this.activeSheet.selectionContent.val();
		this.cutSource = null;
	}

	public void cbCut(boolean fromMenu) {
		this.cutSource = this.activeSheet.selection.ref(true);
		// mark cut area
	}

	public void toggleTreeView() {
		this.activeSheet.toggleTreeView();
	}

	/**
	 * called when the container of a sheet was scrolled
	 */
	public void onScroll(Sheet sheet) {
		if (sheet.desc.name == this.desc.name) {// synchronize summary sheet with main sheet
			this.sheet(this.desc.name + ".summary").syncScroll(sheet);
		}
	}

	/**
	 * called when a column was resized
	 */
	public void onResizeColumns(Sheet sheet) {
		if (sheet.desc.name == this.desc.name) {// synchronize summary sheet with main sheet
			this.sheet(this.desc.name + ".summary").syncColumnSizes(sheet);
		}
	}

	private boolean allSheetsLoaded() {
		for (String s : this.sheets) {
			if (!this.sheets.$get(s).loaded) {
				return false;
			}
		}
		return true;
	}

	private void onAllFramesLoaded() {
		if (this.windowId != null) {
			return;
		}
		this.acquireWindowId();

		this.sheet(this.desc.name + ".summary").container.nxtable("synchronize", this.sheet(this.desc.name).container);
		// this.sheet(this.desc.name).scrollLeft(260);
		// this.sheet(this.desc.name).scrollLeft(0);
		// this.sheet(this.desc.name).scrollLeft(160);
	}

	public void onFrameLoaded(Sheet sheet) {
		if (this.allSheetsLoaded()) {
			this.onAllFramesLoaded();
		}

		if (this.activeSheet != sheet) {
			$(".cellsDiv", sheet.container).addClass("no-overflow");
		}
	}

	public void info(String msg) {
		$("#message").toggle(msg);
		$("#message").text(msg);
	}

	public void print() {
		window.open(this.desc.context + "/rest/sheets/" + this.desc.workbook + "/" + this.desc.name + "/pdf", "_blank");
	}

	public void sort() {
		String sortSpec = "+" + nx.utils.columnLabel(this.sheet(this.desc.name).selection.start.col);
		if (this.lastSortSpec == sortSpec) {
			sortSpec = "-" + nx.utils.columnLabel(this.sheet(this.desc.name).selection.start.col);
		}
		this.lastSortSpec = sortSpec;
		nx.resources.sheets.sort(this.desc.workbook, this.desc.name, sortSpec, null, null);
	}

	/**
	 * popup find dialog and selects the first cell matching.
	 */
	private void dlgDisconnected() {
		final Application that = this;
		$.nxdialog("disconnected", new NetxiliaDialogOptions() {
			{
				height = 250;
				closable = false;
				buttons = $map("Login", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						$(THIS).dialog("close");
						window.location.reload();
					}
				});
			}
		});
	}

	private void dlgFind() {
		final Application that = this;
		$("#find #findMessage").text("");
		$.nxdialog("find", new NetxiliaDialogOptions() {
			{
				modal = false;
				height = 200;
				buttons = $map("Search", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						String searchFormula = "=A1=value(\"" + $("#searchText", THIS).val() + "\")";
						String lastFindRef = that.sheet(that.desc.name).selection.start.ref(false);
						if (lastFindRef == "A1") {
							lastFindRef = null;
						}
						nx.resources.cells.find(that.desc.workbook, that.desc.name, lastFindRef, searchFormula,
								new Callback1<String>() {
									public void $invoke(String refString) {
										if (refString == null) {
											$("#find #findMessage").text("Not found");
											return;
										}
										$("#find #findMessage").text("");
										JsCellReference ref = nx.utils.parseCellReference(refString);
										// XXX:page works correctly only without filter!
										int page = parseInt(ref.row / nx.app.pageSize);
										Sheet sheet = that.sheet(that.desc.name);
										if (page != sheet.pageNo) {
											sheet.selection.start.row = sheet.selection.end.row = ref.row;
											sheet.selection.start.col = sheet.selection.end.col = ref.col;
											sheet.viewPage(page, false);
										} else {
											sheet.selectionRange(ref.row, ref.col, ref.row, ref.col, false, false);
										}

									}
								}, null);

					}
				});
			}
		});

	}

	private void dlgStyles() {
		final Application that = this;
		$.nxdialog("styles", new NetxiliaDialogOptions() {
			{
				modal = false;
				height = 200;
				buttons = $map("Apply", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						NetxiliaJQuery $dlg = $(THIS);
						String styles = (String) $dlg.find("#selectedStyles").val();

						nx.resources.cells.applyStyle(that.desc.workbook, that.activeSheet.selection.ref(true), styles,
								"set", null);
					}
				});
			}
		});

	}

	private void dlgEditAliases() {
		final Application that = this;
		String defs = "";
		int x = 0;
		for (String ref : this.activeSheet.aliases) {
			String alias = this.activeSheet.aliases.$get(ref);
			defs += "<label>" + alias + "</label> ";
			defs += "<input type='text' id='alias" + x + "' value='" + ref + "'>";
			defs += "<a href='#' onclick='nx.app.setAlias(\"" + alias + "\",$(\"#alias" + x + "\").val())'>Save</a> ";
			defs += "<a href='#' onclick='nx.app.deleteAlias(\"" + alias + "\")'>Delete</a> ";
			defs += "<br>";
			++x;
		}
		$("#aliases #aliasDefinitions").html(defs);
		$.nxdialog("aliases", new NetxiliaDialogOptions() {
			{
				modal = true;
				height = 250;
				closable = false;
				buttons = $map("Done", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						$(THIS).dialog("close");
					}
				});
			}
		});
	}

	public void dlgChart(Integer chartId, ChartDescription chart) {
		final Application that = this;
		$("#chart #chartId").val(chartId != null ? chartId : -1);
		if (chart != null) {
			$("#chart #chartArea").val(chart.areaReference);
			$("#chart #chartType").val(chart.type);
			$("#chart #chartTitle").val(chart.title.text);
		} else {
			$("#chart #chartArea").val(this.activeSheet.selection.ref(false));
		}
		$.nxdialog("chart", new NetxiliaDialogOptions() {
			{
				modal = true;
				height = 250;
				buttons = $map("Save", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						String title = (String) $("#chart #chartTitle").val();
						String areaRef = (String) $("#chart #chartArea").val();
						String type = (String) $("#chart #chartType").val();
						int id = parseInt($("#chart #chartId").val());
						if (id < 0) {
							nx.resources.charts.add(that.desc.workbook, that.activeSheet.desc.name, areaRef, title,
									type, new Callback1<Void>() {
										public void $invoke(Void v) {
											that.activeSheet.reload();
										}
									}, null);
						} else {
							nx.resources.charts.set(that.desc.workbook, that.activeSheet.desc.name, id, areaRef, title,
									type, new Callback1<Void>() {
										public void $invoke(Void v) {
											that.activeSheet.reload();
										}
									}, null);
						}
						$(this).dialog("close");
					}
				});
			}
		});
	}

	private void deleteAlias(String alias) {
		if (!confirm("Are you sure you want to delete the alias '" + alias + "'?")) {
			return;
		}
		nx.resources.sheets.deleteAlias(this.activeSheet.desc.workbook, this.activeSheet.desc.name, alias, null, null);
	}

	private void setAlias(String alias, String ref) {
		nx.resources.sheets
				.setAlias(this.activeSheet.desc.workbook, this.activeSheet.desc.name, alias, ref, null, null);
	}

	public void undo() {
		nx.resources.windows.undo(this.windowId, null, null);
	}

	public void redo() {
		nx.resources.windows.redo(this.windowId, null, null);
	}

	private void toggleFilter(boolean withFormula) {
		this.activeSheet.toggleFilter(withFormula);
		this.menuStatus();
	}

	private void dlgPrivateNotes() {
		$(".privateCellsDiv").dialog("open");
	}

	private void dlgAddFormatter() {
		final Application that = this;
		$("#addFormatter #formatterSourceWorkbook").val(this.activeSheet.desc.workbook);

		$.nxdialog("addFormatter", new NetxiliaDialogOptions() {
			{
				modal = true;
				height = 250;
				width = 350;
				buttons = $map("Apply", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						String name = (String) $("#addFormatter #formatterName").val();
						String sourceWorkbook = (String) $("#addFormatter #formatterSourceWorkbook").val();
						String nameRef = (String) $("#addFormatter #formatterNameRange").val();
						String valueRef = (String) $("#addFormatter #formatterValueRange").val();

						nx.resources.formatters.setFormatter(that.desc.workbook, name, sourceWorkbook, nameRef,
								valueRef, null);
						// TODO should refresh the formatters list
						$(THIS).dialog("close");
					}
				});
			}
		});
	}

	public void menuStatus() {
		final Application that = this;
		setTimeout(new Callback0() {
			@Override
			public void $invoke() {
				if (that.activeSheet == null || that.activeSheet.selection == null) {
					return;
				}
				// styling - TODO do for entire selection
				$(".toolbar .ToolIcon_bold").toggleClass("active", that.activeSheet.selection.start.hasCss("b"));
				$(".toolbar .ToolIcon_italic").toggleClass("active", that.activeSheet.selection.start.hasCss("i"));
				$(".toolbar .ToolIcon_underline").toggleClass("active", that.activeSheet.selection.start.hasCss("u"));
				$(".toolbar .ToolIcon_strikethrough").toggleClass("active",
						that.activeSheet.selection.start.hasCss("s"));

				$(".toolbar .ToolIcon_alignleft").toggleClass("active", that.activeSheet.selection.start.hasCss("a-l"));
				$(".toolbar .ToolIcon_aligncenter").toggleClass("active",
						that.activeSheet.selection.start.hasCss("a-c"));
				$(".toolbar .ToolIcon_alignright")
						.toggleClass("active", that.activeSheet.selection.start.hasCss("a-r"));
				$(".toolbar .ToolIcon_alignjustify").toggleClass("active",
						that.activeSheet.selection.start.hasCss("a-j"));
				$(".toolbar .ToolIcon_wordwrap").toggleClass("active", that.activeSheet.selection.start.hasCss("wp"));
				// $(".toolbar .ToolIcon_fontSize").text(that.activeSheet.selection.start.$td.css("font-size"));

				// filters
				$(".toolbar .ToolIcon_filter").toggleClass("active",
						that.activeSheet.filter != null && !that.activeSheet.filterFormula);
				$(".toolbar .ToolIcon_filterFormula").toggleClass("active",
						that.activeSheet.filter != null && that.activeSheet.filterFormula);

				// structural
				$(".toolbar .ToolIcon_mergecells").toggleClass("disabled", that.activeSheet.filter != null);
				$(".toolbar .ToolIcon_insertrow").toggleClass("disabled", that.activeSheet.filter != null);
				$(".toolbar .ToolIcon_autoinsertrow").toggleClass("disabled", that.activeSheet.filter != null);
				$(".toolbar .ToolIcon_deleterow").toggleClass("disabled", that.activeSheet.filter != null);
				$(".toolbar .ToolIcon_insertcol").toggleClass("disabled", that.activeSheet.filter != null);
				$(".toolbar .ToolIcon_deletecol").toggleClass("disabled", that.activeSheet.filter != null);

				CellRange.StyleRange selectedStyles = that.activeSheet.selection.css();
				$("#styles #selectedStyles").val(selectedStyles.css);
				$("#styles #selectedStyles").toggleClass("partial", selectedStyles.partial);

				$(".toolbar .ToolIcon_privateNotes").toggleClass("disabled",
						(Boolean) $(".privateCellsDiv").dialog("isOpen"));

			}
		}, 50);

	}

}
