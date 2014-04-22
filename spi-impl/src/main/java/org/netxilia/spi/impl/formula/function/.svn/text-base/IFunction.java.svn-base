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
package org.netxilia.spi.impl.formula.function;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.spi.impl.formula.parser.ASTBaseNode;

/**
 * Defines the operations supported by all functions.
 * 
 * @author catac
 * @since Nov 19, 2009
 */
public interface IFunction {

	/** Get the function's name, in UPPERCASE. */
	public String getName();

	/**
	 * Get the names of input parameters, for dynamic, quick help informative purposes.
	 */
	public String getParamNames();

	/** Get documentation regarding the function, for dynamic, help purposes */
	public String getDocumentation();

	/**
	 * Perform the actual evaluation, with the given list of parameters. This receives the AST Nodes and not the actual
	 * GenericValues to allow for lazy, selective evaluation of parameters and to accommodate AreaReferences.
	 */
	public IGenericValue eval(IFormulaContext context, ASTBaseNode... paramNodes);

	/**
	 * @return true if the value of the can be cached between two calls. i.e. if two subsequent evaluations of this
	 *         formula, if the value of dependencies doesn't change, will return the same result. Typical functions for
	 *         which this should return false are RAND() or NOW().
	 */
	public boolean isCacheble();
}
