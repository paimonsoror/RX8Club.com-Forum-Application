package com.normalexception.app.rx8club.httpclient;

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

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.normalexception.app.rx8club.Log;

/**
 * Custom redirect strategy
 */
public class RedirectStrategy extends DefaultRedirectStrategy {
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	public RedirectStrategy() {
		Log.d(TAG, "Custom Redirect Strategy Established");
	}
    
	/*
	 * (non-Javadoc)
	 * @see ch.boye.httpclientandroidlib.impl.client.DefaultRedirectStrategy#isRedirected(ch.boye.httpclientandroidlib.HttpRequest, ch.boye.httpclientandroidlib.HttpResponse, ch.boye.httpclientandroidlib.protocol.HttpContext)
	 */
    public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context)  {
        boolean isRedirect=false;
        try {
            isRedirect = super.isRedirected(request, response, context);
        } catch (ProtocolException e) {
        	Log.e(TAG, e.getMessage(), e);
        }
        if (!isRedirect) {
            int responseCode = response.getStatusLine().getStatusCode();
            if (responseCode == HttpStatus.SC_MOVED_PERMANENTLY  || 
            		responseCode == HttpStatus.SC_MOVED_TEMPORARILY) {
                return true;
            }
        }
        return isRedirect;
    }
}
