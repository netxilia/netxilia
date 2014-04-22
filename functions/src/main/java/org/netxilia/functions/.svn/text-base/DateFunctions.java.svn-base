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
package org.netxilia.functions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.joda.LocalDateCalculator;

import org.joda.time.DateTimeConstants;
import org.joda.time.DateTimeFieldType;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.netxilia.api.utils.DateUtils;
import org.netxilia.api.value.DateValue;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.spi.formula.Function;
import org.netxilia.spi.formula.Functions;
import org.netxilia.spi.formula.SkipFunction;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * 
 */
@Functions
public class DateFunctions {
	private final static LocalDateTime ORIGIN = DateValue.ORIGIN.toLocalDateTime();

	private static final LocalDate ORIGIN_DATE = ORIGIN.toLocalDate();

	@Autowired
	private IGenericValueParseService parseService;

	@SkipFunction
	public IGenericValueParseService getParseService() {
		return parseService;
	}

	@SkipFunction
	public void setParseService(IGenericValueParseService parseService) {
		this.parseService = parseService;
	}

	public LocalDate DATE(int year, int month, int day) {
		return new LocalDate(year, month, day);
	}

	public ReadablePartial DATEVALUE(String text) {
		IGenericValue value = parseService.parse(text);
		return value.getDateValue();
	}

	public int DAY(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.dayOfMonth());
	}

	public int DAYS360(ReadablePartial date1, ReadablePartial date2, boolean europeanMethod) {
		return (int) new Duration(DateUtils.toLocalDateTime(date1, ORIGIN).toDateTime(), DateUtils.toLocalDateTime(
				date2, ORIGIN).toDateTime()).getMillis()
				/ DateTimeConstants.MILLIS_PER_DAY;
	}

	/**
	 * The result is a date a number of Months away from the given Start_date. Only months are considered, days are not
	 * used for calculation. Months is the number of months.
	 * 
	 * @param startDate
	 * @param months
	 * @return
	 */
	public ReadablePartial EDATE(ReadablePartial startDate, int months) {
		if (startDate instanceof LocalDate) {
			return ((LocalDate) startDate).plusMonths(months);
		}
		return DateUtils.toLocalDateTime(startDate, ORIGIN).plusMonths(months);
	}

	/**
	 * Returns the date of the last day of a month which falls Months away from the given Start_date. Months is the
	 * number of months before (negative) or after (positive) the start date.
	 * 
	 * @param startDate
	 * @param months
	 * @return
	 */
	public ReadablePartial EOMONTH(ReadablePartial startDate, int months) {
		if (startDate instanceof LocalDate) {
			return ((LocalDate) startDate).plusMonths(months + 1).withDayOfMonth(1).minusDays(1);
		}
		return DateUtils.toLocalDateTime(startDate, ORIGIN).plusMonths(months + 1).withDayOfMonth(1).minusDays(1);
	}

	public int HOUR(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.hourOfDay());
	}

	public int MINUTE(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.minuteOfHour());
	}

	public int MONTH(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.monthOfYear());
	}

	@Function(cacheable = false)
	public ReadablePartial NOW() {
		return new LocalDateTime();
	}

	public int SECOND(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.secondOfMinute());
	}

	@Function(cacheable = false)
	public ReadablePartial TIME(int hour, int minute, int second) {
		return new LocalTime(hour, minute, second, 0);
	}

	@Function(cacheable = false)
	public ReadablePartial TODAY() {
		return new LocalDate();
	}

	/**
	 * Returns the day of the week for the given number (date value). The day is returned as an integer based on the
	 * type. Type determines the type of calculation: type = 1 (default), the weekdays are counted starting from Sunday
	 * (Monday = 0), type = 2, the weekdays are counted starting from Monday (Monday = 1), type = 3, the weekdays are
	 * counted starting from Monday (Monday = 0).
	 * 
	 * @param date
	 * @return
	 */
	public int WEEKDAY(ReadablePartial date) {
		// TODO use type
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.dayOfWeek());
	}

	public int NETWORKDAYS(ReadablePartial startDate, ReadablePartial endDate, Iterator<ReadablePartial> holidays) {
		LocalDate startLocalDate = DateUtils.toLocalDate(startDate, ORIGIN_DATE);
		LocalDate endLocalDate = DateUtils.toLocalDate(endDate, ORIGIN_DATE);
		if (endLocalDate.isBefore(startLocalDate)) {
			return -NETWORKDAYS(endDate, startDate, holidays);
		}

		Set<LocalDate> holidaySet = new HashSet<LocalDate>();
		if (holidays != null) {
			while (holidays.hasNext()) {
				holidaySet.add(DateUtils.toLocalDate(holidays.next(), ORIGIN_DATE));
			}
		}

		HolidayCalendar<LocalDate> holidayCalendar = new DefaultHolidayCalendar<LocalDate>(holidaySet);

		DateCalculator<LocalDate> calc = new LocalDateCalculator("", startLocalDate, holidayCalendar, null);

		int workDays = 0;
		outer: for (LocalDate crtDate = startLocalDate; !crtDate.isAfter(endLocalDate); crtDate = crtDate.plusDays(1)) {
			while (calc.isNonWorkingDay(crtDate)) {
				crtDate = crtDate.plusDays(1);
				if (crtDate.isAfter(endLocalDate)) {
					break outer;
				}
			}
			workDays++;
		}

		return workDays;
	}

	/**
	 * Returns a date number that can be formatted as a date. You then see the date of a day that is a certain number of
	 * Workdays away from the start_date. Holidays (optional) is a list of holidays. Enter a cell range in which the
	 * holidays are listed individually.
	 */
	public ReadablePartial WORKDAY(ReadablePartial startDate, int days, Iterator<ReadablePartial> holidays) {
		Set<LocalDate> holidaySet = new HashSet<LocalDate>();
		if (holidays != null) {
			while (holidays.hasNext()) {
				holidaySet.add(DateUtils.toLocalDate(holidays.next(), ORIGIN_DATE));
			}
		}

		HolidayCalendar<LocalDate> holidayCalendar = new DefaultHolidayCalendar<LocalDate>(holidaySet);

		DateCalculator<LocalDate> calc = new LocalDateCalculator("", DateUtils.toLocalDate(startDate, ORIGIN_DATE),
				holidayCalendar, null);

		calc = calc.moveByBusinessDays(days);
		return calc.getCurrentBusinessDate();
	}

	public int YEAR(ReadablePartial date) {
		return DateUtils.getField(date, ORIGIN, DateTimeFieldType.year());
	}

	/**
	 * Returns a number between 0 and 1, representing the fraction of a year between start_date and end_date. Start_date
	 * and end_date are two date values. Basis is chosen from a list of options and indicates how the year is to be
	 * calculated.
	 * 
	 * @return
	 */
	// public DateTime YEARFRAC(start_date, end_date, basis) {
	//		 
	// }

}
