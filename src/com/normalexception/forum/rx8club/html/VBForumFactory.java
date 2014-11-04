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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import android.app.Activity;
import android.widget.Toast;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;
import ch.boye.httpclientandroidlib.util.EntityUtils;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.fragment.FragmentUtils;
import com.normalexception.forum.rx8club.httpclient.ClientUtils;
import com.normalexception.forum.rx8club.utils.Utils;

/**
 * Classes that pertain to a VB type forum
 */
public class VBForumFactory {
	
	private static VBForumFactory _instance = null;
	private Logger TAG =  LogManager.getLogger(this.getClass());
		
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
		return WebUrls.rootUrl;
	}

	/**
	 * Get the frontpage for the forum
	 * @param src   The source activity
	 * @param lf	The login factory object
	 * @return		The output text for the frontpage
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getForumFrontpage(Activity src, LoginFactory lf) 
			throws ClientProtocolException, IOException {
		return getForumPage(src, lf, WebUrls.rootUrl);
	}
	
	/**
	 * Get the page context from from a supplied forum address
	 * @param src   The source activity
	 * @param lf	The login factory object
	 * @param addr	The address to grab information from
	 * @return		The output text for the page
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public String getForumPage(Activity src, LoginFactory lf, String addr) 
			throws ClientProtocolException, IOException {
		String output = null;
		try {
			HttpClient client = null;		
			
			// Grab the login client
			Log.d(TAG, "Grabbing Login Client");
			client = lf.getClient();
			
			// If client isn't null, continue
			if(client != null && (addr != null && !addr.equals(""))) {
				HttpGet httpget = null;
				Log.d(TAG, "Resolving URL");
				addr = Utils.resolveUrl(addr);
				
				try {
					Log.d(TAG, "Executing HTTP Get on: " + addr);
					httpget = ClientUtils.getHttpGet(addr);
			    	output = 
		    			EntityUtils.toString( 
		    					client.execute( httpget, lf.getHttpContext() ).getEntity(), 
		    					"ISO-8859-1");
	
					httpget.releaseConnection();
					
					if(output == null || 
							output.equals("") || 
							output.contains("You are not logged in")) {
						Log.w(TAG, "Error Parsing Output!");
						Log.w(TAG, output);
						FragmentUtils.returnToLoginPage(src, false, false);
					}
				} catch (NullPointerException e) {		
					notifyError(src, 
							"Error Opening Page. This Has Been Logged", e);
				} catch (IllegalStateException e) {
					Log.e(TAG, e.getMessage(), e);
				}
			} else {
				notifyError(src, 
						"Error With Credentials", null);
			}
		} catch (Exception e) {
			notifyError(src, "No Internet Connection...", null);
			FragmentUtils.returnToLoginPage(src, false, false);
		}
		
		if(output == null || output.length() == 0) {
			notifyError(src, "No Internet Connection...", null);
			FragmentUtils.returnToLoginPage(src, false, false);		
		}
		
		return output;
	}
	
	/**
	 * Convenience method of displaying error to user and logging
	 * the exception
	 * @param src	The source activity
	 * @param msg	The message to post
	 * @param e		The exception to log
	 */
	private void notifyError(Activity src, final String msg, Exception e) {
		src.runOnUiThread(new Runnable() {
			  public void run() {
				Toast.makeText(MainApplication.getAppContext(),
						msg,
						Toast.LENGTH_SHORT).show();
			  }
		});
	}
	
	/**
     * Grab the forum as a jsoup document
     * @return	A jsoup document object that contains the 
     * 			forum contents
     */
    public Document get(Activity src, String addr) {  
    	LoginFactory lf = LoginFactory.getInstance();
    	
    	String output = "";	
		try {
			VBForumFactory ff = VBForumFactory.getInstance();
			output = ff.getForumPage(src, lf, addr);
			return Jsoup.parse(output);
		} catch (ClientProtocolException e) {
			Log.e(TAG, "Error grabbing category page: " + e.getMessage(), e);
		} catch (IOException e) {
			Log.e(TAG, "Error grabbing category page: " + e.getMessage(), e);
		} catch (IllegalArgumentException e) {
			Log.e(TAG, "Error grabbing category page: " + e.getMessage(), e);
		}
		
	   	return null;
    }
}
