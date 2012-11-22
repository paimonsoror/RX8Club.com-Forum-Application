package com.normalexception.forum.rx8club.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.normalexception.forum.rx8club.WebUrls;

public class HtmlFormUtils {	
	private static String responseUrl = "";
	
	public static boolean deletePM(String securityToken, String pmid) 
			throws ClientProtocolException, IOException {
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(WebUrls.pmUrl);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", "managepm"));
    	nvps.add(new BasicNameValuePair("dowhat", "delete"));
    	nvps.add(new BasicNameValuePair("pm[" + pmid + "]","0_today"));
    	
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
		return false;
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
    	DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(WebUrls.pmSubmitAddress + pmid);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", doType));
    	nvps.add(new BasicNameValuePair("recipients", recips));
    	nvps.add(new BasicNameValuePair("title", subject));
    	nvps.add(new BasicNameValuePair("pmid", pmid));
    	
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
		return false;
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
    	DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(WebUrls.postSubmitAddress + postNumber);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("t", thread));
		nvps.add(new BasicNameValuePair("p", postNumber));
		nvps.add(new BasicNameValuePair("loggedinuser", UserProfile.getUserId()));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do", doType));
    	
    	long secondsSinceEpoch = Utils.getTime();
    	
    	nvps.add(new BasicNameValuePair("poststarttime", Long.toString(secondsSinceEpoch)));
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
		return false;
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
    	DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(WebUrls.updatePostAddress + postNumber);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("message", post));
		nvps.add(new BasicNameValuePair("p", postNumber));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do","updatepost"));
    	nvps.add(new BasicNameValuePair("posthash", posthash));
    	nvps.add(new BasicNameValuePair("poststarttime", poststart));
 
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {    					
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
		return false;
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
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
    	
		HttpPost httpost = new HttpPost(WebUrls.deletePostAddress + postNum);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("postid", postNum));
		nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	nvps.add(new BasicNameValuePair("do","deletepost"));
    	nvps.add(new BasicNameValuePair("deletepost", "delete"));
 
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {    					
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
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
		
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
		
		HttpPost httpost = new HttpPost(WebUrls.editPostAddress + postid);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
    	nvps.add(new BasicNameValuePair("securitytoken", securityToken));
    	
    	httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

    	HttpResponse response = httpclient.execute(httpost);
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
			
			entity.consumeContent();
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
		DefaultHttpClient httpclient = LoginFactory.getInstance().getClient();
		long secondsSinceEpoch = Utils.getTime();
		
		HttpPost httpost = new HttpPost(WebUrls.newThreadAddress + forumId);
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

		HttpContext context = new BasicHttpContext();
    	HttpResponse response = httpclient.execute(httpost, context);
    	HttpEntity entity = response.getEntity();

    	if (entity != null) {
    		entity.consumeContent();
    		
    		HttpUriRequest request = (HttpUriRequest) context.getAttribute(
    		        ExecutionContext.HTTP_REQUEST);

    		responseUrl = request.getURI().toString();
    		
    		return true;
    	}
    	
		return false;
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
