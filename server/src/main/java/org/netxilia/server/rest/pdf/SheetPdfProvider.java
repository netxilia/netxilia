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
package org.netxilia.server.rest.pdf;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.apache.commons.lang.StringUtils;
import org.netxilia.api.INetxiliaSystem;
import org.netxilia.api.display.IStyleService;
import org.netxilia.api.display.Styles;
import org.netxilia.api.exception.NetxiliaBusinessException;
import org.netxilia.api.exception.NetxiliaResourceException;
import org.netxilia.api.model.CellData;
import org.netxilia.api.model.ColumnData;
import org.netxilia.api.model.ISheet;
import org.netxilia.api.model.RowData;
import org.netxilia.api.model.SheetFullName;
import org.netxilia.api.reference.AreaReference;
import org.netxilia.api.reference.CellReference;
import org.netxilia.api.reference.Range;
import org.netxilia.api.user.IUserService;
import org.netxilia.api.utils.Matrix;
import org.netxilia.api.value.RichValue;
import org.springframework.beans.factory.annotation.Autowired;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Cell;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.Table;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This class exports a {@link ISheet} in PDF format.
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
@Produces("application/pdf")
@Provider
public class SheetPdfProvider implements MessageBodyWriter<SheetFullName> {

	@Autowired
	private IStyleService styleService;

	@Autowired
	private INetxiliaSystem workbookProcessor;

	@Autowired
	private IUserService userService;

	public IStyleService getStyleService() {
		return styleService;
	}

	public void setStyleService(IStyleService styleService) {
		this.styleService = styleService;
	}

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

	/**
	 * Initialize the main info holder table.
	 * 
	 * @throws BadElementException
	 *             for errors during table initialization
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	protected Table initTable(ISheet sheet) throws BadElementException, NetxiliaResourceException,
			NetxiliaBusinessException {
		Table tablePDF = new Table(sheet.getDimensions().getNonBlocking().getColumnCount() + 1);
		// tablePDF.setDefaultVerticalAlignment(Element.ALIGN_TOP);
		// tablePDF.setCellsFitPage(true);
		// tablePDF.setWidth(100);

		tablePDF.setPadding(2);
		tablePDF.setSpacing(0);

		return tablePDF;
	}

	/**
	 * Generates the header cells, which persist on every page of the PDF document.
	 * 
	 * @throws BadElementException
	 *             IText exception
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	protected void generateHeaders(ISheet sheet, Table tablePDF, Font font, int columnCount)
			throws BadElementException, NetxiliaResourceException, NetxiliaBusinessException {

		Cell hdrCell = getCell("", font, Element.ALIGN_CENTER, 50);
		hdrCell.setGrayFill(0.9f);
		hdrCell.setHeader(true);
		tablePDF.addCell(hdrCell);

		List<ColumnData> columnData = sheet.receiveColumns(Range.ALL).getNonBlocking();

		for (int i = 0; i < columnCount; ++i) {
			ColumnData column = i < columnData.size() ? columnData.get(i) : null;
			hdrCell = getCell(CellReference.columnLabel(i), font, Element.ALIGN_CENTER, column != null ? column
					.getWidth() : 120);
			hdrCell.setGrayFill(0.9f);
			hdrCell.setHeader(true);
			tablePDF.addCell(hdrCell);

		}
	}

	/**
	 * Generates all the row cells.
	 * 
	 * @throws JspException
	 *             for errors during value retrieving from the table model
	 * @throws BadElementException
	 *             errors while generating content
	 * @throws NetxiliaBusinessException
	 * @throws NetxiliaResourceException
	 */
	protected void generateRows(ISheet sheet, boolean summarySheet, Table tablePDF, Font font, int columnCount)
			throws JspException, BadElementException, NetxiliaResourceException, NetxiliaBusinessException {
		Styles rightAlign = Styles.styles("a-r");
		List<RowData> rowData = sheet.receiveRows(Range.ALL).getNonBlocking();
		List<ColumnData> columnData = sheet.receiveColumns(Range.ALL).getNonBlocking();

		Matrix<CellData> cellData = sheet.receiveCells(AreaReference.ALL).getNonBlocking();
		for (RowData row : rowData) {
			String rowHdr = "";
			if (summarySheet) {
				rowHdr += "S";
			}
			rowHdr += Integer.toString(row.getIndex() + 1);
			Cell rowHdrCell = getCell(rowHdr, font, Element.ALIGN_LEFT, -1);
			rowHdrCell.setGrayFill(0.9f);
			tablePDF.addCell(rowHdrCell);

			int c = 0;

			for (CellData cell : cellData.getRow(row.getIndex())) {
				RichValue formattedValue = styleService.formatCell(sheet.getWorkbook().getId(), cell, rowData.get(cell
						.getReference().getRowIndex()), columnData.get(cell.getReference().getColumnIndex()));
				// TODO check for aligns
				int horizAlign = Element.ALIGN_LEFT;
				if (formattedValue.getStyles() != null && formattedValue.getStyles().contains(rightAlign)) {
					horizAlign = Element.ALIGN_RIGHT;
				}
				Cell pdfCell = getCell(formattedValue.getDisplay(), font, horizAlign, -1);
				tablePDF.addCell(pdfCell);
				c++;
			}
			for (; c < columnCount; ++c) {
				tablePDF.addCell(getCell("", font, Element.ALIGN_LEFT, -1));
			}
		}
	}

