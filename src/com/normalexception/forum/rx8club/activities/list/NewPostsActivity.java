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
import java.util.LinkedHashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.CTextDetail;
import com.normalexception.forum.rx8club.view.CTextView;
import com.normalexception.forum.rx8club.view.ViewContents;

/**
 * Activity that is used to display all "new" posts since the users
 * last visit.  This is essentially a modified CategoryActivity
 * 
 * Required Intent Parameters:
 * link - The link to the "new posts" results
 */
public class NewPostsActivity extends ForumBaseActivity implements OnClickListener {
	
	private static final String TAG = "Application:NewPostsActivity";
	private static TableLayout tl;
	
	//private LinkedHashMap<String,String> styleMap, userMap, lastUserMap;
	private ThreadListContents tlContents = null;
	
	private String pageNumber = "";
	private static String link;
	
	public int scaledImage = 12;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("contents", tlContents);
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onRestoreInstanceState(android.os.Bundle)
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		try {
			tlContents =
					(ThreadListContents) savedInstanceState.getSerializable("contents");			
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage());
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
	        setContentView(R.layout.activity_new_posts);
	        
	        findViewById(R.id.previousButton).setOnClickListener(this);
	        findViewById(R.id.nextButton).setOnClickListener(this);
	        
	         runOnUiThread(new Runnable() {
		            public void run() {	
		            	// Hide pagination
		            	findViewById(R.id.paginationRow).setVisibility(View.GONE);
		            }
	         });
	        
