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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TableRow;
import android.widget.TextView;

import com.normalexception.forum.rx8club.preferences.PreferenceHelper;

/**
 * A custom textview that handles a lot of the common settings for what
 * a textview entails in the ui
 */
public class CTextDetail extends TextView {
	
	private TableRow.LayoutParams params = null;

	/**
	 * Default constructor
	 * @param context	Source context
	 */
	public CTextDetail(Context context) {
		super(context);
	}
	
	/**
	 * Constructor to a new text detail object
	 * @param context	Source context
	 * @param user		The thread user
	 * @param lastuser  The last thread responder
	 */
	public CTextDetail(Context context, String user, String lastuser) {
		super(context);
		float scaledText = (float) PreferenceHelper.getFontSize(context);
    	setTextSize((float) (scaledText * 0.75));
    	setTextColor(Color.WHITE);
    	setTypeface(null, Typeface.ITALIC);

    	StringBuilder userText = new StringBuilder();
    	userText.append("\tStarted By: ");
    	userText.append(user);
    	if(lastuser.length() != 0) {
    		userText.append(",\tLast: ");
    		userText.append(lastuser);
    	}
    	setText(userText.toString());
    	
    	params = new TableRow.LayoutParams();
        params.span = 5;  
        params.weight = 1f;
	}
	
	/**
	 * Report the text parameters
	 * @return	Text parameters for view
	 */
	public TableRow.LayoutParams getTextParameters() {
		return params;
	}
}
