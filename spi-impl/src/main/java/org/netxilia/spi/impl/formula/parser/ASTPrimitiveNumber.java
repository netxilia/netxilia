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
/* Generated By:JJTree: Do not edit this line. ASTNxNumber.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package org.netxilia.spi.impl.formula.parser;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.formula.IFormulaRenderer;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.NumberValue;

/**
 * An AST node holding a parsed number.
 */
public class ASTPrimitiveNumber extends ASTBaseNode {
	private NumberValue number;

	public ASTPrimitiveNumber(int id) {
		super(id);
	}

	public ASTPrimitiveNumber(FormulaParser p, int id) {
		super(p, id);
	}

	/** Set the node's number to the given value. Used by the parser. */
	public void setNumber(Double value) {
		this.number = new NumberValue(value);
	}

	@Override
	public IGenericValue eval(IFormulaContext context) {
		return number;
	}

	@Override
	public String text(IFormulaRenderer context) {
		return number.getStringValue();
	}

	@Override
	public String toString() {
		return "PrimitiveNumber[" + number + "]";
	}
}
/* JavaCC - OriginalChecksum=05067fd6e34d7a2b8c97b8d845934971 (do not edit this line) */
