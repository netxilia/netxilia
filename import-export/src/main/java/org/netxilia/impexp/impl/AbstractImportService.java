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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.netxilia.api.exception.NotFoundException;
import org.netxilia.api.exception.StorageException;
import org.netxilia.api.model.IWorkbook;

public class AbstractImportService {
	protected String getNextFreeSheetName(IWorkbook nxWorkbook, String sheetName) throws StorageException,
			NotFoundException {

		if (!nxWorkbook.hasSheet(sheetName)) {
			// the name is not taken
			return sheetName;
		}

		// now try to increment the last number. For "Sheet1" the root is "Sheet" and start is 1. For "Sheet" the root
		// is "Sheet" and start is 0.
		String root = sheetName;
		int start = 0;
		Pattern namePattern = Pattern.compile("\\d+$");
		Matcher m = namePattern.matcher(sheetName);
		if (m.find()) {
			start = Integer.parseInt(m.group(0));
			root = sheetName.substring(0, m.start(0));
		}
		String nextName = null;
		do {
			nextName = root + (++start);
		} while (nxWorkbook.hasSheet(nextName));
		return nextName;
	}
}
