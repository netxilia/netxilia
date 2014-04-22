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
package org.netxilia.spi.impl.formula;

import java.util.Iterator;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.IGenericValue;

public class FilterRowsFormulaContext implements IFormulaContext {
	private final IFormulaContext delegate;

	private int rowDelta = 0;
	private int columnDelta = 0;

	public FilterRowsFormulaContext(IFormulaContext delegate) {
		this.delegate = delegate;
	}

	public int getRowDelta() {
		return rowDelta;
	}

	public void setRowDelta(int rowDelta) {
		this.rowDelta = rowDelta;
	}

	public int getColumnDelta() {
		return columnDelta;
	}

	public void setColumnDelta(int columnDelta) {
		this.columnDelta = columnDelta;
	}

	protected int getRowIndex(CellReference ref) {
		return ref.isAbsoluteRow() ? ref.getRowIndex() : ref.getRowIndex() + rowDelta;
	}

	protected int getColumnIndex(CellReference ref) {
		return ref.isAbsoluteColumn() ? ref.getColumnIndex() : ref.getColumnIndex() + columnDelta;
	}

	public IGenericValue getCellValue(CellReference ref) {
		CellReference shiftedReference = new CellReference(ref.getSheetName(), getRowIndex(ref), getColumnIndex(ref));
		return delegate.getCellValue(shiftedReference);
	}

	public CellReference getCell() {
		return delegate.getCell();
	}

	public AreaReference resolveAlias(AbsoluteAlias alias) {
		return delegate.resolveAlias(alias);
	}

	public ISheet getSheet() {
		return delegate.getSheet();
	}

	public Iterator<CellData> getCellIterator(AreaReference ref) {
		return delegate.getCellIterator(ref);
	}

}
