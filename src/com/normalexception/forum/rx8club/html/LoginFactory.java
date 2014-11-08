package com.normalexception.forum.rx8club.html;

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
import java.util.Locale;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.enums.VBulletinKeys;
import com.normalexception.forum.rx8club.httpclient.ClientUtils;
import com.normalexception.forum.rx8club.httpclient.KeepAliveStrategy;
import com.normalexception.forum.rx8club.httpclient.RedirectStrategy;
import com.normalexception.forum.rx8club.httpclient.RetryHandler;
import com.normalexception.forum.rx8club.user.UserProfile;
import com.normalexception.forum.rx8club.utils.PasswordUtils;

/**
 * Singleton class for the login information
 */
public class LoginFactory {
	
	private static LoginFactory _instance = null;
	
	private static Logger TAG =  LogManager.getLogger(LoginFactory.class);
	
	private String password = null;
	
	private CloseableHttpClient httpclient = null;
	
	private boolean isLoggedIn = false;
	
	private SharedPreferences pref = null;
	
	/**
	 * Preference specific variables
	 */
	private static final String PREFS_NAME = "login_preferences";
	private static final String PREF_USERNAME = "username";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_AUTOLOGIN = "autologin";
	private static final String PREF_REMEMBERME = "rememberme";
	private static final String PREF_SIGNATURE = "signature";
	
	/**
	 * Network specific variables
	 */
	private static final String NETWORK_WIFI = "WIFI";
	private static final String NETWORK_MOBILE = "MOBILE";
	private static final String NETWORK_ETH = "ETH";
	private static final String NETWORK_ETHERNET = "ETHERNET";
	
	private static BasicCookieStore cookieStore;
	private static HttpContext httpContext;
	
	private static boolean isGuestMode = false;
	private static boolean isInitialized = false;
	
	private static int TIMEOUT = 10000;
	
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
	 * Check to see if a network connection exists
	 * @return	True if network connection exists
	 */
	public static boolean haveNetworkConnection() {
	    boolean haveConnectedWifi = false;
	    boolean haveConnectedMobile = false;
	    boolean haveConnectedEth = false;
	    
	    Log.d(TAG, "Checking available network connections");
	    ConnectivityManager cm = 
	    		(ConnectivityManager) 
	    			MainApplication.getAppContext()
	    				.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo[] netInfo = cm.getAllNetworkInfo();
	    for (NetworkInfo ni : netInfo) {
	    	Log.d(TAG, 
	    			String.format("%s is %sconnected", 
	    					ni.getTypeName(), ni.isConnected()? "" : "not "));
	        if (ni.getTypeName().equalsIgnoreCase(LoginFactory.NETWORK_WIFI))
	            if (ni.isConnected()) {
	            	Log.d(TAG, "Wifi Connection Detected");
	                haveConnectedWifi = true;
	            }
	        if (ni.getTypeName().equalsIgnoreCase(LoginFactory.NETWORK_MOBILE))
	            if (ni.isConnected()) {
	            	Log.d(TAG, "Mobile Connection Detected");
	                haveConnectedMobile = true;
	            }
	        if(ni.getTypeName().equalsIgnoreCase(LoginFactory.NETWORK_ETH) || 
	        		ni.getTypeName().equalsIgnoreCase(LoginFactory.NETWORK_ETHERNET))
	        	if (ni.isConnected()) {
	        		Log.d(TAG, "Ethernet Connection Detected");
	        		haveConnectedEth = true;
	        	}
	    }
	    return haveConnectedWifi || 
	    		haveConnectedMobile || 
	    			haveConnectedEth;
	}
	
	/**
	 * Report the cookie store
	 * @return	The cookie store
	 */
	public CookieStore getCookieStore() {
		return cookieStore;
	}
	
	/**
	 * Report the cookies as a string
	 * @return	The cookies as a string
	 */
	public String getCookies() {
		String cStr = "";
		String del = "";
		for(Cookie c : cookieStore.getCookies()) {
			cStr += del + c.getName() + "=" + c.getValue();
			del = ";";
		}
		return cStr;
	}
	
	/**
	 * Initialize the client, cookie store, and context
	 */
	private void initializeClientInformation() {
		Log.d(TAG, "Initializing Client...");
		
		/*
		Log.d(TAG, "Creating Custom Cache Configuration");
		CacheConfig cacheConfig = CacheConfig.custom()
		        .setMaxCacheEntries(1000)
		        .setMaxObjectSize(8192)
		        .build();
		*/
		
		Log.d(TAG, "Creating Custom Request Configuration");
	    RequestConfig rConfig = RequestConfig.custom()
	    		.setCircularRedirectsAllowed(true)
	    		.setConnectionRequestTimeout(TIMEOUT)
	    		.setSocketTimeout(TIMEOUT)
	    		.build();
	    
	    cookieStore = new BasicCookieStore();
	    httpContext = new BasicHttpContext();
	    
	    Log.d(TAG, "Building Custom HTTP Client");
	    HttpClientBuilder httpclientbuilder = HttpClients.custom();
	    //httpclientbuilder.setCacheConfig(cacheConfig);
	    httpclientbuilder.setDefaultRequestConfig(rConfig);
	    httpclientbuilder.setDefaultCookieStore(cookieStore);

	    Log.d(TAG, "Connection Manager Initializing");
	    PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	    cm.setMaxTotal(200);
	    cm.setDefaultMaxPerRoute(20);
	    httpclientbuilder.setConnectionManager(cm);
	    
	    Log.d(TAG, "Enable GZIP Compression");
	    httpclientbuilder.addInterceptorLast(new HttpRequestInterceptor() {

            public void process(
                    final HttpRequest request,
                    final HttpContext context) throws HttpException, IOException {
                if (!request.containsHeader("Accept-Encoding")) {
                    request.addHeader("Accept-Encoding", "gzip");
                }
            }

        });
        httpclientbuilder.addInterceptorLast(new HttpResponseInterceptor() {

            public void process(
                    final HttpResponse response,
                    final HttpContext context) throws HttpException, IOException {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    Header ceheader = entity.getContentEncoding();
                    if (ceheader != null) {
                        HeaderElement[] codecs = ceheader.getElements();
                        for (int i = 0; i < codecs.length; i++) {
                            if (codecs[i].getName().equalsIgnoreCase("gzip")) {
                                response.setEntity(
                                        new GzipDecompressingEntity(response.getEntity()));
                                return;
                            }
                        }
                    }
                }
            }

        });
	    
