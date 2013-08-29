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

import android.content.Context;

import com.normalexception.forum.rx8club.cache.ObjectCache;
import com.normalexception.forum.rx8club.user.UserProfile;

public class UserProfileCache extends ObjectCache<UserProfile> {
	
	/**
	 * To help increase the speed of the app, we are going to go ahead
	 * and cache the view list.  The cache expires each day, so if
	 * the forums update it wont take long for the app to get the
	 * updates
	 * @param ctx		The source context
	 * @param filename	The name of the cache file
	 */
	public UserProfileCache(Context ctx, String filename) {
		super(ctx);
		this.CACHEFILENAME = filename;
		this.cacheDir = getExternalCache();
	}
}
