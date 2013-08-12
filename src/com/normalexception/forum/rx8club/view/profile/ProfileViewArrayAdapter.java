package com.normalexception.forum.rx8club.view.profile;

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

import java.util.List;

import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ProfileViewArrayAdapter extends ArrayAdapter<ProfileView> {
	private Context activity;
	private List<ProfileView> data;

	/**
	 * A custom adapter that handles PM View objects
	 * @param context				The source context
	 * @param textViewResourceId	The resource ID
	 * @param objects				The objects in the list
	 */
	public ProfileViewArrayAdapter(Context context, int textViewResourceId,
			List<ProfileView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return data == null? 0 : data.size();
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getItem(int)
	 */
	@Override  
	public ProfileView getItem(int position) {     
		return data.get(position);  
	} 

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {		
		View vi = convertView;
		if(vi == null) {
			vi = new TextView(activity);
		}

		TextView tv = (TextView)vi;
		final ProfileView pm = data.get(position);
		String text = String.format("%s\n%s", pm.getName(), pm.getText());
		SpannableString spanString = new SpannableString(text);
		spanString.setSpan(new StyleSpan(Typeface.BOLD), 
				0, text.indexOf("\n"), 0);
		spanString.setSpan(new StyleSpan(Typeface.ITALIC), 
				text.indexOf("\n") + 1, text.length(), 0);
		tv.setText(spanString);
		tv.setPadding(1, 10, 1, 10);
		tv.setBackgroundColor(Color.DKGRAY);
		tv.setTextColor(Color.WHITE);
		tv.setTextSize(10);
		tv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent _intent = 
						new Intent(activity, ThreadActivity.class);
				_intent.putExtra("link", pm.getLink());
				_intent.putExtra("title", pm.getName());
				activity.startActivity(_intent);
			}
		});

		return vi;
	}
}
