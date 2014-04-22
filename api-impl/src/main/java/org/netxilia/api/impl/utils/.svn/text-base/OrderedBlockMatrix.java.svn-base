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
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.netxilia.api.impl.event.DispatchableEvent;
import org.netxilia.api.impl.event.DispatchableEventSupport;
import org.netxilia.api.impl.utils.BlockEvent.EventType;
import org.netxilia.api.impl.utils.intervals.Interval;
import org.netxilia.api.impl.utils.intervals.IntervalTree;

import com.google.common.util.concurrent.MoreExecutors;

public class OrderedBlockMatrix<V> implements ISparseMatrix<V> {
	private int blockCount = 0;

	private final IntervalTree<Map<OrderedBlock, BlockMetadata<V>>> columns;
	// private final IntervalTree<Collection<BlockMetadata<V>>> columns;
	private final OrderedBlockEventSupport eventSupport;

	public OrderedBlockMatrix() {
		this.columns = new IntervalTree<Map<OrderedBlock, BlockMetadata<V>>>();
		// this.columns = new IntervalTree<Collection<BlockMetadata<V>>>();
		eventSupport = new OrderedBlockEventSupport();
	}

	/**
	 * Set the value for the cell at the given position. <br>
	 * <br>
	 * 
	 * In order to reduce the number of different value instances, we try to aggressively merge the given value with
	 * equal neighboring value. Therefore, it is possible that the resulting value is a different instance than the
	 * given one.
	 * 
	 */
	@Override
	public void set(int firstRow, int firstCol, int lastRow, int lastCol, V value) {
		for (int r = firstRow; r <= lastRow; ++r) {
			for (int c = firstCol; c <= lastCol; ++c) {
				set(r, c, value);
			}
		}
	}

	/**
	 * Set the value for the cell at the given position. <br>
	 * <br>
	 * 
	 * In order to reduce the number of different value instances, we try to aggressively merge the given value with
	 * equal neighboring value. Therefore, it is possible that the resulting value is a different instance than the
	 * given one.
	 * 
	 * 
	 */
	@Override
	public void set(int row, int col, V value) {
		set(new OrderedBlock(row, col), value);
	}

	private void set(OrderedBlock pos, V newMD) {
		// first, check if we have something on this position
		BlockMetadata<V> blockMD = getBlockMetadata(pos);
		if (blockMD == null) {
			// we don't. Insert it, attempting to merge with its neighbors
			attemptMerge(pos, newMD);
			return;
		}
		// we have. First check if we have the same value
		V oldMD = blockMD.getValue();
		if (oldMD.equals(newMD)) {
			return;
		}
		// it doesn't. We have to remove and split the current block
		deleteBlockMetadata(blockMD.getBlock());

		// insert the generated parts, attempting to merge them with their neighbors
		OrderedBlock block = blockMD.getBlock();
		if (!pos.equals(block)) {
			for (OrderedBlock part : block.split(pos)) {
				attemptMerge(part, oldMD);
			}
		}

		// finally, insert the position, attempting to merge it with its neighbors
		attemptMerge(pos, newMD);
	}

	/**
	 * This inserts the given value for the specified cells block attempting to merge it with block's neighbors. The
	 * merging is performed recursively, i.e. we try to merge the result of a successful merge.
	 * 
	 * @return the final value block resulted after insertion.
	 */
	private BlockMetadata<V> attemptMerge(OrderedBlock block, V md) {
		// no need to insert this value in the tree
		if (md == null) {
			return null;
		}
		BlockMetadata<V> newBMD = null;

		// for each of the neighboring cells of this block
		for (OrderedBlock pos : block.neighborCells()) {
			BlockMetadata<V> neighBMD = getBlockMetadata(pos);

			// if we have a real neighboring block
			if (neighBMD != null) {
				OrderedBlock neighBlock = neighBMD.getBlock();

				// check if we can merge with it, i.e. has the same side size and the same value
				if (neighBlock.canMerge(block) && neighBMD.getValue().equals(md)) {

					// it does! we remove the previous block and create the merged block
					deleteBlockMetadata(neighBlock);

					OrderedBlock newBlock = neighBlock.merge(block);
					V newMD = neighBMD.getValue(); // keep existing value

					// attempt to merge further the resulting block
					newBMD = attemptMerge(newBlock, newMD);

					// stop the merging here. All other sides were already checked recursively
					return newBMD;
				}
			}
		}
		// didn't merge with any neighbor. Create and record the new block
		newBMD = new BlockMetadata<V>(block, md);
		insertBlockMetadata(newBMD);

		return newBMD;
	}

	/** Record the given value block. Tracks changes for persistence. */
	private void insertBlockMetadata(BlockMetadata<V> newBMD) {
		restoreBlockMetadata(newBMD);
		eventSupport.fireInsertedEvent(newBMD);
	}

