package com.normalexception.forum.rx8club.fragment.thread;

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

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.task.NewThreadTask;
import com.normalexception.forum.rx8club.view.threaditem.ThreadItemView;
import com.normalexception.forum.rx8club.view.threaditem.ThreadItemViewArrayAdapter;

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
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onSaveInstanceState(android.os.Bundle)
	 */
	public void onSaveInstanceState(Bundle outState) {	
		// First save the subject and post
		subject = ((TextView)getView().findViewById(R.id.postSubject)).getText().toString();
		post = ((TextView)getView().findViewById(R.id.postPost)).getText().toString();
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onRestoreInstanceState(android.os.Bundle)
	 */
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		if(savedInstanceState != null) {
			subject 	= savedInstanceState.getString("subject");
			post 		= savedInstanceState.getString("post");
		}
	}

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
				    	pva = new ThreadItemViewArrayAdapter(getActivity(), R.layout.view_newthread, tlist);
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
    }
    
    class NewThreadListener implements OnClickListener {
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
					NewThreadTask ntt = new NewThreadTask(getActivity(), forumId, s, 
							 token, f, posthash, subject, post, source);
					ntt.execute();
				break;
			}
		}
    }
}
