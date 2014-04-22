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
package org.netxilia.spi.impl.storage.cache;

import org.netxilia.api.model.SheetFullName;
import org.netxilia.spi.impl.storage.db.CellsMapper;
import org.netxilia.spi.impl.storage.db.ColumnsMapper;
import org.netxilia.spi.impl.storage.db.DbSheetStorageServiceImpl;
import org.netxilia.spi.impl.storage.db.DbWorkbookStorageServiceImpl;
import org.netxilia.spi.impl.storage.db.RowsMapper;
import org.netxilia.spi.impl.storage.db.SheetsMapper;
import org.netxilia.spi.impl.storage.db.SparseMatrixMapper;

public class CachedDbWorkbookStorageServiceImpl extends DbWorkbookStorageServiceImpl {

	@Override
	protected DbSheetStorageServiceImpl newSheetStorage(SheetFullName sheetName, SheetsMapper sheetsMapper,
			RowsMapper rowsMapper, ColumnsMapper columnsMapper, CellsMapper cellsMapper, SparseMatrixMapper matrixMapper) {
		return new CachedDbSheetStorageServiceImpl(this, sheetName, sheetsMapper, rowsMapper, columnsMapper,
				cellsMapper, matrixMapper);
	}
}
