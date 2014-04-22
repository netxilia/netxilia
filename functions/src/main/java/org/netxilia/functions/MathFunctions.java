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
package org.netxilia.functions;

import java.math.BigDecimal;
import java.util.Iterator;

import org.apache.commons.math.util.MathUtils;
import org.netxilia.spi.formula.Function;
import org.netxilia.spi.formula.Functions;

/**
 * This are the mathematical functions implemented by Google Docs
 */
@Functions
public class MathFunctions {

	public double ABS(double number) {
		return Math.abs(number);
	}

	public double ACOS(double number) {
		return Math.acos(number);
	}

	public double ACOSH(double x) {
		return Math.log(x + Math.sqrt(x * x - 1));
	}

	public double ASIN(double number) {
		return Math.asin(number);
	}

	public double ASINH(double x) {
		return Math.log(x + Math.sqrt(x * x + 1));
	}

	public double ATAN(double number) {
		return Math.atan(number);
	}

	public double ATAN2(double number_x, double number_y) {
		return Math.atan2(number_x, number_y);
	}

	public double ATANH(double x) {
		return Math.log((1 + x) / (1 - x)) / 2;
	}

	/**
	 * Rounds the given number to the nearest integer or multiple of significance. Significance is the value to whose
	 * multiple of ten the value is to be rounded up (.01, .1, 1, 10, etc.). Mode is an optional value. If it is
	 * indicated and non-zero and if the number and significance are negative, rounding up is carried out based on that
	 * value.
	 * 
	 * @return
	 */
	public double CEILING(double number, double significance) {
		return Math.ceil(number / significance) * significance;
	}

	public long COMBIN(int count1, int count2) {
		if (count2 > count1) {
			throw new IllegalArgumentException("Second argument should be smaller the the first argument");
		}
		return MathUtils.factorial(count1) / (MathUtils.factorial(count2) * MathUtils.factorial(count1 - count2));
	}

	public double COS(double number) {
		return Math.cos(number);
	}

	public double COSH(double number) {
		return MathUtils.cosh(number);
	}

	public int COUNTBLANK(Iterator<String> range) {
		int n = 0;
		while (range.hasNext()) {
			String s = range.next();
			if (s == null || s.isEmpty()) {
				n++;
			}
		}
		return n;
	}

	public int COUNTIF(Iterator<String> range, String criteria) {
		int n = 0;
		while (range.hasNext()) {
			String s = range.next();
			if (criteria == null) {
				if (s == null) {
					n++;
				}
			} else if (criteria.equals(s)) {
				n++;
			}
		}
		return n;
	}

	public double DEGREES(double number) {
		return Math.toDegrees(number);
	}

	private long exactOrNextInteger(double n) {
		double f = Math.floor(n);
		if (n == f) {
			return (long) f;
		}
		return (long) (f + 1);
	}

	/**
	 * Rounds the given number up to the nearest even integer.
	 * 
	 * @param number
	 * @return
	 */
	public long EVEN(double number) {
		if (number < 0) {
			return -EVEN(-number);
		}
		long n = exactOrNextInteger(number);

		if (n % 2 == 0) {
			return n;
		}
		return n + 1;
	}

	public double EXP(double number) {
		return Math.exp(number);
	}

	public long FACT(int number) {
		return MathUtils.factorial(number);
	}

	public double FACTDOUBLE(int number) {
		return MathUtils.factorialDouble(number);
	}

	public double FLOOR(double number, double significance) {
		return Math.floor(number / significance) * significance;
	}

	/**
	 * Returns the greatest common divisor of one or more integers.
	 * 
	 * @return
	 */
	public long GCD(Iterator<Long> values) {
		if (!values.hasNext()) {
			return 1;
		}
		long result = values.next();
		while (values.hasNext()) {
			result = MathUtils.gcd(result, values.next());
		}
		return result;
	}

	public long INT(double number) {
		return Math.round(number);
	}

	public boolean ISEVEN(double value) {
		return ((long) value) % 2 == 0;
	}

	public boolean ISODD(double value) {
		return ((long) value) % 2 == 1;
	}

	/**
	 * Returns the least common multiple of one or more integers. Integer_1, integer_2,... integer_30 are integers whose
	 * lowest common multiple is to be calculated.
	 * 
	 * @param values
	 * @return
	 */
	public long LCM(Iterator<Long> values) {
		if (!values.hasNext()) {
			return 1;
		}
		long result = values.next();
		while (values.hasNext()) {
			result = MathUtils.lcm(result, values.next());
		}
		return result;
	}

	public double LN(double number) {
		return Math.log(number);
	}

	public double LOG(double number, double base) {
		return Math.log(number) / Math.log(base);
	}

	public double LOG10(double number) {
		return Math.log10(number);
	}

	public long MOD(long dividend, long divisor) {
		return dividend % divisor;
	}

