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
package org.netxilia.spi.impl.storage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.netxilia.api.chart.Chart;
import org.netxilia.api.chart.Title;
import org.netxilia.api.command.CellCommands;
import org.netxilia.api.command.ColumnCommands;
import org.netxilia.api.command.RowCommands;
import org.netxilia.api.command.SheetCommands;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.AlreadyExistsException;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.formula.Formula;
import org.netxilia.api.impl.NetxiliaSystemImpl;
import org.netxilia.api.impl.model.Workbook;
import org.netxilia.api.model.Alias;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetData;
import org.netxilia.api.model.SheetType;
import org.netxilia.api.model.WorkbookId;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.user.AclPrivilegedMode;
import org.netxilia.api.value.BooleanValue;
import org.netxilia.spi.impl.storage.db.DbWorkbookStorageServiceImpl;
import org.netxilia.spi.impl.storage.db.ddl.dialect.MySQLDialect;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbColumn;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbDataType;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbSchema;
import org.netxilia.spi.impl.storage.db.ddl.schema.DbTable;
import org.netxilia.spi.impl.storage.ddl.MockDDLUtils;
import org.netxilia.spi.impl.storage.ddl.MockDDLUtilsFactory;
import org.netxilia.spi.impl.storage.sql.MockResultSet;
import org.netxilia.spi.impl.storage.sql.MockResultSetMetaData;
import org.netxilia.spi.impl.structure.NoCheckAclServiceImpl;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestDbStorage {
	private MockDDLUtils ddlUtils = null;
	private MockConnectionWrapper jdbcOperations = null;
	private NetxiliaSystemImpl nx;
	private ApplicationContext context;

	/************ ASSERTS *****************/
	private void assertQuery(MockConnectionWrapper.ParamQuery query, String sql, Object... params) {
		Assert.assertEquals(sql.trim(), query.getSql().trim());
		Assert.assertArrayEquals(params, query.getArgs());
	}

	private void assertInsertPropsQuery(MockConnectionWrapper.ParamQuery query, int sheetId, String category,
			Object object, String property, String value) {
		assertQuery(query,
				"INSERT INTO main_props (sheet_id, category, object, property, value, big_value) VALUES (?,?,?,?,?,?)",
				sheetId, category, object.toString(), property, value, null);
	}

	private void assertUpdatePropsQuery(MockConnectionWrapper.ParamQuery query, int sheetId, String category,
			Object object, String property, String value) {
		assertQuery(
				query,
				"UPDATE main_props SET value = ?, big_value = ? WHERE sheet_id = ? AND category = ? AND object = ? AND property = ?",
				value, null, sheetId, category, object.toString(), property);
	}

	private void assertDeletePropsQuery(MockConnectionWrapper.ParamQuery query, int sheetId, String category) {
		assertQuery(query, "DELETE FROM main_props WHERE sheet_id = ? AND category = ?", sheetId, category);
	}

	private void assertDeletePropsQuery(MockConnectionWrapper.ParamQuery query, int sheetId, String category,
			Object objectId) {
		assertQuery(query, "DELETE FROM main_props WHERE sheet_id = ? AND category = ? AND object = ?", sheetId,
				category, objectId);
	}

	private void assertCreateProps(String sql) {
		Assert.assertEquals(
				"CREATE TABLE main_props(sheet_id INTEGER NOT NULL, category VARCHAR(55) NOT NULL, object VARCHAR(150) NOT NULL, property VARCHAR(55) NOT NULL, "
						+ "value VARCHAR(150) DEFAULT NULL, big_value CLOB DEFAULT NULL, CONSTRAINT pk_main_props PRIMARY KEY(sheet_id, category, object, property))",
				sql);

	}

	/*************** JDBC operations builders *************/
	private void addPropSet(DbSchema schema, Integer sheetId, String category, Object[][] rows) {
		addPropSet(schema, sheetId, category, rows, null);
	}

	private void addPropSet(DbSchema schema, Integer sheetId, String category, Object[][] rows, Range records) {
		ResultSet propSet = MockResultSet.create(MockResultSetMetaData.create(schema.getTable("main_props")), rows);
		if (sheetId != null) {
			if (records != null) {
				jdbcOperations
						.addResponse(
								new MockConnectionWrapper.ParamQuery(
										"SELECT * FROM main_props WHERE sheet_id = ? AND category = ? order by category, object, property LIMIT ? OFFSET ?",
										new Object[] { sheetId, category, records.count(), records.getMin() }), propSet);
			} else {
				jdbcOperations
						.addResponse(
								new MockConnectionWrapper.ParamQuery(
										"SELECT * FROM main_props WHERE sheet_id = ? AND category = ? order by category, object, property",
										new Object[] { sheetId, category }), propSet);
			}
		} else {
			jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
					"SELECT * FROM main_props WHERE category = ? order by category, object, property",
					new Object[] { category }), propSet);
		}
	}

	private void addPropSet(DbSchema schema, Integer sheetId, String category, String property, Object[]... rows) {
		ResultSet propSet = MockResultSet.create(MockResultSetMetaData.create(schema.getTable("main_props")), rows);
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
				"SELECT * FROM main_props WHERE sheet_id = ? AND category = ? AND object = ? AND property = ?",
				new Object[] { sheetId, category, sheetId, property }), propSet);

	}

	private void addPropSetIn(DbSchema schema, Integer sheetId, String category, Object objectId, Object[]... rows) {
		ResultSet propSet = MockResultSet.create(MockResultSetMetaData.create(schema.getTable("main_props")), rows);
		jdbcOperations
				.addResponse(
						new MockConnectionWrapper.ParamQuery(
								"SELECT * FROM main_props WHERE sheet_id = ? AND category = ? AND object IN (?) order by category, object, property",
								new Object[] { sheetId, category, objectId }), propSet);

	}

	private void addCountProps(Integer sheetId, String category, String property, int count) {
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
				"SELECT COUNT(*) FROM main_props WHERE sheet_id = ? AND category = ? AND property = ?", new Object[] {
						sheetId, category, property }), MockResultSet.create(count));
	}

	private void addSheetId(String sheetName, int sheetId) {
		jdbcOperations.addResponse(
				new MockConnectionWrapper.ParamQuery(
						"SELECT sheet_id FROM main_props WHERE category = ? AND value = ?", new Object[] { "sheet",
								sheetName }), MockResultSet.create(sheetId));
	}

	private void addSheet(DbSchema schema, String sheetName, int sheetId, SheetType type) {
		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ sheetId, "sheet", String.valueOf(sheetId), "name", sheetName, null }, //
				{ sheetId, "sheet", String.valueOf(sheetId), "type", type.name(), null }, //
				{ sheetId, "sheet", String.valueOf(sheetId), "dbTableName", "main_" + sheetId + "_data", null } };

		addPropSet(schema, sheetId, "sheet", sheetDescRows);
		addPropSet(schema, null, "sheet", sheetDescRows);
	}

	private void addMaxSheetId() {
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT MAX(sheet_id) FROM main_props",
				new Object[] {}), MockResultSet.create(0));

	}

	private void addMaxRowId(int sheetId) {
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
				"SELECT MAX(object) FROM main_props WHERE sheet_id = ? AND category = ?",
				new Object[] { sheetId, "row" }), MockResultSet.create(0));
	}

	private void addRowId(int sheetId, int row) {
		String tableName = "main_" + sheetId + "_data";
		ResultSet rs = MockResultSet.create(MockResultSetMetaData.create(new String[] { "id", "order_by" }),
				new Object[][] { new Object[] { row, (float) row } });
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT id, order_by FROM " + tableName
				+ " ORDER BY order_by LIMIT ? OFFSET ?", new Object[] { 1, row }), rs);
	}

	private void addEmptyRow(DbSchema schema, int sheetId, int row, SheetType sheetType) {
		addPropSet(schema, sheetId, "row", new Object[][] {}, null);
		if (sheetType == SheetType.normal) {
			String tableName = "main_" + sheetId + "_data";
			jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT id, order_by FROM " + tableName
					+ " ORDER BY order_by LIMIT ? OFFSET ?", new Object[] { 1, row }), MockResultSet.create());

		} else {
			addPropSet(schema, sheetId, "row", new Object[][] {}, Range.range(row));
		}
	}

	private Workbook loadWorkbook(String workbookId) throws StorageException, NotFoundException {
		return nx.getWorkbook(new WorkbookId(workbookId));
	}

	@Before
	public void setUp() {
		AclPrivilegedMode.set();

		ddlUtils = newDDLUtils(new MySQLDialect().getDbDialect());
		jdbcOperations = new MockConnectionWrapper();

		context = new ClassPathXmlApplicationContext("classpath:test-domain-services.xml");
		nx = context.getBean(NetxiliaSystemImpl.class);

		DbWorkbookStorageServiceImpl storage = (DbWorkbookStorageServiceImpl) context.getBean("dbStorageService");
		MockDDLUtilsFactory factory = new MockDDLUtilsFactory();
		factory.setDdlUtils(ddlUtils);
		MockConnectionWrapperFactory connectionWrapperFactory = new MockConnectionWrapperFactory();
		connectionWrapperFactory.setConnectionWrapper(jdbcOperations);
		storage.setDdlUtilsFactory(factory);
		storage.setConnectionWrapperFactory(connectionWrapperFactory);

		nx.setStorageService(storage);
		nx.setSheetInitializationEnabled(false);
		nx.setAclService(new NoCheckAclServiceImpl());

	}

	@Test
	public void testCreateSheet() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();

		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);
		Assert.assertNotNull(sh);
		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(2, queries.size());
		// sheet's data table created
		System.out.println(queries);
		// properties table
		assertCreateProps(queries.get(0));
		Assert.assertEquals(
				"CREATE TABLE main_1_data(id INTEGER NOT NULL, order_by DECIMAL NOT NULL, CONSTRAINT pk_main_1_data PRIMARY KEY(id))",
				queries.get(1));

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(3, updateQueries.size());
		assertInsertPropsQuery(updateQueries.get(0), 1, "sheet", "1", "name", "Sheet1");
		assertInsertPropsQuery(updateQueries.get(1), 1, "sheet", "1", "type", "normal");
		assertInsertPropsQuery(updateQueries.get(2), 1, "sheet", "1", "dbTableName", "main_1_data");

	}

	private DbSchema prepareEmptySheet(String sheetName, int sheetId, SheetType sheetType) {
		// prepare the queries for empty sheet
		String tableName = "main_" + sheetId + "_data";

		DbSchema schema = loadSchema("main", sheetId, 0, true);
		addSheetId(sheetName, sheetId);
		addSheet(schema, sheetName, sheetId, sheetType);
		// row count and max id
		if (sheetType == SheetType.normal) {
			jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT COUNT(*) FROM " + tableName),
					MockResultSet.create(0));
			jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT MAX(id) FROM " + tableName),
					MockResultSet.create(0));
		} else {
			addCountProps(sheetId, "row", "orderBy", 0);
			addMaxRowId(sheetId);
		}

		addPropSet(schema, 1, "sheet", "columnsStorage");
		addPropSet(schema, 1, "sheet", "columns");
		addPropSet(schema, 1, "cells", new Object[][] {});

		// XXX - weird
		addEmptyRow(schema, sheetId, 2, sheetType);

		ddlUtils.reader().setSchema(schema);
		return schema;
	}

	@Test
	public void testSetCellSheet() throws NetxiliaResourceException, NetxiliaBusinessException {
		addMaxSheetId();

		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);
		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		prepareEmptySheet("Sheet1", 1, SheetType.normal);

		// add cell
		sh.sendValue(new CellReference(null, 2, 1), new BooleanValue(Boolean.FALSE)).getNonBlocking();

		List<String> queries = ddlUtils.writer().getExecutedQueries();

		Assert.assertEquals(2, queries.size());
		// add column 0
		Assert.assertEquals("ALTER TABLE main_1_data ADD COLUMN COL0 VARCHAR(255) DEFAULT NULL", queries.get(0));
		// add column 1
		Assert.assertEquals("ALTER TABLE main_1_data ADD COLUMN COL1 VARCHAR(255) DEFAULT NULL", queries.get(1));

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();

		System.out.println(updateQueries);
		// check - the add rows queries
		Assert.assertEquals(10, updateQueries.size());

		// insert column 0 and 1
		assertUpdatePropsQuery(updateQueries.get(0), 1, "sheet", 1, "columnsStorage",
				"[{\"dbColumnName\":\"COL0\"},{\"dbColumnName\":\"COL1\"}]");

		assertInsertPropsQuery(updateQueries.get(1), 1, "sheet", 1, "columnsStorage",
				"[{\"dbColumnName\":\"COL0\"},{\"dbColumnName\":\"COL1\"}]");

		// insert row 0
		assertQuery(updateQueries.get(2), "INSERT INTO main_1_data (id, order_by) VALUES (?, ?)", 1, 1.0f);

		// insert row 1
		assertQuery(updateQueries.get(3), "INSERT INTO main_1_data (id, order_by) VALUES (?, ?)", 2, 2.0f);

		// insert row 2
		assertQuery(updateQueries.get(4), "INSERT INTO main_1_data (id, order_by) VALUES (?, ?)", 3, 3.0f);

		// update cell
		assertQuery(updateQueries.get(5), "UPDATE main_1_data SET COL1 = ? WHERE id = ?", "false", 3);

		// insert cell's type
		assertInsertPropsQuery(updateQueries.get(9), 1, "cells", "B3:B3", "type", "BOOLEAN");

	}

	@Test
	public void testSetCellProperties() throws NetxiliaResourceException, NetxiliaBusinessException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);

		DbSchema schema = prepareEmptySheet("Sheet1", 1, SheetType.normal);

		CellReference ref = new CellReference(null, 2, 1);
		// add cell
		sh.sendValue(ref, new BooleanValue(Boolean.FALSE)).getNonBlocking();

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		// the db should return inserted data
		// ---- 2 columns
		Object[][] columnRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'COL0'},{dbColumnName:'COL1'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnRows);
		// ----- 3rd row
		addRowId(1, 2);

		// now set data
		sh.sendFormula(ref, new Formula("=1 + 2"));
		sh.sendCommand(CellCommands.styles(new AreaReference(ref), Styles.styles("b")));

		List<String> queries = ddlUtils.writer().getExecutedQueries();

		Assert.assertEquals(0, queries.size());

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();

		System.out.println(updateQueries);
		Assert.assertEquals(4, updateQueries.size());

		// insert new type
		assertInsertPropsQuery(updateQueries.get(1), 1, "cells", "B3:B3", "type", "NUMBER");

		// insert formula
		assertInsertPropsQuery(updateQueries.get(2), 1, "cells", "B3:B3", "formula", "=1 + 2");

		// insert style
		assertInsertPropsQuery(updateQueries.get(3), 1, "cells", "B3:B3", "styles", "b");

	}

	@Test
	public void testGetCellNormalSheet() throws NetxiliaResourceException, NetxiliaBusinessException {

		DbSchema schema = loadSchema("main", 1, 2, true);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1", null }, //
				{ 1, "sheet", "1", "type", "normal", null }, //
				{ 1, "sheet", "1", "dbTableName", "main_1_data", null } };

		addPropSet(schema, 1, "sheet", sheetDescRows);
		addPropSet(schema, null, "sheet", sheetDescRows);

		// add column storage
		Object[][] columnRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'COL0'},{dbColumnName:'COL1'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnRows);

		// add cell value data
		Object[][] dataRows = { { 2, 3.0, null, "false" } };
		ResultSet dataSet = MockResultSet
				.create(MockResultSetMetaData.create(schema.getTable("main_1_data")), dataRows);
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
				"SELECT * FROM main_1_data ORDER BY order_by LIMIT ? OFFSET ?", new Object[] { 1, 2 }), dataSet);

		// add cell props data
		// cells B2:B2 formula =a2 + 10
		Object[][] propRows = { { 1, "cells", "B3:B3", "styles", "b", null },
				{ 1, "cells", "B3:B3", "type", "BOOLEAN", null } };
		addPropSet(schema, 1, "cells", propRows);

		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT COUNT(*) FROM main_1_data"),
				MockResultSet.create(3));

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1");

		Assert.assertNotNull(sh);

		// add cell
		CellData cell = sh.receiveCell(new CellReference(null, 2, 1)).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertNotNull(cell.getValue());
		Assert.assertEquals(new BooleanValue(Boolean.FALSE), cell.getValue());
		Assert.assertEquals(Styles.styles("b"), cell.getStyles());
		// cell.setFormula(new Formula("=1 + 2"));
	}

	@Test
	public void testGetCellSuppSheet() throws NetxiliaResourceException, NetxiliaBusinessException {
		DbSchema schema = loadSchema("main", 1, 2, false);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1.user", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1.user", null }, //
				{ 1, "sheet", "1", "type", "user", null }, //
				{ 1, "sheet", "1", "dbTableName", "none", null } };

		addPropSet(schema, 1, "sheet", sheetDescRows);
		addPropSet(schema, null, "sheet", sheetDescRows);

		// add column storage
		Object[][] columnRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'none'},{dbColumnName:'none'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnRows);

		// add rows data
		Object[][] rowRows = { { 1, "row", "0", "orderBy", "1", null }, //
				{ 1, "row", "1", "orderBy", "2", null },//
				{ 1, "row", "2", "orderBy", "3", null } };
		addPropSet(schema, 1, "row", rowRows);
		// row count
		addCountProps(1, "row", "orderBy", 3);

		// add cell props and value data
		// cells B2:B2 formula =a2 + 10
		Object[][] propRows = { { 1, "cells", "B3:B3", "styles", "b", null }, //
				{ 1, "cells", "B3:B3", "value", "false", null },//
				{ 1, "cells", "B3:B3", "type", "BOOLEAN", null } };
		addPropSet(schema, 1, "cells", propRows);

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1.user");

		Assert.assertNotNull(sh);

		// add cell
		CellData cell = sh.receiveCell(new CellReference(2, 1)).getNonBlocking();
		Assert.assertNotNull(cell);
		Assert.assertNotNull(cell.getValue());
		Assert.assertEquals(new BooleanValue(Boolean.FALSE), cell.getValue());
		Assert.assertEquals(Styles.styles("b"), cell.getStyles());
		// cell.setFormula(new Formula("=1 + 2"));

	}

	@Test
	public void testDeleteNormalSheet() throws NetxiliaResourceException, NetxiliaBusinessException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);

		DbSchema schema = prepareEmptySheet("Sheet1", 1, SheetType.normal);

		CellReference ref = new CellReference(null, 2, 1);
		// add cell
		sh.sendValue(ref, new BooleanValue(Boolean.FALSE)).getNonBlocking();

		// the db should return inserted data
		// ---- 2 columns
		Object[][] columnRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'COL0'},{dbColumnName:'COL1'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnRows);
		// ----- 3rd row
		addRowId(1, 2);

		// add cell
		sh.sendFormula(ref, new Formula("=1 + 2"));
		sh.sendCommand(CellCommands.styles(new AreaReference(ref), Styles.styles("b")));

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();
		book.deleteSheet(sh);

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(1, queries.size());

		// drop data table
		Assert.assertEquals("DROP TABLE main_1_data", queries.get(0));

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(3, updateQueries.size());

		// delete sheet data
		assertDeletePropsQuery(updateQueries.get(0), 1, "sheet", "1");

		// delete rows properties
		assertDeletePropsQuery(updateQueries.get(1), 1, "row");

		// delete cells properties
		assertDeletePropsQuery(updateQueries.get(2), 1, "cells");

	}

	@Test
	public void testDeleteSuppSheet() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.summary);

		prepareEmptySheet("Sheet1", 1, SheetType.summary);
		// add cell
		CellReference ref = new CellReference(null, 2, 1);
		sh.sendFormula(ref, new Formula("=1 + 2"));
		sh.sendCommand(CellCommands.styles(new AreaReference(ref), Styles.styles("b")));

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();
		book.deleteSheet(sh);

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(0, queries.size());

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(3, updateQueries.size());

		// delete sheet data
		assertDeletePropsQuery(updateQueries.get(0), 1, "sheet", "1");

		// delete rows properties
		assertDeletePropsQuery(updateQueries.get(1), 1, "row");

		// delete cells properties
		assertDeletePropsQuery(updateQueries.get(2), 1, "cells");

	}

	@Test
	public void testGetSheetProperties() throws NetxiliaResourceException, NetxiliaBusinessException {

		DbSchema schema = loadSchema("main", 1, 2, false);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1", null }, //
				{ 1, "sheet", "1", "type", "normal", null }, //
				{ 1, "sheet", "1", "dbTableName", "main_1_data", null },//
				{ 1, "sheet", "1", "aliases", "{\"myalias\":\"B1:C2\"}", null }, //
				{ 1, "sheet", "1", "charts", "[{\"title\":{\"text\":\"chart_title\"}}]", null } };

		addPropSet(schema, null, "sheet", sheetDescRows);
		addPropSet(schema, 1, "sheet", sheetDescRows);

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1");

		Assert.assertNotNull(sh);

		SheetData sheetData = sh.receiveSheet().getNonBlocking();
		// aliases
		Assert.assertNotNull(sheetData.getAliases());
		Assert.assertEquals(new AreaReference("B1:C2"), sheetData.resolveAlias(new Alias("myalias")));

		// charts
		Assert.assertNotNull(sheetData.getCharts());
		Assert.assertEquals(1, sheetData.getCharts().size());
		Assert.assertNotNull(sheetData.getCharts().get(0));
		Assert.assertEquals("chart_title", sheetData.getCharts().get(0).getTitle().getText());

	}

	@Test
	public void testSetSheetProperties() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		prepareEmptySheet("Sheet1", 1, SheetType.normal);

		sh.sendCommand(SheetCommands.setAlias(new Alias("myalias"), new AreaReference("B1:C2")));
		Chart chart = new Chart();
		chart.setTitle(new Title("chart_title"));
		sh.sendCommand(SheetCommands.addChart(chart));

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(0, queries.size());

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(4, updateQueries.size());

		// alias
		assertUpdatePropsQuery(updateQueries.get(0), 1, "sheet", 1, "aliases", "{\"myalias\":\"B1:C2\"}");
		assertInsertPropsQuery(updateQueries.get(1), 1, "sheet", 1, "aliases", "{\"myalias\":\"B1:C2\"}");

		// chart
		assertUpdatePropsQuery(updateQueries.get(2), 1, "sheet", 1, "charts",
				"[{\"title\":{\"text\":\"chart_title\"},\"height\":200,\"width\":500,\"top\":0,\"left\":0}]");
		assertInsertPropsQuery(updateQueries.get(3), 1, "sheet", 1, "charts",
				"[{\"title\":{\"text\":\"chart_title\"},\"height\":200,\"width\":500,\"top\":0,\"left\":0}]");

	}

	@Test
	public void testSetColumnProperties() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		prepareEmptySheet("Sheet1", 1, SheetType.normal);

		sh.sendCommand(ColumnCommands.styles(Range.range(1), Styles.styles("b")));

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(2, queries.size());
		Assert.assertEquals("ALTER TABLE main_1_data ADD COLUMN COL0 VARCHAR(255) DEFAULT NULL", queries.get(0));
		Assert.assertEquals("ALTER TABLE main_1_data ADD COLUMN COL1 VARCHAR(255) DEFAULT NULL", queries.get(1));

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(4, updateQueries.size());

		assertUpdatePropsQuery(updateQueries.get(0), 1, "sheet", 1, "columnsStorage",
				"[{\"dbColumnName\":\"COL0\"},{\"dbColumnName\":\"COL1\"}]");

		assertInsertPropsQuery(updateQueries.get(1), 1, "sheet", 1, "columnsStorage",
				"[{\"dbColumnName\":\"COL0\"},{\"dbColumnName\":\"COL1\"}]");

		assertUpdatePropsQuery(updateQueries.get(2), 1, "sheet", 1, "columns",
				"[{\"width\":0},{\"width\":0,\"styles\":\"b\"}]");
		assertInsertPropsQuery(updateQueries.get(3), 1, "sheet", 1, "columns",
				"[{\"width\":0},{\"width\":0,\"styles\":\"b\"}]");
	}

	@Test
	public void testGetColumnProperties() throws NetxiliaResourceException, NetxiliaBusinessException {

		DbSchema schema = loadSchema("main", 1, 2, false);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1", null }, //
				{ 1, "sheet", "1", "type", "normal", null }, //
				{ 1, "sheet", "1", "dbTableName", "main_1_data", null } };

		addPropSet(schema, 1, "sheet", sheetDescRows);
		addPropSet(schema, null, "sheet", sheetDescRows);

		// add column storage
		Object[][] columnStorageRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'COL0'},{dbColumnName:'COL1'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnStorageRows);

		// add column
		Object[][] columnRows = { //
		{ 1, "sheet", "1", "columns", "[{},{styles:'b'}]", null } };
		addPropSet(schema, 1, "sheet", "columns", columnRows);

		// add row count
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT COUNT(*) FROM main_1_data"),
				MockResultSet.create(0));

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1");

		Assert.assertNotNull(sh);

		// style
		Assert.assertEquals(2, sh.getDimensions().getNonBlocking().getColumnCount());

		ColumnData column = sh.receiveColumn(1).getNonBlocking();
		Assert.assertNotNull(column);
		Assert.assertEquals(Styles.styles("b"), column.getStyles());
	}

	@Test
	public void testGetRowProperties() throws NetxiliaResourceException, NetxiliaBusinessException {
		DbSchema schema = loadSchema("main", 1, 2, true);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1", null }, //
				{ 1, "sheet", "1", "type", "normal", null }, //
				{ 1, "sheet", "1", "dbTableName", "main_1_data", null } };

		addPropSet(schema, null, "sheet", sheetDescRows);
		addPropSet(schema, 1, "sheet", sheetDescRows);

		// add row data
		Object[][] dataRows = { { 1, 2.0, null, null } };
		ResultSet dataSet = MockResultSet
				.create(MockResultSetMetaData.create(schema.getTable("main_1_data")), dataRows);
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery(
				"SELECT id, order_by FROM main_1_data ORDER BY order_by LIMIT ? OFFSET ?", new Object[] { 1, 1 }),
				dataSet);

		// add row count
		jdbcOperations.addResponse(new MockConnectionWrapper.ParamQuery("SELECT COUNT(*) FROM main_1_data"),
				MockResultSet.create(2));

		// add column storage - needed for sheet dimensions
		Object[][] columnStorageRows = { //
		{ 1, "sheet", "1", "columnsStorage", "[{dbColumnName:'COL0'},{dbColumnName:'COL1'}]", null } };
		addPropSet(schema, 1, "sheet", "columnsStorage", columnStorageRows);

		// add row props
		Object[][] propRows = { { 1, "row", "1", "styles", "b", null } };
		addPropSetIn(schema, 1, "row", 1, propRows);

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1");
		Assert.assertNotNull(sh);

		// style
		RowData row = sh.receiveRow(1).getNonBlocking();

		Assert.assertEquals(2, sh.getDimensions().getNonBlocking().getRowCount());
		Assert.assertNotNull(row);
		Assert.assertEquals(Styles.styles("b"), row.getStyles());
	}

	@Test
	public void testGetOtherRowProperties() throws NetxiliaResourceException, NetxiliaBusinessException {

		DbSchema schema = loadSchema("main", 1, 0, false);
		ddlUtils.reader().setSchema(schema);

		addSheetId("Sheet1.user", 1);

		// add sheet desc data
		Object[][] sheetDescRows = { //
		{ 1, "sheet", "1", "name", "Sheet1.user", null }, //
				{ 1, "sheet", "1", "type", "user", null }, //
				{ 1, "sheet", "1", "dbTableName", "none", null } };

		addPropSet(schema, null, "sheet", sheetDescRows);
		addPropSet(schema, 1, "sheet", sheetDescRows);

		// add row storage data
		Object[][] propStorageRows = { //
		{ 1, "row", "1", "orderBy", "2.0", null } //
		};
		addPropSet(schema, 1, "row", propStorageRows, Range.range(1));

		// add row props
		Object[][] propRows = { { 1, "row", "1", "styles", "b", null } };
		addPropSetIn(schema, 1, "row", 1, propRows);

		// row count
		addCountProps(1, "row", "orderBy", 2);

		// add column storage - needed for sheet dimensions
		Object[][] columnStorageRows = { //
		};
		addPropSet(schema, 1, "sheet", "columnsStorage", columnStorageRows);

		Workbook book = loadWorkbook("main");
		ISheet sh = book.getSheet("Sheet1.user");

		Assert.assertNotNull(sh);

		RowData row = sh.receiveRow(1).getNonBlocking();

		Assert.assertEquals(2, sh.getDimensions().getNonBlocking().getRowCount());
		Assert.assertNotNull(row);
		Assert.assertEquals(Styles.styles("b"), row.getStyles());
	}

	@Test
	public void testSetRowProperties() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.normal);

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		// prepare db
		DbSchema schema = prepareEmptySheet("Sheet1", 1, SheetType.normal);
		addEmptyRow(schema, 1, 1, SheetType.normal);

		sh.sendCommand(RowCommands.styles(Range.range(1), Styles.styles("b")));

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(0, queries.size());

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(4, updateQueries.size());

		// insert row 0
		assertQuery(updateQueries.get(0), "INSERT INTO main_1_data (id, order_by) VALUES (?, ?)", 1, 1.0f);

		// insert row 1
		assertQuery(updateQueries.get(1), "INSERT INTO main_1_data (id, order_by) VALUES (?, ?)", 2, 2.0f);

		// tentative update
		assertUpdatePropsQuery(updateQueries.get(2), 1, "row", "2", "styles", "b");
		// insert
		assertInsertPropsQuery(updateQueries.get(3), 1, "row", "2", "styles", "b");
	}

	@Test
	public void testSetOtherRowProperties() throws StorageException, NotFoundException, AlreadyExistsException {
		addMaxSheetId();
		Workbook book = loadWorkbook("main");
		ISheet sh = book.addNewSheet("Sheet1", SheetType.user);
		DbSchema schema = prepareEmptySheet("Sheet1", 1, SheetType.user);
		addEmptyRow(schema, 1, 1, SheetType.user);

		// clean up tracking lists
		ddlUtils.writer().getExecutedQueries().clear();
		jdbcOperations.getUpdateQueries().clear();

		sh.sendCommand(RowCommands.styles(Range.range(1), Styles.styles("b")));

		List<String> queries = ddlUtils.writer().getExecutedQueries();
		Assert.assertEquals(0, queries.size());

		List<MockConnectionWrapper.ParamQuery> updateQueries = jdbcOperations.getUpdateQueries();
		Assert.assertEquals(4, updateQueries.size());

		// insert row 0
		assertInsertPropsQuery(updateQueries.get(0), 1, "row", "1", "orderBy", "1.0");

		// insert row 1
		assertInsertPropsQuery(updateQueries.get(1), 1, "row", "2", "orderBy", "2.0");

		// tentative update
		assertUpdatePropsQuery(updateQueries.get(2), 1, "row", "2", "styles", "b");
		// insert
		assertInsertPropsQuery(updateQueries.get(3), 1, "row", "2", "styles", "b");
	}

	private MockDDLUtils newDDLUtils(String dialect) {
		try {
			return new MockDDLUtils(dialect);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private DbSchema loadSchema(String workbookName, int sheetId, int columns, boolean withData) {
		DbSchema schema = new DbSchema();

		if (withData) {
			// cell data table
			DbTable dataTable = new DbTable((DbTable) context.getBean("table-values"));
			dataTable.setName(workbookName + "_" + sheetId + dataTable.getName());
			// add supp columns
			for (int i = 0; i < columns; ++i) {
				dataTable.addColumn(buildDbColumn(i));
			}
			schema.addTable(dataTable);
		}

		// props table
		DbTable propsTable = new DbTable((DbTable) context.getBean("table-properties"));
		propsTable.setName(workbookName + propsTable.getName());
		schema.addTable(propsTable);

		return schema;
	}

	private DbColumn buildDbColumn(int id) {
		DbColumn col = new DbColumn();
		col.setName("COL" + id);
		col.setPrimaryKey(false);
		col.setDataType(DbDataType.VARCHAR);
		col.setSize(255);
		// col.setDefaultValue(colTempl.getDefaultValue());
		return col;
	}

}
