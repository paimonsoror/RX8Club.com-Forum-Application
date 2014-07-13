package com.normalexception.forum.rx8club.task;

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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

/**
 * Task generated to move the submit of a post to an async
 * task
 */
public class AdminTask extends AsyncTask<Void,String,Void>{

	private ProgressDialog mProgressDialog;
	private Activity sourceActivity;
	
	private String token, thread, doType;

	private Logger TAG =  Logger.getLogger(this.getClass());
	
	public static final String LOCK_THREAD = "openclosethread";
	public static final String DELETE_THREAD = "dodeletethread";
	
	public static final String DELETE_REASON = "Deleted From Mobile App";
	
	private static final Map<String, String> progressText;
	private static final Map<String, String> descriptionText;
	
	static
    {
			progressText = new HashMap<String, String>();
			progressText.put(LOCK_THREAD, "Un/Locking Thread...");
			progressText.put(DELETE_THREAD, "Deleting Thread...");
			
			descriptionText = new HashMap<String, String>();
			descriptionText.put(LOCK_THREAD, "Lock / Unlock Thread");
			descriptionText.put(DELETE_THREAD, "Delete Thread");
    }
	
	/*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		Log.d(TAG, progressText.get(doType));
    		HtmlFormUtils.adminTypePost(doType, token, thread);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
    }
	
	/**
	 * Constructor to a LockTask
	 * @param sourceActivity	The source activity
	 * @param securityToken		The users security token
	 * @param threadNumber		The source thread number
	 */
	public AdminTask(Activity sourceActivity, String securityToken, String threadNumber, String action) 
	{
		this.sourceActivity = sourceActivity;
		this.token = securityToken;
		this.thread = threadNumber;
		this.doType = action;
	}
	
	public String getDescription() {
		return descriptionText.get(doType);
	}
	
	public void debug() {
		Log.d(TAG, 
				String.format("Token: %s, Thread: %s", 
						this.token, this.thread));
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
    	
        mProgressDialog = 
        		ProgressDialog.show(this.sourceActivity, progressText.get(doType), "Please Wait...");
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
    protected void onPostExecute(Void result) {
		try {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
		
		if(doType == LOCK_THREAD) {
			Intent _intent = this.sourceActivity.getIntent();
			boolean isLocked = _intent.getBooleanExtra("locked", false);
			this.sourceActivity.finish();
			
			Log.d(TAG, "Setting Thread Lock To: " + !isLocked);
			_intent.putExtra("locked", !isLocked);
			this.sourceActivity.startActivity(_intent);
		} else {
			this.sourceActivity.finish();
		}
    }
}
