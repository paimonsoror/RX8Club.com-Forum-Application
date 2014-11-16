package com.normalexception.app.rx8club.view.thread;

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

public class ThreadModel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String postCount, viewCount, myPosts;
	private String startUser, lastUser;
	private String link, lastLink, lastPostTime;
	private String forum;
	private boolean isAnnouncement, isSticky, isLocked, isFavorite, isPoll, hasAttachment = false;
	private boolean isStub = false;
	
	public ThreadModel() {
		this.isStub = false;
	}
	
	public ThreadModel(boolean stub) {
		this.isStub = stub;
	}
	
	/**
	 * Report if this is a stub thread.  Typically used for the end of new threads
	 * @return	True if stub thread.
	 */
	public boolean isStub() {
		return this.isStub;
	}
	
	/**
	 * Report if thread is an announcement
	 * @return	True if announcement
	 */
	public boolean isAnnouncement() {
		return this.isAnnouncement;
	}
	
	/**
	 * Set if thread is an announcement
	 * @param ann	True if announcement
	 */
	public void setAnnouncement(boolean ann) {
		this.isAnnouncement = ann;
	}
	
	/**
	 * Set the last post time
	 * @param time	Set the last post time
	 */
	public void setLastPostTime(String time) {
		this.lastPostTime = time;
	}
	
	/**
	 * Report the last post time
	 * @return	The last post time
	 */
	public String getLastPostTime() {
		return this.lastPostTime;
	}
	
	/**
	 * Report the forum of the thread
	 * @return	Report the forum of the thread
	 */
	public String getForum() {
		return forum;
	}
	
	/**
	 * Set the source forum
	 * @param frm The source forum of the thread
	 */
	public void setForum(String frm) {
		this.forum = frm;
	}
	
	/**
	 * Report if thread is a poll thread
	 * @return	True if the thread has a poll
	 */
	public boolean isPoll() {
		return isPoll;
	}
	
	/**
	 * Set if the thread is / has a poll
	 * @param ip	True if the thread has a poll
	 */
	public void setPoll(boolean ip) {
		this.isPoll = ip;
	}
	
	/**
	 * Report if thread has an attachment
	 * @return	True if has attachment
	 */
	public boolean hasAttachment() {
		return hasAttachment;
	}
	
	/**
	 * Set if thread has an attachment
	 * @param attach	True if has attachment
	 */
	public void setHasAttachment(boolean attach) {
		hasAttachment = attach;
	}
	
	/**
	 * Report if thread is favorite
	 * @return	True if favorite
	 */
	public boolean isFavorite() {
		return isFavorite;
	}
	
	/**
	 * Set if the thread is a favorite
	 * @param fave	True if favorite
	 */
	public void setFavorite(boolean fave) {
		isFavorite = fave;
	}

	/**
	 * Report if the thread is sticky
	 * @return	True if sticky thread
	 */
	public boolean isSticky() {
		return isSticky;
	}

	/**
	 * Set if the thread is sticky
	 * @param isSticky	True if sticky thread
	 */
	public void setSticky(boolean isSticky) {
		this.isSticky = isSticky;
	}

	/**
	 * Report if the thread is locked
	 * @return	True if thread locked
	 */
	public boolean isLocked() {
		return isLocked;
	}

	/**
	 * Set if the thread is locked
	 * @param isLocked	True if thread locked
	 */
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	/**
	 * Report the thread title
	 * @return	The thread title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the thread title
	 * @param title	The thread title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Report the number of posts the current user
	 * has in the thread
	 * @return	The current users post count within the thread
	 */
	public String getMyPosts() {
		if(myPosts == null || myPosts.length() == 0)
			return "0";
		else
			return myPosts;
	}
	
	/**
	 * Set the number of posts the user has in the thread
	 * @param myPosts	The number of posts the user has in the thread
	 */
	public void setMyPosts(String myPosts) {
		this.myPosts = myPosts;
	}

	/**
	 * Report the total posts in the thread
	 * @return	The total posts in the thread
	 */
	public String getPostCount() {
		return postCount;
	}

	/**
	 * Set the total posts in the thread
	 * @param postCount	The total posts in the thread
	 */
	public void setPostCount(String postCount) {
		this.postCount = postCount;
	}

	/**
	 * Report the view count for the thread
	 * @return	The thread's view count
	 */
	public String getViewCount() {
		return viewCount;
	}

	/**
	 * Set the view count for the thread	
	 * @param viewCount	The thread's view count
	 */
	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}

	/**
	 * Report the user that started the thread
	 * @return	The user that started the thread
	 */
	public String getStartUser() {
		return startUser;
	}

	/**
	 * Set the user that started the thread
	 * @param startUser	The user that started the thread
	 */
	public void setStartUser(String startUser) {
		this.startUser = startUser;
	}

	/**
	 * Report the last user to post in the thread
	 * @return	The last user to post in the thread
	 */
	public String getLastUser() {
		return lastUser;
	}

	/**
	 * Set the last user that posted in the thread
	 * @param lastUser	The last user that posted
	 */
	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}
	
	/**
	 * Report the link to the thread
	 * @return	The link to the thread
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * Set the link to the thread
	 * @param lnk	The link to the thread
	 */
	public void setLink(String lnk) {
		this.link = lnk;
	}
	
	/**
	 * Report the link to the last page of thread
	 * @return	The link to the thread
	 */
	public String getLastLink() {
		return lastLink;
	}
	
	/**
	 * Set the link to the last page of thread
	 * @param lnk	The link to the thread
	 */
	public void setLastLink(String lnk) {
		this.lastLink = lnk;
	}
}
