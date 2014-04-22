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

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.ReferenceTransformers;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.ErrorValueType;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.api.value.NumberValue;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.netxilia.spi.impl.structure.SheetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestFormulaParser {
	private ApplicationContext context;

	@Before
	public void setup() {
		context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		NetxiliaSystemImpl nx = context.getBean(NetxiliaSystemImpl.class);
		nx.setAclService(new NoCheckAclServiceImpl());
		AclPrivilegedMode.set();
	}

	private JavaCCFormulaParserImpl getParser() {
		return context.getBean(JavaCCFormulaParserImpl.class);
	}

	@Test
	public void testNumberOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(3.0,
				parser.executeFormula(new Formula("=a1 + 1"), SheetUtils.sheetWithCell("A1", 2.0), null)
						.getNumberValue(), 0.001);
		Assert.assertEquals(125.0, parser.executeFormula(new Formula("=5^3"), SheetUtils.sheetWithCell(), null)
				.getNumberValue(), 0.001);

		Assert.assertEquals(1.0,
				parser.executeFormula(new Formula("=a1 - 1"), SheetUtils.sheetWithCell("A1", 2.0), null)
						.getNumberValue(), 0.001);

		Assert.assertEquals(4.0,
				parser.executeFormula(new Formula("=a1 * 2"), SheetUtils.sheetWithCell("A1", 2.0), null)
						.getNumberValue(), 0.001);

		Assert.assertEquals(1.0,
				parser.executeFormula(new Formula("=a1 / 2"), SheetUtils.sheetWithCell("A1", 2.0), null)
						.getNumberValue(), 0.001);

		IGenericValue value = parser.executeFormula(new Formula("=a1 / 0"), SheetUtils.sheetWithCell("A1", 2.0), null);
		Assert.assertTrue(value instanceof ErrorValue);
		Assert.assertEquals(ErrorValueType.DIV_ZERO, ((ErrorValue) value).getErrorType());

		try {
			value = parser.executeFormula(new Formula("=a1 % 10"), SheetUtils.sheetWithCell("A1", 2.0), null);
			Assert.fail("Expecting FormulaParsingException");
		} catch (FormulaParsingException ex) {
			//
		}
	}

	@Test
	public void testDateOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(
				new LocalDateTime(2001, 1, 2, 0, 0),
				parser.executeFormula(new Formula("=a1 + 1"),
						SheetUtils.sheetWithCell("A1", new LocalDate(2001, 1, 1)), null).getDateValue());

		Assert.assertEquals(
				new LocalDateTime(2000, 12, 31, 0, 0),
				parser.executeFormula(new Formula("=a1 - 1"),
						SheetUtils.sheetWithCell("A1", new LocalDate(2001, 1, 1)), null).getDateValue());

		Assert.assertEquals(
				-2.0,
				parser.executeFormula(new Formula("=a1 - b1"),
						SheetUtils.sheetWithCell("A1", new LocalDate(2001, 1, 1), "B1", new LocalDate(2001, 1, 3)),
						null).getNumberValue(), 0.001);

		Assert.assertEquals(
				new LocalDateTime(2001, 1, 2, 0, 0),
				parser.executeFormula(new Formula("=1 + a1"),
						SheetUtils.sheetWithCell("A1", new LocalDate(2001, 1, 1)), null).getDateValue());

	}

	@Test
	public void testStringOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals("abcxy",
				parser.executeFormula(new Formula("=a1 & \"xy\""), SheetUtils.sheetWithCell("A1", "abc"), null)
						.getStringValue());
		Assert.assertEquals("=A1 & \"xy\"", parser.parseFormula(new Formula("=a1 & \"xy\"")).getFormula());

	}

	@Test
	public void testLogicalOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(true,
				parser.executeFormula(new Formula("=A1=\"2\""), SheetUtils.sheetWithCell("A1", "2"), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=A1=2"), SheetUtils.sheetWithCell("A1", "2"), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=A1 < 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=A1 > 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(true,
				parser.executeFormula(new Formula("=A1 <= 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(true,
				parser.executeFormula(new Formula("=A1 >= 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=A1 <> 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=A1 != 2"), SheetUtils.sheetWithCell("A1", 2), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(
				ErrorValueType.VALUE,
				((ErrorValue) parser.executeFormula(new Formula("=A1 != 2"),
						SheetUtils.sheetWithCell("A1", ErrorValueType.VALUE), null)).getErrorType());

		Assert.assertEquals(
				ErrorValueType.VALUE,
				((ErrorValue) parser.executeFormula(new Formula("=2 != A1"),
						SheetUtils.sheetWithCell("A1", ErrorValueType.VALUE), null)).getErrorType());

	}

	@Test
	public void testFilter() throws NetxiliaResourceException, NetxiliaBusinessException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell("A1", "abc", "A2", "cde", "A3", "abc");
		List<Integer> rows = parser.filterWithFormula(new Formula("=A1=\"abc\""), sheet);
		Assert.assertNotNull(rows);
		Assert.assertTrue(Arrays.deepEquals(new Integer[] { 0, 2 }, rows.toArray(new Integer[2])));
	}

	@Test
	public void testFind() throws NetxiliaResourceException, NetxiliaBusinessException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell("A1", "abc", "A2", "cde", "A3", "abc");
		CellReference ref = parser.find(null, new Formula("=A1=\"abc\""), sheet);
		Assert.assertNotNull(ref);
		Assert.assertEquals(0, ref.getRowIndex());
		Assert.assertEquals(0, ref.getColumnIndex());

		ref = parser.find(ref, new Formula("=A1=\"abc\""), sheet);
		Assert.assertNotNull(ref);
		Assert.assertEquals(2, ref.getRowIndex());
		Assert.assertEquals(0, ref.getColumnIndex());

		ref = parser.find(ref, new Formula("=A1=\"abc\""), sheet);
		Assert.assertNull(ref);

	}

	@Test
	public void testCrossReference() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell(context.getBean(NetxiliaSystemImpl.class), "A1", "100", "A2", "cde",
				"A3", "abc");
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("test2", SheetType.normal);
		Assert.assertEquals(110.0, parser.executeFormula(new Formula("=test!A1 + 10"), sheet2, null).getNumberValue(),
				0.001);
	}

	@Test
	public void testInvalidSheetReference() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell(context.getBean(NetxiliaSystemImpl.class), "A1", "100", "A2", "cde",
				"A3", "abc");
		IGenericValue value = parser.executeFormula(new Formula("=test2!A1 + 10"), sheet, null);
		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.ERROR, value.getValueType());
		Assert.assertEquals(ErrorValueType.REF, ((ErrorValue) value).getErrorType());
	}

	@Test
	public void testCrossReferenceComplexSheetName() throws FormulaParsingException, AlreadyExistsException,
			StorageException, NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell(context.getBean(NetxiliaSystemImpl.class), "A1", "100", "A2", "cde",
				"A3", "abc");
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("Sheet - é", SheetType.normal);
		sheet2.sendValue(new CellReference("A1"), new NumberValue(101));

		Assert.assertEquals(111.0, parser.executeFormula(new Formula("='Sheet - é'!A1 + 10"), sheet, null)
				.getNumberValue(), 0.001);
	}

	@Test
	public void testShortReferenceMainSheetName() throws FormulaParsingException, AlreadyExistsException,
			StorageException, NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell(context.getBean(NetxiliaSystemImpl.class), "A1", "100", "A2", "cde",
				"A3", "abc");
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("test.summary", SheetType.summary);

		Assert.assertEquals(110.0, parser.executeFormula(new Formula("=test!A1 + 10"), sheet2, null).getNumberValue(),
				0.001);

		Assert.assertEquals(110.0, parser.executeFormula(new Formula("=.!A1 + 10"), sheet2, null).getNumberValue(),
				0.001);
	}

	@Test
	public void testFunctionWithReference() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "2", "A2", 3, "B3", 2.5);

		// use reference outside
		IGenericValue value = parser.executeFormula(new Formula("=AND(A1, A5)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.BOOLEAN, value.getValueType());
		Assert.assertEquals(Boolean.TRUE, value.getBooleanValue());

		// use reference outside
		value = parser.executeFormula(new Formula("=IF(B1, A1, A2)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		Assert.assertEquals(3, value.getNumberValue(), 0.01);

	}

	@Test
	public void testWrongFunction() throws FormulaParsingException, StorageException, AlreadyExistsException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		// with IGenericValue
		ISheet sheet = SheetUtils.sheetWithCell();
		IGenericValue value = parser.executeFormula(new Formula("=XXSUM(20, 10)"), sheet, null);
		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.ERROR, value.getValueType());
		Assert.assertEquals(new ErrorValue(ErrorValueType.NAME), value);
	}

	@Ignore
	public void testTrueFalseFunction() throws FormulaParsingException {
		JavaCCFormulaParserImpl parser = getParser();
		// with IGenericValue
		IGenericValue value = parser.executeFormula(new Formula("=TRUE()"), null, null);
		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.BOOLEAN, value.getValueType());
		Assert.assertEquals(Boolean.TRUE, value.getBooleanValue());
	}

	@Test
	public void testParseFormula() throws FormulaParsingException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(new Formula("=A1 + 1"), parser.parseFormula(new Formula("=a1 +1")));

	}

	@Test
	public void testTransformFormula() throws FormulaParsingException {
		JavaCCFormulaParserImpl parser = getParser();

		// cells
		Assert.assertEquals(
				new Formula("=A1 + 1"),
				parser.transformFormula(new Formula("=b2 +1"),
						ReferenceTransformers.shiftCell(new CellReference("D2"), new CellReference("C1"))));
		Assert.assertEquals(
				new Formula("=#REF + 1"),
				parser.transformFormula(new Formula("=b2 +1"),
						ReferenceTransformers.shiftCell(new CellReference("D2"), new CellReference("B1"))));

		// areas
		Assert.assertEquals(
				new Formula("=SUM(A1:B1)"),
				parser.transformFormula(new Formula("=sum(b2:c2)"),
						ReferenceTransformers.shiftCell(new CellReference("D2"), new CellReference("C1"))));
		Assert.assertEquals(
				new Formula("=SUM(#REF)"),
				parser.transformFormula(new Formula("=sum(b2:c2)"),
						ReferenceTransformers.shiftCell(new CellReference("D2"), new CellReference("B1"))));

		// delete row
		Assert.assertEquals(new Formula("=SUM(A2:A3)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.deleteRow(2)));
		Assert.assertEquals(new Formula("=SUM(A2:A3)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.deleteRow(1)));
		Assert.assertEquals(new Formula("=SUM(A1:A3)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.deleteRow(0)));
		Assert.assertEquals(new Formula("=SUM(A2:A4)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.deleteRow(5)));
		Assert.assertEquals(new Formula("=#REF + 1"),
				parser.transformFormula(new Formula("=a2 + 1"), ReferenceTransformers.deleteRow(1)));

		// insert row
		Assert.assertEquals(new Formula("=SUM(A2:A5)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.insertRow(2)));
		Assert.assertEquals(new Formula("=SUM(A3:A5)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.insertRow(1)));
		Assert.assertEquals(new Formula("=SUM(A2:A4)"),
				parser.transformFormula(new Formula("=sum(A2:A4)"), ReferenceTransformers.insertRow(5)));

		// delete col
		Assert.assertEquals(new Formula("=SUM(B1:C1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.deleteColumn(2)));
		Assert.assertEquals(new Formula("=SUM(B1:C1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.deleteColumn(1)));
		Assert.assertEquals(new Formula("=SUM(A1:C1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.deleteColumn(0)));
		Assert.assertEquals(new Formula("=SUM(B1:D1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.deleteColumn(5)));
		Assert.assertEquals(new Formula("=#REF + 1"),
				parser.transformFormula(new Formula("=b1 + 1"), ReferenceTransformers.deleteColumn(1)));

		// insert col
		Assert.assertEquals(new Formula("=SUM(B1:E1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.insertColumn(2)));
		Assert.assertEquals(new Formula("=SUM(C1:E1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.insertColumn(1)));
		Assert.assertEquals(new Formula("=SUM(B1:D1)"),
				parser.transformFormula(new Formula("=sum(B1:D1)"), ReferenceTransformers.insertColumn(5)));

	}

	@Test
	public void testErrorParseFormula() throws FormulaParsingException {
		JavaCCFormulaParserImpl parser = getParser();

		try {
			parser.parseFormula(null);
			Assert.fail("No exception thrown");
		} catch (Exception e) {
			Assert.assertEquals(NullPointerException.class, e.getClass());
		}

		try {
			parser.parseFormula(new Formula("=A + 4 -"));
			Assert.fail("No exception thrown");
		} catch (Exception e) {
			Assert.assertEquals(FormulaParsingException.class, e.getClass());
		}

	}

	@Test
	public void testMatchFunction() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "2", "A2", 3, "B3", 2.5);

		IGenericValue value = parser.executeFormula(new Formula("=MATCH(3, A1:A2, 0)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		Assert.assertEquals(2, value.getNumberValue(), 0.01);
	}

	@Test
	public void testUnaryOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(-1.0, parser.executeFormula(new Formula("=-a1"), SheetUtils.sheetWithCell("A1", "1"), null)
				.getNumberValue());
		Assert.assertEquals(1.0, parser.executeFormula(new Formula("=+a1"), SheetUtils.sheetWithCell("A1", "1"), null)
				.getNumberValue());
		Assert.assertEquals("=+A1", parser.parseFormula(new Formula("=+a1")).getFormula());

	}

	@Test
	public void testPercentOperations() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(13.0,
				parser.executeFormula(new Formula("=10 * a1 % + 3"), SheetUtils.sheetWithCell("A1", 100), null)
						.getNumberValue());

		Assert.assertEquals("=10 * A1% + 3", parser.parseFormula(new Formula("=10 * a1 % + 3")).getFormula());

	}

	@Test
	public void testVector() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals("=SUM(A:A)", parser.parseFormula(new Formula("=sum(A:A)")).getFormula());

		Assert.assertEquals("=SUM(3:3)", parser.parseFormula(new Formula("=sum(3:3)")).getFormula());

	}

	@Test
	public void testBoolean() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(false,
				parser.executeFormula(new Formula("=a1 != true"), SheetUtils.sheetWithCell("A1", true), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals(true,
				parser.executeFormula(new Formula("=a1 != false"), SheetUtils.sheetWithCell("A1", true), null)
						.getBooleanValue().booleanValue());

		Assert.assertEquals("=A1 != true", parser.parseFormula(new Formula("=a1 != true")).getFormula());

	}

	@Test
	public void testParanthesis() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		Assert.assertEquals(1030.0,
				parser.executeFormula(new Formula("=10 * (a1 + 3)"), SheetUtils.sheetWithCell("A1", 100), null)
						.getNumberValue());

		Assert.assertEquals("=10 * (A1 + 3)", parser.parseFormula(new Formula("=10 * (a1 + 3)")).getFormula());

	}
}
