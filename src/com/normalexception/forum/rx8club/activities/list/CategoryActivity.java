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

import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.TimeoutFactory;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.filter.ThreadFilter;
import com.normalexception.forum.rx8club.filter.ThreadFilterFactory;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.view.PTRListView;
import com.normalexception.forum.rx8club.view.PTRListView.OnRefreshListener;
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
	
	private Logger TAG =  Logger.getLogger(this.getClass());
	private static String link;
	private ProgressDialog loadingDialog;
	
	private final int MENU_FAVE = 0;
	private final int MENU_FILTER_USER = 1;
	private final int MENU_FILTER_TITLE = 2;
	private final int MENU_FILTER_LASTUSER = 3;

	private String pageNumber = "1";
	private String forumId = "";
	
	private boolean isNewTopicActivity = false;
	
	private ArrayList<ThreadView> threadlist;
	private ThreadViewArrayAdapter tva;
	
	private PTRListView lv;
	
	private final int NEW_THREAD = 5000;
	
	public static final int NO_PERMISSION = -2;

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        try {
        	super.onCreate(savedInstanceState);
        	super.setState(AppState.State.CATEGORY, this.getIntent());
        	
	        setContentView(R.layout.activity_basiclist);        
	        
	        Log.v(TAG, "Category Activity Started");
	        
	        if(TimeoutFactory.getInstance().checkTimeout(this)) {
		        threadlist = new ArrayList<ThreadView>();
		        lv = (PTRListView)findViewById(R.id.mainlistview);
		        
		        // If the user clicked "New Posts" then we need to
		        // handle things a little bit differently
		        isNewTopicActivity =
						getIntent().getBooleanExtra("isNewTopics", false);
		        
		        if(isNewTopicActivity)
		        	super.setState(AppState.State.NEW_POSTS, this.getIntent());
		        else
		        	super.setState(AppState.State.CATEGORY, this.getIntent());
		        
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
	        }
	        
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Category Activity! " + e.getMessage(), e);
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
		    	tva.setIsNewThread(isNewTopicActivity);
				lv.setAdapter(tva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	ThreadView itm = (ThreadView) parent.getItemAtPosition(position);
		            	Log.d(TAG, "User clicked '" + itm.getTitle() + "'");
						Intent _intent = 
								new Intent(CategoryActivity.this, ThreadActivity.class);
						
												
						// If the user wants the last page when recently updated
						// threads, grab it.
						if(getLastPage(itm) && 
								!itm.getLastLink().equals("#") &&
								!itm.getLastLink().endsWith("/#")) {
							_intent.putExtra("link", itm.getLastLink());
							_intent.putExtra("page", "last");
						} else
							_intent.putExtra("link", itm.getLink());
						
						_intent.putExtra("poll", itm.isPoll());
						_intent.putExtra("locked", itm.isLocked());
						_intent.putExtra("title", itm.getTitle());
						startActivity(_intent);
		            }
		        });
				lv.setOnRefreshListener(new OnRefreshListener() {
		            @Override
		            public void onRefresh() {		            
		                lv.onRefreshComplete();
		                a.finish();
		                a.startActivity(a.getIntent());
		            }
		        });
				if(LoginFactory.getInstance().isLoggedIn())
					registerForContextMenu(lv);
				updatePagination(thisPage, finalPage);
		        updateFilterizedInformation();
            }
    	});
	}
	
	/**
	 * Report if the user selected to have their recently updated threads
	 * display the last page
	 * @param itm	The item that was selected
	 * @return		True if the user wants the last page
	 */
	private boolean getLastPage(ThreadView itm) {
		boolean val = false;
		if(PreferenceHelper.getRecentlyUpdatedThreadPage(this).equals("Last") 
				&& (itm.getLastLink() != null && !itm.getLastLink().equals("")))
				val = true;
		return val;
	}
	
	/**
	 * Construct the view for the activity
	 */
	private void constructView() {
		final ForumBaseActivity src = this;
		
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
				link = 
		        		(String) getIntent().getSerializableExtra("link");
				pageNumber = 
						(String) getIntent().getStringExtra("page");
								
				if(pageNumber == null) pageNumber = "1";
				
		        Document doc = VBForumFactory.getInstance().get(src, 
		        		link == null? WebUrls.newPostUrl : link);
		        
		        
		        if(doc != null) {     
			        // if doc came back, and link was null, we need to update
	 				// the link reference to reflect the new post URL
	 				if(link == null) {
	 					// <link rel="canonical" 
	 					// href="http://www.rx8club.com/search.php?searchid=10961740" />
	 					Elements ele = doc.select("link[rel^=canonical]");
	 					if(ele != null) {
	 						link = ele.attr("href");
	 					}
	 				}
			        
	 				// The forum id data is only required if we are within a category
	 				// and not if we are in a New Posts page.  This data is used when
	 				// we create new threads.
	 				publishProgress(getString(R.string.asyncDialogGrabThreads));
	 				try {
				        if(!isNewTopicActivity) {
					        forumId = link.substring(link.lastIndexOf("-") + 1);
					        
					        // Make sure forumid doesn't end with a "/"
					        forumId = Utils.parseInts(forumId);
					        
					        getCategoryContents(doc, 
									link.substring(link.lastIndexOf('-') + 1, 
											link.lastIndexOf('/')),
									link.contains("sale-wanted"));
				        } else {
				        	getCategoryContents(doc, null, false);
				        }
				        
				        publishProgress(getString(R.string.asyncDialogApplyFilters));
				        threadlist = CategoryFilterizer.applyFilter(threadlist);
	 				} catch (Exception e) {
	 					Toast.makeText(
	 							src, R.string.timeout, Toast.LENGTH_SHORT)
	 						 .show();
	 				}
			        				
					findViewById(R.id.mainlisttitle).setVisibility(View.GONE);
			    	
					publishProgress(getString(R.string.asyncDialogPopulating));
			    	updateList();
		        }
		    	return null;
			}
        	@Override
		    protected void onProgressUpdate(String...progress) {
        		if(loadingDialog != null)
        			loadingDialog.setMessage(progress[0]);
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
	 * @see android.app.Fragment#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	    menu.add(Menu.NONE, MENU_FAVE, Menu.NONE, "Add As Favorite");
	    menu.add(Menu.NONE, MENU_FILTER_USER, Menu.NONE, "Add As User Filter");
	    menu.add(Menu.NONE, MENU_FILTER_TITLE, Menu.NONE, "Add As Title Filter");
	    menu.add(Menu.NONE, MENU_FILTER_LASTUSER, Menu.NONE, "Add As Last User Filter");
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		ThreadView tv = (ThreadView) lv.getAdapter().getItem(info.position);
		
		switch(item.getItemId()) {
		case MENU_FAVE:
	    	FavoriteFactory.getInstance().addFavorite(tv);
	        return true;
		case MENU_FILTER_USER:
			ThreadFilterFactory.getInstance().addFilter(
					new ThreadFilter(ThreadFilter.RuleType.OWNER, tv.getStartUser()));
			return true;
		case MENU_FILTER_TITLE:
			ThreadFilterFactory.getInstance().addFilter(
					new ThreadFilter(ThreadFilter.RuleType.TITLE, tv.getTitle()));
			return true;
		case MENU_FILTER_LASTUSER:
			ThreadFilterFactory.getInstance().addFilter(
					new ThreadFilter(ThreadFilter.RuleType.LASTUSER, tv.getLastUser()));
		default:
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
    			boolean isSticky = false, isLocked = false, 
    					hasAttachment = false, isAnnounce = false,
    					isPoll = false;
    			String formattedTitle = "", postCount = "0", 
    					views = "0", forum = "", threadUser = "", 
    					lastUser = "", threadLink = "", lastPage = "", 
    					totalPosts = "0", threadDate = "";
	    		
    			Elements announcementContainer = thread.select("td[colspan=5]");
    			Elements threadTitleContainer  = thread.select("a[id^=thread_title]");
        		
    			// We could have two different types of threads.  Announcement threads are 
    			// completely different than the other types of threads (sticky, locked, etc)
    			// so we need to play some games here
    			if(announcementContainer != null && !announcementContainer.isEmpty()) {
    				Log.d(TAG, "Announcement Thread Found");
    				
    				Elements annThread = announcementContainer.select("div > a");
    				Elements annUser   = announcementContainer.select("div > span[class=smallfont]");
    				formattedTitle = "Announcement: " + annThread.first().text();
    				threadUser = annUser.last().text();
    				threadLink = annThread.attr("href");
    				isAnnounce = true;
    			} else if(threadTitleContainer != null && !threadTitleContainer.isEmpty()) {
		    		Element threadLinkEl= thread.select("a[id^=thread_title]").first();
		    		Element repliesText = thread.select("td[title^=Replies]").first();
		    		Element threaduser  = thread.select("td[id^=td_threadtitle_] div.smallfont").first();
		    		Element threadicon  = thread.select("img[id^=thread_statusicon_]").first();
		    		Element threadDiv   = thread.select("td[id^=td_threadtitle_] > div").first();
		    		Element threadDateFull  = thread.select("td[title^=Replies:] > div").first();
		    		
		    		try {
		    			isSticky = threadDiv.text().contains("Sticky:");
		    		} catch (Exception e) { }
		    		
		    		try {
		    			isPoll = threadDiv.text().contains("Poll:");
		    		} catch (Exception e) { }
		    		
		    		try {
		    			isLocked = threadicon.attr("src").contains("lock.gif");
		    		} catch (Exception e) { }
		    		
		    		String preString = "";
		    		try {
		    			preString = threadDiv.select("span > b").text();
		    		} catch (Exception e) { }
		    		
		    		try {
		    			hasAttachment = !threadDiv.select("a[onclick^=attachments]").isEmpty();
		    		} catch (Exception e) { }
		    		
		    		// Find the last page if it exists
		    		try {
		    			lastPage = threadDiv.select("span").last().select("a").last().attr("href");
		    		} catch (Exception e) { }
		    		
		    		threadDate = threadDateFull.text();
		    		int findAMPM = threadDate.indexOf("M") + 1;
		    		threadDate = threadDate.substring(0, findAMPM);
		    		
		    		String totalPostsInThreadTitle = threadicon.attr("alt");
		    		
		    		if(totalPostsInThreadTitle != null && totalPostsInThreadTitle.length() > 0)
		    			totalPosts = totalPostsInThreadTitle.split(" ")[2];
		    		
		    		// Remove page from the link
		    		String realLink = Utils.removePageFromLink(link);  			
		    		
		    		if(threadLinkEl.attr("href").contains(realLink) || (isNewTopicActivity || isMarket) ) {
			    		
			    		String txt = repliesText.getElementsByClass("alt2").attr("title");
			    		String splitter[] = txt.split(" ", 4);
			    		
			    		postCount = splitter[1].substring(0, splitter[1].length() - 1);
			    		views = splitter[3];
			    		
			    		try {
			    			if(this.isNewTopicActivity)
			    			 	forum = thread.select("td[class=alt1]").last().text();
			    		} catch (Exception e) { }
		
			    		formattedTitle = 
			    				String.format("%s%s%s", 
			    						isSticky? "Sticky: " : 
			    							isPoll? "Poll: " : "",
			    								preString.length() == 0? "" : preString + " ",
			    									threadLinkEl.text());
		    		}
		    		
		    		threadUser = threaduser.text();
		    		lastUser = repliesText.select("a[href*=members]").text();
		    		threadLink = threadLinkEl.attr("href");
        		}
    			
    			// Add our thread to our list as long as the thread
    			// contains a title
    			if(!formattedTitle.equals("")) {
		    		ThreadView tv = new ThreadView();
		    		tv.setTitle(formattedTitle);
		    		tv.setStartUser(threadUser);
		    		tv.setLastUser(lastUser);
		    		tv.setLink(threadLink);
		    		tv.setLastLink(lastPage);
		    		tv.setPostCount(postCount);
		    		tv.setMyPosts(totalPosts);
		    		tv.setViewCount(views);
		    		tv.setLocked(isLocked);
		    		tv.setSticky(isSticky);
		    		tv.setAnnouncement(isAnnounce);
		    		tv.setPoll(isPoll);
		    		tv.setHasAttachment(hasAttachment);
		    		tv.setForum(forum);
		    		tv.setLastPostTime(threadDate);
		    		threadlist.add(tv);
    			}
    		} catch (Exception e) { 
    			Log.e(TAG, "Error Parsing That Thread...", e);
    			Log.d(TAG, "Thread may have moved");
    		}
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
		        createNewThreadHandler();
				break;
		}
		
		if(_intent != null)
			startActivity(_intent);
	}
	
	/**
	 * Handler used when the user clicks the new thread button.  This
	 * checks to make sure the user has permissions before the page is
	 * created
	 */
	private void createNewThreadHandler() {
		final ForumBaseActivity src = this;
		updaterTask = new AsyncTask<Void,String,Void>() {
        	boolean success = false;
        	
        	@Override
		    protected void onPreExecute() {
        		
		    	loadingDialog = 
						ProgressDialog.show(src, 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
		    	((Button)findViewById(NEW_THREAD)).setEnabled(false);
		    }
        	
        	@Override
			protected Void doInBackground(Void... params) {	
        		success = doesUserHavePermissionToPage(WebUrls.newThreadAddress, forumId);
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
				
				
				if(success) {
					Intent _intent = 
							new Intent(CategoryActivity.this, NewThreadActivity.class);
					_intent.putExtra("link", WebUrls.newThreadAddress + forumId);
					_intent.putExtra("source", link);
					_intent.putExtra("forumid", forumId);
					startActivity(_intent);
				} else {
					 Toast.makeText(src, R.string.noPermission, Toast.LENGTH_LONG).show();
				}
				
				((Button)findViewById(NEW_THREAD)).setEnabled(true);
			}
        };
        updaterTask.execute();
	}
	
	/**
	 * Add information to the title that shows some information
	 * about the currently set filters
	 */
	private void updateFilterizedInformation() {
		int totalF = CategoryFilterizer.getTotalFiltered();
		
		findViewById(R.id.mainlisttitle).setVisibility(totalF > 0? 
				View.VISIBLE : View.GONE);	
		((TextView)findViewById(R.id.mainlisttitle))
			.setText(String.format("Filtered Threads: %d (%d Active Filters)", 
				totalF, ThreadFilterFactory.getInstance().getFilterCount()	));
	}
}