	        Log.v(TAG, "New Posts Activity Started");
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);
	
	        if(savedInstanceState == null)
	        	constructView();
	        else {
	        	updateView(viewContents);
	        }
	        
    	} catch (Exception e) {
    		Log.e(TAG, "Fatal Error In New Post Activity! " + e.getMessage());
    	}
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
 				
 				viewContents = new ArrayList<ViewContents>();     
 				linkMap = new LinkedHashMap<String,String>();			
 				tlContents = new ThreadListContents();
 		        
				final ArrayList<String> list = getContents(doc);
		        
		        viewContents.add(
		        		new ViewContents(Color.BLUE, new String[]{"Forum", "Posts", "Views"}, 40, false));  
		    	
		    	for(String lst : list) {     
		    		final String[] rowText = lst.split("µ");
		    		int clr = Color.DKGRAY;
		    		if(PreferenceHelper.isHighlightStickies(src) && lst.startsWith("Sticky:"))
		    			clr = Color.GRAY;
		    		
		    		viewContents.add(
		    				new ViewContents(clr, new String[]{rowText[0], rowText[1], rowText[2]}, 90, false));
				}
		    	
		    	updateView(viewContents);
		    	
		    	runOnUiThread(new Runnable() {
		            public void run() {
		            	// Restore pagination
		            	findViewById(R.id.paginationRow).setVisibility(View.VISIBLE);
		            }
		    	});
		        
		    	loadingDialog.dismiss();
 			}
         };
         updaterThread.start();
    }
    
    /**
     * Update the view contents
     * @param contents	List of view rows
     */
    private void updateView(final ArrayList<ViewContents> contents) {
    	runOnUiThread(new Runnable() {
    		public void run() {
    			tl = (TableLayout)findViewById(R.id.myTableLayoutNewPost);
    			tl.setColumnStretchable(0, true);
    			for(ViewContents view : contents) {
    				addRow(view.getClr(), view.getTexts(), view.getId(), view.isSpan());
    			}
    		}
    	});
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
     * Add a row to the view
     * @param clr	The background color of the row
     * @param text	The text for the row
     * @param id	The id of the row
     */
    private void addRow(int clr, String texts[], int id, boolean span) {
    	String user = "", lastuser = "";
    	boolean isLocked = false;
    	
    	/* Create a new row to be added. */
    	TableRow tr_head = new TableRow(this);
    	tr_head.setId(id);
    	tr_head.setBackgroundColor(clr);
    			
        int index = 0;
    	for(String text : texts) {
    		// Thread ID
    		int threadId = -1;
    		
    		try { 
    			threadId = 
    				Integer.parseInt(Utils.parseInts(linkMap.get(text)));
    		} catch (StringIndexOutOfBoundsException si) {
    		} catch (NumberFormatException nf) {
    		} catch (NullPointerException np) { }
    		
	    	/* Create a Button to be the row-content. */
	    	CTextView b = new CTextView(this, this, threadId);
			registerForContextMenu(b);
	        
	        String style = tlContents.styleMap.get(text);
	        if(style != null && !style.equals(""))
	        	b.setTypeface(null, Typeface.BOLD);
	        
	        // We need to decode the resource, and then scale
	    	// down the image
	    	Bitmap scaledimg = 
	    			Bitmap.createScaledBitmap(
	    					BitmapFactory.decodeResource(
	    							getResources(), R.drawable.arrow_icon), 
	    							scaledImage, scaledImage, true);
	        
	    	// Do the same for the lock icon
	    	Bitmap scaledlock = 
	    			Bitmap.createScaledBitmap(
	    					BitmapFactory.decodeResource(
	    							getResources(), R.drawable.lock), 
	    							scaledImage, scaledImage, true);
	    	
	        if(index == 0) {
	        	user = tlContents.userMap.get(text);
	        	lastuser = tlContents.lastUserMap.get(text);
	        	
	        	try { 
	        		isLocked = tlContents.lockedMap.get(text); } 
	        	catch (NullPointerException npe) { }
	        	
	        	// Set the information for the text line as a spannable
	        	// for the first column
	        	b.setUserPostInformation(text, isLocked? scaledlock : scaledimg, clr);
        		b.setSpannedWidth();
	        } else {
	        	b.setText(text);
	        }
	
	        tr_head.addView(b, b.getTextParameters(index));
	        index++;
    	}

    	/* Add row to TableLayout. */
        tl.addView(tr_head, tl.getChildCount() - 1);
        
        if(PreferenceHelper.isShowPostDetailButton(this)) {
	        if(user != null) {
		        // Add username
		        tr_head = new TableRow(this);
		    	tr_head.setBackgroundColor(clr);
		    	
		    	CTextDetail b = new CTextDetail(this, user, lastuser);		    			    	
		    	tr_head.addView(b, b.getTextParameters());
		    	
		    	tl.addView(tr_head, tl.getChildCount() - 1);
	        }
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
    	updatePagination(doc);
    	
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
	    		
	    		if(PreferenceHelper.isShowPostCountButton(this) && 
	    				totalPostsInThreadTitle != null && totalPostsInThreadTitle.length() > 0)
	    			totalPosts = CTextView.LPAD +
	    				totalPostsInThreadTitle.split(" ")[2] + 
	    				" " + 
	    				totalPostsInThreadTitle.split(" ")[3] +
	    				CTextView.RPAD;
	    		
	    		String txt = repliesText.getElementsByClass("alt2").attr("title");
	    		String splitter[] = txt.split(" ", 4);
	    		String postCount = splitter[1].substring(0, splitter[1].length() - 1);
	    		String views = splitter[3];

	    		String formattedTitle = 
	    				String.format("%s%s", isSticky? "Sticky: " : "", threadLink.text()); 
	    		
	    		titles.add(String.format(
	    				"%s%sµ%sµ%s", formattedTitle, totalPosts, postCount, views));
	    		
	    		linkMap.put(
	    				(formattedTitle + totalPosts).trim(), 
	    				threadLink.attr("href"));
	    		tlContents.styleMap.put(
	    				(formattedTitle + totalPosts).trim(), 
	    				threadLink.attr("style"));
	    		tlContents.userMap.put(
	    				(formattedTitle + totalPosts).trim(), 
	    				threaduser.text());
	    		tlContents.lastUserMap.put(
	    				(formattedTitle + totalPosts).trim(), 
	    				repliesText.select("a[href*=members]").text());
	    		tlContents.lockedMap.put(
	    				(formattedTitle + totalPosts).trim(),
	    				isLocked);
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
			default:
				TextView tv = (TextView)arg0;
				final String linkText = tv.getText().toString();		
				Log.v(TAG, "User clicked '" + linkText + "'");
				final String trimmedLinkText = linkText.trim().replace("\u00a0", "");
				final String link = linkMap.get(trimmedLinkText);
				
				if(link == null) {
					Log.e(TAG, "Could Not Find Key of '" + trimmedLinkText + "'");
					Log.e(TAG, "Keys: " + linkMap.keySet().toString());
				} else {
					_intent = 
							new Intent(NewPostsActivity.this, ThreadActivity.class);
					_intent.putExtra("link", link);
					_intent.putExtra("title", linkText);
				}
		}
		
		if(_intent != null)
			startActivity(_intent);
	}
}
