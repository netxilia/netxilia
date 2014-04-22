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

import java.text.NumberFormat;
import java.util.Locale;

import org.apache.commons.lang.math.NumberUtils;
import org.netxilia.api.display.StyleDefinition;

public class CurrencyFormatter extends NumberFormatter {

	private static final long serialVersionUID = -260961767872663089L;
	private static final String ATT_DECIMALS = "decimals";
	private static final String ATT_COUNTRY = "country";
	private final int decimalCount;

	private final String country;

	public CurrencyFormatter(StyleDefinition definition) {
		super(definition);
		this.decimalCount = NumberUtils.toInt(definition.getAttribute(ATT_DECIMALS), -1);
		this.country = definition.getAttribute(ATT_COUNTRY);
	}

	public int getDecimalCount() {
		return decimalCount;
	}

	@Override
	protected NumberFormat buildFormat() {
		// TODO find a way to reuse formatters
		NumberFormat format = NumberFormat.getCurrencyInstance(getLocaleObject());
		if (decimalCount >= 0) {
			format.setMaximumFractionDigits(decimalCount);
		}
		// format.setCurrency(Currency.getInstance(getPattern().getPattern()));
		return format;
	}

	@Override
	public void init() {
		if (country != null) {
			if (country.equals("EUR")) {
				setLocaleObject(new Locale("fr", "FR", "EUR"));
			} else {
				Locale setLocale = Locale.getDefault();
				for (Locale locale : Locale.getAvailableLocales()) {
					if (country.equalsIgnoreCase(locale.getCountry())) {
						setLocale = locale;
						break;
					}
				}
				setLocaleObject(setLocale);
			}
		} else {
			setLocaleObject(Locale.getDefault());
		}
	}
}
