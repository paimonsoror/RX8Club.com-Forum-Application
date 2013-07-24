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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.activities.list.CategoryUtils;
import com.normalexception.forum.rx8club.activities.list.ThreadTypeFactory;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.UserProfile;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.CTextView;
import com.normalexception.forum.rx8club.view.ViewContents;

/**
 * Main activity for the application.  This is the main forum view that 
 * is displayed after login has been completed.
 * 
 * Required Intent Parameters:
 * none
 */
public class MainActivity extends ForumBaseActivity implements OnClickListener {
    
    private Map<String,String> linkMap;
    
	private static final String TAG = "Application";
	
	private Map<String, Collection<?>> mainForumContainer;
	
	public int scaledImage = 12;
	
	// The Forum's Main Page Has The Following Column
	@SuppressWarnings("unused")
	private static final int ROTOR_ICON = 0,
			                 FORUM_NAME = 1,
			                 LAST_POST  = 2,
			                 THREADS_CNT= 3,
			                 POSTS_CNT  = 4;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @SuppressWarnings("unchecked")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	    	Log.v(TAG, "Application Started");
	    	
	        super.onCreate(savedInstanceState);
	        super.setTitle("RX8Club.com Forums");
	        
	        setContentView(R.layout.activity_main);
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);

	        if(savedInstanceState == null)
	        	constructView();
	        else {
	        	viewContents = (ArrayList<ViewContents>) savedInstanceState.getSerializable("contents");
	        	updateView(viewContents);
	        }
    	} catch (Exception e) {
    		Log.e(TAG, "Fatal Error In Main Activity! " + e.getMessage());
    	}
    }
    
    /**
     * Start the application activity
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        final ForumBaseActivity thisActivity = this;
        
        /**
         * Thread created to list the contents of the forum into
         * the screen.  The screen contains a table layout, and
         * each category and each forum is inserted as a row
         */
        updaterThread = new Thread("Updater") {
        	public void run() {
        		try {
	        		Log.v(TAG, "Updater Thread Started");
	        		
	        		Document doc = null;
	        		viewContents = new ArrayList<ViewContents>();
	        		linkMap = new LinkedHashMap<String,String>();
	        		
	        		// User information
	        		if(!UserProfile.isInitialized() || UserProfile.getUserProfileLink().equals("")) {
	        			doc = 
	        				VBForumFactory.getInstance().get(thisActivity, VBForumFactory.getRootAddress());
	        			Elements userElement = 
	        				doc.select("a[href^=http://www.rx8club.com/members/" + UserProfile.getUsername().replace(".", "-") + "]");
	        			UserProfile.setUserProfileLink(userElement.attr("href"));
	        		}
	        		
	        		doc = getForum();
	        		
	                mainForumContainer = getCategories(doc);
	                
	                Log.v(TAG, "Document Container Read, size=" + mainForumContainer.size());
	                
	                viewContents.add(new ViewContents(Color.BLUE, new String[]{"Forum","Threads","Posts"}, 40, false));

	                Iterator<?> it = mainForumContainer.entrySet().iterator();
	                while (it.hasNext()) {
	        			@SuppressWarnings("unchecked")
						Map.Entry<String, List<String>> pairs = (Map.Entry<String, List<String>>)it.next();
	                    
	                    // Always add view objects on a Ui Thread
	                    viewContents.add(new ViewContents(Color.DKGRAY, pairs.getKey().split("�"), 10, true));           	
	                    
	                    List<String> vals = pairs.getValue();
	                    for(final String val : vals) {
	                    	 viewContents.add(new ViewContents(Color.GRAY, val.split("�"), 20, false));  
	                    }
	                    
	                    // avoids a ConcurrentModificationException
	                    it.remove(); 
	                }
	                
	                updateView(viewContents);
	                Log.v(TAG, "Dismissing Wait Dialog");
        		} catch(Exception e) {
        			thisActivity.runOnUiThread(new Runnable() {
        				public void run() {
        					Toast.makeText(thisActivity, 
                					"Sorry, there was an error connecting!", Toast.LENGTH_SHORT).show();
        				}
        			});
        		} finally {
        			if(loadingDialog != null)
        				loadingDialog.dismiss();
        		}
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
    			tl = (TableLayout)findViewById(R.id.myTableLayout);
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
    	// Create a new row to be added.
    	TableRow tr_head = new TableRow(this);
    	tr_head.setId(id);
    	tr_head.setBackgroundColor(clr);

    	// We need to decode the resource, and then scale
    	// down the image
    	Bitmap scaledimg = 
    			ThreadTypeFactory.getBitmap(this, scaledImage, scaledImage, false, false);
    	
    	int index = ROTOR_ICON;
    	for(String text : texts) {
	    	// Create a Button to be the row-content.
	    	CTextView b = new CTextView(this, this, id);
	    	
	    	SpannableStringBuilder htext = new SpannableStringBuilder(" " + text);
			htext.setSpan(new ImageSpan(scaledimg), 
	    			0, 1, Spannable.SPAN_INCLUSIVE_INCLUSIVE );
			b.setText(clr == Color.BLUE || clr == Color.DKGRAY || index > ROTOR_ICON? text : htext);
			
	        if(index == ROTOR_ICON) {
	        	// Convert dip to px
	        	Resources r = getResources();
        		int px = 
        			(int)TypedValue.applyDimension(
        					TypedValue.COMPLEX_UNIT_DIP, 0, r.getDisplayMetrics());
        		b.setWidth(px);
	        }
	
	        TableRow.LayoutParams params = new TableRow.LayoutParams();
	        params.span = span? 5 :	1;  
	        if(index == ROTOR_ICON) params.weight = 1f;
	        tr_head.addView(b,params);
	        index++;
    	}

    	// Add row to TableLayout.
        tl.addView(tr_head);
    }
    
    /**
     * Get the forum contents as a Map that links the category names
     * to a list of the forums within each category
     * @param root	The full forum document
     * @return		A map of the categories to the forums
     */
    private Map<String, Collection<?>> getCategories(Document root) {	
    	Map< String, Collection<?> > container = new LinkedHashMap< String, Collection<?> >();
    	
    	// Grab each category
    	Elements categories       = root.getElementsByClass("tcat");
    	
    	// We need to remove the first and last sections here because
    	// at this forum, the first category displays the member pictures
    	// while the last is the 'whats going on' section
    	categories.remove(0); categories.remove(categories.size() - 1);
    	
    	// Now grab each section within a category
    	Elements categorySections = root.select("tbody[id^=collapseobj_forumbit_]");
    	
    	// These should match in size
    	if(categories.size() != categorySections.size()) return container;
    	
    	// Create a list that will hold category contents
    	List<String> catContents = null;
    	
    	// Iterate over each category
    	int catIndex = 0;
    	for(Element category : categorySections) {
    		container.put(categories.get(catIndex++).text(), 
    				      catContents = new ArrayList<String>());
    		
    		Elements forums = category.select("tr[align=center]");
    		for(Element forum : forums) {
    			// Each forum object should have 5 columns
    			Elements columns = forum.select("tr[align=center] > td");
    			if(columns.size() != 5) continue;

    			String forum_name = columns.get(MainActivity.FORUM_NAME).select("strong").text();
    			String forum_href = columns.get(MainActivity.FORUM_NAME).select("a").attr("href");
    			String threads    = columns.get(MainActivity.THREADS_CNT).text();
    			String posts      = columns.get(MainActivity.POSTS_CNT).text();
    			
    			catContents.add(
    					String.format("%s�%s�%s", forum_name, threads, posts));
    			linkMap.put(forum_name, forum_href);
    		}
    	}
        
        return container;
    }

    /**
     * Grab the forum as a jsoup document
     * @return	A jsoup document object that contains the 
     * 			forum contents
     */
    public Document getForum() {    	
		LoginFactory lf = LoginFactory.getInstance();
		
		String output = "";
		
		try {
			VBForumFactory ff = VBForumFactory.getInstance();
			output = ff.getForumFrontpage(this, lf);
		} catch (IOException ioe) {
			Log.e(TAG, "Error Grabbing Forum Frontpage: " + ioe.getMessage());
		}		
		
	   	return Jsoup.parse(output);
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
				TextView tv = (TextView)arg0;
				Log.v(TAG, "Category Clicked: " + tv.getText());
				final String link = linkMap.get(tv.getText().toString().trim());
				if(link != null && !link.equals("")) {
					Log.v(TAG, "User Clicked: " + link);
					
					// Open new thread
					new Thread("RefreshDisplayList") {
						public void run() {
							Intent intent = 
									new Intent(MainActivity.this, CategoryActivity.class);
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
		// Do Nothing
	} 
}