	/**
	 * Returns a formatted cell for the given value.
	 * 
	 * @param value
	 *            cell value
	 * @return Cell
	 * @throws BadElementException
	 *             errors while generating content
	 */
	private Cell getCell(String value, Font font, int horizAlign, int width) throws BadElementException {
		Cell cell = new Cell(new Chunk(StringUtils.trimToEmpty(value), font));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(horizAlign);
		cell.setLeading(8);
		if (width > 0) {
			cell.setWidth(width);
		}
		return cell;
	}

	@Override
	public long getSize(SheetFullName sheet, Class<?> clazz, Type type, Annotation[] ann, MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> clazz, Type type, Annotation[] ann, MediaType mediaType) {
		return SheetFullName.class.isAssignableFrom(clazz);
	}

	@Override
	public void writeTo(SheetFullName sheetName, Class<?> clazz, Type type, Annotation[] ann, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream out) throws IOException, WebApplicationException {

		if (sheetName == null) {
			return;
		}

		/**
		 * This is the table, added as an Element to the PDF document. It contains all the data, needed to represent the
		 * visible table into the PDF
		 */
		Table tablePDF;

		/**
		 * The default font used in the document.
		 */
		Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Font.NORMAL, new Color(0, 0, 0));
		ISheet summarySheet = null;
		ISheet sheet = null;
		try {
			sheet = workbookProcessor.getWorkbook(sheetName.getWorkbookId()).getSheet(sheetName.getSheetName());
			try {
				// get the corresponding summary sheet
				SheetFullName summarySheetName = SheetFullName
						.summarySheetName(sheetName, userService.getCurrentUser());
				summarySheet = workbookProcessor.getWorkbook(summarySheetName.getWorkbookId()).getSheet(
						summarySheetName.getSheetName());

			} catch (Exception e) {
				// no summary sheet - go without one
			}

			// Initialize the Document and register it with PdfWriter listener and the OutputStream
			Document document = new Document(PageSize.A4.rotate(), 60, 60, 40, 40);
			document.addCreationDate();
			HeaderFooter footer = new HeaderFooter(new Phrase("", smallFont), true);
			footer.setBorder(Rectangle.NO_BORDER);
			footer.setAlignment(Element.ALIGN_CENTER);

			PdfWriter.getInstance(document, out);

			// Fill the virtual PDF table with the necessary data
			// Initialize the table with the appropriate number of columns
			tablePDF = initTable(sheet);
			// take tha maximum numbers of columns
			int columnCount = sheet.getDimensions().getNonBlocking().getColumnCount();
			if (summarySheet != null) {
				columnCount = Math.max(columnCount, summarySheet.getDimensions().getNonBlocking().getColumnCount());
			}
			generateHeaders(sheet, tablePDF, smallFont, columnCount);

			tablePDF.endHeaders();
			generateRows(sheet, false, tablePDF, smallFont, columnCount);
			if (summarySheet != null) {
				generateRows(summarySheet, true, tablePDF, smallFont, columnCount);
			}

			document.open();
			document.setFooter(footer);
			document.add(tablePDF);
			document.close();

			out.flush();
			out.close();

		} catch (Exception e) {
			throw new IOException(e);
		}
	}
}
