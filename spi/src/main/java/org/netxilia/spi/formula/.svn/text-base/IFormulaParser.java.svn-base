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
package org.netxilia.spi.formula;

import java.util.List;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.value.IGenericValue;

/**
 * This is the entry point to the formula parsing and evaluation.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IFormulaParser {
	/**
	 * 
	 * @param formula
	 *            with the leading "=".
	 * @return the formula with references potentially modified
	 * @throws FormulaParsingException
	 *             if the given formula is incorrect
	 */
	public Formula parseFormula(Formula formula) throws FormulaParsingException;

	/**
	 * Execute a previously parsed formula using the given sheet to resolve the cell references. The contextCell is the
	 * cell for which the formula is executed, used to resolve relative references or aliases. If the contextCell is
	 * null and there are uncomplete references within the formula, they will be resolved to an error.
	 * 
	 * @param parsedFormula
	 * @param workbook
	 * @throws FormulaParsingException
	 *             if the given formula is incorrect
	 * @return
	 */
	public IGenericValue executeFormula(Formula parsedFormula, ISheet sheet, CellReference contextCell)
			throws FormulaParsingException;

	/**
	 * Execute a previously parsed formula using the given sheet to resolve the cell references. The contextCell is the
	 * cell for which the formula is executed, used to resolve relative references or aliases. If the contextCell is
	 * null and there are uncomplete references within the formula, they will be resolved to an error.
	 * 
	 * @param parsedFormula
	 * @param workbook
	 * @throws FormulaParsingException
	 *             if the given formula is incorrect
	 * @return
	 */
	public IGenericValue executeFormula(Formula parsedFormula, IFormulaContext formulaContext)
			throws FormulaParsingException;

	/**
	 * It applies the given formula to every row of the sheet and return all the matching row IDs. In the formula, all
	 * the relative row reference will be incremented for each row To have predictable results wherever a relative row
	 * reference is used, use 1.
	 * 
	 * e.g.:<b>C1=2</b>
	 * 
	 * @param formula
	 * @param sheet
	 * @return
	 * @throws FormulaParsingException
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public List<Integer> filterWithFormula(Formula formula, ISheet sheet) throws FormulaParsingException,
			NetxiliaResourceException, NetxiliaBusinessException;

	/**
	 * Search the given formula in the sheet. The startRef parameter is usually the result of the last search in order
	 * to perform "find next" functionality. The sheet is traversed starting at row 0, column 0 and than it goes to the
	 * end of the row before moving to the next row. If the startRef parameter is null the search starts at the row 0,
	 * column 0 row. .
	 * 
	 * @param sheet
	 * @param startRef
	 * @param searchText
	 * @return null if the given text is not found in the sheet or the first cell the searched text is found.
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public CellReference find(CellReference startRef, Formula searchFormula, ISheet sheet)
			throws FormulaParsingException, NetxiliaResourceException, NetxiliaBusinessException;

	/**
	 * 
	 * @param parsedFormula
	 * @param referenceCell
	 * @param targetCell
	 * @return
	 */

	public Formula transformFormula(Formula formula, IReferenceTransformer transformer) throws FormulaParsingException;

	/**
	 * 
	 * @param formula
	 * @return the list of cells that are used within this formula
	 * @throws FormulaParsingException
	 */
	public List<AreaReference> getDependencies(Formula formula, IFormulaContext context) throws FormulaParsingException;

	public List<AbsoluteAlias> getAliases(Formula formula, IFormulaContext context) throws FormulaParsingException;

	/**
	 * 
	 * @return true if the value of the can be cached between two calls. i.e. if two subsequent evaluations of this
	 *         formula, if the value of dependencies doesn't change, will return the same result. Typical formulas for
	 *         which this should return false are the ones containing calls to RAND() or NOW().
	 * @throws FormulaParsingException
	 */
	public boolean isCacheable(Formula formula) throws FormulaParsingException;
}
