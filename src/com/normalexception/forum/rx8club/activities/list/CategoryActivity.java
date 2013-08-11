package com.normalexception.forum.rx8club.activities.list;

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
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.view.thread.ThreadView;
import com.normalexception.forum.rx8club.view.thread.ThreadViewArrayAdapter;

/**
 * Activity used to display forum category contents.  This will essentially
 * open a category and display all of the threads that are contained
 * in that category
 * 
 * Required Intent Parameters:
 * link - The link to the category view, example http://www.rx8club.com/lounge-4/
 * page - The current page number.  This is used for the pagination info
 */
public class CategoryActivity extends ForumBaseActivity implements OnClickListener {
	
	private static final String TAG = "Application:Category";
	private static String link;

	private String pageNumber = "1";
	private String forumId = "";
	
	private boolean isNewTopicActivity = false;
	
	private ArrayList<ThreadView> threadlist;
	private ThreadViewArrayAdapter tva;
	
	private ListView lv;
	
	private final int NEW_THREAD = 5000;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        try {
        	super.onCreate(savedInstanceState);
	        
	        setContentView(R.layout.activity_basiclist);        
	        
	        Log.v(TAG, "Category Activity Started");
	        
	        threadlist = new ArrayList<ThreadView>();
	        lv = (ListView)findViewById(R.id.mainlistview);
	        
	        // If the user clicked "New Posts" then we need to
	        // handle things a little bit differently
	        isNewTopicActivity =
					getIntent().getBooleanExtra("isNewTopics", false);
	        
	        // We do not need to have a "New Thread" button if the
	        // user clicked New Posts.
	        if(!isNewTopicActivity && LoginFactory.getInstance().isLoggedIn()) {
		        Button bv = new Button(this);
		        bv.setId(NEW_THREAD);
		        bv.setOnClickListener(this);
		        bv.setText("New Thread");
		        bv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		        lv.addHeaderView(bv);
	        }
	        
	        // Footer has pagination information
	        View v = getLayoutInflater().inflate(R.layout.view_category_footer, null);
	    	v.setOnClickListener(this);
	    	lv.addFooterView(v);
	        
	        if(savedInstanceState == null || 
	        		(tva == null || tva.getCount() == 0))
	        	constructView();
	        else
	        	updateList();
	        
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Category Activity! " + e.getMessage());
		}	
    }
	
	/**
	 * Update the view's list with the appropriate data
	 */
	private void updateList() {
		final Activity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {
		    	tva = new ThreadViewArrayAdapter(a, R.layout.view_thread, threadlist);
				lv.setAdapter(tva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	ThreadView itm = (ThreadView) parent.getItemAtPosition(position);
		            	Log.v(TAG, "User clicked '" + itm.getTitle() + "'");
						Intent _intent = 
								new Intent(CategoryActivity.this, ThreadActivity.class);
						_intent.putExtra("link", itm.getLink());
						_intent.putExtra("title", itm.getTitle());
						startActivity(_intent);
		            }
		        });
				if(LoginFactory.getInstance().isLoggedIn())
					registerForContextMenu(lv);
				updatePagination(thisPage, finalPage);
            }
    	});
	}
	
	/**
	 * Construct the view for the activity
	 */
	private void constructView() {
		loadingDialog = 
				ProgressDialog.show(
						this, getString(R.string.loading), getString(R.string.pleaseWait), true);
		final ForumBaseActivity src = this;
		
		updaterThread = new Thread("CategoryThread") {
			public void run() {
				link = 
		        		(String) getIntent().getSerializableExtra("link");
				pageNumber = 
						(String) getIntent().getStringExtra("page");
								
				if(pageNumber == null) pageNumber = "1";
				
		        Document doc = VBForumFactory.getInstance().get(src, 
		        		link == null? WebUrls.newPostUrl : link);
		        
		        // if doc came back, and link was null, we need to update
 				// the link reference to reflect the new post URL
 				if(link == null) {
 					// <link rel="canonical" href="http://www.rx8club.com/search.php?searchid=10961740" />
 					Elements ele = doc.select("link[rel^=canonical]");
 					if(ele != null) {
 						link = ele.attr("href");
 					}
 				}
		        
 				// The forum id data is only required if we are within a category
 				// and not if we are in a New Posts page.  This data is used when
 				// we create new threads.
		        if(!isNewTopicActivity) {
			        forumId = link.substring(link.lastIndexOf("-") + 1);
			        
			        // Make sure forumid doesn't end with a "/"
			        forumId = Utils.parseInts(forumId);
			        
			        getCategoryContents(doc, 
							link.substring(link.lastIndexOf('-') + 1, link.lastIndexOf('/')),
							link.contains("sale-wanted"));
		        } else {
		        	getCategoryContents(doc, null, false);
		        }
		        				
				findViewById(R.id.mainlisttitle).setVisibility(View.GONE);
		    			    	
		    	updateList();
				
		    	loadingDialog.dismiss();
			}
		};
		updaterThread.start();
	}
    
    /*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterContextMenuInfo info = 
				(AdapterContextMenuInfo) menuInfo;
        int position = info.position;
	    menu.add(Menu.NONE, position, Menu.NONE, "Add As Favorite");   
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    if(item.getItemId() > -1) {
	    	int menuItemIndex = item.getItemId();
	    	ThreadView tv = (ThreadView) lv.getAdapter().getItem(menuItemIndex);
	    	FavoriteFactory.getInstance().addFavorite(tv);
	        return true;
	    } else {
	        return super.onContextItemSelected(item);
	    }
	}
    
    /**
     * Grab contents from the forum that the user clicked on
     * @param doc		The document parsed from the link
     * @param id		The id number of the link
     * @param isMarket 	True if the link is from a marketplace category
     */
    public void getCategoryContents(Document doc, String id, boolean isMarket) {
    	
    	// Update pagination
    	try {
	    	Elements pageNumbers = doc.select("div[class=pagenav]");
			Elements pageLinks = 
					pageNumbers.first().select("td[class^=vbmenu_control]");
			thisPage = pageLinks.text().split(" ")[1];
			finalPage = pageLinks.text().split(" ")[3];
    	} catch (Exception e) { }
    	    	
    	// Make sure id contains only numbers
    	if(!isNewTopicActivity)
    		id = Utils.parseInts(id);
    	
    	// Grab each thread
    	Elements threadListing =  
    			doc.select("table[id=threadslist] > tbody > tr");
 
    	for(Element thread : threadListing) {
    		try {
    			Elements threadTitleContainer  = thread.select("a[id^=thread_title]");
        		
        		if(threadTitleContainer != null && !threadTitleContainer.isEmpty()) {
		    		Element threadLink  = thread.select("a[id^=thread_title]").first();
		    		Element repliesText = thread.select("td[title^=Replies]").first();
		    		Element threaduser  = thread.select("td[id^=td_threadtitle_] div.smallfont").first();
		    		Element threadicon  = thread.select("img[id^=thread_statusicon_]").first();
		    		Element threadDiv   = thread.select("td[id^=td_threadtitle_] > div").first();
		    		
		    		boolean isSticky = false, isLocked = false;    		
		    		try {
		    			isSticky = threadDiv.text().contains("Sticky:");
		    		} catch (Exception e) { }
		    		
		    		try {
		    			isLocked = threadicon.attr("src").contains("lock.gif");
		    		} catch (Exception e) { }
		    		
		    		String preString = "";
		    		try {
		    			preString = threadDiv.select("span > b").text();
		    		} catch (Exception e) { }
		    		
		    		String totalPostsInThreadTitle = threadicon.attr("alt");
		    		String totalPosts = "";	
		    		
		    		if(totalPostsInThreadTitle != null && totalPostsInThreadTitle.length() > 0)
		    			totalPosts = totalPostsInThreadTitle.split(" ")[2];
		    		
		    		// Remove page from the link
		    		String realLink = Utils.removePageFromLink(link);  			
		    		
		    		if(threadLink.attr("href").contains(realLink) || (isNewTopicActivity || isMarket) ) {
			    		
			    		String txt = repliesText.getElementsByClass("alt2").attr("title");
			    		String splitter[] = txt.split(" ", 4);
			    		String postCount = splitter[1].substring(0, splitter[1].length() - 1);
			    		String views = splitter[3];
		
			    		String formattedTitle = 
			    				String.format("%s%s%s", 
			    						isSticky? "Sticky: " : "",
			    								preString.length() == 0? "" : preString + " ",
			    								threadLink.text()); 
			    		
			    		ThreadView tv = new ThreadView();
			    		tv.setTitle(formattedTitle);
			    		tv.setStartUser(threaduser.text());
			    		tv.setLastUser(repliesText.select("a[href*=members]").text());
			    		tv.setLink(threadLink.attr("href"));
			    		tv.setPostCount(postCount);
			    		tv.setMyPosts(totalPosts);
			    		tv.setViewCount(views);
			    		tv.setLocked(isLocked);
			    		tv.setSticky(isSticky);
			    		threadlist.add(tv);
		    		}
        		}
    		} catch (Exception e) { Log.w(TAG, "Error Parsing That Thread..."); }
    	}
    }
    
    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
    @Override
    protected void enforceVariants(int myPage, int lastPage) {
    	if(myPage == 1)
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.previousButton).setEnabled(false);
    			}
    		});
    	else 
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.previousButton).setEnabled(true);
    			}
    		});
    	
    	if(lastPage > myPage) {
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.nextButton).setEnabled(true);
    			}
    		});
    	} else {
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.nextButton).setEnabled(false);
    			}
    		});
    	}
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		Intent _intent = null;
		boolean result = false;
		
		switch(arg0.getId()) {
			case R.id.previousButton:
				_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
				_intent.putExtra("link", Utils.decrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				_intent.putExtra("isNewTopics", this.isNewTopicActivity);
				this.finish();
				break;
			case R.id.nextButton:
				_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
				_intent.putExtra("link", Utils.incrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
				_intent.putExtra("isNewTopics", this.isNewTopicActivity);
				this.finish();
				break;
			case NEW_THREAD:
				_intent = new Intent(CategoryActivity.this, NewThreadActivity.class);
				_intent.putExtra("link", WebUrls.newThreadAddress + forumId);
				_intent.putExtra("source", link);
				_intent.putExtra("forumid", forumId);
				result = true;
				break;
		}
		
		if(_intent != null)
			if(result)
				startActivityForResult(_intent, 1);
			else
				startActivity(_intent);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 1) {
		     if(resultCode == RESULT_OK) {
		    	 finish();
		     }
		}
	}
}
