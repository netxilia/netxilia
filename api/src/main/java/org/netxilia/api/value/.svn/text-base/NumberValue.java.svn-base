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

public class NumberValue implements IGenericValue, Serializable {

	private static final long serialVersionUID = -3223433340850806258L;

	public final static double APPROXIMATE_ZERO = 1e-10;

	private final Double value;

	public NumberValue(Double value) {
		this.value = value;
	}

	public NumberValue(int value) {
		this(Double.valueOf(value));
	}

	public NumberValue(double value) {
		this(Double.valueOf(value));
	}

	public NumberValue(String rawString) {
		this(Double.valueOf(rawString));
	}

	public Boolean getBooleanValue() {
		return Boolean.valueOf((value != null) && (Math.abs(value.doubleValue()) > APPROXIMATE_ZERO));
	}

	public Double getNumberValue() {
		return value;
	}

	public String getStringValue() {
		if (value == null) {
			return "";
		}
		long lVal = value.longValue();
		double dVal = value.doubleValue();
		return (Math.abs(dVal - lVal) < APPROXIMATE_ZERO) ? Long.toString(lVal) : Double.toString(dVal);
	}

	@Override
	public ReadablePartial getDateValue() {
		return value != null ? DateValue.ORIGIN.plus((long) (value * DateTimeConstants.MILLIS_PER_DAY)).toLocalDate()
				: DateValue.ORIGIN.toLocalDate();
	}

	public GenericValueType getValueType() {
		return GenericValueType.NUMBER;
	}

	@Override
	public String toString() {
		return getStringValue();
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
		NumberValue other = (NumberValue) obj;
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
		Double other = value.getNumberValue();
		if (other == null) {
			if (this.value == null) {
				return 0;
			}
			return 1;
		}
		return this.value.compareTo(other);
	}
}
