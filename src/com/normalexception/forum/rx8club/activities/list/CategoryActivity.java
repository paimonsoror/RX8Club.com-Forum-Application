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
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;
import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.preferences.PreferenceHelper;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.ViewContents;

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
	
	private static char lpad = '«';
	private static char rpad = '»';
	
	private String pageNumber = "";
	
	private String forumId = "";
	
	//private LinkedHashMap<String,String> styleMap, userMap, lastUserMap;
	private ThreadListContents tlContents = null;
	
	public int scaledImage = 12;
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("forumid", forumId);
		outState.putSerializable("tlcontents", tlContents);
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
			tlContents =
					(ThreadListContents) savedInstanceState.getSerializable("tlcontents");
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
	        setContentView(R.layout.activity_category);        

	        findViewById(R.id.newThreadButton).setOnClickListener(this);
	        findViewById(R.id.previousButton).setOnClickListener(this);
	        findViewById(R.id.nextButton).setOnClickListener(this);
	        
	        runOnUiThread(new Runnable() {
	            public void run() {
	            	// Hide pagination
	            	findViewById(R.id.paginationRow).setVisibility(View.GONE);
	            	findViewById(R.id.menuRow).setVisibility(View.GONE);
	            }
	        });
	        
	        Log.v(TAG, "Category Activity Started");
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);
	        
	        if(savedInstanceState == null)
	        	constructView();
	        else {
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
		        
		        viewContents = new ArrayList<ViewContents>();
		        linkMap = new LinkedHashMap<String,String>();
		        tlContents = new ThreadListContents();
		        
				final ArrayList<String> list = 
						getCategoryContents(doc, 
								link.substring(link.lastIndexOf('-') + 1, link.lastIndexOf('/')),
								link.contains("sale-wanted"));
		        
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

    /**
     * Add a row to the view
     * @param clr	The background color of the row
     * @param text	The text for the row
     * @param id	The id of the row
     */
    private void addRow(int clr, String texts[], int id, boolean span) {
    	String user = "", lastuser = "";
    	
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
	        
	        if(index == 0) {
	        	user = tlContents.userMap.get(text);
	        	lastuser = tlContents.lastUserMap.get(text);
	        	
        		int spanStart = text.lastIndexOf(lpad);
        		if(spanStart > -1) {
        			int spanEnd = text.lastIndexOf(rpad) + 1;
        			String preDetail = text.substring(0, spanStart);
        			String postDetail = text.substring(spanStart, spanEnd);
        			SpannableStringBuilder htext = new SpannableStringBuilder(
        					Html.fromHtml("&nbsp;" + preDetail + "&nbsp;&nbsp;" +
    						"<font color='yellow'>" + 
    						postDetail + 
    						"</font>"));
        			
        			htext.setSpan(new ImageSpan(scaledimg), 
        	    			0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        			
        			b.setText( htext,
	    						TextView.BufferType.SPANNABLE);
        		} else {
        			SpannableStringBuilder htext = new SpannableStringBuilder(" " + text);
        			htext.setSpan(new ImageSpan(scaledimg), 
        	    			0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
        			b.setText(clr == Color.BLUE? text : htext);
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
        
        if(PreferenceHelper.isShowPostDetailButton(this)) {
	        if(user != null) {
		        // Add username
		        tr_head = new TableRow(this);
		    	tr_head.setBackgroundColor(clr);
		    	
		    	TextView b = new TextView(this);
		    	float scaledText = (float) PreferenceHelper.getFontSize(this);
		    	b.setTextSize((float) (scaledText * 0.75));
		    	b.setTextColor(Color.WHITE);
		    	b.setTypeface(null, Typeface.ITALIC);
		    	
		    	StringBuilder userText = new StringBuilder();
		    	userText.append("\tStarted By: ");
		    	userText.append(user);
		    	if(lastuser.length() != 0) {
		    		userText.append(",\tLast: ");
		    		userText.append(lastuser);
		    	}
		    	b.setText(userText.toString());
		    	
		    	TableRow.LayoutParams params = new TableRow.LayoutParams();
		        params.span = 5;  
		        params.weight = 1f;
		    	tr_head.addView(b, params);
		    	
		    	tl.addView(tr_head, tl.getChildCount() - 1);
	        }
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
 
    	Elements threadLinks = doc.select("a[id^=thread_title]");
    	Elements repliesText = doc.select("td[title^=Replies");
    	
    	int zindex = 0;
    	for(Element threadLink : threadLinks) {
    		String idnumber = Utils.parseInts(threadLink.id());
    		Elements threadhrefs = doc.select("#td_threadtitle_" + idnumber + " a");
    		Elements threaduser = doc.select("#td_threadtitle_" + idnumber + " div.smallfont");
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
    		
    		// Remove page from the link
    		String realLink = Utils.removePageFromLink(link);  			
    		
    		if(threadhrefs.get(0).attr("href").contains(realLink) || isMarket) {
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
	    			
	    		linkMap.put(
	    				(threadLink.text() + totalPosts).trim(), 
	    				threadhrefs.get(index).attr("href"));
	    		tlContents.styleMap.put(
	    				(threadLink.text() + totalPosts).trim(), 
	    				threadhrefs.get(index).attr("style"));
	    		tlContents.userMap.put(
	    				(threadLink.text() + totalPosts).trim(), 
	    				threaduser.text());
	    		tlContents.lastUserMap.put(
	    				(threadLink.text() + totalPosts).trim(), 
	    				repliesText.get(zindex).select("a[href*=members]").text());
	    		
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
				final String trimmedLinkText = linkText.trim().replace("\u00a0", "");
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
