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
package org.netxilia.spi.impl.structure;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.netxilia.api.impl.utils.BlockEvent;
import org.netxilia.api.impl.utils.ISparseMatrix;
import org.netxilia.api.impl.utils.ISparseMatrixListener;
import org.netxilia.api.impl.utils.InsertMode;
import org.netxilia.api.impl.utils.OrderedBlock;
import org.netxilia.api.impl.utils.OrderedBlockMatrix;

public class TestSparseMatrix {

	@Test
	public void testSimpleSetGet() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());

		matrix.set(2, 4, "test2");
		check = matrix.get(2, 4);
		Assert.assertEquals("test2", check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testNullSetGet() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());

		matrix.set(2, 4, null);
		check = matrix.get(2, 4);
		Assert.assertNull(check);
		Assert.assertEquals(0, matrix.getBlockCount());
	}

	@Test
	public void testSimpleMerge() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());

		matrix.set(2, 5, "test1");
		check = matrix.get(2, 5);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testMerge() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(2, 6, "test1");
		matrix.set(2, 5, "test1");

		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 6);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testMergeAndSplit() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(2, 6, "test1");
		matrix.set(2, 5, "test1");

		Assert.assertEquals(1, matrix.getBlockCount());

		matrix.set(2, 5, "test2");
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertEquals("test2", check);
		check = matrix.get(2, 6);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(3, matrix.getBlockCount());
	}

	@Test
	public void testSetBlock() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, 2, 6, "test1");

		Assert.assertEquals(1, matrix.getBlockCount());
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 6);
		Assert.assertEquals("test1", check);
	}

	@Test
	public void testInsertRowInsideGrow() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");

		// add inside
		matrix.insertRow(3, InsertMode.grow);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(3, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(4, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testInsertRowInsideSplit() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");

		// add inside
		matrix.insertRow(3, InsertMode.split);
		System.out.println(matrix);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(3, 4);
		Assert.assertNull(check);
		check = matrix.get(4, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(2, matrix.getBlockCount());
	}

	@Test
	public void testInsertRowBeforeGrow() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");

		// add inside
		matrix.insertRow(1, InsertMode.grow);
		String check = matrix.get(2, 4);
		Assert.assertNull(check);
		check = matrix.get(3, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(4, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());

	}

	@Test
	public void testInsertRowFirstSplit() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");

		// add inside
		matrix.insertRow(2, InsertMode.split);
		String check = matrix.get(2, 4);
		Assert.assertNull(check);
		check = matrix.get(3, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(4, 4);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());

	}

	@Test
	public void testInsertRowAfter() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");

		// add inside
		matrix.insertRow(4, InsertMode.grow);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(3, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(4, 4);
		Assert.assertNull(check);
		Assert.assertEquals(1, matrix.getBlockCount());

	}

	@Test
	public void testDeleteRowInside() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(3, 4, "test1");
		matrix.set(4, 4, "test1");

		// add inside
		matrix.deleteRow(3);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(3, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(4, 4);
		Assert.assertNull(check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testInsertColumnInsideGrow() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(2, 5, "test1");

		// add inside
		matrix.insertColumn(5, InsertMode.grow);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 6);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	@Test
	public void testInsertColumnInsideSplit() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(2, 5, "test1");

		// add inside
		matrix.insertColumn(5, InsertMode.split);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertNull(check);
		check = matrix.get(2, 6);
		Assert.assertEquals("test1", check);
		Assert.assertEquals(2, matrix.getBlockCount());
	}

	@Test
	public void testDeleteColumnInside() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 4, "test1");
		matrix.set(2, 5, "test1");
		matrix.set(2, 6, "test1");

		// add inside
		matrix.deleteColumn(5);
		String check = matrix.get(2, 4);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 5);
		Assert.assertEquals("test1", check);
		check = matrix.get(2, 6);
		Assert.assertNull(check);
		Assert.assertEquals(1, matrix.getBlockCount());
	}

	private class ModificationTracker implements ISparseMatrixListener<String> {
		private final List<BlockEvent<String>> modifs = new ArrayList<BlockEvent<String>>();

		@Override
		public void onDeletedBlock(BlockEvent<String> event) {
			modifs.add(event);
		}

		@Override
		public void onInsertedBlock(BlockEvent<String> event) {
			modifs.add(event);
		}

		public List<BlockEvent<String>> getModifs() {
			return modifs;
		}

	}

	@Test
	public void testModifications() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		ModificationTracker modifications = new ModificationTracker();
		matrix.addEntryListener(modifications);
		matrix.set(2, 4, "test1");
		matrix.set(2, 6, "test1");
		matrix.set(2, 5, "test1");

		Assert.assertEquals(5, modifications.getModifs().size());

		// inserted
		Assert.assertEquals(new OrderedBlock(2, 4, 2, 4), modifications.getModifs().get(0).getEntry().getBlock());
		Assert.assertEquals(BlockEvent.EventType.inserted, modifications.getModifs().get(0).getType());

		// inserted
		Assert.assertEquals(new OrderedBlock(2, 6, 2, 6), modifications.getModifs().get(1).getEntry().getBlock());
		Assert.assertEquals(BlockEvent.EventType.inserted, modifications.getModifs().get(1).getType());

		// merged => 2 deletes and 1 insert
		// deleted
		Assert.assertEquals(new OrderedBlock(2, 4, 2, 4), modifications.getModifs().get(2).getEntry().getBlock());
		Assert.assertEquals(BlockEvent.EventType.deleted, modifications.getModifs().get(2).getType());

		// deleted
		Assert.assertEquals(new OrderedBlock(2, 6, 2, 6), modifications.getModifs().get(3).getEntry().getBlock());
		Assert.assertEquals(BlockEvent.EventType.deleted, modifications.getModifs().get(3).getType());

		// inserted
		Assert.assertEquals(new OrderedBlock(2, 4, 2, 6), modifications.getModifs().get(4).getEntry().getBlock());
		Assert.assertEquals(BlockEvent.EventType.inserted, modifications.getModifs().get(4).getType());

	}

	@Test
	public void testBugOrder() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(2, 1, "test1");
		matrix.set(6, 0, 7, 2, "test2");
		Assert.assertEquals("test2", matrix.get(7, 0));

		// change order of insertion
		matrix = new OrderedBlockMatrix<String>();
		matrix.set(6, 0, 7, 2, "test2");
		matrix.set(2, 1, "test1");
		Assert.assertEquals("test2", matrix.get(7, 0));

	}

	@Test
	public void testBugGetOrder2() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(1, 1, "test1");
		matrix.set(0, 0, 3, 0, "test2");
		Assert.assertEquals("test2", matrix.get(2, 0));

		// change order of insertion
		matrix = new OrderedBlockMatrix<String>();
		matrix.set(0, 0, 3, 0, "test2");
		matrix.set(1, 1, "test1");
		Assert.assertEquals("test2", matrix.get(2, 0));

	}

	@Test
	public void testPutGetOrder2() {
		ISparseMatrix<String> matrix = new OrderedBlockMatrix<String>();
		matrix.set(1, 1, "test1");
		matrix.set(0, 0, 3, 0, "test2");
		matrix.set(2, 0, "test3");
		Assert.assertEquals("test3", matrix.get(2, 0));

		// change order of insertion
		matrix = new OrderedBlockMatrix<String>();
		matrix.set(0, 0, 3, 0, "test2");
		matrix.set(1, 1, "test1");
		matrix.set(2, 0, "test3");
		Assert.assertEquals("test3", matrix.get(2, 0));

	}
}
