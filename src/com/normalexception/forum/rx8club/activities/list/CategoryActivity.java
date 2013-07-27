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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
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

	private String pageNumber = "";
	private String forumId = "";
	
	private ArrayList<ThreadView> threadlist;
	private ThreadViewArrayAdapter tva;
	
	public int scaledImage = 12;
	
	private ListView lv;
	
	private final int NEW_THREAD = 5000;
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("forumid", forumId);
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		if(savedInstanceState != null) {
			forumId = 
					savedInstanceState.getString("forumid");
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
	        setContentView(R.layout.activity_basiclist);        
	        
	        Log.v(TAG, "Category Activity Started");
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);
	        threadlist = new ArrayList<ThreadView>();
	        lv = (ListView)findViewById(R.id.mainlistview);
	        
	        Button bv = new Button(this);
	        bv.setId(NEW_THREAD);
	        bv.setOnClickListener(this);
	        bv.setText("New Thread");
	        bv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
	        lv.addHeaderView(bv);
	        
	        View v = getLayoutInflater().inflate(R.layout.view_category_footer, null);
	    	v.setOnClickListener(this);
	    	lv.addFooterView(v);
	        
	        if(savedInstanceState == null)
	        	constructView();
	        else
	        	updateList();
	        
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Category Activity! " + e.getMessage());
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
								new Intent(CategoryActivity.this, ThreadActivity.class);
						_intent.putExtra("link", itm.getLink());
						_intent.putExtra("title", itm.getTitle());
						startActivity(_intent);
		            }
		        });
            }
    	});
	}
	
	/**
	 * Construct the view for the activity
	 */
	private void constructView() {
		loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		final ForumBaseActivity src = this;
		
		updaterThread = new Thread("CategoryThread") {
			public void run() {
				link = 
		        		(String) getIntent().getSerializableExtra("link");
				pageNumber = 
						(String) getIntent().getStringExtra("page");
				if(pageNumber == null) pageNumber = "1";
				
		        Document doc = VBForumFactory.getInstance().get(src, link);
		        forumId = link.substring(link.lastIndexOf("-") + 1);
		        
		        // Make sure forumid doesn't end with a "/"
		        forumId = Utils.parseInts(forumId);
		        
				getCategoryContents(doc, 
						link.substring(link.lastIndexOf('-') + 1, link.lastIndexOf('/')),
						link.contains("sale-wanted"));
		    			    	
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
     * @param doc		The document parsed from the link
     * @param id		The id number of the link
     * @param isMarket 	True if the link is from a marketplace category
     * @return			An arraylist of forum contents
     */
    public ArrayList<String> getCategoryContents(Document doc, String id, boolean isMarket) {
    	ArrayList<String> titles = new ArrayList<String>();
    	
    	// Update pagination
    	updatePagination(doc);
    	
    	// Make sure id contains only numbers
    	id = Utils.parseInts(id);
    	
    	// Grab each thread
    	Elements threadListing = doc.select("tbody[id^=threadbits_] > tr");
 
    	for(Element thread : threadListing) {
    		Element threadLink  = thread.select("a[id^=thread_title]").get(0);
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
    		
    		// Remove page from the link
    		String realLink = Utils.removePageFromLink(link);  			
    		
    		if(threadLink.attr("href").contains(realLink) || isMarket) {
	    		
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
		boolean result = false;
		
		switch(arg0.getId()) {
			case R.id.previousButton:
				_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
				_intent.putExtra("link", Utils.decrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				this.finish();
				break;
			case R.id.nextButton:
				_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
				_intent.putExtra("link", Utils.incrementPage(link, this.pageNumber));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
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
