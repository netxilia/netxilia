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

import java.security.AccessControlException;

import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;

/**
 * This service handles the permissions of the users and groups on the system's sheets. The implementations of this
 * service choose how store the permissions for each sheet.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public interface IAclService {
	/**
	 * This method verifies that the principal in the current spring security context has the given permission on the
	 * given sheet.
	 * 
	 * @param sheet
	 * @param permission
	 * @throws AccessControlException
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public void checkPermission(SheetFullName sheetName, Permission permission) throws AccessControlException;

	/**
	 * This method verifies that the principal in the current spring security context has the given permission on the
	 * given workbook.
	 * 
	 * @param workbookId
	 * @param permission
	 * @throws AccessControlException
	 * @throws StorageException
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	public void checkPermission(WorkbookId workbookId, Permission permission) throws AccessControlException;

	/**
	 * Store the given list of permission for the given sheet and for the given user. The principal in the current
	 * security context has to have the permission to {@link Permission#administer} the sheet.
	 * 
	 * @param sheet
	 * @param grantee
	 * @param permissions
	 * @throws AccessControlException
	 * @throws StorageException
	 */
	public void setPermissions(SheetFullName sheetName, User grantee, Permission... permissions)
			throws AccessControlException, StorageException;

	/**
	 * Store the given list of permission for the given workbook and for the given user. The principal in the current
	 * security context has to have the permission to {@link Permission#administer} the sheet.
	 * 
	 * @param id
	 * @param grantee
	 * @param permissions
	 * @throws AccessControlException
	 * @throws StorageException
	 */
	public void setPermissions(WorkbookId id, User grantee, Permission... permissions) throws AccessControlException,
			StorageException;

}
