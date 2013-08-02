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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.MainActivity;
import com.normalexception.forum.rx8club.activities.ProfileActivity;
import com.normalexception.forum.rx8club.activities.SearchActivity;
import com.normalexception.forum.rx8club.activities.list.NewPostsActivity;
import com.normalexception.forum.rx8club.activities.pm.PrivateMessageActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.favorites.FavoriteThreads;
import com.normalexception.forum.rx8club.view.thread.ThreadView;

/**
 * Implementation of the banner found on all views
 */
public class BannerFragment extends Fragment implements OnClickListener {
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Inflate our fragment
        View view = inflater.inflate(R.layout.fragment_banner, container, false);        
        return view;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
        getView().findViewById(R.id.imageView1).setOnClickListener(this);
        getView().findViewById(R.id.newTopicsButton).setOnClickListener(this);
        getView().findViewById(R.id.favoritesButton).setOnClickListener(this);
        getView().findViewById(R.id.inboxButton).setOnClickListener(this);
        getView().findViewById(R.id.profileButton).setOnClickListener(this);
        getView().findViewById(R.id.searchButton).setOnClickListener(this);
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
				_intent = new Intent(arg0.getContext(), NewPostsActivity.class);
				break;
				
			case R.id.favoritesButton:
				_intent = null;
				createFavoritesMenu();
				break;
			
			case R.id.inboxButton:
				_intent = new Intent(arg0.getContext(), PrivateMessageActivity.class);
				break;
			
			case R.id.profileButton:
				_intent = new Intent(arg0.getContext(), ProfileActivity.class);
				break;
				
			case R.id.searchButton:
				_intent = new Intent(arg0.getContext(), SearchActivity.class);
				break;
				
			case R.id.imageView1:
				_intent = new Intent(arg0.getContext(), MainActivity.class);
				break;
		}
		
		if(_intent != null)
			startActivity(_intent);
	}
	
	/**
	 * Create the favorites menu with the list of the saved favorites
	 * from the internal memory
	 */
	private void createFavoritesMenu() {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		List<String>    ls = new ArrayList<String>();
		final FavoriteThreads ft = 
				FavoriteFactory.getInstance().getFavorites();
		for(ThreadView tv : ft)
			ls.add(tv.getTitle());
		CharSequence[] cs = ls.toArray(new CharSequence[ls.size()]);
		
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setItems(cs, 
					new DialogInterface.OnClickListener() {
				    	public void onClick(DialogInterface dialog, int which) {
				          	// The 'which' argument contains the index position
				           	// of the selected item
				    		ThreadView tv = ft.get(which);
				    		Intent _intent = 
									new Intent(getActivity(), ThreadActivity.class);
							_intent.putExtra("link", tv.getLink());
							_intent.putExtra("title", tv.getTitle());
							startActivity(_intent);
				        }
				  	})
		       .setTitle("Favorites");

		// 3. Get the AlertDialog from create()
		builder.create().show();
	}

}