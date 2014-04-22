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
package org.netxilia.api.reference;

import junit.framework.Assert;

import org.junit.Test;

public class TestCellReference {

	@Test
	public void testGetStringReference() {
		CellReference cr = new CellReference(null, 5, 3);
		Assert.assertEquals("D6", cr.formatAsString());

		cr = new CellReference("SheetName", 5, 3);
		Assert.assertEquals("SheetName!D6", cr.formatAsString());

		cr = new CellReference(null, 5, 3, false, true);
		Assert.assertEquals("$D6", cr.formatAsString());
		cr = new CellReference(null, 5, 3, true, true);
		Assert.assertEquals("$D$6", cr.formatAsString());
	}

	@Test
	public void testSetStringReference() {
		CellReference cr = new CellReference("SheetName!AB$7");
		Assert.assertEquals("SheetName", cr.getSheetName());
		Assert.assertEquals(27, cr.getColumnIndex());
		Assert.assertFalse(cr.isAbsoluteColumn());
		Assert.assertEquals(6, cr.getRowIndex());
		Assert.assertTrue(cr.isAbsoluteRow());
	}

	@Test
	public void testModify() {
		CellReference cr = new CellReference("FX234");
		Assert.assertEquals(179, cr.getColumnIndex());
		Assert.assertEquals(233, cr.getRowIndex());
	}

	@Test
	public void testInfiniteValues() {
		CellReference cr = new CellReference("3");
		Assert.assertTrue(cr.isInfiniteColumn());
		Assert.assertEquals(2, cr.getRowIndex());
		Assert.assertEquals("3", cr.formatAsString());

		cr = new CellReference("A");
		Assert.assertTrue(cr.isInfiniteRow());
		Assert.assertEquals(0, cr.getColumnIndex());
		Assert.assertEquals("A", cr.formatAsString());
	}

	@Test
	public void testInfiniteAreas() {
		AreaReference ar = new AreaReference("3:4");
		Assert.assertTrue(ar.getTopLeft().isInfiniteColumn());
		Assert.assertTrue(ar.isFullRow());
		Assert.assertTrue(ar.getBottomRight().isInfiniteColumn());
		Assert.assertEquals("3:4", ar.formatAsString());

		ar = new AreaReference("A:D");
		Assert.assertTrue(ar.getTopLeft().isInfiniteRow());
		Assert.assertTrue(ar.isFullColumn());
		Assert.assertTrue(ar.getBottomRight().isInfiniteRow());
		Assert.assertEquals("A:D", ar.formatAsString());

		try {
			ar = new AreaReference("A:D2");
			Assert.fail("Exception not thrown");
		} catch (IllegalArgumentException ex) {
			// ok
		}

	}

	@Test
	public void testRCReference() {
		RCCellReference cr = new RCCellReference(new CellReference("B3"), new CellReference("C2"));
		Assert.assertEquals(-1, cr.getRowIndex());
		Assert.assertEquals(1, cr.getColumnIndex());
		Assert.assertEquals("R[-1]C[1]", cr.toString());

		cr = new RCCellReference(new CellReference("B3"), new CellReference("C$2"));
		Assert.assertEquals(1, cr.getRowIndex());
		Assert.assertEquals(1, cr.getColumnIndex());
		Assert.assertEquals("R1C[1]", cr.toString());

		cr = new RCCellReference(new CellReference("B3"), new CellReference("C"));
		Assert.assertTrue(cr.isInfiniteRow());
		Assert.assertEquals(1, cr.getColumnIndex());
		Assert.assertEquals("C[1]", cr.toString());

		cr = new RCCellReference(new CellReference("B3"), new CellReference("2"));
		Assert.assertEquals(-1, cr.getRowIndex());
		Assert.assertTrue(cr.isInfiniteColumn());
		Assert.assertEquals("R[-1]", cr.toString());

	}

	@Test
	public void testComplexSheetNameReference1() {
		CellReference cr = new CellReference("'Sheet name - é'!A7");
		Assert.assertEquals("Sheet name - é", cr.getSheetName());
		Assert.assertEquals(0, cr.getColumnIndex());
		Assert.assertEquals(6, cr.getRowIndex());
	}

	@Test
	public void testComplexSheetNameReference2() {
		CellReference cr = new CellReference("Sheet name - é", 6, 0);
		Assert.assertEquals("'Sheet name - é'!A7", cr.toString());
	}

}
