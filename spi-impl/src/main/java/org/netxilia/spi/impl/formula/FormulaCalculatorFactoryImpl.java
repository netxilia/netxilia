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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.IExecutorServiceFactory;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.FullCellReference;
import org.netxilia.spi.formula.IFormulaCalculator;
import org.netxilia.spi.formula.IFormulaCalculatorFactory;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.impl.formula.function.FunctionRegistry;
import org.springframework.beans.factory.annotation.Autowired;

public class FormulaCalculatorFactoryImpl implements IFormulaCalculatorFactory {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FormulaCalculatorFactoryImpl.class);

	@Autowired
	private IExecutorServiceFactory executorServiceFactory;

	@Autowired
	private FunctionRegistry functionRegistry;

	@Autowired
	private ISpringUserService userService;

	@Autowired
	private IFormulaParser formulaParser;

	private ConcurrentMap<FullCellReference, IFormulaCalculator> runningFormulas = new ConcurrentHashMap<FullCellReference, IFormulaCalculator>();

	public IExecutorServiceFactory getExecutorServiceFactory() {
		return executorServiceFactory;
	}

	public void setExecutorServiceFactory(IExecutorServiceFactory executorServiceFactory) {
		this.executorServiceFactory = executorServiceFactory;
	}

	public FunctionRegistry getFunctionRegistry() {
		return functionRegistry;
	}

	public void setFunctionRegistry(FunctionRegistry functionRegistry) {
		this.functionRegistry = functionRegistry;
	}

	public ISpringUserService getUserService() {
		return userService;
	}

	public void setUserService(ISpringUserService userService) {
		this.userService = userService;
	}

	@Override
	public IFormulaCalculator getCalculator(ISheet sheet, CellData cellData) throws FormulaParsingException {
		CellReference ref = cellData.getReference();
		if (ref.getSheetName() == null) {
			ref = ref.withSheetName(sheet.getName());
		}
		FullCellReference fullRef = new FullCellReference(sheet.getFullName().getWorkbookId(), ref);

		FormulaCalculatorActor actor = new FormulaCalculatorActor(userService, sheet, cellData, functionRegistry,
				executorServiceFactory.newExecutorService("formula-" + cellData.getReference()), formulaParser);

		IFormulaCalculator oldCalculator = runningFormulas.put(fullRef, actor);
		if (oldCalculator != null) {
			log.info("In factory Abandoned:" + ref);
			oldCalculator.abandon();
		}

		return actor;
	}

	@Override
	public void removeCalculator(ISheet sheet, CellReference reference) {
		CellReference ref = reference;
		if (ref.getSheetName() == null) {
			ref = ref.withSheetName(sheet.getName());
		}
		FullCellReference fullRef = new FullCellReference(sheet.getFullName().getWorkbookId(), ref);
		runningFormulas.remove(fullRef);
	}

}
