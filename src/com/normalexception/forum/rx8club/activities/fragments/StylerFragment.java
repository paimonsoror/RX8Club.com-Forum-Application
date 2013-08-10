package com.normalexception.forum.rx8club.activities.fragments;

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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.view.ViewHolder;

/**
 * Styler found on the threads
 */
public class StylerFragment extends Fragment implements OnClickListener {
	
	private static final String TAG = "Application:StylerFragment";
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Inflate our fragment
        View view = inflater.inflate(R.layout.fragment_styler, container, false);        
        return view;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ViewHolder.get(getView(), R.id.boldButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.italicButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.underlineButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.linkCodeButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.imageCodeButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.quoteCodeButton).setOnClickListener(this);
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		TextView tv = 
				(TextView)ViewHolder.get(arg0.getRootView(), R.id.postBox);
		
		if(tv == null) {
			Log.e(TAG, "TextView is null...");
			return;
		}
		
		String val = "";
		
		switch(arg0.getId()) {
		case R.id.boldButton:
			val = "[b][/b]";
			break;
		case R.id.italicButton:
			val = "[i][/i]";
			break;
		case R.id.underlineButton:
			val = "[u][/u]";
			break;
		case R.id.linkCodeButton:
			val = "[url][/url]";
			break;
		case R.id.imageCodeButton:
			val = "[img][/img]";
			break;
		case R.id.quoteCodeButton:
			val = "[quote][/quote]";
			break;
		}
		
		tv.setText(tv.getText().toString() + val);
	}

}