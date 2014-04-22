package org.netxilia.spi.impl.user;

import java.security.AccessControlException;
import java.util.Collection;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.impl.user.ISpringUserService;
import org.netxilia.api.impl.user.SpringUserAdapter;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.IWorkbook;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.storage.DataSourceConfigurationId;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.user.User;
import org.netxilia.api.value.NumberValue;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class TestACLService {
	private INetxiliaSystem netxilia;
	private ISpringUserService userService;

	// test sheet: read, write, none: read sheet, modif sheet, delete sheet

	@Before
	public void setup() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		netxilia = context.getBean(INetxiliaSystem.class);
		userService = context.getBean(ISpringUserService.class);
	}

	private void setUser(String user) {
		AclPrivilegedMode.clear();
		User nxUser = new User();
		nxUser.setLogin(user);
		nxUser.setPassword("xxx");
		Authentication auth = new TestingAuthenticationToken(new SpringUserAdapter(nxUser, true, true, true, true,
				new GrantedAuthority[0]), null);
		userService.setSpringAuthentication(auth);
	}

	@Test
	public void testWorkbookUserNone() throws StorageException, NotFoundException, AlreadyExistsException {
		setUser("user-none");
		try {
			netxilia.getWorkbook(new WorkbookId("ACL"));
			Assert.fail("AccessControlException was expected");
		} catch (AccessControlException e) {//
		}

	}

	@Test
	public void testWorkbookUserRead() throws StorageException, NotFoundException, AlreadyExistsException {
		setUser("user-read-wk");
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		Collection<ISheet> sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		Assert.assertEquals(2, sheets.size());

		try {
			wk.addNewSheet("new", SheetType.normal);
			Assert.fail("AccessControlException was expected");
		} catch (AccessControlException e) {//
		}
	}

	@Test
	public void testAddWorkbookUserRead() throws StorageException, NotFoundException, AlreadyExistsException {
		setUser("user-read-wk");
		IWorkbook wk = netxilia.addNewWorkbook(new DataSourceConfigurationId(0), new WorkbookId("NEWACL"));
		Assert.assertNotNull(wk);
		Collection<ISheet> sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		// the permission sheet was created
		Assert.assertEquals(1, sheets.size());

		wk.addNewSheet("new", SheetType.normal);

	}

	@Test
	public void testWorkbookUserWrite() throws StorageException, NotFoundException, AlreadyExistsException {
		setUser("user-write-wk");
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		Collection<ISheet> sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		Assert.assertEquals(2, sheets.size());

		wk.addNewSheet("new", SheetType.normal);
		sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		Assert.assertEquals(3, sheets.size());

	}

	@Test
	public void testWorkbookAdmin() throws StorageException, NotFoundException, AlreadyExistsException {
		AclPrivilegedMode.set();
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		Collection<ISheet> sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		Assert.assertEquals(2, sheets.size());

		wk.addNewSheet("new", SheetType.normal);
		sheets = wk.getSheets();
		Assert.assertNotNull(sheets);
		Assert.assertEquals(3, sheets.size());

	}

	@Test
	public void testSheetUserRead() throws NetxiliaResourceException, NetxiliaBusinessException {
		setUser("user-read-sh");
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		ISheet sheet = wk.getSheet("test");
		Assert.assertNotNull(sheet);

		// read
		CellData cell = sheet.receiveCell(new CellReference("A1")).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertEquals(1.0, cell.getValue().getNumberValue());

		// write
		try {
			sheet.sendValue(new CellReference("A1"), new NumberValue(2));
			Assert.fail("AccessControlException was expected");
		} catch (AccessControlException e) {//
		}

		// delete
		try {
			wk.deleteSheet("test");
			Assert.fail("AccessControlException was expected");
		} catch (AccessControlException e) {//
		}

	}

	@Test
	public void testSheetUserWrite() throws NetxiliaResourceException, NetxiliaBusinessException {
		setUser("user-write-sh");
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		ISheet sheet = wk.getSheet("test");
		Assert.assertNotNull(sheet);

		// read
		CellData cell = sheet.receiveCell(new CellReference("A1")).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertEquals(1.0, cell.getValue().getNumberValue());

		// write
		sheet.sendValue(new CellReference("A1"), new NumberValue(2));

		// delete
		wk.deleteSheet("test");
	}

	@Test
	public void testSheetAdmin() throws NetxiliaResourceException, NetxiliaBusinessException {
		AclPrivilegedMode.set();
		IWorkbook wk = netxilia.getWorkbook(new WorkbookId("ACL"));
		ISheet sheet = wk.getSheet("test");
		Assert.assertNotNull(sheet);

		// read
		CellData cell = sheet.receiveCell(new CellReference("A1")).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertEquals(1.0, cell.getValue().getNumberValue());

		// write
		sheet.sendValue(new CellReference("A1"), new NumberValue(2));

		// delete
		wk.deleteSheet("test");
	}
}
