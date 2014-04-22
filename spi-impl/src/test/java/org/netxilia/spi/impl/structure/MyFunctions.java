package org.netxilia.spi.impl.structure;

import org.netxilia.spi.formula.Function;
import org.netxilia.spi.formula.Functions;

@Functions
public class MyFunctions {
	private static int testValue = 10;

	@Function(cacheable = false)
	public static int GET_TEST_VALUE() {
		return testValue;
	}

	public static void SET_TEST_VALUE(int t) {
		testValue = t;
	}
}
