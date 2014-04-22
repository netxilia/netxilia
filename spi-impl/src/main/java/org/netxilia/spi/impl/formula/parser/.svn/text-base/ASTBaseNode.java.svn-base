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
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.ReferenceUtils;
import org.netxilia.api.value.IGenericValue;

/**
 * Defines the basic operations for all AST nodes.
 * 
 * @author catac
 */
public abstract class ASTBaseNode extends SimpleNode {

	public ASTBaseNode(int i) {
		super(i);
	}

	public ASTBaseNode(FormulaParser p, int i) {
		super(p, i);
	}

	/**
	 * Evaluate the expression starting at this node.
	 * 
	 * @param context
	 *            the context in which the formula is evaluated
	 * @return the result, as a {@link IGenericValue}
	 */
	public abstract IGenericValue eval(IFormulaContext context);

	/**
	 * Generate the text of the formula starting at this node
	 * 
	 * @param context
	 *            the context in which the formula text is generated
	 * @return the formula string.
	 */
	public abstract String text(IFormulaRenderer context);

	/**
	 * Debug helper to dump the formula tree and sub-nodes with partial evaluation.
	 */
	public void dump(String prefix, IFormulaContext context, IFormulaRenderer renderer) {
		System.out.println(toString(prefix, context, renderer));
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				ASTBaseNode n = (ASTBaseNode) children[i];
				if (n != null) {
					n.dump(prefix + " ", context, renderer);
				}
			}
		}
	}

	/**
	 * Visits all the node of the tree starting from this node. It stops whenever a non-null result is returned by the
	 * visitor.
	 * 
	 * @param <T>
	 * @param visitor
	 * @return
	 */
	public <T> T visit(INodeVisitor<T> visitor) {
		T result = visitor.visit(this);
		if (result != null) {
			return result;
		}
		if (children != null) {
			for (int i = 0; i < children.length; ++i) {
				ASTBaseNode n = (ASTBaseNode) children[i];
				if (n != null) {
					result = n.visit(visitor);
					if (result != null) {
						return result;
					}
				}
			}
		}
		return null;
	}

	public ASTBaseNode getParent() {
		return (ASTBaseNode) super.jjtGetParent();
	}

	/**
	 * 
	 * @return false if this node accepts as children only cells and not areas (for example number operators)
	 */
	protected boolean acceptsAreaChildren() {
		return false;
	}

	protected AreaReference checkOneCell(AreaReference ref, IFormulaContext context) {
		AreaReference returnRef = ref;
		if (!returnRef.isOneCell() && !getParent().acceptsAreaChildren()) {
			returnRef = new AreaReference(ReferenceUtils.toOneCell(returnRef, context.getCell()));
		}
		return returnRef;
	}

	/**
	 * Get the string representation, with a prefix, evaluated in a certain context. This must be overridden instead of
	 * toString() if context is needed.
	 * 
	 * @param context
	 *            to be used in overriding implementations
	 */
	public String toString(String prefix, IFormulaContext context, IFormulaRenderer renderer) {
		return super.toString(prefix);
	}
}
