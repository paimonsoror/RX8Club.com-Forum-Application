package com.normalexception.app.rx8club.fragment;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.cache.impl.UserProfileCache;
import com.normalexception.app.rx8club.cache.impl.ViewListCache;
import com.normalexception.app.rx8club.favorites.FavoriteFactory;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.html.VBForumFactory;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.user.UserProfile;
import com.normalexception.app.rx8club.view.category.CategoryModel;
import com.normalexception.app.rx8club.view.category.CategoryViewArrayAdapter;
import com.normalexception.app.rx8club.view.category.SubCategoryModel;

/**
 * Main activity for the application.  This is the main forum view that 
 * is displayed after login has been completed.
 * 
 * Required Intent Parameters:
 * none
 */
public class HomeFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());

	private ArrayList<CategoryModel> mainList;
	private CategoryViewArrayAdapter cva;

	// The Forum's Main Page Has The Following Column
	@SuppressWarnings("unused")
	private static final int ROTOR_ICON = 0,
	FORUM_NAME = 1,
	LAST_POST  = 2,
	THREADS_CNT= 3,
	POSTS_CNT  = 4;

	private ViewListCache<CategoryModel> hcache = null;
	private UserProfileCache upcache = null;

	private ProgressDialog loadingDialog;
	private ProgressDialog profileDialog = null;
	
	private AsyncTask<Void,String,Void> updaterTask;
	private AsyncTask<Void,String,Void> profileTask;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_content, container, false);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {

		try {
			Log.v(TAG, "Application Started");

			MainApplication.setState(AppState.State.MAIN_PAGE, this);

			view.findViewById(R.id.mainlisttitle).setVisibility(View.GONE);

			if(TimeoutFactory.getInstance().checkTimeout(this)) {
				// Read in the favorites if they exist
				FavoriteFactory.getInstance();

				// Now that we introduced a cache, we are going to first
				// check to see if the cache is valid, if so, use it 
				// so that we dont waste bandwidth
				if(!LoginFactory.getInstance().isGuestMode()) {
					hcache = new ViewListCache<CategoryModel>(
							this.getActivity(), getString(R.string.file_homecache));
					if(hcache.isCacheExpired()) {
						Log.d(TAG, "Cache Expired, Creating Main");
						if(savedInstanceState == null || 
								(cva == null || cva.getCount() == 0)) {
							constructView();
						}
					} else {
						mainList = (ArrayList<CategoryModel>) hcache.getCachedContents();
						
						// Just incase our cached content is corrupt
						if(mainList != null)
							updateList();
						else {
							Log.w(TAG, "Cached CategoryView was corrupt, constructing new");
							constructView();
						}
					}
				} else {
					constructView();
				}

				// Once we brought a cache into the mix, we now need a way to 
				// validate the user profile.  Unfortunately we can't place
				// the data in our cache, because we cache the main page
				// regardless of user / guest.  What we will do now, is cache
				// the user profile as well.
				if(LoginFactory.getInstance().isLoggedIn()) {
					String currentUser = UserProfile.getInstance().getUsername();

					upcache = new UserProfileCache(this.getActivity(), currentUser);
					UserProfile cachedProfile = upcache.getCachedContents();
					if(cachedProfile == null || 
							!cachedProfile.getUsername().equals(currentUser) ||
							cachedProfile.getUserId().equals("")) {
						Log.d(TAG, "User Cache Expired, Recreating");
						constructUserProfile(null);
					} else {
						UserProfile.getInstance().copy(upcache.getCachedContents());
					}

					Log.d(TAG, String.format("%s(%s) succesfully logged in.", 
							UserProfile.getInstance().getUsername(), 
							UserProfile.getInstance().getUserId()));
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Main Activity! " + e.getMessage(), e);
		}
	}

	/**
	 * User profile will be read as an async task after the main
	 * activity has started.  This doesn't always run, only when 
	 * the cache is either non-existant, or expired
	 * @param doc	The current page
	 */
	private void constructUserProfile(final Document doc) {
		profileTask = new AsyncTask<Void,String,Void>() {
			@Override
			protected void onPreExecute() {
				profileDialog = 
						ProgressDialog.show(getActivity(), 
								getString(R.string.loading), 
								"Validating Profile", true);
			}
			@Override
			protected Void doInBackground(Void... params) {		
				if(LoginFactory.getInstance().isLoggedIn()) {
					Document localDoc = doc;
					if(localDoc == null)
						localDoc = VBForumFactory.getInstance().get(
								getActivity(), VBForumFactory.getRootAddress());
					if(localDoc != null) {
						Elements userElement = 
								localDoc.select("a[href^=http://www.rx8club.com/members/" + 
										UserProfile.getInstance().getHtmlUsername()+ "]");
						String un = userElement.attr("href");

						UserProfile.getInstance().setUserProfileLink(un);

						try {
							// Try and scrap the uid from the href
							UserProfile.getInstance().setUserId(
									un.substring(un.lastIndexOf("-") + 1, un.lastIndexOf("/")));
						} catch (Exception e) {
							Log.e(TAG, "Error Parsing User ID", e);
						}
					}
				}
				return null;
			}	
			@Override
			protected void onPostExecute(Void result) {
				try {
					profileDialog.dismiss();
					profileDialog = null;
				} catch (Exception e) {
					Log.w(TAG, e.getMessage());
				}
				upcache.cacheContents(UserProfile.getInstance());
			}
		};
		profileTask.execute();
	}

	/**
	 * Start the application activity
	 */
	private void constructView() {  
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
						ProgressDialog.show(getActivity(), 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
			}
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Log.v(TAG, "Updater Thread Started");

					publishProgress(getString(R.string.asyncDialogGrabForumContents));
					doc 	 = getForum();

					mainList = new ArrayList<CategoryModel>();

					publishProgress(getString(R.string.asyncDialogCategorySort));
					getCategories(doc);

					publishProgress(getString(R.string.asyncDialogUpdateCache));
					if(hcache != null)
						hcache.cacheContents(mainList);
				} catch(Exception e) {
					getActivity().runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(getActivity(), 
									R.string.connectionError, 
									Toast.LENGTH_SHORT).show();
						}
					});
					Log.e(TAG, e.getMessage(), e);
				}
				return null;
			}
			@Override
			protected void onProgressUpdate(String...progress) {
				if(loadingDialog != null)
					loadingDialog.setMessage(progress[0]);
			}			
			@Override
			protected void onPostExecute(Void result) {
				updateList();

				try {
					loadingDialog.dismiss();
					loadingDialog = null;
				} catch (Exception e) {
					Log.w(TAG, e.getMessage());
				}

				if(LoginFactory.getInstance().isLoggedIn()) {
					// Construct a new user profile
					//constructUserProfile(doc);
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
		final Fragment _frag = this;
		getActivity().runOnUiThread(new Runnable() {
			public void run() {	        
				final ListView lv = (ListView)getView().findViewById(R.id.mainlistview);
				cva = new CategoryViewArrayAdapter(_frag, R.layout.view_category, mainList);
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

			CategoryModel cv = new CategoryModel();
			cv.setTitle(categories.get(catIndex++).text());
			mainList.add(cv);

			Elements forums = category.select("tr[align=center]");
			for(Element forum : forums) {
				cv = new CategoryModel();
				List<SubCategoryModel> scvList = cv.getSubCategories();

				// Each forum object should have 5 columns
				Elements columns = forum.select("tr[align=center] > td");
				try {
					if(columns.size() != 5) continue;

					String forum_name = columns.get(HomeFragment.FORUM_NAME).select("strong").text();
					String forum_href = columns.get(HomeFragment.FORUM_NAME).select("a").attr("href");
					String forum_desc = "";
					try {
						forum_desc = 
								columns.get(HomeFragment.FORUM_NAME).select("div[class=smallfont]").first().text();
					} catch (NullPointerException npe) { /* Some might not have a desc */ }
					String threads    = columns.get(HomeFragment.THREADS_CNT).text();
					String posts      = columns.get(HomeFragment.POSTS_CNT).text();

					// Lets grab each subcategory
					Elements subCats  = columns.select("tbody a");
					for(Element subCat : subCats) {
						SubCategoryModel scv = new SubCategoryModel();
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
				} catch (Exception e) {
					Log.e(TAG, "Error Parsing Forum", e);
				}
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
			output = ff.getForumFrontpage(getActivity(), lf);
		} catch (IOException ioe) {
			Log.e(TAG, "Error Grabbing Forum Frontpage: " + ioe.getMessage(), ioe);
		}		

		if(output == null) {
			Toast.makeText(getActivity(), 
					R.string.connectionError, 
					Toast.LENGTH_LONG).show();
			FragmentUtils.returnToLoginPage(getActivity(), FragmentUtils.LogoutReason.ERROR);
			return null;
		} else {
			return Jsoup.parse(output);
		}
	}
}
