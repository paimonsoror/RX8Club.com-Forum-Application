package com.normalexception.app.rx8club.preferences;

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

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;

/**
 * Helper class for the preference manager
 */
public class PreferenceHelper {
	
	public final static String PREFS_NAME = "app_prefs";
	
	/**
	 * Report if the user wants to hide or show signatures
	 * @param context	The source context
	 * @return			The option value
	 */
	public static boolean isShowSignatures(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean("showsignatures", true);
	}
	
	/**
	 * Report if the user wants to hide old posts in the 'new posts'
	 * page
	 * @param context	The source context
	 * @return			The option value
	 */
	public static boolean hideOldPosts(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean("oldposts", false);
	}
	
	/**
	 * Report if the user wants the banner hidden
	 * @param context	The source context
	 * @return			The option value
	 */
	public static boolean isHideBanner(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getBoolean("hidebanner", false);
	}
	
	/**
	 * Get the thread image size from the preferences
	 * @param context	The source context
	 * @return			The option value
	 */
	public static double getThreadImageSize(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		String defaultSize = 
				context.getResources().getStringArray(R.array.threadImagePercentValues)[0];
		try {
			String prefsVal = prefs.getString("threadimagesize", defaultSize);
			return Double.parseDouble(prefsVal);
		} catch (Throwable e) {
			Log.w("Preferences", "Exception w/ Image Size, Resetting");
			Editor prefsEditor = prefs.edit();
	        prefsEditor.putString("threadimagesize", defaultSize);
	        prefsEditor.commit();
	        return Double.parseDouble(defaultSize);
		}
	}
	
	/**
	 * Report the option that the user has for the recently updated
	 * thread page to open
	 * @param context	The source context
	 * @return			The option value
	 */
	public static String getRecentlyUpdatedThreadPage(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getString("newthreadpage", "First");
	}
	
	/**
	 * Get the font size from the preference manager
	 * @param context	The application context
	 * @return			The font size
	 */
	public static int getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        try {
        	return Integer.parseInt(prefs.getString("fontsize", "12"));
        } catch (NumberFormatException e) {
        	setFontSize(MainApplication.getAppContext(), "12");
        	return 12;
        }
    }
	
	/**
	 * Get the users language from the preferences
	 * @param context	The application context
	 * @return			The selected language
	 */
	public static String getUserLanguage(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
		return prefs.getString("language", "English");
	}
	
	/**
	 * Set the font size
	 * @param context	The application context
	 * @param newValue	The new font value
	 */
    public static void setFontSize(Context context, String newValue) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putString("fontsize", newValue);
        prefsEditor.commit();
    }
    
    /**
     * Report if the favorites dialog is going to display as a pop up
     * menu, or an activity list.
     * @param context	The application context
     * @return			The favorites type value
     */
    public static boolean isFavoriteAsDialog(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("favorites_as_dialog", false);
    }

    /**
     * Report if advertise option is enabled
     * @param context	Application context
     * @return			True if option enabled
     */
    public static boolean isAdvertiseEnabled(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("appSig", true);
    }
    
    /**
     * Report if user wants to show likes in threads
     * @param context	Application context
     * @return			True if likes enabled
     */
    public static boolean isShowLikes(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("showlikes", true);
    }
    
    /**
     * Report if show avatar option is enabled
     * @param context	The application context
     * @return			True if option enabled
     */
    public static boolean isShowAvatars(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("showavatars", true);
    }
    
    /**
     * Report if show attachments option is enabled
     * @param context	The application context
     * @return			True if option enabled
     */
    public static boolean isShowAttachments(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("showattachments", true);
    }
    
    /**
     * Convenience method to set the advertise enabled option
     * @param context	The application context
     * @param newValue	The new option value
     */
    public static void setAdvertiseEnabled(Context context, boolean newValue) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("appSig", newValue);
        prefsEditor.commit();
    }
	
    /**
     * Report if user wants linear threads
     * @param context	The application context
     * @return			True if option enabled
     */
	public static boolean isLinearThread(Context context) {
		SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("linearthread", true);
	}
}
