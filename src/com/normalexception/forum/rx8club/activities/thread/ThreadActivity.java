package com.normalexception.forum.rx8club.activities.thread;

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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.task.SubmitTask;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.UserProfile;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.ViewContents;
import com.normalexception.forum.rx8club.view.thread.ThreadView;
import com.normalexception.forum.rx8club.view.thread.ThreadViewArrayAdapter;
import com.normalexception.forum.rx8club.view.threadpost.PostView;
import com.normalexception.forum.rx8club.view.threadpost.PostViewArrayAdapter;

/**
 * Activity used to display thread contents.  Within this activity a user can
 * create new posts.
 * 
 * Required Intent Parameters:
 * link - The link to the thread
 * title - The title of the thread
 * page - The page number of the thread
 */
public class ThreadActivity extends ForumBaseActivity implements OnClickListener {

	private static final String TAG = "Application:Thread";
	public static final int ThreadIdIndex = 9000;
	
	private String currentPageLink;
	private String currentPageTitle;
	
	private String threadNumber;
	
	private String pageNumber = "1";
	
	private String securityToken = "none";
	private String postNumber = "none";
	
	private int scaledImage = 12;
	
	private ArrayList<PostView> postlist;
	private PostViewArrayAdapter pva;
	private ListView lv;
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("threadnumber",  threadNumber);
		outState.putString("securitytoken", securityToken);
		outState.putString("postnumber",    postNumber);
		outState.putString("final",         finalPage);
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
				threadNumber =  savedInstanceState.getString("threadnumber");
				securityToken = savedInstanceState.getString("securitytoken");
				postNumber =    savedInstanceState.getString("postnumber");
				finalPage =     savedInstanceState.getString("final");
			}				
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage());
		}
	}
	
	/**
	 * Container for thread posts and thread post related information
	 */
	private class ThreadPost {
		private String name, title, location, join, postcount, post, 
					   postDate, postid;
		public String toString() {
			return postid + "|" + name + "|" + title + "|" + location + 
					"|" + join + "|" + postcount + "|" + post + "|" + postDate;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try{
	    	 super.onCreate(savedInstanceState);
	         super.setTitle("RX8Club.com Forums");
	         setContentView(R.layout.activity_basiclist);
	         
	         Log.v(TAG, "Category Activity Started");
	         
	         scaledImage = ThreadUtils.setScaledImageSizes(this);
	         
	         postlist = new ArrayList<PostView>();
	        if(savedInstanceState == null)
	        	constructView();
	        else
	        	updateList();

	 	} catch (Exception e) {
	 		Log.e(TAG, "Fatal Error In Thread Activity! " + e.getMessage());
	 	}
    }
    
    /**
     * Construct the thread activity view
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
    	final ThreadActivity src = this;
    	    	
        updaterThread = new Thread("CategoryThread") {
			public void run() {
				currentPageLink = 
		        		(String) getIntent().getStringExtra("link");
				currentPageTitle = 
						(String) getIntent().getStringExtra("title");			
				pageNumber = 
						(String) getIntent().getStringExtra("page");
				if(pageNumber == null) pageNumber = "1";
				
				Log.v(TAG, "Grabbing link: " + currentPageLink);
				
				Document doc = VBForumFactory.getInstance().get(src, currentPageLink);
				viewContents = new ArrayList<ViewContents>();
				
				lv = (ListView)findViewById(R.id.mainlistview);
		    	View v = getLayoutInflater().inflate(R.layout.view_newreply_footer, null);
		    	v.setOnClickListener(src);
		    	lv.addFooterView(v);
				
				getThreadContents(doc);
				
		    	updateList();
		    	loadingDialog.dismiss();			
			}
        };
        updaterThread.start();
    }
    
	private void updateList() {
		final ForumBaseActivity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {    	    	
		    	pva = new PostViewArrayAdapter(a, R.layout.view_thread, postlist);
				lv.setAdapter(pva);
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
     * Get the user that posted the message
     * @param text	The thread text
     * @return		The user
     */
    private String getPostUser(String text) {
    	return text.split("\n")[0];
    }
    
    /**
     * Check if the post is by the logged in user
     * @param text	The post text
     * @return		True if the post is by the logged in user
     */
    private boolean isPostByUser(String text) {
		// if the post is by the user, the user name should
    	// be the first string
    	String assumedUser = getPostUser(text);
    	if(UserProfile.getUsername().equals(assumedUser))
    		return true;
		return false;
	}
    
    /**
     * Grab contents from the forum that the user clicked on
     * @param doc	The document parsed from the link
     * @param id	The id number of the link
     * @return		An arraylist of forum contents
     */
    public ArrayList<ThreadPost> getThreadContents(Document doc) {
    	ArrayList<ThreadPost> titles = new ArrayList<ThreadPost>();
    	
    	// Update pagination
    	updatePagination(doc);
    	
    	// Get the user's actual ID, there is a chance they never got it
    	// before
    	UserProfile.setUserId(
    			HtmlFormUtils.getInputElementValue(doc, "loggedinuser"));
    	
    	// Get Post Number and security token
    	securityToken = HtmlFormUtils.getInputElementValue(doc, "securitytoken");
    	Elements pNumber = 
    			doc.select("a[href^=http://www.rx8club.com/newreply.php?do=newreply&noquote=1&p=]");
    	String pNumberHref = pNumber.attr("href");
    	postNumber = pNumberHref.substring(pNumberHref.lastIndexOf("=") + 1);
    	threadNumber = doc.select("input[name=searchthreadid]").attr("value");
    	
        Elements posts = doc.select("div[id=posts]").select("div[id^=edit]");
        for(Element post : posts) {
        	Elements innerPost = post.select("table[id^=post]");
        	
        	// User Control Panel
        	Elements userCp = innerPost.select("td[class=alt2]");
        	Elements userDetail = userCp.select("div[class=smallfont]");
        	Elements userSubDetail = null;
        	
        	try{ userSubDetail = userDetail.get(2).select("div"); }
        	catch(Exception e) { userSubDetail = userDetail.get(1).select("div"); }
    	
        	// User Information
        	ThreadPost user = new ThreadPost();
        	user.name = userCp.select("div[id^=postmenu]").text();
        	user.title = userDetail.get(0).text();
        	user.postDate = innerPost.select("td[class=thead]").get(0).text();
        	user.postid = Utils.parseInts(post.attr("id"));
        	
        	for(int i = 1; i < userSubDetail.size(); i++) {
        		switch(i) {
        		case 1:
        			break;
        		case 2:
        			user.join = userSubDetail.get(i).text();
        			break;
        		case 3:
        			user.location  = userSubDetail.get(i).text();
        			break;
        		case 4:
        			user.postcount  = userSubDetail.get(i).text();
        			break;
        		}
        	}
        	
        	// User Post Content
        	user.post = innerPost.select("td[class=alt1]").select("div[id^=post_message]").html();
        	
        	PostView pv = new PostView();
        	pv.setUserName(user.name);
        	pv.setUserTitle(user.title);
        	pv.setPostDate(user.postDate);
        	pv.setJoinDate(user.join);
        	pv.setUserPostCount(user.postcount);
        	pv.setUserPost(user.post);
        	postlist.add(pv);
    	}
    	
    	return titles;
    }
    
    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
    @Override
    protected void enforceVariants(int myPage, int lastPage) {
    	final boolean first, prev, next, last;
    	
    	prev = myPage == 1? 		false : true;
    	first = myPage == 1? 		false : true;
    	next = lastPage > myPage? 	true : false;
    	last = myPage == lastPage? 	false : true;
    	
    	runOnUiThread(new Runnable() {
    		public void run() {
    			findViewById(R.id.previousButton).setEnabled(prev);
				findViewById(R.id.firstButton).setEnabled(first);
				findViewById(R.id.nextButton).setEnabled(next);
				findViewById(R.id.lastButton).setEnabled(last);
    		}
    	});
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View arg0) {	
		super.onClick(arg0);
		Intent _intent = null;
		_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
		_intent.putExtra("title", this.currentPageTitle);
		
		switch(arg0.getId()) {
			case R.id.previousButton:
				_intent.putExtra("link", Utils.decrementPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				this.finish();
				break;
			case R.id.nextButton:
				_intent.putExtra("link", Utils.incrementPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
				this.finish();
				break;
			case R.id.submitButton:
				_intent = null;
				String advert = PreferenceHelper.isAdvertiseEnabled(MainApplication.getAppContext())?
						"Posted From RX8Club.com Android App" : "";
				String toPost = 
						String.format("%s\n\n%s", 
								((TextView)findViewById(R.id.postBox)).getText().toString(), advert);
				SubmitTask sTask = new SubmitTask(this, this.securityToken, 
						this.threadNumber, this.postNumber,
						toPost, this.currentPageTitle, this.pageNumber);
				sTask.debug();
				sTask.execute();
				break;
			case R.id.paginationText:
				final EditText input = new EditText(this);
				input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
				new AlertDialog.Builder(ThreadActivity.this)
			    .setTitle("Go To Page...")
			    .setMessage("Enter New Page Number")
			    .setView(input)
			    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            String value = input.getText().toString(); 
			            Intent _intent = new Intent(ThreadActivity.this, ThreadActivity.class);
						_intent.putExtra("link", Utils.getPage(currentPageLink, value));
						_intent.putExtra("page", value);
						_intent.putExtra("title", currentPageTitle);
						startActivity(_intent);
						finish();
			        }
			    }).setNegativeButton("Cancel", null).show();	
				_intent = null; // Just to make sure we dont start another activity 
				break;
				
			case R.id.firstButton:
				_intent.putExtra("link", Utils.getPage(this.currentPageLink, Integer.toString(1)));
				_intent.putExtra("page", "1");
				finish();
				break;
				
			case R.id.lastButton:
				_intent.putExtra("link", Utils.getPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", this.finalPage);
				finish();
				break;	
				
			default:
				_intent = null;
				break;
		}	
		
		if(_intent != null)
			startActivity(_intent);
	}
}