	    // Follow Redirects
        Log.d(TAG, "Registering Redirect Strategy");
	    httpclientbuilder.setRedirectStrategy(new RedirectStrategy());
	    
	    // Setup retry handler
	    Log.d(TAG, "Registering Retry Handler");
	    httpclientbuilder.setRetryHandler(new RetryHandler());
	    
	    // Setup KAS
	    Log.d(TAG, "Registering Keep Alive Strategy");
	    httpclientbuilder.setKeepAliveStrategy(new KeepAliveStrategy());
	    
	    httpclient = httpclientbuilder.build();
	    
	    //httpclient.log.enableDebug(
	    //		MainApplication.isHttpClientLogEnabled());
	    
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
       .putString(PREF_USERNAME, UserProfile.getInstance().getUsername())
       .putString(PREF_PASSWORD, this.password)
       .putBoolean(PREF_AUTOLOGIN, login)
       .putBoolean(PREF_REMEMBERME, remember)
       .commit();
	}
	
	/**
	 * Save the user's signature to the preferences
	 * @param signature	The user's signature
	 */
	public void saveSignature(String signature) {
		pref.edit().putString(PREF_SIGNATURE, signature).commit();
	}
	
	/**
	 * Report the user's signature.  If it doesn't exist, then post default
	 * @return	User's signature, or default
	 */
	public String getSignature() {
		return pref.getString(PREF_SIGNATURE, 
				MainApplication.getAppContext().getString(R.string.app_signature));
	}

	/**
	 * Set the password for the login, and don't forget to make sure
	 * it is already encoded
	 * @param pw	The password for the login user
	 */
	public void setPassword(String pw) {
		Log.v(TAG, "Setting Password");
		this.password = PasswordUtils.isValidMD5(pw)? 
				pw : PasswordUtils.hexMd5(pw);
	}
	
	/**
	 * Get the http client object
	 * @return	A DefaultHttpClient object
	 */
	public CloseableHttpClient getClient() {
		return httpclient;
	}
	
	/**
	 * Set that we are running in guest mode
	 */
	public void setGuestMode() {		
		Log.d(TAG, "Clearing Cookies");
		//httpclient.getCookieStore().clear();
		
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
		// Log.d(TAG, String.format("Checking If Guestmode: %B", isGuestMode));
		return isGuestMode;
	}
	
	/**
	 * Check if the user is logged in
	 * @return	True if logged in, false if else
	 */
	public boolean isLoggedIn() {
		// Log.d(TAG, String.format("Checking If Logged In: %B", isLoggedIn));
		return isLoggedIn;
	}
	
	/**
	 * Clear cookies and logoff
	 */
	public void logoff(boolean clearPrefs) {
		this.password = "";
		if(clearPrefs)
			this.savePreferences(false, false);
		//httpclient.getCookieStore().clear();
		try {
			httpclient.close();
		} catch (IOException ioe) {}
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
	public boolean login() 
			throws NoSuchAlgorithmException, ClientProtocolException, IOException {
		if(UserProfile.getInstance().getUsername() == null || password == null)
			return false;
		
		httpclient = getClient();
    	HttpPost httpost = ClientUtils.getHttpPost(WebUrls.loginUrl);

    	if(!isInitialized)
    		initializeClientInformation();
    	
    	List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	nvps.add(new BasicNameValuePair(VBulletinKeys.UserName.getValue(), 
    			UserProfile.getInstance().getUsername()));
    	nvps.add(new BasicNameValuePair(VBulletinKeys.Password.getValue(), 
    			this.password));
    	nvps.add(new BasicNameValuePair(VBulletinKeys.PasswordUtf.getValue(), 
    			this.password));
    	nvps.add(new BasicNameValuePair(VBulletinKeys.CookieUser.getValue(), "1"));
    	nvps.add(new BasicNameValuePair(VBulletinKeys.Do.getValue(), "login"));

    	httpost.setEntity(new UrlEncodedFormEntity(nvps));

    	try {
	    	HttpResponse response = httpclient.execute(httpost, getHttpContext());
	    	HttpEntity entity = response.getEntity();
	
	    	if (entity != null) {
	    		List<Cookie> cookies = cookieStore.getCookies();        	
	        	boolean val = 
	        			response.getStatusLine().getStatusCode() != HttpStatus.SC_BAD_REQUEST;
	        	Log.d(TAG, "Login status code: " + response.getStatusLine().getStatusCode());
	        	//httpost.releaseConnection();
	        	
	        	for(Cookie cookie : cookies)
	        		if(cookie.getName().equals(VBulletinKeys.CheckLoginStatus.getValue()) && 
	        				cookie.getValue().toLowerCase(Locale.US).equals("yes"))
	        			isLoggedIn = true;
	        	
	        	isGuestMode = !isLoggedIn;
	        	return val && isLoggedIn;
	    	}
    	} catch (Exception e) {
    		Log.e(TAG, "HTTP Response based error!", e);
    	}
    	
    	return false;
	}
}