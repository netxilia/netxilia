package org.netxilia.spi.impl.formula;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.formula.FormulaParsingException;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.value.ErrorValue;
import org.netxilia.api.value.ErrorValueType;
import org.netxilia.api.value.GenericValueType;
import org.netxilia.api.value.IGenericValue;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.netxilia.spi.impl.structure.SheetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestAliases {
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
	public void testShortReferenceAndAliasMainSheetName() throws FormulaParsingException, AlreadyExistsException,
			StorageException, NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();
		ISheet sheet = SheetUtils.sheetWithCell(context.getBean(NetxiliaSystemImpl.class), "A1", "100", "A2", "cde",
				"A3", "abc");
		sheet.sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("A1:A1")));
		ISheet sheet2 = sheet.getWorkbook().addNewSheet("test.summary", SheetType.summary);

		Assert.assertEquals(110.0, parser.executeFormula(new Formula("=test!Alias + 10"), sheet2, null)
				.getNumberValue(), 0.001);

		Assert.assertEquals(110.0, parser.executeFormula(new Formula("=.!Alias + 10"), sheet2, null).getNumberValue(),
				0.001);
	}

	@Test
	public void testAlias() throws FormulaParsingException, AlreadyExistsException, StorageException, NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "1", "A2", "cde", "A3", "abc");
		sheet.sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("A1:A1")));

		IGenericValue value = parser.executeFormula(new Formula("=Alias + 3"), sheet, new CellReference("B1"));
		Assert.assertNotNull(value);
		Assert.assertEquals(4, value.getNumberValue(), 0.1);

	}

	@Test
	public void testInvalidAlias() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "1", "A2", "cde", "A3", "abc");

		IGenericValue value = parser.executeFormula(new Formula("=Alias + 3"), sheet, new CellReference("B1"));
		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.ERROR, value.getValueType());
		Assert.assertEquals(ErrorValueType.NAME, ((ErrorValue) value).getErrorType());
	}

	@Test
	public void testAliasArea() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "1", "A2", "2", "A3", "abc");
		sheet.sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("A1:A2")));

		IGenericValue value = parser.executeFormula(new Formula("=Alias + 3"), sheet, new CellReference("B2"));
		Assert.assertNotNull(value);
		Assert.assertEquals(5, value.getNumberValue(), 0.1);

		// use context reference
		value = parser.executeFormula(new Formula("=Alias + 3"), sheet, new CellReference("B3"));
		Assert.assertNotNull(value);
		// take first cell
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		// Assert.assertEquals(ErrorValueType.NAME, ((ErrorValue) value).getErrorType());
		Assert.assertEquals(4, value.getNumberValue(), 0.1);

		// use row index
		value = parser.executeFormula(new Formula("=Alias 1 + 3"), sheet, new CellReference("B3"));
		Assert.assertNotNull(value);
		Assert.assertEquals(4, value.getNumberValue(), 0.1);

	}

	@Test
	public void testAliasInfiniteArea() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "1", "A2", "2", "A3", "abc");
		sheet.sendCommand(SheetCommands.setAlias(new Alias("Alias"), new AreaReference("A:A")));

		// use row index
		IGenericValue value = parser.executeFormula(new Formula("=Alias 1 + 3"), sheet, new CellReference("B3"));
		Assert.assertNotNull(value);
		Assert.assertEquals(4, value.getNumberValue(), 0.1);

		// use the same row index
		value = parser.executeFormula(new Formula("=Alias + 3"), sheet, new CellReference("B2"));
		Assert.assertNotNull(value);
		Assert.assertEquals(5, value.getNumberValue(), 0.1);

		value = parser.executeFormula(new Formula("=sum(Alias) + 3"), sheet, new CellReference("B2"));
		Assert.assertNotNull(value);
		Assert.assertEquals(6, value.getNumberValue(), 0.1);

	}

	@Test
	public void testFunctionWithAreas() throws FormulaParsingException, AlreadyExistsException, StorageException,
			NotFoundException {
		JavaCCFormulaParserImpl parser = getParser();

		ISheet sheet = SheetUtils.sheetWithCell("A1", "2", "A2", 3, "B3", 2.5);

		// with IGenericValue
		IGenericValue value = parser.executeFormula(new Formula("=SUM(A1:B3, 10)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		Assert.assertEquals(17.5, value.getNumberValue(), 0.1);

		// with IGenericValue
		value = parser.executeFormula(new Formula("=SUM(A:B, 10)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		Assert.assertEquals(17.5, value.getNumberValue(), 0.1);

		// with IGenericValue
		value = parser.executeFormula(new Formula("=SUM(A1, A2, 10)"), sheet, null);

		Assert.assertNotNull(value);
		Assert.assertEquals(GenericValueType.NUMBER, value.getValueType());
		Assert.assertEquals(15.0, value.getNumberValue(), 0.1);

	}
}
