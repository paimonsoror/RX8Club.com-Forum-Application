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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.activities.list.CategoryUtils;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.UserProfile;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.category.CategoryView;
import com.normalexception.forum.rx8club.view.category.CategoryViewArrayAdapter;

/**
 * Main activity for the application.  This is the main forum view that 
 * is displayed after login has been completed.
 * 
 * Required Intent Parameters:
 * none
 */
public class MainActivity extends ForumBaseActivity {
    
	private static final String TAG = "Application";
	
	private ArrayList<CategoryView> mainList;
	private CategoryViewArrayAdapter cva;
	
	private int scaledImage = 12;
	
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
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	    	Log.v(TAG, "Application Started");
	    	
	        super.onCreate(savedInstanceState);
	        super.setTitle("RX8Club.com Forums");
	        
	        setContentView(R.layout.activity_basiclist);
	        findViewById(R.id.mainlisttitle).setVisibility(View.GONE);
	        
	        scaledImage = CategoryUtils.setScaledImageSizes(this);

	        if(savedInstanceState == null)
	        	constructView();
	        else {
	        	updateList();
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

	        		// User information
	        		if(!UserProfile.isInitialized() || UserProfile.getUserProfileLink().equals("")) {
	        			doc = 
	        				VBForumFactory.getInstance().get(thisActivity, VBForumFactory.getRootAddress());
	        			Elements userElement = 
	        				doc.select("a[href^=http://www.rx8club.com/members/" + UserProfile.getUsername().replace(".", "-") + "]");
	        			UserProfile.setUserProfileLink(userElement.attr("href"));
	        		}
	        		
	        		doc = getForum();
	        		
	        		mainList           = new ArrayList<CategoryView>();
	                getCategories(doc);
	                
	                updateList();
	                Log.v(TAG, "Dismissing Wait Dialog");
        		} catch(Exception e) {
        			thisActivity.runOnUiThread(new Runnable() {
        				public void run() {
        					Toast.makeText(thisActivity, 
                					"Sorry, there was an error connecting!", Toast.LENGTH_SHORT).show();
        				}
        			});
        			e.printStackTrace();
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
    private void updateList() {
    	final Activity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {	        
		    	ListView lv = (ListView)findViewById(R.id.mainlistview);
		    	cva = new CategoryViewArrayAdapter(a, R.layout.view_category, mainList);
				lv.setAdapter(cva);
				lv.setOnItemClickListener(new OnItemClickListener() {
					@Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
						CategoryView cv = (CategoryView) parent.getItemAtPosition(position);
						Log.v(TAG, "Category Clicked: " + cv.getTitle());
						if(cv.getLink() != null) {
							Intent intent = 
									new Intent(MainActivity.this, CategoryActivity.class);
							intent.putExtra("link", cv.getLink());
							startActivity(intent);
						}
					}
				});
            }
    	});
    }
    
    /**
     * Get the forum contents as a Map that links the category names
     * to a list of the forums within each category
     * @param root	The full forum document
     * @return		A map of the categories to the forums
     */
    private void getCategories(Document root) {	
    	
    	// Grab each category
    	Elements categories       = root.getElementsByClass("tcat");
    	
    	// We need to remove the first and last sections here because
    	// at this forum, the first category displays the member pictures
    	// while the last is the 'whats going on' section
    	categories.remove(0); categories.remove(categories.size() - 1);
    	
    	// Now grab each section within a category
    	Elements categorySections = root.select("tbody[id^=collapseobj_forumbit_]");
    	
    	// These should match in size
    	if(categories.size() != categorySections.size()) return;
    	
    	// Iterate over each category
    	int catIndex = 0;
    	for(Element category : categorySections) {
    		
    		CategoryView cv = new CategoryView();
    		cv.setTitle(categories.get(catIndex++).text());
    		mainList.add(cv);
    		
    		Elements forums = category.select("tr[align=center]");
    		for(Element forum : forums) {
    			cv = new CategoryView();
    			
    			// Each forum object should have 5 columns
    			Elements columns = forum.select("tr[align=center] > td");
    			if(columns.size() != 5) continue;

    			String forum_name = columns.get(MainActivity.FORUM_NAME).select("strong").text();
    			String forum_href = columns.get(MainActivity.FORUM_NAME).select("a").attr("href");
    			String forum_desc = "";
    			try {
    				forum_desc = 
    					columns.get(MainActivity.FORUM_NAME).select("div[class=smallfont]").first().text();
    			} catch (NullPointerException npe) { /* Some might not have a desc */ }
    			String threads    = columns.get(MainActivity.THREADS_CNT).text();
    			String posts      = columns.get(MainActivity.POSTS_CNT).text();
    			
    			cv.setTitle(forum_name);
    			cv.setThreadCount(threads);
    			cv.setPostCount(posts);
    			cv.setLink(forum_href);
    			cv.setDescription(forum_desc);
    			mainList.add(cv);
    		}
    	}
        
        return;
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

	@Override
	protected void enforceVariants(int currentPage, int lastPage) {	
	}
}
