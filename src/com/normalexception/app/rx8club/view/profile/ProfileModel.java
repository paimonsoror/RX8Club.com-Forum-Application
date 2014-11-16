package com.normalexception.app.rx8club.view.profile;

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

public class ProfileModel {
	private String name, link, text;

	/**
	 * Report the name of the thread that the user posted on
	 * @param name	The name of the thread
	 */
	public void setName(String name) { 
		this.name = name; 
	}
	
	/**
	 * Set the link of the thread the user posted on
	 * @param link	The link of the thread
	 */
	public void setLink(String link) { 
		this.link = link; 
	}
	
	/**
	 * Set the last snippet of text from the thread
	 * @param txt	The snippet of text
	 */
	public void setText(String txt) { 
		this.text = txt; 
	}

	/**
	 * Report the name of the thread
	 * @return	The name of the thread
	 */
	public String getName() {
		return name;
	}

	/**
	 * Report the link of the thread
	 * @return	The link of the thread
	 */
	public String getLink() {
		return link;
	}

	/**
	 * Report the snippet of text
	 * @return	The snippet of text
	 */
	public String getText() {
		return text;
	}

	public String toString() { return name + ", " + link + ", " + text; }
}
