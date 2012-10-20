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

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.normalexception.forum.rx8club.R;

/**
 * Activity used whenever the user wants to edit the post
 * 
 * Required Intent Parameters:
 * postId - the post id of the post that is to be edited
 */
public class EditPostActivity extends ForumBaseActivity {

	private static final String TAG = "EditPostActivity";
	private String postId;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        setContentView(R.layout.activity_edit_post);
        
        Log.v(TAG, "Edit Thread Activity Started");

        // Register the titlebar gui buttons
        this.registerGuiButtons();
        
        postId = 
        		(String) getIntent().getStringExtra("postid");
        
        Toast.makeText(this, "Editing Post " + postId, Toast.LENGTH_LONG).show();        
    }

    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
}