	/**
	 * The result is the nearest integer multiple of the number.
	 * 
	 * @param number
	 * @param multiple
	 * @return
	 */
	public double MROUND(double number, double multiple) {
		return Math.round(number / multiple) * multiple;
	}

	/**
	 * Returns the factorial of the sum of the arguments divided by the product of the factorials of the arguments.
	 * 
	 * @param values
	 * @return
	 */
	public double MULTINOMIAL(Iterator<Integer> values) {
		int sum = 0;
		long product = 1;
		while (values.hasNext()) {
			Integer n = values.next();
			sum += n;
			product *= MathUtils.factorial(n);
		}
		return MathUtils.factorial(sum) / product;
	}

	/**
	 * Rounds the given number up to the nearest odd integer.
	 * 
	 * @param number
	 * @return
	 */
	public long ODD(double number) {
		if (number < 0) {
			return -ODD(-number);
		}
		long n = exactOrNextInteger(number);
		if (n % 2 == 1) {
			return n;
		}
		return n + 1;
	}

	public double POWER(double base, double power) {
		return Math.pow(base, power);
	}

	public double PRODUCT(Iterator<Double> values) {
		double n = 1;
		while (values.hasNext()) {
			n *= values.next();
		}
		return n;
	}

	public long QUOTIENT(long numerator, long denominator) {
		return numerator / denominator;
	}

	public double RADIANS(double number) {
		return Math.toRadians(number);
	}

	@Function(cacheable = false)
	public double RAND() {
		return Math.random();
	}

	@Function(cacheable = false)
	public double RANDBETWEEN(double bottom, double top) {
		return bottom + (top - bottom) * Math.random();
	}

	/**
	 * Rounds the given number to a certain number of decimal places according to valid mathematical criteria. Count
	 * (optional) is the number of the places to which the value is to be rounded. If the count parameter is negative,
	 * only the whole number portion is rounded. It is rounded to the place indicated by the count.
	 * 
	 * @param number
	 * @return
	 */
	public double ROUND(double number, int count) {
		return MathUtils.round(number, count, BigDecimal.ROUND_FLOOR);
	}

	public double ROUNDDOWN(double number, int count) {
		return MathUtils.round(number, count, BigDecimal.ROUND_DOWN);
	}

	public double ROUNDUP(double number, int count) {
		return MathUtils.round(number, count, BigDecimal.ROUND_UP);
	}

	/**
	 * Returns a sum of powers of the number x in accordance with the following formula: SERIESSUM(x,n,m,coefficients) =
	 * coefficient_1*x^n + coefficient_2*x^(n+m) + coefficient_3*x^(n+2m) +...+ coefficient_i*x^(n+(i-1)m). x is the
	 * number as an independent variable. n is the starting power. m is the increment. Coefficients is a series of
	 * coefficients. For each coefficient the series sum is extended by one section.
	 * 
	 * @return
	 */
	public double SERIESSUM(double x, double n, double m, Iterator<Double> coefficients) {
		double result = 0;
		double pow = n;
		while (coefficients.hasNext()) {
			result += coefficients.next() * Math.pow(x, pow);
			pow += m;
		}
		return result;
	}

	public double SIGN(double number) {
		return Math.signum(number);
	}

	public double SIN(double number) {
		return Math.sin(number);
	}

	public double SINH(double number) {
		return Math.sinh(number);
	}

	public double SQRT(double number) {
		return Math.sqrt(number);
	}

	/**
	 * Returns the square root of the product of the given number and PI.
	 * 
	 * @param number
	 * @return
	 */
	public double SQRTPI(double number) {
		return Math.sqrt(number * Math.PI);
	}

	public double TAN(double number) {
		return Math.tan(number);
	}

	public double TANH(double number) {
		return Math.tanh(number);
	}

	public double TRUNC(double number, int count) {
		return MathUtils.round(number, count, BigDecimal.ROUND_DOWN);
	}

	public double SUM(Iterator<Double> values) {
		double sum = 0;
		while (values.hasNext()) {
			sum += values.next();
		}
		return sum;
	}

	/**
	 * Adds the cells specified by a given criteria. Range is the range to which the criteria are to be applied.
	 * Criteria is the cell in which the search criterion is shown, or the search criterion itself. Sum_range is the
	 * range from which values are summed, if it has not been indicated, the values found in the Range are summed.
	 * public double SUMIF(range, criteria, sum_range){
	 * 
	 * }
	 */

	public double SUMSQ(Iterator<Double> values) {
		double sum = 0;
		while (values.hasNext()) {
			double x = values.next();
			sum += x * x;
		}
		return sum;
	}

	public double PI() {
		return Math.PI;
	}

	public static void main(String[] args) {
		System.out.println(new MathFunctions().ODD(5.5));
		System.out.println(new MathFunctions().EVEN(12));
	}

}
