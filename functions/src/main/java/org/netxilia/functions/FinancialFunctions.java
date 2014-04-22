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

import java.util.Iterator;

import org.netxilia.spi.formula.Functions;

/*
 * ACCRINT(issue, first_interest, settlement, rate, par, frequency, basis)
 ACCRINTM(issue, settlement, rate, par, basis)
 COUPDAYBS(settlement, maturity, frequency, basis)
 COUPDAYS(settlement, maturity, frequency, basis)
 COUPDAYSNC(settlement, maturity, frequency, basis)
 COUPNCD(settlement, maturity, frequency, basis)
 COUPNUM(settlement, maturity, frequency, basis)
 COUPPCD(settlement, maturity, frequency, basis)
 CUMIPMT(rate, NPER, PV, S, E, type)
 CUMPRINC(rate, NPER, PV, S, E, type)
 DB(cost, salvage, life, period, month)
 DDB(cost, salvage, life, period, factor)
 DISC(settlement, maturity, price, redemption, basis)
 DOLLARDE(fractional _dollar, fraction)
 DOLLARFR(decimal _dollar, fraction)
 DURATION(rate, PV, FV)
 EFFECT(NOM, P)
 FVSCHEDULE(principal, schedule)
 INTRATE(settlement, maturity, investment, redemption, basis)
 IPMT(rate, period, NPER, PV, FV, type)
 IRR(values, guess)
 MDURATION(settlement, maturity, coupon, yield, frequency, basis)
 MIRR(values, investment, reinvest_rate)
 NOMINAL(effective_rate, Npery)



 PPMT(rate, period, NPER, PV, FV, type)
 PRICE(settlement, maturity, rate, yield, redemption, frequency, basis)
 PRICEDISC(settlement, maturity, discount, redemption, basis)
 PRICEMAT(settlement, maturity, issue, rate, yield, basis)

 RATE(NPER, PMT, PV, FV, type, guess)
 RECEIVED(settlement, maturity, investment, discount, basis)
 SLN(cost, salvage, life)
 SYD(cost, salvage, life, period)
 TBILLEQ(settlement, maturity, discount)
 TBILLPRICE(settlement, maturity, discount)
 TBILLYIELD(settlement, maturity, price)
 XIRR(values, dates, guess)
 XNPV(rate, values, dates)
 YIELD(settlement, maturity, rate, price, redemption, frequency, basis)
 YIELDDISC(settlement, maturity, price, redemption, basis)
 */
@Functions
public class FinancialFunctions {
	/**
	 * FV(rate, NPER, PMT, PV, type)
	 * 
	 * Future value of an amount given the number of payments, rate, amount of individual payment, present value and
	 * boolean value indicating whether payments are due at the beginning of period (false => payments are due at end of
	 * period)
	 * 
	 * @param r
	 *            rate
	 * @param n
	 *            num of periods
	 * @param y
	 *            pmt per period
	 * @param p
	 *            future value
	 * @param t
	 *            type (true=pmt at end of period, false=pmt at begining of period)
	 */
	public static double FV(double r, double n, double y, double p, boolean t) {
		double retval = 0;
		if (r == 0) {
			retval = -1 * (p + (n * y));
		} else {
			double r1 = r + 1;
			retval = ((1 - Math.pow(r1, n)) * (t ? r1 : 1) * y) / r - p * Math.pow(r1, n);
		}
		return retval;
	}

	/**
	 * PV(rate, NPER, PMT, FV, type)
	 * 
	 * Present value of an amount given the number of future payments, rate, amount of individual payment, future value
	 * and boolean value indicating whether payments are due at the beginning of period (false => payments are due at
	 * end of period)
	 * 
	 * @param r
	 * @param n
	 * @param y
	 * @param f
	 * @param t
	 */
	public static double PV(double r, double n, double y, double f, boolean t) {
		double retval = 0;
		if (r == 0) {
			retval = -1 * ((n * y) + f);
		} else {
			double r1 = r + 1;
			retval = (((1 - Math.pow(r1, n)) / r) * (t ? r1 : 1) * y - f) / Math.pow(r1, n);
		}
		return retval;
	}

	/**
	 * NPV(Rate, value_1, value_2, ... value_30)
	 * 
	 * calculates the Net Present Value of a principal amount given the discount rate and a sequence of cash flows
	 * (supplied as an array). If the amounts are income the value should be positive, else if they are payments and not
	 * income, the value should be negative.
	 * 
	 * @param r
	 * @param cfs
	 *            cashflow amounts
	 */
	public static double NPV(double r, Iterator<Double> values) {
		double npv = 0;
		double r1 = r + 1;
		double trate = r1;
		while (values.hasNext()) {
			npv += values.next() / trate;
			trate *= r1;
		}

		return npv;
	}

	/**
	 * PMT(rate, NPER, PV, FV, type)
	 * 
	 * 
	 * @param r
	 * @param n
	 * @param p
	 * @param f
	 * @param t
	 */
	public static double PMT(double r, double n, double p, double f, boolean t) {
		double retval = 0;
		if (r == 0) {
			retval = -1 * (f + p) / n;
		} else {
			double r1 = r + 1;
			retval = (f + p * Math.pow(r1, n)) * r / ((t ? r1 : 1) * (1 - Math.pow(r1, n)));
		}
		return retval;
	}

	/**
	 * NPER(rate, PMT, PV, FV, type)
	 * 
	 * 
	 * @param r
	 * @param y
	 * @param p
	 * @param f
	 * @param t
	 */
	public static double NPER(double r, double y, double p, double f, boolean t) {
		double retval = 0;
		if (r == 0) {
			retval = -1 * (f + p) / y;
		} else {
			double r1 = r + 1;
			double ryr = (t ? r1 : 1) * y / r;
			double a1 = ((ryr - f) < 0) ? Math.log(f - ryr) : Math.log(ryr - f);
			double a2 = ((ryr - f) < 0) ? Math.log(-p - ryr) : Math.log(p + ryr);
			double a3 = Math.log(r1);
			retval = (a1 - a2) / a3;
		}
		return retval;
	}
}
