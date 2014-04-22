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
package org.netxilia.api.value;

import org.joda.time.ReadablePartial;
import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.ReferenceUtils;

/**
 * This is a special value to lazily resolve references.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ReferenceValue implements IGenericValue, IDelegateValue {
	public static final IGenericValue DEFAULT_VALUE = new StringValue("");

	private final AreaReference reference;
	private final IFormulaContext context;

	public ReferenceValue(AreaReference reference, IFormulaContext context) {
		this.reference = reference;
		this.context = context;
	}

	public AreaReference getReference() {
		return reference;
	}

	public IFormulaContext getContext() {
		return context;
	}

	public IGenericValue getGenericValue() {
		CellReference relativeReference = ReferenceUtils.toOneCell(reference, context.getCell());
		IGenericValue value = context.getCellValue(relativeReference);
		return value != null ? value : DEFAULT_VALUE;
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

}
