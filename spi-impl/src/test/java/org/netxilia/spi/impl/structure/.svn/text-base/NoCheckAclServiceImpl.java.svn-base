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
package org.netxilia.spi.impl.structure;

import java.security.AccessControlException;

import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.user.User;

/**
 * For tests only. It makes no check.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class NoCheckAclServiceImpl implements IAclService {

	@Override
	public void checkPermission(SheetFullName sheetName, Permission permission) throws AccessControlException,
			StorageException {

	}

	@Override
	public void setPermissions(SheetFullName sheetName, User grantee, Permission... permissions)
			throws AccessControlException, StorageException {

	}

	@Override
	public void checkPermission(WorkbookId workbookId, Permission permission) throws AccessControlException,
			StorageException {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPermissions(WorkbookId id, User grantee, Permission... permissions) throws AccessControlException,
			StorageException {
		// TODO Auto-generated method stub

	}

}
