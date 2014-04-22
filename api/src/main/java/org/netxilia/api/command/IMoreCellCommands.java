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
package org.netxilia.api.command;

import java.util.List;

import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.Styles;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;

/**
 * This interface offers more complex cell commands, that need access to SPI services.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IMoreCellCommands {
	public ICellCommand paste(AreaReference ref, String[][] pasteData, CellReference from);

	public ICellCommand formulaTransformer(AreaReference area, IReferenceTransformer referenceTransformer);

	public ICellCommand applyStyles(WorkbookId workbookId, AreaReference ref, Styles styles, StyleApplyMode mode);

	public ICellCommand copyContent(AreaReference to, CellData source);

	/**
	 * Sent to refresh the list of the cells,usually due to a change in the value of cell used in the given cell's
	 * formula. The relative CellReferences (with null sheet name) are considered to belong to the sheet to which the
	 * command is first time sent.
	 * 
	 * 
	 * @param reason
	 * 
	 * @param refreshCell
	 */
	public ICellCommand refresh(List<CellReference> refreshCells, boolean stopPropagation);
}
