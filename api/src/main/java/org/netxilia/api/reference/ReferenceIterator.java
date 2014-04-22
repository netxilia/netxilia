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
package org.netxilia.api.reference;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * This is a simple iterator on the sheet's cells using the area reference. It limits itself on the sheet's boundaries.
 * 
 */
public final class ReferenceIterator implements Iterator<CellReference> {
	private final AreaReference areaRef;
	private int currentRow;
	private int currentColumn;
	private final int firstColumnIndex;
	private final int lastRowIndex;
	private final int lastColumnIndex;

	public ReferenceIterator(AreaReference areaReference, int maxRow, int maxColumn) {
		this.areaRef = areaReference;
		this.currentRow = areaReference.getFirstRowIndex();
		this.currentColumn = areaReference.getFirstColumnIndex();
		this.lastColumnIndex = Math.min(maxColumn - 1, areaReference.getLastColumnIndex());
		this.lastRowIndex = Math.min(maxRow - 1, areaReference.getLastRowIndex());
		this.firstColumnIndex = areaReference.getFirstColumnIndex();
	}

	@Override
	public boolean hasNext() {
		return (currentRow <= lastRowIndex) && (currentColumn <= lastColumnIndex);
	}

	@Override
	public CellReference next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}

		CellReference ref = new CellReference(areaRef.getSheetName(), currentRow, currentColumn);
		currentColumn++;
		if ((currentColumn > lastColumnIndex)) {
			currentColumn = firstColumnIndex;
			currentRow++;
		}
		return ref;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
