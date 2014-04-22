package org.netxilia.server.jslib;

import org.stjs.javascript.Array;
import org.stjs.javascript.Map;
import org.stjs.javascript.dom.Option;
import org.stjs.javascript.functions.Callback4;
import org.stjs.javascript.jquery.JQueryAndPlugins;
import org.stjs.javascript.jquery.JQueryXHR;

public interface NetxiliaJQuery extends JQueryAndPlugins<NetxiliaJQuery>, TDJQueryHelpers, BoundsPlugin {
	public Object nxtable(String method);

	public void nxtable(String string, Integer rowIndex, int colIndex);

	public void nxtable(String string, Object nxtable);

	public CaretPosition caret();

	public void caret(CaretPosition caretPosition);

	public void putCursorAtEnd();

	public void simpleDatepicker(Map<String, Object> params);

	public void multiSelectOptionsUpdate(Array<Option> options);

	public void multiSelect(Map<String, ? extends Object> params);

	public void multiSelectOptionsShow();

	public void multiSelectOptionsHide();

	public String selectedValuesString();

	public void threeColumn(String string, int i);

	public NetxiliaJQuery threeColumn();

	public void splitter();

	public void popupmenu(Map<String, ? extends Object> $map);

	public NetxiliaJQuery nxtable();

	public void validate();

	public void threeColumn(Map<String, ? extends Object> params);

	public void treeview(Map<String, ? extends Object> params);

	public void ajaxForm(Callback4<String, String, JQueryXHR, NetxiliaJQuery> eventHandler);

	public void load(String url);

}
