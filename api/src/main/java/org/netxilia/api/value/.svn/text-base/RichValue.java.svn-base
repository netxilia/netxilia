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
import org.netxilia.api.display.Styles;

public class RichValue implements IGenericValue, IDelegateValue {
	private final Styles styles;
	private final String display;
	private final IGenericValue value;

	public RichValue(IGenericValue value, String display, Styles styles) {
		this.value = value;
		this.styles = styles;
		this.display = display;
	}

	public RichValue(IGenericValue value, Styles styles) {
		this(value, null, styles);
	}

	public RichValue(IGenericValue value, String display) {
		this(value, display, null);
	}

	public RichValue(IGenericValue value) {
		this(value, null, null);
	}

	public IGenericValue getValue() {
		return value;
	}

	public String getDisplay() {
		return display;
	}

	public Styles getStyles() {
		return styles;
	}

	public int compareTo(IGenericValue o) {
		return value.compareTo(o);
	}

	public Boolean getBooleanValue() {
		return value.getBooleanValue();
	}

	public ReadablePartial getDateValue() {
		return value.getDateValue();
	}

	public Double getNumberValue() {
		return value.getNumberValue();
	}

	public String getStringValue() {
		return value.getStringValue();
	}

	public GenericValueType getValueType() {
		return value.getValueType();
	}

	@Override
	public IGenericValue getGenericValue() {
		return value;
	}

}
