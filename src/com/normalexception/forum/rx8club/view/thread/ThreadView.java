package com.normalexception.forum.rx8club.view.thread;

public class ThreadView {
	private String title;
	private String postCount, viewCount, myPosts;
	private String startUser, lastUser;
	private String link;
	private boolean isSticky, isLocked;

	public boolean isSticky() {
		return isSticky;
	}

	public void setSticky(boolean isSticky) {
		this.isSticky = isSticky;
	}

	public boolean isLocked() {
		return isLocked;
	}

	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMyPosts() {
		return myPosts;
	}
	
	public void setMyPosts(String myPosts) {
		this.myPosts = myPosts;
	}

	public String getPostCount() {
		return postCount;
	}

	public void setPostCount(String postCount) {
		this.postCount = postCount;
	}

	public String getViewCount() {
		return viewCount;
	}

	public void setViewCount(String viewCount) {
		this.viewCount = viewCount;
	}

	public String getStartUser() {
		return startUser;
	}

	public void setStartUser(String startUser) {
		this.startUser = startUser;
	}

	public String getLastUser() {
		return lastUser;
	}

	public void setLastUser(String lastUser) {
		this.lastUser = lastUser;
	}
	
	public String getLink() {
		return link;
	}
	
	public void setLink(String lnk) {
		this.link = lnk;
	}
}
