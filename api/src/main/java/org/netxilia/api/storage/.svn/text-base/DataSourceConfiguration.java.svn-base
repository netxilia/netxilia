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
package org.netxilia.api.storage;

/**
 * This class is a basic configuration for a DataSource used by the storage engine. The storage engine uses this
 * information to know where to store the sheet information.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class DataSourceConfiguration {
	private final DataSourceConfigurationId id;
	private final String name;
	private final String description;
	private final String driverClassName;
	private final String url;
	private final String username;
	private final String password;

	public DataSourceConfiguration(DataSourceConfigurationId id, String name, String description,
			String driverClassName, String url, String username, String password) {
		this.id = id;
		this.driverClassName = driverClassName;
		this.url = url;
		this.username = username;
		this.password = password;
		this.name = name;
		this.description = description;
	}

	public DataSourceConfigurationId getId() {
		return id;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public String getUrl() {
		return url;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "DataSourceConfiguration [description=" + description + ", driverClassName=" + driverClassName + ", id="
				+ id + ", name=" + name + ", password=" + password + ", url=" + url + ", username=" + username + "]";
	}

}
