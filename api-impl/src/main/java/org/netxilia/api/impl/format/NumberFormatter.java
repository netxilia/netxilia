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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang.LocaleUtils;
import org.netxilia.api.display.StyleAttribute;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IGenericValue;

/**
 * This class formats the numbers. A format pattern can be given.
 * 
 * @author sa
 * 
 */
public class NumberFormatter extends AbstractStyleFormatter {

	private final String pattern;
	private String locale;

	private Locale localeObject;

	private final ThreadLocal<NumberFormat> formatHolder = new ThreadLocal<NumberFormat>() {
		@Override
		protected NumberFormat initialValue() {
			return buildFormat();
		};
	};

	public NumberFormatter(StyleDefinition definition) {
		super(definition);
		this.pattern = definition.getAttribute(StyleAttribute.PATTERN);
		// Assert.notNull(this.pattern);
	}

	public GenericValueType getValueType() {
		return GenericValueType.NUMBER;
	}

	protected NumberFormat buildFormat() {
		if (localeObject == null) {
			throw new IllegalStateException("The init method was not called");
		}
		return new DecimalFormat(getPattern(), new DecimalFormatSymbols(localeObject));
	}

	private NumberFormat getFormat() {
		return formatHolder.get();
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public void setLocaleObject(Locale localeObject) {
		this.localeObject = localeObject;
	}

	public String getPattern() {
		return pattern;
	}

	public String getLocale() {
		return locale;
	}

	/**
	 * this should be called before using the formatter!
	 */
	public void init() {
		this.localeObject = locale != null ? LocaleUtils.toLocale(locale) : Locale.getDefault();
	}

	public Locale getLocaleObject() {
		return localeObject;
	}

	@Override
	public String format(IGenericValue value) {
		if (value == null) {
			return "";
		}
		Double n = value.getNumberValue();
		if (n == null) {
			return "";
		}
		return getFormat().format(n);
	}

}
