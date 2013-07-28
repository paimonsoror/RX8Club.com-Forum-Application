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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import ch.boye.httpclientandroidlib.client.ClientProtocolException;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import com.normalexception.forum.rx8club.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.UserProfile;

/**
 * Activity that is designed to handle all login related
 * GUI information.
 * 
 * Required Intent Parameters:
 * none
 */
public class LoginActivity extends ForumBaseActivity implements OnClickListener, OnCheckedChangeListener {

	private static String TAG = "LoginActivity";
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        super.setTitle("Please Enter Credentials");
	        setContentView(R.layout.activity_login);
	        
	        Button loginButton = (Button)findViewById(R.id.loginButton);
	        loginButton.setOnClickListener(this);
	        
	        CheckBox checkBox = (CheckBox)findViewById(R.id.autoLoginBox);
	        checkBox.setOnCheckedChangeListener(this);
	        
	        LoginFactory lf = LoginFactory.getInstance();
	        boolean autoLogin = lf.getAutoLogin();
	        boolean rememberme = lf.getRememberMe();
	        
	        ((CheckBox)findViewById(R.id.autoLoginBox)).setChecked(autoLogin);
	        ((CheckBox)findViewById(R.id.rememberMeBox)).setChecked(rememberme);
	        
	       if (rememberme || autoLogin) {
	        	((TextView)findViewById(R.id.usernameText)).setText(lf.getStoredUserName());
	        	((TextView)findViewById(R.id.passwordText)).setText(lf.getStoredPassword());
	        	
	        	if(autoLogin) {
	        		// Simulate login click
	        		onClick(findViewById(R.id.loginButton));
	        	}
	        }
	       
	       ((TextView)findViewById(R.id.versonCode)).setText(
	    		   getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName);
    	} catch (Exception e) {
    		Log.e(TAG, "Unexpected Error!: " + e.getMessage());
    	}
    }
   
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()){
	       case R.id.loginButton :
	    	   runOnUiThread(new Runnable() {
					public void run() {
						((Button)findViewById(R.id.loginButton)).setEnabled(false);
					}
				});	
	    	   new AsyncLogin().execute(this, null, null);
	    	   break;
		}		
	}
	
	/**
	 * Innerclass that is designed to handle all async login classes
	 * @see android.os.AsyncTask
	 */
	class AsyncLogin extends AsyncTask<Context, Void, Void> {
		private LoginFactory lf = null;
		private boolean loggedIn = false;

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(final Context... params) {
			runOnUiThread(new Runnable() {
				  public void run() {
					  loadingDialog = ProgressDialog.show(params[0], "Logging In", "Please wait...", true);
				  }
			});
			
			lf = LoginFactory.getInstance();
			UserProfile.setUsername(((TextView)findViewById(R.id.usernameText)).getText().toString());
			lf.setPassword(((TextView)findViewById(R.id.passwordText)).getText().toString());
			lf.savePreferences(((CheckBox)findViewById(R.id.autoLoginBox)).isChecked(), 
					   ((CheckBox)findViewById(R.id.rememberMeBox)).isChecked());
			try {
				loggedIn = lf.login();
			} catch (NoSuchAlgorithmException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage());
			} catch (ClientProtocolException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage());
			} catch (IOException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage());
			} finally {
				runOnUiThread(new Runnable() {
					public void run() {
						((Button)findViewById(R.id.loginButton)).setEnabled(true);
					}
				});	
			}
			return null;
		}
		
		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void param) {
			runOnUiThread(new Runnable() {
				  public void run() {
					  loadingDialog.dismiss();
				  }
			});
			
			if(loggedIn) {
				Intent in = new Intent(LoginActivity.this, MainActivity.class);
				startActivity(in);
				finish();
			} else {
				runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainApplication.getAppContext(), 
								"Sorry, Your Login Authentication Failed", Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch(arg0.getId()) {
		case R.id.autoLoginBox :
			((CheckBox)findViewById(R.id.rememberMeBox)).setChecked(arg1);
			break;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
	 */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
		// Do Nothing
	}
}
