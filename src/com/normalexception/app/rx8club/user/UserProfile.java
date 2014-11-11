package com.normalexception.app.rx8club.user;

import java.io.Serializable;

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
public class UserProfile implements Serializable {

	private static final long serialVersionUID = 1L;
	private String id = null;
	private String user = null;
	private String title = null;
	private String link = null;
	private String posts = null;
	private String join = null;
	private String image = null;
	
	private static UserProfile _instance = null;
	
	/**
	 * Report an instance of the UserProfile.  If one isn't 
	 * available, one will be created
	 * @return	An instance of the user profile
	 */
	public static UserProfile getInstance() {
		if (_instance == null)
			_instance = new UserProfile();
		return _instance;
	}
	
	/**
	 * Create a new user profile
	 */
	protected UserProfile() {
		id = user = title = link = posts = join = image = "";
	}
	
	/**
	 * Copy a user profile
	 * @param source	The source to copy from
	 */
	protected UserProfile(UserProfile source) {
		id    = source.id;
		user  = source.user;
		title = source.title;
		link  = source.link;
		posts = source.posts;
		join  = source.join;
		image = source.image;
	}
	
	/**
	 * Copy a user profile object to the stored instance 
	 * @param source	The source profile to copy
	 */
	public void copy(UserProfile source) {
		_instance = new UserProfile(source);
	}
	
	/**
	 * Set the user id number
	 * @param id	The user id number
	 */
	public void setUserId(String id) {
		this.id = id;
	}
	
	/**
	 * Report the user id number
	 * @return	The user id number
	 */
	public String getUserId() {
		return this.id;
	}
	
	/**
	 * Set the user name
	 * @param name	The user name
	 */
	public void setUsername(String name) {
		this.user = name;
	}
	
	/**
	 * Report the user name
	 * @return	The user name
	 */
	public String getUsername() {
		return this.user;
	}
	
	/**
	 * Report the username that has dots and spaces
	 * replaced
	 * @return	The html formatted username
	 */
	public String getHtmlUsername() {
		return this.user
				.replace(".", "-")
				.replace(" ", "-");
	}
	
	/**
	 * Set the user forum title
	 * @param title The user title
	 */
	public void setUserTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Report the user title
	 * @return The user title
	 */
	public String getUserTitle() {
		return this.title;
	}
	
	/**
	 * Set the userp rofile link
	 * @param lnk	The user profile link
	 */
	public void setUserProfileLink(String lnk) {
		this.link = lnk;
	}
	
	/**
	 * Report the user profile link	
	 * @return	The user profile link
	 */
	public String getUserProfileLink() {
		return this.link;
	}
	
	/**
	 * Check if the user profile is initialized
	 * @return	True if user and title has been set
	 */
	public boolean isInitialized() {
		return ((user != null) && (title != null));
	}
	
	/**
	 * Report the user post count
	 * @return	The user post count
	 */
	public String getUserPostCount() {
		return posts;
	}
	
	/**
	 * Set the user post count
	 * @param post	The user post count
	 */
	public void setUserPostCount(String post) {
		this.posts = post;
	}
	
	/**
	 * Set the users join date
	 * @param date	The user join date
	 */
	public void setUserJoinDate(String date) {
		this.join = date;
	}
	
	/**
	 * Report the user join date
	 * @return	The user join date
	 */
	public String getUserJoinDate() {
		return this.join;
	}
	
	/**
	 * Set the user's image link
	 * @param	img	The user's image link
	 */
	public void setUserImageLink(String img) {
		this.image = img;
	}
	
	/**
	 * Report the user image link
	 * @return 	The user image link
	 */
	public String getUserImageLink() {
		return this.image;
	}
}
