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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;

public class UserCpActivity extends ForumBaseActivity {

	private String token, customTitle, homepageurl, biography, location, interests, occupation;
	
	public static String TAG = "UserCpActivity";
	
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
        setContentView(R.layout.activity_user_cp);

       	constructView();
    }
    
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
        
        updaterThread = new Thread("Updater") {
        	public void run() {
        		try {
        			Document doc = 
    						VBForumFactory.getInstance().get(WebUrls.editProfile);
        			token = 
        					HtmlFormUtils.getInputElementValue(doc, "securitytoken");
        			
        			Elements fieldSets =
        					doc.select("fieldset[class=fieldset]");
        			
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
        		} catch (Exception e) {
        			Log.e(TAG, e.getMessage());
        		} finally {
        			loadingDialog.dismiss();
        		}
        	}
        };
        updaterThread.start();

    }
    
    private void updateView() {
    	runOnUiThread(new Runnable() {
    		public void run() {
    			((TextView)findViewById(R.id.customTitle)).setText(customTitle);
    			((TextView)findViewById(R.id.homepageUrl)).setText(homepageurl);
    			((TextView)findViewById(R.id.biography)).setText(biography);
    			((TextView)findViewById(R.id.location)).setText(location);
    			((TextView)findViewById(R.id.interests)).setText(interests);
    			((TextView)findViewById(R.id.occupation)).setText(occupation);
    		}
    	});
    }

    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {	
	}
}
