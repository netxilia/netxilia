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

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.google.common.collect.ImmutableList;

public class Matrix<E> implements Iterable<E> {
	private final List<List<E>> elements;

	public Matrix(List<List<E>> elements) {
		// assumes all lists have the same size
		ImmutableList.Builder<List<E>> builder = ImmutableList.<List<E>> builder();
		for (List<E> row : elements) {
			builder.add(ImmutableList.copyOf(row));
		}
		this.elements = builder.build();
	}

	/**
	 * Special internal constructor
	 * 
	 * @param elements
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	Matrix(ImmutableList<ImmutableList<E>> elements, boolean internal) {
		this.elements = (List) elements;
	}

	public Matrix() {
		this(ImmutableList.<List<E>> of());
	}

	public List<List<E>> getRows() {
		return elements;
	}

	public List<E> getRow(int i) {
		return elements.get(i);
	}

	public int getRowCount() {
		return elements.size();
	}

	public int getColumnCount() {
		if (elements.size() == 0) {
			return 0;
		}
		return elements.get(0).size();
	}

	public E get(int r, int c) {
		return elements.get(r).get(c);
	}

	public int size() {
		return getRowCount() * getColumnCount();
	}

	public Matrix<E> subMatrix(int firstRow, int firstColumn, int lastRow, int lastColumn) {
		if (firstRow == 0 && firstColumn == 0 && lastRow == getRowCount() && lastColumn == getColumnCount()) {
			return this;
		}
		// TODO optimize using offsets here
		ImmutableList.Builder<ImmutableList<E>> builder = ImmutableList.<ImmutableList<E>> builder();
		for (int r = firstRow; r < lastRow; ++r) {
			builder.add((ImmutableList<E>) elements.get(r).subList(firstColumn, lastColumn));
		}
		return new Matrix<E>(builder.build(), true);

	}

	public Iterator<E> iterator() {
		return new MatrixIterator();
	}

	private class MatrixIterator implements Iterator<E> {
		private Iterator<List<E>> rowIterator;
		private Iterator<E> iteratorInTheRow = null;
		private Boolean hasNextValue;
		private E nextValue;

		public MatrixIterator() {
			rowIterator = elements.iterator();

		}

		@Override
		public boolean hasNext() {
			if (hasNextValue == null) {
				moveToNext();
			}
			return hasNextValue;
		}

		@Override
		public E next() {
			if (hasNextValue == null) {
				moveToNext();
			}
			if (!hasNextValue) {
				throw new NoSuchElementException();
			}
			hasNextValue = null;
			return nextValue;
		}

		protected void moveToNext() {
			while (iteratorInTheRow == null || !iteratorInTheRow.hasNext()) {
				if (rowIterator.hasNext()) {
					iteratorInTheRow = rowIterator.next().iterator();
				} else {
					break;
				}
			}

			if (iteratorInTheRow != null && iteratorInTheRow.hasNext()) {
				nextValue = iteratorInTheRow.next();
				hasNextValue = true;
			} else {
				hasNextValue = false;
			}

		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	@Override
	public String toString() {

		return elements.toString();
	}

}
