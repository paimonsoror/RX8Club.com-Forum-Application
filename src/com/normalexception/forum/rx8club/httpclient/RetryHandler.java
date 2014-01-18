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

import java.io.IOException;

import org.apache.log4j.Logger;

import ch.boye.httpclientandroidlib.NoHttpResponseException;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpRequestRetryHandler;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import com.normalexception.forum.rx8club.Log;

/**
 * Custom retry handler
 */
public class RetryHandler extends DefaultHttpRequestRetryHandler {
	private static final int MAX_RETRY = 5;
	private Logger TAG =  Logger.getLogger(this.getClass());
	
	/*
	 * (non-Javadoc)
	 * @see ch.boye.httpclientandroidlib.impl.client.DefaultHttpRequestRetryHandler#retryRequest(java.io.IOException, int, ch.boye.httpclientandroidlib.protocol.HttpContext)
	 */
	public boolean retryRequest(IOException exception, 
			int executionCount, 
			HttpContext context) 
	{
		// retry a max of MAX_RETRY times
		if (executionCount >= MAX_RETRY) {
			Log.d(TAG, "Max Retries Exceeded");
			return false;
		}

		if (exception instanceof NoHttpResponseException) {
			// Retry if the server dropped connection on us
			Log.d(TAG, "Server dropped request, retrying");
			return true;
		}

		Boolean b = (Boolean)
				context.getAttribute(ExecutionContext.HTTP_REQ_SENT);
		boolean sent = (b != null && b.booleanValue());   
		if (!sent) {
			// Retry if the request has not been sent fully or
			// if it's OK to retry methods that have been sent
			Log.d(TAG, "Request not fully sent, retrying");
			return true;
		}

		// otherwise do not retry
		return false;
	}
}