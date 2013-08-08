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
import java.util.Iterator;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.task.SubmitTask;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.UserProfile;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.ViewHolder;
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
	
	private String currentPageLink;
	private String currentPageTitle;
	
	private String threadNumber;
	
	private String pageNumber = "1";
	
	private String securityToken = "none";
	private String postNumber = "none";
	
	private ArrayList<PostView> postlist;
	private PostViewArrayAdapter pva;
	private ListView lv;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try{
	    	 super.onCreate(savedInstanceState);
	         
	         setContentView(R.layout.activity_basiclist);
	         
	         Log.v(TAG, "Category Activity Started");
	         
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
				
				Document doc = 
						VBForumFactory.getInstance().get(src, currentPageLink);
				
				lv = (ListView)findViewById(R.id.mainlistview);
				
				runOnUiThread(new Runnable() {
		            public void run() {
		            	View v = getLayoutInflater().
		            			inflate(R.layout.view_newreply_footer, null);
		            	v.setOnClickListener(src);
		            	
						// If the user is guest, then hide the items that
		            	// they generally wont be able to use
						if(LoginFactory.getInstance().isGuestMode()) {
							ViewHolder.get(v, R.id.nr_replycontainer)
								.setVisibility(View.GONE);
						}
		            	lv.addFooterView(v);
		            }
	            });
				
				getThreadContents(doc);
				
		    	updateList();
		    	loadingDialog.dismiss();			
			}
        };
        updaterThread.start();
    }
    
	private void updateList() {
		final ThreadActivity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {    	
		    	findViewById(R.id.mainlisttitle).setVisibility(View.VISIBLE);
    			((TextView)findViewById(R.id.mainlisttitle)).setText(currentPageTitle);
		    	pva = new PostViewArrayAdapter(a, R.layout.view_thread, postlist);
				lv.setAdapter(pva);
		    	updatePagination(thisPage, finalPage);
            }
    	});
	}
    
    /**
     * Grab contents from the forum that the user clicked on
     * @param doc	The document parsed from the link
     * @param id	The id number of the link
     * @return		An arraylist of forum contents
     */
    public void getThreadContents(Document doc) {    	
    	// Update pagination
    	try {
	    	Elements pageNumbers = doc.select("div[class=pagenav]");
			Elements pageLinks = 
					pageNumbers.first().select("td[class^=vbmenu_control]");
			thisPage = pageLinks.text().split(" ")[1];
			finalPage = pageLinks.text().split(" ")[3];
    	} catch (Exception e) { }
    	
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
        	Elements userSubDetail = userDetail.last().select("div"); 
        	Elements userAvatar = userDetail.select("img[alt$=Avatar]");
    	
        	// User Information
        	PostView pv = new PostView();
        	pv.setUserName(userCp.select("div[id^=postmenu]").text());
        	pv.setIsLoggedInUser(
        			LoginFactory.getInstance().isLoggedIn()?
        			UserProfile.getUsername().equals(pv.getUserName()) : false);	
        	pv.setUserTitle(userDetail.first().text());
        	pv.setUserImageUrl(userAvatar.attr("src"));
        	pv.setPostDate(innerPost.select("td[class=thead]").first().text());
        	pv.setPostId(Utils.parseInts(post.attr("id")));
        	
        	// userSubDetail
        	// 0 - full container , full container
        	// 1 - Trader Score   , Trader Score
        	// 2 - Join Date      , Join Date
        	// 3 - Post Count     , Location
        	// 4 - Blank          , Post Count
        	// 5 -                , Blank || Social
        	//
        	Iterator<Element> itr = userSubDetail.listIterator();
        	while(itr.hasNext()) {
        		String txt = itr.next().text();
        		if(txt.contains("Location:"))
        			pv.setUserLocation(txt);
        		else if (txt.contains("Posts:"))
        			pv.setUserPostCount(txt);
        		else if (txt.contains("Join Date:"))
        			pv.setJoinDate(txt);
        	}
        	
        	// User Post Content
        	pv.setUserPost(Utils.reformatQuotes(
        			innerPost.select("td[class=alt1]")
        			.select("div[id^=post_message]").html()));

        	pv.setSecurityToken(securityToken);
        	postlist.add(pv);
    	}
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
		Log.d(TAG, "BUTTON CLICKED");
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
