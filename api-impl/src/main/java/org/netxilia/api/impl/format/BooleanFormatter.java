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
package org.netxilia.api.impl.format;

import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.value.IGenericValue;

public class BooleanFormatter extends AbstractStyleFormatter {

	private static final long serialVersionUID = -2353431091645917030L;

	private String trueString = Boolean.TRUE.toString().toUpperCase();
	private String falseString = Boolean.FALSE.toString().toUpperCase();

	public BooleanFormatter(StyleDefinition definition) {
		super(definition);
	}

	@Override
	public String format(IGenericValue value) {
		Boolean b = value.getBooleanValue();
		return b.booleanValue() ? trueString : falseString;
	}

}
