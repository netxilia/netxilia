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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.netxilia.spi.impl.formula.function.FunctionRegistry;
import org.netxilia.spi.impl.formula.parser.ASTFormulaTree;
import org.netxilia.spi.impl.formula.parser.FormulaParser;

/**
 * @author Catalin Cirstoiu
 * 
 */
public class FormulaParserDemo {

	public static void main(String args[]) throws Exception {
		// initialize the function registry
		FunctionRegistry funReg = new FunctionRegistry();

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		while (true) {
			String line;
			System.out.print("Enter an expression like \"-2*sum(3,4)+5\" :");
			line = br.readLine();
			if ((line == null) || (line.trim().length() == 0)) {
				break;
			}
			try {
				FormulaParser nxp = new FormulaParser(new StringReader(line));
				nxp.setFunctionRegistry(funReg);

				ASTFormulaTree n = nxp.buildFormulaTree();
				// XXX - this does not work
				n.dump("", new FormulaContextImpl(null, null), new FormulaRendererImpl());
			} catch (Throwable t) {
				System.out.println("OOOPS: " + t.getMessage());
				// t.printStackTrace();
			}
		}
	}
}
