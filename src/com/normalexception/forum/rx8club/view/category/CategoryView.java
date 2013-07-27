package com.normalexception.forum.rx8club.view.category;

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
 * Class that represents a Category item
 */
public class CategoryView {
	private String title, link;
	private String threadCount, postCount;
	
	/**
	 * Report the category title
	 * @return	The category title
	 */
	public String getTitle() {
		return title;
	}
	
	/**
	 * Set the category title
	 * @param title	The title
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	/**
	 * Report the link to the category
	 * @return	The category link
	 */
	public String getLink() {
		return link;
	}
	
	/**
	 * Set the category's link
	 * @param link	The link
	 */
	public void setLink(String link) {
		this.link = link;
	}
	
	/**
	 * Report the thread count within the category
	 * @return	The thread count
	 */
	public String getThreadCount() {
		return threadCount;
	}
	
	/**
	 * Set the thread count within the category
	 * @param threadCount	The thread count
	 */
	public void setThreadCount(String threadCount) {
		this.threadCount = threadCount;
	}
	
	/**
	 * Report the post count within the category
	 * @return	The post count
	 */
	public String getPostCount() {
		return postCount;
	}
	
	/**
	 * Set the post count within the category
	 * @param postCount	The post count
	 */
	public void setPostCount(String postCount) {
		this.postCount = postCount;
	}
}
