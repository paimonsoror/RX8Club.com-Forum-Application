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

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.task.DeletePmTask;
import com.normalexception.forum.rx8club.task.PmTask;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.pmpost.PMPostView;
import com.normalexception.forum.rx8club.view.pmpost.PMPostViewArrayAdapter;

public class PrivateMessageViewActivity extends ForumBaseActivity {

	private final String TAG = "PrivateMessageViewActivity";
	
	private String postUser = null;
	private String postText = null;
	private String securityToken = null;
	private String pmid = null;
	private String title = null;
	
	private ArrayList<PMPostView> pmlist;
	private PMPostViewArrayAdapter pmva;
	
	private ListView lv;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_basiclist);
        
        Log.v(TAG, "PM View Activity Started");
        
        pmlist = new ArrayList<PMPostView>();
        lv = (ListView)findViewById(R.id.mainlistview);
        
        View v = getLayoutInflater().inflate(R.layout.view_pmitem_footer, null);
    	v.setOnClickListener(this);
    	lv.addFooterView(v);
        
        if(savedInstanceState == null)
        	constructView();
        else {
        	updateList();
        }
    }
    
    private void updateList() {
		final Activity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {
		    	pmva = new PMPostViewArrayAdapter(a, R.layout.view_newreply, pmlist);
				lv.setAdapter(pmva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	
		            }
		        });
            }
    	});
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
				
				title =
						HtmlFormUtils.getInputElementValue(doc, "title");
				
				Elements postMenu = doc.select("div[id=postmenu_]");
				
				postUser = postMenu.text();
				
				Elements postMessage = doc.select("div[id=post_message_]");
				
				postText = postMessage.html();
		    	
				PMPostView pmi = new PMPostView();
				pmi.setUserName(postUser);
				pmi.setUserPost(postText);
				pmi.setSecurityToken(securityToken);
				pmlist.add(pmi);
				
				loadingDialog.dismiss();	
				
				updateList();
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
   		
   		switch(arg0.getId()) {	
   		case R.id.pmitem_submit:
   			Log.v(TAG, "PM Submit Clicked");
   			String toPost = 
					((TextView)findViewById(R.id.postBox)).getText().toString();
			PmTask sTask = 
					new PmTask(this, this.securityToken, "Re: " + this.title, 
							toPost, this.postUser, this.pmid);
			sTask.execute();
   			break;
   		case R.id.pmitem_delete:
   			final Activity ctx = this;
   			// Lets make sure the user didn't accidentally click this
			DialogInterface.OnClickListener dialogClickListener = 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
				    	case DialogInterface.BUTTON_POSITIVE:
				    		DeletePmTask dpm = new DeletePmTask(ctx, securityToken, pmid);
							dpm.execute();
			   				break;
			        }
			    }
			};
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder
				.setMessage("Are you sure you want to delete PM?")
				.setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener)
			    .show();
			break;
   		}
   	}

    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
}
