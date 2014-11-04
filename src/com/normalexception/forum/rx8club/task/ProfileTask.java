package com.normalexception.forum.rx8club.task;

import java.io.IOException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.fragment.UserCpFragment;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

public class ProfileTask extends AsyncTask<Void,Void,Void>{
	private ProgressDialog mProgressDialog;
	
	private String token, customtext, homepage, 
				   bio, location, interests, 
				   occupation; 
	private Activity sourceActivity;
	private String TAG = "ProfileTask";
	
	public ProfileTask(Activity source, String token, String title, String homepage, String bio, 
					   String location, String interests, String occupation) {
		this.sourceActivity = source;
		this.token = token;
		this.customtext = title;
		this.homepage = homepage;
		this.bio = bio;
		this.location = location;
		this.interests = interests;
		this.occupation = occupation;
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
		
		Intent _intent = new Intent(sourceActivity, UserCpFragment.class);
		_intent.putExtra("link", HtmlFormUtils.getResponseUrl());
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
        		ProgressDialog.show(this.sourceActivity, "Updating...", "Updating Profile...");
    }

    /*
     * (non-Javadoc)
     * @see android.os.AsyncTask#doInBackground(Params[])
     */
    @Override
    protected Void doInBackground(Void... params) {
    	try {
    		HtmlFormUtils.updateProfile(token, customtext, homepage, 
    				bio, location, interests, occupation);
		} catch (ClientProtocolException e) {
			Log.e(TAG, e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
        return null;
    }
}
