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

/**
 * An AST node representing a binary expression with an infix operator. This class is abstract as it has no own eval().
 * 
 * @author catac
 * @since Nov 19, 2009
 */
public abstract class ASTBinaryExpression extends ASTBaseNode {
	private String name;
	protected String operator;

	public ASTBinaryExpression(int id, String name) {
		super(id);
		this.name = name;
	}

	public ASTBinaryExpression(FormulaParser p, int id, String name) {
		super(p, id);
		this.name = name;
	}

	protected void setOperator(String operator) {
		this.operator = operator;
	}

	@Override
	public String text(IFormulaRenderer context) {
		ASTBaseNode nLeft = (ASTBaseNode) jjtGetChild(0);
		ASTBaseNode nRight = (ASTBaseNode) jjtGetChild(1);

		StringBuilder sb = new StringBuilder();
		sb.append(nLeft.text(context));
		sb.append(" ");
		sb.append(operator);
		sb.append(" ");
		sb.append(nRight.text(context));
		return sb.toString();
	}

	@Override
	public String toString(String prefix, IFormulaContext context, IFormulaRenderer renderer) {
		return prefix + name + "[" + operator + "]: " + text(renderer) + " = " + eval(context);
	}
}
