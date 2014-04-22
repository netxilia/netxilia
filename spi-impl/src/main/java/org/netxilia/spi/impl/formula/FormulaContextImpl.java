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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.netxilia.api.exception.EvaluationException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetDimensions;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.ErrorValueType;
import org.netxilia.api.value.IGenericValue;
import org.springframework.util.Assert;

public class FormulaContextImpl implements IFormulaContext {
	private final ISheet sheet;
	private final CellReference contextCell;

	private Map<SheetFullName, ISheet> openSheets = new HashMap<SheetFullName, ISheet>();

	public FormulaContextImpl(ISheet sheet, CellReference contextCell) {
		Assert.notNull(sheet);
		this.sheet = sheet;
		this.contextCell = contextCell;
	}

	public CellReference getContextCell() {
		return contextCell;
	}

	protected ISheet getSheet(String sheetName) {
		SheetFullName sheetFullName = new SheetFullName(sheet.getFullName().getWorkbookId(), sheetName);

		ISheet retSheet = openSheets.get(sheetFullName);
		if (retSheet == null) {
			try {
				retSheet = sheet.getWorkbook().getSheet(sheetFullName.getSheetName());
			} catch (StorageException e) {
				throw new EvaluationException(ErrorValueType.REF, e);
			} catch (NotFoundException e) {
				throw new EvaluationException(ErrorValueType.REF, e);
			}
			openSheets.put(sheetFullName, retSheet);
		}
		return retSheet;
	}

	@Override
	public Iterator<CellData> getCellIterator(AreaReference ref) {
		ISheet currentSheet;
		if (ref.getSheetName() == null) {
			currentSheet = sheet;
		} else {
			currentSheet = getSheet(ref.getSheetName());
		}
		if (currentSheet == null) {
			return Collections.<CellData> emptyList().iterator();
		}

		// TODO shift also areas!?
		// int r = getRowIndex(ref);
		// int c = getColumnIndex(ref);

		SheetDimensions dim;
		try {
			dim = currentSheet.getDimensions().getNonBlocking();
		} catch (NetxiliaResourceException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		} catch (NetxiliaBusinessException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		}

		return new CellIterator(currentSheet, ref.iterator(dim.getRowCount(), dim.getColumnCount()));
	}

	@Override
	public IGenericValue getCellValue(CellReference ref) {
		ISheet currentSheet;
		if (ref.getSheetName() == null) {
			currentSheet = sheet;
		} else {
			currentSheet = getSheet(ref.getSheetName());
		}
		if (currentSheet == null) {
			return new ErrorValue(ErrorValueType.REF);
		}

		int r = ref.getRowIndex();
		int c = ref.getColumnIndex();

		try {
			CellData cell = null;
			SheetDimensions dim = currentSheet.getDimensions().getNonBlocking();
			if (r >= dim.getRowCount() || c >= dim.getColumnCount()) {
				return new ErrorValue(ErrorValueType.REF);
			}

			cell = currentSheet.receiveCell(new CellReference(r, c)).getNonBlocking();
			// TODO - here we assume the value is already calculated if the cell is a formula. Should check this!
			return cell != null ? cell.getValue() : new ErrorValue(ErrorValueType.REF);
		} catch (NetxiliaResourceException e) {
			return new ErrorValue(ErrorValueType.REF);
		} catch (NetxiliaBusinessException e) {
			return new ErrorValue(ErrorValueType.REF);
		}

	}

	@Override
	public AreaReference resolveAlias(AbsoluteAlias alias) {
		ISheet currentSheet;
		if (alias.getSheetName() == null) {
			currentSheet = sheet;
		} else {
			currentSheet = getSheet(alias.getSheetName());
		}

		if (currentSheet == null) {
			return null;
		}
		SheetData sheetData;
		try {
			sheetData = currentSheet.receiveSheet().getNonBlocking();
		} catch (NetxiliaResourceException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		} catch (NetxiliaBusinessException e) {
			throw new EvaluationException(ErrorValueType.REF, e);
		}
		AreaReference ref = sheetData.resolveAlias(alias.getAlias());
		if (ref == null) {
			return null;
		}
		// ref may not have the sheet name set. make sure the area reference is absolute
		return ref.withSheetName(alias.getSheetName());
	}

	@Override
	public CellReference getCell() {
		return contextCell;
	}

	@Override
	public ISheet getSheet() {
		return sheet;
	}

}
