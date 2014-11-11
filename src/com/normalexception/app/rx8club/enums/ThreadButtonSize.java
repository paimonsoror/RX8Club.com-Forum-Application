package com.normalexception.app.rx8club.enums;

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

/**
 * Enumerator to designate a pixel size of thread buttons for each
 * type of device DPI
 */
public enum ThreadButtonSize {
	
	/*
	 * Low DPI
	 */
	LDPI(12), 
	
	/*
	 * Medium DPI
	 */
	MDPI(12), 
	
	/*
	 * High DPI
	 */
	HDPI(24), 
	
	/*
	 * Extra High DPI
	 */
	XHDPI(48);
	
	private int value;    
	
	/**
	 * Constructor to the enumerator
	 * @param value	The pixel value of the size
	 */
	private ThreadButtonSize(int value) {
		this.value = value;
	}
	
	/**
	 * Report the value of the enumerator
	 * @return	The pixel size of the enumerator
	 */
	public int getValue() {
		return value;
	} 
}
