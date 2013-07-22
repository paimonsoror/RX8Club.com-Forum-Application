package com.normalexception.forum.rx8club.activities.list;

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

import java.io.Serializable;
import java.util.LinkedHashMap;

public class ThreadListContents implements Serializable {

	private static final long serialVersionUID = 1L;
	public LinkedHashMap<String,String> styleMap, userMap, lastUserMap;
	public LinkedHashMap<String,Boolean> lockedMap, stickyMap;
	
	public ThreadListContents() {
		styleMap = new LinkedHashMap<String,String>();
        userMap = new LinkedHashMap<String,String>();
	    lastUserMap = new LinkedHashMap<String, String>();
	    lockedMap = new LinkedHashMap<String, Boolean>();
	    stickyMap = new LinkedHashMap<String, Boolean>();
	}
	
	public void add(String key, String style, String user, 
			        String lastUser, boolean isLocked, boolean isSticky) {
		styleMap.put(key, style);
		userMap.put(key, user);
		lastUserMap.put(key, lastUser);
		lockedMap.put(key, isLocked);
		stickyMap.put(key, isSticky);
	}
}
