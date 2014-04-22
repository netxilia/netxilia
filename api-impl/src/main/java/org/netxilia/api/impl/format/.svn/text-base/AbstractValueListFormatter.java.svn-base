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

import java.util.List;

import org.netxilia.api.display.IValueListStyleFormatter;
import org.netxilia.api.display.StyleDefinition;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.NamedValue;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public abstract class AbstractValueListFormatter extends AbstractStyleFormatter implements IValueListStyleFormatter {

	private static final long serialVersionUID = 1L;

	private boolean multivalue = true;
	private char separator = ',';

	private String unknownPrefix = "?";

	public AbstractValueListFormatter(StyleDefinition definition) {
		super(definition);
	}

	@Override
	public String format(IGenericValue value) {
		if (value == null) {
			return "";
		}
		String valueAsString = value.getStringValue();
		if (valueAsString == null) {
			return "";
		}
		if (multivalue) {
			String[] valueItems = valueAsString.split("[" + separator + "]");
			String[] names = new String[valueItems.length];
			for (int i = 0; i < valueItems.length; ++i) {
				NamedValue namedValue = searchByValue(valueItems[i]);
				names[i] = namedValue != null ? namedValue.getName() : unknownValue(valueItems[i]);
			}
			StringBuilder nameAsString = new StringBuilder();
			for (String name : names) {
				if (nameAsString.length() != 0) {
					nameAsString.append(separator);
				}
				nameAsString.append(name);
			}
			return nameAsString.toString();
		}
		NamedValue namedValue = searchByValue(valueAsString);
		return namedValue != null ? namedValue.getName() : unknownValue(valueAsString);
	}

	protected String unknownValue(String valueAsString) {
		return unknownPrefix + valueAsString;
	}

	public abstract List<NamedValue> getValues();

	// protected NamedValue searchByNameAndValue(String text) {
	// NamedValue nv = searchByName(text);
	// if (nv != null) {
	// return nv;
	// }
	// return searchByValue(text);
	// }

	/**
	 * The text can be either the name of a value or directly a value as a text. In any of the cases the corresponding
	 * IGenericValue should be returned. If none found, null must be returned.
	 * 
	 * Implementing classes may decide to use hashmaps to speed up the search process.
	 * 
	 * @param text
	 * @return
	 */
	// protected NamedValue searchByName(String text) {
	// List<NamedValue> namedValues = getValues();
	// if (namedValues == null) {
	// return null;
	// }
	// // search by name
	// for (NamedValue nv : namedValues) {
	// if (nv.getName().equals(text)) {
	// return nv;
	// }
	// }
	//
	// return null;
	// }

	/**
	 * Return the name of the
	 * 
	 * @param valueAsString
	 * @return
	 */
	protected NamedValue searchByValue(String text) {
		List<NamedValue> namedValues = getValues();
		if (namedValues == null) {
			return null;
		}
		// search by value
		for (NamedValue nv : namedValues) {
			if (nv.getValue() != null && nv.getValue().toString().equals(text)) {
				return nv;
			}
		}
		return null;
	}

}
