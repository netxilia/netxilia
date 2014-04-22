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
package org.netxilia.api.utils;

import java.util.ArrayList;
import java.util.List;

public class MatrixBuilder<E> {

	private List<List<E>> elements;
	private final IMatrixElementCreator<E> elementCreator;

	public MatrixBuilder(E nullElement) {
		this(sameElement(nullElement));
	}

	public MatrixBuilder(IMatrixElementCreator<E> elementCreator) {
		if (elementCreator == null) {
			throw new NullPointerException();
		}
		elements = new ArrayList<List<E>>();
		this.elementCreator = elementCreator;
	}

	public MatrixBuilder(Matrix<E> matrix, final E nullElement) {
		this(matrix, sameElement(nullElement));
	}

	public MatrixBuilder(Matrix<E> matrix, IMatrixElementCreator<E> elementCreator) {
		elements = new ArrayList<List<E>>(matrix.getRowCount());
		for (List<E> row : matrix.getRows()) {
			elements.add(new ArrayList<E>(row));
		}
		this.elementCreator = elementCreator;
	}

	private static <T> IMatrixElementCreator<T> sameElement(final T nullElement) {
		return new IMatrixElementCreator<T>() {
			@Override
			public T newElement(int row, int column) {
				return nullElement;
			}
		};
	}

	public int getColumnCount() {
		if (elements.size() == 0) {
			return 0;
		}
		return elements.get(0).size();
	}

	private List<E> emptyRow(int row) {
		return CollectionUtils.atLeastSize(new ArrayList<E>(), getColumnCount(), new CellCreator(elementCreator, row));
	}

	private void elementsWithSize(int rowCount, int columnCount) {
		if (rowCount <= elements.size() && columnCount <= getColumnCount()) {
			return;
		}

		CellCreator cellCreator = new CellCreator(elementCreator, 0);

		if (rowCount > elements.size()) {
			RowCreator rowCreator = new RowCreator(cellCreator, Math.max(columnCount, getColumnCount()));
			elements = CollectionUtils.atLeastSize(elements, rowCount, rowCreator);
		}
		if (columnCount > getColumnCount()) {
			for (int i = 0; i < elements.size(); ++i) {
				List<E> row = elements.get(i);
				if (cellCreator != null) {
					cellCreator.setRow(i);
				}
				elements.set(i, CollectionUtils.atLeastSize(row, columnCount, cellCreator));
			}
		}
	}

	public MatrixBuilder<E> set(int r, int c, E element) {
		elementsWithSize(r + 1, c + 1);
		elements.get(r).set(c, element);
		return this;
	}

	public MatrixBuilder<E> removeColumn(int columnIndex) {
		for (List<E> row : elements) {
			row.remove(columnIndex);
		}
		return this;
	}

	public MatrixBuilder<E> removeRow(int rowIndex) {
		elements.remove(rowIndex);
		return this;
	}

	public MatrixBuilder<E> insertRow(int index) {
		elements.add(index, emptyRow(index));
		return this;
	}

	public MatrixBuilder<E> setSize(int rowCount, int columnCount) {
		elementsWithSize(rowCount, columnCount);
		return this;
	}

	public Matrix<E> build() {
		return new Matrix<E>(elements);
	}

	private class CellCreator implements IListElementCreator<E> {
		private final IMatrixElementCreator<E> elementCreator;
		private int row;

		public CellCreator(IMatrixElementCreator<E> elementCreator, int row) {
			this.elementCreator = elementCreator;
			this.row = row;
		}

		public void setRow(int row) {
			this.row = row;
		}

		public E newElement(int column) {
			return elementCreator.newElement(row, column);
		}

	}

	private class RowCreator implements IListElementCreator<List<E>> {
		private final CellCreator cellCreator;
		private final int columnCount;

		public RowCreator(CellCreator cellCreator, int columnCount) {
			this.cellCreator = cellCreator;
			this.columnCount = columnCount;
		}

		@Override
		public List<E> newElement(int row) {
			if (cellCreator != null) {
				cellCreator.setRow(row);
			}
			List<E> newRow = CollectionUtils.atLeastSize(new ArrayList<E>(), columnCount, cellCreator);
			return newRow;
		}

	}
}
