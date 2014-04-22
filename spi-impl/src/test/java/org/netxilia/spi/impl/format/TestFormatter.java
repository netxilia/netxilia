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
package org.netxilia.spi.impl.format;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.value.DateValue;
import org.netxilia.api.value.IGenericValueParseService;
import org.netxilia.api.value.NumberValue;
import org.netxilia.api.value.StringValue;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.netxilia.spi.impl.structure.SheetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFormatter {
	private ApplicationContext context;
	private IStyleService formatterService;
	private IGenericValueParseService valueParser;

	@Before
	public void setup() {
		context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		valueParser = context.getBean(IGenericValueParseService.class);
		formatterService = context.getBean(IStyleService.class);
		NetxiliaSystemImpl nx = context.getBean(NetxiliaSystemImpl.class);
		nx.setAclService(new NoCheckAclServiceImpl());
		AclPrivilegedMode.set();
	}

	@Test
	public void testDefaultFormatDate() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", new LocalTime(14, 15, 20));
		Assert.assertEquals(
				"14:15:20",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendValue(new CellReference("A1"), new DateValue(new LocalDateTime(2010, 11, 20, 14, 15, 20)));
		Assert.assertEquals(
				"20-11-2010 14:15:20",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendValue(new CellReference("A1"), new DateValue(new LocalDate(2010, 11, 20)));
		Assert.assertEquals(
				"20-11-2010",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		AclPrivilegedMode.clear();
	}

	@Test
	public void testFormatDate() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", new LocalDateTime(2010, 11, 20, 14, 15, 20));
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("short-time")));
		Assert.assertEquals(
				"14:15",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendValue(new CellReference("A1"), new DateValue(new LocalDateTime(2010, 11, 20, 14, 15, 20)));
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")),
				Styles.styles("short-dateTime")));
		Assert.assertEquals(
				"20-11-10 14:15",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendValue(new CellReference("A1"), new DateValue(new LocalDateTime(2010, 11, 20, 14, 15, 20)));
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("short-date")));
		Assert.assertEquals(
				"20-11-10",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		AclPrivilegedMode.clear();

	}

	@Test
	public void testFormatNumber() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", new Double(10.234));
		Assert.assertEquals(
				"10.23",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendValue(new CellReference("A1"), new NumberValue(new Double(-10.234)));
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("financial")));
		Assert.assertEquals(
				"(10.23)",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		AclPrivilegedMode.clear();
	}

	@Test
	public void testFormatBoolean() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", Boolean.TRUE);
		Assert.assertEquals(
				"TRUE",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("boolean")));
		Assert.assertEquals(
				"TRUE",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		AclPrivilegedMode.clear();
	}

	@Test
	public void testFormatCurrency() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", new Double(10.123));

		// CHF
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("chf")));
		Assert.assertEquals(
				"SFr. 10.12",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// EUR rounded
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("eur-rounded")));
		Assert.assertEquals(
				"10 €",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());
		AclPrivilegedMode.clear();
	}

	@Test
	public void testFormatEnumeration() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", 2);

		// value is ordinal
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("test-enum")));
		Assert.assertEquals(
				"c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// value is name
		sheet.sendValue(new CellReference("A1"), new StringValue("c"));
		Assert.assertEquals(
				"?c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// other value
		sheet.sendValue(new CellReference("A1"), new StringValue("cc"));
		Assert.assertEquals(
				"?cc",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// value is multiple ordinal
		sheet.sendValue(new CellReference("A1"), new StringValue("0,2"));
		Assert.assertEquals(
				"a,c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());
		AclPrivilegedMode.clear();
	}

	@Test
	public void testFormatSheetValues() throws NetxiliaResourceException, NetxiliaBusinessException {
		IStyleService service = formatterService;

		AclPrivilegedMode.set();
		ISheet sheet = SheetUtils.sheetWithCell("A1", 2);

		// value is in value column
		sheet.sendCommand(CellCommands.styles(new AreaReference(new CellReference("A1")), Styles.styles("test-values")));
		Assert.assertEquals(
				"c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// value is in name column
		sheet.sendValue(new CellReference("A1"), new StringValue("c"));
		Assert.assertEquals(
				"?c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// other value
		sheet.sendValue(new CellReference("A1"), new StringValue("cc"));
		Assert.assertEquals(
				"?cc",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());

		// value is multiple ordinal
		sheet.sendValue(new CellReference("A1"), new StringValue("0,2"));
		Assert.assertEquals(
				"a,c",
				service.formatCell(sheet.getWorkbook().getId(),
						sheet.receiveCell(new CellReference("A1")).getNonBlocking(), null, null).getDisplay());
		AclPrivilegedMode.clear();
	}

	@Test
	public void testParseDate() {
		IGenericValueParseService service = valueParser;

		Assert.assertEquals(new LocalTime(14, 15, 0), service.parse("14:15").getDateValue());
		Assert.assertEquals(new LocalDate(2010, 11, 20), service.parse("20/11/2010").getDateValue());
		Assert.assertEquals(new LocalDateTime(2000, 1, 1, 14, 15, 0), service.parse("01-01-00 14:15").getDateValue());
	}
}
