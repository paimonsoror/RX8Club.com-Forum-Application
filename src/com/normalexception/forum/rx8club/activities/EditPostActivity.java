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

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.task.UpdateTask;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

/**
 * Activity used whenever the user wants to edit the post
 * 
 * Required Intent Parameters:
 * postId - the post id of the post that is to be edited
 * securitytoken - the security token of the session
 * link - the original thread link
 * title - original thread title
 * page - page number
 * threadnumber - original thread number
 */
public class EditPostActivity extends ForumBaseActivity {

	private static final String TAG = "EditPostActivity";
	private String postId, securityToken, postHash, poststart, 
		pageNumber, pageTitle;
	
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
        
        findViewById(R.id.editThreadSubmit).setOnClickListener(this);
        
        postId = 
        		(String) getIntent().getStringExtra("postid");
        securityToken = 
        		(String) getIntent().getStringExtra("securitytoken");
        pageNumber =
        		(String) getIntent().getStringExtra("pagenumber");
        pageTitle =
        		(String) getIntent().getStringExtra("pagetitle");
        constructView();
    }
    
    /**
     * Construct the view items
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
    	
    	try {
    		Document editPage = 
    			VBForumFactory.getInstance().getEditPostPage(securityToken, postId);
    		String msg = editPage.select("textarea[name=message]").text();
    		((TextView)findViewById(R.id.postMessage)).setText(msg);
    		
    		Elements pansurr = editPage.select("td[class=panelsurround]");
    		this.securityToken = getInputElementValue(pansurr, "securitytoken");
    		this.postId = getInputElementValue(pansurr, "p");
    		this.postHash = getInputElementValue(pansurr, "posthash");
    		this.poststart = getInputElementValue(pansurr, "poststarttime");
    	} catch (Exception e) {
    		
    	} finally {
    		loadingDialog.dismiss();
    	}
    }
    
    /**
     * Report the value inside of an input element
     * @param pan	The panel where all of the input elements reside
     * @param name	The name of the input to get the value for
     * @return		The string value of the input
     */
    private String getInputElementValue(Elements pan, String name) {
    	return pan.select("input[name=" + name + "]").attr("value");
    }


    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		switch(arg0.getId()) {
		case R.id.editThreadSubmit:
			String toPost = 
					((TextView)findViewById(R.id.postMessage)).getText().toString();
			UpdateTask utask = new UpdateTask(this, this.securityToken, this.postId,
					this.postHash, this.poststart, this.pageNumber, this.pageTitle, toPost);
			utask.execute();
			break;
		}
	}
}
