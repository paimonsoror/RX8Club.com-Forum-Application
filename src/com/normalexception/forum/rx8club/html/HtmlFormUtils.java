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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import ch.boye.httpclientandroidlib.HttpEntity;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.NameValuePair;
import ch.boye.httpclientandroidlib.StatusLine;
import ch.boye.httpclientandroidlib.client.ClientProtocolException;
import ch.boye.httpclientandroidlib.client.entity.UrlEncodedFormEntity;
import ch.boye.httpclientandroidlib.client.methods.HttpPost;
import ch.boye.httpclientandroidlib.client.methods.HttpUriRequest;
import ch.boye.httpclientandroidlib.entity.mime.HttpMultipartMode;
import ch.boye.httpclientandroidlib.entity.mime.MultipartEntity;
import ch.boye.httpclientandroidlib.entity.mime.content.FileBody;
import ch.boye.httpclientandroidlib.entity.mime.content.StringBody;
import ch.boye.httpclientandroidlib.impl.client.DefaultHttpClient;
import ch.boye.httpclientandroidlib.message.BasicNameValuePair;
import ch.boye.httpclientandroidlib.protocol.ExecutionContext;
import ch.boye.httpclientandroidlib.protocol.HttpContext;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.user.UserProfile;
import com.normalexception.forum.rx8club.utils.Utils;

public class HtmlFormUtils {	
	private static String responseUrl = "";
	private static final String TAG = "HtmlFormUtils";

