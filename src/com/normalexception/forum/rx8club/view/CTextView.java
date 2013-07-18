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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.widget.TableRow;
import android.widget.TextView;

import com.normalexception.forum.rx8club.preferences.PreferenceHelper;

/**
 * A custom textview that handles a lot of the common settings for what
 * a textview entails in the ui
 */
public class CTextView extends TextView {
	
	public static final char LPAD = '«';
	public static final char RPAD = '»';
	
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
	
	/**
	 * Set the post detail information
	 * @param text			The string to set
	 * @param scaledimg		The image that will be set
	 * @param clr			The color of the text
	 */
	public void setUserPostInformation(String text, Bitmap scaledimg, int clr) {
		int spanStart = text.lastIndexOf(LPAD);
		if(spanStart > -1) {
			int spanEnd = text.lastIndexOf(RPAD) + 1;
			String preDetail = text.substring(0, spanStart);
			String postDetail = text.substring(spanStart, spanEnd);
			SpannableStringBuilder htext = new SpannableStringBuilder(
					Html.fromHtml("&nbsp;" + preDetail + "&nbsp;&nbsp;" +
					"<font color='yellow'>" + 
					postDetail + 
					"</font>"));
			
			htext.setSpan(new ImageSpan(scaledimg), 
	    			0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
			
			setText( htext,
						TextView.BufferType.SPANNABLE);
		} else {
			SpannableStringBuilder htext = new SpannableStringBuilder(" " + text);
			htext.setSpan(new ImageSpan(scaledimg), 
	    			0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
			setText(clr == Color.BLUE? text : htext);
		}
	}
	
	/**
	 * Set the spanned width of the view
	 */
	public void setSpannedWidth() {
		// Convert dip to px
    	Resources r = getResources();
    	int px = 
    			(int)TypedValue.applyDimension(
    					TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
    	setWidth(px);
	}

	/**
	 * Report the text parameters
	 * @return	Text parameters for view
	 */
	public TableRow.LayoutParams getTextParameters(int index) {
		TableRow.LayoutParams params = new TableRow.LayoutParams();
	    params.span = 1;  
	    if(index == 0) params.weight = 1f;
	    return params;
	}
}

