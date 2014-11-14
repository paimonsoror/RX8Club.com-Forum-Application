package com.normalexception.app.rx8club.fragment.thread;

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

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.html.HtmlFormUtils;
import com.normalexception.app.rx8club.html.VBForumFactory;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.task.NewThreadTask;
import com.normalexception.app.rx8club.view.threaditem.ThreadItemView;
import com.normalexception.app.rx8club.view.threaditem.ThreadItemViewArrayAdapter;

/**
 * Activity that is loaded when the user is creating a new thread.
 * 
 * Required Intent Parameters:
 * forumid - The forum id number as a string 
 * link - 	Link to the forums new thread handler
 * source - The address of the source category
 */
public class NewThreadFragment extends Fragment {
	
	private String forumId = "", link = "", 
			       s, token, f, posthash, 
			       subject, post, source;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private ListView lv;
	
	private ArrayList<ThreadItemView> tlist;
	private ThreadItemViewArrayAdapter pva;
	
	private ProgressDialog loadingDialog;

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
    	try {
	        super.onCreate(savedInstanceState);
	        MainApplication.setState(AppState.State.NEW_THREAD, this);
	        
	        forumId = getArguments().getString("forumid");
	        link    = getArguments().getString("link");
	        source  = getArguments().getString("source");
	        lv      = (ListView)view.findViewById(R.id.mainlistview);
	        lv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
	        lv.setScrollContainer(false);
	        tlist   = new ArrayList<ThreadItemView>();
	        
        	if(savedInstanceState == null)
        		constructView();
	        
    	} catch (Exception e) {
    		Log.e(TAG, "Error In New Thread Activity " + e.getMessage(), e);
    	}
    }
    
    /**
     * Construct the view
     */
    private void constructView() { 
    	final Fragment _src = this;
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
		    	if(link != null) {
			    	Document doc = VBForumFactory.getInstance().get(getActivity(), link);
			    	if(doc != null) {
				    	s 			= HtmlFormUtils.getInputElementValueByName(doc, "s");
				    	token 		= HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
				    	f 			= HtmlFormUtils.getInputElementValueByName(doc, "f");
				    	posthash 	= HtmlFormUtils.getInputElementValueByName(doc, "posthash");
				    	
				    	tlist.add(new ThreadItemView());
				    	
				    	getActivity().runOnUiThread(new Runnable() {
				            public void run() {
						    	pva = new ThreadItemViewArrayAdapter(_src, 
						    			R.layout.view_newthread, tlist, new NewThreadListener(_src));
								lv.setAdapter(pva);		        
				            }
				    	});
			    	}
		    	} else {
		    		Log.e(TAG, "Link Was NULL!", null);
		    		Toast.makeText(getActivity(), "Sorry! Error Trying To Create New Thread!", 
		    				Toast.LENGTH_SHORT).show();
		    		//this.finish();
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
    
    class NewThreadListener implements OnClickListener {
    	private Fragment src_;
    	
    	public NewThreadListener(Fragment src) {
    		this.src_ = src;
    	}
    	
	    /*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			// First save the subject and post
			subject = ((TextView)getView().findViewById(R.id.postSubject)).getText().toString();
			post = ((TextView)getView().findViewById(R.id.postPost)).getText().toString();
					
			switch(arg0.getId()) {
				case R.id.newThreadButton:
					NewThreadTask ntt = new NewThreadTask(src_, forumId, s, 
							 token, f, posthash, subject, post, source);
					ntt.execute();
				break;
			}
		}
    }
}
