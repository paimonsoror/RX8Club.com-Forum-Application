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
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

/**
 * Task generated to move the submit of a post to an async
 * task
 */
public class SubmitTask extends AsyncTask<Void,Void,Void>{
	private ProgressDialog mProgressDialog;
	private Activity sourceActivity;
	
	private String token, thread, post, text, pageTitle, pageNumber, postHash, doType;
	private List<String> bitmaps;
	private Class<?> postClazz;

	private static String TAG = "SubmitTask";
	
	/**
	 * Constructor to a SubmitTask
	 * @param sourceActivity	The source activity
	 * @param securityToken		The users security token
	 * @param bmapList			The list of bitmaps to attach
	 * @param threadNumber		The source thread number
	 * @param postNumber		The post number
	 * @param toPost			The text to post
	 * @param currentPageLink	The current page link
	 * @param pageTitle			The current page title
	 * @param pageNumber		The current page number
	 */
	public SubmitTask(Activity sourceActivity, List<String> bmapList, 
					  String securityToken, String threadNumber, String postNumber, 
					  String phash, String toPost, String pageTitle, String pageNumber) 
	{
		this.sourceActivity = sourceActivity;
		this.bitmaps = bmapList;
		this.token = securityToken;
		this.thread = threadNumber;
		this.post = postNumber;
		this.postHash = phash;
		this.text = toPost;
		this.pageTitle = pageTitle;
		this.pageNumber = pageNumber;
		this.doType = "postreply";
		this.postClazz = ThreadActivity.class;
	}
	
	public void debug() {
		Log.d(TAG, 
				String.format("Token: %s, Thread: %s, Post: %s, Text: %s", 
						this.token, this.thread, this.post, this.text));
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
    protected void onPostExecute(Void result) {
        mProgressDialog.dismiss();
		Intent _intent = new Intent(sourceActivity, postClazz);
		_intent.putExtra("link", HtmlFormUtils.getResponseUrl());
		_intent.putExtra("page", String.valueOf(Integer.parseInt(pageNumber)));
		_intent.putExtra("title", pageTitle);
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
        		ProgressDialog.show(this.sourceActivity, "Submitting...", "Submitting Post...");
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		if(bitmaps.size() != 0)
    			HtmlFormUtils.submitAttachment(
    					token, bitmaps, post);
    		
    		HtmlFormUtils.submitPost(doType, token, thread, 
					post, text);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
        return null;
    }
}