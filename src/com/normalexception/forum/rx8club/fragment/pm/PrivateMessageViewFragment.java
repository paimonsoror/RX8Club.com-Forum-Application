package com.normalexception.forum.rx8club.fragment.pm;

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
import java.util.Iterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.TimeoutFactory;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.task.DeletePmTask;
import com.normalexception.forum.rx8club.task.PmTask;
import com.normalexception.forum.rx8club.user.UserProfile;
import com.normalexception.forum.rx8club.utils.Utils;
import com.normalexception.forum.rx8club.view.pmpost.PMPostView;
import com.normalexception.forum.rx8club.view.pmpost.PMPostViewArrayAdapter;

public class PrivateMessageViewFragment extends Fragment {

	private Logger TAG = LogManager.getLogger(this.getClass());
	
	private String postUser = null;
	private String securityToken = null;
	private String pmid = null;
	private String title = null;
	
	private ArrayList<PMPostView> pmlist;
	private PMPostViewArrayAdapter pmva;
	
	private ListView lv;
	
	private ProgressDialog loadingDialog;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);    	
        return rootView;
    }
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.PMVIEW, this);
        
        //setContentView(R.layout.activity_basiclist);
        
        Log.v(TAG, "PM View Activity Started");
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
	        pmlist = new ArrayList<PMPostView>();
	        lv = (ListView)getView().findViewById(R.id.mainlistview);
	        
	        View v = getActivity().getLayoutInflater().inflate(R.layout.view_pmitem_footer, null);
	    	v.setOnClickListener(new PrivateMessageViewListener());
	    	lv.addFooterView(v);
	        
	        if(savedInstanceState == null || 
	        		(pmva == null || pmva.getCount() == 0))
	        	constructView();
	        else {
	        	updateList();
	        }
        }
    }
    
    private void updateList() {
    	getActivity().runOnUiThread(new Runnable() {
            public void run() {
		    	pmva = new PMPostViewArrayAdapter(getActivity(), R.layout.view_newreply, pmlist);
				lv.setAdapter(pmva);
            }
    	});
	}
    
    /**
     * Construct the view elements
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
				String link = 
		        		getArguments().getString("link");
				Document doc = 
						VBForumFactory.getInstance().get(getActivity(), 
								VBForumFactory.getRootAddress() + "/" + link);
				
				if(doc != null) {
					securityToken =
							HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
					
					pmid =
							HtmlFormUtils.getInputElementValueByName(doc, "pmid");
					
					title =
							HtmlFormUtils.getInputElementValueByName(doc, "title");
					
					Elements userPm = doc.select("table[id^=post]");
					publishProgress(getString(R.string.asyncDialogLoadingPM));
					
					// User Control Panel
					Elements userCp = userPm.select("td[class=alt2]");
					Elements userDetail = userCp.select("div[class=smallfont]");
					Elements userSubDetail = userDetail.last().select("div"); 
					Elements userAvatar = userDetail.select("img[alt$=Avatar]");
					Elements postMessage = doc.select("div[id=post_message_]");

					
					PMPostView pv = new PMPostView();
					pv.setUserName(userCp.select("div[id^=postmenu]").text());
					pv.setIsLoggedInUser(
							LoginFactory.getInstance().isLoggedIn()?
									UserProfile.getInstance().getUsername().equals(
											pv.getUserName()) : false);	
					pv.setUserTitle(userDetail.first().text());
					pv.setUserImageUrl(Utils.resolveUrl(userAvatar.attr("src")));
					pv.setPostDate(userPm.select("td[class=thead]").first().text());

					// userSubDetail
					// 0 - full container , full container
					// 1 - Trader Score   , Trader Score
					// 2 - Join Date      , Join Date
					// 3 - Post Count     , Location
					// 4 - Blank          , Post Count
					// 5 -                , Blank || Social
					//
					Iterator<Element> itr = userSubDetail.listIterator();
					while(itr.hasNext()) {
						String txt = itr.next().text();
						if(txt.contains("Location:"))
							pv.setUserLocation(txt);
						else if (txt.contains("Posts:"))
							pv.setUserPostCount(txt);
						else if (txt.contains("Join Date:"))
							pv.setJoinDate(txt);
					}

					// User Post Content
					pv.setUserPost(formatUserPost(postMessage));
					
					pmlist.add(pv);
					
					TextView comment = (TextView)getView().findViewById(R.id.pmitem_comment);
					Elements textarea = doc.select("textarea[id=vB_Editor_QR_textarea]");
					if(textarea != null) {				
						comment.setText(textarea.first().text());
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
	 * Format the user post by removing the vb style quotes and the 
	 * duplicate youtube links
	 * @param innerPost	The element that contains the inner post
	 * @return			The formatted string
	 */
	private String formatUserPost(Elements innerPost) {
	
		// Remove the duplicate youtube links (this is caused by a plugin on 
		// the forum that embeds youtube videos automatically)
		for(Element embedded : innerPost.select("div[id^=ame_doshow_post_]"))
			embedded.remove();
		
		// Remove the vbulletin quotes
		String upost = Utils.reformatQuotes(innerPost.html());
		
		return upost;
	}
	
	class PrivateMessageViewListener implements OnClickListener {
    
	    /*
	   	 * (non-Javadoc)
	   	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	   	 */
	   	@Override
	   	public void onClick(View arg0) {
	   		
	   		switch(arg0.getId()) {	
	   		case R.id.pmitem_submit:
	   			Log.v(TAG, "PM Submit Clicked");
	   			String toPost = 
						((TextView)getView().findViewById(R.id.pmitem_comment)).getText().toString();
				PmTask sTask = 
						new PmTask(getActivity(), securityToken, "Re: " + title, 
								toPost, postUser, pmid);
				sTask.execute();
	   			break;
	   		case R.id.pmitem_delete:
	   			// Lets make sure the user didn't accidentally click this
				DialogInterface.OnClickListener dialogClickListener = 
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which){
					    	case DialogInterface.BUTTON_POSITIVE:
					    		DeletePmTask dpm = new DeletePmTask(getActivity(), securityToken, pmid, false);
								dpm.execute();
				   				break;
				        }
				    }
				};
	    		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder
					.setMessage(R.string.dialogPmDeleteConfirm)
					.setPositiveButton(R.string.Yes, dialogClickListener)
				    .setNegativeButton(R.string.No, dialogClickListener)
				    .show();
				break;
	   		}
	   	}
	}
}
