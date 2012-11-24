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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.utils.UserProfile;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

/**
 * Activity that sets up the users profile
 * 
 * Required Intent Parameters:
 * none
 */
public class ProfileActivity extends ForumBaseActivity implements OnClickListener {

	private static String TAG = "ProfileActivity";
	private ArrayList<ProfileThreadStub> stubs;
	
	/**
	 * A stub inner class that defines a thread that the user
	 * has posted on recently
	 */
	private class ProfileThreadStub {
		private String name, link, text;
		
		public void setName(String name) { this.name = name; }
		public void setLink(String link) { this.link = link; }
		public void setText(String txt) { this.text = txt; }

		public String toString() { return name + ", " + link + ", " + text; }
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        setContentView(R.layout.activity_profile);
        
        Log.v(TAG, "Category Activity Started");
        
        constructView();
    }
    
    /**
     * Construct the profile view
     */
    private void constructView() {
    	tl = (TableLayout)findViewById(R.id.myTableLayoutProfile);
        
        loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        
        updaterThread = new Thread("CategoryThread") {
			public void run() {				
				Document doc = 
						VBForumFactory.getInstance().get(UserProfile.getUserProfileLink());
				String id = UserProfile.getUserProfileLink().substring(
						UserProfile.getUserProfileLink().lastIndexOf("-") + 1,
						UserProfile.getUserProfileLink().length() - 1);
				UserProfile.setUserId(id);
				getUserInformation(doc);
				
				runOnUiThread(new Runnable() {
                    public void run() {
                    	((TextView)findViewById(R.id.userNameText)).setText(
                    			UserProfile.getUsername() + " (ID: " + UserProfile.getUserId() + ")");
                    	((TextView)findViewById(R.id.userTitleText)).setText(UserProfile.getUserTitle());
                    	((TextView)findViewById(R.id.userPostCountText)).setText(UserProfile.getUserPostCount());
                    	((TextView)findViewById(R.id.joinDateText)).setText(UserProfile.getUserJoinDate());
                    	
                    	addRow(Color.BLACK, new String[]{"Your Recent Activity"}, 40, false);
                    	
                    	boolean alternate = true;
                    	for(ProfileThreadStub stub : stubs) {
                    		addRow(alternate? Color.GRAY : Color.DKGRAY,
                    				new String[]{stub.name + "\n\n" + stub.text},
                    				100, false);
                    		alternate = !alternate;
                    	}
                    }
				});

				loadingDialog.dismiss();
			}
        };
        updaterThread.start();
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

    	for(String text : texts) {
	    	/* Create a Button to be the row-content. */
	    	TextView b = new TextView(this);
	    	b.setId(id);
	    	
	    	if(text.indexOf("\n") != -1) {
	    	  SpannableString spanString = new SpannableString(text);
	    	  spanString.setSpan(new StyleSpan(Typeface.BOLD), 
	    				0, text.indexOf("\n"), 0);
	    	  spanString.setSpan(new StyleSpan(Typeface.ITALIC), 
	    				text.indexOf("\n") + 1, text.length(), 0);
    		  b.setText(spanString);
	    	} else {   	
	    	  SpannableString spanString = new SpannableString(text);
	    	  spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(), 0);
	    	  b.setText(spanString);
	    	}
	    	b.setOnClickListener(this);
	    	b.setTextSize((float) 10.0);
	    	b.setTextColor(Color.WHITE);
	        b.setPadding(5, 5, 5, 5);
	        
	
	    	/* Add Button to row. */
	        if(span) {
		        TableRow.LayoutParams params = new TableRow.LayoutParams();
		        params.span = 3;
		        tr_head.addView(b,params);
	        } else {
	        	tr_head.addView(b);
	        }
    	}

    	/* Add row to TableLayout. */
        tl.addView(tr_head,new TableLayout.LayoutParams(
    			LayoutParams.WRAP_CONTENT,
    			LayoutParams.WRAP_CONTENT));
    }
    
    /**
     * Get the user information from the users profile
     * @param doc	The page document
     */
    private void getUserInformation(Document doc) {
    	stubs = new ArrayList<ProfileThreadStub>();
    	
    	// Title
    	Elements userInfo = doc.select("div[id=main_userinfo]");
    	Elements title = userInfo.select("h2");
    	UserProfile.setUserTitle(title.text());
    	
    	// Posts
    	Elements statisticInfo = doc.select("fieldset[class=statistics_group]");
    	Elements post = statisticInfo.select("li");
    	
    	// Grab Post count, trap exception
    	try {
    		UserProfile.setUserPostCount(post.get(0).text() + " / " + post.get(1).text().split(" ",4)[3] + " per day");
    	} catch (Exception e) {
    		BugSenseHandler.sendExceptionMessage("Post Object", post.text(), e);
    		UserProfile.setUserPostCount("Error Getting Post Count");
    	}
    	
    	// Grab Join Date, trap exception
    	try {
    		UserProfile.setUserJoinDate(post.get(13).text());
    	} catch (Exception e) {
    		BugSenseHandler.sendExceptionMessage("Post Object", post.text(), e);
    		UserProfile.setUserJoinDate("Error Getting Join Date");
    	}
    	
    	// Threads
    	String link = "http://www.rx8club.com/search.php?do=finduser&u=" + UserProfile.getUserId();
    	doc = VBForumFactory.getInstance().get(link);
    	Elements threadlist = doc.select("table[id^=post]");
    	for(Element threadl : threadlist) {
    		ProfileThreadStub stub = new ProfileThreadStub();
    		Elements divs = threadl.getElementsByTag("div");
    		Elements div = divs.get(1).getElementsByTag("a");
    		stub.setLink(div.attr("href"));
    		stub.setName(div.text());
    		
    		div = divs.get(5).getElementsByTag("a");
    		stub.setText(div.text());
    		stubs.add(stub);
    	}
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
	@Override
	public void onClick(View arg0) {		
	}

	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
	 */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
}
