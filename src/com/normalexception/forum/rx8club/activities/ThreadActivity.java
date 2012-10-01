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

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.http.client.ClientProtocolException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.handler.ForumImageHandler;
import com.normalexception.forum.rx8club.handler.GuiHandlers;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.ViewContents;

public class ThreadActivity extends ForumBaseActivity implements OnClickListener {

	private static final String TAG = "Application:Thread";

	private String currentPageLink;
	private String currentPageTitle;
	
	private String threadNumber;
	
	private String pageNumber = "1";
	
	private String securityToken = "none";
	private String postNumber = "none";
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("threadnumber", threadNumber);
		outState.putString("securitytoken", securityToken);
		outState.putString("postnumber", postNumber);
		outState.putString("final", finalPage);
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
				threadNumber = savedInstanceState.getString("threadnumber");
				securityToken = savedInstanceState.getString("securitytoken");
				postNumber = savedInstanceState.getString("postnumber");
				finalPage = savedInstanceState.getString("final");
			}				
		} catch (Exception e) {
			Log.e(TAG, "Error Restoring Contents: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
	}
	
	/**
	 * Container for thread posts and thread post related information
	 */
	private class ThreadPost {
		private String name, title, location, join, postcount, post;
		public String toString() {
			return name + "|" + title + "|" + location + 
					"|" + join + "|" + postcount + "|" + post;
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
	         setContentView(R.layout.activity_thread);
	         
	         Log.v(TAG, "Category Activity Started");
	         
	         findViewById(R.id.newTopicsButton).setOnClickListener(new GuiHandlers(this));
	         findViewById(R.id.newPmButton).setOnClickListener(new GuiHandlers(this));
	         findViewById(R.id.liveButton).setOnClickListener(new GuiHandlers(this));
	         findViewById(R.id.profileButton).setOnClickListener(new GuiHandlers(this));
	         findViewById(R.id.searchButton).setOnClickListener(new GuiHandlers(this));
	         findViewById(R.id.previousButton).setOnClickListener(this);
	         findViewById(R.id.nextButton).setOnClickListener(this);
	         findViewById(R.id.paginationText).setOnClickListener(this);
	         findViewById(R.id.submitButton).setOnClickListener(this);
	         findViewById(R.id.firstButton).setOnClickListener(this);
	         findViewById(R.id.lastButton).setOnClickListener(this);
	         
	         runOnUiThread(new Runnable() {
		            public void run() {
		            	findViewById(R.id.controlsRow).setVisibility(View.GONE);
		            }
	         });
	         
	         if(savedInstanceState == null)
		        	constructView();
		        else {
		        	viewContents = (ArrayList<ViewContents>) savedInstanceState.getSerializable("contents");
		        	updateView(viewContents);
		        }
	 	} catch (Exception e) {
	 		Log.e(TAG, "Fatal Error In Thread Activity! " + e.getMessage());
	 		BugSenseHandler.sendException(e);
	 	}
    }
    
    /**
     * Construct the thread activity view
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        
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
				
				Document doc = VBForumFactory.getInstance().get(currentPageLink);
				viewContents = new ArrayList<ViewContents>();
				
				final ArrayList<ThreadPost> list = getThreadContents(doc);
				
				viewContents.add(
						new ViewContents(Color.BLUE, currentPageTitle, 40, false, true));
		        
               	for(ThreadPost post : list) {                   		
               		String text = post.name + "\n" + post.title + "\n" + 
               				post.location + "\n" + post.join + "\n" + post.postcount;
               		
               		viewContents.add(new ViewContents(Color.GRAY, text, 22, false, false));
               		viewContents.add(new ViewContents(Color.DKGRAY, post.post, 23, true, false));
               	}
		    	
		    	runOnUiThread(new Runnable() {
		            public void run() {
		            	findViewById(R.id.controlsRow).setVisibility(View.VISIBLE);
		            }
		    	});
		    	
		    	updateView(viewContents);
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
    			tl = (TableLayout)findViewById(R.id.myTableLayoutThread);
    			
    			for(ViewContents view : contents) {
    				addRow(view.getClr(), view.getText(), view.getId(), view.isHtml(), view.isSpan());
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
    private void addRow(int clr, String text, int id, boolean html, boolean span) {
    	/* Create a new row to be added. */
    	TableRow tr_head = new TableRow(this);
    	tr_head.setId(id);
    	tr_head.setBackgroundColor(clr);
    	
    	if(clr == Color.DKGRAY)
    		tr_head.setPadding(5, 5, 5, 15);

    	/* Create a Button to be the row-content. */
    	TextView b = new TextView(this);
    	b.setId(id);
    	b.setMovementMethod(LinkMovementMethod.getInstance());
    	if(!html && text.indexOf("\n") != -1) {
    		SpannableString spanString = new SpannableString(text);
    		spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.indexOf("\n"), 0);
    		b.setText(spanString);
    	} else {
    		// remove quotes for now
    		text = reformatQuotes(text);
    		ForumImageHandler imageHandler = new ForumImageHandler(b, this);
    		b.setText(html? Html.fromHtml(text + "<br><br><br>", imageHandler, null) : text);
    	}
    	
    	b.setTextSize((float) 10.0);
    	b.setTextColor(Color.WHITE);
    	
    	/* Add Button to row. */
    	TableRow.LayoutParams params = new TableRow.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT,
                1.0f);
        
    	if(span) {
	        params.gravity = Gravity.CENTER;
	        params.span = 5;
    	} else {
        	// Convert dip to px
        	Resources r = getResources();
        	int px = 
        			(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
        	b.setWidth(px);
    	}

    	/* Add Button to row. */
        tr_head.addView(b, params);      

    	/* Add row to TableLayout. */
        tl.addView(tr_head, tl.getChildCount() - 1);
    }
    
    /**
     * Reformat the quotes to blockquotes since Android fromHtml does
     * not parse tables
     * @param source	The source text
     * @return			The updated source text
     */
    private String reformatQuotes(String source) {
    	String finalText = "";

    	StringTokenizer st = new StringTokenizer(source, "\r\n\t");
    	while (st.hasMoreTokens()) {
        	String nextTok = st.nextToken();      	
        	if(nextTok.contains("<table ")) {
        		nextTok = "<blockquote>";
        	}
        	if(nextTok.contains("</table>")) {
        		nextTok = nextTok.replace("</table>","</blockquote>");
        	}

        	finalText += nextTok + " ";
        }
        
        return finalText;
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
    	
    	// Get Post Number and security token
    	securityToken = doc.select("input[name=securitytoken]").attr("value");
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
        	
        	titles.add(user);
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
    				findViewById(R.id.firstButton).setEnabled(false);
    			}
    		});
    	else 
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.previousButton).setEnabled(true);
    				findViewById(R.id.firstButton).setEnabled(true);
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
    	
    	if(myPage == lastPage) {
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.lastButton).setEnabled(false);
    			}
    		});
    	} else {
    		runOnUiThread(new Runnable() {
    			public void run() {
    				findViewById(R.id.lastButton).setEnabled(true);
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
				_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
				_intent.putExtra("link", Utils.decrementPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) - 1));
				_intent.putExtra("title", this.currentPageTitle);
				this.finish();
				break;
			case R.id.nextButton:
				_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
				_intent.putExtra("link", Utils.incrementPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber) + 1));
				_intent.putExtra("title", this.currentPageTitle);
				this.finish();
				break;
			case R.id.submitButton:
				try {
					String toPost = 
							((TextView)findViewById(R.id.postBox)).getText() + 
							"\n\nPosted From RX8Club.com Android App";
					VBForumFactory.getInstance().submitPost(securityToken, threadNumber, postNumber, toPost);
					_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
					_intent.putExtra("link", this.currentPageLink);
					_intent.putExtra("page", String.valueOf(Integer.parseInt(this.pageNumber)));
					_intent.putExtra("title", this.currentPageTitle);
					this.finish();
				} catch (ClientProtocolException e) {
					BugSenseHandler.sendException(e);
				} catch (IOException e) {
					BugSenseHandler.sendException(e);
				}
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
			    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int whichButton) {
			            // Do nothing.
			        }
			    }).show();				
				break;
				
			case R.id.firstButton:
				_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
				_intent.putExtra("link", Utils.getPage(this.currentPageLink, Integer.toString(1)));
				_intent.putExtra("page", "1");
				_intent.putExtra("title", this.currentPageTitle);
				finish();
				break;
				
			case R.id.lastButton:
				_intent = new Intent(ThreadActivity.this, ThreadActivity.class);
				_intent.putExtra("link", Utils.getPage(this.currentPageLink, this.finalPage));
				_intent.putExtra("page", this.finalPage);
				_intent.putExtra("title", this.currentPageTitle);
				finish();
				break;	
		}	
		
		if(_intent != null)
			startActivity(_intent);
	}
}
