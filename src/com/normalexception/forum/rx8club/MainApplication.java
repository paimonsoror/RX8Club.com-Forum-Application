package com.normalexception.forum.rx8club;

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

import android.app.Application;
import android.content.Context;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.state.AppState;

/**
 * Main Application
 */
public class MainApplication extends Application {

	private static Context context;
	
	private static String TAG = "Application";
	
	public static final String APP_PACKAGE = "com.normalexception.forum.rx8club";
	public static final String BUG_APIKEY  = "fd9ce344";
	
	private static final int LOG_LEVEL           = Log.DEBUG;
	private static final boolean HTTP_CLIENT_LOG = false; 

	/*
	 * (non-Javadoc)
	 * @see android.app.Application#onCreate()
	 */
    public void onCreate(){
    	// Set the logger level for our log wrapper
    	Log.setLevel(LOG_LEVEL);
    	
    	Log.v(TAG, "Starting Application");
        super.onCreate();
        
        BugSenseHandler.initAndStartSession(getApplicationContext(), BUG_APIKEY);
        
        MainApplication.context = getApplicationContext();
    }

    /**
     * Report the application context
     * @return	Application context
     */
    public static Context getAppContext() {
        return MainApplication.context;
    }
    
    /**
     * Report the current application state
     * @return	The current application state
     */
    public static AppState.State getApplicationState() {
    	return AppState.getInstance().getCurrentState();
    }
    
    /**
     * Report if we are enabling HTTPClient logging
     * @return	True if enabled, false if else
     */
    public static boolean isHttpClientLogEnabled() {
    	return HTTP_CLIENT_LOG;
    }
}
