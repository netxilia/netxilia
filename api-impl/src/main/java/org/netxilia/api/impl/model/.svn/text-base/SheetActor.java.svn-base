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
package org.netxilia.api.impl.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.concurrent.Future;

import org.netxilia.api.command.BlockCellCommandBuilder;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.IRowCommand;
import org.netxilia.api.command.ISheetCommand;
import org.netxilia.api.command.RowCommands;
import org.netxilia.api.concurrent.IFutureListener;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.event.CellEvent;
import org.netxilia.api.event.CellEventType;
import org.netxilia.api.event.ColumnEvent;
import org.netxilia.api.event.ColumnEventType;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.event.RowEvent;
import org.netxilia.api.event.RowEventType;
import org.netxilia.api.event.SheetEvent;
import org.netxilia.api.event.SheetEventType;
import org.netxilia.api.exception.AbandonedCalculationException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IPreloadedFormulaContext;
import org.netxilia.api.formula.IPreloadedFormulaContextFactory;
import org.netxilia.api.impl.IExecutorServiceFactory;
import org.netxilia.api.impl.concurrent.MutableFutureWithCounter;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.CellCreator;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.SortSpecifier;
import org.netxilia.api.model.SortSpecifier.SortColumn;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.reference.Range;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;
import org.netxilia.api.utils.Pair;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.spi.formula.IFormulaCalculator;
import org.netxilia.spi.formula.IFormulaCalculatorFactory;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.storage.ISheetStorageService;
import org.springframework.util.Assert;

public class SheetActor {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(SheetActor.class);

	enum SheetStatus {
		notInitiliazed, initializing, ready
	}

	private ISheet myReference;

	private final IFormulaCalculatorFactory formulaCalculatorFactory;

	private final IFormulaParser formulaParser;

	private final ISheetStorageService storageService;

	private final SheetEventSupport eventSupport;

	private final Workbook workbook;

	private final SheetFullName name;
	private final SheetType type;

	private boolean refreshEnabled = true;

	private SheetStatus status = SheetStatus.notInitiliazed;

	private final ISpringUserService userService;

	private final IMoreCellCommands moreCellCommands;

	private final IPreloadedFormulaContextFactory preloadedContextFactory;

	public SheetActor(Workbook workbook, SheetData sheetData, IFormulaCalculatorFactory formulaCalculatorFactory,
			IFormulaParser formulaParser, ISheetStorageService storageService,
			IExecutorServiceFactory executorServiceFactory, ISpringUserService userService,
			IMoreCellCommands moreCellCommands, IPreloadedFormulaContextFactory preloadedContextFactory) {
		Assert.notNull(workbook);
		Assert.notNull(sheetData);
		Assert.notNull(formulaCalculatorFactory);
		Assert.notNull(formulaParser);
		Assert.notNull(storageService);
		Assert.notNull(executorServiceFactory);
		Assert.notNull(userService);
		Assert.notNull(moreCellCommands);
		Assert.notNull(preloadedContextFactory);

		this.formulaCalculatorFactory = formulaCalculatorFactory;
		this.formulaParser = formulaParser;
		this.name = sheetData.getFullName();
		this.type = sheetData.getType();
		this.workbook = workbook;
		this.storageService = storageService;
		this.userService = userService;
		this.moreCellCommands = moreCellCommands;
		this.preloadedContextFactory = preloadedContextFactory;
		eventSupport = new SheetEventSupport(this.name, executorServiceFactory);
	}

	public ISheet getReference() {
		return myReference;
	}

	public SheetType getType() {
		return type;
	}

	public IWorkbook getWorkbook() {
		return workbook;
	}

	private void init() {
		if (status == SheetStatus.ready || status == SheetStatus.initializing) {
			return;
		}
		Assert.notNull(myReference);
		status = SheetStatus.initializing;
		if (workbook.isInitializationEnabled()) {
			new SheetInitializationProcess(this, formulaParser, workbook.getDependencyManager(),
					workbook.getAliasDependencyManager(), moreCellCommands);
		}
		status = SheetStatus.ready;
	}

	public SheetData getSheet() {
		try {
			return storageService.loadSheet();
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			throw new StorageException(e);
		}
	}

