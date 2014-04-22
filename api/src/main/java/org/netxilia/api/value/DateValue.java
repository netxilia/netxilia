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

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class DateValue implements IGenericValue, Serializable {

	private static final long serialVersionUID = 3836743905167601667L;

	public static final DateTime ORIGIN = new DateTime(1900, 1, 1, 0, 0, 0, 0);

	private static final String DATE_PATTERN = "dd/MM/yyyy";
	private static final String TIME_PATTERN = "HH:mm:ss";
	private static final String DATETIME_PATTERN = "dd/MM/yyyy HH:mm:ss";

	// TODO should take it from the locale
	public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormat.forPattern(DATE_PATTERN);
	public static final DateTimeFormatter DEFAULT_DATETIME_FORMATTER = DateTimeFormat.forPattern(DATETIME_PATTERN);
	public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormat.forPattern(TIME_PATTERN);

	private final ReadablePartial value;

	public DateValue(ReadablePartial value) {
		this.value = value;
	}

	public DateValue(String rawString) {
		if (rawString.length() == DATE_PATTERN.length()) {
			this.value = DEFAULT_DATE_FORMATTER.parseDateTime(rawString).toLocalDate();
		} else if (rawString.length() == TIME_PATTERN.length()) {
			this.value = DEFAULT_TIME_FORMATTER.parseDateTime(rawString).toLocalTime();
		} else {
			this.value = DEFAULT_DATETIME_FORMATTER.parseDateTime(rawString).toLocalDateTime();
		}

	}

	@Override
	public Boolean getBooleanValue() {
		return value != null ? Boolean.TRUE : Boolean.FALSE;
	}

	@Override
	public ReadablePartial getDateValue() {
		return value;
	}

	@Override
	public Double getNumberValue() {
		return value != null ? (double) new Duration(ORIGIN, value.toDateTime(ORIGIN)).getMillis()
				/ DateTimeConstants.MILLIS_PER_DAY : Double.valueOf(0);
	}

	@Override
	public String getStringValue() {
		if (value == null) {
			return "";
		}
		if (value instanceof LocalDate) {
			return DEFAULT_DATE_FORMATTER.print(value);
		}
		if (value instanceof LocalTime) {
			return DEFAULT_TIME_FORMATTER.print(value);
		}
		return DEFAULT_DATETIME_FORMATTER.print(value);
	}

	@Override
	public GenericValueType getValueType() {
		return GenericValueType.DATE;
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
		DateValue other = (DateValue) obj;
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
		ReadablePartial other = value.getDateValue();
		if (other == null) {
			if (this.value == null) {
				return 0;
			}
			return 1;
		}
		return this.value.toDateTime(ORIGIN).compareTo(other.toDateTime(ORIGIN));
	}

}