	private void addBlock(BlockMetadata<V> md) {
		Interval interval = new Interval(md.getBlock().getFirstCol(), md.getBlock().getLastCol());
		Map<OrderedBlock, BlockMetadata<V>> blocks = columns.get(interval);
		if (blocks == null) {
			blocks = new HashMap<OrderedBlock, BlockMetadata<V>>();
			columns.put(interval, blocks);
		}
		blocks.put(md.getBlock(), md);
		columns.recalculate();
	}

	private BlockMetadata<V> deleteBlock(OrderedBlock block) {
		Interval interval = new Interval(block.getFirstCol(), block.getLastCol());
		Map<OrderedBlock, BlockMetadata<V>> blocks = columns.get(interval);
		if (blocks == null) {
			return null;
		}
		BlockMetadata<V> deleted = blocks.remove(block);
		if (blocks.isEmpty()) {
			columns.remove(interval);
		}
		if (deleted != null) {
			columns.recalculate();
		}
		return deleted;
	}

	/**
	 * Restore from the storage system a block of value, i.e. load it in memory.
	 * 
	 * @throws IllegalArgumentException
	 *             if the given block overlaps with an existing one.
	 * 
	 */
	public void restoreBlockMetadata(BlockMetadata<V> newBMD) {
		addBlock(newBMD);
		// addBlock(columns, new Interval(newBMD.getBlock().getFirstCol(), newBMD.getBlock().getLastCol()), newBMD);
		blockCount++;
	}

	/** Delete the value for the block at the given position. Tracks changes for persistence. */
	private void deleteBlockMetadata(OrderedBlock block) {
		// take it first time it is found in an interval
		BlockMetadata<V> deletedBMD = deleteBlock(block);

		if (deletedBMD == null) {
			return;
		}

		blockCount--;
		eventSupport.fireDeletedEvent(deletedBMD);
	}

	private BlockMetadata<V> getBlockMetadata(OrderedBlock block) {
		Interval interval = new Interval(block.getFirstCol(), block.getLastCol());
		List<Map.Entry<Interval, Map<OrderedBlock, BlockMetadata<V>>>> entries = columns.searchEntries(interval);
		// for each interval look into the corresponding blocks to find the one containing the provided block.
		// TODO could use an interval tree for column search also
		for (Map.Entry<Interval, Map<OrderedBlock, BlockMetadata<V>>> entry : entries) {
			for (BlockMetadata<V> storedBlock : entry.getValue().values()) {
				if (storedBlock.getBlock().contains(block)) {
					return storedBlock;
				}
			}
		}
		return null;
	}

	/**
	 * Enlarge vertically the blocks that overlap the given row and shift down by one position the ones below.
	 * 
	 * @return the list of affected entries
	 */
	public List<? extends ISparseMatrixEntry<V>> insertRow(final int row, final InsertMode insertMode) {
		return shiftBlocks(row, 0, new IBlockTransformer<V>() {
			@Override
			public List<BlockMetadata<V>> apply(BlockMetadata<V> orig) {
				return orig.withInsertedRow(row, insertMode);
			}
		});
	}

	/**
	 * Shrink vertically the blocks that overlap the given row, delete the ones that don't exist anymore and shift up by
	 * one position the ones below.
	 * 
	 * @return the list of affected entries
	 */
	public List<? extends ISparseMatrixEntry<V>> deleteRow(final int row) {
		return shiftBlocks(row, 0, new IBlockTransformer<V>() {
			@Override
			public List<BlockMetadata<V>> apply(BlockMetadata<V> orig) {
				return orig.withDeletedRow(row);
			}
		});
	}

	/**
	 * Enlarge horizontally the blocks that overlap the given column and shift right by one position the ones to the
	 * right.
	 * 
	 * @return the list of affected entries
	 */
	public List<? extends ISparseMatrixEntry<V>> insertColumn(final int col, final InsertMode insertMode) {
		return shiftBlocks(0, col, new IBlockTransformer<V>() {
			@Override
			public List<BlockMetadata<V>> apply(BlockMetadata<V> orig) {
				return orig.withInsertedCol(col, insertMode);
			}
		});
	}

	/**
	 * Shrink horizontally the blocks that overlap the given column, delete the ones that don't exist anymore and shift
	 * left by one position the ones to the right.
	 * 
	 * @return the list of affected entries
	 */
	public List<? extends ISparseMatrixEntry<V>> deleteColumn(final int col) {
		return shiftBlocks(0, col, new IBlockTransformer<V>() {
			@Override
			public List<BlockMetadata<V>> apply(BlockMetadata<V> orig) {
				return orig.withDeletedCol(col);
			}
		});
	}

	/**
	 * Defines a transformation over a value block, to be used mainly for shifting blocks on rows and columns insertions
	 * and deletions.
	 */
	private interface IBlockTransformer<VV> {
		public List<BlockMetadata<VV>> apply(BlockMetadata<VV> orig);
	}