	public SheetDimensions getDimensions() {
		try {
			return storageService.getSheetDimensions();
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			throw new StorageException(e);
		}
	}

	public void setActorRef(ISheet proxy) {
		this.myReference = proxy;

	}

	public Matrix<CellData> getCells(AreaReference ref) {
		Assert.notNull(ref);
		Assert.isTrue(ref.getSheetName() == null || ref.getSheetName().equals(name.getSheetName()));

		init();

		try {
			return storageService.loadCells(ref);
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			// deleted externally !?
			throw new StorageException(e);
		}
	}

	public CellData getCell(CellReference ref) {
		Assert.notNull(ref);
		Assert.isTrue(ref.getSheetName() == null || ref.getSheetName().equals(name.getSheetName()));

		init();

		Matrix<CellData> data;
		try {
			data = storageService.loadCells(new AreaReference(ref));
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			// deleted externally !?
			throw new StorageException(e);
		}
		if (data.getColumnCount() == 0 || data.getRowCount() == 0) {
			return null;
		}
		return data.get(0, 0);
	}

	public SheetFullName getFullName() {
		return name;
	}

	public String getName() {
		return name.getSheetName();
	}

	private List<CellData> moveRow(List<CellData> cells, int fromRow, int toRow) throws FormulaParsingException {
		List<CellData> newCells = new ArrayList<CellData>(cells.size());
		for (int c = 0; c < cells.size(); ++c) {
			CellData cell = cells.get(c);
			if (cell.getFormula() == null) {
				newCells.add(cell);
			} else {
				IReferenceTransformer transformer = ReferenceTransformers.shiftCell(
						new CellReference(name.getSheetName(), fromRow, c), new CellReference(name.getSheetName(),
								toRow, c));
				newCells.add(cell.withFormula(formulaParser.transformFormula(cell.getFormula(), transformer)));
			}
		}
		return newCells;
	}

	/**
	 * Sort the rows of this sheet using the sort specifier. It will modify the position of the rows within the sheet.
	 * It generates an update modification type for each row, to only update the rows order. The cells should also
	 * update their row indices.
	 * 
	 * TODO should have formula's stored in a relative way. Thus no modification is needed to be done to formulas when
	 * sorting.
	 * 
	 * @param sortSpecifier
	 * @throws NetxiliaBusinessException
	 * @throws CyclicDependenciesException
	 */
	public int sort(SortSpecifier sortSpecifier) throws CyclicDependenciesException, NetxiliaBusinessException {
		init();

		Matrix<CellData> cells = getCells(AreaReference.ALL);
		List<RowDataHolder> sortedRows = new ArrayList<RowDataHolder>(cells.getRowCount());
		for (int i = 0; i < cells.getRowCount(); ++i) {
			sortedRows.add(new RowDataHolder(cells, i));
		}
		List<RowData> rowData = getRows(Range.ALL);

		Collections.sort(sortedRows, new RowDataHolderComparator(sortSpecifier));

		int changes = 0;
		for (int i = 0; i < sortedRows.size(); ++i) {
			RowDataHolder row = sortedRows.get(i);
			if (i == row.getIndex()) {
				// nothing changed
				continue;
			}
			// replace the content of the new row
			RowData prevRowData = rowData.get(row.getIndex());
			sendCommand(RowCommands.row(Range.range(i), prevRowData.getHeight(), prevRowData.getStyles()));

			sendCommand(CellCommands.row(new AreaReference(name.getSheetName(), i, 0, i, cells.getColumnCount() - 1),
					moveRow(cells.getRow(row.getIndex()), row.getIndex(), i)));
			changes++;

		}
		return changes;
	}

	/**
	 * reorder the sheet rows using the given list of changes. In each pair the first is the index of the row to be
	 * moved and the second is the position where the row should arrive
	 * 
	 * @param rowOrders
	 */
	public void reorder(Collection<Pair<Integer, Integer>> rowOrders) {
		Assert.notNull(rowOrders);
	}

	public void addListener(ISheetEventListener listener) {
		eventSupport.removeListener(listener);
		eventSupport.addListener(listener);
	}

	public void removeListener(ISheetEventListener listener) {
		eventSupport.removeListener(listener);
	}

	public ColumnData getColumn(int colIndex) {
		init();

		List<ColumnData> result = getColumns(Range.range(colIndex));
		return result.size() > 0 ? result.get(0) : null;
	}

