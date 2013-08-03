package com.normalexception.forum.rx8club.utils;

/************************************************************************
 * NormalException.net Software, and other contributors
 * http://www.normalexception.net
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ************************************************************************/

public class SpecialNumberFormatter {
	
	private static char[] c = new char[]{'k', 'm', 'b', 't'};

	/**
	 * Recursive implementation, invokes itself for each factor 
	 * of a thousand, increasing the class on each invocation.
	 * @param n 		the number to format
	 * @param iteration in fact this is the class from the array c
	 * @return 			a formatted string.
	 */
	public static String collapseNumber(double n, int iteration) {
		if(n < 1000 && iteration == 0) return Double.toString(n).replace(".0", "");
		
	    double d = ((long) n / 100) / 10.0;
	    boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
	    return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
	        ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
	         (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
	         ) + "" + c[iteration]) 
	        : collapseNumber(d, iteration+1));
	}
}
