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
package org.netxilia.api.structure;

import junit.framework.Assert;

import org.junit.Test;
import org.netxilia.api.model.SortSpecifier;

public class TestSort {
	@Test
	public void testSortSpecifier1() {
		SortSpecifier sp = new SortSpecifier("+A-B+C");
		Assert.assertEquals(3, sp.getColumns().size());

		Assert.assertEquals("A", sp.getColumns().get(0).getName());
		Assert.assertEquals(SortSpecifier.SortOrder.ascending, sp.getColumns().get(0).getOrder());

		Assert.assertEquals("B", sp.getColumns().get(1).getName());
		Assert.assertEquals(SortSpecifier.SortOrder.descending, sp.getColumns().get(1).getOrder());

		Assert.assertEquals("C", sp.getColumns().get(2).getName());
		Assert.assertEquals(SortSpecifier.SortOrder.ascending, sp.getColumns().get(2).getOrder());

		Assert.assertEquals("+A-B+C", sp.toString());
	}

	@Test
	public void testSortSpecifier2() {
		SortSpecifier sp = new SortSpecifier("A-B");
		Assert.assertEquals(2, sp.getColumns().size());

		Assert.assertEquals("A", sp.getColumns().get(0).getName());
		Assert.assertEquals(SortSpecifier.SortOrder.ascending, sp.getColumns().get(0).getOrder());

		Assert.assertEquals("B", sp.getColumns().get(1).getName());
		Assert.assertEquals(SortSpecifier.SortOrder.descending, sp.getColumns().get(1).getOrder());

		Assert.assertEquals("+A-B", sp.toString());
	}
}