	public List<ColumnData> getColumns(Range range) {
		init();

		try {
			return storageService.loadColumns(range);
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			throw new StorageException(e);
		}
	}

	public RowData getRow(int rowIndex) {
		init();

		List<RowData> result = getRows(Range.range(rowIndex));
		return result.size() > 0 ? result.get(0) : null;
	}

	public List<RowData> getRows(Range range) {
		init();

		try {
			return storageService.loadRows(range);
		} catch (StorageException e) {
			throw e;
		} catch (NotFoundException e) {
			throw new StorageException(e);
		}
	}

	public void setValue(CellReference ref, IGenericValue value) throws NetxiliaBusinessException,
			CyclicDependenciesException {
		Assert.notNull(ref);
		init();

		sendCommand(CellCommands.value(new AreaReference(ref), value));
	}

	public void setFormula(CellReference ref, Formula formula) throws NetxiliaBusinessException,
			CyclicDependenciesException {
		Assert.notNull(ref);
		init();

		sendCommand(CellCommands.formula(new AreaReference(ref), formula));
	}

	private void saveCell(CellDataWithProperties dataWithProperties, ICellCommand command) throws StorageException,
			NotFoundException {
		if (dataWithProperties.getProperties().isEmpty()) {
			return;
		}
		saveCells(Collections.singletonList(dataWithProperties), command);
	}

	private void saveCells(List<CellDataWithProperties> saveCells, ICellCommand command) throws StorageException,
			NotFoundException {
		storageService.saveCells(saveCells);
		for (CellDataWithProperties saveCell : saveCells) {
			sendEvents(saveCell, command);
			command.done(myReference, saveCell);
		}
	}

	/**
	 * saves the cells and send the event
	 * 
	 * @param newCell
	 * @param properties
	 * @param eventToSend
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	private void sendEvents(CellDataWithProperties dataWithProperties, ICellCommand command) throws StorageException,
			NotFoundException {

		if (!command.isStopPropagation() && refreshEnabled) {
			workbook.getDependencyManager().getManagerForSheet(name.getSheetName())
					.propagateCellValue(dataWithProperties.getCellData(), dataWithProperties.getProperties());
		}
		if (eventSupport.hasListeners()) {
			eventSupport.fireEvent(new CellEvent(CellEventType.modified, getFullName(), dataWithProperties
					.getCellData(), dataWithProperties.getProperties()));
		}
	}

	/**
	 * This method will launch a formula calculator for the given cell
	 * 
	 * @param oldCell
	 * @param newCellWithProps
	 * @param command
	 * @throws FormulaParsingException
	 * @throws CyclicDependenciesException
	 * @throws StorageException
	 * @throws NotFoundException
	 */
	private void calculateFormula(final MutableFutureWithCounter<ICellCommand> result, final CellData oldCell,
			final CellData newCell, final ICellCommand command) throws FormulaParsingException,
			CyclicDependenciesException, StorageException, NotFoundException {
		// check formula and set dependencies before
		final IPreloadedFormulaContext context = preloadedContextFactory.newPreloadAliasesContext(myReference,
				newCell.getReference(), newCell.getFormula(), myReference.getExecutor());

		context.load(new Runnable() {
			@Override
			public void run() {
				try {
					workbook.getDependencyManager().setDependencies(newCell.getFormula(), context);
					workbook.getAliasDependencyManager().setAliasDependencies(newCell.getFormula(), context);

					// start actor
					IFormulaCalculator calculator = formulaCalculatorFactory.getCalculator(myReference, newCell);
					calculator.sendCalculate().addListener(
							new FutureListenerWithUser<CellData>(userService, new IFutureListener<CellData>() {
								@Override
								public void ready(Future<CellData> future) {
									CellData data;
									CellDataWithProperties newCellWithProperties = null;
									try {
										data = future.get();
										Collection<CellData.Property> properties = CellData.diff(oldCell, data);
										newCellWithProperties = new CellDataWithProperties(data, properties);
										saveCell(newCellWithProperties, command);
										result.decrement();
									} catch (Exception e) {
										if (e.getCause() instanceof AbandonedCalculationException) {
											log.info("Abandoned:" + oldCell.getReference());
										} else {
											result.setException(e);
										}
									} finally {
										formulaCalculatorFactory.removeCalculator(myReference, newCell.getReference());
									}

								}
							}), myReference.getExecutor());
				} catch (Exception e) {
					result.setException(e);
					return;
				}

			}
		});

	}

