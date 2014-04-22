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
package org.netxilia.api.impl.user;

import java.security.AccessControlException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.model.SheetNames;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.user.AclObjectType;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.IAclService;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.Permission;
import org.netxilia.api.user.User;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.StringValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class AclServiceImpl implements IAclService {
	private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(AclServiceImpl.class);

	private String permissionSheet = SheetNames.PERMISSIONS;

	private static final Map<String, IGenericValue> HEADERS = new HashMap<String, IGenericValue>();

	private static final String ANY_USER = "*";
	private static final String ANY_SHEET = "*";

	static {
		HEADERS.put("A", new StringValue("Object type"));
		HEADERS.put("B", new StringValue("User"));
		HEADERS.put("C", new StringValue("Object name"));
		HEADERS.put("D", new StringValue("Permission"));
	}
	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IUserService userService;

	@Autowired
	private ISheetOperations sheetOperations;

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	public IUserService getUserService() {
		return userService;
	}

	public void setUserService(IUserService userService) {
		this.userService = userService;
	}

	public ISheetOperations getSheetOperations() {
		return sheetOperations;
	}

	public void setSheetOperations(ISheetOperations sheetOperations) {
		this.sheetOperations = sheetOperations;
	}

	protected ISheet getAclSheet(WorkbookId workbookId) throws StorageException, NotFoundException {
		try {
			return workbookProcessor.getWorkbook(workbookId).getSheet(permissionSheet);
		} catch (NotFoundException e) {
			ISheet aclSheet = null;
			try {
				aclSheet = workbookProcessor.getWorkbook(workbookId).addNewSheet(permissionSheet, SheetType.normal);
			} catch (AlreadyExistsException e1) {
				// nothing to do
			}
			aclSheet.sendCommand(CellCommands.mapValues(AreaReference.lastRow(0, 3), HEADERS));
			return aclSheet;
		}
	}

	private boolean checkPermission(INetxiliaSystem workbookProcessor, ISheet aclSheet, AclObjectType objectType,
			String userSpec, String sheetName, Permission permission) throws NetxiliaResourceException,
			NetxiliaBusinessException {

		// A=object type => sheet
		// B=username
		// C=object name => sheet name
		// D=permissions comma delimited

		List<Integer> rows;
		String formulaText = "=AND(A1=\"" + objectType.name() + "\", B1=\"" + userSpec + "\"";
		if (sheetName != null) {
			formulaText += ",C1=\"" + sheetName + "\"";
		}
		formulaText += ")";
		rows = sheetOperations.filter(aclSheet, new Formula(formulaText)).getNonBlocking();
		// NO AND YET
		if (rows == null || rows.size() == 0) {
			return false;
		}

		int row = rows.get(0);
		Matrix<CellData> cells = aclSheet.receiveCells(new AreaReference(null, row, 0, row, 3)).getNonBlocking();

		String permissions = cells.get(0, 3).getValue().getStringValue();
		String[] permissionsArray = StringUtils.split(permissions, ",");
		for (int i = 0; i < permissionsArray.length; ++i) {
			Permission p = Permission.valueOf(permissionsArray[i]);
			if (p == permission) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void checkPermission(SheetFullName sheetFullName, Permission permission) throws AccessControlException {
		if (log.isDebugEnabled()) {
			log.debug("Check for " + sheetFullName + " " + permission + " isSet:" + AclPrivilegedMode.isSet());
		}
		if (AclPrivilegedMode.isSet()) {
			return;
		}

		ISheet aclSheet = null;

		boolean wasSet = AclPrivilegedMode.set();
		try {

			aclSheet = getAclSheet(new WorkbookId(sheetFullName.getWorkbookName()));

			User user = userService.getCurrentUser();
			if (user == null) {
				throw new AccessControlException("No current user");
			}
			if (user.isAdmin()) {
				return;
			}
			// sheet.summary has the same permissions as sheet itself
			String sheetName = SheetFullName.sheetSimpleName(sheetFullName.getSheetName(), user);
			// if it's the user's private sheet, all access is allowed
			if (sheetName.equals(SheetFullName.privateSheetName(sheetFullName, user))) {
				return;
			}

			// check user
			if (checkPermission(workbookProcessor, aclSheet, AclObjectType.sheet, user.getLogin(), sheetName,
					permission)) {
				return;
			}
			// TODO: check groups
			// check all
			if (checkPermission(workbookProcessor, aclSheet, AclObjectType.sheet, ANY_USER, sheetName, permission)) {
				return;
			}

			if (checkPermission(workbookProcessor, aclSheet, AclObjectType.sheet, user.getLogin(), ANY_SHEET,
					permission)) {
				return;
			}

			throw new AccessControlException("Operation not permitted");
		} catch (NotFoundException e) {
			// only happens if somebody deleted the sheet right before the filtering
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} catch (NetxiliaResourceException e) {
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} catch (NetxiliaBusinessException e) {
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} finally {
			if (!wasSet) {
				AclPrivilegedMode.clear();
			}
			if (log.isDebugEnabled()) {
				log.debug("<-- done for " + sheetFullName + " " + permission);
			}
		}

	}

	@Override
	public void setPermissions(SheetFullName sheetFullName, User grantee, Permission... permissions)
			throws AccessControlException, StorageException {

		// sheet.summary has the same permissions as sheet itself
		if (sheetFullName.getType() != SheetType.normal) {
			return;
		}
		setPermissions(AclObjectType.sheet, new WorkbookId(sheetFullName.getWorkbookName()),
				sheetFullName.getSheetName(), grantee, permissions);
	}

	@Override
	public void checkPermission(WorkbookId workbookId, Permission permission) throws AccessControlException {
		if (log.isDebugEnabled()) {
			log.debug("Check for " + workbookId + " " + permission + " isSet:" + AclPrivilegedMode.isSet());
		}
		if (AclPrivilegedMode.isSet()) {
			return;
		}

		ISheet aclSheet = null;
		boolean wasSet = AclPrivilegedMode.set();
		try {

			aclSheet = getAclSheet(workbookId);

			User user = userService.getCurrentUser();
			if (user == null) {
				throw new AccessControlException("No current user");
			}
			if (user.isAdmin()) {
				return;
			}

			// check user
			if (checkPermission(workbookProcessor, aclSheet, AclObjectType.workbook, user.getLogin(), null, permission)) {
				return;
			}
			// TODO: check groups
			// check all
			if (checkPermission(workbookProcessor, aclSheet, AclObjectType.workbook, ANY_USER, null, permission)) {
				return;
			}

			throw new AccessControlException("Operation not permitted");
		} catch (NotFoundException e) {
			// only happens if somebody deleted the sheet right before the filtering
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} catch (NetxiliaResourceException e) {
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} catch (NetxiliaBusinessException e) {
			throw new AccessControlException("Cannot check permissions. Reason: " + e);
		} finally {
			if (!wasSet) {
				AclPrivilegedMode.clear();
			}
			if (log.isDebugEnabled()) {
				log.debug("<-- done for " + workbookId + " " + permission);
			}
		}

	}

	@Override
	public void setPermissions(WorkbookId id, User grantee, Permission... permissions) throws AccessControlException,
			StorageException {
		setPermissions(AclObjectType.workbook, id, null, grantee, permissions);

	}

	private void setPermissions(AclObjectType objectType, WorkbookId workbookId, String sheetName, User grantee,
			Permission... permissions) throws StorageException {

		// A=object type => sheet
		// B=username
		// C=object name => sheet name
		// D=permissions comma delimited
		ISheet aclSheet = null;
		boolean wasSet = AclPrivilegedMode.set();
		try {

			aclSheet = getAclSheet(workbookId);

			User user = userService.getCurrentUser();
			if (user == null) {
				throw new AccessControlException("No current user");
			}
			if (user.isAdmin()) {
				return;
			}

			Map<String, IGenericValue> row = new HashMap<String, IGenericValue>();
			row.put("A", new StringValue(objectType.name()));
			row.put("B", new StringValue(user.getLogin()));
			row.put("C", new StringValue(sheetName));
			StringBuilder permString = new StringBuilder();
			for (Permission perm : permissions) {
				if (permString.length() > 0) {
					permString.append(",");
				}
				permString.append(perm.name());
			}
			row.put("D", new StringValue(permString.toString()));

			aclSheet.sendCommand(CellCommands.mapValues(AreaReference.lastRow(0, 3), row));
		} catch (NotFoundException e) {
			// only happens if somebody deleted the sheet right before the filtering
			throw new StorageException(e);
		} finally {
			if (!wasSet) {
				AclPrivilegedMode.clear();
			}
		}
	}
}
