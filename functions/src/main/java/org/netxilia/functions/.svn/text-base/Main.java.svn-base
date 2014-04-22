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

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayCalendar;
import net.objectlab.kit.datecalc.joda.LocalDateCalculator;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

public class Main {
	public DateTime WORKDAY(DateTime startDate, int days, Iterator<DateTime> holidays) {
		Set<LocalDate> holidaySet = new HashSet<LocalDate>();
		while (holidays.hasNext())
			holidaySet.add(holidays.next().toLocalDate());

		HolidayCalendar<LocalDate> holidayCalendar = new DefaultHolidayCalendar<LocalDate>(holidaySet);

		DateCalculator<LocalDate> calc = new LocalDateCalculator("", startDate.toLocalDate(), holidayCalendar, null);
		calc = calc.moveByBusinessDays(days);
		return calc.getCurrentBusinessDate().toDateTimeAtStartOfDay();
	}

	public static void main(String[] args) {
		System.out.println(new Main().WORKDAY(new DateTime(), 1, Collections.<DateTime> emptyList().iterator()));
	}
}
