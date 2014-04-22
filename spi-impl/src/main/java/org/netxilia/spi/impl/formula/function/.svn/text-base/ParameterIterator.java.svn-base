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
package org.netxilia.spi.impl.formula.function;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.netxilia.api.formula.IFormulaContext;
import org.netxilia.api.model.CellData;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.value.GenericValueUtils;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.ReferenceValue;
import org.netxilia.api.value.StringValue;
import org.netxilia.spi.impl.formula.parser.ASTBaseNode;

/**
 * This class is an iterator through all the parameters that can be sent to a function. If a parameter is a
 * ReferenceValue than it iterates through all the cells of the given area. It also converts data to the desired type.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 * @param <E>
 */
public class ParameterIterator<E> implements Iterator<E> {
	private static final IGenericValue EMPTY_VALUE = new StringValue("");
	private final Class<E> elementType;
	private final ASTBaseNode[] nodes;
	private final IFormulaContext context;
	private Iterator<CellData> cellIterator;
	private int currentNode = 0;
	private E nextValue = null;
	private Boolean hasNextValue = null;

	public ParameterIterator(Class<E> elementType, IFormulaContext context, ASTBaseNode[] nodes, int currentNode) {
		this.elementType = elementType;
		this.nodes = nodes;
		this.context = context;
		this.currentNode = currentNode;
	}

	@Override
	public boolean hasNext() {
		if (hasNextValue == null) {
			moveToNext();
		}
		return hasNextValue;
	}

	@Override
	public E next() {
		if (hasNextValue == null) {
			moveToNext();
		}
		if (!hasNextValue) {
			throw new NoSuchElementException();
		}
		hasNextValue = null;
		return nextValue;
	}

	protected void moveToNext() {
		if (cellIterator != null && cellIterator.hasNext()) {
			CellData cell = cellIterator.next();
			nextValue = GenericValueUtils.convert(cell != null && cell.getValue() != null ? cell.getValue()
					: EMPTY_VALUE, elementType);
			hasNextValue = true;
			return;
		}

		if (currentNode < nodes.length) {
			IGenericValue next = nodes[currentNode++].eval(context);
			if (next instanceof ReferenceValue) {
				ReferenceValue refValue = (ReferenceValue) next;
				AreaReference ref = refValue.getReference();
				cellIterator = refValue.getContext().getCellIterator(ref);
				moveToNext();
				return;
			}

			cellIterator = null;
			nextValue = GenericValueUtils.convert(next, elementType);
			hasNextValue = true;
			return;
		}

		hasNextValue = false;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
