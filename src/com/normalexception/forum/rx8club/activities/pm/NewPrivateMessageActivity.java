package com.normalexception.forum.rx8club.activities.pm;

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

import android.app.ProgressDialog;
import android.os.Bundle;
import com.normalexception.forum.rx8club.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.task.PmTask;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

public class NewPrivateMessageActivity extends ForumBaseActivity {

	private final String TAG = "NewPrivateMessageActivity";
	
	private String postUser = null;
	private String postText = null;
	private String securityToken = null;
	private String recipients = null;
	private String pmid = null;
	private String title = null;
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("postUser", postUser);
		outState.putString("securitytoken", securityToken);
		outState.putString("postText", postText);
		outState.putString("recipients", recipients);
		outState.putString("pmid", pmid);
		outState.putString("title", title);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		try {
			if(savedInstanceState != null) {
				postText = savedInstanceState.getString("postText");
				securityToken = savedInstanceState.getString("securitytoken");
				postUser = savedInstanceState.getString("postUser");
				recipients = savedInstanceState.getString("recipients");
				pmid = savedInstanceState.getString("pmid");
				title = savedInstanceState.getString("title");
			}				
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        
        setContentView(R.layout.activity_new_private_message);
        
        findViewById(R.id.newPmButton).setOnClickListener(this);
    
	    if(savedInstanceState == null)
	    	constructView();
	}
	
	/**
	 * Construct the view elements
	 */
	private void constructView() {
		loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		final ForumBaseActivity src = this;
		
	    updaterThread = new Thread("PrivateMessageThread") {
			public void run() {
				String link = 
		        		(String) getIntent().getStringExtra("link");
				Document doc = 
						VBForumFactory.getInstance().get(src, 
								VBForumFactory.getRootAddress() + "/" + link);
				securityToken =
						HtmlFormUtils.getInputElementValue(doc, "securitytoken");
				
				pmid =
						HtmlFormUtils.getInputElementValue(doc, "pmid");
				
				postUser = 
						getIntent().getStringExtra("user");
				
				if(validateInputs(postUser))
					runOnUiThread(new Runnable() {
			    		public void run() {
			    			((TextView)findViewById(R.id.pmRecipientsText)).setText(postUser);
			    		}
					});
		    	
				loadingDialog.dismiss();	
			}
	    };
	    updaterThread.start();
	}
	
	 /*
   	 * (non-Javadoc)
   	 * @see android.view.View.OnClickListener#onClick(android.view.View)
   	 */
   	@Override
   	public void onClick(View arg0) {
   		super.onClick(arg0);
   		
   		postUser = ((TextView)findViewById(R.id.pmRecipientsText)).getText().toString();
   		postText = ((TextView)findViewById(R.id.pmMessageText)).getText().toString();
   		title    = ((TextView)findViewById(R.id.pmSubjectText)).getText().toString();
   		
   		if(validateInputs(postUser, postText, title)) {
	   		switch(arg0.getId()) {	
	   		case R.id.newPmButton:
	   			Log.v(TAG, "PM Submit Clicked");
				PmTask sTask = 
						new PmTask(this, this.securityToken, this.title, postText, this.postUser, this.pmid);
				sTask.execute();
	   			break;
	   		}
   		} else {
   			Toast.makeText(this, "Not Valid", Toast.LENGTH_SHORT).show();
   		}
   	}
   	
   	/**
   	 * Make sure that the inputs have content and are not null
   	 * @param params	A set of parameters to check
   	 * @return			True if the params have contents
   	 */
   	private boolean validateInputs(String... params) {
   		boolean valid = true;
   		for(String param : params) {
   			valid &= ((param != null) && (!param.equals("")));
   		}
   		return valid;
   	}

   	/*
   	 * (non-Javadoc)
   	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
   	 */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {	
	}
}
