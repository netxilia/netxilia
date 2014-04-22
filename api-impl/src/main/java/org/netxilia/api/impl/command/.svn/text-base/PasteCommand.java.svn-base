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
package org.netxilia.api.impl.command;

import org.netxilia.api.command.AbstractCellCommand;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellData.Property;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.spi.formula.IFormulaParser;

public class PasteCommand extends AbstractCellCommand {
	private final String[][] pasteData;
	private final CellReference from;
	private final IFormulaParser formulaParser;
	private final IGenericValueParseService valueParser;

	public PasteCommand(AreaReference target, String[][] pasteData, CellReference from, IFormulaParser formulaParser,
			IGenericValueParseService valueParser) {
		super(target);
		this.pasteData = pasteData;
		this.from = from;
		this.formulaParser = formulaParser;
		this.valueParser = valueParser;
	}

	@Override
	public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {
		int row = data.getReference().getRowIndex() - getTarget().getFirstRowIndex();
		int col = data.getReference().getColumnIndex() - getTarget().getFirstColumnIndex();

		String theValue = col < pasteData[row].length ? pasteData[row][col] : null;

		if (theValue == null) {
			return new CellDataWithProperties(data.withValue(null), CellData.Property.value);
		}
		if (Formula.isFormula(theValue)) {
			Formula formula = new Formula(theValue);
			if (from != null) {
				formula = formulaParser.transformFormula(formula,
						ReferenceTransformers.shiftCell(from, data.getReference()));
			}
			return new CellDataWithProperties(data.withFormula(formula), Property.formula);
		}
		IGenericValue genericValue = valueParser.parse(theValue);
		return new CellDataWithProperties(data.withValue(genericValue), Property.value);

	}

}