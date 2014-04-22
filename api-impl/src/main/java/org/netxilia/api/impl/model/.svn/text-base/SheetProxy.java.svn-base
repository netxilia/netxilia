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

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IColumnCommand;
import org.netxilia.api.command.IRowCommand;
import org.netxilia.api.command.ISheetCommand;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.concurrent.NetxiliaListenableFutureAdapter;
import org.netxilia.api.event.ISheetEventListener;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.formula.CyclicDependenciesException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.concurrent.MutableFuture;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.SortSpecifier;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.springframework.util.Assert;

import com.google.common.base.Function;
import com.google.common.util.concurrent.Futures;

public class SheetProxy implements ISheet {

	private final SheetActor sheetActor;
	private final ExecutorService executorService;
	private final ISpringUserService userService;
	private final IAclService aclService;

	public SheetProxy(ExecutorService executorService, ISpringUserService userService, IAclService aclService,
			SheetActor actor) {
		Assert.notNull(executorService);
		Assert.notNull(userService);
		Assert.notNull(actor);
		this.sheetActor = actor;
		this.executorService = executorService;
		this.userService = userService;
		this.aclService = aclService;
	}

	@Override
	public String getName() {
		return sheetActor.getName();
	}

	@Override
	public SheetFullName getFullName() {
		return sheetActor.getFullName();
	}

	@Override
	public SheetType getType() {
		return sheetActor.getType();
	}

	@Override
	public IWorkbook getWorkbook() {
		return sheetActor.getWorkbook();
	}

	@Override
	public void addListener(ISheetEventListener listener) {
		sheetActor.addListener(listener);
	}

	@Override
	public void removeListener(ISheetEventListener listener) {
		sheetActor.removeListener(listener);
	}

	@Override
	public Executor getExecutor() {
		return executorService;
	}

	private <V> IListenableFuture<V> submitRead(Callable<V> callable) {
		aclService.checkPermission(this.getFullName(), Permission.read);
		return (IListenableFuture<V>) executorService.submit(new CallableWithUser<V>(userService, callable));
	}

	private <V> IListenableFuture<V> submitWrite(Callable<V> callable) {
		aclService.checkPermission(this.getFullName(), Permission.write);
		return (IListenableFuture<V>) executorService.submit(new CallableWithUser<V>(userService, callable));
	}

	/**
	 * use this construction when the returned result from the actor is a Future itself.
	 * 
	 * @param <T>
	 * @param cmd
	 * @return
	 */
	private <T> IListenableFuture<T> delayedWriteCall(final T cmd) {
		return submitWrite(new Callable<T>() {
			@Override
			public T call() throws Exception {
				return cmd;
			}
		});
	}

	@Override
	public IListenableFuture<ICellCommand> sendFormula(final CellReference ref, final Formula formula) {
		return sendCommand(CellCommands.formula(new AreaReference(ref), formula));
	}

	@Override
	public IListenableFuture<ICellCommand> sendValue(final CellReference ref, final IGenericValue value) {
		return sendCommand(CellCommands.value(new AreaReference(ref), value));

	}

	@Override
	public IListenableFuture<SheetData> receiveSheet() {
		return submitRead(new Callable<SheetData>() {
			@Override
			public SheetData call() {
				return sheetActor.getSheet();
			}
		});
	}

	@Override
	public IListenableFuture<CellData> receiveCell(final CellReference ref) {
		return submitRead(new Callable<CellData>() {
			@Override
			public CellData call() {
				return sheetActor.getCell(ref);
			}
		});
	}

	@Override
	public IListenableFuture<Matrix<CellData>> receiveCells(final AreaReference ref) {
		return submitRead(new Callable<Matrix<CellData>>() {
			@Override
			public Matrix<CellData> call() {
				return sheetActor.getCells(ref);
			}
		});
	}

	@Override
	public IListenableFuture<ColumnData> receiveColumn(final int colIndex) {
		return submitRead(new Callable<ColumnData>() {
			@Override
			public ColumnData call() {
				return sheetActor.getColumn(colIndex);
			}
		});
	}

	@Override
	public IListenableFuture<List<ColumnData>> receiveColumns(final Range range) {
		return submitRead(new Callable<List<ColumnData>>() {
			@Override
			public List<ColumnData> call() {
				return sheetActor.getColumns(range);
			}
		});
	}

