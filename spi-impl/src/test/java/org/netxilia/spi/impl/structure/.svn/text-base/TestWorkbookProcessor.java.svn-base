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

import org.junit.Assert;
import org.junit.Test;
import org.netxilia.api.command.IMoreCellCommands;
import org.netxilia.api.command.RowCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.IPreloadedFormulaContextFactory;
import org.netxilia.api.impl.IExecutorServiceFactory;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.user.UserServiceImpl;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.api.value.NumberValue;
import org.netxilia.spi.formula.IFormulaCalculatorFactory;
import org.netxilia.spi.formula.IFormulaParser;
import org.netxilia.spi.impl.storage.memory.InMemoryWorkbookStorageServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestWorkbookProcessor {
	private NetxiliaSystemImpl getWorkbookProcessor() {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");

		NetxiliaSystemImpl p = new NetxiliaSystemImpl();
		InMemoryWorkbookStorageServiceImpl storage = new InMemoryWorkbookStorageServiceImpl();
		storage.setWorkbookProcessor(p);
		p.setStorageService(storage);
		p.setAclService(new NoCheckAclServiceImpl());
		UserServiceImpl userService = new UserServiceImpl();
		p.setUserService(userService);
		p.setSpringUserService(userService);
		p.setStyleService(context.getBean(IStyleService.class));
		p.setFormulaParser(context.getBean(IFormulaParser.class));
		p.setParseService(context.getBean(IGenericValueParseService.class));
		p.setFormulaCalculatorFactory(context.getBean(IFormulaCalculatorFactory.class));
		p.setExecutorServiceFactory(context.getBean(IExecutorServiceFactory.class));
		p.setMoreCellCommands(context.getBean(IMoreCellCommands.class));
		p.setPreloadContextFactory(context.getBean(IPreloadedFormulaContextFactory.class));
		return p;
	}

	@Test
	public void testDeleteRow() throws NetxiliaResourceException, NetxiliaBusinessException {
		NetxiliaSystemImpl p = getWorkbookProcessor();
		SheetFullName name = new SheetFullName("test", "test");
		ISheet s = p.getWorkbook(name.getWorkbookId()).addNewSheet(name.getSheetName(), SheetType.normal);
		s.sendFormula(new CellReference(1, 1), new Formula("=E3 + 1")); // B2
		s.sendFormula(new CellReference(2, 1), new Formula("=C3 + 1")); // B3
		s.sendFormula(new CellReference(2, 3), new Formula("=sum(A1:A3)")); // D3

		s = p.getWorkbook(name.getWorkbookId()).getSheet(name.getSheetName());
		s.sendCommand(RowCommands.delete(Range.range(1)));

		Assert.assertNull(s.receiveCell(new CellReference(0, 1)).getNonBlocking().getValue());

		CellData cell = s.receiveCell(new CellReference(1, 1)).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertNotNull(cell.getFormula());
		Assert.assertEquals("=C2 + 1", cell.getFormula().getFormula());

		CellData cell2 = s.receiveCell(new CellReference(1, 3)).getNonBlocking();
		Assert.assertNotNull(cell2);
		Assert.assertNotNull(cell2.getFormula());
		Assert.assertEquals("=SUM(A1:A2)", cell2.getFormula().getFormula());
	}

	@Test
	public void testInsertRow() throws NetxiliaResourceException, NetxiliaBusinessException {
		NetxiliaSystemImpl p = getWorkbookProcessor();
		SheetFullName name = new SheetFullName("test", "test");
		ISheet s = p.getWorkbook(name.getWorkbookId()).addNewSheet(name.getSheetName(), SheetType.normal);
		s.sendFormula(new CellReference(2, 1), new Formula("=A2 + 1")); // B3

		s = p.getWorkbook(name.getWorkbookId()).getSheet(name.getSheetName());
		s.sendCommand(RowCommands.insert(Range.range(2)));

		CellData cell = s.receiveCell(new CellReference(3, 1)).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertNotNull(cell.getFormula());
		Assert.assertEquals("=A2 + 1", cell.getFormula().getFormula());

		Assert.assertNull(s.receiveCell(new CellReference(2, 1)).getNonBlocking().getValue());
		// Assert.assertNotNull(s.getCell(3, 1).getFormula());
		// Assert.assertEquals("=A2+1", s.getCell(3, 1).getFormula().getFormula());

		// Assert.assertNotNull(s.getCell(1, 3));
		// Assert.assertNotNull(s.getCell(1, 3).getFormula());
		// Assert.assertEquals("=SUM(A1:A2)", s.getCell(1, 3).getFormula().getFormula());
	}

	@Test
	public void testFormulaAndLocks() throws NetxiliaResourceException, NetxiliaBusinessException {
		NetxiliaSystemImpl p = getWorkbookProcessor();
		SheetFullName name1 = new SheetFullName("test", "test1");
		ISheet s1 = p.getWorkbook(name1.getWorkbookId()).addNewSheet(name1.getSheetName(), SheetType.normal);
		s1.sendValue(new CellReference("A1"), new NumberValue(2)); // A1

		SheetFullName name2 = new SheetFullName("test", "test2");
		ISheet s2 = p.getWorkbook(name2.getWorkbookId()).addNewSheet(name2.getSheetName(), SheetType.normal);
		s2.sendFormula(new CellReference("B2"), new Formula("=test1!A1 + 2"));

		s1 = p.getWorkbook(name1.getWorkbookId()).getSheet(name1.getSheetName());
		s2 = p.getWorkbook(name2.getWorkbookId()).getSheet(name2.getSheetName());

		CellData cell2 = s2.receiveCell(new CellReference("B2")).getNonBlocking();
		Assert.assertNotNull(cell2);
		Assert.assertNotNull(cell2.getFormula());
		Assert.assertNotNull(cell2.getValue());
		Assert.assertEquals(4, cell2.getValue().getNumberValue(), 0.1);

	}
}
