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

import org.joda.time.ReadablePartial;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IDelegateValue;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.StringValue;
import org.netxilia.spi.impl.formula.parser.ASTBaseNode;

/**
 * This generic value is used to lazily evaluate a node, in the case for example of a AND operation where if one node is
 * false, subsequent nodes should not be evaluated.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class LazyGenericValue implements IGenericValue, IDelegateValue {
	private final static IGenericValue NOT_CALCULATED = new StringValue("NOT_CALCULATED");
	private final ASTBaseNode node;
	private final IFormulaContext context;
	private volatile IGenericValue genericValue = NOT_CALCULATED;

	public LazyGenericValue(ASTBaseNode node, IFormulaContext context) {
		this.node = node;
		this.context = context;
	}

	public IGenericValue getGenericValue() {
		if (genericValue == NOT_CALCULATED) {
			genericValue = node.eval(context);
		}
		// TODO what to do with NULL
		return genericValue;
	}

	@Override
	public Boolean getBooleanValue() {
		return getGenericValue().getBooleanValue();
	}

	@Override
	public ReadablePartial getDateValue() {
		return getGenericValue().getDateValue();
	}

	@Override
	public Double getNumberValue() {
		return getGenericValue().getNumberValue();
	}

	@Override
	public String getStringValue() {
		return getGenericValue().getStringValue();
	}

	@Override
	public GenericValueType getValueType() {
		return getGenericValue().getValueType();
	}

	@Override
	public int compareTo(IGenericValue o) {
		return getGenericValue().compareTo(o);
	}

	@Override
	public boolean equals(Object obj) {
		return getGenericValue().equals(obj);
	}

	@Override
	public int hashCode() {
		return getGenericValue().hashCode();
	}

}
