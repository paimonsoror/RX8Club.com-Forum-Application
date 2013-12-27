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

import java.util.ArrayList;
import java.util.List;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.filter.ThreadFilter;
import com.normalexception.forum.rx8club.filter.ThreadFilterFactory;
import com.normalexception.forum.rx8club.filter.ThreadFilter.RuleType;
import com.normalexception.forum.rx8club.view.thread.ThreadView;

public class CategoryFilterizer {
	
	private static String TAG = "CategoryFilterizer";
	
	/**
	 * Filter our category if any actually exist
	 * @param threadlist	The source list
	 * @return				The filtered list (or the source list if no
	 *                      filters exist)
	 */
	public static ArrayList<ThreadView> applyFilter(ArrayList<ThreadView> threadlist) {
    	// Do we have any filters?  If so, lets filter out threads
    	if(ThreadFilterFactory.getInstance().hasFilters()) {
    		List<ThreadFilter> filters = 
    				ThreadFilterFactory.getInstance().getThreadFilters();
    		ArrayList<ThreadView> filtered = new ArrayList<ThreadView>();
    		for(ThreadView tv : threadlist) {
    			boolean filterOut = false;
    			for(ThreadFilter tf : filters) {
    				// Filter by thread owner
    				if(tf.getRule() == RuleType.OWNER) {
    					if(tv.getStartUser().equalsIgnoreCase(tf.getSubject()))
    						filterOut = true;
    					
    				// Filter by thread title
    				} else if(tf.getRule() == RuleType.TITLE) {
    					if(tv.getTitle().equalsIgnoreCase(tf.getSubject()))
    						filterOut = true;
    					
    				// Filter by last responded user
    				} else if(tf.getRule() == RuleType.LASTUSER) {
    					if(tv.getLastUser().equalsIgnoreCase(tf.getSubject()))
    						filterOut = true;
    				}
    			}
    			if(!filterOut) {
    				filtered.add(tv);
    			} else 
    				Log.d(TAG, "Filtering Out " + tv.getTitle());
    		}
    		
    		// Now copy our filtered back to the main list
    		return new ArrayList<ThreadView>(filtered);
    	} else {
    		return threadlist;
    	}
	}
}
