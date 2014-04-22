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

import java.util.HashMap;
import java.util.Map;

import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

/**
 * This build builds a BlockCellCommand using the block having the maximum size.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class BlockCellCommandBuilder {
	private int firstRow = CellReference.MAX_ROW_INDEX;
	private int lastRow = CellReference.MIN_ROW_INDEX;
	private int firstColumn = CellReference.MAX_COLUMN_INDEX;
	private int lastColumn = CellReference.MIN_COLUMN_INDEX;
	private String sheetName;
	private Map<CellReference, ICellCommand> commands = new HashMap<CellReference, ICellCommand>();

	public BlockCellCommandBuilder command(ICellCommand cmd) {
		for (CellReference ref : cmd.getTarget()) {
			commands.put(ref, cmd);
		}
		firstRow = Math.min(firstRow, cmd.getTarget().getFirstRowIndex());
		lastRow = Math.max(lastRow, cmd.getTarget().getLastRowIndex());
		firstColumn = Math.min(firstColumn, cmd.getTarget().getFirstColumnIndex());
		lastColumn = Math.max(lastColumn, cmd.getTarget().getLastColumnIndex());
		sheetName = cmd.getTarget().getSheetName();
		return this;
	}

	public boolean isEmpty() {
		return commands.isEmpty();
	}

	public BlockCellCommand build() {
		if (commands.isEmpty()) {
			throw new IllegalStateException("At least one command should be added to the builder");
		}
		return new BlockCellCommand(new AreaReference(sheetName, firstRow, firstColumn, lastRow, lastColumn), commands);
	}

	public int size() {
		return commands.size();
	}
}
