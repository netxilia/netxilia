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
package org.netxilia.spi.impl.formula;

import java.util.Iterator;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.CellReference;
import org.springframework.util.Assert;

public class CellIterator implements Iterator<CellData> {
	private final ISheet sheet;
	private final Iterator<CellReference> referenceIterator;

	public CellIterator(ISheet sheet, Iterator<CellReference> referenceIterator) {
		Assert.notNull(sheet);
		Assert.notNull(referenceIterator);
		this.sheet = sheet;
		this.referenceIterator = referenceIterator;
	}

	@Override
	public boolean hasNext() {
		return referenceIterator.hasNext();
	}

	@Override
	public CellData next() {
		try {
			return sheet.receiveCell(referenceIterator.next()).getNonBlocking();
		} catch (NetxiliaResourceException e) {
			throw e;
		} catch (NetxiliaBusinessException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

}
