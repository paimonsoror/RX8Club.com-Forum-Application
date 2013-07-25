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

import ch.boye.httpclientandroidlib.HeaderElement;
import ch.boye.httpclientandroidlib.HeaderElementIterator;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.impl.client.DefaultConnectionKeepAliveStrategy;
import ch.boye.httpclientandroidlib.message.BasicHeaderElementIterator;
import ch.boye.httpclientandroidlib.protocol.HTTP;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

/**
 * Custom Keep alive strategy
 */
public class KeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

	private static final int KAL_TIME = 30; // seconds
	
	/*
	 * (non-Javadoc)
	 * @see ch.boye.httpclientandroidlib.impl.client.DefaultConnectionKeepAliveStrategy#getKeepAliveDuration(ch.boye.httpclientandroidlib.HttpResponse, ch.boye.httpclientandroidlib.protocol.HttpContext)
	 */
	public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
		// Honor 'keep-alive' header
		HeaderElementIterator it = new BasicHeaderElementIterator(
				response.headerIterator(HTTP.CONN_KEEP_ALIVE));
		while (it.hasNext()) {
			HeaderElement he = it.nextElement();
			String param = he.getName(); 
			String value = he.getValue();
			if (value != null && param.equalsIgnoreCase("timeout")) {
				try {
					return Long.parseLong(value) * 1000;
				} catch(NumberFormatException ignore) {
				}
			}
		}

		//keep alive for 30 seconds
		return KAL_TIME * 1000;
	}
}
