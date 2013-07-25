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

import com.normalexception.forum.rx8club.handler.ForumImageHandler;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Custom textview that is used to display the data found within a thread.  This
 * also implements the image getter that will insert images into the thread text
 */
public class CThreadTextView extends TextView {
	
	public CThreadTextView(Context ctx) {
		super(ctx);
		setMovementMethod(LinkMovementMethod.getInstance());
	}
	
	/**
	 * Set the text that is found at the top of each thread response
	 * @param text	The text to set
	 */
	public void setTitleText(String text) {
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.indexOf("\n"), 0);
		setText(spanString);
		setTextSize((float) PreferenceHelper.getFontSize(getContext()));
    	setTextColor(Color.WHITE);
	}
	
	/**
	 * Set the text that is found in the thread response
	 * @param text	The text to set
	 */
	public void setContentText(String text) {
		try {
			ForumImageHandler imageHandler = new ForumImageHandler(this, getContext());
			setText( 
				Html.fromHtml(text + "<br><br><br>", imageHandler, null));
		} catch (Exception e) {
			setText( 
				Html.fromHtml(text + "<br><br><br>"));
		}
		setTextSize((float) PreferenceHelper.getFontSize(getContext()));
    	setTextColor(Color.WHITE);
	}
	
	/**
	 * Set the title of the thread
	 * @param text	The title text
	 */
	public void setThreadTitle(String text) {
		setText(text);
		setTextSize((float) PreferenceHelper.getFontSize(getContext()));
    	setTextColor(Color.WHITE);
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
	public TableRow.LayoutParams getTextParameters() {
		TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT,
                1.0f);
    	params.weight = 0;
    	return params;
	}
}
