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

import com.normalexception.forum.rx8club.preferences.PreferenceHelper;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

/**
 * A custom textview that handles a lot of the common settings for what
 * a textview entails in the ui
 */
public class CTextView extends TextView {
	
	/**
	 * Default constructor
	 * @param context	Source context
	 */
	public CTextView(Context context) {
		super(context);
	}
	
	/**
	 * Constructor to a custom TextView
	 * @param context	Source context
	 * @param listener	Source listener
	 * @param id		Id for the view
	 */
	public CTextView(Context context, OnClickListener listener, int id) {
		super(context);
		setId(id);
    	setOnClickListener(listener);
    	setTextSize((float) PreferenceHelper.getFontSize(context));
    	setTextColor(Color.WHITE);
        setPadding(5, 5, 5, 5);
	}
}
