package com.normalexception.forum.rx8club.fragment;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.TimeoutFactory;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.task.ProfileTask;

public class UserCpFragment extends Fragment {

	private String token, customTitle, homepageurl, biography, location, interests, occupation;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private ProgressDialog loadingDialog;
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {	
		try {
			outState.putSerializable("token", token);
			outState.putSerializable("customTitle", customTitle); 
			outState.putSerializable("homepageurl", homepageurl);
			outState.putSerializable("biography", biography);
			outState.putSerializable("location", location);
			outState.putSerializable("interests", interests);
			outState.putSerializable("occupation", occupation);
		} catch (Exception e) {
			Log.e(TAG, "Error Serializing: " + e.getMessage(), e);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		try {
			if(savedInstanceState != null) {
				token =
						(String)savedInstanceState.getSerializable("token");
				customTitle = 
						(String)savedInstanceState.getSerializable("customTitle");
				homepageurl =
						(String)savedInstanceState.getSerializable("homepageurl");
				biography =
						(String)savedInstanceState.getSerializable("biography");
				location =
						(String)savedInstanceState.getSerializable("location");
				interests =
						(String)savedInstanceState.getSerializable("interests");
				occupation =
						(String)savedInstanceState.getSerializable("occupation");
			}
		} catch (Exception e) {
			Log.e(TAG, "Error UnSerializing: " + e.getMessage(), e);
		}
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.activity_user_cp, container, false);    	
        return rootView;
    }
	
	public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.USER_CP, this);

        view.findViewById(R.id.submitUserCpButton).setOnClickListener(new UserCpClickListener());
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
        	constructView();
        }
    }
    
    /**
     * Construct the main view for our user profile
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
        		try {
        			Document doc = 
    						VBForumFactory.getInstance().get(getActivity(), WebUrls.editProfile);
        			
        			if(doc != null) {
	        			token = 
	        					HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
	        			
	        			Elements fieldSets =
	        					doc.select("fieldset[class=fieldset]");
	        			
	        			publishProgress(getString(R.string.asyncDialogPopulating));
	        			for(Element fieldSet : fieldSets) {
	        				String legend = fieldSet.select("legend").text();
	        				if(legend.equals("Custom User Title")) {
	        					customTitle = fieldSet.select("strong").text();
	        					continue;
	        				} else if (legend.equals("Home Page URL")) {
	        					homepageurl = fieldSet.getElementById("tb_homepage").attr("value");
	        					continue;
	        				} else if (legend.equals("Biography")) {
	        					biography = fieldSet.getElementById("ctb_field1").attr("value");
	        					continue;
	        				} else if (legend.equals("Location")) {
	        					location = fieldSet.getElementById("ctb_field2").attr("value");
	        					continue;
	        				} else if (legend.equals("Interests")) {
	        					interests = fieldSet.getElementById("ctb_field3").attr("value");
	        					continue;
	        				} else if (legend.equals("Occupation")) {
	        					occupation = fieldSet.getElementById("ctb_field4").attr("value");
	        					continue;
	        				}
	        			}
	 			        
	        			updateView();
        			}
        		} catch (Exception e) {
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
    
    private void updateView() {
    	getActivity().runOnUiThread(new Runnable() {
    		public void run() {
    			((TextView)getView().findViewById(R.id.customTitle)).setText(customTitle);
    			((TextView)getView().findViewById(R.id.homepageUrl)).setText(homepageurl);
    			((TextView)getView().findViewById(R.id.biography)).setText(biography);
    			((TextView)getView().findViewById(R.id.location)).setText(location);
    			((TextView)getView().findViewById(R.id.interests)).setText(interests);
    			((TextView)getView().findViewById(R.id.occupation)).setText(occupation);
    		}
    	});
    }
    
    class UserCpClickListener implements OnClickListener {
	    /*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			Activity view = getActivity();
			switch(arg0.getId()) {
			case R.id.submitUserCpButton:
				customTitle = ((TextView)view.findViewById(R.id.customTitle)).getText().toString();
				homepageurl = ((TextView)view.findViewById(R.id.homepageUrl)).getText().toString();
				biography = ((TextView)view.findViewById(R.id.biography)).getText().toString();
				location = ((TextView)view.findViewById(R.id.location)).getText().toString();
				interests = ((TextView)view.findViewById(R.id.interests)).getText().toString();
				occupation = ((TextView)view.findViewById(R.id.occupation)).getText().toString();
				
				ProfileTask update = new ProfileTask(getActivity(), token, customTitle, homepageurl, 
	    				biography, location, interests, occupation);
				update.execute();
				break;
			}
	}
	}
}
