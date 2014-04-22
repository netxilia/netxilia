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
package org.netxilia.api.command;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import org.netxilia.api.display.Styles;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.CellData.Property;
import org.netxilia.api.model.CellDataWithProperties;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.value.IGenericValue;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CellCommands {

	public static ICellCommand styles(final AreaReference ref, final Styles styles) {
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				return new CellDataWithProperties(data.withStyles(styles), CellData.Property.styles);
			}

		};
	}

	public static ICellCommand formula(final AreaReference ref, final Formula formula) {
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				return new CellDataWithProperties(data.withFormula(formula), CellData.Property.formula);
			}

		};
	}

	public static ICellCommand value(final AreaReference ref, final IGenericValue value) {
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				return new CellDataWithProperties(data.withValue(value), CellData.Property.value);
			}

		};
	}

	public static ICellCommand cell(final AreaReference ref, final IGenericValue value, final Styles styles) {
		return cell(ref, value, null, styles);
	}

	public static ICellCommand cell(final AreaReference ref, final Formula formula, final Styles styles) {
		return cell(ref, null, formula, styles);
	}

	public static ICellCommand cell(final AreaReference ref, final IGenericValue value, final Formula formula,
			final Styles styles) {
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				CellData newData = new CellData(data.getReference(), value, formula, styles);
				return new CellDataWithProperties(newData, CellData.diff(data, newData));
			}

		};
	}

	public static ICellCommand mapValues(final AreaReference ref, Map<String, IGenericValue> values) {
		final Map<String, IGenericValue> immValues = ImmutableMap.copyOf(values);

		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				IGenericValue value = immValues.get(CellReference.columnLabel(data.getReference().getColumnIndex()));
				return new CellDataWithProperties(data.withValue(value), Property.value);
			}

		};
	}

	public static ICellCommand properties(final AreaReference ref, final IGenericValue value, final Styles styles,
			final Formula formula, Collection<CellData.Property> properties) {
		final Collection<CellData.Property> immProperties = EnumSet.copyOf(properties);
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				return new CellDataWithProperties(data.withProperties(value, formula, styles, immProperties),
						immProperties);
			}

		};
	}

	public static ICellCommand row(final AreaReference ref, List<CellData> row) {
		final List<CellData> immRow = ImmutableList.copyOf(row);
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				if (data.getReference().getColumnIndex() < immRow.size()) {
					CellData newData = data.withData(immRow.get(data.getReference().getColumnIndex()));
					return new CellDataWithProperties(newData, CellData.diff(data, newData));
				}
				return new CellDataWithProperties(data);
			}

		};
	}

	public static ICellCommand rowValues(final AreaReference ref, List<IGenericValue> row) {
		final List<IGenericValue> immRow = ImmutableList.copyOf(row);
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				if (data.getReference().getColumnIndex() < immRow.size()) {
					return new CellDataWithProperties(data.withValue(immRow.get(data.getReference().getColumnIndex())),
							CellData.Property.value);
				}
				return new CellDataWithProperties(data, Collections.<CellData.Property> emptyList());
			}

		};
	}

	public static ICellCommand moveContent(AreaReference from, CellReference to) {
		throw new UnsupportedOperationException();
	}

	/**
	 * this commands leaves the cell unchanged - used as return value in {@link ISheet#sendCommand(ICellCommand)} when a
	 * cell commands is called and no change is done on the cell
	 * 
	 * @param ref
	 * @return
	 */
	public static ICellCommand doNothing(final AreaReference ref) {
		return new AbstractCellCommand(ref) {
			@Override
			public CellDataWithProperties apply(CellData data) {
				return new CellDataWithProperties(data);
			}

		};
	}

	// COPY content
	// Formula formula = null;
	// IGenericValue value = null;
	// if (fromCell != null) {
	// // this is for relative formula
	// if (fromCell.getFormula() != null) {
	// try {
	// formula = getSheet().getFormulaParser().transformFormula(fromCell.getFormula(),
	// ReferenceTransformers.shiftCell(fromCell.getReference(), getReference()));
	// } catch (Exception e) {
	// value = new ErrorValue(ErrorValueType.VALUE);
	// }
	// } else {
	// value = fromCell.getValue();
	// }
	// }
}
