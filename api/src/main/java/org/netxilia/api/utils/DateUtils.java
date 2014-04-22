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
package org.netxilia.api.utils;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.joda.time.Chronology;
import org.joda.time.DateTime;
import org.joda.time.DateTimeField;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DurationFieldType;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParserBucket;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DateUtils {
	/**
	 * 
	 * @param formatter
	 * @param text
	 * @return the ReadablePartial corresponding to the given formatter and text
	 */
	public static ReadablePartial parsePartial(DateTimeFormatter formatter, String text) {
		PartialDateTimeParserBucket bucket = new PartialDateTimeParserBucket();
		int newPos = formatter.getParser().parseInto(bucket, text, 0);

		if (newPos >= 0 && newPos >= text.length()) {
			long millis = bucket.computeMillis(true, text);
			DateTime dt = new DateTime(millis, (Chronology) null);
			if (bucket.isHasDate() && bucket.isHasTime()) {
				return dt.toLocalDateTime();
			}
			if (bucket.isHasDate()) {
				return dt.toLocalDate();
			}
			return dt.toLocalTime();
		}
		throw new IllegalArgumentException("Cannot parse date:" + text);
	}

	public static Class<? extends ReadablePartial> getPartialClass(DateTimeFormatter dateTimeFormatter) {
		String text = dateTimeFormatter.print(System.currentTimeMillis());
		ReadablePartial parsed = parsePartial(dateTimeFormatter, text);
		return parsed.getClass();
	}

	public static LocalDateTime toLocalDateTime(ReadablePartial partial, LocalDateTime fullDateTime) {
		if (partial instanceof LocalDateTime) {
			return (LocalDateTime) partial;
		}
		if (partial instanceof LocalDate) {
			LocalDate d = (LocalDate) partial;
			return new LocalDateTime(d.getYear(), d.getMonthOfYear(), d.getDayOfMonth(), fullDateTime.getHourOfDay(),
					fullDateTime.getMinuteOfHour(), fullDateTime.getSecondOfMinute(), fullDateTime.getMillisOfSecond());
		}

		if (partial instanceof LocalTime) {
			LocalTime t = (LocalTime) partial;
			return new LocalDateTime(fullDateTime.getYear(), fullDateTime.getMonthOfYear(), fullDateTime
					.getDayOfMonth(), t.getHourOfDay(), t.getMinuteOfHour(), t.getSecondOfMinute(), t
					.getMillisOfSecond());
		}

		throw new IllegalArgumentException("The partial parameter has an unsupported class:" + partial.getClass());
	}

	public static LocalDate toLocalDate(ReadablePartial partial, LocalDate fullDate) {
		if (partial instanceof LocalDateTime) {
			LocalDateTime d = (LocalDateTime) partial;
			return d.toLocalDate();
		}
		if (partial instanceof LocalDate) {
			return (LocalDate) partial;
		}

		if (partial instanceof LocalTime) {
			return fullDate;
		}

		throw new IllegalArgumentException("The partial parameter has an unsupported class:" + partial.getClass());
	}

	public static int getField(ReadablePartial partial, LocalDateTime fullDateTime, DateTimeFieldType field) {
		return partial.isSupported(field) ? partial.get(field) : fullDateTime.get(field);
	}

	/**
	 * 
	 * Tracks the types of field set.
	 * 
	 */
	private static class PartialDateTimeParserBucket extends DateTimeParserBucket {
		private static final Set<DurationFieldType> TIME_DURATION_TYPES = new HashSet<DurationFieldType>();
		static {
			TIME_DURATION_TYPES.add(DurationFieldType.millis());
			TIME_DURATION_TYPES.add(DurationFieldType.seconds());
			TIME_DURATION_TYPES.add(DurationFieldType.minutes());
			TIME_DURATION_TYPES.add(DurationFieldType.hours());
		}
		private boolean hasTime = false;
		private boolean hasDate = false;

		public PartialDateTimeParserBucket() {
			super(0, null, null);
		}

		private void checkDateTime(DateTimeFieldType fieldType) {
			boolean isTime = TIME_DURATION_TYPES.contains(fieldType.getDurationType());
			hasTime = isTime || hasTime;
			hasDate = !isTime || hasDate;
		}

		public boolean isHasTime() {
			return hasTime;
		}

		public boolean isHasDate() {
			return hasDate;
		}

		@Override
		public void saveField(DateTimeField field, int value) {
			checkDateTime(field.getType());
			super.saveField(field, value);
		}

		@Override
		public void saveField(DateTimeFieldType fieldType, int value) {
			checkDateTime(fieldType);
			super.saveField(fieldType, value);
		}

		@Override
		public void saveField(DateTimeFieldType fieldType, String text, Locale locale) {
			checkDateTime(fieldType);
			super.saveField(fieldType, text, locale);
		}
	}

}
