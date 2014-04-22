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

import org.netxilia.api.formula.IFormulaRenderer;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.IReferenceTransformer;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.ErrorValueType;

/**
 * Use this context to transform a formula from a cell to another cell by changing the relative references.
 * 
 * E.g. a formula "A1 + 2" from C10 cell copied into the E14 cell will become: "C5 + 2"
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class FormulaRendererImpl implements IFormulaRenderer {
	private final IReferenceTransformer referenceTransformer;

	public FormulaRendererImpl(IReferenceTransformer referenceTransformer) {
		this.referenceTransformer = referenceTransformer;
	}

	public FormulaRendererImpl() {
		this(null);
	}

	@Override
	public String getAreaText(AreaReference ref) {
		AreaReference finalRef = referenceTransformer != null ? referenceTransformer.transform(ref) : ref;
		if (finalRef != null) {
			return finalRef.formatAsString();
		}
		return new ErrorValue(ErrorValueType.REF).getStringValue();
	}

	@Override
	public String getCellText(CellReference ref) {
		CellReference finalRef = referenceTransformer != null ? referenceTransformer.transform(ref) : ref;
		if (finalRef != null) {
			return finalRef.formatAsString();
		}
		return new ErrorValue(ErrorValueType.REF).getStringValue();
	}

}
