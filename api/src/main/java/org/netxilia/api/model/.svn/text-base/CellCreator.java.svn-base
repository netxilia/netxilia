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
package org.netxilia.api.model;

import org.netxilia.api.reference.CellReference;
import org.netxilia.api.utils.IMatrixElementCreator;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class CellCreator implements IMatrixElementCreator<CellData> {
	private final String sheetName;
	private final int startRow;
	private final int startColumn;

	public CellCreator(String sheetName, int startRow, int startColumn) {
		this.sheetName = sheetName;
		this.startRow = startRow;
		this.startColumn = startColumn;
	}

	@Override
	public CellData newElement(int row, int column) {
		return new CellData(new CellReference(sheetName, startRow + row, startColumn + column));
	}
}