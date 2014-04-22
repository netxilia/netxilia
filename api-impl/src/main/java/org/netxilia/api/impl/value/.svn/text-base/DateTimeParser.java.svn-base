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
package org.netxilia.api.impl.value;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.netxilia.api.utils.DateUtils;
import org.netxilia.api.value.DateValue;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParser;
import org.springframework.beans.factory.InitializingBean;

/**
 * inspired by Stripes Date converter
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DateTimeParser implements IGenericValueParser, InitializingBean {
	private Locale locale = Locale.getDefault();

	private String datePatterns[];
	private String timePatterns[];

	private List<DateTimeFormatter> dateTimeFormatters;

	/**
	 * <p>
	 * A pattern used to pre-process Strings before the parsing attempt is made. Since SimpleDateFormat strictly
	 * enforces that the separator characters in the input are the same as those in the pattern, this regular expression
	 * is used to remove commas, slashes, hyphens and periods from the input String (replacing them with spaces) and to
	 * collapse multiple white-space characters into a single space.
	 * </p>
	 * 
	 * <p>
	 * This pattern can be changed by providing a different value under the
	 * <code>'stripes.dateTypeConverter.preProcessPattern'</code> key in the resource bundle. The default value is
	 * <code>(?&lt;!GMT)[\\s,-/\\.]+</code>.
	 * </p>
	 */
	private static final Pattern PRE_PROCESS_PATTERN = Pattern.compile("(?<!GMT)[\\s/\\.-]+");

	@Override
	public IGenericValue parse(String text) {
		String input = preProcessInput(text);
		for (DateTimeFormatter formatter : dateTimeFormatters) {
			try {
				return new DateValue(DateUtils.parsePartial(formatter, input));
			} catch (IllegalArgumentException e) {
				// try next pattern
			}
		}
		return null;
	}

	/**
	 * Pre-processes the input String to improve the chances of parsing it. First uses the regular expression Pattern
	 * returned by getPreProcessPattern() to remove all separator chars and ensure that components are separated by
	 * single spaces. Then invokes {@link #checkAndAppendYear(String)} to append the year to the date in case the date
	 * is in a format like "12/25" which would otherwise fail to parse.
	 */
	protected String preProcessInput(String input) {
		input = PRE_PROCESS_PATTERN.matcher(input.trim()).replaceAll(" ");
		input = checkAndAppendYear(input);
		return input;
	}

	/**
	 * Checks to see how many 'parts' there are to the date (separated by spaces) and if there are only two parts it
	 * adds the current year to the end by getting the Locale specific year string from a Calendar instance.
	 * 
	 * @param input
	 *            the date string after the pre-process pattern has been run against it
	 * @return either the date string as is, or with the year appended to the end
	 */
	protected String checkAndAppendYear(String input) {
		// Count the spaces, date components = spaces + 1
		int count = 0;
		for (char ch : input.toCharArray()) {
			if (ch == ' ') {
				++count;
			}
		}

		// Looks like we probably only have a day and month component, that won't work!
		if (count == 1) {
			input += " " + Calendar.getInstance(locale).get(Calendar.YEAR);
		}
		return input;
	}

	public String[] getDatePatterns() {
		return datePatterns;
	}

	public void setDatePatterns(String[] datePatterns) {
		this.datePatterns = datePatterns;
	}

	public String[] getTimePatterns() {
		return timePatterns;
	}

	public void setTimePatterns(String[] timePatterns) {
		this.timePatterns = timePatterns;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// add date, time, and date + time
		dateTimeFormatters = new ArrayList<DateTimeFormatter>();

		for (String datePattern : datePatterns) {
			dateTimeFormatters.add(DateTimeFormat.forPattern(datePattern));
		}

		for (String timePattern : timePatterns) {
			dateTimeFormatters.add(DateTimeFormat.forPattern(timePattern));
		}

		for (String datePattern : datePatterns) {
			for (String timePattern : timePatterns) {
				dateTimeFormatters.add(DateTimeFormat.forPattern(datePattern + " " + timePattern));
			}
		}
	}
}
