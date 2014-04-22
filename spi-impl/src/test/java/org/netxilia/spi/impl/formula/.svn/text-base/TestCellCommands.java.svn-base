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

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.netxilia.spi.impl.structure.SheetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCellCommands {
	private IMoreCellCommands cellCommands;

	@Before
	public void startup() throws AlreadyExistsException, StorageException, NotFoundException {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		NetxiliaSystemImpl nx = context.getBean(NetxiliaSystemImpl.class);
		nx.setAclService(new NoCheckAclServiceImpl());
		cellCommands = context.getBean(IMoreCellCommands.class);
		AclPrivilegedMode.set();
	}

	@Test
	public void testCopyContent() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell();

		CellData data = new CellData(new CellReference("A1"), new Formula("=B1 + 10"));
		sheet.sendCommand(cellCommands.copyContent(new AreaReference("C2:C3"), data));

		CellData newData = sheet.receiveCell(new CellReference("C2")).getNonBlocking();
		Assert.assertNotNull(newData);
		Assert.assertEquals(new Formula("=D2 + 10"), newData.getFormula());

		CellData newData2 = sheet.receiveCell(new CellReference("C3")).getNonBlocking();
		Assert.assertNotNull(newData2);
		Assert.assertEquals(new Formula("=D3 + 10"), newData2.getFormula());
	}

	@Test
	public void testPasteFromCell() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell();

		String[][] data = { { "=B1 + 10", "100" }, { "true", "=$B$2 + 100" } };
		sheet.sendCommand(cellCommands.paste(new AreaReference("C2:D3"), data, new CellReference("A1")));

		CellData newDataC2 = sheet.receiveCell(new CellReference("C2")).getNonBlocking();
		Assert.assertNotNull(newDataC2);
		Assert.assertEquals(new Formula("=D2 + 10"), newDataC2.getFormula());

		CellData newDataC3 = sheet.receiveCell(new CellReference("C3")).getNonBlocking();
		Assert.assertNotNull(newDataC3);
		Assert.assertEquals(true, newDataC3.getValue().getBooleanValue().booleanValue());

		CellData newDataD2 = sheet.receiveCell(new CellReference("D2")).getNonBlocking();
		Assert.assertNotNull(newDataD2);
		Assert.assertEquals(100.0, newDataD2.getValue().getNumberValue());

		CellData newDataD3 = sheet.receiveCell(new CellReference("D3")).getNonBlocking();
		Assert.assertNotNull(newDataD3);
		Assert.assertEquals(new Formula("=$B$2 + 100"), newDataD3.getFormula());
	}

	@Test
	public void testPasteNoCell() throws NetxiliaResourceException, NetxiliaBusinessException {
		ISheet sheet = SheetUtils.sheetWithCell();

		String[][] data = { { "=B1 + 10", "100" }, { "true", "=$B$2 + 100" } };
		sheet.sendCommand(cellCommands.paste(new AreaReference("C2:D3"), data, null));

		CellData newDataC2 = sheet.receiveCell(new CellReference("C2")).getNonBlocking();
		Assert.assertNotNull(newDataC2);
		Assert.assertEquals(new Formula("=B1 + 10"), newDataC2.getFormula());

		CellData newDataC3 = sheet.receiveCell(new CellReference("C3")).getNonBlocking();
		Assert.assertNotNull(newDataC3);
		Assert.assertEquals(true, newDataC3.getValue().getBooleanValue().booleanValue());

		CellData newDataD2 = sheet.receiveCell(new CellReference("D2")).getNonBlocking();
		Assert.assertNotNull(newDataD2);
		Assert.assertEquals(100.0, newDataD2.getValue().getNumberValue());

		CellData newDataD3 = sheet.receiveCell(new CellReference("D3")).getNonBlocking();
		Assert.assertNotNull(newDataD3);
		Assert.assertEquals(new Formula("=$B$2 + 100"), newDataD3.getFormula());
	}
}
