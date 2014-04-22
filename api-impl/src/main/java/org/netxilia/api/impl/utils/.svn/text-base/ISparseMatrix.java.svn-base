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
package org.netxilia.api.impl.utils;

import java.util.Collection;
import java.util.List;

/**
 * This is a kind of special matrix where large blocks of cells may contain the same information. <br>
 * The implementations should aim to minimize the memory occupied by collapsing when needed the blocks. The matrix does
 * not have a predefined size. It adapts to the indexed used when setting values.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 * @param <V>
 *            the value kept in the cell
 */
public interface ISparseMatrix<V> {
	public V get(int row, int col);

	public void set(int row, int col, V value);

	/**
	 * set the same value to the entire block
	 * 
	 * @param firstRow
	 * @param firstCol
	 * @param lastRow
	 * @param lastCol
	 * @param value
	 */
	public void set(int firstRow, int firstCol, int lastRow, int lastCol, V value);

	public List<? extends ISparseMatrixEntry<V>> insertRow(int row, InsertMode insertMode);

	public List<? extends ISparseMatrixEntry<V>> deleteRow(int row);

	public List<? extends ISparseMatrixEntry<V>> insertColumn(int column, InsertMode insertMode);

	public List<? extends ISparseMatrixEntry<V>> deleteColumn(int column);

	/**
	 * 
	 * @return the number of collapsed blocks (not the actual size of the matrix)
	 */
	public int getBlockCount();

	public Collection<? extends ISparseMatrixEntry<V>> entries();

	public void addEntryListener(ISparseMatrixListener<V> listener);

	public void removeEntryListener(ISparseMatrixListener<V> listener);
}
