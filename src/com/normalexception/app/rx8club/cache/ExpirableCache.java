package com.normalexception.app.rx8club.cache;

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

import java.util.Date;

import android.content.Context;

public class ExpirableCache extends Cache {

	protected static final long MS_IN_DAY = 86400000;
	protected int cacheDays;
	
	/**
	 * Constructor to an expirable cache
	 * @param ctx	The source context
	 */
	public ExpirableCache(Context ctx) {
		super(ctx);
		
		// Default set to one day
		cacheDays = 1;
	}
    
	/**
	 * Check if the cache is expired
	 * @return	True if cache is expired
	 */
	public boolean isCacheExpired() {
		boolean result = true;
		cacheFile = getCacheFile();
		if(cacheFile.exists()) {
			long today = new Date().getTime();
			if(cacheFile.lastModified() - today > (MS_IN_DAY * cacheDays))
				result = true;
			else
				result = false;
		}
		return result;
	}
	
	/**
	 * Set the number of days that the cache is valid for
	 * @param numberOfDays	The number of days cache is valid
	 */
	public void setCacheDays(int numberOfDays) {
		cacheDays = numberOfDays;
	}	
}
