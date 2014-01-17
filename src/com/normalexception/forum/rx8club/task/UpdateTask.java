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

import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import com.normalexception.forum.rx8club.Log;

import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

/**
 * Task used to move all post editing tasks to an async task
 */
public class UpdateTask extends AsyncTask<Void,Void,Void> {
	private String TAG = this.getClass().getName();
	
	private ProgressDialog mProgressDialog;
	private Activity sourceActivity;
	
	private String securitytoken, p, posthash, 
		poststarttime, msg, pageTitle;
	
	private boolean delete = false, deleteThread = false;
	
	public UpdateTask(Activity sourceActivity, String token, String postid, 
					  String posthash, String poststarttime, String pageNumber,
					  String pageTitle, String message, boolean delete,
					  boolean deleteThread) {
		this.sourceActivity = sourceActivity;
		securitytoken = token;
		p = postid;
		this.posthash = posthash;
		this.poststarttime = poststarttime;
		this.msg = message;
		this.pageTitle = pageTitle;
		this.delete = delete;
		this.deleteThread = deleteThread;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Void doInBackground(Void... params) {
		try {
			if(delete) {
				HtmlFormUtils.submitDelete(securitytoken, p);
			} else {
				HtmlFormUtils.submitEdit(securitytoken, p, 
					posthash, poststarttime, msg);
			}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
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
		
		String url = HtmlFormUtils.getResponseUrl();
		Intent _intent = null;
		
		if(deleteThread) {
			_intent = new Intent(sourceActivity, CategoryActivity.class);
		} else {
			_intent = new Intent(sourceActivity, ThreadActivity.class);			
		}
		
		_intent.putExtra("title", pageTitle);
		_intent.putExtra("page", "1");
		_intent.putExtra("link", url);
		sourceActivity.finish();
		sourceActivity.startActivity(_intent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
    	if(delete) 
    		mProgressDialog = 
    			ProgressDialog.show(this.sourceActivity, "Deleting...", "Deleting Post...");
    	else
    		mProgressDialog = 
        		ProgressDialog.show(this.sourceActivity, "Submitting...", "Submitting Post...");
    }
}
