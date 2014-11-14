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
import com.normalexception.app.rx8club.fragment.pm.PrivateMessageInboxFragment;
import com.normalexception.app.rx8club.html.HtmlFormUtils;

public class DeletePmTask extends AsyncTask<Void,Void,Void>{
	private ProgressDialog mProgressDialog;
	private Fragment sourceFragment;
	
	private String token, pmid;
	private boolean outbound = false;

	private Logger TAG =  LogManager.getLogger(this.getClass());

	/**
	 * Async Task handler for deleting a Private messages
	 * @param sourceActivity	The source activity	
	 * @param securityToken		The security token of the session
	 * @param pmid				The id of the PM to delete
	 * @param outbound			True if showing sent items
	 */
	public DeletePmTask(Fragment source, String securityToken, 
			String pmid, boolean outbound) {
		this.sourceFragment = source;
		this.token = securityToken;
		this.pmid = pmid;
		this.outbound = outbound;
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

		Bundle args = new Bundle();
		args.putString("link", HtmlFormUtils.getResponseUrl());
		args.putBoolean(PrivateMessageInboxFragment.showOutboundExtra, outbound);
		FragmentUtils.fragmentTransaction(sourceFragment.getActivity(), 
				new PrivateMessageInboxFragment(), false, false, args);
    }

	/*
	 * (non-Javadoc)
	 * @see android.os.AsyncTask#onPreExecute()
	 */
    @Override
    protected void onPreExecute() {
    	
        mProgressDialog = 
        		ProgressDialog.show(sourceFragment.getActivity(), "Deleting...", "Deleting PM...");
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		HtmlFormUtils.deletePM(this.token, this.pmid);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
    }	
}
