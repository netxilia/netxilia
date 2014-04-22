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

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Currency;
import java.util.Locale;

import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParser;
import org.netxilia.api.value.NumberValue;
import org.springframework.beans.factory.InitializingBean;

/**
 * Taken from Stripes number converter
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NumberParser implements IGenericValueParser, InitializingBean {
	private Locale locale;
	private NumberFormat[] formats;
	private String currencySymbol;

	private String localeId;

	/** Returns the Locale set on the object using setLocale(). */
	public Locale getLocale() {
		return locale;
	}

	public String getLocaleId() {
		return localeId;
	}

	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}

	/**
	 * Fetches one or more NumberFormat instances that can be used to parse numbers for the current locale. The default
	 * implementation returns two instances, one regular NumberFormat and a currency instance of NumberFormat.
	 * 
	 * @return one or more NumberFormats to use in parsing numbers
	 */
	protected NumberFormat[] getNumberFormats() {
		return new NumberFormat[] { NumberFormat.getInstance(this.locale) };
	}

	@Override
	public IGenericValue parse(String text) {
		String input = preprocess(text);
		ParsePosition pp = new ParsePosition(0);

		for (NumberFormat format : this.formats) {
			pp.setIndex(0);
			Number number = format.parse(input, pp);
			if (number != null && input.length() == pp.getIndex()) {
				return new NumberValue(number.doubleValue());
			}
		}

		return null;
	}

	/**
	 * Pre-processes the String to give the NumberFormats a better shot at parsing the input. The default implementation
	 * trims the String for whitespace and then looks to see if the number is surrounded by parentheses, e.g. (800), and
	 * if so removes the parentheses and prepends a minus sign. Lastly it will remove the currency symbol from the
	 * String so that we don't have to use too many NumberFormats!
	 * 
	 * @param input
	 *            the String as input by the user
	 * @return the result of preprocessing the String
	 */
	protected String preprocess(String input) {
		// Step 1: trim whitespace
		String output = input.trim();

		// Step 2: remove the currency symbol
		// The casts are to make sure we don't call replace(String regex, String replacement)
		output = output.replace(currencySymbol, "");

		// Step 3: replace parentheses with negation
		if (output.startsWith("(") && output.endsWith(")")) {
			output = "-" + output.substring(1, output.length() - 1);
		}

		return output;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.locale = localeId != null ? new Locale(localeId) : Locale.getDefault();
		this.formats = getNumberFormats();

		// Use the appropriate currency symbol if our locale has a country, otherwise try the dollar sign!
		if (locale.getCountry() != null && !"".equals(locale.getCountry())) {
			this.currencySymbol = Currency.getInstance(locale).getSymbol(locale);
		} else {
			this.currencySymbol = "$";
		}

	}
}
