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

/**
 * Container for user profile information
 */
public class UserProfile {
	private static String id = null;
	private static String user = null;
	private static String title = null;
	private static String link = null;
	private static String posts = null;
	private static String join = null;
	
	/**
	 * Set the user id number
	 * @param id	The user id number
	 */
	public static void setUserId(String id) {
		UserProfile.id = id;
	}
	
	/**
	 * Report the user id number
	 * @return	The user id number
	 */
	public static String getUserId() {
		return UserProfile.id;
	}
	
	/**
	 * Set the user name
	 * @param name	The user name
	 */
	public static void setUsername(String name) {
		UserProfile.user = name;
	}
	
	/**
	 * Report the user name
	 * @return	The user name
	 */
	public static String getUsername() {
		return UserProfile.user;
	}
	
	/**
	 * Set the user forum title
	 * @param title The user title
	 */
	public static void setUserTitle(String title) {
		UserProfile.title = title;
	}
	
	/**
	 * Report the user title
	 * @return The user title
	 */
	public static String getUserTitle() {
		return UserProfile.title;
	}
	
	/**
	 * Set the userp rofile link
	 * @param lnk	The user profile link
	 */
	public static void setUserProfileLink(String lnk) {
		UserProfile.link = lnk;
	}
	
	/**
	 * Report the user profile link	
	 * @return	The user profile link
	 */
	public static String getUserProfileLink() {
		return UserProfile.link;
	}
	
	/**
	 * Check if the user profile is initialized
	 * @return	True if user and title has been set
	 */
	public static boolean isInitialized() {
		return ((user != null) && (title != null));
	}
	
	/**
	 * Report the user post count
	 * @return	The user post count
	 */
	public static String getUserPostCount() {
		return posts;
	}
	
	/**
	 * Set the user post count
	 * @param post	The user post count
	 */
	public static void setUserPostCount(String post) {
		UserProfile.posts = post;
	}
	
	/**
	 * Set the users join date
	 * @param date	The user join date
	 */
	public static void setUserJoinDate(String date) {
		UserProfile.join = date;
	}
	
	/**
	 * Report the user join date
	 * @return	The user join date
	 */
	public static String getUserJoinDate() {
		return UserProfile.join;
	}
}