	@Override
	public IListenableFuture<RowData> receiveRow(final int rowIndex) {
		return submitRead(new Callable<RowData>() {
			@Override
			public RowData call() {
				return sheetActor.getRow(rowIndex);
			}
		});
	}

	@Override
	public IListenableFuture<List<RowData>> receiveRows(final Range range) {
		return submitRead(new Callable<List<RowData>>() {
			@Override
			public List<RowData> call() {
				return sheetActor.getRows(range);
			}
		});
	}

	/**
	 * send asynchronously the command. As the result of the command is also a future, use the chain functionality to
	 * return it to the calling client.
	 */
	@Override
	public IListenableFuture<ICellCommand> sendCommand(final ICellCommand command) {
		IListenableFuture<ICellCommand> delayedCommand = delayedWriteCall(command);
		// TODO - this can be created only once as no data is different.
		Function<ICellCommand, IListenableFuture<ICellCommand>> func = new FunctionWithUser<ICellCommand, IListenableFuture<ICellCommand>>(
				userService, new Function<ICellCommand, IListenableFuture<ICellCommand>>() {
					@Override
					public IListenableFuture<ICellCommand> apply(ICellCommand input) {
						try {
							return sheetActor.sendCommand(input);
						} catch (NetxiliaBusinessException e) {
							return new MutableFuture<ICellCommand>(e);
						}
					}
				});
		return new NetxiliaListenableFutureAdapter<ICellCommand>(Futures.chain(delayedCommand, func));
	}

	/**
	 * send asynchronously the command. As the result of the command is also a future, use the chain functionality to
	 * return it to the calling client.
	 */
	@Override
	public IListenableFuture<ICellCommand> sendCommandNoUndo(final ICellCommand command) {
		IListenableFuture<ICellCommand> delayedCommand = delayedWriteCall(command);
		Function<ICellCommand, IListenableFuture<ICellCommand>> func = new FunctionWithUser<ICellCommand, IListenableFuture<ICellCommand>>(
				userService, new Function<ICellCommand, IListenableFuture<ICellCommand>>() {
					@Override
					public IListenableFuture<ICellCommand> apply(ICellCommand input) {
						try {
							return sheetActor.sendCommandNoUndo(input);
						} catch (NetxiliaBusinessException e) {
							return new MutableFuture<ICellCommand>(e);
						}
					}
				});
		return new NetxiliaListenableFutureAdapter<ICellCommand>(Futures.chain(delayedCommand, func));
	}

	@Override
	public IListenableFuture<IRowCommand> sendCommand(final IRowCommand command) {
		return submitWrite(new Callable<IRowCommand>() {
			@Override
			public IRowCommand call() {
				try {
					return sheetActor.sendCommand(command);
				} catch (NetxiliaBusinessException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public IListenableFuture<IColumnCommand> sendCommand(final IColumnCommand command) {
		return submitWrite(new Callable<IColumnCommand>() {
			@Override
			public IColumnCommand call() {
				try {
					return sheetActor.sendCommand(command);
				} catch (NetxiliaBusinessException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public IListenableFuture<ISheetCommand> sendCommand(final ISheetCommand command) {
		return submitWrite(new Callable<ISheetCommand>() {
			@Override
			public ISheetCommand call() {
				try {
					return sheetActor.sendCommand(command);
				} catch (NetxiliaBusinessException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}

	@Override
	public IListenableFuture<SheetDimensions> getDimensions() {
		return submitRead(new Callable<SheetDimensions>() {
			@Override
			public SheetDimensions call() {
				return sheetActor.getDimensions();
			}
		});
	}

	/***** TO FIGURE OUT **/
	@Override
	public IListenableFuture<Integer> sort(final SortSpecifier sortSpecifier) {
		return submitWrite(new Callable<Integer>() {
			@Override
			public Integer call() {
				try {
					return sheetActor.sort(sortSpecifier);
				} catch (CyclicDependenciesException ex) {
					throw new RuntimeException(ex);
				} catch (NetxiliaBusinessException ex) {
					throw new RuntimeException(ex);
				}
			}
		});

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + sheetActor.getFullName().hashCode();
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
		SheetProxy other = (SheetProxy) obj;
		return sheetActor.getFullName().equals(other.getFullName());
	}

	@Override
	public void setRefreshEnabled(final boolean enabled) {
		submitWrite(new Callable<Boolean>() {
			@Override
			public Boolean call() {
				try {
					sheetActor.setRefreshEnabled(enabled);
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
				return true;
			}
		});

	}

}
