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
package org.netxilia.api.impl.operation;

import java.util.List;

import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.IExecutorServiceFactory;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.CellReference;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Acts as an actor factory for operations dealing with a sheet
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetOperationsImpl implements ISheetOperations {
	@Autowired
	private IFormulaParser formulaParser;

	@Autowired
	private IExecutorServiceFactory executorServiceFactory;

	@Override
	public IListenableFuture<List<Integer>> filter(ISheet sheet, Formula filter) {
		return new FilterOperationActor(sheet, filter, executorServiceFactory.newExecutorService(sheet.getFullName()
				+ "-filter"), formulaParser).execute();
	}

	@Override
	public IListenableFuture<CellReference> find(ISheet sheet, CellReference startRef, Formula searchFormula) {
		return new FindOperationActor(sheet, startRef, searchFormula, executorServiceFactory.newExecutorService(sheet
				.getFullName() + "-find"), formulaParser).execute();
	}

}
