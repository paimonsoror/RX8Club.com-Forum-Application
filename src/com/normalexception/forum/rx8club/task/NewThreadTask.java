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

import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

/**
 * Task generated to move the submit of a new thread to an async
 * task
 */
public class NewThreadTask extends AsyncTask<Void,Void,Void> {
	private ProgressDialog mProgressDialog;
	
	private Activity sourceActivity;
	private String s, token, posthash, subject, post, forumId;
	
	private String TAG = this.getClass().getName();
	
	public NewThreadTask(Activity source, String forumId, String s, 
						 String token, String f, String posthash,
						 String subject, String post, String sourceLink) {
		this.sourceActivity = source;
		this.s = s;
		this.token = token;
		this.posthash = posthash;
		this.subject = subject;
		this.post = post;
		this.forumId = forumId;
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
		
		Intent _intent = new Intent(sourceActivity, ThreadActivity.class);
		_intent.putExtra("link", HtmlFormUtils.getResponseUrl());
		_intent.putExtra("title", subject);
		_intent.putExtra("page", "1");
		sourceActivity.finish();
		sourceActivity.startActivity(_intent);
    }

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
        mProgressDialog = 
        		ProgressDialog.show(this.sourceActivity, "Submitting...", "Creating New Thread...");
    }
	
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
	@Override
	protected Void doInBackground(Void... arg0) {
		try {
			HtmlFormUtils.newThread(forumId, s, token, posthash, subject, post);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
	}

}
