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
/* Generated By:JJTree: Do not edit this line. ASTVectorReference.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.netxilia.spi.impl.formula.parser;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.formula.IFormulaRenderer;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.ReferenceValue;

/**
 * An AST node representing a reference to an area of cells.
 */
public class ASTVectorReference extends ASTBaseNode implements IReferenceNode {
	private AreaReference ref;

	public ASTVectorReference(int id) {
		super(id);
	}

	public ASTVectorReference(FormulaParser p, int id) {
		super(p, id);
	}

	public void setRef(String sheetRef, String vectorRef) {
		// ACR: ref contains trailing !
		ref = new AreaReference(sheetRef + vectorRef);
	}

	@Override
	public IGenericValue eval(IFormulaContext context) {
		return new ReferenceValue(getReference(context), context);
	}

	@Override
	public AreaReference getReference(IFormulaContext context) {
		return checkOneCell(ref, context).withRelativeSheetName(context.getSheet().getName());
	}

	@Override
	public String text(IFormulaRenderer context) {
		return context.getAreaText(ref);
	}

	@Override
	public String toString() {
		return "VectorReference[" + ref + "]";
	}
}
/* JavaCC - OriginalChecksum=3fc1d3fe9dda5321318f4fefdc13c9ab (do not edit this line) */