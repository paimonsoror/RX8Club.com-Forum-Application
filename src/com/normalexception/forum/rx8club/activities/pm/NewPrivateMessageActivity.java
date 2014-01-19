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

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.TimeoutFactory;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.task.PmTask;
import com.normalexception.forum.rx8club.view.pmitem.PMItemView;
import com.normalexception.forum.rx8club.view.pmitem.PMItemViewArrayAdapter;

public class NewPrivateMessageActivity extends ForumBaseActivity {

	private Logger TAG = Logger.getLogger(this.getClass());
	
	private String postUser = null;
	private String postText = null;
	private String securityToken = null;
	private String recipients = null;
	private String pmid = null;
	private String title = null;
	
	private ListView lv;
	
	private ArrayList<PMItemView> tlist;
	private PMItemViewArrayAdapter pva;
	
	private ProgressDialog loadingDialog;

	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("postUser", postUser);
		outState.putString("postText", postText);
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
				postUser = savedInstanceState.getString("postUser");
				title = savedInstanceState.getString("title");
			}				
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setState(AppState.State.NEW_PM, this.getIntent());
        
        setContentView(R.layout.activity_basiclist);
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
	        lv      = (ListView)findViewById(R.id.mainlistview);
	        lv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
	        lv.setScrollContainer(false);
	        tlist   = new ArrayList<PMItemView>();
	    
		    if(savedInstanceState == null)
		    	constructView();
        }
	}
	
	/**
	 * Construct the view elements
	 */
	private void constructView() {
		final NewPrivateMessageActivity src = this;
		
		updaterTask = new AsyncTask<Void,String,Void>() {
        	@Override
		    protected void onPreExecute() {
        		
		    	loadingDialog = 
						ProgressDialog.show(src, 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
		    }
        	@Override
			protected Void doInBackground(Void... params) {
				String link = 
		        		(String) getIntent().getStringExtra("link");
				Document doc = 
						VBForumFactory.getInstance().get(src, 
								VBForumFactory.getRootAddress() + "/" + link);
				
				if(doc != null) {
					securityToken =
							HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
					
					pmid =
							HtmlFormUtils.getInputElementValueByName(doc, "pmid");
					
					postUser = 
							getIntent().getStringExtra("user");    	
					
					PMItemView pm = new PMItemView();
					if(validateInputs(postUser))
						pm.setName(postUser);
					tlist.add(pm);
			    	
			    	runOnUiThread(new Runnable() {
			            public void run() {
					    	pva = new PMItemViewArrayAdapter(src, R.layout.view_newpm, tlist);
							lv.setAdapter(pva);	
			            }
			    	});
				}
		    	
		    	return null;
			}
        	@Override
		    protected void onPostExecute(Void result) {
				try {
					loadingDialog.dismiss();
					loadingDialog = null;
				} catch (Exception e) {
					Log.w(TAG, e.getMessage());
				}
			}
        };
        updaterTask.execute();
	}
	
	 /*
   	 * (non-Javadoc)
   	 * @see android.view.View.OnClickListener#onClick(android.view.View)
   	 */
   	@Override
   	public void onClick(View arg0) {
   		super.onClick(arg0);
   		
   		recipients = ((TextView)findViewById(R.id.pmRecipientsText)).getText().toString();
   		postText = ((TextView)findViewById(R.id.pmMessageText)).getText().toString();
   		title    = ((TextView)findViewById(R.id.pmSubjectText)).getText().toString();
   		
   		if(validateInputs(recipients, postText, title)) {
	   		switch(arg0.getId()) {	
	   		case R.id.newPmButton:
	   			Log.v(TAG, "PM Submit Clicked");
				PmTask sTask = 
						new PmTask(this, this.securityToken, this.title, postText, this.recipients, this.pmid);
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
}
