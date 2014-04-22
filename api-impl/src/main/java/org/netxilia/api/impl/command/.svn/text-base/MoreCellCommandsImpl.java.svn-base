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

import java.util.List;

import org.netxilia.api.command.AbstractCellCommand;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ICellCommand;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.StyleApplyMode;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellData.Property;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This is the implementation of more complex commands. It uses services from SPI.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class MoreCellCommandsImpl implements IMoreCellCommands {
	@Autowired
	private IGenericValueParseService valueParser;

	@Autowired
	private IFormulaParser formulaParser;

	@Autowired
	private IStyleService styleService;

	public IGenericValueParseService getValueParser() {
		return valueParser;
	}

	public void setValueParser(IGenericValueParseService valueParser) {
		this.valueParser = valueParser;
	}

	public IFormulaParser getFormulaParser() {
		return formulaParser;
	}

	public void setFormulaParser(IFormulaParser formulaParser) {
		this.formulaParser = formulaParser;
	}

	public ICellCommand paste(AreaReference ref, String[][] pasteData, CellReference from) {
		return new PasteCommand(ref, pasteData, from, formulaParser, valueParser);
	}

	@Override
	public ICellCommand formulaTransformer(final AreaReference area, final IReferenceTransformer referenceTransformer) {
		return new AbstractCellCommand(area) {

			@Override
			public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {
				return new CellDataWithProperties(data.withFormula(formulaParser.transformFormula(data.getFormula(),
						referenceTransformer)), Property.formula);
			}
		};
	}

	@Override
	public ICellCommand applyStyles(final WorkbookId workbookId, final AreaReference area, final Styles applyStyle,
			final StyleApplyMode applyMode) {
		return new AbstractCellCommand(area) {

			@Override
			public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {
				Styles newStyles = styleService.applyStyle(workbookId, data.getStyles(), applyStyle, applyMode);
				return new CellDataWithProperties(data.withStyles(newStyles), Property.styles);
			}
		};
	}

	@Override
	public ICellCommand copyContent(final AreaReference to, final CellData source) {
		if (source.getFormula() != null) {
			return new AbstractCellCommand(to) {

				@Override
				public CellDataWithProperties apply(CellData data) throws NetxiliaBusinessException {

					IReferenceTransformer referenceTransformer = ReferenceTransformers.shiftCell(source.getReference(),
							data.getReference());
					return new CellDataWithProperties(data.withFormula(formulaParser.transformFormula(
							source.getFormula(), referenceTransformer)), Property.formula);
				}
			};
		}
		return CellCommands.value(to, source.getValue());
	}

	@Override
	public ICellCommand refresh(List<CellReference> refreshCells, boolean stopPropagation) {
		if (refreshCells == null || refreshCells.isEmpty()) {
			// an error is more appropriate here !?
			return CellCommands.doNothing(new AreaReference("A1:A1"));
		}
		return new RefreshCellCommand(refreshCells, stopPropagation);
	}

}
