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
package org.netxilia.api.impl.dependencies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.utils.ISparseMatrix;
import org.netxilia.api.impl.utils.ISparseMatrixEntry;
import org.netxilia.api.impl.utils.InsertMode;
import org.netxilia.api.impl.utils.OrderedBlockMatrix;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.spi.formula.IFormulaParser;
import org.springframework.util.Assert;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

/**
 * This is the default implementation of the {@link IAliasDependencyManager}. It synchronizes access to internal
 * structure. It contains a bidirectional hashmap between aliases and cell references.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetAliasDependencyManager {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(NetxiliaSystemImpl.class);

	/**
	 * a sparse matrix containing the list of all the aliases referenced from a cell.
	 */
	private final ISparseMatrix<List<AbsoluteAlias>> dependencies = new OrderedBlockMatrix<List<AbsoluteAlias>>();
	private Map<Alias, AreaReference> previousAliases = new HashMap<Alias, AreaReference>();

	private final WorkbookAliasDependencyManager workbookAliasDependencyManager;
	private final ISheet sheet;
	private final IFormulaParser formulaParser;

	public static SheetAliasDependencyManager newInstance(
			WorkbookAliasDependencyManager workbookAliasDependencyManager, ISheet sheet, IFormulaParser formulaParser) {
		Assert.notNull(workbookAliasDependencyManager);
		Assert.notNull(sheet);
		Assert.notNull(formulaParser);

		SheetAliasDependencyManager mgr = new SheetAliasDependencyManager(workbookAliasDependencyManager, sheet,
				formulaParser);
		return mgr;
	}

	private SheetAliasDependencyManager(WorkbookAliasDependencyManager workbookAliasDependencyManager, ISheet sheet,
			IFormulaParser formulaParser) {
		this.sheet = sheet;
		this.formulaParser = formulaParser;
		this.workbookAliasDependencyManager = workbookAliasDependencyManager;
	}

	public ISheet getSheet() {
		return sheet;
	}

	public void insertRow(int row) {
		dependencies.insertRow(row, InsertMode.grow);
	}

	public void deleteRow(int row) {
		dependencies.deleteRow(row);
	}

	public void insertColumn(int column) {
		dependencies.insertColumn(column, InsertMode.grow);
	}

	public void deleteColumn(int column) {
		dependencies.deleteColumn(column);
	}

	/**
	 * iterates through all the dependencies and find all the cells containing references to the given alias
	 * 
	 * @param alias
	 * @return
	 */
	public Collection<AreaReference> getAliasDependants(AbsoluteAlias alias) {
		List<AreaReference> affectedAreas = new ArrayList<AreaReference>();
		for (ISparseMatrixEntry<List<AbsoluteAlias>> matrixEntry : dependencies.entries()) {
			if (matrixEntry.getValue().contains(alias)) {
				affectedAreas.add(new AreaReference(new CellReference(sheet.getName(), matrixEntry.getFirstRow(),
						matrixEntry.getFirstColumn()), new CellReference(sheet.getName(), matrixEntry.getLastRow(),
						matrixEntry.getLastColumn())));
			}
		}
		return affectedAreas;
	}

	public void setAliasDependencies(Formula formula, IFormulaContext context) {
		Assert.notNull(context);
		try {
			CellReference ref = context.getCell();
			List<AbsoluteAlias> aliases = formula != null ? formulaParser.getAliases(formula, context) : null;
			dependencies.set(ref.getRowIndex(), ref.getColumnIndex(), aliases);
		} catch (FormulaParsingException e) {
			log.error("Cannot parse formula:" + formula + ":" + e);
		}
	}

	public Map<Alias, AreaReference> getAliases() {
		return previousAliases;
	}

	public void saveSheet(SheetData sheetData, Collection<SheetData.Property> properties) {
		if (properties != null && properties.contains(SheetData.Property.aliases)) {
			// check which alias changed
			// make a diff between the stored version of aliases and the new list

			MapDifference<Alias, AreaReference> diff = Maps.difference(previousAliases, sheetData.getAliases());
			previousAliases = new HashMap<Alias, AreaReference>(sheetData.getAliases());
			workbookAliasDependencyManager.refreshAliases(sheet.getName(), diff.entriesDiffering().keySet(), diff
					.entriesOnlyOnLeft().keySet());

		}
	}
}
