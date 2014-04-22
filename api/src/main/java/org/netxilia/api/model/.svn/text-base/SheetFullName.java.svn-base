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
package org.netxilia.api.model;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.netxilia.api.user.User;

/**
 * 
 * @author <a href='mailto:ax.craciun@gmail.com'>Alexandru Craciun</a>
 * 
 */
public class SheetFullName implements Serializable, Comparable<SheetFullName> {
	public static final Pattern NAME_PATTERN = Pattern.compile("[^\\n'!:]+");

	public static final Pattern NEED_QUOTES_PATTERN = Pattern.compile("\\s+-*");

	public final static String SUMMARY_SUFFIX = "summary";

	/**
	 * this string can be used instead of the full name of the main sheet when referenced in a summary sheet.
	 */
	public final static String ALIAS_MAIN_SHEET = ".";

	public final static String SEPARATOR = "/";

	private static final long serialVersionUID = -1903375002735796251L;
	private final WorkbookId workbookName;
	private final String sheetName;

	public SheetFullName(String fullName) {
		String[] items = fullName.split(SEPARATOR);
		if (items.length != 2) {
			throw new IllegalArgumentException("Format is <workbook>/<sheet>");
		}
		workbookName = new WorkbookId(items[0]);
		sheetName = items[1];
	}

	public SheetFullName(String workbookName, String sheetName) {
		this(new WorkbookId(workbookName), sheetName);
	}

	public SheetFullName(WorkbookId workbookId, String sheetName) {
		if (!isValid(sheetName)) {
			// the - (dot) is only for extension
			throw new IllegalArgumentException("The sheet name is invalid:" + sheetName);
		}
		this.workbookName = workbookId;
		this.sheetName = sheetName;
	}

	public String getWorkbookName() {
		return workbookName.getKey();
	}

	public WorkbookId getWorkbookId() {
		return workbookName;
	}

	public String getSheetName() {
		return sheetName;
	}

	public SheetType getType() {
		if (sheetName.endsWith("." + SUMMARY_SUFFIX)) {
			return SheetType.summary;
		}
		if (sheetName.contains(".")) {
			return SheetType.user;// XXX not sure in fact unless '.' is illegal in the name of the sheet other than the
			// special sheets
		}
		return SheetType.normal;
	}

	public static boolean isValid(String name) {
		return name != null && NAME_PATTERN.matcher(name).matches();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sheetName == null) ? 0 : sheetName.hashCode());
		result = prime * result + ((workbookName == null) ? 0 : workbookName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SheetFullName other = (SheetFullName) obj;
		if (sheetName == null) {
			if (other.sheetName != null) {
				return false;
			}
		} else if (!sheetName.equals(other.sheetName)) {
			return false;
		}
		if (workbookName == null) {
			if (other.workbookName != null) {
				return false;
			}
		} else if (!workbookName.equals(other.workbookName)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(SheetFullName o) {
		int cmp = workbookName.compareTo(o.workbookName);
		if (cmp != 0) {
			return cmp;
		}
		return sheetName.compareTo(o.sheetName);
	}

	@Override
	public String toString() {
		return workbookName + SEPARATOR + sheetName;
	}

	public static String sheetSimpleName(String sheetName, User user) {
		if (sheetName.endsWith("." + SUMMARY_SUFFIX)) {
			return sheetName.substring(0, sheetName.length() - SUMMARY_SUFFIX.length() - 1);
		}
		if (user != null && sheetName.endsWith("." + user.getLogin())) {
			return sheetName.substring(0, sheetName.length() - user.getLogin().length() - 1);
		}
		return sheetName;
	}

	/**
	 * 
	 * @param baseSheetName
	 * @param sheetName
	 * @param user
	 * @return a sheet name relative to the baseSheetName is the parameter sheetName is one of the special sheets
	 */
	public static String relativeName(String baseSheetName, String sheetName, User user) {
		if (SheetFullName.ALIAS_MAIN_SHEET.equals(sheetName)) {
			return SheetFullName.sheetSimpleName(baseSheetName, user);
		}
		return sheetName;
	}

	/**
	 * 
	 * @param baseSheetName
	 * @param sheetName
	 * @param user
	 * @return a sheet name relative to the baseSheetName is the parameter sheetName is one of the special sheets
	 */
	public static SheetFullName relativeName(SheetFullName baseSheetName, String sheetName, User user) {
		if (SheetFullName.ALIAS_MAIN_SHEET.equals(sheetName)) {
			return SheetFullName.mainSheetName(baseSheetName, user);
		}
		return new SheetFullName(baseSheetName.getWorkbookId(), sheetName);
	}

	public static SheetFullName summarySheetName(ISheet sheet, User user) {
		return new SheetFullName(sheet.getWorkbook().getName(), sheetSimpleName(sheet.getName(), user) + "."
				+ SUMMARY_SUFFIX);
	}

	public static SheetFullName privateSheetName(ISheet sheet, User user) {
		return new SheetFullName(sheet.getWorkbook().getName(), sheetSimpleName(sheet.getName(), user) + "."
				+ user.getLogin());
	}

	public static SheetFullName summarySheetName(SheetFullName sheetFullName, User user) {
		return new SheetFullName(sheetFullName.getWorkbookName(), sheetFullName.getSheetName() + "." + SUMMARY_SUFFIX);
	}

	public static SheetFullName mainSheetName(SheetFullName sheetFullName, User user) {
		return new SheetFullName(sheetFullName.getWorkbookName(), sheetSimpleName(sheetFullName.getSheetName(), user));
	}

	public static SheetFullName privateSheetName(SheetFullName sheetFullName, User user) {
		return new SheetFullName(sheetFullName.getWorkbookName(), sheetFullName.getSheetName() + "." + user.getLogin());
	}

	/**
	 * 
	 * @param sheetName
	 * @return the sheet name single-quoted if need (special characters inside).
	 */
	public static String toStringForFormula(String sheetName) {
		if (sheetName == null) {
			return null;
		}
		boolean needQuotes = NEED_QUOTES_PATTERN.matcher(sheetName).find();
		return needQuotes ? "'" + sheetName + "'" : sheetName;
	}
}