	/** Helper for insert/delete Row/Col methods */
	private List<? extends ISparseMatrixEntry<V>> shiftBlocks(int minRow, int minCol, IBlockTransformer<V> transformer) {
		// remove the affected blocks for shifting
		List<BlockMetadata<V>> shiftedBMList = new LinkedList<BlockMetadata<V>>();
		List<BlockMetadata<V>> deletedBMList = new LinkedList<BlockMetadata<V>>();
		for (Map<OrderedBlock, BlockMetadata<V>> blocks : columns.values()) {
			for (BlockMetadata<V> bm : blocks.values()) {
				if (bm.getBlock().getLastRow() < minRow || bm.getBlock().getLastCol() < minCol) {
					continue;
				}
				deletedBMList.add(bm);

				List<BlockMetadata<V>> newBlocks = transformer.apply(bm);

				// TODO completely deleted cells (newBlocks == null) should somehow be returned - better return a pair
				// old,
				// new
				if (newBlocks != null) {
					shiftedBMList.addAll(newBlocks);
				}
			}
		}
		// first delete the modified blocks
		for (BlockMetadata<V> shBM : deletedBMList) {
			deleteBlockMetadata(shBM.getBlock());
		}

		// re-insert the shifted blocks
		for (BlockMetadata<V> shBM : shiftedBMList) {
			insertBlockMetadata(shBM);
		}
		return shiftedBMList;
	}

	/** Get the value for the cell at the given position. */
	@Override
	public V get(int row, int col) {
		// OrderedBlock searchedBlock = new OrderedBlock(row, col);
		// for (Map.Entry<OrderedBlock, BlockMetadata<V>> entry : mdBlocks.entrySet()) {
		// if (entry.getKey().contains(searchedBlock)) {
		// return entry.getValue().getValue();
		// }
		// }
		// return null;
		// TODO the algorithm should be fixed because it does not work in all the cases
		BlockMetadata<V> md = getBlockMetadata(new OrderedBlock(row, col));
		return md != null ? md.getValue() : null;
	}

	@Override
	public int getBlockCount() {
		return blockCount;
	}

	@Override
	public Collection<? extends ISparseMatrixEntry<V>> entries() {
		List<BlockMetadata<V>> entries = new ArrayList<BlockMetadata<V>>(getBlockCount());
		for (Map<OrderedBlock, BlockMetadata<V>> blocks : columns.values()) {
			entries.addAll(blocks.values());
		}
		return entries;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("{#").append(getBlockCount()).append("\n");
		for (ISparseMatrixEntry<V> entry : entries()) {
			s.append(entry).append("\n");
		}
		s.append("}");
		return s.toString();
	}

	public String treeToString() {
		return "ROWS:" + columns;
	}

	private class OrderedBlockEventSupport extends DispatchableEventSupport<ISparseMatrixListener<V>> {
		public OrderedBlockEventSupport() {
			super(MoreExecutors.sameThreadExecutor());
		}

		public void fireDeletedEvent(BlockMetadata<V> deletedBMD) {
			if (super.getListeners().hasNext()) {
				fireEvent(new BlockEvent<V>(deletedBMD, EventType.deleted));
			}

		}

		public void fireInsertedEvent(BlockMetadata<V> newBMD) {
			if (super.getListeners().hasNext()) {
				fireEvent(new BlockEvent<V>(newBMD, EventType.inserted));
			}
		}

		public void fireEvent(BlockEvent<V> ev) {
			fireEvent(new DispatchableEvent<ISparseMatrixListener<V>, BlockEvent<V>>(ev) {
				@Override
				public void dispatch(ISparseMatrixListener<V> target, BlockEvent<V> event) {
					if (event.getType() == EventType.deleted) {
						target.onDeletedBlock(event);
					} else if (event.getType() == EventType.inserted) {
						target.onInsertedBlock(event);
					}
				}
			});
		}

	}

	@Override
	public void addEntryListener(ISparseMatrixListener<V> listener) {
		eventSupport.addListener(listener);

	}

	@Override
	public void removeEntryListener(ISparseMatrixListener<V> listener) {
		eventSupport.removeListener(listener);

	}

	public static void main(String[] args) {
		OrderedBlockMatrix<Integer> matrix = new OrderedBlockMatrix<Integer>();
		long t1 = System.currentTimeMillis();

		for (int r = 0; r < 100; ++r) {
			for (int c = 0; c < 100; ++c) {
				matrix.set(r, c, c % 2);
			}
		}
		// for (int r = 0; r < 4; ++r) {
		// for (int c = 0; c < 4; ++c) {
		// matrix.set(r, c, c % 2);
		// System.out.println(matrix);
		// System.out.println(matrix.columns);
		// System.out.println("---------------");
		// }
		// }
		long t2 = System.currentTimeMillis();
		System.out.println("time:" + (t2 - t1));
		System.out.println("rows:" + matrix.columns.size());
		System.out.println("blocks:" + matrix.getBlockCount());
		System.out.println(matrix);
	}
}
