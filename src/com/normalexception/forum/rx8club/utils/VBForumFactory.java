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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.util.Log;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.MainApplication;

/**
 * Classes that pertain to a VB type forum
 */
public class VBForumFactory {
	
	private static VBForumFactory _instance = null;
	private static final String TAG = "Application:VBForumFactory";
	
	private static final String urlAddress = "http://www.rx8club.com";
	
	private static final String postSubmitAddress = 
			"http://www.rx8club.com/newreply.php?do=newreply&noquote=1&p=";
	
	private static final String newThreadAddress =
			"http://www.rx8club.com/newthread.php?do=newthread&f=";
	
	/**
	 * Constructor
	 */
	protected VBForumFactory() {
		Log.v(TAG, "Creating Forum Factory");
	}
	
	/**
	 * Grab an instance of the VBForumFactory, if one doesn't exist
	 * then create one
	 * @return	An instance of the VBForumFactory
	 */
	public static VBForumFactory getInstance() {
		if(_instance == null)
			_instance = new VBForumFactory();
		return _instance;
	}
	
	/**
	 * Return the root address of the forum
	 * @return	The root address of the forum
	 */
	public static String getRootAddress() {
		return urlAddress;
	}

	/**
	 * Get the frontpage for the forum
	 * @param lf	The login factory object
	 * @return		The output text for the frontpage
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getForumFrontpage(LoginFactory lf) throws ClientProtocolException, IOException {
		return getForumPage(lf, urlAddress);
	}
	
	/**
	 * Submit a post to the server
	 * @param securityToken	The posting security token
	 * @param thread		The thread number
	 * @param postNumber	The post number
	 * @param post			The actual post
	 * @return				True if the post was successful
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public boolean submitPost(String securityToken, String thread, String postNumber, String post) 
			throws ClientProtocolException, IOException {
    	DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(postSubmitAddress + postNumber);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("t", thread));
		nvps.add(new BasicNameValuePair("p", postNumber));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", "postreply"));
    	
    	long secondsSinceEpoch = getTime();
    	
    	nvps.add(new BasicNameValuePair("poststarttime", Long.toString(secondsSinceEpoch)));
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpResponse response = httpclient.execute(httpost);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		return true;
    	}
    	
		return false;
	}
	
	/**
	 * Submit a new thread to the server
	 * @param forumId	The category id
	 * @param s			? not sure ?
	 * @param token		The security token
	 * @param posthash	The post hash code
	 * @param subject	The user defined subject
	 * @param post		The user defined initial post
	 * @return			True if successful
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public boolean newThread(String forumId, String s, String token,
							 String posthash, String subject, String post) 
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
		long secondsSinceEpoch = getTime();
		
		HttpPost httpost = new HttpPost(newThreadAddress + forumId);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("s", s));
		nvps.add(new BasicNameValuePair("securitytoken", token));
		nvps.add(new BasicNameValuePair("f", forumId));
		nvps.add(new BasicNameValuePair("do", "postthread"));
		nvps.add(new BasicNameValuePair("posthash", posthash));
		nvps.add(new BasicNameValuePair("poststarttime", Long.toString(secondsSinceEpoch)));
		nvps.add(new BasicNameValuePair("subject", subject));
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		
		httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpResponse response = httpclient.execute(httpost);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		return true;
    	}
    	
		return false;
	}
	
	/**
	 * Report the time since the epoch
	 * @return	Time since epoch
	 */
	public long getTime() {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
    	calendar.clear();
    	calendar.set(2011, Calendar.OCTOBER, 1);
    	return calendar.getTimeInMillis() / 1000L;
	}
	
	/**
	 * Get the page context from from a supplied forum address
	 * @param lf	The login factory object
	 * @param addr	The address to grab information from
	 * @return		The output text for the page
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getForumPage(LoginFactory lf, String addr) throws ClientProtocolException, IOException {
		DefaultHttpClient client = null;
		String output = null;
		
		// Grab the login client
		try {
			client = lf.getClient();
			if(client == null) {
				Log.w(TAG,"HTTPClient was null!");
				
				// Try logging in again
				lf.login();
				
				client = lf.getClient();
			}
		} catch (Exception e) {
			BugSenseHandler.sendException(e);
		}
		
		// If client isn't null, continue
		if(client != null) {
			HttpGet httpost = null;
			
			try {
				httpost = new HttpGet(addr);
			} catch (IllegalStateException e) {
				BugSenseHandler.sendExceptionMessage("Address", addr, e);
				
				// Sometimes we pass an address with no host, fix
				// that issue here
				addr = addr.startsWith("/")? addr : "/" + addr;
				httpost = new HttpGet(getRootAddress() + addr);			
			}
			
	    	HttpResponse response = client.execute(httpost);
	    	HttpEntity entity = response.getEntity();
	    	
	    	// Get login results (in this case the forum frontpage0
			BufferedReader in = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			
			StringBuilder sb = new StringBuilder();
			
			String inputLine; 
			while ((inputLine = in.readLine()) != null) 
				sb.append(inputLine);
			
			output = sb.toString();
			
			in.close();	
			
			entity.consumeContent();
		} else {
			Toast.makeText(MainApplication.getAppContext(),
					"Error With Credentials",
					Toast.LENGTH_SHORT).show();
		}
		
		return output;
	}
	
	/**
     * Grab the forum as a jsoup document
     * @return	A jsoup document object that contains the 
     * 			forum contents
     */
    public Document get(String addr) {  
    	LoginFactory lf = LoginFactory.getInstance();
    	
    	String output = "";	
		try {
			VBForumFactory ff = VBForumFactory.getInstance();
			output = ff.getForumPage(lf, addr);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error grabbing category page: " + e.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "Error grabbing category page: " + e.getMessage());
		}
		
	   	return Jsoup.parse(output);
    }
}
