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

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.WebUrls;
import com.normalexception.app.rx8club.handler.AvatarLoader;
import com.normalexception.app.rx8club.html.VBForumFactory;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.user.UserProfile;
import com.normalexception.app.rx8club.view.profile.ProfileView;
import com.normalexception.app.rx8club.view.profile.ProfileViewArrayAdapter;

/**
 * Activity that sets up the users profile
 * 
 * Required Intent Parameters:
 * none
 */
public class ProfileFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private ProgressDialog loadingDialog;
	
	private ArrayList<ProfileView> stubs;	
	private ProfileViewArrayAdapter pva;
	private ListView lv;
	
	private AvatarLoader imageLoader;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.PROFILE, this);
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
	        Log.v(TAG, "Profile Activity Started");
	        
	        imageLoader = new AvatarLoader(getActivity());
	        
	        constructView();
        }
    }
    
    private void updateList() {
    	final Fragment _frag = this;
		getActivity().runOnUiThread(new Runnable() {
            public void run() {
		    	pva = new ProfileViewArrayAdapter(_frag, 0, stubs);
				lv.setAdapter(pva);
            }
    	});
	}
    
    /**
     * Construct the profile view
     */
    private void constructView() {        
        AsyncTask<Void,String,Void> updaterTask = new AsyncTask<Void,String,Void>() {
        	@Override
		    protected void onPreExecute() {
        		
		    	loadingDialog = 
						ProgressDialog.show(getActivity(), 
								getString(R.string.loading), 
								getString(R.string.pleaseWait), true);
		    }
        	@Override
			protected Void doInBackground(Void... params) {
        		final UserProfile upInstance = UserProfile.getInstance();
				Document doc = 
						VBForumFactory.getInstance().get(getActivity(), upInstance.getUserProfileLink());
				if(doc != null) {
					try {
						publishProgress(getString(R.string.asyncDialogGrabProfile));
						String id = upInstance.getUserProfileLink().substring(
								upInstance.getUserProfileLink().lastIndexOf("-") + 1,
								upInstance.getUserProfileLink().length() - 1);
						upInstance.setUserId(id);
						getUserInformation(doc);
						
						lv = (ListView)getView().findViewById(R.id.mainlistview);
						
						getActivity().runOnUiThread(new Runnable() {
				            public void run() {
				            	View v = getActivity().getLayoutInflater().
				            			inflate(R.layout.view_profile_header, null);
				            	//v.setOnClickListener(this);
				            	lv.addHeaderView(v);
	
				            	publishProgress(getString(R.string.asyncDialogPopulating));
				            	
		                    	// the dateline at the end of the file so that we aren't
		                        // creating multiple images for a user.  The image still
		                        // gets returned without a date
		                        String nodate_avatar = 
		                        		upInstance.getUserImageLink().indexOf("&dateline") == -1? 
		                        				upInstance.getUserImageLink() : 
		                        					upInstance.getUserImageLink().substring(0, 
		                        							upInstance.getUserImageLink().indexOf("&dateline"));
		                        
		                        if(!nodate_avatar.isEmpty()) {
		                        	ImageView avatar = ((ImageView)getView().findViewById(R.id.pr_image));
		                        	imageLoader.DisplayImage(nodate_avatar, avatar);
		                        }
		                        
		                    	((TextView)getView().findViewById(R.id.pr_username)).setText(
		                    			upInstance.getUsername() + " (ID: " + upInstance.getUserId() + ")");
		                    	((TextView)getView().findViewById(R.id.pr_userTitle)).setText(upInstance.getUserTitle());
		                    	((TextView)getView().findViewById(R.id.pr_userPosts)).setText(upInstance.getUserPostCount());
		                    	((TextView)getView().findViewById(R.id.pr_userJoin)).setText(upInstance.getUserJoinDate());	          
		                    }
						});
					} catch (Exception e) {
						Log.e(TAG, "Error Grabbing Profile Data", e);
					}
					
					updateList();				
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
				try {
					loadingDialog.dismiss();
					loadingDialog = null;
				} catch (Exception e) {
					Log.w(TAG, e.getMessage());
				}
			}
        };
        updaterTask.execute();
    }
    
    /**
     * Get the user information from the users profile
     * @param doc	The page document
     */
    private void getUserInformation(Document doc) {
    	final UserProfile upInstance = UserProfile.getInstance();
    	stubs = new ArrayList<ProfileView>();
    	
    	// Title
    	Elements userInfo = doc.select("div[id=main_userinfo]");
    	Elements title = userInfo.select("h2");
    	upInstance.setUserTitle(title.text());
    	
    	// Posts
    	Elements statisticInfo = doc.select("fieldset[class=statistics_group]");
    	Elements post = statisticInfo.select("li");
    	
    	// Profile Pic
    	Elements profilePicInfo = doc.select("td[id=profilepic_cell] > img");
 
    	// Grab image, trap
    	try {
    		upInstance.setUserImageLink(profilePicInfo.attr("src"));
    	} catch (Exception e) { }
    	
    	// Grab Post count, trap exception
    	try {
    		upInstance.setUserPostCount(post.get(0).text() + " / " + post.get(1).text().split(" ",4)[3] + " per day");
    	} catch (Exception e) {
    		upInstance.setUserPostCount("Error Getting Post Count");
    	}
    	
    	// Grab Join Date, trap exception
    	try {
    		upInstance.setUserJoinDate(post.get(13).text());
    	} catch (Exception e) {
    		upInstance.setUserJoinDate("Error Getting Join Date");
    	}
    	
    	// Threads
    	String link = WebUrls.userUrl + upInstance.getUserId();
    	doc = VBForumFactory.getInstance().get(getActivity(), link);
    	if(doc != null) {
	    	Elements threadlist = doc.select("table[id^=post]");
	    	for(Element threadl : threadlist) {
	    		ProfileView stub = new ProfileView();
	    		Elements divs = threadl.getElementsByTag("div");
	    		Elements div = divs.get(1).getElementsByTag("a");
	    		stub.setLink(div.attr("href"));
	    		stub.setName(div.text());
	    		
	    		div = divs.get(5).getElementsByTag("a");
	    		stub.setText(div.text());
	    		stubs.add(stub);
	    	}
    	}
    }
}
