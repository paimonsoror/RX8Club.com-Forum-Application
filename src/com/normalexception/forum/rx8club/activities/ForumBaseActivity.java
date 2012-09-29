package com.normalexception.forum.rx8club.activities;

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
import java.util.LinkedHashMap;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.view.ViewContents;

public abstract class ForumBaseActivity extends Activity {
	
	protected Map<String,String> linkMap;
	protected ArrayList<ViewContents> viewContents;
	
	protected Thread updaterThread;
	protected ProgressDialog loadingDialog;
	
	protected static final int LOGOFF_MENU = 0;
	
	private static String TAG = "ForumBaseActivity";
	
	protected static TableLayout tl = null;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		try {
			if(loadingDialog != null && loadingDialog.isShowing())
				loadingDialog.dismiss();
		} catch (Exception e) {
			Log.e(TAG, "Error dismissing loading dialog");
			BugSenseHandler.sendException(e);
		}
		
		try {
			Log.v(TAG, "Serializing Contents");
			outState.putSerializable("contents", viewContents);
			outState.putSerializable("links", (LinkedHashMap<String,String>)linkMap);
		} catch (Exception e) {
			Log.e(TAG, "Error Serializing: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
		if(savedInstanceState != null)
			viewContents = 
				(ArrayList<ViewContents>) savedInstanceState.getSerializable("contents");
			linkMap = 
					(LinkedHashMap<String, String>) savedInstanceState.getSerializable("links");
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
    public boolean onCreateOptionsMenu(Menu menu){ 
        menu.add(0,LOGOFF_MENU,0,"Logoff");
        return true; 
    } 
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
    public boolean onOptionsItemSelected (MenuItem item)
    { 
        switch(item.getItemId())
        {
           case(LOGOFF_MENU):
   				Log.v(TAG, "Logoff Pressed");
   				LoginFactory.getInstance().logoff();
   				Intent _intent = 
   						new Intent(MainApplication.getAppContext(), LoginActivity.class);
   				_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
   				finish();
   				startActivity(_intent);
   				break;
        } 
        return false; 
    } 
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
	    if ((keyCode == KeyEvent.KEYCODE_BACK))
	    {
	    	Log.v(TAG, "Back pressed, Finishing Activity");
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
