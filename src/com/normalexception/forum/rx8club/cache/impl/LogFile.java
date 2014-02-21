package com.normalexception.forum.rx8club.cache.impl;

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

import java.io.File;

import android.content.Context;

import com.normalexception.forum.rx8club.cache.Cache;

public class LogFile extends Cache {
	
	private static final String FILENAME = "rx8club.log";
	private static String logfile = null;

	/**
	 * Initialize a reference to our log file
	 * @param ctx	The application context
	 */
	public LogFile(Context ctx) {
		super(ctx);	
		if(logfile == null) {
			File external = getExternalCache();
			logfile = String.format("%s%s%s", 
					external.getAbsolutePath(), 
					File.separator,
					FILENAME);
		}
	}
	
	/**
	 * Report the log file
	 * @return	The location of the log file as a string
	 */
	public synchronized static String getLogFile() {
		return logfile;
	}
}
