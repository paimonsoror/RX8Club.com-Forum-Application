package com.normalexception.app.rx8club.view.threadpost;

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

import java.util.List;

public class PostView {
	private String userName, userTitle, userLocation;
	private String joinDate, postDate;
	private String userPostCount, userPost;
	private String postId, token;
	private String userImageUrl;
	private List<String> likes;
	private List<String> attachments;
	private boolean isLoggedInUser = false;
	
	public void setUserImageUrl(String url) {
		userImageUrl = url;
	}
	
	public String getUserImageUrl() {
		return userImageUrl;
	}
	
	public void setLikes(List<String> likes) {
		this.likes = likes;
	}
	
	public List<String> getLikes() {
		return likes;
	}
	
	/**
	 * Set the user's location
	 * @param loc	The user location
	 */
	public void setUserLocation(String loc) {
		userLocation = loc;
	}
	
	/**
	 * Report the user's location
	 * @return	The user's location
	 */
	public String getUserLocation() {
		return userLocation;
	}
	
	/**
	 * Set the security token
	 * @param tok	The security token
	 */
	public void setSecurityToken(String tok) {
		token = tok;
	}
	
	/**
	 * Get the security token for the user
	 * @return	The security token
	 */
	public String getToken() {
		return token;
	}
	
	/**
	 * Get the post id for the post
	 * @return	The post ID
	 */
	public String getPostId() {
		return postId;
	}
	
	/**
	 * Set the id number of the post
	 * @param pid	The post id number
	 */
	public void setPostId(String pid) {
		postId = pid;
	}
	
	/**
	 * Report if post is by logged in user
	 * @return True if post is by logged in user
	 */
	public boolean isLoggedInUser() {
		return isLoggedInUser;
	}
	
	/**
	 * Set if the post is by the logged in user
	 * @param isl	True if post is by logged in user
	 */
	public void setIsLoggedInUser(boolean isl) {
		isLoggedInUser = isl;
	}
	
	/**
	 * Report the user name
	 * @return	Report the post's user name
	 */
	public String getUserName() {
		return userName;
	}
	
	/**
	 * Set the post's user name
	 * @param userName	The post's user name
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	/**
	 * Report the title of the user
	 * @return	The user's title
	 */
	public String getUserTitle() {
		return userTitle;
	}
	
	/**
	 * Set the user's title
	 * @param userTitle	The user's title
	 */
	public void setUserTitle(String userTitle) {
		this.userTitle = userTitle;
	}
	
	/**
	 * Report the user's join date
	 * @return	The user's join date
	 */
	public String getJoinDate() {
		return joinDate;
	}
	
	/**
	 * Set the user's join date
	 * @param joinDate	The user's join date
	 */
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}
	
	/**
	 * Report the reply post date
	 * @return	The reply post date
	 */
	public String getPostDate() {
		return postDate;
	}
	
	/**
	 * Set the reply post date
	 * @param postDate	The reply post date
	 */
	public void setPostDate(String postDate) {
		this.postDate = postDate;
	}
	
	/**
	 * Report the user's post count
	 * @return	The user's total post count
	 */
	public String getUserPostCount() {
		return userPostCount;
	}
	
	/**
	 * Set the user's post count
	 * @param userPostCount	The user's total post count
	 */
	public void setUserPostCount(String userPostCount) {
		this.userPostCount = userPostCount;
	}
	
	/**
	 * Get the user's post
	 * @return	The user's post
	 */
	public String getUserPost() {
		return userPost;
	}
	
	/**
	 * Set the user's post
	 * @param userPost	The user's post
	 */
	public void setUserPost(String userPost) {
		this.userPost = userPost;
	}
	
	/**
	 * Set the post's attachments
	 * @param attachments	A list of attachments
	 */
	public void setAttachments(List<String> attachments) {
		this.attachments = attachments;
	}
	
	/**
	 * Report the list of attachments
	 * @return	The list of attachments
	 */
	public List<String> getAttachments() {
		return attachments;
	}
}
