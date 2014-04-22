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
package org.netxilia.server.rest.html.datasource;

import java.util.List;

import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.storage.DataSourceConfiguration;

/**
 * The model used by the datasource edit page.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DataSourceConfigurationModel {
	private final DataSourceConfiguration configuration;
	private final List<WorkbookId> workbookIds;

	public DataSourceConfigurationModel(DataSourceConfiguration cfg, List<WorkbookId> workbookIds) {
		this.configuration = cfg;
		this.workbookIds = workbookIds;
	}

	public DataSourceConfiguration getConfiguration() {
		return configuration;
	}

	public List<WorkbookId> getWorkbookIds() {
		return workbookIds;
	}

}
