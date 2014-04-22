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

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

import org.netxilia.api.concurrent.IActor;
import org.netxilia.api.concurrent.IListenableFuture;
import org.netxilia.api.exception.AbandonedCalculationException;
import org.netxilia.api.exception.EvaluationException;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.concurrent.MutableFuture;
import org.netxilia.api.impl.model.CallableWithUser;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.spi.formula.IFormulaCalculator;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.impl.formula.function.FunctionRegistry;
import org.springframework.util.Assert;

/**
 * This class handles the asynchronous calculation of a formula before setting the results to the sheet. It loads the
 * needed dependencies before launching the calculation.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class FormulaCalculatorActor implements IActor, IFormulaCalculator {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(FormulaCalculatorActor.class);

	private final CellData originalCell;
	private final ISheet sheet;

	private AsyncPreloadedFormulaContextImpl formulaContext;

	private MutableFuture<CellData> calculationResult = null;

	private volatile boolean abandoned = false;

	private final ExecutorService executor;

	private final IFormulaParser formulaParser;

	private final ISpringUserService userService;

	public FormulaCalculatorActor(ISpringUserService userService, ISheet sheet, CellData originalCell,
			FunctionRegistry functionRegistry, ExecutorService executor, IFormulaParser formulaParser)
			throws FormulaParsingException {
		Assert.notNull(sheet);
		Assert.notNull(originalCell);
		Assert.notNull(functionRegistry);
		Assert.notNull(userService);

		this.userService = userService;
		this.executor = executor;
		this.sheet = sheet;
		this.originalCell = originalCell;
		this.formulaParser = formulaParser;

		this.formulaContext = new AsyncPreloadedFormulaContextImpl(sheet, originalCell.getReference(),
				originalCell.getFormula(), true, executor, formulaParser, userService);
	}

	@Override
	public IListenableFuture<CellData> sendCalculate() {
		if (calculationResult != null) {
			throw new IllegalStateException("A calculation is already ongoing");
		}
		// TODO check here if the executor cannot return directly the needed future
		calculationResult = new MutableFuture<CellData>();
		executor.submit(new CallableWithUser<CellData>(userService, new Callable<CellData>() {
			@Override
			public CellData call() {
				loadData();
				return null;
			}

		}));

		return calculationResult;
	}

	public Executor getExecutor() {
		return executor;
	}

	public void abandon() {
		abandoned = true;
	}

	private void loadData() {
		try {
			formulaContext.load(new Runnable() {
				@Override
				public void run() {
					doCalculate();
				}
			});
		} catch (EvaluationException ex) {
			calculationResult.set(originalCell.withValue(new ErrorValue(ex.getErrorType())));
		} catch (Exception ex) {
			calculationResult.setException(ex);
		}
	}

	private void doCalculate() {
		if (originalCell.getFormula() == null) {
			calculationResult.set(originalCell);
			return;
		}

		try {
			if (abandoned) {
				throw new AbandonedCalculationException();
			}
			IGenericValue value = formulaParser.executeFormula(originalCell.getFormula(), formulaContext);

			value = GenericValueUtils.deferenceValue(value);
			calculationResult.set(originalCell.withValue(value));
		} catch (EvaluationException ex) {
			calculationResult.set(originalCell.withValue(new ErrorValue(ex.getErrorType())));
		} catch (Exception ex) {
			calculationResult.setException(ex);
		}
	}

	public ISheet getSheet() {
		return sheet;
	}

}
