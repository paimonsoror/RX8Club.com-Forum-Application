package com.normalexception.forum.rx8club.handler;

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

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.MainActivity;
import com.normalexception.forum.rx8club.activities.NewPostsActivity;
import com.normalexception.forum.rx8club.activities.ProfileActivity;
import com.normalexception.forum.rx8club.activities.SearchActivity;

public class GuiHandlers implements OnClickListener {
	
	private static final String TAG = "Application:GuiHandler";
	private static Activity _src = null;
	
	public GuiHandlers(Activity src) {
		Log.v(TAG, "Create");
		_src = src;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		Intent _intent = null;
		switch(arg0.getId()) {
			case R.id.newTopicsButton:
				Log.v(TAG, "New Topics Pressed");
				Log.v(TAG, "Finishing Old Activity");
				if(!(_src instanceof MainActivity))
					_src.finish();
				_intent = new Intent(arg0.getContext(), NewPostsActivity.class);
				break;
			
			case R.id.newPmButton:
				_src.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainApplication.getAppContext(), 
								"Sorry Not Implemented Yet!", Toast.LENGTH_SHORT).show();
					}
				});
				break;
			
			case R.id.profileButton:
				_intent = new Intent(arg0.getContext(), ProfileActivity.class);
				break;
				
			case R.id.searchButton:
				_intent = new Intent(arg0.getContext(), SearchActivity.class);
				break;
				
			case R.id.liveButton:
				_src.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(MainApplication.getAppContext(), 
								"Sorry Not Implemented Yet!", Toast.LENGTH_SHORT).show();
					}
				});
				break;
				
			case R.id.imageView1:
				_intent = new Intent(arg0.getContext(), MainActivity.class);
				if(!(_src instanceof MainActivity))
					_src.finish();
				break;
		}
		
		if(_intent != null)
			_src.startActivity(_intent);
	}
}
