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
package org.netxilia.spi.impl.storage.db;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.netxilia.api.impl.utils.BlockEvent;
import org.netxilia.api.impl.utils.BlockMetadata;
import org.netxilia.api.impl.utils.OrderedBlock;
import org.netxilia.api.impl.utils.OrderedBlockMatrix;

/**
 * This class listens for events in an {@link OrderedBlockMatrix} and collects the blocks to be added and the blocks to
 * be inserted. The operations must be dumped one the group of events finished.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SparseMatrixCollectionListener implements ISparseMatrixCollectionListener {
	private final static Logger log = Logger.getLogger(SparseMatrixCollectionListener.class);

	private final SheetDbSession session;
	private final DbSheetStorageInfo storage;
	private final SparseMatrixMapper mapper;

	private final Map<String, BlocksByProperty> blocks = new HashMap<String, BlocksByProperty>();

	public SparseMatrixCollectionListener(SheetDbSession session, DbSheetStorageInfo storage, SparseMatrixMapper mapper) {
		this.session = session;
		this.storage = storage;
		this.mapper = mapper;
	}

	private BlocksByProperty getBlocks(String property) {
		BlocksByProperty b = blocks.get(property);
		if (b == null) {
			b = new BlocksByProperty();
			blocks.put(property, b);
		}
		return b;
	}

	@Override
	public void onInsertedBlock(String property, BlockEvent<String> event) {
		getBlocks(property).insertBlock(event.getEntry());
	}

	@Override
	public void onDeletedBlock(String property, BlockEvent<String> event) {
		getBlocks(property).deleteBlock(event.getEntry().getBlock());
	}

	@Override
	public void save() {
		for (Map.Entry<String, BlocksByProperty> entry : blocks.entrySet()) {
			if (log.isDebugEnabled()) {
				log.debug("prop [" + entry.getKey() + "]: remove " + entry.getValue().deleted.size() + " insert "
						+ entry.getValue().inserted.size());
			}
			for (OrderedBlock deleted : entry.getValue().deleted) {
				mapper.delete(session, storage, entry.getKey(), deleted);
			}
			for (BlockMetadata<String> modified : entry.getValue().modified.values()) {
				mapper.modify(session, storage, entry.getKey(), modified);
			}
			for (BlockMetadata<String> inserted : entry.getValue().inserted.values()) {
				mapper.insert(session, storage, entry.getKey(), inserted);
			}
		}

	}

	private class BlocksByProperty {
		final Map<OrderedBlock, BlockMetadata<String>> inserted = new LinkedHashMap<OrderedBlock, BlockMetadata<String>>();
		final Map<OrderedBlock, BlockMetadata<String>> modified = new LinkedHashMap<OrderedBlock, BlockMetadata<String>>();
		final Set<OrderedBlock> deleted = new HashSet<OrderedBlock>();

		public void deleteBlock(OrderedBlock block) {
			if (inserted.remove(block) == null) {
				// this is not a block previously inserted
				deleted.add(block);
			}
		}

		public void insertBlock(BlockMetadata<String> md) {
			if (deleted.remove(md.getBlock())) {
				// the block was previously deleted and then inserted - this is rather a modification
				modified.put(md.getBlock(), md);
			} else {
				inserted.put(md.getBlock(), md);
			}
		}

	}

}
