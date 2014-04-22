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
package org.netxilia.api.display;

import java.util.Collection;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.value.RichValue;

/**
 * This service gives access to all the CSS styles defined in the system. It is also used to merge styles.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IStyleService {
	/**
	 * When a style is merged with another, if there is a style from the same group both in "style" and in the "toAdd"
	 * style, only the one from "toAdd" is kept. That is styles from the same group are seen as mutually exclusive: e.g.
	 * a-l and a-r (align left and align right).
	 * 
	 * If one of the parameter is null, the other one is returned. If both are null, null is returned.
	 * 
	 * @param style
	 * @param toAdd
	 * @return
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public Styles applyStyle(WorkbookId workbookId, Styles style, Styles applyStyle, StyleApplyMode applyMode)
			throws NetxiliaResourceException, NetxiliaBusinessException;

	// public RichValue applyStyles(RichValue value, StyleDefinition... styles);

	public StyleDefinition getStyleDefinition(WorkbookId workbookId, Style style) throws NetxiliaResourceException,
			NetxiliaBusinessException;

	public Collection<StyleDefinition> getStyleDefinitionsByGroup(WorkbookId workbookId, StyleGroup group)
			throws NetxiliaResourceException, NetxiliaBusinessException;

	public Collection<StyleDefinition> getStyleDefinitions(WorkbookId workbookId) throws NetxiliaResourceException,
			NetxiliaBusinessException;

	RichValue formatCell(WorkbookId workbook, CellData cell, RowData row, ColumnData colum)
			throws NetxiliaResourceException, NetxiliaBusinessException;

	public void addValueFormatter(WorkbookId workbook, String name, WorkbookId sourceWorkbookId, AreaReference nameRef,
			AreaReference valueRef) throws NetxiliaResourceException, NetxiliaBusinessException;

	public void addValueFormatter(WorkbookId workbook, String name, WorkbookId sourceWorkbookId, AbsoluteAlias nameRef,
			AbsoluteAlias valueRef) throws NetxiliaResourceException, NetxiliaBusinessException;
}