	/**
	 * Submit a form and its contents
	 * @param url	The url to submit the form to
	 * @param nvps	The name value pair of form contents
	 * @return		True if it worked
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private static boolean formSubmit(String url, List<NameValuePair> nvps) 
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
		
		HttpPost httpost = new HttpPost(url);	
		Log.d(TAG, "[Submit] Submit URL: " + url);
		
	    httpost.setEntity(new UrlEncodedFormEntity(nvps));

    	HttpContext context = LoginFactory.getInstance().getHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();
    	StatusLine statusLine = response.getStatusLine();
    	
    	Log.d(TAG, "[Submit] Status: " + statusLine.getStatusCode());
    	if (entity != null) {
    		//entity.consumeContent();
    		httpost.releaseConnection();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		Log.d(TAG, "[Submit] Response URL: " + responseUrl);
    		
    		return true;
    	}
    	
		return false;
	}
	
	/**
	 * Update user profile
	 * @param token		 The users security token	
	 * @param title		 The user title
	 * @param homepage	 The users homepage
	 * @param bio		 The users bio
	 * @param location	 The users location
	 * @param interests  The users interests
	 * @param occupation The users occupation
	 * @return			 True if success
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean updateProfile(String token, String title, String homepage, String bio, 
					   					String location, String interests, String occupation) 
		throws ClientProtocolException, IOException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", token));
		nvps.add(new BasicNameValuePair("do", "updateprofile"));
		nvps.add(new BasicNameValuePair("customtext", title));
		nvps.add(new BasicNameValuePair("homepage", homepage));
		nvps.add(new BasicNameValuePair("userfield[field1]", bio));
		nvps.add(new BasicNameValuePair("userfield[field2]", location));
		nvps.add(new BasicNameValuePair("userfield[field3]", interests));
		nvps.add(new BasicNameValuePair("userfield[field4]", occupation));
		
		return formSubmit(WebUrls.profileUrl, nvps);
    
	}
	
	/**
	 * Delete private message
	 * @param securityToken	Users security token
	 * @param pmid			The private message id number
	 * @return				True if success
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean deletePM(String securityToken, String pmid) 
			throws ClientProtocolException, IOException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", "managepm"));
    	nvps.add(new BasicNameValuePair("dowhat", "delete"));
    	nvps.add(new BasicNameValuePair("pm[" + pmid + "]","0_today"));
    	
    	return formSubmit(WebUrls.pmUrl, nvps);
	}
	
	/**
	 * Convenience method to send a private message
	 * @param doType		Submit type
	 * @param securityToken User's security token	
	 * @param post			The text from the PM
	 * @param subject		The subject of the PM
	 * @param recips		The recipient of the PM
	 * @param pmid			The pm id number
	 * @return				True if the submit worked
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean submitPM(String doType, String securityToken, 
			                String post, String subject, String recips, String pmid) 
			throws ClientProtocolException, IOException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", doType));
    	nvps.add(new BasicNameValuePair("recipients", recips));
    	nvps.add(new BasicNameValuePair("title", subject));
    	nvps.add(new BasicNameValuePair("pmid", pmid));
    	
    	return formSubmit(WebUrls.pmSubmitAddress + pmid, nvps);
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
	public static boolean submitPost(String doType, String securityToken, String thread, 
							String postNumber, String post) 
			throws ClientProtocolException, IOException {
    	
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("t", thread));
		nvps.add(new BasicNameValuePair("p", postNumber));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
		nvps.add(new BasicNameValuePair("fromquickreply", "1"));
		nvps.add(new BasicNameValuePair("parseurl", "1"));
    	nvps.add(new BasicNameValuePair("do", doType));
    	
    	return formSubmit(WebUrls.quickPostAddress + thread, nvps);
	}
	
	/**
	 * Submit a post edit
	 * @param securityToken	The security token of the posting
	 * @param postNumber	The post number being edited
	 * @param posthash		The post hash number
	 * @param poststart		The post start time
	 * @param post			The post text
	 * @return				True if submit successful
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean submitEdit(String securityToken, String postNumber, 
							  String posthash, String poststart, String post) 
			throws ClientProtocolException, IOException {
    	
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("p", postNumber));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do","updatepost"));
    	nvps.add(new BasicNameValuePair("posthash", posthash));
    	nvps.add(new BasicNameValuePair("poststarttime", poststart));
 
    	return formSubmit(WebUrls.updatePostAddress + postNumber, nvps);
	}
	
	/**
	 * Submit a request to the server to delete the post
	 * @param securityToken	The session security token
	 * @param postNum		The post number to delete
	 * @return				True if delete successful
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static boolean submitDelete(String securityToken, String postNum)
		throws ClientProtocolException, IOException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("postid", postNum));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do","deletepost"));
    	nvps.add(new BasicNameValuePair("deletepost", "delete"));
 
    	return formSubmit(WebUrls.deletePostAddress + postNum, nvps);
	}
	
	/**
	 * Submit a request to upload an attachment to the server
	 * @param securityToken
	 * @param filePath
	 * @param thread
	 * @param postnum
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	//TODO Coming Soon....
	public static boolean submitAttachment(String securityToken, String filePath, 
			String thread, String postnum) throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = 
				LoginFactory.getInstance().getClient();
		
		MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
		entity.addPart("s", new StringBody(""));
		entity.addPart("securitytoken", new StringBody(securityToken));
		entity.addPart("do", new StringBody("manageattach"));
		entity.addPart("t", new StringBody(thread));
		entity.addPart("f", new StringBody("6"));
		entity.addPart("p", new StringBody(""));
		entity.addPart("poststarttime", new StringBody(""));
		entity.addPart("editpost", new StringBody("0"));
		entity.addPart("posthash", new StringBody(""));
		entity.addPart("MAX_FILE_SIZE", new StringBody("2097152"));
		entity.addPart("upload", new StringBody("Upload"));
		entity.addPart("attachmenturl[]", new StringBody(""));
		
		File fileToUpload = new File(filePath);
		FileBody fileBody = new FileBody(fileToUpload, "application/octet-stream");
		entity.addPart("attachment[]", fileBody);

		HttpPost httpPost = new HttpPost("http://some-web-site");
		httpPost.setEntity(entity);
		HttpResponse response = httpclient.execute(httpPost);
		HttpEntity result = response.getEntity();
		
		return false;
	}
	
	/**
	 * Report the contents of the post that we are intending
	 * on editing
	 * @param securityToken	The security token of the session
	 * @return				The response page 
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static Document getEditPostPage(String securityToken, String postid) 
			throws ClientProtocolException, IOException {
		String output = "";
		
		DefaultHttpClient httpclient = 
				LoginFactory.getInstance().getClient();
		
		HttpPost httpost = new HttpPost(WebUrls.editPostAddress + postid);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	
    	httpost.setEntity(new UrlEncodedFormEntity(nvps));

    	HttpResponse response = 
    			httpclient.execute(httpost, LoginFactory.getInstance().getHttpContext());
    	HttpEntity entity = response.getEntity();
    	
    	if(entity != null) {
	    	// Get login results (in this case the forum frontpage0
			BufferedReader in = new BufferedReader(new InputStreamReader(
					entity.getContent()));
			
			StringBuilder sb = new StringBuilder();
			
			String inputLine; 
			while ((inputLine = in.readLine()) != null) 
				sb.append(inputLine);
			
			output = sb.toString();
			
			in.close();	
			
			//entity.consumeContent();
			httpost.releaseConnection();
    	}
		
		return Jsoup.parse(output);
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
	public static boolean newThread(String forumId, String s, String token,
							 String posthash, String subject, String post) 
			throws ClientProtocolException, IOException {

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("s", s));
		nvps.add(new BasicNameValuePair("securitytoken", token));
		nvps.add(new BasicNameValuePair("f", forumId));
		nvps.add(new BasicNameValuePair("do", "postthread"));
		nvps.add(new BasicNameValuePair("posthash", posthash));
		nvps.add(new BasicNameValuePair("poststarttime", Long.toString(Utils.getTime())));
		nvps.add(new BasicNameValuePair("subject", subject));
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		
		return formSubmit(WebUrls.newThreadAddress + forumId, nvps);
	}
	
	/**
	 * Report the response url
	 * @return	The response url
	 */
	public static String getResponseUrl() {
		return WebUrls.rootUrl + responseUrl;
	}

    /**
     * Report the value inside of an input element
     * @param pan	The panel where all of the input elements reside
     * @param name	The name of the input to get the value for
     * @return		The string value of the input
     */
    public static String getInputElementValue(Document pan, String name) {
    	return pan.select("input[name=" + name + "]").attr("value");
    }

}
