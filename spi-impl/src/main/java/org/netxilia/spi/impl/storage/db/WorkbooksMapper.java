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

import java.sql.SQLException;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.model.WorkbookId;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Handles the DB-Mapping for Workbook.
 * 
 * @author acraciun
 */
public class WorkbooksMapper {
	@Autowired
	private SheetsMapper sheetsMapper;

	@Autowired
	private SparseMatrixMapper matrixMapper;

	public WorkbooksMapper() {
	}

	public SheetsMapper getSheetsMapper() {
		return sheetsMapper;
	}

	public void setSheetsMapper(SheetsMapper sheetsMapper) {
		this.sheetsMapper = sheetsMapper;
	}

	public SparseMatrixMapper getMatrixMapper() {
		return matrixMapper;
	}

	public void setMatrixMapper(SparseMatrixMapper matrixMapper) {
		this.matrixMapper = matrixMapper;
	}

	public void delete(NetxiliaSystemImpl workbookProcessor, WorkbookDbSession data, WorkbookId workbookId)
			throws SQLException, StorageException, NotFoundException {
		Workbook workbook = Workbook.newInstance(workbookProcessor, workbookId);
		// drop the properties table
		matrixMapper.deleteAll(data, workbook);
	}

}
