package com.normalexception.app.rx8club.fragment;

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
import java.net.SocketTimeoutException;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.activities.MainActivity;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.user.UserProfile;

/**
 * Activity that is designed to handle all login related
 * GUI information.
 * 
 * Required Intent Parameters:
 * none
 */
public class LoginFragment extends Fragment implements OnClickListener, OnCheckedChangeListener {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	private ProgressDialog loadingDialog;
	private boolean userClick = false;
	
	public static LoginFragment getInstance(boolean userClicked) {
		LoginFragment lf = new LoginFragment();
		Bundle args = new Bundle(1);
		args.putBoolean("userclick", userClicked);
		lf.setArguments(args);
		return lf;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_login, container, false);    	
        return rootView;
    }
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        MainApplication.setState(AppState.State.LOGIN, this);
	        this.userClick = getArguments().getBoolean("userclick");

	        // First, check to see if we are already logged in,
	        // we could be still in memory so lets reuse the 
	        // session
	        if(!LoginFactory.getInstance().isLoggedIn() || this.userClick) {
	        	
	        	if(this.userClick)
	        		LoginFactory.getInstance().logoff(false);
	        	
		        Button loginButton = (Button)view.findViewById(R.id.loginButton);
		        loginButton.setOnClickListener(this);
		        
		        Button guestButton = (Button)view.findViewById(R.id.guestButton);
		        guestButton.setOnClickListener(this);
		        
		        CheckBox checkBox = (CheckBox)view.findViewById(R.id.autoLoginBox);
		        checkBox.setOnCheckedChangeListener(this);
		        
		        LoginFactory lf = LoginFactory.getInstance();
		        boolean autoLogin = lf.getAutoLogin();
		        boolean rememberme = lf.getRememberMe();
		        
		        ((CheckBox)view.findViewById(R.id.autoLoginBox)).setChecked(autoLogin);
		        ((CheckBox)view.findViewById(R.id.rememberMeBox)).setChecked(rememberme);
		        
		       if (rememberme || autoLogin) {
		        	((TextView)view.findViewById(R.id.usernameText)).setText(lf.getStoredUserName());
		        	((TextView)view.findViewById(R.id.passwordText)).setText(lf.getStoredPassword());
		        	
		        	if(autoLogin && !this.userClick) {
		        		// Simulate login click
		        		onClick(view.findViewById(R.id.loginButton));
		        	}
		        }
		       
		       ((TextView)view.findViewById(R.id.versonCode)).setText(
		    		   getActivity().getPackageManager().getPackageInfo(
		    				   getActivity().getPackageName(), 0).versionName);
	        } else {
	        	Log.d(TAG, "Already Logged In, Moving To Main Screen");
	        	loadMainPage();
	        }
    	} catch (Exception e) {
    		Log.e(TAG, "Unexpected Error!: " + e.getMessage(), e);
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
	    	   if(((TextView)getView().findViewById(R.id.usernameText)).getText().length() == 0 || 
	    			   ((TextView)getView().findViewById(R.id.passwordText)).getText().length() == 0) {
	    		   Toast.makeText(getActivity(), 
	    				   R.string.loginFillInformation, 
	    				   Toast.LENGTH_LONG).show();
	    	   } else {
	    		   getActivity().runOnUiThread(new Runnable() {
						public void run() {
							((Button)getView().findViewById(R.id.loginButton)).setEnabled(false);
						}
					});	
		    	   new AsyncLogin().execute(getActivity(), null, null);
	    	   }
	    	   break;
	       case R.id.guestButton :
	    	   LoginFactory.getInstance().setGuestMode();
	    	   loadMainPage();
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
		private boolean timeout = false;

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(final Context... params) {
			
			getActivity().runOnUiThread(new Runnable() {
				  public void run() {
					  loadingDialog = 
							  ProgressDialog.show(params[0], 
									  getString(R.string.loggingIn), 
									  getString(R.string.pleaseWait), 
									  true);
				  }
			});
			
			lf = LoginFactory.getInstance();
			UserProfile.getInstance()
				.setUsername(((TextView)getView().findViewById(R.id.usernameText)).getText().toString());
			lf.setPassword(((TextView)getView().findViewById(R.id.passwordText)).getText().toString());
			lf.savePreferences(((CheckBox)getView().findViewById(R.id.autoLoginBox)).isChecked(), 
					   ((CheckBox)getView().findViewById(R.id.rememberMeBox)).isChecked());
			try {
			    Log.v(TAG, "Checking for network connection before we log in");
				if(LoginFactory.haveNetworkConnection())
					loggedIn = lf.login();
				else
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(params[0], R.string.networkNotAvailable, Toast.LENGTH_LONG).show();
						}
					});
			} catch (SocketTimeoutException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage(), e);
				timeout = true;
			} catch (NoSuchAlgorithmException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage(), e);
			} catch (org.apache.http.client.ClientProtocolException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage(), e);
			} catch (IOException e) {
				Log.e(TAG, "Error Logging In " + e.getMessage(), e);
			} finally {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						((Button)getView().findViewById(R.id.loginButton)).setEnabled(true);
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
			int message = 0;
			
			// Dismiss the loading dialog, but make sure we trap if
			// the dialog went away before we got here.
			try {
				loadingDialog.dismiss();
				loadingDialog = null;
			} catch (Exception e) { }
			
			if(loggedIn) {
				((MainActivity)getActivity()).setUserMenu();
				((MainActivity)getActivity()).invalidateOptionsMenu();
				loadMainPage();
			} else if (timeout) {
				message = R.string.networkTimedOut;
			} else {
				message = R.string.loginAuthFailed;
			}
			
			// Display message to user if exists
			if(message != 0) {
				getActivity().runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainApplication.getAppContext(), 
								R.string.loginAuthFailed, Toast.LENGTH_LONG).show();
					}
				});
			}
		}
	}
	
	/**
	 * Lets load the main page, generally if we are logged in
	 */
	private void loadMainPage() {        
        TimeoutFactory.getInstance().updatePongTime();
        ((MainActivity)getActivity()).displayView(0, false);
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.CompoundButton.OnCheckedChangeListener#onCheckedChanged(android.widget.CompoundButton, boolean)
	 */
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
		switch(arg0.getId()) {
		case R.id.autoLoginBox :
			((CheckBox)getView().findViewById(R.id.rememberMeBox)).setChecked(arg1);
			break;
		}
	}
}
