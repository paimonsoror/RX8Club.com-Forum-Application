package com.normalexception.app.rx8club.task;

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

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.thread.ThreadFragment;
import com.normalexception.app.rx8club.html.HtmlFormUtils;

/**
 * Task generated to move the submit of a post to an async
 * task
 */
public class SubmitTask extends AsyncTask<Void,String,Void>{
	private ProgressDialog mProgressDialog;
	private Fragment sourceActivity;
	
	private String token, thread, post, text, pageTitle, pageNumber, doType;
	private List<String> bitmaps;

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
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
	public SubmitTask(Fragment sourceActivity, List<String> bmapList, 
					  String securityToken, String threadNumber, String postNumber, 
					  String toPost, String pageTitle, String pageNumber) 
	{
		this.sourceActivity = sourceActivity;
		this.bitmaps = bmapList;
		this.token = securityToken;
		this.thread = threadNumber;
		this.post = postNumber;
		this.text = toPost;
		this.pageTitle = pageTitle;
		this.pageNumber = pageNumber;
		this.doType = "postreply";
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
		try {
			mProgressDialog.dismiss();
			mProgressDialog = null;
		} catch (Exception e) {
			Log.w(TAG, e.getMessage());
		}
		
		final String respLink = HtmlFormUtils.getResponseUrl();
		Log.d(TAG, String.format("Setting Response In Bundle: %s", respLink));
		
		Bundle _args = new Bundle();
		_args.putString("link", respLink);
		_args.putString("page", pageNumber.equals("last")? pageNumber :
			String.valueOf(Integer.parseInt(pageNumber)));
		_args.putString("title", pageTitle);

		Fragment _frag = ThreadFragment.newInstance( );

		FragmentUtils.fragmentTransaction(sourceActivity.getActivity(), 
				_frag, true, true, _args, "thread");
    }

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
    	
        mProgressDialog = 
        		ProgressDialog.show(sourceActivity.getActivity(), "Submitting...", "Please Wait...");
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		String attId = "";
    		if(bitmaps.size() != 0) {
    			publishProgress(
    					sourceActivity.getString(R.string.asyncDialogUploadAttachment));
    			//attId = HtmlFormUtils.submitAttachment(token, bitmaps, post);
    			publishProgress(
    					sourceActivity.getString(R.string.asyncDialogUploadDone));
    		}
    		
    		publishProgress(
    				sourceActivity.getString(R.string.asyncDialogSubmitting));
    		if(HtmlFormUtils.submitPost(doType, token, thread, post, attId, text))
    			Log.d(TAG, "Html Form Submitted");
    		else
    			Log.d(TAG, "Form Submit Failed");
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
    }
    
    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#onProgressUpdate(Progress[])
     */
    @Override
    protected void onProgressUpdate(String...progress) {
        mProgressDialog.setMessage(progress[0]);
    }
}