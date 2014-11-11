package com.normalexception.app.rx8club.fragment.pm;

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

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.normalexception.app.rx8club.TimeoutFactory;
import com.normalexception.app.rx8club.WebUrls;
import com.normalexception.app.rx8club.html.HtmlFormUtils;
import com.normalexception.app.rx8club.html.VBForumFactory;
import com.normalexception.app.rx8club.state.AppState;
import com.normalexception.app.rx8club.task.PmTask;
import com.normalexception.app.rx8club.view.pmitem.PMItemView;
import com.normalexception.app.rx8club.view.pmitem.PMItemViewArrayAdapter;

public class NewPrivateMessageFragment extends Fragment {

	private Logger TAG = LogManager.getLogger(this.getClass());
	
	private String postUser = null;
	private String postText = null;
	private String securityToken = null;
	private String recipients = null;
	private String pmid = null;
	private String title = null;
	
	private ListView lv;
	
	private ArrayList<PMItemView> tlist;
	private PMItemViewArrayAdapter pva;
	
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
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.NEW_PM, this);
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
	        lv      = (ListView)getView().findViewById(R.id.mainlistview);
	        lv.setDescendantFocusability(ListView.FOCUS_AFTER_DESCENDANTS);
	        lv.setScrollContainer(false);
	        tlist   = new ArrayList<PMItemView>();
	    
		    if(savedInstanceState == null)
		    	constructView();
        }
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
				//String link = 
		        //		(String) getIntent().getStringExtra("link");
				Document doc = 
						VBForumFactory.getInstance().get(getActivity(), WebUrls.newPmAddress);
				
				if(doc != null) {
					securityToken =
							HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
					
					pmid =
							HtmlFormUtils.getInputElementValueByName(doc, "pmid");
					
					postUser = 
							getArguments().getString("user");    	
					
					PMItemView pm = new PMItemView();
					if(validateInputs(postUser))
						pm.setName(postUser);
					tlist.add(pm);
			    	
			    	getActivity().runOnUiThread(new Runnable() {
			            public void run() {
					    	pva = new PMItemViewArrayAdapter(getActivity(), R.layout.view_newpm, tlist);
							lv.setAdapter(pva);	
			            }
			    	});
				}
		    	
		    	return null;
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
	
	class NewPrivateMessageListener implements OnClickListener {
		 /*
	   	 * (non-Javadoc)
	   	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	   	 */
	   	@Override
	   	public void onClick(View arg0) {   		
	   		recipients = ((TextView)getView().findViewById(R.id.pmRecipientsText)).getText().toString();
	   		postText = ((TextView)getView().findViewById(R.id.pmMessageText)).getText().toString();
	   		title    = ((TextView)getView().findViewById(R.id.pmSubjectText)).getText().toString();
	   		
	   		if(validateInputs(recipients, postText, title)) {
		   		switch(arg0.getId()) {	
		   		case R.id.newPmButton:
		   			Log.v(TAG, "PM Submit Clicked");
					PmTask sTask = 
							new PmTask(getActivity(), securityToken, 
									title, postText, recipients, pmid);
					sTask.execute();
		   			break;
		   		}
	   		} else {
	   			Toast.makeText(getActivity(), "Not Valid", Toast.LENGTH_SHORT).show();
	   		}
	   	}
	}
   	
   	/**
   	 * Make sure that the inputs have content and are not null
   	 * @param params	A set of parameters to check
   	 * @return			True if the params have contents
   	 */
   	private boolean validateInputs(String... params) {
   		boolean valid = true;
   		for(String param : params) {
   			valid &= ((param != null) && (!param.equals("")));
   		}
   		return valid;
   	}
}
