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
package org.netxilia.spi.impl.storage.db;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.utils.BlockEvent;
import org.netxilia.api.impl.utils.ISparseMatrix;
import org.netxilia.api.impl.utils.ISparseMatrixListener;
import org.netxilia.api.impl.utils.InsertMode;
import org.netxilia.api.impl.utils.OrderedBlockMatrix;
import org.netxilia.api.reference.AreaReference;

public class SparseMatrixCollection {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NetxiliaSystemImpl.class);

	private final Map<String, ISparseMatrix<String>> matrices = new HashMap<String, ISparseMatrix<String>>();

	private ISparseMatrixCollectionListener listener;

	public ISparseMatrix<String> getMatrix(String property) {
		ISparseMatrix<String> matrix = matrices.get(property);
		if (matrix == null) {
			matrix = new OrderedBlockMatrix<String>();
			matrix.addEntryListener(new PropertyMatrixListener(property));
			matrices.put(property, matrix);
		}
		return matrix;
	}

	public ISparseMatrixCollectionListener getListener() {
		return listener;
	}

	public void setListener(ISparseMatrixCollectionListener listener) {
		this.listener = listener;
	}

	public void put(AreaReference ref, String property, String value) {
		try {
			getMatrix(property).set(ref.getFirstRowIndex(), ref.getFirstColumnIndex(), ref.getLastRowIndex(),
					ref.getLastColumnIndex(), value);
		} catch (Exception e) {
			log.error("Cannot set property:" + property + " area " + ref + " value:" + value + ":" + e, e);
		}

	}

	public void put(int row, int column, String property, String value) {
		getMatrix(property).set(row, column, value);
	}

	public String get(int row, int column, String property) {
		return getMatrix(property).get(row, column);
	}

	public Collection<String> getProperties() {
		return matrices.keySet();
	}

	public void insertColumn(String property, int index) {
		getMatrix(property).insertColumn(index, InsertMode.split);
	}

	public void deleteColumn(String property, int index) {
		getMatrix(property).deleteColumn(index);
	}

	public void insertRow(String property, int index) {
		getMatrix(property).insertRow(index, InsertMode.split);

	}

	public void deleteRow(String property, int index) {
		getMatrix(property).deleteRow(index);
	}

	private class PropertyMatrixListener implements ISparseMatrixListener<String> {
		private final String property;

		public PropertyMatrixListener(String property) {
			this.property = property;
		}

		@Override
		public void onDeletedBlock(BlockEvent<String> event) {
			if (listener != null) {
				listener.onDeletedBlock(property, event);
			}

		}

		@Override
		public void onInsertedBlock(BlockEvent<String> event) {
			if (listener != null) {
				listener.onInsertedBlock(property, event);
			}
		}

	}

}
