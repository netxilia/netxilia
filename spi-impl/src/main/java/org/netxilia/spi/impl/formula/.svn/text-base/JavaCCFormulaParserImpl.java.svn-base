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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.netxilia.api.exception.EvaluationException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.impl.formula.function.FunctionRegistry;
import org.netxilia.spi.impl.formula.function.IFunction;
import org.netxilia.spi.impl.formula.parser.ASTAliasReference;
import org.netxilia.spi.impl.formula.parser.ASTBaseNode;
import org.netxilia.spi.impl.formula.parser.ASTFormulaTree;
import org.netxilia.spi.impl.formula.parser.ASTFunction;
import org.netxilia.spi.impl.formula.parser.FormulaParser;
import org.netxilia.spi.impl.formula.parser.INodeVisitor;
import org.netxilia.spi.impl.formula.parser.IReferenceNode;
import org.netxilia.spi.impl.formula.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An implementation of the {@link IFormulaParser} using JavaCC. The implementation receives also a
 * {@link FunctionRegistry} containing the function that can be used within a formula. This implementation uses also a
 * {@link Ehcache} to cache the AST tree corresponding to the formula.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class JavaCCFormulaParserImpl implements IFormulaParser {
	@Autowired
	private FunctionRegistry functionRegistry;
	private Ehcache cache;

	public JavaCCFormulaParserImpl() {
		cache = CacheManager.create().getEhcache("formula-cache");
	}

	public FunctionRegistry getFunctionRegistry() {
		return functionRegistry;
	}

	public void setFunctionRegistry(FunctionRegistry functionRegistry) {
		this.functionRegistry = functionRegistry;
	}

	private ASTFormulaTree parse(Formula formula) throws FormulaParsingException {
		if (formula == null) {
			throw new NullPointerException();
		}

		Element element;
		if ((element = cache.get(formula)) != null) {
			return (ASTFormulaTree) element.getObjectValue();
		}

		FormulaParser nxp = new FormulaParser(new StringReader(formula.getFormula().substring(1)));
		nxp.setFunctionRegistry(functionRegistry);

		try {
			ASTFormulaTree tree = nxp.buildFormulaTree();
			cache.put(new Element(formula, tree));
			return tree;
		} catch (ParseException e) {
			throw new FormulaParsingException(e);
		}
	}

	@Override
	public IGenericValue executeFormula(Formula formula, ISheet sheet, CellReference contextCell)
			throws FormulaParsingException {
		return executeFormula(formula, new FormulaContextImpl(sheet, contextCell));
	}

	@Override
	public IGenericValue executeFormula(Formula formula, IFormulaContext formulaContext) throws FormulaParsingException {
		ASTFormulaTree parsedFormula = parse(formula);
		try {
			IGenericValue value = parsedFormula.eval(formulaContext);
			value = GenericValueUtils.deferenceValue(value);
			return value;
		} catch (EvaluationException e) {
			return new ErrorValue(e.getErrorType());
		}

	}

	@Override
	public Formula parseFormula(Formula formula) throws FormulaParsingException {
		ASTFormulaTree n = parse(formula);
		return new Formula("=" + n.text(new FormulaRendererImpl()));

	}

	@Override
	public Formula transformFormula(Formula formula, IReferenceTransformer transformer) throws FormulaParsingException {
		ASTFormulaTree parsedFormula = parse(formula);

		return new Formula("=" + parsedFormula.text(new FormulaRendererImpl(transformer)));

	}

	@Override
	public List<Integer> filterWithFormula(Formula formula, ISheet sheet) throws NetxiliaResourceException,
			NetxiliaBusinessException {
		ASTFormulaTree parsedFormula = parse(formula);
		FilterRowsFormulaContext context = new FilterRowsFormulaContext(new FormulaContextImpl(sheet, null));
		List<Integer> results = new ArrayList<Integer>();
		for (int i = 0; i < sheet.getDimensions().getNonBlocking().getRowCount(); ++i) {
			context.setRowDelta(i);
			IGenericValue value = parsedFormula.eval(context);
			if (value != null && value.getBooleanValue() != null && value.getBooleanValue()) {
				results.add(i);
			}
		}
		return results;

	}

	@Override
	public CellReference find(CellReference startRef, Formula searchFormula, ISheet sheet)
			throws NetxiliaResourceException, NetxiliaBusinessException {
		ASTFormulaTree parsedFormula = parse(searchFormula);
		FilterRowsFormulaContext context = new FilterRowsFormulaContext(new FormulaContextImpl(sheet, null));
		SheetDimensions dimensions = sheet.getDimensions().getNonBlocking();
		int startRow = startRef != null ? startRef.getRowIndex() : 0;
		for (int r = startRow; r < dimensions.getRowCount(); ++r) {
			int startCol = (startRef != null && r == startRef.getRowIndex()) ? startRef.getColumnIndex() + 1 : 0;
			// load row by row -> TODO load more rows !?
			Matrix<CellData> row = sheet.receiveCells(
					new AreaReference(null, r, startCol, r, dimensions.getColumnCount())).getNonBlocking();
			context.setRowDelta(r);
			for (int c = startCol; c < row.getColumnCount(); ++c) {
				CellData cell = row.get(0, c - startCol);
				if (cell == null || cell.getValue() == null) {
					continue;
				}
				context.setColumnDelta(c);
				IGenericValue value = parsedFormula.eval(context);
				if (value != null && value.getBooleanValue() != null && value.getBooleanValue()) {
					return new CellReference(sheet.getName(), r, c);
				}
			}
		}
		return null;

	}

	@Override
	public List<AreaReference> getDependencies(Formula formula, final IFormulaContext context)
			throws FormulaParsingException {
		ASTFormulaTree tree = parse(formula);
		final List<AreaReference> references = new ArrayList<AreaReference>();
		tree.visit(new INodeVisitor<Boolean>() {
			@Override
			public Boolean visit(ASTBaseNode node) {
				if (node instanceof IReferenceNode) {
					AreaReference ref = ((IReferenceNode) node).getReference(context);
					if (ref != null) {
						references.add(ref);
					}
				}
				return null;
			}
		});
		return references;

	}

	@Override
	public List<AbsoluteAlias> getAliases(Formula formula, final IFormulaContext context)
			throws FormulaParsingException {
		ASTFormulaTree tree = parse(formula);
		final List<AbsoluteAlias> aliases = new ArrayList<AbsoluteAlias>();
		tree.visit(new INodeVisitor<Boolean>() {
			@Override
			public Boolean visit(ASTBaseNode node) {
				if (node instanceof ASTAliasReference) {
					aliases.add(((ASTAliasReference) node).getAlias(context));
				}
				return null;
			}
		});
		return aliases;

	}

	@Override
	public boolean isCacheable(Formula formula) throws FormulaParsingException {
		ASTFormulaTree tree = parse(formula);
		// stops at the first function node with a non-cacheable result
		Boolean cacheable = tree.visit(new INodeVisitor<Boolean>() {
			@Override
			public Boolean visit(ASTBaseNode node) {
				if (node instanceof ASTFunction) {
					IFunction function = ((ASTFunction) node).getFunction();
					if (function == null || !function.isCacheble()) {
						return Boolean.FALSE;
					}
				}
				return null;
			}
		});
		return cacheable != null ? cacheable.booleanValue() : true;

	}
}
