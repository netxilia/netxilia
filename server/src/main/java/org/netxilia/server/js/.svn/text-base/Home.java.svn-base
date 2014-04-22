package org.netxilia.server.js;

import static org.netxilia.server.js.NX.nx;
import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.stjs.javascript.Global.$map;
import static org.stjs.javascript.Global.$or;
import static org.stjs.javascript.Global.alert;
import static org.stjs.javascript.Global.confirm;
import static org.stjs.javascript.Global.eval;
import static org.stjs.javascript.Global.parseInt;
import static org.stjs.javascript.Global.setTimeout;
import static org.stjs.javascript.Global.window;

import org.netxilia.server.js.data.DataSourceConfiguration;
import org.netxilia.server.js.data.HomeDescription;
import org.netxilia.server.js.data.StringHolder;
import org.netxilia.server.js.plugins.NetxiliaDialogOptions;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Array;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback0;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.functions.Callback3;
import org.stjs.javascript.functions.Callback4;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.EventHandler;
import org.stjs.javascript.jquery.JQueryXHR;

public class Home {
	private HomeDescription desc;
	private String sheet;
	protected String datasource;
	protected String workbook;

	public void init(HomeDescription desc) {

		this.desc = desc;
		$(".container").threeColumn($map("resizeLeft", true));

		final Home that = this;
		$(window).resize(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				nx.workbook.resize();
				return false;
			}
		});
		$(window.document).ready(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.bindTreeview(true);
				$("img.logo").click(new EventHandler() {
					@Override
					public boolean onEvent(Event ev, Element THIS) {
						that.viewMain();
						return false;
					}
				});
				return false;
			}
		});
		this.viewMain();
	}

	private void selectNode(Element node) {
		$(".filetree .selected").removeClass("selected");
		$(node).parent().addClass("selected");
	}

	private void bindTreeview(boolean ok) {
		final Home that = this;
		$(".filetree").treeview($map("persist", "cookie"));
		$(".filetree .sheet").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.datasource = null;
				that.sheet = $(THIS).text();
				that.workbook = $(THIS).parents("li").find(".workbook").text();
				that.viewSheet();
				return false;
			}
		});
		$(".filetree .workbook").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.datasource = null;
				that.sheet = null;
				that.workbook = $(THIS).text();
				that.viewWorkbook();
				return false;
			}
		});
		$(".filetree .datasources").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.sheet = that.workbook = that.datasource = null;
				that.viewDatasources();
				return false;
			}
		});
		$(".filetree .datasource").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.sheet = that.workbook = null;
				that.viewDatasource($(THIS).attr("id"));
				return false;
			}
		});

		$(".filetree .modules").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.sheet = that.workbook = that.datasource = null;
				that.viewModules();
				return false;
			}
		});

		$(".filetree .build").click(new EventHandler() {
			@Override
			public boolean onEvent(Event ev, Element THIS) {
				that.selectNode(THIS);
				that.sheet = that.workbook = that.datasource = null;
				that.viewRequests();
				return false;
			}
		});
	}

	/**
	 * called to calculate the size of the application's elements
	 */
	private void resize() {
	}

	private void viewMain() {
		final Home that = this;
		that.sheet = that.workbook = that.datasource = null;
		$("#display").load(that.desc.context + "/readme.jsp");
		that.showMenu("main");
	}

	private void newSheet() {
		final Home that = this;
		$.nxdialog("newSheet", new NetxiliaDialogOptions() {
			{
				height = 240;
				buttons = $map("Create", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						final String name = (String) $("#sheetName", THIS).val();
						final NetxiliaJQuery $dlg = $(THIS);
						$dlg.find("#error").text("");
						nx.resources.sheets.newSheet(that.workbook, name, new Callback1<String>() {
							public void $invoke(String sheet) {
								window.open(that.desc.context + "/rest/sheets/" + that.workbook + "/" + name + "/edit",
										"_blank");
								that.refreshTree();
								$dlg.dialog("close");
							}
						}, new Callback3<String, JQueryXHR, String>() {
							public void $invoke(String error, JQueryXHR xhr, String nxError) {
								String err = $or(nxError, xhr.statusText);
								$dlg.find("#error").text(err);
							}
						});
					}
				});
			}
		});
	}

	private void newWorkbook() {
		final Home that = this;
		// fill the datasource select
		nx.resources.ds.list(new Callback1<Array<DataSourceConfiguration>>() {
			public void $invoke(Array<DataSourceConfiguration> cfgs) {
				NetxiliaJQuery $ds = $("#datasources");
				String setVal = (String) $ds.val();
				if (setVal == null) {
					setVal = that.desc.mostUsedDataSource;
				}
				$ds.html("");
				for (int c : cfgs) {
					$("<option value='" + cfgs.$get(c).id.id + "'>" + cfgs.$get(c).name + "</option>").appendTo($ds);
				}
				$ds.val(setVal);
			}
		});

		$.nxdialog("newWorkbook", new NetxiliaDialogOptions() {
			{
				height = 260;
				buttons = $map("Create", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						final String name = (String) $("#workbookName", THIS).val();
						final String datasource = (String) $("#datasources", THIS).val();
						final NetxiliaJQuery $dlg = $(THIS);
						$dlg.find("#error").text("");

						nx.resources.workbooks.newWorkbook(name, datasource, new Callback1<Void>() {
							public void $invoke(Void v) {
								that.refreshTree();
								$dlg.dialog("close");
							}
						}, new Callback3<String, JQueryXHR, String>() {
							public void $invoke(String error, JQueryXHR xhr, String nxError) {
								String err = $or(nxError, xhr.statusText);
								$dlg.find("#error").text(err);
							}
						});
					}
				});
			}
		});
	}

	private void importSheets() {
		final Home that = this;
		$("#display").html("<iframe name='importResults' frameborder='no' width='100%' height='600' src=''></iframe>");

		$.nxdialog("importSheets", new NetxiliaDialogOptions() {
			{
				height = 250;
				width = 340;
				buttons = $map("Import", new Callback1<Element>() {
					public void $invoke(Element THIS) {
						NetxiliaJQuery $frm = $("form", THIS);
						NetxiliaJQuery $dlg = $(THIS);
						$frm.attr("action", that.desc.context + "/rest/workbooks/" + that.workbook + "/import");
						$frm.submit();
						$dlg.dialog("close");

						// $frm.ajaxSubmit({
						// url: that.desc.context + "/rest/workbooks/" + that.workbook + "/import",
						// dataType: "json",
						// success(responseText){
						// var sheet = eval("(" + responseText + ")");
						// that.refreshTree();
						// $dlg.dialog('close');
						// }
						// });
					}
				});
			}
		});
	}

	private void showMenu(String type) {
		if (type == "sheet") {
			$("#workbookStyles").attr("href", this.desc.context + "/rest/styles/" + this.workbook);
		} else {
			$("#workbookStyles").attr("href", "");
		}

		$("#menu-sheet").toggle(type == "sheet");
		$("#menu-workbook").toggle(type == "workbook");
		$("#menu-main").toggle(type == "main");
		$("#menu-datasources").toggle(type == "datasources");
		$("#menu-datasource").toggle(type == "datasource");

		$("#menu-requests").toggle(type == "requests");
		$("#menu-modules").toggle(type == "modules");
	}

	private void refreshTree() {
		final Home that = this;
		nx.resources.home.treeview(new Callback1<StringHolder>() {
			public void $invoke(final StringHolder tv) {
				setTimeout(new Callback0() {
					@Override
					public void $invoke() {
						$(".filetree").html(tv.value);
						that.bindTreeview(false);
					}
				}, 1);
			}
		}, null);
	}

	private void viewSheet() {
		this.showMenu("sheet");
		if (this.sheet != null) {
			$("#display").load(
					nx.utils.url(this.desc.context + "/rest/sheets/{}/{}/overview", this.workbook, this.sheet));
		}
	}

	private void viewWorkbook() {
		this.showMenu("workbook");
		if (this.workbook != null) {
			$("#display").load(this.desc.context + "/rest/workbooks/" + this.workbook);
		}
	}

	private void deleteWorkbook() {
		final Home that = this;
		if (this.workbook != null) {
			if (confirm("Are you sure you want to delete this workbook?")) {
				nx.resources.workbooks.deleteWorkbook(this.workbook, new Callback1<Void>() {
					public void $invoke(Void v) {
						that.refreshTree();
						that.viewDatasources();
					}
				}, null);
			}
		}
	}

	private void editSheet() {
		if (this.sheet != null) {
			window.open(this.desc.context + "/rest/sheets/" + this.workbook + "/" + this.sheet + "/edit", "_blank");
		}
	}

	private void deleteSheet() {
		final Home that = this;
		if (this.sheet != null) {
			if (confirm("Are you sure you want to delete this sheet?")) {
				nx.resources.sheets.del(this.workbook, this.sheet, new Callback1<Void>() {
					public void $invoke(Void v) {
						that.refreshTree();
						that.viewWorkbook();
					}
				}, new Callback3<String, JQueryXHR, String>() {
					public void $invoke(String error, JQueryXHR xhr, String nxError) {
						String err = $or(nxError, xhr.statusText);
						alert(err);
					}
				});
			}
		}
	}

	private void pdfSheet() {
		if (this.sheet != null) {
			$("#display").html(
					"<iframe frameborder='no' width='100%' height='600' src='" + this.desc.context + "/rest/sheets/"
							+ this.workbook + "/" + this.sheet + "/pdf" + "'></iframe>");
		}
	}

	private void exportSheet() {
		if (this.sheet != null) {
			final Home that = this;
			$.nxdialog("exportSheets", new NetxiliaDialogOptions() {
				{
					height = 150;
					width = 300;
					buttons = $map("Export", new Callback1<Element>() {
						public void $invoke(Element THIS) {
							NetxiliaJQuery $frm = $("form", THIS);
							NetxiliaJQuery format = $("#format", $frm);
							NetxiliaJQuery $dlg = $(THIS);

							// $("#display").html("<iframe frameborder='no' width='100%' height='600' src='" +
							// that.desc.context + "/rest/sheets/" + that.workbook + "/" + that.sheet + "/" +
							// format.val() + "'></iframe>");
							window.open(that.desc.context + "/rest/sheets/" + that.workbook + "/" + that.sheet + "/"
									+ format.val(), "_blank");
							$dlg.dialog("close");
						}
					});
				}
			});
		}
	}

	private void viewDatasources() {
		this.showMenu("datasources");
		$("#display").html("");
	}

	private void viewModules() {
		this.showMenu("modules");
		$("#display").load(this.desc.context + "/temp/modules.jsp");
	}

	private void viewRequests() {
		this.showMenu("requests");
		$("#display").load(this.desc.context + "/temp/requests.jsp");
	}

	private void viewDatasource(String ds) {
		final Home that = this;
		this.showMenu("datasource");
		if (ds != null) {
			this.datasource = ds;
		}

		nx.resources.ds.edit(parseInt(this.datasource), new Callback1<String>() {
			public void $invoke(String html) {
				$("#display").html(html);
				$("#form-datasource").ajaxForm(new Callback4<String, String, JQueryXHR, NetxiliaJQuery>() {
					@Override
					public void $invoke(String p1, String p2, JQueryXHR p3, NetxiliaJQuery p4) {
						that.refreshTree();
					}
				});
			}
		}, null);
	}

	private void deleteDatasource() {
		final Home that = this;
		if (this.datasource != null) {
			if (confirm("Are you sure you want to delete this datasource?")) {
				nx.resources.ds.remove(parseInt(this.datasource), new Callback1<Void>() {
					public void $invoke(Void v) {
						that.refreshTree();
						that.viewDatasources();
					}
				});
			}
		}
	}

	private void testDatasource() {
		if (this.datasource != null) {
			nx.resources.ds.test(parseInt(this.datasource), new Callback1<String>() {
				public void $invoke(String msg) {
					alert(msg);
				}
			}, null);
		}
	}

	private void newDatasource() {
		final Home that = this;
		this.datasource = "";
		this.showMenu("datasource");
		$("#display").load(this.desc.context + "/rest/ds/editNew", null, new Callback3<Object, String, JQueryXHR>() {
			@Override
			public void $invoke(Object data, String status, JQueryXHR request) {
				$("#form-datasource").ajaxForm(new Callback4<String, String, JQueryXHR, NetxiliaJQuery>() {
					@Override
					public void $invoke(String responseText, String p2, JQueryXHR p3, NetxiliaJQuery p4) {
						DataSourceConfiguration ds = eval("(" + responseText + ")");
						that.refreshTree();
						that.viewDatasource("" + ds.id);
					}
				});
			}
		});
	}
}
