package com.normalexception.forum.rx8club.activities;

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

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.state.AppState;

/**
 * Activity used to create a search in the forum.
 * 
 * Required Intent Parameters:
 * none
 */
public class SearchActivity extends ForumBaseActivity implements OnClickListener {

	private String TAG = this.getClass().getName();
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setState(AppState.State.SEARCH, this.getIntent());
        
        setContentView(R.layout.activity_search);
        
        findViewById(R.id.searchSubmitButton).setOnClickListener(this);
    }
	
	/**
	 * Construct a search url from the selected gui options
	 * @return	A search string based on gui selections
	 */
	private String getSearchUrl() {
		String searchText =
				((TextView)findViewById(R.id.searchText)).getText().toString().replace(" ", "%20");
		String searchUrl = 
				searchText;

		int selectedType =
				(int)((Spinner)findViewById(R.id.searchTypeSpinner)).getSelectedItemPosition();
		
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
				(int)((Spinner)findViewById(R.id.searchDateSpinner)).getSelectedItemPosition();
		String selectedDateVals[] = 
				getResources().getStringArray(R.array.searchDateArrayValue);
		
		int selectedSort =
				(int)((Spinner)findViewById(R.id.searchSortSpinner)).getSelectedItemPosition();
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
		super.onClick(arg0);
		switch(arg0.getId()) {
			case R.id.searchSubmitButton:	
				String searchText = getSearchUrl();
				Intent _intent = new Intent(SearchActivity.this, CategoryActivity.class);
				_intent.putExtra("link", WebUrls.searchUrl + searchText);
				_intent.putExtra("isNewTopics", true);
				Log.v(TAG, "Adding Link To Search: " + _intent.getStringExtra("link"));
				startActivity(_intent);
			break;
		}
	}
}
