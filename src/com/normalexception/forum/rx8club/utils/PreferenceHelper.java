package com.normalexception.forum.rx8club.utils;

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

import com.normalexception.forum.rx8club.enums.FontSize;

/**
 * Helper class for the preference manager
 */
public class PreferenceHelper {
	
	public final static String PREFS_NAME = "app_prefs";
	
	/**
	 * Get the font size from the preference manager
	 * @param context	The application context
	 * @return			The font size
	 */
	public static int getFontSize(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        return Integer.parseInt(prefs.getString("fontsize", "12"));
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
     * Report if advertise option is enabled
     * @param context	Application context
     * @return			True if option enabled
     */
    public static boolean isAdvertiseEnabled(Context context) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
    	return prefs.getBoolean("advertise", true);
    }
    
    /**
     * Convenience method to set the advertise enabled option
     * @param context	The application context
     * @param newValue	The new option value
     */
    public static void setAdvertiseEnabled(Context context, boolean newValue) {
    	SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        Editor prefsEditor = prefs.edit();
        prefsEditor.putBoolean("advertise", newValue);
        prefsEditor.commit();
    }
}
