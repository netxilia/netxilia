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
package org.netxilia.api.impl.utils;

import java.util.EventObject;

public class BlockEvent<V> extends EventObject {

	private static final long serialVersionUID = 1L;

	public enum EventType {
		inserted, deleted
	}

	private final BlockMetadata<V> entry;
	private final EventType type;

	public BlockEvent(BlockMetadata<V> entry, EventType type) {
		super("nosource");
		this.entry = entry;
		this.type = type;
	}

	public BlockMetadata<V> getEntry() {
		return entry;
	}

	public EventType getType() {
		return type;
	}

	@Override
	public String toString() {
		return "BlockEvent [block=" + entry + ", type=" + type + "]";
	}

}
