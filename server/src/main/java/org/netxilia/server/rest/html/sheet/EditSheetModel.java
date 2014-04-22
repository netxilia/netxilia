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
package org.netxilia.server.rest.html.sheet;

import java.util.Collection;

import org.netxilia.api.display.StyleDefinition;

public class EditSheetModel {
	private final SheetModel sheetModel;
	private final SheetModel summarySheetModel;
	private final SheetModel privateSheetModel;

	private final Collection<StyleDefinition> fontSizes;
	private final Collection<StyleDefinition> backgrounds;
	private final Collection<StyleDefinition> foregrounds;
	private final Collection<StyleDefinition> formatters;
	private final Collection<StyleDefinition> editors;

	private final int backgroundColumns;
	private final int foregroundColumns;

	public EditSheetModel(SheetModel sheetModel, SheetModel summarySheetModel, SheetModel privateSheetModel,
			Collection<StyleDefinition> fontSizes, Collection<StyleDefinition> backgrounds,
			Collection<StyleDefinition> foregrounds, Collection<StyleDefinition> formatters,
			Collection<StyleDefinition> editors) {

		this.sheetModel = sheetModel;
		this.summarySheetModel = summarySheetModel;
		this.privateSheetModel = privateSheetModel;
		// no need to copy the collections - they are built inside the service only
		this.fontSizes = fontSizes;
		this.backgrounds = backgrounds;
		this.foregrounds = foregrounds;
		this.formatters = formatters;
		this.editors = editors;

		backgroundColumns = (int) Math.ceil(Math.sqrt(backgrounds.size()));
		foregroundColumns = (int) Math.ceil(Math.sqrt(foregrounds.size()));
	}

	public SheetModel getSheetModel() {
		return sheetModel;
	}

	public SheetModel getSummarySheetModel() {
		return summarySheetModel;
	}

	public SheetModel getPrivateSheetModel() {
		return privateSheetModel;
	}

	public Collection<StyleDefinition> getFontSizes() {
		return fontSizes;
	}

	public Collection<StyleDefinition> getBackgrounds() {
		return backgrounds;
	}

	public StyleDefinition getFirstBackground() {
		return backgrounds.size() > 0 ? backgrounds.iterator().next() : null;
	}

	public Collection<StyleDefinition> getForegrounds() {
		return foregrounds;
	}

	public StyleDefinition getFirstForeground() {
		return foregrounds.size() > 0 ? foregrounds.iterator().next() : null;
	}

	public Collection<StyleDefinition> getFormatters() {
		return formatters;
	}

	public Collection<StyleDefinition> getEditors() {
		return editors;
	}

	public int getBackgroundColumns() {
		return backgroundColumns;
	}

	public int getForegroundColumns() {
		return foregroundColumns;
	}

}
