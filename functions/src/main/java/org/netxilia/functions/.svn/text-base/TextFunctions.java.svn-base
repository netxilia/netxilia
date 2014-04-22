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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.spi.formula.Functions;
import org.netxilia.spi.formula.SkipFunction;
import org.springframework.beans.factory.annotation.Autowired;

/*
 * 

 */
@Functions
public class TextFunctions {
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

	public IGenericValue VALUE(String text) {
		return parseService.parse(text);
	}

	public String CHAR(int number) {
		return String.valueOf((char) number);
	}

	/**
	 * Returns a numeric code for the first character in a text string. Text is the text for which the code of the first
	 * character is to be found.
	 * 
	 * @return
	 */
	public int CODE(String text) {
		return text.isEmpty() ? 0 : text.charAt(0);
	}

	public String CONCATENATE(Iterator<String> values) {
		StringBuilder sb = new StringBuilder();
		while (values.hasNext()) {
			sb.append(values.next());
		}
		return sb.toString();
	}

	public String DOLLAR(double value, int decimals) {
		NumberFormat format = NumberFormat.getCurrencyInstance();
		format.setMaximumFractionDigits(decimals);
		return format.format(value);
	}

	public boolean EXACT(String text1, String text2) {
		return text1.equals(text2);
	}

	public int FIND(String findText, String text, int start) {
		return text.indexOf(findText, start + 1) + 1;
	}

	public String FIXED(double number, int decimals, boolean no_thousands_separator) {
		NumberFormat format = NumberFormat.getInstance();
		format.setMaximumFractionDigits(decimals);
		format.setGroupingUsed(!no_thousands_separator);
		return format.format(number);
	}

	public String LEFT(String text, int number) {
		return text.substring(0, number);
	}

	public int LEN(String text) {
		return text.length();
	}

	public String LOWER(String text) {
		return text.toLowerCase();
	}

	public String MID(String text, int start, int number) {
		return text.substring(start, start - 1 + number);
	}

	/**
	 * Capitalizes the first letter in all words of a text string.
	 * 
	 * @return
	 */
	public String PROPER(String text) {
		return WordUtils.capitalize(text);
	}

	/**
	 * Replaces part of a text string with a different text string. This function can be used to replace both characters
	 * and numbers (which are automatically converted to text). The result of the function is always displayed as text.
	 * To perform further calculations with a number which has been replaced by text, convert it back to a number using
	 * the VALUE function. Any text containing numbers must be enclosed in quotation marks so it is not interpreted as a
	 * number and automatically converted to text. Text is text of which a part will be replaced. Position is the
	 * position within the text where the replacement will begin. Length is the number of characters in text to be
	 * replaced. New_text is the text which replaces text.
	 * 
	 * @return
	 */
	public String REPLACE(String text, int position, int length, String newText) {
		int p = position - 1; // position is 1-based
		if (p >= text.length() || p + length >= text.length()) {
			return text;
		}
		return text.substring(0, p) + newText + text.substring(p + length);
	}

	public String REPT(String text, int number) {
		return StringUtils.repeat(text, number);
	}

	public String RIGHT(String text, int number) {
		return text.substring(Math.max(0, text.length() - number), text.length());
	}

	/**
	 * Returns the position of a text segment within a character string. The start of the search can be set as an
	 * option. The search text can be a number or any sequence of characters. The search is not case-sensitive.
	 * Find_text is the text to be searched for. Text is the text where the search will take place. Position (optional)
	 * is the position in the text where the search is to start.
	 * 
	 * @return
	 */
	public int SEARCH(String findText, String text, int start) {
		return text.toLowerCase().indexOf(findText.toLowerCase(), start - 1) + 1;
	}

	/**
	 * Splits text based on the given delimiter, putting each section into a separate column in the row.
	 * 
	 * @param string
	 * @param delimiter
	 * @return
	 */
	// public String SPLIT(String string, String delimiter){
	//		
	// }

	/**
	 * Substitutes new text for old text in a string. Text is the text in which text segments are to be exchanged.
	 * Search_text is the text segment that is to be replaced (a number of times). New text is the text that is to
	 * replace the text segment. Occurrence (optional) indicates how many occurrences of the search text are to be
	 * replaced.
	 */
	public String SUBSTITUTE(String text, String searchText, String newText, int occurrence) {
		return StringUtils.replace(text, searchText, newText, occurrence == 0 ? -1 : occurrence);
	}

	/**
	 * Converts a number to a blank text string. Value is the value to be converted. Also, a reference can be used as a
	 * parameter. If the referenced cell includes a number or a formula containing a numerical result, the result will
	 * be an empty string.
	 * 
	 * @param value
	 * @return
	 */
	public String T(IGenericValue value) {
		if (value.getValueType() == GenericValueType.STRING) {
			return value.getStringValue();
		}
		return "";
	}

	public String TEXT(double number, String format) {
		return new DecimalFormat(format).format(number);
	}

	public String TRIM(String text) {
		return text.trim();
	}

	public String UPPER(String text) {
		return text.toUpperCase();
	}
}
