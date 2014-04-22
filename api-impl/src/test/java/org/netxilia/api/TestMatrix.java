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
package org.netxilia.api;

import java.util.Iterator;

import junit.framework.Assert;

import org.junit.Test;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.utils.MatrixBuilder;

public class TestMatrix {

	@Test
	public void testSetGet() {
		Matrix<Integer> m = new Matrix<Integer>();

		m = new MatrixBuilder<Integer>(m, 0).set(1, 1, 100).build();
		Assert.assertEquals(100, m.get(1, 1).intValue());

		m = new MatrixBuilder<Integer>(m, 0).set(2, 2, 200).build();
		Assert.assertEquals(200, m.get(2, 2).intValue());
	}

	@Test
	public void testIterator() {
		Matrix<Integer> m = new Matrix<Integer>();

		m = new MatrixBuilder<Integer>(m, 0).set(0, 1, 100).build();
		m = new MatrixBuilder<Integer>(m, 0).set(1, 1, 200).build();
		Iterator<Integer> it = m.iterator();
		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(0, it.next().intValue());

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(100, it.next().intValue());

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(0, it.next().intValue());

		Assert.assertTrue(it.hasNext());
		Assert.assertEquals(200, it.next().intValue());

		Assert.assertFalse(it.hasNext());
	}

	@Test
	public void testIteratorEmpty() {
		Matrix<Integer> m = new Matrix<Integer>();

		Iterator<Integer> it = m.iterator();

		Assert.assertFalse(it.hasNext());
	}
}
