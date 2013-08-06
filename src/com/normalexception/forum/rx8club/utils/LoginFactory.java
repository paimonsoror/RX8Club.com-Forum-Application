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
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.SharedPreferences;
import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.params.ClientPNames;
import ch.boye.httpclientandroidlib.client.protocol.ClientContext;
import ch.boye.httpclientandroidlib.conn.ClientConnectionManager;
import ch.boye.httpclientandroidlib.cookie.Cookie;
import ch.boye.httpclientandroidlib.impl.client.BasicCookieStore;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.impl.conn.PoolingClientConnectionManager;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.params.BasicHttpParams;
import ch.boye.httpclientandroidlib.params.HttpConnectionParams;
import ch.boye.httpclientandroidlib.params.HttpParams;
import ch.boye.httpclientandroidlib.protocol.BasicHttpContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.httpclient.KeepAliveStrategy;
import com.normalexception.forum.rx8club.httpclient.RedirectStrategy;
import com.normalexception.forum.rx8club.httpclient.RetryHandler;

/**
 * Singleton class for the login information
 */
public class LoginFactory {
	
	private static LoginFactory _instance = null;
	
	private static final String TAG = "Application:LoginFactory";
	
	private String password = null;
	
	private DefaultHttpClient httpclient = null;
	
	private boolean isLoggedIn = false;
	
	private SharedPreferences pref = null;
	
	private static final String PREFS_NAME = "MyPrefsFile";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_AUTOLOGIN = "autologin";
	private static final String PREF_REMEMBERME = "rememberme";
	
	private static BasicCookieStore cookieStore;
	private static HttpContext httpContext;
	
	private static boolean isGuestMode = false;
	private static boolean isInitialized = false;
	
	/**
	 * Constructor
	 */
	protected LoginFactory() {
		Log.v(TAG, "Initializing Login Factory");
		pref = MainApplication
				.getAppContext()
				.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE);
		
		initializeClientInformation();
	}
	
	/**
	 * Initialize the client, cookie store, and context
	 */
	private void initializeClientInformation() {
		Log.d(TAG, "Initializing Client...");
		
		HttpParams params = new BasicHttpParams();
	    params.setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true); 
	    HttpConnectionParams.setConnectionTimeout(params, 10000);
	    HttpConnectionParams.setSoTimeout(params, 10000);
	    HttpConnectionParams.setTcpNoDelay(params, true);    
	    
	    cookieStore = new BasicCookieStore();
	    httpContext = new BasicHttpContext();
	    httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	    httpclient = new DefaultHttpClient();
	    ClientConnectionManager mgr = httpclient.getConnectionManager();
	    httpclient = new DefaultHttpClient(
	    		new PoolingClientConnectionManager(mgr.getSchemeRegistry()),
	    		params);
	    httpclient.log.enableDebug(true);
	    
	    // Follow Redirects
	    httpclient.setRedirectStrategy(new RedirectStrategy());
	    
	    // Setup retry handler
	    httpclient.setHttpRequestRetryHandler(new RetryHandler());
	    
	    // Setup KAS
	    httpclient.setKeepAliveStrategy(new KeepAliveStrategy());
	    
	    isInitialized = true;
	}
	
	/**
	 * Report the context
	 * @return	The http context
	 */
	public HttpContext getHttpContext() {
		return httpContext;
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
	 * Set that we are running in guest mode
	 */
	public void setGuestMode() {		
		Log.d(TAG, "Clearing Cookies");
		httpclient.getCookieStore().clear();
		
		if(!isInitialized)
    		initializeClientInformation();
		
		Log.d(TAG, "Guest Mode Enabled...");
		isGuestMode = true;
	}
	
	/**
	 * Report if running in guest mode
	 * @return	True if guest mode
	 */
	public boolean isGuestMode() {
		Log.d(TAG, String.format("Checking If Guestmode: %B", isGuestMode));
		return isGuestMode;
	}
	
	/**
	 * Check if the user is logged in
	 * @return	True if logged in, false if else
	 */
	public boolean isLoggedIn() {
		Log.d(TAG, String.format("Checking If Logged In: %B", isLoggedIn));
		return isLoggedIn;
	}
	
	/**
	 * Clear cookies and logoff
	 */
	public void logoff(boolean clearPrefs) {
		this.password = "";
		if(clearPrefs)
			this.savePreferences(false, false);
		httpclient.getCookieStore().clear();
		httpclient.getConnectionManager().shutdown();
		isLoggedIn = false;
		isInitialized = false;
		
		Log.d(TAG, "Destroying Client...");
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
		
		httpclient = getClient();
    	HttpPost httpost = new HttpPost(WebUrls.loginUrl);

    	if(!isInitialized)
    		initializeClientInformation();
    	
    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	String password = PasswordUtils.hexMd5(this.password);
    	nvps.add(new BasicNameValuePair("vb_login_username", UserProfile.getUsername()));
    	nvps.add(new BasicNameValuePair("vb_login_md5password", password));
    	nvps.add(new BasicNameValuePair("vb_login_md5password_utf", password));
    	nvps.add(new BasicNameValuePair("do", "login"));

    	httpost.setEntity(new UrlEncodedFormEntity(nvps));

    	HttpResponse response = httpclient.execute(httpost, getHttpContext());
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		List<Cookie> cookies = cookieStore.getCookies();        	
        	boolean val = response.getStatusLine().getStatusCode() != 400;
        	//entity.consumeContent();
        	httpost.releaseConnection();
        	
        	for(Cookie cookie : cookies)
        		if(cookie.getName().equals("bbimloggedin") && cookie.getValue().toLowerCase().equals("yes"))
        			isLoggedIn = true;
        	
        	isGuestMode = !isLoggedIn;
        	return val && isLoggedIn;
    	}
    	
    	return false;
	}
}