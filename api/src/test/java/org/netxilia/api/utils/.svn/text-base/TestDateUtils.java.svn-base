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

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

public class TestDateUtils {

	@Test
	public void testParseDateTime() {
		ReadablePartial d = DateUtils.parsePartial(DateTimeFormat.forPattern("dd/MM/yyyy mm:HH:ss"),
				"10/04/2010 10:20:30");
		Assert.assertEquals("2010-04-10T20:10:30.000", d.toString());
		Assert.assertEquals(LocalDateTime.class, d.getClass());
	}

	@Test
	public void testParseDate() {
		ReadablePartial d = DateUtils.parsePartial(DateTimeFormat.forPattern("dd/MM/yyyy"), "10/04/2010");
		Assert.assertEquals("2010-04-10", d.toString());
		Assert.assertEquals(LocalDate.class, d.getClass());
	}

	@Test
	public void testParseTime() {
		ReadablePartial d = DateUtils.parsePartial(DateTimeFormat.forPattern("mm:HH:ss"), "10:20:30");
		Assert.assertEquals("20:10:30.000", d.toString());
		Assert.assertEquals(LocalTime.class, d.getClass());
	}

	@Test
	public void testPartialClass() {
		Assert.assertEquals(LocalTime.class, DateUtils.getPartialClass(DateTimeFormat.forPattern("mm:HH:ss")));
		Assert.assertEquals(LocalDate.class, DateUtils.getPartialClass(DateTimeFormat.forPattern("dd/MM/yyyy")));
		Assert.assertEquals(LocalDateTime.class,
				DateUtils.getPartialClass(DateTimeFormat.forPattern("dd/MM/yyyy mm:HH:ss")));
	}
}
