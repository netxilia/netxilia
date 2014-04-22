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
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.netxilia.api.display.DefaultStyle;
import org.netxilia.api.display.DefaultStyleGroup;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.Style;
import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.display.StyleGroup;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.impl.format.SheetValueListFormatter;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.RichValue;
import org.netxilia.api.value.RichValueBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class StyleServiceImpl implements IStyleService {

	@Autowired
	private IStyleRepository styleRepository;

	public IStyleRepository getStyleRepository() {
		return styleRepository;
	}

	public void setStyleRepository(IStyleRepository styleRepository) {
		this.styleRepository = styleRepository;
	}

	private Styles remove(WorkbookId workbookId, Styles style, Styles toRemove) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		if (style == null) {
			return null;
		}
		if (toRemove == null) {
			return style;
		}
		List<Style> newEntries = new ArrayList<Style>();
		List<Style> toRemoveEntries = toRemove.getItems();
		// remove the items from "this" that are from a group existing in a "toRemove" entry
		for (Style entry1 : style.getItems()) {
			boolean okToRemove = false;

			for (Style entry2 : toRemoveEntries) {
				if (entry2.equals(entry1)) {
					okToRemove = true;
					break;
				}
				StyleDefinition def1 = getStyleDefinition(workbookId, entry1);
				StyleDefinition def2 = getStyleDefinition(workbookId, entry2);
				if (def1 != null && def2 != null) {
					if (def2.getGroup().equals(def1.getGroup()) && def1.getGroup().isRadio()) {
						okToRemove = true;
						break;
					}
				}
			}
			if (!okToRemove) {
				newEntries.add(entry1);
			}
		}
		return Styles.styles(newEntries);
	}

	private Styles merge(WorkbookId workbookId, Styles style, Styles toAdd) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		if (style == null) {
			return toAdd;
		}
		if (toAdd == null) {
			return style;
		}

		Set<Style> newEntries = new LinkedHashSet<Style>();
		for (Style entry2 : toAdd.getItems()) {
			newEntries.add(entry2);
		}
		List<Style> toAddEntries = toAdd.getItems();
		// add only the items from "style" that are not from a group already in "toAdd" style
		for (Style entry1 : style.getItems()) {
			StyleDefinition def1 = getStyleDefinition(workbookId, entry1);

			if (def1 == null) {
				continue;
			}
			if (!def1.getGroup().isRadio()) {
				newEntries.add(entry1);
				continue;
			}
			boolean okToAdd = true;

			for (Style entry2 : toAddEntries) {
				StyleDefinition def2 = getStyleDefinition(workbookId, entry2);
				if (def2 == null) {
					continue;
				}
				if (def2.getGroup().equals(def1.getGroup())) {
					okToAdd = false;
					break;
				}
			}
			if (okToAdd) {
				newEntries.add(entry1);
			}
		}

		return Styles.styles(new ArrayList<Style>(newEntries));
	}

	/**
	 * Apply the applyStyle to the style parameter using the applyMode. It returns the new style.
	 * 
	 * @param style
	 * @param applyStyle
	 * @param applyMode
	 * @return
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public Styles applyStyle(WorkbookId workbookId, Styles style, Styles applyStyle, StyleApplyMode applyMode)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		switch (applyMode) {
		case clear:
			return remove(workbookId, style, applyStyle);
		case set:
			return applyStyle;
		case add:
			return merge(workbookId, style, applyStyle);
		default:
			throw new IllegalArgumentException("Unknown apply mode:" + applyMode);
		}

	}

	/**
	 * Formats the cell's content using the formats from cell, row or column in this order looking for the first
	 * non-null value.
	 * 
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	@Override
	public RichValue formatCell(WorkbookId workbookId, CellData cell, RowData row, ColumnData column)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		if (cell == null) {
			return null;
		}
		String display = null;
		IGenericValue value = cell.getValue();
		Styles formattedStyle = null;
		if (value instanceof RichValue) {
			RichValue richValue = (RichValue) value;
			value = richValue.getGenericValue();
			formattedStyle = richValue.getStyles();
			display = richValue.getDisplay();
		}

		Styles mergedStyle = cell.getStyles();
		if (row != null) {
			mergedStyle = merge(workbookId, row.getStyles(), mergedStyle);
		}
		if (column != null) {
			mergedStyle = merge(workbookId, column.getStyles(), mergedStyle);
		}
		mergedStyle = merge(workbookId, formattedStyle, mergedStyle);

		List<StyleDefinition> definitions = new ArrayList<StyleDefinition>();
		if (mergedStyle != null) {
			for (Style style : mergedStyle.getItems()) {
				StyleDefinition def = getStyleDefinition(workbookId, style);
				if (def != null) {
					definitions.add(def);
				}
			}
		}
		return applyStyles(workbookId, new RichValue(value, display),
				definitions.toArray(new StyleDefinition[definitions.size()]));
	}

	protected RichValue applyStyles(WorkbookId workbookId, RichValue value, StyleDefinition... styles)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		RichValueBuilder builder = new RichValueBuilder(value);
		for (StyleDefinition style : styles) {
			if (style.getFormatter() != null) {
				builder.display(style.getFormatter().format(builder.getValue()));
			}
			builder.addStyle(style.getId());
		}
		if (builder.getDisplay() == null) {
			if (builder.getValue() != null) {
				// no formatter applied yet
				StyleDefinition defaultFormatter = getDefaultFormatter(workbookId, builder.getValue());
				if (defaultFormatter != null) {
					if (defaultFormatter.getFormatter() != null) {
						builder.display(defaultFormatter.getFormatter().format(builder.getValue()));
					}
					builder.addStyle(defaultFormatter.getId());
				} else {
					builder.display(builder.getValue().getStringValue());
				}
			} else {
				builder.display("");
			}
		}
		return builder.build();
	}

	private StyleDefinition getDefaultFormatter(WorkbookId workbookId, IGenericValue value)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		switch (value.getValueType()) {
		case BOOLEAN:
			return getStyleDefinition(workbookId, DefaultStyle.formatBoolean.getStyle());
		case NUMBER:
			return getStyleDefinition(workbookId, DefaultStyle.formatNumber.getStyle());
		case DATE:
			if (value.getDateValue() instanceof LocalTime) {
				return getStyleDefinition(workbookId, DefaultStyle.formatTime.getStyle());
			} else if (value.getDateValue() instanceof LocalDate) {
				return getStyleDefinition(workbookId, DefaultStyle.formatDate.getStyle());
			} else {
				return getStyleDefinition(workbookId, DefaultStyle.formatDateTime.getStyle());
			}

		}
		return null;
	}

	@Override
	public StyleDefinition getStyleDefinition(WorkbookId workbookId, Style style) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		Assert.notNull(workbookId);
		Assert.notNull(style);
		WorkbookStyleDefinitions defs = styleRepository.getDefinitions(workbookId);
		return defs.getDefinition(style);
	}

	@Override
	public Collection<StyleDefinition> getStyleDefinitionsByGroup(WorkbookId workbookId, StyleGroup group)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		Assert.notNull(workbookId);
		Assert.notNull(group);
		WorkbookStyleDefinitions defs = styleRepository.getDefinitions(workbookId);
		return defs.getDefinitionsByGroup(group);
	}

	@Override
	public Collection<StyleDefinition> getStyleDefinitions(WorkbookId workbookId) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		Assert.notNull(workbookId);
		WorkbookStyleDefinitions defs = styleRepository.getDefinitions(workbookId);
		return defs.getDefinitions();
	}

	@Override
	public void addValueFormatter(WorkbookId workbookId, String name, WorkbookId sourceWorkbookId,
			AreaReference nameReference, AreaReference valueReference) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		StyleDefinition definition = SheetValueListFormatter.buildDefinition(new Style(name),
				DefaultStyleGroup.Formatters.getGroupId(), name, "-", sourceWorkbookId, nameReference, valueReference);
		styleRepository.addDefinition(workbookId, definition);
	}

	@Override
	public void addValueFormatter(WorkbookId workbookId, String name, WorkbookId sourceWorkbookId,
			AbsoluteAlias nameReference, AbsoluteAlias valueReference) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		StyleDefinition definition = SheetValueListFormatter.buildDefinition(new Style(name),
				DefaultStyleGroup.Formatters.getGroupId(), name, "-", sourceWorkbookId, nameReference, valueReference);
		styleRepository.addDefinition(workbookId, definition);
	}
}
