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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.model.SheetNames;
import org.netxilia.api.impl.model.WorkbookIds;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.user.Role;
import org.netxilia.api.user.User;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.StringValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.GrantedAuthorityImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserServiceImpl implements UserDetailsService, IUserService, ISpringUserService {
	private final static Logger log = Logger.getLogger(UserServiceImpl.class);

	private String userSheetName = SheetNames.USERS;

	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private ISheetOperations sheetOperations;

	public INetxiliaSystem getWorkbookProcessor() {
		return workbookProcessor;
	}

	public void setWorkbookProcessor(INetxiliaSystem workbookProcessor) {
		this.workbookProcessor = workbookProcessor;
	}

	public String getUserSheetName() {
		return userSheetName;
	}

	public void setUserSheetName(String userSheetName) {
		this.userSheetName = userSheetName;
	}

	public ISheetOperations getSheetOperations() {
		return sheetOperations;
	}

	public void setSheetOperations(ISheetOperations sheetOperations) {
		this.sheetOperations = sheetOperations;
	}

	protected ISheet getUsersSheet() throws StorageException, NotFoundException {
		try {
			return workbookProcessor.getWorkbook(WorkbookIds.SYSTEM).getSheet(userSheetName);
		} catch (NotFoundException e) {
			return null;
		}

	}

	protected ISheet getWriteableUsersSheet() throws StorageException, NotFoundException {
		try {
			return workbookProcessor.getWorkbook(WorkbookIds.SYSTEM).getSheet(userSheetName);
		} catch (NotFoundException e) {
			try {
				return workbookProcessor.getWorkbook(WorkbookIds.SYSTEM).addNewSheet(userSheetName, SheetType.normal);
			} catch (AlreadyExistsException e1) {
				// try one more time
				return workbookProcessor.getWorkbook(WorkbookIds.SYSTEM).getSheet(userSheetName);
			}
		}

	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException, DataAccessException {
		ISheet sheet = null;
		try {
			log.info("Load user details for " + userName);
			AclPrivilegedMode.set();
			sheet = getUsersSheet();
			if (sheet == null) {
				throw new UsernameNotFoundException(userName);
			}
			CellReference ref = sheetOperations.find(sheet, null, new Formula("=A1=\"" + userName + "\""))
					.getNonBlocking();
			if (ref == null) {
				throw new UsernameNotFoundException(userName);
			}
			// A=username
			// B=password
			// all=ROLE_USER,
			// ROLE_ADMIN=?

			Matrix<CellData> cells = sheet.receiveCells(
					new AreaReference(null, ref.getRowIndex(), 0, ref.getRowIndex(), 3)).getNonBlocking();
			String password = cells.get(0, 1).getValue().getStringValue();
			String roles = cells.get(0, 3).getValue().getStringValue();
			String[] rolesArray = StringUtils.split(roles, ",");
			Role[] netxiliaRoles = new Role[rolesArray.length];
			GrantedAuthority[] springRoles = new GrantedAuthority[rolesArray.length];
			for (int i = 0; i < rolesArray.length; ++i) {
				springRoles[i] = new GrantedAuthorityImpl(rolesArray[i]);
				netxiliaRoles[i] = Role.valueOf(rolesArray[i]);
			}

			User netxiliaUser = new User();
			netxiliaUser.setLogin(userName);
			netxiliaUser.setPassword(password);
			netxiliaUser.setRoles(netxiliaRoles);

			SpringUserAdapter springUser = new SpringUserAdapter(netxiliaUser, true, true, true, true, springRoles);
			log.info("Done user details for " + userName);
			return springUser;
		} catch (NotFoundException e) {
			throw new DataRetrievalFailureException("Sheet 'system.users' not found", e);

		} catch (StorageException e) {
			throw new DataRetrievalFailureException("Sheet 'system.users' not found", e);
		} catch (NetxiliaResourceException e) {
			throw new DataRetrievalFailureException("Sheet 'system.users' not found", e);
		} catch (NetxiliaBusinessException e) {
			throw new DataRetrievalFailureException("Sheet 'system.users' not found", e);
		} finally {
			AclPrivilegedMode.clear();
		}

	}

	@Override
	public void addUser(User user) throws StorageException {
		ISheet sheet = null;

		Map<String, IGenericValue> values = new HashMap<String, IGenericValue>();
		values.put("A", new StringValue(user.getLogin()));
		values.put("B", new StringValue(user.getPassword()));
		if (user.getEmail() != null) {
			values.put("C", new StringValue(user.getEmail().getEmail()));
		}
		values.put("D", new StringValue(StringUtils.join(user.getRoles(), ',')));
		try {
			AclPrivilegedMode.set();
			sheet = getWriteableUsersSheet();
			sheet.sendCommand(CellCommands.mapValues(AreaReference.lastRow(0, 3), values));

		} catch (NotFoundException e) {
			// should not happen as the sheet was previously created
			throw new RuntimeException(e);
		} finally {
			AclPrivilegedMode.clear();
		}

	}

	@Override
	public boolean isAdminAccountCreated() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = null;
		try {
			AclPrivilegedMode.set();
			sheet = getUsersSheet();
			if (sheet == null) {
				return false;
			}
			List<Integer> rows = sheetOperations.filter(sheet, new Formula("=D1=\"ROLE_ADMIN\"")).getNonBlocking();
			return rows != null && rows.size() > 0;
		} catch (NotFoundException e) {
			return false;
		} finally {
			AclPrivilegedMode.clear();
		}

	}

	@Override
	public User getCurrentUser() {
		if (SecurityContextHolder.getContext() != null
				&& SecurityContextHolder.getContext().getAuthentication() != null) {
			if (!(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SpringUserAdapter)) {
				return null;
			}
			SpringUserAdapter springUserAdapter = (SpringUserAdapter) SecurityContextHolder.getContext()
					.getAuthentication().getPrincipal();
			if (springUserAdapter != null) {
				return springUserAdapter.getNetxiliaUser();
			}
		}

		return null;
	}

	public Authentication getSpringAuthentication() {
		if (SecurityContextHolder.getContext() != null) {
			return SecurityContextHolder.getContext().getAuthentication();
		}
		return null;
	}

	public void setSpringAuthentication(Authentication auth) {
		if (SecurityContextHolder.getContext() != null) {
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

	}

	@Override
	public WorkbookId getWorkbookId() {
		return WorkbookIds.SYSTEM;
	}
}
