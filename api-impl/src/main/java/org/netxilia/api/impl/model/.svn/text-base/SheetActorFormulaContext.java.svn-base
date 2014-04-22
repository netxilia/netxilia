package org.netxilia.api.impl.model;

import java.util.Iterator;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.IGenericValue;
import org.springframework.util.Assert;

/**
 * This formula context is used to take the data directly from the actor without passing through the asynchronous
 * mechanism.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetActorFormulaContext implements IFormulaContext {
	private final SheetActor sheetActor;
	private final CellReference contextCell;

	public SheetActorFormulaContext(SheetActor sheetActor, CellReference contextCell) {
		Assert.notNull(sheetActor);
		Assert.notNull(contextCell);
		this.sheetActor = sheetActor;
		this.contextCell = contextCell;
	}

	@Override
	public IGenericValue getCellValue(CellReference ref) {
		throw new UnsupportedOperationException("Not implemented");
	}

	@Override
	public CellReference getCell() {
		return contextCell;
	}

	@Override
	public AreaReference resolveAlias(AbsoluteAlias alias) {
		if (alias.getSheetName() == null || sheetActor.getName().equals(alias.getSheetName())) {
			return sheetActor.getSheet().resolveAlias(alias.getAlias());
		}
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public ISheet getSheet() {
		return sheetActor.getReference();
	}

	@Override
	public Iterator<CellData> getCellIterator(AreaReference ref) {
		throw new UnsupportedOperationException("Not implemented");
	}

}
