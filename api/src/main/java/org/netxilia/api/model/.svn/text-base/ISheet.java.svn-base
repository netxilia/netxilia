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

import java.util.List;

import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IRowCommand;
import org.netxilia.api.command.ISheetCommand;
import org.netxilia.api.concurrent.IActor;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.event.SheetEventType;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;

public interface ISheet extends IActor {
	public IWorkbook getWorkbook();

	public String getName();

	public SheetFullName getFullName();

	public SheetType getType();

	public IListenableFuture<SheetData> receiveSheet();

	public IListenableFuture<CellData> receiveCell(CellReference ref);

	public IListenableFuture<Matrix<CellData>> receiveCells(AreaReference ref);

	public IListenableFuture<RowData> receiveRow(int rowIndex);

	public IListenableFuture<List<RowData>> receiveRows(Range range);

	public IListenableFuture<ColumnData> receiveColumn(int colIndex);

	public IListenableFuture<List<ColumnData>> receiveColumns(Range range);

	public IListenableFuture<ICellCommand> sendValue(CellReference ref, IGenericValue value);

	public IListenableFuture<ICellCommand> sendFormula(CellReference ref, Formula formula);

	/**
	 * it returns the commands to execute in order to undo the effect of the current command
	 * 
	 * @param command
	 * @return
	 */
	public IListenableFuture<ICellCommand> sendCommand(ICellCommand command);

	/**
	 * This command will return a future of a do nothing command.
	 * 
	 * @param command
	 * @return
	 */
	public IListenableFuture<ICellCommand> sendCommandNoUndo(ICellCommand command);

	public IListenableFuture<IRowCommand> sendCommand(IRowCommand command);

	public IListenableFuture<IColumnCommand> sendCommand(IColumnCommand command);

	public IListenableFuture<ISheetCommand> sendCommand(ISheetCommand command);

	/**
	 * enable or disable the automatic refresh of the formulas. Should be disabled when numerous formulas are added to
	 * the sheet.
	 * 
	 * @param enabled
	 */
	public void setRefreshEnabled(boolean enabled);

	public IListenableFuture<SheetDimensions> getDimensions();

	/**
	 * Sorts the given sheet using the sort specifier on the sheet's storage. An event of
	 * {@link SheetEventType#modified} is triggered instead of update of individual cell. The change can trigger
	 * modifications of the sheets referring the sorted sheet.
	 * 
	 * @param sheetName
	 * @param sortSpecifier
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	public IListenableFuture<Integer> sort(SortSpecifier sortSpecifier);

	public void addListener(ISheetEventListener listener);

	public void removeListener(ISheetEventListener listener);

}
