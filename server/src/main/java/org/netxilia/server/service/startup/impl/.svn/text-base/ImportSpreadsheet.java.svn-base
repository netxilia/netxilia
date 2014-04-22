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
package org.netxilia.server.service.startup.impl;

import java.net.URL;
import java.util.List;

import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impexp.IImportService;
import org.netxilia.api.impexp.ImportException;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;
import org.netxilia.api.storage.IDataSourceConfigurationService;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.User;
import org.netxilia.server.service.startup.IStartupService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * This class is used to import spreadsheets at startup after admin was created.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class ImportSpreadsheet implements IStartupService {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(ImportSpreadsheet.class);

	@Autowired
	private IDataSourceConfigurationService dataSourceConfigurationService;

	@Autowired
	private INetxiliaSystem workbookProcessor;

	private IImportService jsonImportService;

	private IImportService excelImportService;

	private List<ImportSpreadsheetInfo> jsonImports;

	private List<ImportSpreadsheetInfo> excelImports;

	private boolean createDemoOnly;

	public IDataSourceConfigurationService getDataSourceConfigurationService() {
		return dataSourceConfigurationService;
	}

	public void setDataSourceConfigurationService(IDataSourceConfigurationService dataSourceConfigurationService) {
		this.dataSourceConfigurationService = dataSourceConfigurationService;
	}

	public IImportService getJsonImportService() {
		return jsonImportService;
	}

	public void setJsonImportService(IImportService jsonImportService) {
		this.jsonImportService = jsonImportService;
	}

	public IImportService getExcelImportService() {
		return excelImportService;
	}

	public void setExcelImportService(IImportService excelImportService) {
		this.excelImportService = excelImportService;
	}

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	public List<ImportSpreadsheetInfo> getJsonImports() {
		return jsonImports;
	}

	public void setJsonImports(List<ImportSpreadsheetInfo> jsonImports) {
		this.jsonImports = jsonImports;
	}

	public List<ImportSpreadsheetInfo> getExcelImports() {
		return excelImports;
	}

	public void setExcelImports(List<ImportSpreadsheetInfo> excelImports) {
		this.excelImports = excelImports;
	}

	public boolean isCreateDemoOnly() {
		return createDemoOnly;
	}

	public void setCreateDemoOnly(boolean createDemoOnly) {
		this.createDemoOnly = createDemoOnly;
	}

	private void importSheet(IImportService importService, DataSourceConfiguration dataSource,
			ImportSpreadsheetInfo importInfo) throws StorageException, NotFoundException {
		WorkbookId wkid = new WorkbookId(importInfo.getWorkbook());
		try {
			dataSourceConfigurationService.loadByWorkbook(wkid);
		} catch (NotFoundException ex) {
			dataSourceConfigurationService.setConfigurationForWorkbook(wkid, dataSource.getId());
		}
		URL jsonUrl = Thread.currentThread().getContextClassLoader().getResource(importInfo.getFile());
		if (jsonUrl == null) {
			log.error("Could not find import file: " + importInfo.getFile());
			return;
		}
		try {
			AclPrivilegedMode.set();
			importService.importSheets(workbookProcessor, new WorkbookId(importInfo.getWorkbook()), jsonUrl, null);
		} catch (ImportException e) {
			log.error("Could not import from json url:" + jsonUrl + ":" + e, e);
		} finally {
			AclPrivilegedMode.clear();
		}
	}

	@Override
	public void startup(DataSourceConfiguration dataSource, User admin, boolean createDemo) throws StorageException {
		if (createDemoOnly && !createDemo) {
			return;
		}
		if (jsonImports != null) {
			for (ImportSpreadsheetInfo importInfo : jsonImports) {
				try {
					importSheet(jsonImportService, dataSource, importInfo);
				} catch (Exception e) {
					log.error("Could not import:" + importInfo.getFile() + ":" + e, e);
				}
			}
		}

		if (excelImports != null) {
			for (ImportSpreadsheetInfo importInfo : excelImports) {
				try {
					importSheet(excelImportService, dataSource, importInfo);
				} catch (Exception e) {
					log.error("Could not import:" + importInfo.getFile() + ":" + e, e);
				}
			}
		}
	}

}
