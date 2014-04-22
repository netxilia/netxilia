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

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.netxilia.api.concurrent.IActor;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.concurrent.MutableFuture;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.operation.IOperation;
import org.netxilia.api.reference.CellReference;
import org.netxilia.spi.formula.IFormulaParser;

/**
 * This operation finds the first cell reference matching the given formula.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class FindOperationActor implements IActor, IOperation<CellReference> {
	private final ISheet sheet;

	private final CellReference startRef;

	private final Formula searchFormula;

	private final ExecutorService executor;

	private final IFormulaParser formulaParser;

	public FindOperationActor(ISheet sheet, CellReference startRef, Formula searchFormula, ExecutorService executor,
			IFormulaParser formulaParser) {
		this.sheet = sheet;
		this.startRef = startRef;
		this.searchFormula = searchFormula;
		this.executor = executor;
		this.formulaParser = formulaParser;
	}

	@Override
	public IListenableFuture<CellReference> execute() {
		// TODO should let storage try to implement better the filter - i.e. direct query to the database
		CellReference foundRef = null;
		try {
			foundRef = formulaParser.find(startRef, searchFormula, sheet);
		} catch (FormulaParsingException e) {
			// TODO what shall I do here !? - an exception is better !?
			foundRef = null;
		} catch (NetxiliaResourceException e) {
			foundRef = null;
		} catch (NetxiliaBusinessException e) {
			foundRef = null;
		}
		MutableFuture<CellReference> f = new MutableFuture<CellReference>();
		f.set(foundRef);
		return f;
	}

	@Override
	public Executor getExecutor() {
		return executor;
	}
}
