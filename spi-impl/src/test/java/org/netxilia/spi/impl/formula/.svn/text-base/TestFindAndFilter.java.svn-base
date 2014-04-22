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
package org.netxilia.spi.impl.formula;

import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.operation.ISheetOperations;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.netxilia.spi.impl.structure.SheetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFindAndFilter {
	private ISheetOperations operations;

	@Before
	public void startup() throws AlreadyExistsException, StorageException, NotFoundException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		NetxiliaSystemImpl nx = context.getBean(NetxiliaSystemImpl.class);
		nx.setAclService(new NoCheckAclServiceImpl());
		operations = context.getBean(ISheetOperations.class);
		AclPrivilegedMode.set();
	}

	@Test
	public void testFindValue() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell("A1", 200, "A2", "200", "A3", 200.0);
		CellReference ref1 = operations.find(sheet, null, new Formula("=A1=200")).getNonBlocking();
		Assert.assertEquals(new CellReference("test!A1"), ref1);

		// "200" is not equals to 200 !
		CellReference ref3 = operations.find(sheet, ref1, new Formula("=A1=200")).getNonBlocking();
		Assert.assertEquals(new CellReference("test!A3"), ref3);

		CellReference ref4 = operations.find(sheet, ref3, new Formula("=A1=200")).getNonBlocking();
		Assert.assertNull(ref4);
	}

	@Test
	public void testFindWrongFormula() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell("A1", 200, "A2", "200", "A3", 200.0);
		CellReference ref1 = operations.find(sheet, null, new Formula("=A1=200+")).getNonBlocking();
		Assert.assertNull(ref1);
	}

	@Test
	public void testFilterValue() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell("A1", 200, "A2", "200", "A3", 200.0);
		List<Integer> rows = operations.filter(sheet, new Formula("=A1=200")).getNonBlocking();
		Assert.assertNotNull(rows);
		Assert.assertEquals(rows.size(), 2);
		Assert.assertEquals(0, rows.get(0).intValue());
		Assert.assertEquals(2, rows.get(1).intValue());
	}

	@Test
	public void testFilterValueNotFound() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell("A1", 200, "A2", "200", "A3", 200.0);
		List<Integer> rows = operations.filter(sheet, new Formula("=A1=300")).getNonBlocking();
		Assert.assertNotNull(rows);
		Assert.assertEquals(rows.size(), 0);

	}

	@Test
	public void testFilterValueWrongFormula() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell("A1", 200, "A2", "200", "A3", 200.0);
		List<Integer> rows = operations.filter(sheet, new Formula("=A1=300+")).getNonBlocking();
		Assert.assertNotNull(rows);
		Assert.assertEquals(rows.size(), 0);

	}
}