	/**
	 * Send the given command to storage and listeners
	 * 
	 * @param command
	 * @return
	 * @throws NetxiliaBusinessException
	 * @throws CyclicDependenciesException
	 */
	public IListenableFuture<ICellCommand> sendCommandNoUndo(ICellCommand command) throws CyclicDependenciesException,
			NetxiliaBusinessException {
		Assert.notNull(command);
		return sendCommand(command, false);
	}

	public IListenableFuture<ICellCommand> sendCommand(ICellCommand command) throws NetxiliaBusinessException,
			CyclicDependenciesException {
		return sendCommand(command, true);
	}

	private IListenableFuture<ICellCommand> sendCommand(ICellCommand command, boolean withUndo)
			throws NetxiliaBusinessException, CyclicDependenciesException {
		Assert.notNull(command);

		init();

		Matrix<CellData> cells = null;
		// special construction to add a new row
		if (command.getTarget().getFirstRowIndex() == CellReference.LAST_ROW_INDEX) {
			SheetDimensions dims = storageService.getSheetDimensions();
			cells = new MatrixBuilder<CellData>(new CellCreator(name.getSheetName(), dims.getRowCount(), command
					.getTarget().getFirstColumnIndex())).setSize(1, command.getTarget().getColumnCount()).build();
		} else {
			cells = getCells(command.getTarget());
			// build a matrix of the needed size if the returned matrix is smaller
			if (cells.getRowCount() < command.getTarget().getRowCount()
					|| cells.getColumnCount() < command.getTarget().getColumnCount()) {
				cells = new MatrixBuilder<CellData>(cells, new CellCreator(name.getSheetName(), command.getTarget()
						.getFirstRowIndex(), command.getTarget().getFirstColumnIndex())).setSize(
						command.getTarget().getRowCount(), command.getTarget().getColumnCount()).build();

			}
		}
		BlockCellCommandBuilder commandBuilder = withUndo ? new BlockCellCommandBuilder() : null;

		List<CellDataWithProperties> saveCells = new ArrayList<CellDataWithProperties>();
		MutableFutureWithCounter<ICellCommand> result = new MutableFutureWithCounter<ICellCommand>(cells.size());

		for (CellData cell : cells) {
			CellDataWithProperties newCellWithProps = command.apply(cell);
			if (newCellWithProps.getProperties().size() == 0) {
				// nothing changed
				result.decrement();
				continue;
			}

			if (newCellWithProps.getProperties().contains(CellData.Property.value)
					&& !newCellWithProps.getProperties().contains(CellData.Property.formula)
					&& newCellWithProps.getCellData().getFormula() != null) {
				// set formula to null when a value is set
				EnumSet<CellData.Property> newProps = EnumSet.copyOf(newCellWithProps.getProperties());
				newProps.add(CellData.Property.formula);
				newCellWithProps = new CellDataWithProperties(newCellWithProps.getCellData().withFormula(null),
						newProps);
			}
			if (commandBuilder != null) {
				commandBuilder.command(CellCommands.properties(new AreaReference(cell.getReference()), cell.getValue(),
						cell.getStyles(), cell.getFormula(), newCellWithProps.getProperties()));
			}

			if (newCellWithProps.getProperties().contains(CellData.Property.formula) && refreshEnabled) {
				// the formula changed. this needs the formula evaluation before setting the value
				calculateFormula(result, cell, newCellWithProps.getCellData(), command);
				continue;
			}

			// TODO: the storage may not be able to store one of the cells - should break all for one cell !?
			saveCells.add(newCellWithProps);
			result.decrement();
		}
		saveCells(saveCells, command);

		if (commandBuilder == null || commandBuilder.isEmpty()) {
			result.set(CellCommands.doNothing(command.getTarget()));
		} else {
			result.set(commandBuilder.build());
		}
		return result;
	}

