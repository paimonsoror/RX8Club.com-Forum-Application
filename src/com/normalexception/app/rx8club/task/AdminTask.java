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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.dialog.MoveThreadDialog;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.fragment.thread.ThreadFragment;
import com.normalexception.app.rx8club.html.HtmlFormUtils;

/**
 * Task generated to move the submit of a post to an async
 * task
 */
public class AdminTask extends AsyncTask<Void,String,Void>{

	private ProgressDialog mProgressDialog;
	private Fragment sourceFragment;
	
	private String token, thread, doType, deleteResponse;

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	public static final String LOCK_THREAD = "openclosethread";
	public static final String DELETE_THREAD = "dodeletethread";
	public static final String MOVE_THREAD = "movethread";
	public static final String DOMOVE_THREAD = "domovethread";
	
	public static final String DELETE_REASON = "Deleted From Mobile App";
	
	private static final Map<String, String> progressText;
	private static final Map<String, String> descriptionText;
	
	private String threadTitle = "";
	private Map<String,Integer> selectOptions = 
			new LinkedHashMap<String,Integer>();
	
	static
    {
			progressText = new HashMap<String, String>();
			progressText.put(LOCK_THREAD, "Un/Locking Thread...");
			progressText.put(DELETE_THREAD, "Deleting Thread...");
			progressText.put(MOVE_THREAD, "Moving Thread...");
			
			descriptionText = new HashMap<String, String>();
			descriptionText.put(LOCK_THREAD, "Lock / Unlock Thread");
			descriptionText.put(DELETE_THREAD, "Delete Thread");
			descriptionText.put(MOVE_THREAD, "Move Thread");
    }
	
	/*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		Log.d(TAG, progressText.get(doType));
    		
    		if(this.doType == DELETE_THREAD) {
    			HtmlFormUtils.adminTypePost(doType, token, thread, deleteResponse);
    		} else
    			HtmlFormUtils.adminTypePost(doType, token, thread, null);
    		
    		if(this.doType == MOVE_THREAD) {
	    		String response = HtmlFormUtils.getResponseUrl();
	    		Log.d(TAG, "Response: " + response);
	    			
	    		Document doc = Jsoup.parse(HtmlFormUtils.getResponseContent());
	    		
	    		threadTitle = HtmlFormUtils.getInputElementValueByName(doc, "title");
	    		Log.d(TAG, "Thread Title: " + threadTitle);
	    		
	    		Elements selects = doc.select("select[name=destforumid] > option");
	    		for(Element select : selects) {
	    			selectOptions.put(select.text(), 
	    					Integer.parseInt(select.attr("value")));
	    		}
	    		
	    		Log.d(TAG, "Parsed " + selectOptions.keySet().size() + " options");
    		}
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
    }
	
	/**
	 * Constructor to a LockTask
	 * @param sourceFragment	The source activity
	 * @param securityToken		The users security token
	 * @param threadNumber		The source thread number
	 */
	public AdminTask(Fragment sourceFragment, String securityToken, String threadNumber, String action) 
	{
		this.sourceFragment = sourceFragment;
		this.token = securityToken;
		this.thread = threadNumber;
		this.doType = action;
	}
	
	public String getType() {
		return doType;
	}
	
	public String getDescription() {
		return descriptionText.get(doType);
	}
	
	public void setDeleteResponse(String resp) {
		this.deleteResponse = resp;
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
        		ProgressDialog.show(sourceFragment.getActivity(), progressText.get(doType), "Please Wait...");
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
			Bundle args = sourceFragment.getArguments();
			boolean isLocked = args.getBoolean("locked");
			Log.d(TAG, "Setting Thread Lock To: " + !isLocked);
			args.putBoolean("locked", !isLocked);
			
			
			// Create new fragment and transaction
			Fragment newFragment = 
					ThreadFragment.newInstance();

			FragmentUtils.fragmentTransaction(sourceFragment.getActivity(), 
					newFragment, true, true, args);
		} else if (doType == MOVE_THREAD) {
			MoveThreadDialog mtd = new MoveThreadDialog(
					sourceFragment, token, thread, threadTitle, selectOptions);
			mtd.show();
		} else {
			//this.sourceFragment.finish();
			sourceFragment.getFragmentManager().popBackStack();
		}
    }
}
