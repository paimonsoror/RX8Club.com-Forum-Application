package com.normalexception.app.rx8club;

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

public class WebUrls {
	
	public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36";

	public static final String rootUrl = "http://www.rx8club.com";
	public static final String marketUrl = "market://details?id=";
	
	public static final String loginUrl = rootUrl + "/login.php";
	public static final String newPostUrl = rootUrl + "/search.php?do=getnew";
	
	public static final String pmInboxUrl = rootUrl + "/private.php";
	public static final String pmSentUrl  = rootUrl + "/private.php?s=&pp=50&folderid=-1";
	
	public static final String profileUrl = rootUrl + "/profile.php";
	public static final String searchUrl = rootUrl + "/search.php?do=process&query=";
	public static final String userUrl = rootUrl + "/search.php?do=finduser&u=";

	public static final String adminLockUrl = rootUrl + "/postings.php";
	
	public static final String postSubmitAddress = 	rootUrl + "/newreply.php?do=newreply&noquote=1&p=";
	public static final String quickPostAddress = rootUrl + "/newreply.php?do=postreply&t=";
	public static final String newPmAddress = rootUrl + "/private.php?do=newpm";
	public static final String newThreadAddress = rootUrl + "/newthread.php?do=newthread&f=";
	public static final String editPostAddress = rootUrl + "/editpost.php?do=editpost&p=";
	public static final String updatePostAddress = 	rootUrl + "/editpost.php?do=updatepost&p=";
	public static final String deletePostAddress =	rootUrl + "/editpost.php?do=deletepost&p=";
	public static final String pmSubmitAddress = rootUrl + "/private.php?do=insertpm&pmid=";
	public static final String postAttachmentAddress = rootUrl + "/newattachment.php?do=manageattach&p=";
	public static final String postReportAddress = rootUrl + "/report.php?p=";
	
	public static final String userCpAddress = rootUrl + "/usercp.php";
	public static final String editProfile = rootUrl + "/profile.php?do=editprofile";
	
	public static final String paypalUrl = "https://www.paypal.com/cgi-bin/webscr" +
			"?cmd=_donations&business=XSEK8GC74RMMS&lc=US" +
			"&currency_code=USD&bn=PP%2dDonations" +
			"BF%3abtn_donateCC_LG%2egif%3aNonHosted";
}
