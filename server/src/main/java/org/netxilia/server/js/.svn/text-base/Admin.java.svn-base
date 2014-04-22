package org.netxilia.server.js;

import static org.netxilia.server.jslib.NetxiliaGlobal.$;
import static org.netxilia.server.js.NX.nx;
import static org.stjs.javascript.Global.$properties;
import static org.stjs.javascript.Global.alert;

import org.netxilia.server.js.data.AdminDescription;
import org.netxilia.server.js.data.DataSourceConfiguration;
import org.netxilia.server.jslib.NetxiliaJQuery;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Element;
import org.stjs.javascript.functions.Callback1;
import org.stjs.javascript.jquery.Event;
import org.stjs.javascript.jquery.plugins.TabsOptions;
import org.stjs.javascript.jquery.plugins.TabsUI;
import org.stjs.javascript.jquery.plugins.UIEventHandler;

public class Admin {
	private Long datasource;

	private AdminDescription desc;

	public void init(AdminDescription desc) {
		final Admin that = this;
		this.desc = desc;
		$("#datasources").tabs(new TabsOptions<NetxiliaJQuery>() {
			{
				select = new UIEventHandler<TabsUI<NetxiliaJQuery>>() {
					public boolean onEvent(Event event, TabsUI<NetxiliaJQuery> ui, Element THIS) {
						if (ui.index < that.desc.datasources.$length()) {
							that.viewDatasource(that.desc.datasources.$get(ui.index));
						} else {
							that.newDatasource();
						}
						return false;
					}
				};
			}
		});
		$("#datasources").tabs("select", 0);
		that.viewDatasource(that.desc.datasources.$get(0));
		$("#createAdminForm").validate();
	}

	public void viewDatasource(DataSourceConfiguration ds) {
		Map<String, Object> map = $properties(ds);
		for (String att : map) {
			$("#ds-" + att).val(map.$get(att));
		}
	}

	public void testDatasource() {
		if (this.datasource != null) {
			nx.resources.ds.test(this.datasource, new Callback1<String>() {
				@Override
				public void $invoke(String msg) {
					alert(msg);
				}
			}, null);
		}
	}

	public void newDatasource() {
		this.viewDatasource(new DataSourceConfiguration() {
			{
				id = null;
				name = "";
				driver = "";
				url = "";
				username = "";
				password = "";
			}
		});
	}
}
