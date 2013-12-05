package com.normalexception.forum.rx8club.preferences;

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

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.cache.Cache;
import com.normalexception.forum.rx8club.cache.FileCache;
import com.normalexception.forum.rx8club.dialog.FavoriteDialog;
import com.normalexception.forum.rx8club.dialog.SignatureDialog;
import com.normalexception.forum.rx8club.utils.SpecialNumberFormatter;

/**
 * Class used to set and save preferences
 */
public class Preferences extends PreferenceActivity {
	
	/*
	 * (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().setSharedPreferencesName(
                PreferenceHelper.PREFS_NAME);
        addPreferencesFromResource(R.xml.preferences);
        
        final Context ctx = this;
        
        Preference customAdv = (Preference)findPreference("customAdvertise");
        customAdv.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
        	@Override
        	public boolean onPreferenceClick(Preference arg0) {
        		SignatureDialog sd = new SignatureDialog(ctx);
        		sd.show();
        		return true;
        	}
        });
        
        Preference man_fave = (Preference)findPreference("manage_favorites");
        man_fave.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {			
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				FavoriteDialog fd = new FavoriteDialog(ctx);
				fd.registerToRemove();
				fd.show();
				return true;
			}
		});
        
        final Preference cache = (Preference)findPreference("appcache");
        cache.setSummary(
        		String.format("Cache Size: %s", 
        		SpecialNumberFormatter.readableFileSize(
        				(new Cache(ctx)).getCacheSize())));
        cache.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	new AsyncTask<Void,String,Void>() {
            		ProgressDialog loadingDialog;
            		
        		    @Override
        		    protected void onPreExecute() {
        		    	loadingDialog = 
        						ProgressDialog.show(ctx, 
        								getString(R.string.dialogClearingCache), 
        								getString(R.string.pleaseWait), true);
        		    }
        		    
        			@Override
        			protected Void doInBackground(Void... params) {
        				try {
        					(new FileCache(ctx)).clear();
        				} catch (Exception e) {}
		            	return null;
        			}
        			
	    			@Override
	    		    protected void onPostExecute(Void result) {
	    				loadingDialog.dismiss();
	    				cache.setSummary(
	    		        		String.format("Cache Size: %s", 
	    		        		SpecialNumberFormatter.readableFileSize(
	    		        				(new Cache(ctx)).getCacheSize())));
	    				Toast.makeText(ctx, 
            					R.string.dialogCacheCleared, 
            					Toast.LENGTH_SHORT).show();
	    			}
	    		}.execute();
                return true;
            }
        });
        
        Preference button = (Preference)findPreference("donate");
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	try {
            		startActivity(new Intent(Intent.ACTION_VIEW, 
            			Uri.parse(WebUrls.paypalUrl)));
            	} catch (ActivityNotFoundException e) {
            		// In the event that the browser isn't working
            		// properly
            		notifyError("Error Opening Browser, Sorry!");
            	}
                return true;
            }
        });
        
        Preference rate = (Preference)findPreference("rate");
        rate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference arg0) { 
            	try {
            		startActivity(
            			new Intent(Intent.ACTION_VIEW, 
            					Uri.parse(WebUrls.marketUrl + 
            			MainApplication.APP_PACKAGE)));
            	} catch (ActivityNotFoundException e) {
            		// In the event that the market isn't installed
            		// or is unavailable
            		notifyError("Error Opening Market, Sorry!");
            	}
                return true;
            }
        }); 
        
        try {
        	Preference version = (Preference)findPreference("version");
        	version.setSummary(
        			getPackageManager().getPackageInfo(
        					this.getPackageName(), 0).versionName);
        } catch (Exception e) {}
        
        try {
        	Preference build = (Preference)findPreference("build");
        	build.setSummary(
        			Integer.toString(
        					getPackageManager().getPackageInfo(
        							this.getPackageName(), 0).versionCode));
        } catch (Exception e) {}
    }
	
	/**
	 * Convenience method of displaying error to user and logging
	 * the exception
	 * @param src	The source activity
	 * @param msg	The message to post
	 * @param e		The exception to log
	 */
	private void notifyError(final String msg) {
		this.runOnUiThread(new Runnable() {
			  public void run() {
				Toast.makeText(MainApplication.getAppContext(),
						msg,
						Toast.LENGTH_SHORT).show();
			  }
		});
	}
}
