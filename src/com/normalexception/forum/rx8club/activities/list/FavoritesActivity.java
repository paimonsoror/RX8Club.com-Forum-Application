package com.normalexception.forum.rx8club.activities.list;

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
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.favorites.FavoriteThreads;
import com.normalexception.forum.rx8club.view.thread.ThreadView;
import com.normalexception.forum.rx8club.view.thread.ThreadViewArrayAdapter;

public class FavoritesActivity extends ForumBaseActivity implements OnClickListener {
	
	private static final String TAG = "Application:Favorites";
	
	private FavoriteThreads threadlist;
	private ThreadViewArrayAdapter tva;
	
	private ListView lv;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        try {
        	super.onCreate(savedInstanceState);      
	        setContentView(R.layout.activity_basiclist);        
	        
	        lv = (ListView)findViewById(R.id.mainlistview);
	        updateList();	        
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Category Activity! " + e.getMessage());
		}	
    }
	
	/**
	 * Update the view's list with the appropriate data
	 */
	private void updateList() {
		final Activity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {
            	findViewById(R.id.mainlisttitle).setVisibility(View.VISIBLE);
				((TextView)findViewById(R.id.mainlisttitle)).setText("Favorite Threads");
				
            	threadlist = FavoriteFactory.getInstance().getFavorites();
		    	tva = new ThreadViewArrayAdapter(a, R.layout.view_thread, threadlist);
				lv.setAdapter(tva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	ThreadView itm = (ThreadView) parent.getItemAtPosition(position);
		            	Log.v(TAG, "User clicked '" + itm.getTitle() + "'");
						Intent _intent = 
								new Intent(FavoritesActivity.this, ThreadActivity.class);
						_intent.putExtra("link", itm.getLink());
						_intent.putExtra("title", itm.getTitle());
						startActivity(_intent);
		            }
		        });
            }
    	});
	}
}
