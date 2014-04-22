package org.netxilia.spi.impl.formula;

import java.util.concurrent.Executor;

import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.IPreloadedFormulaContext;
import org.netxilia.api.formula.IPreloadedFormulaContextFactory;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.CellReference;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.beans.factory.annotation.Autowired;

public class PreloadedFormulaContextFactoryImpl implements IPreloadedFormulaContextFactory {
	@Autowired
	private IFormulaParser formulaParser;

	@Autowired
	private ISpringUserService userService;

	public IFormulaParser getFormulaParser() {
		return formulaParser;
	}

	public void setFormulaParser(IFormulaParser formulaParser) {
		this.formulaParser = formulaParser;
	}

	public ISpringUserService getUserService() {
		return userService;
	}

	public void setUserService(ISpringUserService userService) {
		this.userService = userService;
	}

	public IPreloadedFormulaContext newPreloadAliasesContext(ISheet sheet, CellReference cell, Formula formula,
			Executor executor) {
		return new AsyncPreloadedFormulaContextImpl(sheet, cell, formula, false, executor, formulaParser, userService);
	}

	public IPreloadedFormulaContext newPreloadValuesContext(ISheet sheet, CellReference cell, Formula formula,
			Executor executor) {
		return new AsyncPreloadedFormulaContextImpl(sheet, cell, formula, true, executor, formulaParser, userService);
	}

}
