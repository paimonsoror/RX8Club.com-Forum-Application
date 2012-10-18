package com.normalexception.forum.rx8club.activities;

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
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.utils.PreferenceHelper;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.ViewContents;

public class CategoryActivity extends ForumBaseActivity implements OnClickListener {
	
	private static final String TAG = "Application:Category";
	private static String link;
	
	private static char lpad = '«';
	private static char rpad = '»';
	
	private String pageNumber = "";
	
	private String forumId = "";
	
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
			forumId = savedInstanceState.getString("forumid");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        try {
        	super.onCreate(savedInstanceState);
	        super.setTitle("RX8Club.com Forums");
	        setContentView(R.layout.activity_category);
	        
	        // Register the titlebar gui buttons
	        this.registerGuiButtons();	        

	        findViewById(R.id.newThreadButton).setOnClickListener(this);
	        
	        runOnUiThread(new Runnable() {
	            public void run() {
	            	// Hide pagination
	            	findViewById(R.id.paginationRow).setVisibility(View.GONE);
	            	findViewById(R.id.menuRow).setVisibility(View.GONE);
	            }
	        });
	        
	        Log.v(TAG, "Category Activity Started");
	        
	        if(savedInstanceState == null)
	        	constructView();
	        else {
	        	viewContents = (ArrayList<ViewContents>) savedInstanceState.getSerializable("contents");
	        	updateView(viewContents);
	        }
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Category Activity! " + e.getMessage());
			BugSenseHandler.sendException(e);
		}	
    }
	
	/**
	 * Construct the view for the activity
	 */
	private void constructView() {
		loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		
		updaterThread = new Thread("CategoryThread") {
			public void run() {
				link = 
		        		(String) getIntent().getSerializableExtra("link");
				pageNumber = 
						(String) getIntent().getStringExtra("page");
				if(pageNumber == null) pageNumber = "1";
				
		        Document doc = VBForumFactory.getInstance().get(link);
		        forumId = link.substring(link.lastIndexOf("-") + 1);
		        
		        // Make sure forumid doesn't end with a "/"
		        forumId = Utils.parseInts(forumId);
		        
		        viewContents = new ArrayList<ViewContents>();
		        linkMap = new LinkedHashMap<String,String>();
		        
				final ArrayList<String> list = 
						getCategoryContents(doc, link.substring(link.lastIndexOf('-') + 1));
		        
				viewContents.add(
						new ViewContents(Color.BLUE, new String[]{"Forum", "Posts", "Views"}, 40, false));
		    	
		    	for(String lst : list) {     
		    		final String[] rowText = lst.split("µ");
		    		viewContents.add(
		    				new ViewContents(Color.DKGRAY, new String[]{rowText[0], rowText[1], rowText[2]}, 90, false));
				}
		    	
		    	updateView(viewContents);
		    	
		    	runOnUiThread(new Runnable() {
		            public void run() {
		            	// Restore pagination
		            	findViewById(R.id.paginationRow).setVisibility(View.VISIBLE);
		            	findViewById(R.id.menuRow).setVisibility(View.VISIBLE);
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
    			tl = (TableLayout)findViewById(R.id.myTableLayoutCategory);
    			tl.setColumnStretchable(0, true);
    			for(ViewContents view : contents) {
    				addRow(view.getClr(), view.getTexts(), view.getId(), view.isSpan());
    			}
    		}
    	});
    }
	
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
	@Override
	public void onDestroy() {
		Log.v(TAG, "Closing Category Activity");
		super.onDestroy();		
	}

    /**
     * Add a row to the view
     * @param clr	The background color of the row
     * @param text	The text for the row
     * @param id	The id of the row
     */
    private void addRow(int clr, String texts[], int id, boolean span) {
    	/* Create a new row to be added. */
    	TableRow tr_head = new TableRow(this);
    	tr_head.setId(id);
    	tr_head.setBackgroundColor(clr);
    	
    	int index = 0;
    	for(String text : texts) {
	    	/* Create a Button to be the row-content. */
	    	TextView b = new TextView(this);
	    	b.setId(id);
	    	b.setOnClickListener(this);
	    	b.setTextSize((float) PreferenceHelper.getFontSize(this));
	    	b.setTextColor(Color.WHITE);
	        b.setPadding(5, 5, 5, 5);
	        
	        if(index == 0) {
        		int spanStart = text.lastIndexOf(lpad);
        		if(spanStart > -1) {
        			int spanEnd = text.lastIndexOf(rpad) + 1;
        			String preDetail = text.substring(0, spanStart);
        			String postDetail = text.substring(spanStart, spanEnd);
        			b.setText( 
	    				Html.fromHtml(preDetail + "&nbsp;&nbsp;" +
	    						"<font color='yellow'>" + 
	    						postDetail + 
	    						"</font>"),
	    						TextView.BufferType.SPANNABLE);
        		} else {
        			b.setText(text);
        		}
        		
        		// Convert dip to px
            	Resources r = getResources();
            	int px = 
            			(int)TypedValue.applyDimension(
            					TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
            	b.setWidth(px);
	        } else {
	        	b.setText(text);
	        }
	
	        /* Add Button to row. */
	        TableRow.LayoutParams params = new TableRow.LayoutParams();
	        params.span = span? 5 :	1;  
	        if(index == 0) params.weight = 1f;
	        tr_head.addView(b,params);
	        
	        index++;
    	}

    	/* Add row to TableLayout. */
        tl.addView(tr_head,tl.getChildCount() - 1);
    }
    
    /**
     * Grab contents from the forum that the user clicked on
     * @param doc	The document parsed from the link
     * @param id	The id number of the link
     * @return		An arraylist of forum contents
     */
    public ArrayList<String> getCategoryContents(Document doc, String id) {
    	ArrayList<String> titles = new ArrayList<String>();
    	
    	// Update pagination
    	updatePagination(doc);
    	
    	// Make sure id contains only numbers
    	id = Utils.parseInts(id);
 
    	Elements threadLinks = doc.select("a[id^=thread_title]");
    	Elements repliesText = doc.select("td[title^=Replies");
    	
    	int zindex = 0;
    	for(Element threadLink : threadLinks) {
    		String idnumber = Utils.parseInts(threadLink.id());
    		Elements threadhrefs = doc.select("#td_threadtitle_" + idnumber + " a");
    		Elements threadicon = doc.select("img[id=thread_statusicon_" + idnumber + "]");
    		String totalPostsInThreadTitle = threadicon.attr("alt");
    		String totalPosts;
    		
    		if(totalPostsInThreadTitle != null && totalPostsInThreadTitle.length() > 0)
    			totalPosts = lpad +
    				totalPostsInThreadTitle.split(" ")[2] + 
    				" " + 
    				totalPostsInThreadTitle.split(" ")[3] +
    				rpad;
    		else
    			totalPosts = "";
    		
    		if(threadhrefs.get(0).attr("href").contains(link)) {
	    		String idlink = "http://www.rx8club.com/misc.php?do=whoposted&t=" + idnumber;
	    		
	    		String txt = repliesText.get(zindex).getElementsByClass("alt2").attr("title");
	    		String splitter[] = txt.split(" ", 4);
	    		String postCount = splitter[1].substring(0, splitter[1].length() - 1);
	    		String views = splitter[3];

	    		titles.add(threadLink.text() + totalPosts + "µ" + postCount + "µ" + views);
	    		
	    		if(threadhrefs.isEmpty())
	    			Log.e(TAG, "Thread Links are empty on this iteration! " + idlink);
	    		
	    		int index = 0;
	    		if(threadhrefs.size() > 1)
	    			index = 1;
	    			
	    		Log.v(TAG, "Adding: " + threadhrefs.get(index).attr("href"));
	    		linkMap.put((threadLink.text() + totalPosts).trim(), threadhrefs.get(index).attr("href"));
	    		
	    		zindex++;
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
				_intent.putExtra("link", Utils.decrementPage(link, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				this.finish();
				break;
			case R.id.nextButton:
				_intent = new Intent(CategoryActivity.this, CategoryActivity.class);
				_intent.putExtra("link", Utils.incrementPage(link, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
				this.finish();
				break;
			case R.id.newThreadButton:
				_intent = new Intent(CategoryActivity.this, NewThreadActivity.class);
				_intent.putExtra("link", "http://www.rx8club.com/newthread.php?do=newthread&f=" + forumId);
				_intent.putExtra("source", link);
				_intent.putExtra("forumid", forumId);
				result = true;
				break;
			default:
				TextView tv = (TextView)arg0;
				final String linkText = tv.getText().toString();		
				Log.v(TAG, "User clicked '" + linkText + "'");

				final String trimmedLinkText = linkText.replace("\u00a0", "");
				final String link = linkMap.get(trimmedLinkText);
				_intent = new Intent(CategoryActivity.this, ThreadActivity.class);
				_intent.putExtra("link", link);
				_intent.putExtra("title", linkText);
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
