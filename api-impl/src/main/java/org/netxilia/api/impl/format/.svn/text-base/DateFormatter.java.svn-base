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
package org.netxilia.api.impl.format;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.netxilia.api.display.StyleAttribute;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.utils.DateUtils;
import org.netxilia.api.value.DateValue;
import org.netxilia.api.value.IGenericValue;
import org.springframework.util.Assert;

public class DateFormatter extends AbstractStyleFormatter {
	private final DateTimeFormatter dateTimeFormatter;

	private static final LocalDateTime ORIGIN = DateValue.ORIGIN.toLocalDateTime();

	public DateFormatter(StyleDefinition definition) {
		super(definition);

		String pattern = definition.getAttribute(StyleAttribute.PATTERN);
		Assert.notNull(pattern);

		dateTimeFormatter = DateTimeFormat.forPattern(pattern);
	}

	@Override
	public String format(IGenericValue value) {
		return dateTimeFormatter.print(DateUtils.toLocalDateTime(value.getDateValue(), ORIGIN));
	}

}
