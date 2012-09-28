package com.normalexception.forum.rx8club.view;

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
 
import java.io.Serializable;

/**
 * Simple container that will hold the view contents to be referenced
 * later.  This class was created so that the view information would be
 * held in memory and quickly restored during a configuration change
 */
public class ViewContents implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private int clr;
	private String texts[], text;
	private int id;
	private boolean span, html;
	
	public ViewContents(int clr, String text, int id, boolean html, boolean span) {
		this.clr = clr;
		this.text = text;
		this.id = id;
		this.span = span;
		this.html = html;
	}
	
	public ViewContents(int clr, String texts[], int id, boolean span) {
		this.clr = clr;
		this.texts = texts;
		this.id = id;
		this.span = span;
	}
	
	public ViewContents(int clr, String text, int id, boolean html) {
		this.clr = clr;
		this.text = text;
		this.id = id;
		this.html = html;
	}
	
	public boolean isHtml() {
		return html;
	}
	
	public void setHtml(boolean html) {
		this.html = html;
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String text) {
		this.text = text;
	}
	
	public int getClr() {
		return clr;
	}

	public void setClr(int clr) {
		this.clr = clr;
	}

	public String[] getTexts() {
		return texts;
	}

	public void setTexts(String[] texts) {
		this.texts = texts;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isSpan() {
		return span;
	}

	public void setSpan(boolean span) {
		this.span = span;
	}

}
