package com.normalexception.forum.rx8club.fragment;

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

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.fragment.category.CategoryFragment;
import com.normalexception.forum.rx8club.state.AppState;

/**
 * Activity used to create a search in the forum.
 * 
 * Required Intent Parameters:
 * none
 */
public class SearchFragment extends Fragment implements OnClickListener {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_search, container, false);
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.SEARCH, this);
        view.findViewById(R.id.searchSubmitButton).setOnClickListener(this);
    }
	
	/**
	 * Construct a search url from the selected gui options
	 * @return	A search string based on gui selections
	 */
	private String getSearchUrl() {
		String searchText =
				((TextView)getView().findViewById(R.id.searchText)).getText().toString().replace(" ", "%20");
		String searchUrl = 
				searchText;

		int selectedType =
				(int)((Spinner)getView().findViewById(R.id.searchTypeSpinner)).getSelectedItemPosition();
		
		// Append selected type to the search params
		switch(selectedType) {
		case 0:
			break;
		case 1:
			searchUrl += "&titleonly=1";
			break;
		case 2:
			searchUrl += "&starteronly=" + searchText;
			break;
		}
		
		int selectedDate = 
				(int)((Spinner)getView().findViewById(R.id.searchDateSpinner)).getSelectedItemPosition();
		String selectedDateVals[] = 
				getResources().getStringArray(R.array.searchDateArrayValue);
		
		int selectedSort =
				(int)((Spinner)getView().findViewById(R.id.searchSortSpinner)).getSelectedItemPosition();
		String selectedSortVals[] =
				getResources().getStringArray(R.array.searchSortArrayValue);
		
		searchUrl += "&searchdate=" + selectedDateVals[selectedDate] + 
				"&sortby=" + selectedSortVals[selectedSort];
		
		return searchUrl;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		switch(arg0.getId()) {
			case R.id.searchSubmitButton:	
				String searchText = getSearchUrl();
				Bundle args = new Bundle();
				args.putString("link", WebUrls.searchUrl + searchText);
				args.putBoolean("isNewTopics", true);
				Log.v(TAG, "Adding Link To Search: " + args.getString("link"));
				
				// Create new fragment and transaction
				Fragment newFragment = new CategoryFragment();
				newFragment.setArguments(args);
				FragmentTransaction transaction = getFragmentManager().beginTransaction();

				// Replace whatever is in the fragment_container view with this fragment,
				// and add the transaction to the back stack
				transaction.add(R.id.content_frame, newFragment);
				transaction.addToBackStack("search");

				// Commit the transaction
				transaction.commit();
			break;
		}
	}
}
