package com.normalexception.app.rx8club.view.pm;

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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.view.ViewHolder;

public class PMView extends RelativeLayout {

	private TextView pmSubject;
	private TextView pmFrom;
	private TextView pmDate;
	
	private TextView pmFromLabel;
	private TextView pmDateLabel;
	
	private ImageView pmImage;

	public PMView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_pm_children, this, true);
		setupChildren();
	}

	public PMView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_pm_children, this, true);
		setupChildren();
	}

	public PMView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_pm_children, this, true);
		setupChildren();
	}

	/**
	 * Setup the children we contain in this view
	 */
	private void setupChildren() {
		pmSubject = (TextView) findViewById(R.id.pm_subject);
		pmFrom    = (TextView) findViewById(R.id.pm_from);
		pmDate    = (TextView) findViewById(R.id.pm_date);
		pmFromLabel = (TextView) findViewById(R.id.pm_fromlabel);
		pmDateLabel = (TextView) findViewById(R.id.pm_datelabel);
		pmImage     = (ImageView)findViewById(R.id.pm_image);
	}

	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static PMView inflate(ViewGroup parent) {
		PMView pmView = (PMView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_pm, parent, false);
		return pmView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param pm	The model we are going to use to populate the view
	 */
	public void setPM(final PMModel pm) {
		pmSubject.setText(pm.getTitle());

		if(pm.getUser() == null || pm.getDate() == null) {
			setMode(this, true);
		} else {
			setMode(this, false);
			pmFrom.setText(pm.getUser());
			pmDate.setText(
				String.format("%s, %s", pm.getDate(), pm.getTime())
			);
		}
	}

	/**
	 * Set the mode of the category line
	 * @param vi		The view line
	 * @param isTitle	If we are going to represent a title
	 */
	private void setMode(View vi, boolean isTitle) {
		int showMode = isTitle? View.GONE : View.VISIBLE;
		int colorMode= isTitle? Color.DKGRAY : Color.GRAY;
		int textColor= isTitle? Color.WHITE : Color.BLACK;

		pmFrom.setVisibility(showMode);
		pmFromLabel.setVisibility(showMode);
		pmDate.setVisibility(showMode);
		pmDateLabel.setVisibility(showMode);
		pmImage.setVisibility(showMode);
		vi.setBackgroundColor(colorMode);

		((TextView) ViewHolder.get(vi,R.id.pm_subject)).setTextColor(textColor);
	}
}
