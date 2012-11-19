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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.utils.PreferenceHelper;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

public class PrivateMessageActivity extends ForumBaseActivity implements OnClickListener {

	private static String TAG = "PrivateMessageActivity";

	private ArrayList<PrivateMessage> privateMessages;
	private Map<String, String> linkMap;
	
	/**
	 * Container that holds information about each private message
	 */
	private class PrivateMessage implements Serializable, Parcelable {
		private static final long serialVersionUID = 1L;
		private String user, time, subject, date, link;
		public void setUser(String usr) { user = usr; }
		public String getUser() { return user; }
		public void setTime(String tim) { time = tim; }
		public String getTime() { return time; }
		public void setSubject(String subj) { subject = subj; }
		public String getSubject() { return subject; }
		public void setDate(String dat) { date = dat; }
		public String getDate() { return date; }
		public void setLink(String lnk) { link = lnk; }
		public String getLink() { return link; }
		
		public String toString() {
			// Log for debug purposes
			return String.format("[%s %s] %s | %s",
						getDate(),
						getTime(),
						getUser(),
						getSubject());
		}
		@Override
		public int describeContents() {
			return 0;
		}
		@Override
		public void writeToParcel(Parcel arg0, int arg1) {}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {	
		//super.onSaveInstanceState(outState);
		try {
			outState.putSerializable("contents", privateMessages);
		} catch (Exception e) {
			Log.e(TAG, "Error Serializing: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	@SuppressWarnings("unchecked")
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		//super.onRestoreInstanceState(savedInstanceState);
		try {
			if(savedInstanceState != null) {
				privateMessages = 
						(ArrayList<PrivateMessage>)savedInstanceState.getSerializable("contents");
			}
		} catch (Exception e) {
			Log.e(TAG, "Error UnSerializing: " + e.getMessage());
			BugSenseHandler.sendException(e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        setContentView(R.layout.activity_private_message);
        
        // Register the titlebar gui buttons
        this.registerGuiButtons();
        
        Log.v(TAG, "PM Activity Started");
        
        if(savedInstanceState == null)
        	constructView();
        else {
        	updateView(privateMessages);
        }
    }
    
    /**
     * Update the view contents
     * @param contents	List of view rows
     */
    private void updateView(final ArrayList<PrivateMessage> contents) {
    	runOnUiThread(new Runnable() {
    		public void run() {
    			tl = (TableLayout)findViewById(R.id.myTableLayoutPM);
    			tl.setColumnStretchable(0, true);
    			
				linkMap = new LinkedHashMap<String, String>();

    			addRow(Color.BLUE, "Subject", "User", "Date");
    			
    			String month = getMonthForInt(0);
    			for(PrivateMessage pm : privateMessages) {
    				String moNum = 
    						getMonthForInt(
    								Integer.parseInt(pm.getDate().split("-")[0]));
    				
    				if(!month.equals(moNum)) {
    					month = moNum;
    					addRow(Color.DKGRAY, month);
    				}
    				
    				//addRow(pm, alternate = !alternate);
    				addRow(Color.GRAY, pm.getSubject(), pm.getUser(), pm.getDate());
    				
    				linkMap.put(pm.getSubject(), pm.getLink());
    			}
    		}
    	});
    }
    
    /**
     * Convert an integer month to a string month
     * @param m	The integer month to convert
     * @return	A string representation of a month
     */
    private String getMonthForInt(int m) {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.MONTH, m - 1);
    	
    	// For API 9
    	//cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
    	
    	return String.format(Locale.US,"%tB",cal);
    }
    
    /**
     * Add a row to the view table
     * @param clr	The color of the background
     * @param texts	The text array that will be added to each row
     */
    private void addRow(int clr, String... texts) {
    	/* Create a new row to be added. */
    	TableRow tr_head = new TableRow(this);
    	tr_head.setId(31);
    	
    	int style = Typeface.NORMAL;
    	int index = 0;
    	for(String text : texts) {
	    	TextView b = new TextView(this);
	    	b.setId(32);
	    	b.setTextColor(Color.WHITE);
	    	b.setTextSize((float) PreferenceHelper.getFontSize(this));
	    	b.setPadding(5,5,5,5);
	    	tr_head.setBackgroundColor(clr);
			SpannableString spanString = new SpannableString(text);
			spanString.setSpan(new StyleSpan(style), 0, text.length(), 0);
			b.setText(spanString);
			b.setOnClickListener(this);
			
			/* Add Button to row. */
	        TableRow.LayoutParams params = new TableRow.LayoutParams();
	        if(index == 0) params.weight = 1f;
	        tr_head.addView(b,params);
	        index++;
    	}
        
        /* Add row to TableLayout. */
        tl.addView(tr_head);
    }
    
    /**
     * Construct view by grabbing all private messages.  This is only done
     * if the view is called for the first time.  If there was a savedinstance
     * of the view then this is not called
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
		
        updaterThread = new Thread("NewPostsThread") {
 			public void run() { 		
 				Document doc = VBForumFactory.getInstance().get(WebUrls.pmUrl);
 				privateMessages = new ArrayList<PrivateMessage>();
 				
 				Elements collapse = doc.select("tbody[id^=collapseobj_pmf0]");
 				for(Element coll : collapse) {
 					Elements trs = coll.select("tr");
 					for(Element tr : trs) {
 						Elements alt1s = tr.getElementsByClass("alt1Active");
 						for(Element alt1 : alt1s) {
 							Elements divs = alt1.select("div");
 							
 							// First grab our link
 							Elements linkElement = divs.get(0).select("a[rel=nofollow]");
 							String pmLink = linkElement.attr("href");
 							
 							// There should be two divs here with text in it
 							// the first is 'MM-DD-YYYY Subject'
 							String dateSubject = divs.get(0).text();
 							String[] dateSubjectSplit = dateSubject.split(" ", 2);
 							
 							// The second is HH:MM AMPM User
 							String timeTimeUser = divs.get(1).text();
 							String[] timeTimeUserSplit = timeTimeUser.split(" ", 3);
 							
 							// Create new pm
 							PrivateMessage pm = new PrivateMessage();
 							pm.setDate(dateSubjectSplit[0]);
 							pm.setSubject(dateSubjectSplit[1]);
 							pm.setTime(timeTimeUserSplit[0] + timeTimeUserSplit[1]);
 							pm.setUser(timeTimeUserSplit[2]);
 							pm.setLink(pmLink);
 							
 							Log.v(TAG, pm.toString());
 							
 							// Save new pm
 							privateMessages.add(pm);
 						}
 					}
 				}
 				
 				updateView(privateMessages);
 				
 				loadingDialog.dismiss();
 			}
        };
        updaterThread.start();
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		
		switch(arg0.getId()) {	
		default:
			Log.v(TAG, "Category Clicked");
			TextView tv = (TextView)arg0;
			final String link = linkMap.get(tv.getText().toString());
			if(link != null && !link.equals("")) {
				Log.v(TAG, "User Clicked: " + link);
				
				// Open new thread
				new Thread("RefreshDisplayList") {
					public void run() {
						Intent intent = 
								new Intent(PrivateMessageActivity.this, 
										PrivateMessageViewActivity.class);
						intent.putExtra("link", link);
						startActivity(intent);
					}
				}.start();	
			}
			break;
		}
	}

    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
}
