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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;

/**
 * Used to manipulate the row and column spans present in {@link SheetData#getSpans()}. It returns the column and the
 * row spans for a given cell. It can also be used to add a new merge area.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SpanTable {
	// private static final Pair<Integer, Integer> INVISIBLE = new Pair<Integer, Integer>(-1, -1);

	/**
	 * contains the value of the spans for the top left corners and -1 for the "invisible cells"
	 */
	private final Map<CellReference, SheetDimensions> spans = new HashMap<CellReference, SheetDimensions>();

	public SpanTable(List<AreaReference> spanList) {
		for (AreaReference area : spanList) {
			addAreaDirectly(area);
		}
	}

	private void addAreaDirectly(AreaReference area) {
		for (int r = area.getFirstRowIndex(); r <= area.getLastRowIndex(); ++r) {
			for (int c = area.getFirstColumnIndex(); c <= area.getLastColumnIndex(); ++c) {
				spans.put(new CellReference(r, c),
						new SheetDimensions(area.getFirstRowIndex() - r, area.getFirstColumnIndex() - c));
			}
		}
		spans.put(area.getTopLeft(), new SheetDimensions(area.getRowCount(), area.getColumnCount()));
	}

	private void removeArea(AreaReference area) {
		for (CellReference cell : area) {
			spans.remove(cell);
		}
	}

	private SheetDimensions getSpan(CellReference ref) {
		CellReference searchRef = ref;
		if (searchRef.getSheetName() != null) {
			// refs are relative (no sheet name)
			searchRef = searchRef.withSheetName(null);
		}
		return spans.get(searchRef);
	}

	public int getRowSpan(CellReference ref) {
		SheetDimensions span = getSpan(ref);
		if (span == null) {
			return 1;
		}
		return span.getRowCount() <= 0 ? -1 : span.getRowCount();
	}

	public int getColSpan(CellReference ref) {
		SheetDimensions span = getSpan(ref);
		if (span == null) {
			return 1;
		}
		return span.getColumnCount() <= 0 ? -1 : span.getColumnCount();
	}

	private AreaReference getArea(CellReference currentPos, SheetDimensions currentDim) {
		if (currentDim.getRowCount() >= 1 && currentDim.getColumnCount() >= 1) {
			// the currentPos is the top left corner
			return new AreaReference(currentPos, new CellReference(currentPos.getRowIndex() + currentDim.getRowCount()
					- 1, currentPos.getColumnIndex() + currentDim.getColumnCount() - 1));
		}
		CellReference topLeftCorner = new CellReference(currentPos.getRowIndex() + currentDim.getRowCount(),
				currentPos.getColumnIndex() + currentDim.getColumnCount());
		SheetDimensions topLeftDim = spans.get(topLeftCorner);
		if (topLeftDim == null) {
			throw new IllegalStateException("Could not find dimension for corner:" + topLeftCorner);
		}
		return getArea(topLeftCorner, topLeftDim);
	}

	/**
	 * if this area goes completely inside an existing area, then remove this span.<br>
	 * if this area is outside any other area, use as is.<br>
	 * if this area intersects existing areas, a new region containing all the cells will replace all
	 * 
	 * @param area
	 */
	public void toggleSpan(AreaReference area) {
		int firstRow = area.getFirstRowIndex();
		int lastRow = area.getLastRowIndex();
		int firstCol = area.getFirstColumnIndex();
		int lastCol = area.getLastColumnIndex();
		for (CellReference cell : area) {
			SheetDimensions dim = spans.get(cell);
			if (dim != null) {
				AreaReference foundArea = getArea(cell, dim);
				firstRow = Math.min(foundArea.getFirstRowIndex(), firstRow);
				lastRow = Math.max(foundArea.getLastRowIndex(), lastRow);
				firstCol = Math.min(foundArea.getFirstColumnIndex(), firstCol);
				lastCol = Math.max(foundArea.getLastColumnIndex(), lastCol);
				removeArea(foundArea);
				if (foundArea.contains(area)) {
					return;
				}
			}
		}
		addAreaDirectly(new AreaReference(null, firstRow, firstCol, lastRow, lastCol));

	}

	public List<AreaReference> getSpans() {
		List<AreaReference> spanList = new ArrayList<AreaReference>();
		for (Map.Entry<CellReference, SheetDimensions> entry : spans.entrySet()) {
			if (entry.getValue().getRowCount() >= 1 && entry.getValue().getColumnCount() >= 1) {
				spanList.add(getArea(entry.getKey(), entry.getValue()));
			}
		}
		return spanList;
	}
}
