package com.normalexception.forum.rx8club.fragment.category;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.favorites.FavoriteThreads;
import com.normalexception.forum.rx8club.fragment.thread.ThreadFragment;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.view.thread.ThreadView;
import com.normalexception.forum.rx8club.view.thread.ThreadViewArrayAdapter;

public class FavoritesFragment extends Fragment {
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private FavoriteThreads threadlist;
	private ThreadViewArrayAdapter tva;
	
	private ListView lv;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);    	
        return rootView;
    }
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
        	super.onCreate(savedInstanceState);      
        	//super.setState(AppState.State.FAVORITES, this.getIntent());      
	        
	        lv = (ListView)view.findViewById(R.id.mainlistview);
	        updateList();	        
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Favorites Activity! " + e.getMessage(), e);
		}	
    }
	
	/**
	 * Update the view's list with the appropriate data
	 */
	private void updateList() {
    	getActivity().runOnUiThread(new Runnable() {
            public void run() {
            	getView().findViewById(R.id.mainlisttitle).setVisibility(View.VISIBLE);
				((TextView)getView().findViewById(R.id.mainlisttitle)).setText("Favorite Threads");
				
            	threadlist = FavoriteFactory.getInstance().getFavorites();
		    	tva = new ThreadViewArrayAdapter(getActivity(), R.layout.view_thread, threadlist);
		    	
				lv.setAdapter(tva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	ThreadView itm = (ThreadView) parent.getItemAtPosition(position);
		            	Log.v(TAG, "User clicked '" + itm.getTitle() + "'");
						//Intent _intent = 
						//		new Intent(FavoritesFragment.this, ThreadActivity.class);
						//_intent.putExtra("link", itm.getLink());
						//_intent.putExtra("title", itm.getTitle());
						//startActivity(_intent);
		            }
		        });
            }
    	});
	}
}
