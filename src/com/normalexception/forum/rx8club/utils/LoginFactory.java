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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.normalexception.forum.rx8club.MainApplication;

import android.app.Activity;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Singleton class for the login information
 */
public class LoginFactory {
	
	private static LoginFactory _instance = null;
	
	private static final String TAG = "Application:LoginFactory";
	private static final String urlAddress = "http://www.rx8club.com/login.php";
	
	private String password = null;
	
	private DefaultHttpClient httpclient = null;
	
	private boolean isLoggedIn = false;
	
	private List<String> cookieList = null;
	
	private SharedPreferences pref = null;
	
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_AUTOLOGIN = "autologin";
	private static final String PREF_REMEMBERME = "rememberme";
	
	/**
	 * Constructor
	 */
	protected LoginFactory() {
		Log.v(TAG, "Initializing Login Factory");
		cookieList = new ArrayList<String>();
		pref = MainApplication.getAppContext().getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
	}
	
	/**
	 * Get an instance of a LoginFactory class.  If an instance
	 * doesn't exist, create a new one
	 * @return	An instance of the LoginFactory
	 */
	public static LoginFactory getInstance() {
		if(_instance == null)
			_instance = new LoginFactory();
		
		return _instance;
	}
	
	/**
	 * Check if the autologin flag was set by the preference
	 * manager
	 * @return	True if autologin was set by manager
	 */
	public boolean getAutoLogin() {
		return pref.getBoolean(PREF_AUTOLOGIN, false);
	}
	
	/**
	 * Check if the rememberme flag was set by the preference
	 * manager
	 * @return	True if rememberme was set by manager
	 */
	public boolean getRememberMe() {
		return pref.getBoolean(PREF_REMEMBERME, false);
	}
	
	/**
	 * Get the stored user name from the preference manager
	 * @return	The username from the manager
	 */
	public String getStoredUserName() {
		return pref.getString(PREF_USERNAME, "");
	}
	
	/**
	 * Get the stored password from the preference manager
	 * @return	The password from the manager
	 */
	public String getStoredPassword() {
		return pref.getString(PREF_PASSWORD, "");
	}
	
	/**
	 * Save the preferences to the manager
	 * @param login		If autologin is enabled
	 * @param remember	If rememberme is enabled
	 */
	public void savePreferences(boolean login, boolean remember) {
 	   pref
       .edit()
       .putString(PREF_USERNAME, UserProfile.getUsername())
       .putString(PREF_PASSWORD, this.password)
       .putBoolean(PREF_AUTOLOGIN, login)
       .putBoolean(PREF_REMEMBERME, remember)
       .commit();
	}

	/**
	 * Set the password for the login
	 * @param pw	The password for the login user
	 */
	public void setPassword(String pw) {
		Log.v(TAG, "Setting Password");
		this.password = pw;
	}
	
	/**
	 * Get the http client object
	 * @return	A DefaultHttpClient object
	 */
	public DefaultHttpClient getClient() {
		return httpclient;
	}
	
	/**
	 * Check if the user is logged in
	 * @return	True if logged in, false if else
	 */
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	
	/**
	 * Clear cookies and logoff
	 */
	public void logoff() {
		this.password = "";
		this.savePreferences(false, false);
		httpclient.getCookieStore().clear();
		isLoggedIn = false;
	}
	
	/**
	 * Log in to the forum
	 * @return	True if the login was successful, false if else
	 * @throws NoSuchAlgorithmException
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public boolean login() throws NoSuchAlgorithmException, ClientProtocolException, IOException {
		if(UserProfile.getUsername() == null) {
			Log.e(TAG, "Username has not been set");
			return false;
		}
		
		if(password == null) {
			Log.e(TAG, "Password has not been set");
			return false;
		}
		
		httpclient = new DefaultHttpClient();
    	HttpPost httpost = new HttpPost(urlAddress);

    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	String password = hexMd5(this.password);
    	nvps.add(new BasicNameValuePair("vb_login_username", UserProfile.getUsername()));
    	nvps.add(new BasicNameValuePair("vb_login_md5password", password));
    	nvps.add(new BasicNameValuePair("vb_login_md5password_utf", password));
    	nvps.add(new BasicNameValuePair("do", "login"));

    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpResponse response = httpclient.execute(httpost);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        	for(Cookie cookie : cookies)
        		cookieList.add(cookie.toString());
        	
        	boolean val = response.getStatusLine().getStatusCode() != 400;
        	entity.consumeContent();
        	
        	for(Cookie cookie : cookies)
        		if(cookie.getName().equals("bbimloggedin") && cookie.getValue().toLowerCase().equals("yes"))
        			isLoggedIn = true;
        	
        	return val && isLoggedIn;
    	}
    	
    	return false;
	}
	
	/**
	 * Get the list of cookies
	 * @return	The cookie list
	 */
	public List<String> getCookieList() {
		return cookieList;
	}
	
	/**
	 * Get the count of available cookies
	 * @return	The number of cookies
	 */
	public int getCookieListCount() {
		return cookieList.size();
	}
	
	/**
	 * Convert the password to an md5 password
	 * @param password	A plaintext password
	 * @return			The md5 encoded password
	 * @throws NoSuchAlgorithmException
	 */
	private static String hexMd5(String password) throws NoSuchAlgorithmException {
		Log.v(TAG, "Creating MD5 Password");
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(password.getBytes());
		BigInteger hash = new BigInteger(1, md5.digest());
		return pad(hash.toString(16), 32, '0');
	}
	    
	/**
	 * Pad the md5 password
	 * @param s			The string to pad
	 * @param length	The total length to pad to
	 * @param pad		The pad character
	 * @return			The padded string
	 */
	private static String pad(String s, int length, char pad) {
		Log.v(TAG, "Padding MD5 Password");
		StringBuffer buffer = new StringBuffer(s);
		while (buffer.length() < length) {
			buffer.insert(0, pad);
		}
		return buffer.toString();
	}
}
