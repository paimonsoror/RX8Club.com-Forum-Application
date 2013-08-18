package com.normalexception.forum.rx8club.httpclient;

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

import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.html.LoginFactory;

import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpRequestBase;

/**
 * Common client utilities
 */
public class ClientUtils {
	
	/**
	 * Report an HttpPost object
	 * @param address	The address to request
	 * @return			An httppost with proper headers
	 */
	public static HttpPost getHttpPost(String address) {
		HttpPost hp = new HttpPost(address);
		return hp;
	}

	/**
	 * Report and HttpGet object
	 * @param address	The address to request
	 * @return			An httpget with proper headers
	 */
	public static HttpGet getHttpGet(String address) {
		HttpGet hg = new HttpGet(address);
		return hg;
	}
	
	/**
	 * A set of common headers to set for our post and get
	 * request bases
	 * @param	hrb		The request base to setup
	 * @return			The request base with the headers applied
	 */
	private static HttpRequestBase getCommonHeaders(HttpRequestBase hrb) {
		hrb.setHeader("Host", "www.rx8club.com");
		hrb.setHeader("User-Agent", WebUrls.USER_AGENT);
		hrb.setHeader("Accept", 
	             "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		hrb.setHeader("Accept-Encoding", "gzip,deflate,sdch");
		hrb.setHeader("Accept-Language", "en-US,en;q=0.8");
		hrb.setHeader("Cookie", LoginFactory.getInstance().getCookies());
		hrb.setHeader("Connection", "keep-alive");
		return hrb;
	}
}
