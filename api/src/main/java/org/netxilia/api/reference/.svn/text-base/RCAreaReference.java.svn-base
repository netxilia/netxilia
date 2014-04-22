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

public final class RCAreaReference {
	private final RCCellReference topLeft;
	private final RCCellReference bottomRight;

	public RCAreaReference(RCCellReference topLeft, RCCellReference bottomRight) throws IllegalArgumentException {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
		if (this.topLeft == null) {
			throw new IllegalArgumentException("TopLeft reference cannot be null");
		}
		if (this.bottomRight == null) {
			throw new IllegalArgumentException("BottomRight reference cannot be null");
		}
	}

	public RCAreaReference(CellReference contextCell, AreaReference areaRef) {
		topLeft = new RCCellReference(contextCell, areaRef.getTopLeft());
		bottomRight = areaRef.isOneCell() ? topLeft : new RCCellReference(contextCell, areaRef.getBottomRight());
	}

	public RCCellReference getTopLeft() {
		return topLeft;
	}

	public RCCellReference getBottomRight() {
		return bottomRight;
	}

	public boolean isOneCell() {
		return topLeft.getRowIndex() == bottomRight.getRowIndex()
				&& topLeft.getColumnIndex() == bottomRight.getColumnIndex();
	}

	public AreaReference getAbsoluteReference(CellReference contextCell) {
		CellReference tl = topLeft.getAbsoluteReference(contextCell);
		CellReference br = isOneCell() ? tl : bottomRight.getAbsoluteReference(contextCell);
		return new AreaReference(tl, br);
	}

	/**
	 * ex: "3:4"
	 * 
	 * @return
	 */
	public boolean isFullRow() {
		return topLeft.isInfiniteColumn();
	}

	/**
	 * ex: C:D
	 * 
	 * @return
	 */
	public boolean isFullColumn() {
		return topLeft.isInfiniteRow();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bottomRight == null) ? 0 : bottomRight.hashCode());
		result = prime * result + ((topLeft == null) ? 0 : topLeft.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RCAreaReference other = (RCAreaReference) obj;
		return bottomRight.equals(other.bottomRight) && topLeft.equals(other.topLeft);
	}

	@Override
	public String toString() {
		return topLeft.toString(true) + ":" + bottomRight.toString(false);
	}

}
