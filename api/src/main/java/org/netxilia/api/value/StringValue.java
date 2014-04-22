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

import java.io.Serializable;

import org.joda.time.DateTimeConstants;
import org.joda.time.ReadablePartial;

public class StringValue implements IGenericValue, Serializable {

	private static final long serialVersionUID = 8445304044484941642L;
	private final String value;

	public StringValue(String value) {
		this.value = value;
	}

	public Boolean getBooleanValue() {
		return (value == null) || (value.length() == 0) ? Boolean.FALSE : Boolean.TRUE;
	}

	public Double getNumberValue() {
		if ((value == null) || (value.length() == 0)) {
			return Double.valueOf(0);
		}
		try {
			return Double.valueOf(value);
		} catch (Exception e) {
			return null;
		}
	}

	public String getStringValue() {
		return value;
	}

	@Override
	public ReadablePartial getDateValue() {
		Double numberValue = getNumberValue();
		return numberValue != null ? DateValue.ORIGIN.plus((long) (numberValue * DateTimeConstants.MILLIS_PER_DAY))
				.toLocalDate() : DateValue.ORIGIN.toLocalDate();
	}

	public GenericValueType getValueType() {
		return GenericValueType.STRING;
	}

	@Override
	public String toString() {
		return value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		StringValue other = (StringValue) obj;
		if (value == null) {
			if (other.value != null) {
				return false;
			}
		} else if (!value.equals(other.value)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(IGenericValue value) {
		if (value == null) {
			return 1;
		}
		String other = value.getStringValue();
		if (other == null) {
			if (this.value == null) {
				return 0;
			}
			return 1;
		}
		return this.value.compareTo(other);
	}

}
