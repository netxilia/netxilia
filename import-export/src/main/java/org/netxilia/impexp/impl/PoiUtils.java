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
package org.netxilia.impexp.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.netxilia.api.display.DefaultStyle;
import org.netxilia.api.display.Style;
import org.netxilia.api.display.Styles;

public class PoiUtils {
	public static final short EXCEL_COLUMN_WIDTH_FACTOR = 256;
	public static final int UNIT_OFFSET_LENGTH = 7;
	public static final int[] UNIT_OFFSET_MAP = new int[] { 0, 36, 73, 109, 146, 182, 219 };

	/** * pixel units to excel width units(units of 1/256th of a character width) * @param pxs * @return */
	public static short pixel2WidthUnits(int pxs) {
		short widthUnits = (short) (EXCEL_COLUMN_WIDTH_FACTOR * (pxs / UNIT_OFFSET_LENGTH));
		widthUnits += UNIT_OFFSET_MAP[(pxs % UNIT_OFFSET_LENGTH)];
		return widthUnits;
	}

	/** * excel width units(units of 1/256th of a character width) to pixel units * @param widthUnits * @return */
	public static int widthUnits2Pixel(int widthUnits) {
		int pixels = (widthUnits / EXCEL_COLUMN_WIDTH_FACTOR) * UNIT_OFFSET_LENGTH;
		int offsetWidthUnits = widthUnits % EXCEL_COLUMN_WIDTH_FACTOR;
		pixels += Math.round(offsetWidthUnits / ((float) EXCEL_COLUMN_WIDTH_FACTOR / UNIT_OFFSET_LENGTH));
		return pixels;
	}

	public static Styles poiStyle2Netxilia(CellStyle poiStyle, Font font, HSSFPalette palette,
			NetxiliaStyleResolver styleResolver) {
		List<Style> entries = new ArrayList<Style>();

		if (!poiStyle.getWrapText()) {
			entries.add(DefaultStyle.nowrap.getStyle());
		}
		// font
		if (font.getItalic()) {
			entries.add(DefaultStyle.italic.getStyle());
		}
		if (font.getStrikeout()) {
			entries.add(DefaultStyle.strikeout.getStyle());
		}
		if (font.getBoldweight() == Font.BOLDWEIGHT_BOLD) {
			entries.add(DefaultStyle.bold.getStyle());
		}
		if (font.getUnderline() != Font.U_NONE) {
			entries.add(DefaultStyle.underline.getStyle());
		}
		// borders
		if (poiStyle.getBorderBottom() != CellStyle.BORDER_NONE) {
			entries.add(DefaultStyle.borderBottom.getStyle());
		}
		if (poiStyle.getBorderLeft() != CellStyle.BORDER_NONE) {
			entries.add(DefaultStyle.borderLeft.getStyle());
		}
		if (poiStyle.getBorderTop() != CellStyle.BORDER_NONE) {
			entries.add(DefaultStyle.borderTop.getStyle());
		}
		if (poiStyle.getBorderRight() != CellStyle.BORDER_NONE) {
			entries.add(DefaultStyle.borderRight.getStyle());
		}
		// align
		switch (poiStyle.getAlignment()) {
		case CellStyle.ALIGN_LEFT:
			entries.add(DefaultStyle.alignLeft.getStyle());
			break;
		case CellStyle.ALIGN_RIGHT:
			entries.add(DefaultStyle.alignRight.getStyle());
			break;
		case CellStyle.ALIGN_CENTER:
			entries.add(DefaultStyle.alignCenter.getStyle());
			break;
		case CellStyle.ALIGN_JUSTIFY:
			entries.add(DefaultStyle.alignJustify.getStyle());
			break;
		}
		if (font != null && font.getColor() != 0) {
			HSSFColor poiForeground = palette.getColor(font.getColor());
			if (poiForeground != null && poiForeground != HSSFColor.AUTOMATIC.getInstance()) {
				Style foregroundDef = styleResolver.approximateForeground(poiForeground.getTriplet()[0],
						poiForeground.getTriplet()[1], poiForeground.getTriplet()[2]);
				if (foregroundDef != null) {
					entries.add(foregroundDef);
				}
			}
		}

		if (poiStyle.getFillForegroundColor() != 0) {
			HSSFColor poiBackground = palette.getColor(poiStyle.getFillForegroundColor());
			if (poiBackground != null && poiBackground != HSSFColor.AUTOMATIC.getInstance()) {
				Style backgroundDef = styleResolver.approximateBackground(poiBackground.getTriplet()[0],
						poiBackground.getTriplet()[1], poiBackground.getTriplet()[2]);
				if (backgroundDef != null) {
					entries.add(backgroundDef);
				}
			}
		}
		return entries.size() > 0 ? Styles.styles(entries) : null;
	}

	public static CellStyle netxiliaStyle2Poi(Styles nxStyle, Workbook workbook) {
		return netxiliaStyle2Poi(nxStyle, workbook, workbook.createCellStyle());
	}

	public static CellStyle netxiliaStyle2Poi(Styles nxStyle, Workbook workbook, CellStyle poiStyle) {
		if (nxStyle == null) {
			return poiStyle;
		}
		poiStyle.setWrapText(nxStyle.contains(DefaultStyle.nowrap.getStyle()));

		// font
		short bold = nxStyle.contains(DefaultStyle.bold.getStyle()) ? Font.BOLDWEIGHT_BOLD : Font.BOLDWEIGHT_NORMAL;
		byte underline = nxStyle.contains(DefaultStyle.underline.getStyle()) ? Font.U_SINGLE : Font.U_NONE;
		boolean italic = nxStyle.contains(DefaultStyle.italic.getStyle());
		boolean strikeout = nxStyle.contains(DefaultStyle.strikeout.getStyle());
		Font defaultFont = workbook.getFontAt(poiStyle.getFontIndex());
		Font font = workbook.findFont(bold, defaultFont.getColor(), defaultFont.getFontHeight(),
				defaultFont.getFontName(), italic, strikeout, defaultFont.getTypeOffset(), underline);
		if (font == null) {
			font = workbook.createFont();
			font.setBoldweight(bold);
			font.setItalic(italic);
			font.setUnderline(underline);
			font.setStrikeout(strikeout);
		}
		poiStyle.setFont(font);

		// borders
		if (nxStyle.contains(DefaultStyle.borderLeft.getStyle())) {
			poiStyle.setBorderLeft(CellStyle.BORDER_THIN);
		}
		if (nxStyle.contains(DefaultStyle.borderRight.getStyle())) {
			poiStyle.setBorderRight(CellStyle.BORDER_THIN);
		}
		if (nxStyle.contains(DefaultStyle.borderTop.getStyle())) {
			poiStyle.setBorderTop(CellStyle.BORDER_THIN);
		}
		if (nxStyle.contains(DefaultStyle.borderBottom.getStyle())) {
			poiStyle.setBorderBottom(CellStyle.BORDER_THIN);
		}

		// align
		if (nxStyle.contains(DefaultStyle.alignLeft.getStyle())) {
			poiStyle.setAlignment(CellStyle.ALIGN_LEFT);
		} else if (nxStyle.contains(DefaultStyle.alignRight.getStyle())) {
			poiStyle.setAlignment(CellStyle.ALIGN_RIGHT);
		} else if (nxStyle.contains(DefaultStyle.alignCenter.getStyle())) {
			poiStyle.setAlignment(CellStyle.ALIGN_CENTER);
		} else if (nxStyle.contains(DefaultStyle.alignJustify.getStyle())) {
			poiStyle.setAlignment(CellStyle.ALIGN_JUSTIFY);
		}

		return poiStyle;
	}
}
