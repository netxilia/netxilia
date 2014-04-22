/*******************************************************************************
 * 
 * Copyright 2010 Alexandru Craciun, and individual contributors as indicated
 * by the @authors tag. 
 * 
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of
 * the License, or (at your option) any later version.
 * 
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 ******************************************************************************/
package org.netxilia.api.impl.display;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.display.IStyleFormatter;
import org.netxilia.api.display.Style;
import org.netxilia.api.display.StyleAttribute;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.display.StyleGroup;
import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.model.SheetNames;
import org.netxilia.api.impl.model.WorkbookIds;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.StringValue;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

/**
 * This repository returns the style definition that are written in a specific spreadsheet in each workbook. The SYSTEM
 * workbook contains the default definitions for the formatters and styles present in the interface.
 * 
 * Each workbook can overwrite the definition of a style.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class StyleRepositoryImpl implements IStyleRepository, ApplicationContextAware {
	private static final int COL_DESCRIPTION = 4;

	private static final int COL_NAME = 3;

	private static final int COL_ATTS = 2;

	private static final int COL_ID = 1;

	private static final int COL_GROUP = 0;

	private static final int ROW_SIZE = 5;

	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(StyleRepositoryImpl.class);

	@Autowired
	private INetxiliaSystem workbookProcessor;

	private String stylesSheetName = SheetNames.STYLES;

	private String formatterPrefix = "formatter-";

	private final Ehcache cache;

	private ApplicationContext context;

	private ISheetEventListener definitionInvalidator = new InvalidateDefinitionCache();

	public StyleRepositoryImpl() {
		cache = CacheManager.create().getEhcache("style-cache");
	}

	public String getStylesSheetName() {
		return stylesSheetName;
	}

	public void setStylesSheetName(String stylesSheetName) {
		this.stylesSheetName = stylesSheetName;
	}

	public String getFormatterPrefix() {
		return formatterPrefix;
	}

	public void setFormatterPrefix(String formatterPrefix) {
		this.formatterPrefix = formatterPrefix;
	}

	private String safeString(List<CellData> row, int col) {
		if (col >= row.size()) {
			return null;
		}
		CellData cell = row.get(col);
		return cell != null && cell.getValue() != null ? cell.getValue().getStringValue().trim() : null;
	}

	/**
	 * Columns
	 * 
	 * <pre>
	 * A=group
	 * B=style id
	 * C=name (what to put in a menu for example)
	 * D=description
	 * F=attributes (; separated)
	 * </pre>
	 * 
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 * 
	 */
	private WorkbookStyleDefinitions loadDefinitions(WorkbookId workbookId) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		List<StyleDefinition> styles = new ArrayList<StyleDefinition>();
		ISheet sheet = null;
		try {
			sheet = workbookProcessor.getWorkbook(workbookId).getSheet(stylesSheetName);
			Matrix<CellData> cells = sheet.receiveCells(AreaReference.ALL).getNonBlocking();
			StyleGroup lastGroup = null;
			for (List<CellData> row : cells.getRows()) {
				if (row.size() < 2) {
					continue;
				}
				String group = safeString(row, COL_GROUP);
				String id = safeString(row, COL_ID);
				String atts = safeString(row, COL_ATTS);
				String name = safeString(row, COL_NAME);
				String desc = safeString(row, COL_DESCRIPTION);
				StyleGroup styleGroup = group != null && group.length() > 0 ? new StyleGroup(group) : lastGroup;
				lastGroup = styleGroup;
				if (id != null && id.length() > 0 && atts != null && atts.length() > 0) {
					List<StyleAttribute> styleAttributes = parseAttributes(atts);
					name = name != null ? name : id;
					StyleDefinition def = new StyleDefinition(new Style(id), styleGroup, name, desc, styleAttributes);
					getFormatter(findAttribute(styleAttributes, StyleAttribute.PATTERN_TYPE), def);

					styles.add(def);
				}
			}
			sheet.addListener(definitionInvalidator);

		} catch (NotFoundException e) {
			// no problem - use the parent one
		}
		if (workbookId.equals(WorkbookIds.SYSTEM)) {
			return new WorkbookStyleDefinitions(this, null, styles);
		}
		return new WorkbookStyleDefinitions(this, WorkbookIds.SYSTEM, styles);
	}

	private IStyleFormatter getFormatter(String type, StyleDefinition definition) {
		if (type == null) {
			return null;
		}
		IStyleFormatter formatter = (IStyleFormatter) context.getBean(formatterPrefix + type, definition);
		if (formatter == null) {
			log.warn("Unknown formatter:" + type);
		} else {
			definition.setFormatter(formatter);
		}
		return formatter;
	}

	private String findAttribute(List<StyleAttribute> attributes, String name) {
		for (StyleAttribute att : attributes) {
			if (name.equals(att.getName())) {
				return att.getValue();
			}
		}
		return null;
	}

	private List<StyleAttribute> parseAttributes(String atts) {
		List<StyleAttribute> attributes = new ArrayList<StyleAttribute>();
		int index = 0;
		while (index < atts.length()) {
			int nameEnd = atts.indexOf(':', index);
			if (nameEnd < 0) {
				break;
			}
			int valueEnd = indexOf(atts, ';', nameEnd + 1);
			if (valueEnd == -1) {
				valueEnd = atts.length();
			}
			attributes.add(new StyleAttribute(atts.substring(index, nameEnd).trim(), removeQuotes(atts.substring(
					nameEnd + 1, valueEnd).trim())));
			index = valueEnd + 1;
		}
		return attributes;
	}

	private String removeQuotes(String str) {
		if (str.length() < 2) {
			return str;
		}
		if (str.charAt(0) == '\'' && str.charAt(str.length() - 1) == '\'') {
			return str.substring(1, str.length() - 1);
		}
		// what to do with unbalanced quotes !?
		return str;
	}

	/**
	 * looks for the first position of the given character, from the given position, by skipping characters inside
	 * simple quotes
	 * 
	 * @param atts
	 * @param c
	 * @param i
	 * @return
	 */
	private int indexOf(String s, char c, int index) {
		boolean inQuotes = false;
		for (int i = index; i < s.length(); ++i) {
			char crtChar = s.charAt(i);

			if (crtChar == c) {
				if (!inQuotes) {
					return i;
				}
			} else if (crtChar == '\'') {
				inQuotes = !inQuotes;
			}
		}
		return -1;
	}

	private String attributesToString(Collection<StyleAttribute> attributes) {
		if (attributes == null || attributes.size() == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (StyleAttribute att : attributes) {
			// TODO quote values when necessary
			sb.append(att.getName()).append(":").append(att.getValue()).append(";");
		}
		return sb.toString();
	}

	@Override
	public WorkbookStyleDefinitions getDefinitions(WorkbookId workbookId) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		Element element;
		if ((element = cache.get(workbookId)) != null) {
			return (WorkbookStyleDefinitions) element.getObjectValue();
		}

		boolean wasSet = AclPrivilegedMode.set();
		try {
			WorkbookStyleDefinitions defs = loadDefinitions(workbookId);
			cache.put(new Element(workbookId, defs));
			return defs;
		} finally {
			if (!wasSet) {
				AclPrivilegedMode.clear();
			}
		}

	}

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		this.context = context;

	}

	@Override
	public void addDefinition(WorkbookId workbookId, StyleDefinition definition) throws StorageException,
			NotFoundException {
		Assert.notNull(definition);
		ISheet formatterSheet = getFormatterSheet(workbookId);
		IGenericValue[] row = new IGenericValue[5];
		row[COL_ID] = new StringValue(definition.getId().getId());
		row[COL_NAME] = new StringValue(definition.getName());
		row[COL_DESCRIPTION] = new StringValue(definition.getDescription());
		row[COL_GROUP] = new StringValue(definition.getGroup().getId());
		row[COL_ATTS] = new StringValue(attributesToString(definition.getAttributes().values()));

		formatterSheet.sendCommand(CellCommands.rowValues(AreaReference.lastRow(0, ROW_SIZE - 1), Arrays.asList(row)));
		cache.remove(workbookId);
	}

	protected ISheet getFormatterSheet(WorkbookId workbookId) throws StorageException, NotFoundException {
		try {
			return workbookProcessor.getWorkbook(workbookId).getSheet(stylesSheetName);
		} catch (NotFoundException e) {
			ISheet formatterSheet = null;
			try {
				formatterSheet = workbookProcessor.getWorkbook(workbookId).addNewSheet(stylesSheetName,
						SheetType.normal);
			} catch (AlreadyExistsException e1) {
				// nothing to do
			}

			return formatterSheet;
		}
	}

	private class InvalidateDefinitionCache implements ISheetEventListener {

		@Override
		public void onSheetEvent(SheetEvent sheetEvent) {
			cache.remove(sheetEvent.getSheetName().getWorkbookId());
		}

		@Override
		public void onRowEvent(RowEvent rowEvent) {
			cache.remove(rowEvent.getSheetName().getWorkbookId());

		}

		@Override
		public void onColumnEvent(ColumnEvent columnEvent) {
			cache.remove(columnEvent.getSheetName().getWorkbookId());

		}

		@Override
		public void onCellEvent(CellEvent cellEvent) {
			cache.remove(cellEvent.getSheetName().getWorkbookId());
			// log.info("Remove from cache:" + cellEvent.getSheetName().getWorkbookId() + ": size:" + cache.getSize()
			// + "," + cache.getKeys());
		}
	}
}
