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
package org.netxilia.api.user;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.WorkbookId;

/**
 * This service offers acces to the users of the netxilia installation. It takes the user information for the sheet
 * "users" from the workbook "system". This sheet has to have at least four columns: the user's login, password and
 * email and roles (admin or user). There should be aliases for these columns called respectevely login, password, email
 * and roles.
 * 
 * By default only the administrator has access to this sheet. But it can grant access to it to any othe user or group.
 * The sheet can be edited like anyother sheet. The admin's account is itself in the sheet. If there is no more admin
 * accounts (because of wrong manipulation) the system will allow anyone to enter and create a new admin.
 * 
 * TODO should rather have a master password or to protect the sheet for this manipulation !?
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IUserService {
	boolean isAdminAccountCreated() throws StorageException, NetxiliaResourceException, NetxiliaBusinessException;

	void addUser(User user) throws StorageException;

	User getCurrentUser();

	WorkbookId getWorkbookId();

}
