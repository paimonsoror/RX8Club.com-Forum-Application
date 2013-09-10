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
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.cache.impl.UserProfileCache;
import com.normalexception.forum.rx8club.cache.impl.ViewListCache;
import com.normalexception.forum.rx8club.favorites.FavoriteFactory;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.user.UserProfile;
import com.normalexception.forum.rx8club.view.category.CategoryView;
import com.normalexception.forum.rx8club.view.category.CategoryViewArrayAdapter;
import com.normalexception.forum.rx8club.view.category.SubCategoryView;

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
	
	// The Forum's Main Page Has The Following Column
	@SuppressWarnings("unused")
	private static final int ROTOR_ICON = 0,
			                 FORUM_NAME = 1,
			                 LAST_POST  = 2,
			                 THREADS_CNT= 3,
			                 POSTS_CNT  = 4;
	
	private ViewListCache<CategoryView> hcache = null;
	private UserProfileCache upcache = null;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	try {
	    	Log.v(TAG, "Application Started");
	    	
	        super.onCreate(savedInstanceState);
	        	        
	        setContentView(R.layout.activity_basiclist);
	        findViewById(R.id.mainlisttitle).setVisibility(View.GONE);

	        // Read in the favorites if they exist
	        FavoriteFactory.getInstance();
	        
	        // Now that we introduced a cache, we are going to first
	        // check to see if the cache is valid, if so, use it 
	        // so that we dont waste bandwidth
	        hcache = new ViewListCache<CategoryView>(this, getString(R.string.file_homecache));
	        if(hcache.isCacheExpired()) {
	        	Log.d(TAG, "Cache Expired, Creating Main");
		        if(savedInstanceState == null || 
		        		(cva == null || cva.getCount() == 0)) {
		        	constructView();
		        }
	        } else {
	        	mainList = (ArrayList<CategoryView>) hcache.getCachedContents();
		        updateList();
	        }
	        
	        // Once we brought a cache into the mix, we now need a way to 
	        // validate the user profile.  Unfortunately we can't place
	        // the data in our cache, because we cache the main page
	        // regardless of user / guest.  What we will do now, is cache
	        // the user profile as well.
	        if(LoginFactory.getInstance().isLoggedIn()) {
		        String currentUser = UserProfile.getInstance().getUsername();
		        upcache = new UserProfileCache(this, currentUser);
		        UserProfile cachedProfile = upcache.getCachedContents();
		        if(cachedProfile == null || 
		        		!cachedProfile.getUsername().equals(currentUser)) {
		        	Log.d(TAG, "User Cache Expired, Recreating");
		        	constructUserProfile(null);
		        } else {
		        	UserProfile.getInstance().copy(upcache.getCachedContents());
		        }
	        }
    	} catch (Exception e) {
    		Log.e(TAG, "Fatal Error In Main Activity! " + e.getMessage());
    	}
    }
    
	/**
	 * User profile will be read as an async task after the main
	 * activity has started.  This doesn't always run, only when 
	 * the cache is either non-existant, or expired
	 * @param doc	The current page
	 */
	private void constructUserProfile(final Document doc) {
		final ForumBaseActivity thisActivity = this;
		new AsyncTask<Void,String,Void>() {
			@Override
		    protected void onPreExecute() {
		    	loadingDialog = 
						ProgressDialog.show(thisActivity, 
								getString(R.string.loading), 
								"Validating Profile", true);
		    }
			@Override
			protected Void doInBackground(Void... params) {		
    			if(LoginFactory.getInstance().isLoggedIn()) {
    				Document localDoc = doc;
    				if(localDoc == null)
    					localDoc = VBForumFactory.getInstance().get(
	    					thisActivity, VBForumFactory.getRootAddress());
    				if(localDoc != null) {
    					Elements userElement = 
    							localDoc.select("a[href^=http://www.rx8club.com/members/" + 
    									UserProfile.getInstance().getUsername().replace(".", "-") + "]");
    					UserProfile.getInstance().setUserProfileLink(userElement.attr("href"));
    				}
    			}
				return null;
			}	
			@Override
		    protected void onPostExecute(Void result) {
				loadingDialog.dismiss();
				upcache.cacheContents(UserProfile.getInstance());
			}
		}.execute();
	}
	
    /**
     * Start the application activity
     */
    private void constructView() {
        final ForumBaseActivity thisActivity = this;
        
        /**
         * Thread created to list the contents of the forum into
         * the screen.  The screen contains a table layout, and
         * each category and each forum is inserted as a row
         */
        updaterTask = new AsyncTask<Void,String,Void>() {
        	Document doc = null;
        	
        	@Override
		    protected void onPreExecute() {
		    	loadingDialog = 
						ProgressDialog.show(thisActivity, 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
		    }
        	@Override
			protected Void doInBackground(Void... params) {
        		try {
	        		Log.v(TAG, "Updater Thread Started");
	        		
	        		publishProgress(getString(R.string.asyncDialogGrabForumContents));
	        		doc 	 = getForum();
	        		
	        		mainList = new ArrayList<CategoryView>();
	        		
	        		publishProgress(getString(R.string.asyncDialogCategorySort));
	                getCategories(doc);
	                
	                publishProgress(getString(R.string.asyncDialogUpdateCache));
		        	hcache.cacheContents(mainList);
        		} catch(Exception e) {
        			thisActivity.runOnUiThread(new Runnable() {
        				public void run() {
        					Toast.makeText(thisActivity, 
                					R.string.connectionError, 
                					Toast.LENGTH_SHORT).show();
        				}
        			});
        			e.printStackTrace();
        		}
        		return null;
        	}
        	@Override
		    protected void onProgressUpdate(String...progress) {
		        loadingDialog.setMessage(progress[0]);
		    }			
			@Override
		    protected void onPostExecute(Void result) {
				updateList();
				loadingDialog.dismiss();
        		
				if(LoginFactory.getInstance().isLoggedIn()) {
	        		// Construct a new user profile
	        		constructUserProfile(doc);
				}
			}
        };
        updaterTask.execute();
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
		        lv.setLongClickable(true);
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
    	Elements categories       = root.select("td[class=tcat][colspan=5]");	
    	Log.d(TAG, "Category Size: " + categories.size());
    	
    	// Now grab each section within a category
    	Elements categorySections = root.select("tbody[id^=collapseobj_forumbit_]");
    	
    	// These should match in size
    	if(categories.size() != categorySections.size()) {
    		Log.w(TAG, 
    				String.format("Size of Categories (%d) doesn't match Category Sections (%d)", 
    						categories.size(), categorySections.size()));
    		return;
    	}
    		
    	// Iterate over each category
    	int catIndex = 0;
    	for(Element category : categorySections) {
    		
    		CategoryView cv = new CategoryView();
    		cv.setTitle(categories.get(catIndex++).text());
    		mainList.add(cv);
    		
    		Elements forums = category.select("tr[align=center]");
    		for(Element forum : forums) {
    			cv = new CategoryView();
    			List<SubCategoryView> scvList = cv.getSubCategories();
    			
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
    			
    			// Lets grab each subcategory
    			Elements subCats  = columns.select("tbody a");
    			for(Element subCat : subCats) {
    				SubCategoryView scv = new SubCategoryView();
    				scv.setLink(subCat.attr("href"));
    				scv.setTitle(subCat.text().toString());
    				scvList.add(scv);
    			}
    			
    			cv.setTitle(forum_name);
    			cv.setThreadCount(threads);
    			cv.setPostCount(posts);
    			cv.setLink(forum_href);
    			cv.setDescription(forum_desc);
    			cv.setSubCategories(scvList);
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
		
		String output = null;
		
		try {
			VBForumFactory ff = VBForumFactory.getInstance();
			output = ff.getForumFrontpage(this, lf);
		} catch (IOException ioe) {
			Log.e(TAG, "Error Grabbing Forum Frontpage: " + ioe.getMessage());
		}		
				
		if(output == null) {
			Toast.makeText(this, 
					R.string.connectionError, 
					Toast.LENGTH_LONG).show();
			returnToLoginPage(false);
			return null;
		} else {
			return Jsoup.parse(output);
		}
    }
}