	private List<RowData> fillRows(List<RowData> rows, int startIndex, int count) {
		List<RowData> newRows = new ArrayList<RowData>(rows);
		for (int i = 0; i < count; ++i) {
			newRows.add(new RowData(startIndex + i, 0, null));
		}
		return newRows;
	}

	private void rowEvent(RowEventType type, RowData row, Collection<RowData.Property> properties) {
		if (eventSupport.hasListeners()) {
			eventSupport.fireEvent(new RowEvent(type, getFullName(), row, properties));
		}
	}

	/**
	 * Send the given command to storage and listeners
	 * 
	 * @param command
	 * @return
	 * @throws StorageException
	 * @throws NetxiliaBusinessException
	 */
	public IRowCommand sendCommand(IRowCommand command) throws StorageException, NetxiliaBusinessException {
		Assert.notNull(command);
		init();

		if (command.toInsert()) {
			// these are inserted rows
			List<RowData> rows = fillRows(Collections.<RowData> emptyList(), command.getTarget().getMin(), command
					.getTarget().count());
			for (RowData row : rows) {
				RowData newRow = command.apply(row);
				Collection<RowData.Property> properties = RowData.diff(row, newRow);
				if (newRow != null) {
					storageService.insertRow(newRow, properties);
					workbook.getDependencyManager().getManagerForSheet(name.getSheetName())
							.insertRow(newRow.getIndex());
					workbook.getAliasDependencyManager().getManagerForSheet(name.getSheetName())
							.insertRow(newRow.getIndex());
					rowEvent(RowEventType.inserted, newRow, properties);
				} else {// the command decided not to insert the new row
					continue;
				}
			}
			return null;
		}

		// modified or deleted rows

		List<RowData> rows = getRows(command.getTarget());
		if (!command.getTarget().equals(Range.ALL) && command.getTarget().count() != rows.size()) {
			// add needed rows
			int startInsertedRow = command.getTarget().getMin() + rows.size();
			rows = fillRows(rows, startInsertedRow, command.getTarget().count() - rows.size());
		}
		for (RowData row : rows) {
			RowData newRow = command.apply(row);
			Collection<RowData.Property> properties = RowData.diff(row, newRow);

			if (properties.size() == 0) {
				// nothing changed
				continue;
			}

			if (newRow == null) {
				storageService.deleteRow(row.getIndex());
				workbook.getDependencyManager().getManagerForSheet(name.getSheetName()).deleteRow(row.getIndex());
				workbook.getAliasDependencyManager().getManagerForSheet(name.getSheetName()).deleteRow(row.getIndex());
				rowEvent(RowEventType.deleted, row, properties);
			} else {
				storageService.saveRow(newRow, properties);
				rowEvent(RowEventType.modified, newRow, properties);
			}
		}
		return null;
	}

	private List<ColumnData> fillColumns(List<ColumnData> columns, int startIndex, int count) {
		List<ColumnData> newColumns = new ArrayList<ColumnData>(columns);
		for (int i = 0; i < count; ++i) {
			newColumns.add(new ColumnData(startIndex + i, 0, null));
		}
		return newColumns;
	}

	private void columnEvent(ColumnEventType type, ColumnData column, Collection<ColumnData.Property> properties) {
		if (eventSupport.hasListeners()) {
			eventSupport.fireEvent(new ColumnEvent(type, getFullName(), column, properties));
		}
	}

