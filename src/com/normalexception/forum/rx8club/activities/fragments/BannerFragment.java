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

/**
 * Implementation of the banner found on all views
 */
public class BannerFragment extends Fragment implements OnClickListener {

	ArrayList<String> favoritesList = new ArrayList<String>();
	CharSequence[] cs = null;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Inflate our fragment
        View view = inflater.inflate(R.layout.fragment_banner, container, false);        
        favoritesList.add("Coming Soon...");
        cs = favoritesList.toArray(new CharSequence[favoritesList.size()]);
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
	
	private void createFavoritesMenu() {
		// 1. Instantiate an AlertDialog.Builder with its constructor
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setItems(cs, new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int which) {
				            // The 'which' argument contains the index position
				            // of the selected item
				            Toast.makeText(getActivity(), "Coming Soon...", Toast.LENGTH_SHORT).show();
				            }
				        })
		       .setTitle("Favorites");

		// 3. Get the AlertDialog from create()
		builder.create().show();
	}

}