package com.normalexception.app.rx8club.view.pmitem;

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

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;

public class PMItemView extends LinearLayout {
	
	private TextView pmRecipientsText;
	
	public PMItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_newpm_children, this, true);
		setupChildren();
	}
	
	public PMItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_newpm_children, this, true);
		setupChildren();
	}

	public PMItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_newpm_children, this, true);
		setupChildren();
	}

	/**
	 * Setup the children we contain in this view
	 */	
	private void setupChildren() {
		pmRecipientsText = (TextView) findViewById(R.id.pmRecipientsText);
	}
	
	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static PMItemView inflate(ViewGroup parent) {
		PMItemView pmItemView = (PMItemView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_newpm, parent, false);
		return pmItemView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param pm	The model we are going to use to populate the view
	 */
	public void setPMItem (final PMItemModel pm) {

        pmRecipientsText
        	.setText(pm.getName());
               
	}
}
