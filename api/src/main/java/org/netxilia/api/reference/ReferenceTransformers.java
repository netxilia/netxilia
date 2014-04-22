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


public class ReferenceTransformers {
	public static IReferenceTransformer deleteRow(final int row) {
		return new IReferenceTransformer() {
			@Override
			public CellReference transform(CellReference ref) {
				return ReferenceUtils.deleteRow(ref, row);
			}

			@Override
			public AreaReference transform(AreaReference ref) {
				return ReferenceUtils.deleteRow(ref, row);
			}
		};
	}

	public static IReferenceTransformer insertRow(final int row) {
		return new IReferenceTransformer() {
			@Override
			public CellReference transform(CellReference ref) {
				return ReferenceUtils.insertRow(ref, row);
			}

			@Override
			public AreaReference transform(AreaReference ref) {
				return ReferenceUtils.insertRow(ref, row);
			}
		};
	}

	public static IReferenceTransformer deleteColumn(final int col) {
		return new IReferenceTransformer() {
			@Override
			public CellReference transform(CellReference ref) {
				return ReferenceUtils.deleteColumn(ref, col);
			}

			@Override
			public AreaReference transform(AreaReference ref) {
				return ReferenceUtils.deleteColumn(ref, col);
			}
		};
	}

	public static IReferenceTransformer insertColumn(final int col) {
		return new IReferenceTransformer() {
			@Override
			public CellReference transform(CellReference ref) {
				return ReferenceUtils.insertColumn(ref, col);
			}

			@Override
			public AreaReference transform(AreaReference ref) {
				return ReferenceUtils.insertColumn(ref, col);
			}
		};
	}

	public static IReferenceTransformer shiftCell(final CellReference referenceCell, final CellReference targetCell) {
		return new IReferenceTransformer() {
			@Override
			public CellReference transform(CellReference ref) {
				// TODO what to do if targetCell and referenceCell are not from the same Sheet !?
				if (ref.getSheetName() != null && ref.getSheetName().equals(referenceCell.getSheetName())) {
					// this cell belongs to other sheet
					return ref;
				}
				if (ref.isAbsoluteColumn() && ref.isAbsoluteRow()) {
					// this cell is fully absolute reference
					return ref;
				}

				int newColumn = ref.getColumnIndex();
				if (!ref.isAbsoluteColumn() && !ref.isInfiniteColumn()) {
					newColumn += targetCell.getColumnIndex() - referenceCell.getColumnIndex();
				}
				int newRow = ref.getRowIndex();
				if (!ref.isAbsoluteRow() && !ref.isInfiniteRow()) {
					newRow += targetCell.getRowIndex() - referenceCell.getRowIndex();
				}
				if (newRow < 0 || newColumn < 0) {
					return null;
				}
				return new CellReference(null, newRow, newColumn);
			}

			@Override
			public AreaReference transform(AreaReference ref) {
				CellReference newTowLeft = transform(ref.getTopLeft());
				CellReference newBottomRight = transform(ref.getBottomRight());
				if (newTowLeft != null && newBottomRight != null) {
					return new AreaReference(newTowLeft, newBottomRight);
				}
				return null;
			}
		};

	}

	public static IReferenceTransformer unchanged() {
		return new IReferenceTransformer() {

			@Override
			public AreaReference transform(AreaReference ref) {
				return ref;
			}

			@Override
			public CellReference transform(CellReference ref) {
				return ref;
			}
		};
	}

	public static IReferenceTransformer excludeSheet(final String sheetName) {
		return new IReferenceTransformer() {

			@Override
			public AreaReference transform(AreaReference ref) {
				return sheetName.equals(ref.getSheetName()) ? null : ref;
			}

			@Override
			public CellReference transform(CellReference ref) {
				return sheetName.equals(ref.getSheetName()) ? null : ref;
			}
		};
	}

}
