package com.normalexception.app.rx8club;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.LoginFragment;

public class TimeoutFactory {
	private static TimeoutFactory _instance = null;
	
	protected static long pingTime = 0;
	protected static long pongTime = 0;
	protected static long diffTime = 0;
	
	protected static final long PING_EXPIRE = 2 /*hours*/ * 3600;
	//protected static final long PING_DEBUG  = 10;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	/**
	 * Report an instanec of the TimeoutFactory, and create 
	 * a new one if one doesn't already exist
	 * @return An instance of the timeout factory
	 */
	public static TimeoutFactory getInstance() {
		if(_instance == null)
			_instance = new TimeoutFactory();
		return _instance;
	}
	
	/**
	 * Singleton coding style for the timeout factory
	 */
	protected TimeoutFactory() {
		resetTimeouts();
	}
	
	/**
	 * Reset all timeout values
	 */
	public void resetTimeouts() {
		Log.v(TAG, "Resetting Timeouts");
		pingTime = pongTime = diffTime = 0;
	}
	
	/**
	 * When we create our activities, we want to update the ping time
	 * this will help us re-login when our cache expires
	 */
	public boolean checkTimeout(final Activity src) {
		return this.checkTimeout(src);
	}
	
	public boolean checkTimeout(final Fragment src) {
		// Only update pong when the login activity was called
		if(!(src instanceof LoginFragment)) {
			pingTime = System.currentTimeMillis() / 1000l;
			diffTime = pingTime - pongTime;
			Log.d(TAG, String.format("Ping Time: %d", pingTime));
			Log.d(TAG, String.format("Difference Time: %d", diffTime));
			
			if(diffTime > PING_EXPIRE) {
				src.getActivity().runOnUiThread(new Runnable() {
		    		public void run() {
		    			Toast.makeText(src.getActivity(), R.string.timeout, Toast.LENGTH_SHORT).show();
		    		}
				});
				Log.d(TAG, "## PING TIME EXPIRED ##");
				FragmentUtils.returnToLoginPage(src.getActivity(), false, false);
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Update our pong time
	 */
	public void updatePongTime() {
		pongTime = System.currentTimeMillis() / 1000l;
		Log.d(TAG, String.format("Pong Time: %d", pongTime));
	}
}
