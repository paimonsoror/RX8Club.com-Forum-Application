package com.normalexception.forum.rx8club.view.pm;

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

public class PMView {
	private String title, user, date, time, link, token;

	/**
	 * Report the time of the PM
	 * @return	The PM time
	 */
	public String getTime() {
		return time;
	}

	/**
	 * Set the time of the PM
	 * @param time	The PM's received time
	 */
	public void setTime(String time) {
		this.time = time;
	}

	/**
	 * Report the security token for the post
	 * @return	The security token
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Set the security token	
	 * @param token	The security token
	 */
	public void setToken(String token) {
		this.token = token;
	}

	/**
	 * Report the PM Link
	 * @return	The PM Link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Set the PM Link
	 * @param link	The PM Link
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Report the PM Title
	 * @return	The PM Title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Set the PM Title
	 * @param title	The PM Title
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Report the user
	 * @return	The pm user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * Set the user that sent the PM
	 * @param user	The PM sender
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * Get the received date of the PM
	 * @return	The PM date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Set the date the PM was received
	 * @param date	The PM date
	 */
	public void setDate(String date) {
		this.date = date;
	}
}
