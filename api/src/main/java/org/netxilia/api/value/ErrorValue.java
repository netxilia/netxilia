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

import org.joda.time.ReadablePartial;

public class ErrorValue implements IGenericValue, Serializable {

	private static final long serialVersionUID = -813056889077405223L;
	private final ErrorValueType errorType;

	public ErrorValue(ErrorValueType errorType) {
		this.errorType = errorType;
	}

	public ErrorValue(String rawString) {
		String errorType = rawString.substring(1); // skip the #
		this.errorType = ErrorValueType.valueOf(errorType);
	}

	public ErrorValueType getErrorType() {
		return errorType;
	}

	public Boolean getBooleanValue() {
		return null;
	}

	public Double getNumberValue() {
		return null;
	}

	public String getStringValue() {
		return "#" + errorType.name();
	}

	@Override
	public ReadablePartial getDateValue() {
		return null;
	}

	public GenericValueType getValueType() {
		return GenericValueType.ERROR;
	}

	@Override
	public String toString() {
		return "Formula Error: " + getErrorType();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((errorType == null) ? 0 : errorType.hashCode());
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
		ErrorValue other = (ErrorValue) obj;
		if (errorType == null) {
			if (other.errorType != null) {
				return false;
			}
		} else if (!errorType.equals(other.errorType)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(IGenericValue value) {
		if (value == null) {
			return 1;
		}
		if (!(value instanceof ErrorValue)) {
			return -1;
		}
		ErrorValueType other = ((ErrorValue) value).errorType;
		if (other == null) {
			if (this.errorType == null) {
				return 0;
			}
			return 1;
		}
		return this.errorType.compareTo(other);
	}
}
