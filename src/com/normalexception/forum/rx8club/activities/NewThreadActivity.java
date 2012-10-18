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

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.task.NewThreadTask;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

public class NewThreadActivity extends ForumBaseActivity implements OnClickListener {
	
	private String forumId = "", link = "", s, token, f, posthash, poststart, subject, post, source;
	
	private static final String TAG = "NewThreadActivity";
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {	
		// First save the subject and post
		subject = ((TextView)findViewById(R.id.postSubject)).getText().toString();
		post = ((TextView)findViewById(R.id.postPost)).getText().toString();
		
		outState.putSerializable("forumid", forumId);
		outState.putSerializable("link", link);
		outState.putSerializable("s", s);
		outState.putSerializable("token", token);
		outState.putSerializable("f", f);
		outState.putSerializable("posthash", posthash);
		outState.putSerializable("poststart", poststart);
		outState.putSerializable("post", post);
		outState.putSerializable("subject", subject);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			forumId 	= savedInstanceState.getString("forumid");
			link 		= savedInstanceState.getString("link");
			s 			= savedInstanceState.getString("s");
			token 		= savedInstanceState.getString("token");
			f 			= savedInstanceState.getString("f");
			posthash 	= savedInstanceState.getString("posthash");
			poststart 	= savedInstanceState.getString("poststart");
			subject 	= savedInstanceState.getString("subject");
			post 		= savedInstanceState.getString("post");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        super.setTitle("RX8Club.com Forums");
	        setContentView(R.layout.activity_new_thread);
	        
	        // Register the titlebar gui buttons
	        this.registerGuiButtons();
	        
	        findViewById(R.id.newThreadButton).setOnClickListener(this);
	        
	        forumId = getIntent().getStringExtra("forumid");
	        link = getIntent().getStringExtra("link");
	        source = getIntent().getStringExtra("source");
	        
	        if(savedInstanceState == null)
	        	constructView();
	        
    	} catch (Exception e) {
    		Log.e(TAG, "Error In New Thread Activity");
    		BugSenseHandler.sendException(e);
    	}
    }
    
    /**
     * Construct the view
     */
    private void constructView() {    	
    	Document doc = VBForumFactory.getInstance().get(link);
    	Elements panel = doc.getElementsByClass("panelsurround");
    	s 			= getInputElementValue(panel, "s");
    	token 		= getInputElementValue(panel, "securitytoken");
    	f 			= getInputElementValue(panel, "f");
    	posthash 	= getInputElementValue(panel, "posthash");
    	poststart	= getInputElementValue(panel, "poststarttime");
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		// First save the subject and post
		subject = ((TextView)findViewById(R.id.postSubject)).getText().toString();
		post = ((TextView)findViewById(R.id.postPost)).getText().toString();
				
		super.onClick(arg0);
		switch(arg0.getId()) {
			case R.id.newThreadButton:
				this.setResult(RESULT_OK);
				NewThreadTask ntt = new NewThreadTask(this, forumId, s, 
						 token, f, posthash, subject, post, source);
				ntt.execute();
			break;
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
}
