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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Associates the same metadata properties for all cells in a given block.
 * 
 * @author catac
 */
public class BlockMetadata<V> implements ISparseMatrixEntry<V> {
	private final OrderedBlock block;
	private final V value;

	public BlockMetadata(OrderedBlock block, V metadata) {
		this.block = block;
		this.value = metadata;
	}

	public OrderedBlock getBlock() {
		return block;
	}

	public V getValue() {
		return value;
	}

	/**
	 * Get the block metadata resulted after inserting a row on the given position
	 * 
	 * @param insertMode
	 * 
	 * @return the new block metadata or this, if row is inserted below the block.
	 */
	public List<BlockMetadata<V>> withInsertedRow(int onRow, InsertMode insertMode) {
		return withUpdatedBlock(block.withInsertedRow(onRow, insertMode));
	}

	/**
	 * Get the block metadata resulted after inserting a column on the given position
	 * 
	 * @param insertMode
	 * 
	 * @return the new block metadata or this, if column is inserted to the right of the block.
	 */
	public List<BlockMetadata<V>> withInsertedCol(int onCol, InsertMode insertMode) {
		return withUpdatedBlock(block.withInsertedCol(onCol, insertMode));
	}

	/**
	 * Get the block metadata resulted after deleting a row on the given position
	 * 
	 * @return the new block metadata, this (if row is deleted below the block) or null if the block doesn't exist
	 *         anymore after row deletion.
	 */
	public List<BlockMetadata<V>> withDeletedRow(int onRow) {
		return withUpdatedBlock(block.withDeletedRow(onRow));
	}

	/**
	 * Get the block metadata resulted after deleting a column on the given position
	 * 
	 * @return the new block metadata, this (if column is deleted to the right of the block) or null if the block
	 *         doesn't exist anymore after column deletion.
	 */
	public List<BlockMetadata<V>> withDeletedCol(int onCol) {
		return withUpdatedBlock(block.withDeletedCol(onCol));
	}

	/** Helper for the with_Inserted/Deleted_Row/Col methods */
	private List<BlockMetadata<V>> withUpdatedBlock(List<OrderedBlock> newBlocks) {
		if (newBlocks == null) {
			// block doesn't exist anymore
			return null;
		}
		if (newBlocks.size() == 0) {
			return Collections.emptyList();
		}
		if (newBlocks.size() == 0 && newBlocks.get(0) == block) {
			// block not affected
			return Collections.singletonList(this);
		}
		List<BlockMetadata<V>> metadatas = new ArrayList<BlockMetadata<V>>(newBlocks.size());
		for (OrderedBlock newBlock : newBlocks) {
			metadatas.add(new BlockMetadata<V>(newBlock, value));
		}
		return metadatas;
	}

	@Override
	public String toString() {
		return block + "=[" + value + "]";
	}

	@Override
	public int getFirstColumn() {
		return block.getFirstCol();
	}

	@Override
	public int getFirstRow() {
		return block.getFirstRow();
	}

	@Override
	public int getLastColumn() {
		return block.getLastCol();
	}

	@Override
	public int getLastRow() {
		return block.getLastRow();
	}
}
