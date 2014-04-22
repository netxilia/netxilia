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
package org.netxilia.spi.impl.formula.parser;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.formula.IFormulaRenderer;
import org.netxilia.api.model.AbsoluteAlias;
import org.netxilia.api.model.Alias;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.ErrorValueType;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.ReferenceValue;

/**
 * An AST node representing an alias to another cell.
 */
public class ASTAliasReference extends ASTBaseNode implements IReferenceNode {
	private Alias alias;
	private AbsoluteAlias absoluteAlias;
	private int row;

	public ASTAliasReference(int id) {
		super(id);
	}

	public ASTAliasReference(FormulaParser p, int id) {
		super(p, id);
	}

	public AbsoluteAlias getAlias(IFormulaContext context) {
		if (absoluteAlias != null) {
			return absoluteAlias.withRelativeSheetName(context.getSheet().getName());
		}
		return new AbsoluteAlias(context.getSheet().getName(), alias);
	}

	public void setRef(String sheetRef, String alias, String rowIndex) {
		// ACR: ref contains trailing !
		String sr = null;
		if (sheetRef != null && sheetRef.length() > 1) {
			sr = sheetRef.substring(0, sheetRef.length() - 1);
		}

		if (sr != null) {
			this.absoluteAlias = new AbsoluteAlias(sr, new Alias(alias));
		} else {
			this.alias = new Alias(alias);
		}
		this.row = !rowIndex.isEmpty() ? Integer.valueOf(rowIndex) : -1;
	}

	@Override
	public AreaReference getReference(IFormulaContext context) {
		AreaReference ref = context.resolveAlias(getAlias(context));
		if (ref == null) {
			return null;
		}
		if (row < 0) {
			return checkOneCell(ref, context);
		}

		// the row is given in the formula
		int rowIndex = row - 1;
		if (rowIndex < ref.getFirstRowIndex() || rowIndex > ref.getLastRowIndex()) {
			return null;
		}
		CellReference relativeReference = new CellReference(ref.getTopLeft().getSheetName(), rowIndex, ref.getTopLeft()
				.getColumnIndex());
		return new AreaReference(relativeReference);

	}

	@Override
	public IGenericValue eval(IFormulaContext context) {
		AreaReference ref = getReference(context);
		if (ref == null) {
			return new ErrorValue(ErrorValueType.NAME);
		}

		return new ReferenceValue(ref, context);
	}

	@Override
	public String text(IFormulaRenderer context) {
		StringBuilder text = new StringBuilder();
		text.append(alias);
		if (row >= 1) {
			text.append(" ").append(row);
		}

		return text.toString();
	}

	@Override
	public String toString(String prefix, IFormulaContext context, IFormulaRenderer renderer) {
		return prefix + "CellReference[" + alias + "]=" + eval(context);
	}

}
/*
 * JavaCC - OriginalChecksum=76e9b91dd772efbf82b5c5778dad2355 (do not edit this line)
 */