	/**
	 * Send the given command to storage and listeners
	 * 
	 * @param command
	 * @return
	 * @throws StorageException
	 * @throws NetxiliaBusinessException
	 */
	public IColumnCommand sendCommand(IColumnCommand command) throws StorageException, NetxiliaBusinessException {
		Assert.notNull(command);
		init();

		if (command.toInsert()) {
			// these are inserted rows
			List<ColumnData> columns = fillColumns(Collections.<ColumnData> emptyList(), command.getTarget().getMin(),
					command.getTarget().count());
			for (ColumnData col : columns) {
				ColumnData newCol = command.apply(col);
				Collection<ColumnData.Property> properties = ColumnData.diff(col, newCol);
				if (newCol != null) {
					storageService.insertColumn(newCol, properties);
					workbook.getDependencyManager().getManagerForSheet(name.getSheetName())
							.insertColumn(newCol.getIndex());
					workbook.getAliasDependencyManager().getManagerForSheet(name.getSheetName())
							.insertColumn(newCol.getIndex());
					columnEvent(ColumnEventType.inserted, newCol, properties);
				} else {// the command decided not to insert the new row
					continue;
				}
			}
			return null;
		}

		List<ColumnData> columns = getColumns(command.getTarget());

		if (!command.getTarget().equals(Range.ALL) && command.getTarget().count() != columns.size()) {
			// add needed columns
			int startInsertedColumn = command.getTarget().getMin() + columns.size();
			columns = fillColumns(columns, startInsertedColumn, command.getTarget().count() - columns.size());
		}

		for (ColumnData col : columns) {
			ColumnData newCol = command.apply(col);
			Collection<ColumnData.Property> properties = ColumnData.diff(col, newCol);

			if (properties.size() == 0) {
				// nothing changed
				continue;
			}

			if (newCol == null) {
				storageService.deleteColumn(col.getIndex());
				workbook.getDependencyManager().getManagerForSheet(name.getSheetName()).deleteColumn(col.getIndex());
				workbook.getAliasDependencyManager().getManagerForSheet(name.getSheetName())
						.deleteColumn(col.getIndex());
				columnEvent(ColumnEventType.deleted, col, properties);
			} else {
				storageService.saveColumn(newCol, properties);
				columnEvent(ColumnEventType.modified, newCol, properties);
			}
		}
		return null;
	}

	public ISheetCommand sendCommand(ISheetCommand command) throws StorageException, NotFoundException {
		Assert.notNull(command);
		init();

		SheetData sheet = getSheet();
		SheetData newSheet = command.apply(sheet);
		Collection<SheetData.Property> properties = SheetData.diff(sheet, newSheet);
		if (properties.size() == 0) {
			// nothing changed
			return null;
		}

		storageService.saveSheet(newSheet, properties);
		workbook.getAliasDependencyManager().getManagerForSheet(name.getSheetName()).saveSheet(newSheet, properties);
		eventSupport.fireEvent(new SheetEvent(SheetEventType.modified, newSheet, properties));
		return null;
	}

	/**
	 * this class holds the row index while the cells are sorted.
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 */
	private class RowDataHolder {
		private final Matrix<CellData> cells;
		private final int index;

		public RowDataHolder(Matrix<CellData> cells, int index) {
			this.cells = cells;
			this.index = index;
		}

		public Matrix<CellData> getCells() {
			return cells;
		}

		public int getIndex() {
			return index;
		}

	}

	/**
	 * compares rows according to the value in a given column
	 * 
	 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
	 * 
	 */
	public class RowDataHolderComparator implements Comparator<RowDataHolder> {
		private final SortSpecifier sortSpecifier;

		public RowDataHolderComparator(SortSpecifier sortSpecifier) {
			this.sortSpecifier = sortSpecifier;
		}

		public SortSpecifier getSortSpecifier() {
			return sortSpecifier;
		}

		private int checkNulls(Object obj1, Object obj2) {
			if (obj1 == null && obj2 == null) {
				return 0;
			}
			if (obj1 == null) {
				return -1;
			}
			if (obj2 == null) {
				return 1;
			}
			return 2;
		}

		@Override
		public int compare(RowDataHolder row1, RowDataHolder row2) {
			int result = 0;
			for (SortColumn sortColumn : sortSpecifier.getColumns()) {
				int columnIndex = CellReference.columnIndex(sortColumn.getName());
				IGenericValue cell1 = row1.getCells().get(row1.getIndex(), columnIndex).getValue();
				IGenericValue cell2 = row2.getCells().get(row2.getIndex(), columnIndex).getValue();
				result = checkNulls(cell1, cell2);
				if (result != 2) {
					return sortColumn.getOrder() == SortSpecifier.SortOrder.ascending ? result : -result;
				}
				result = checkNulls(cell1, cell2);
				if (result != 2) {
					return sortColumn.getOrder() == SortSpecifier.SortOrder.ascending ? result : -result;
				}
				result = cell1.compareTo(cell2);
				if (result != 0) {
					return sortColumn.getOrder() == SortSpecifier.SortOrder.ascending ? result : -result;
				}
			}
			return 0;
		}
	}

	public void setRefreshEnabled(boolean enabled) {
		refreshEnabled = enabled;

	}

}
