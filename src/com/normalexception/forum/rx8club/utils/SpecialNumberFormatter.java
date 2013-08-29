package com.normalexception.forum.rx8club.utils;

import java.text.DecimalFormat;

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
	
	private static final char[]   c      = new char[]{'k', 'm', 'b', 't'};
	private static final String[] units  = new String[] { "B", "KB", "MB", "GB", "TB" };
	
	/**
	 * Convert a file size to a better formatted size
	 * @param size	The file size
	 * @return		A formatted file size
	 */
	public static String readableFileSize(long size) {
	    if(size <= 0) return "0";
	    
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#")
	    	.format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	
	/**
	 * Recursive implementation, invokes itself for each factor 
	 * of a thousand, increasing the class on each invocation.
	 * @param n 		the number to format
	 * @return 			a formatted string.
	 */
	public static String collapseNumber(String n) {
		return collapseNumber(Double.parseDouble(n.replace(",", "")));
	}

	/**
	 * Recursive implementation, invokes itself for each factor 
	 * of a thousand, increasing the class on each invocation.
	 * @param n 		the number to format
	 * @return 			a formatted string.
	 */
	public static String collapseNumber(double n) {
		return collapseNumber(n, 0);
	}
	
	/**
	 * Recursive implementation, invokes itself for each factor 
	 * of a thousand, increasing the class on each invocation.
	 * @param n 		the number to format
	 * @param iteration in fact this is the class from the array c
	 * @return 			a formatted string.
	 */
	private static String collapseNumber(double n, int iteration) {
		// No need to simplify anything under 1000,
		// and remove the decimal
		if(n < 1000 && iteration == 0) 
			return Double.toString(n).replace(".0", "");
		
	    double d = ((long) n / 100) / 10.0;
	    
	    // true if the decimal part is equal to 0 (then it's trimmed anyway)
	    boolean isRound = (d * 10) %10 == 0;
	   
	    return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
	        ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
	         (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
	         ) + "" + c[iteration]) 
	        : collapseNumber(d, iteration+1));
	}
}
