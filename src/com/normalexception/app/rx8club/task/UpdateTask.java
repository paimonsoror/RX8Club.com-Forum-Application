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

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.category.CategoryFragment;
import com.normalexception.app.rx8club.fragment.thread.ThreadFragment;
import com.normalexception.app.rx8club.html.HtmlFormUtils;

/**
 * Task used to move all post editing tasks to an async task
 */
public class UpdateTask extends AsyncTask<Void,Void,Void> {
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private ProgressDialog mProgressDialog;
	private Fragment sourceFragment;
	
	private String securitytoken, p, posthash, 
		poststarttime, msg, pageTitle;
	
	private boolean delete = false, deleteThread = false;
	
	public UpdateTask(Fragment sourceFragment, String token, String postid, 
					  String posthash, String poststarttime, String pageNumber,
					  String pageTitle, String message, boolean delete,
					  boolean deleteThread) {
		this.sourceFragment = sourceFragment;
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
		
		Bundle args = new Bundle();
		args.putString("link", url);
		args.putString("title", pageTitle);
		args.putString("page", "1");
		
		// Create new fragment and transaction
		Fragment newFragment = null;
		if(deleteThread) {
			newFragment = new CategoryFragment();
		} else {
			newFragment = new ThreadFragment(((ThreadFragment)sourceFragment).getParentCategory());
		}
		
		FragmentUtils.fragmentTransaction(sourceFragment.getActivity(), 
				newFragment, true, true, args);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
    	
    	if(delete) 
    		mProgressDialog = 
    			ProgressDialog.show(sourceFragment.getActivity(), "Deleting...", "Deleting Post...");
    	else
    		mProgressDialog = 
        		ProgressDialog.show(sourceFragment.getActivity(), "Submitting...", "Submitting Post...");
    }
}
