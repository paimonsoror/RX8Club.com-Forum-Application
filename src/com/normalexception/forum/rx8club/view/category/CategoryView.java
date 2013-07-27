package com.normalexception.forum.rx8club.view.category;

public class CategoryView {
	private String title, link;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}
	public String getPostCount() {
		return postCount;
	}
	public void setPostCount(String postCount) {
		this.postCount = postCount;
	}
	private String threadCount, postCount;

}
