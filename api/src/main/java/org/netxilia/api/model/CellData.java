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
package org.netxilia.api.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;

import org.netxilia.api.display.Styles;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.utils.ObjectUtils;
import org.netxilia.api.value.IGenericValue;

/**
 * Cell data
 * 
 * @author alexandru craciun
 * 
 */
public class CellData {
	public enum Property {
		value, formula, styles, type
	}

	private final CellReference reference;
	private final IGenericValue value;
	private final Formula formula;
	private final Styles styles;

	public CellData(CellReference reference) {
		this(reference, null, null, null);
	}

	public CellData(CellReference reference, IGenericValue value) {
		this(reference, value, null, null);
	}

	public CellData(CellReference reference, Formula formula) {
		this(reference, null, formula, null);
	}

	public CellData(CellReference reference, IGenericValue value, Formula formula, Styles styles) {
		this.reference = reference;
		this.value = value;
		this.formula = formula;
		this.styles = styles;
	}

	public IGenericValue getValue() {
		return value;
	}

	public Formula getFormula() {
		return formula;
	}

	public CellReference getReference() {
		return reference;
	}

	public Styles getStyles() {
		return styles;
	}

	@Override
	public String toString() {
		return reference + " -> [value=" + value + ", formula=" + formula + ", styles=" + styles + "]";
	}

	public CellData withStyles(Styles newStyle) {
		return new CellData(reference, value, formula, newStyle);
	}

	public CellData withValue(IGenericValue newValue) {
		return new CellData(reference, newValue, formula, styles);
	}

	public CellData withFormula(Formula newFormula) {
		return new CellData(reference, value, newFormula, styles);
	}

	public CellData withData(CellData data) {
		return new CellData(reference, data.value, data.formula, data.styles);
	}

	public static Collection<Property> diff(CellData cell1, CellData cell2) {
		if (cell1 == null || cell2 == null) {
			return Arrays.asList(Property.values());
		}
		if (!cell1.reference.equals(cell2.reference)) {
			throw new IllegalArgumentException("The reference should be identical:" + cell2.reference);
		}
		Collection<Property> properties = EnumSet.noneOf(Property.class);
		if (!ObjectUtils.equals(cell1.value, cell2.value)) {
			properties.add(Property.value);
		}
		if (!ObjectUtils.equals(cell1.formula, cell2.formula)) {
			properties.add(Property.formula);
		}
		if (!ObjectUtils.equals(cell1.styles, cell2.styles)) {
			properties.add(Property.styles);
		}

		return properties;
	}

	public CellData withProperties(IGenericValue value, Formula formula, Styles styles,
			Collection<Property> changeProperties) {
		if (changeProperties.isEmpty()) {
			return this;
		}
		return new CellData(reference, changeProperties.contains(Property.value) ? value : this.value,
				changeProperties.contains(Property.formula) ? formula : this.formula,
				changeProperties.contains(Property.styles) ? styles : this.styles);
	}
}
