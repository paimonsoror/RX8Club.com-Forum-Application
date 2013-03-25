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

import java.util.Calendar;
import java.util.TimeZone;

public class Utils {
	
	/**	
	 * Parse the integers from a string
	 * @param val	The string to parse ints from
	 * @return		A string that contains only the ints from the source
	 */
	public static String parseInts(String val) {
		return val.replaceAll( "[^\\d]", "" );
	}
	
	private static String initializePageOperation(String link) {
		String fragmentedLink = removeTrailingSlash(link);
		fragmentedLink = fragmentedLink.substring(fragmentedLink.lastIndexOf('/') + 1);
		return fragmentedLink;
	}
	
	private enum Operation { Increment, Decrement, Get }
	
	/**
	 * 
	 * @param link
	 * @param page
	 * @param op
	 * @return
	 */
	private static String pageOperation(String link, String page, Operation op) {
		String fragmentedLink = initializePageOperation(link);
		String newLink = null;
		
		if(fragmentedLink.indexOf("page") == 0) {
			int currentPageNumber = Integer.parseInt(parseInts(fragmentedLink));
			String newPage = "page";
			if(op == Operation.Increment)
				newPage += Integer.toString(currentPageNumber + 1);
			else if (op == Operation.Decrement) 
				newPage += Integer.toString(currentPageNumber - 1);
			else if (op == Operation.Get)
				newPage += page;
			
			newLink = link.replace("page" + Integer.toString(currentPageNumber), newPage);
		} else {
			if(link.lastIndexOf("-new-post/") == link.length() - "-new-post/".length()) {
				newLink = link.substring(0, 
						link.lastIndexOf("-new-post/")) + "/page";
			} else {
				if(fragmentedLink.startsWith("search.php")) {
					// Looks like this is a search result
					newLink = link + "&pp=50&page=";
				} else {
					newLink = link + "page";
				}
			}
			
			if(op == Operation.Increment)
				newLink += String.valueOf(Integer.parseInt(page) + 1);
			else if (op == Operation.Decrement) 
				newLink += String.valueOf(Integer.parseInt(page) - 1);
			else if (op == Operation.Get)
				newLink += page;				
		}
		
		return newLink;
	}
	
	/**
	 * Increment the page count and report a link that reflects that page
	 * @param link	The link that we are looking to increment
	 * @return		The new link that equals the link page + 1
	 */
	public static String incrementPage(String link, String page) {
		return pageOperation(link, page, Operation.Increment);
	}
	
	/**
	 * Report a page given the page number
	 * @param link	The current page link
	 * @param page  The new page number requested
	 * @return		A string with the new page number
	 */
	public static String getPage(String link, String page) {
		return pageOperation(link, page, Operation.Get);
	}
	
	/**
	 * Decrease the page count and report a link that reflects that page	
	 * @param link	The link that we are looking to decrement
	 * @return		The new link that equals the link page - 1
	 */
	public static String decrementPage(String link, String page) {
		return pageOperation(link, page, Operation.Decrement);
	}
	
	/**
	 * Remove a trailing slash from the string if it exists
	 * @param link	The link to remove the trailing slash from
	 * @return		The link string without the trailing slash
	 */
	public static String removeTrailingSlash(String link) {
		if(link.lastIndexOf('/') == (link.length() - 1)) 
			return link.substring(0, link.length() - 1);
		else
			return link;
	}
	
	/**
	 * Remove the page number from a link
	 * @param link	The link that the page will be removed from
	 * @return		A pageless link
	 */
	public static String removePageFromLink(String link) {
		String realLink = link;
		String[] splittedLink = link.split("/");
		String pagenumber = splittedLink[splittedLink.length - 1];
		if(pagenumber.contains("page")) {
			StringBuilder sb = new StringBuilder();
			for(int i = 0; i < (splittedLink.length - 1); i++) 
				sb.append(splittedLink[i] + "/");
			realLink = sb.toString();
		} 
		return realLink;
	}

	
	/**
	 * Report the time since the epoch
	 * @return	Time since epoch
	 */
	public static long getTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    	calendar.clear();
    	calendar.set(2011, Calendar.OCTOBER, 1);
    	return calendar.getTimeInMillis() / 1000L;
	}
}
