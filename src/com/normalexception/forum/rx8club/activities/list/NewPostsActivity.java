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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.thread.ThreadView;
import com.normalexception.forum.rx8club.view.thread.ThreadViewArrayAdapter;

/**
 * Activity that is used to display all "new" posts since the users
 * last visit.  This is essentially a modified CategoryActivity
 * 
 * Required Intent Parameters:
 * link - The link to the "new posts" results
 */
public class NewPostsActivity extends ForumBaseActivity implements OnClickListener {
	
	private static final String TAG = "Application:NewPostsActivity";
	
	private ArrayList<ThreadView> threadlist;
	private ThreadViewArrayAdapter tva;
	
	private String pageNumber = "";
	private static String link;
	
	public int scaledImage = 12;
	
	private ListView lv;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	        super.onCreate(savedInstanceState);
	        super.setTitle("RX8Club.com Forums");
	        setContentView(R.layout.activity_basiclist);
	        
	        Log.v(TAG, "New Posts Activity Started");
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);
	        threadlist = new ArrayList<ThreadView>();
	        lv = (ListView)findViewById(R.id.mainlistview);
	        
	        View v = getLayoutInflater().inflate(R.layout.view_category_footer, null);
	    	v.setOnClickListener(this);
	    	lv.addFooterView(v);
	    	
	        if(savedInstanceState == null)
	        	constructView();
	        else
	        	updateList();
	        
    	} catch (Exception e) {
    		Log.e(TAG, "Fatal Error In New Post Activity! " + e.getMessage());
    	}
    }
	
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
								new Intent(NewPostsActivity.this, ThreadActivity.class);
						_intent.putExtra("link", itm.getLink());
						_intent.putExtra("title", itm.getTitle());
						startActivity(_intent);
		            }
		        });
				updatePagination(thisPage, finalPage);
            }
    	});
	}
    
    /**
     * 
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
    	final ForumBaseActivity src = this;
    	
        updaterThread = new Thread("NewPostsThread") {
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
 		        
				getContents(doc);
		    	
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
	    menu.add(Menu.NONE, v.getId(), Menu.NONE, "Add As Favorite");   
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Fragment#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    if(item.getItemId() > -1) {
	        	TextView tv = (TextView) findViewById(item.getItemId());
	        	Toast.makeText(this, tv.getText(), Toast.LENGTH_SHORT).show();
	            return true;
	    } else {
	            return super.onContextItemSelected(item);
	    }
	}
    
    /**
     * Grab contents from the forum that the user clicked on
     * @param doc	The document parsed from the link
     * @return		An arraylist of forum contents
     */
    public ArrayList<String> getContents(Document doc) {
    	ArrayList<String> titles = new ArrayList<String>();
    	
    	// Update pagination
    	try {
	    	Elements pageNumbers = doc.select("div[class=pagenav]");
			Elements pageLinks = 
					pageNumbers.first().select("td[class^=vbmenu_control]");
			thisPage = pageLinks.text().split(" ")[1];
			finalPage = pageLinks.text().split(" ")[3];
    	} catch (Exception e) { }
   	
    	// Grab each thread
    	Elements threadListing = doc.select("table[id=threadslist] > tbody > tr");
 
    	for(Element thread : threadListing) {
    		Elements threadTitleContainer  = thread.select("a[id^=thread_title]");
    		
    		if(threadTitleContainer != null && !threadTitleContainer.isEmpty()) {
	    		Element threadLink  = threadTitleContainer.get(0);
	    		Element repliesText = thread.select("td[title^=Replies]").get(0);
	    		Element threaduser  = thread.select("td[id^=td_threadtitle_] div.smallfont").get(0);
	    		Element threadicon  = thread.select("img[id^=thread_statusicon_]").get(0);
	
	    		boolean isSticky = false, isLocked = false;
	    		try {
	    			isSticky = thread.select("td[id^=td_threadtitle_] > div").text().contains("Sticky:");
	    		} catch (Exception e) { }
	    		try {
	    			isLocked = threadicon.attr("src").contains("lock.gif");
	    		} catch (Exception e) { }
	    		
	    		String totalPostsInThreadTitle = threadicon.attr("alt");
	    		String totalPosts = "";	
	    		
	    		if(totalPostsInThreadTitle != null && totalPostsInThreadTitle.length() > 0)
	    				totalPosts = totalPostsInThreadTitle.split(" ")[2];
	    		
	    		String txt = repliesText.getElementsByClass("alt2").attr("title");
	    		String splitter[] = txt.split(" ", 4);
	    		String postCount = splitter[1].substring(0, splitter[1].length() - 1);
	    		String views = splitter[3];

	    		String formattedTitle = 
	    				String.format("%s%s", isSticky? "Sticky: " : "", threadLink.text()); 
	    		
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
    	
    	return titles;
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
		
		switch(arg0.getId()) {
			case R.id.previousButton:
				_intent = new Intent(NewPostsActivity.this, NewPostsActivity.class);
				_intent.putExtra("link", Utils.decrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				this.finish();
				break;
			case R.id.nextButton:
				_intent = new Intent(NewPostsActivity.this, NewPostsActivity.class);
				_intent.putExtra("link", Utils.incrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
				this.finish();
				break;
		}
		
		if(_intent != null)
			startActivity(_intent);
	}
}
