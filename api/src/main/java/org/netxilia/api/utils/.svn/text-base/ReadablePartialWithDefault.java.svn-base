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
package org.netxilia.api.utils;

import org.joda.time.Chronology;
import org.joda.time.DateTimeField;
import org.joda.time.LocalDateTime;
import org.joda.time.ReadablePartial;
import org.joda.time.base.AbstractPartial;

public class ReadablePartialWithDefault extends AbstractPartial {
	private final ReadablePartial partial;
	private final LocalDateTime fullDateTime;

	public ReadablePartialWithDefault(ReadablePartial partial, LocalDateTime fullDateTime) {
		this.partial = partial;
		this.fullDateTime = fullDateTime;
	}

	@Override
	public Chronology getChronology() {
		return fullDateTime.getChronology();
	}

	@Override
	public int getValue(int index) {
		DateTimeField field = fullDateTime.getField(index);
		if (partial.isSupported(field.getType())) {
			return partial.get(field.getType());
		}
		return fullDateTime.get(field.getType());
	}

	@Override
	public int size() {
		return fullDateTime.size();
	}

	@Override
	protected DateTimeField getField(int index, Chronology chrono) {
		return fullDateTime.getField(index);
	}
}
