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
import java.util.Calendar;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.TimeoutFactory;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;
import com.normalexception.forum.rx8club.html.VBForumFactory;
import com.normalexception.forum.rx8club.task.DeletePmTask;
import com.normalexception.forum.rx8club.view.pm.PMView;
import com.normalexception.forum.rx8club.view.pm.PMViewArrayAdapter;

public class PrivateMessageInboxFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());

	private String token;
	
	private ArrayList<PMView> pmlist;
	private PMViewArrayAdapter pmva;
	
	private ListView lv;
	
	public static final String showOutboundExtra = "showOutbound";
	private boolean showOutbound = false;
	
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
    	//super.setState(AppState.State.PMINBOX, this.getIntent());
        
        if(TimeoutFactory.getInstance().checkTimeout(this)) {
	        pmlist = new ArrayList<PMView>();
	        lv = (ListView)view.findViewById(R.id.mainlistview);
	        
	        ViewGroup header = 
	        		(ViewGroup)getActivity()
	        		.getLayoutInflater()
	        		.inflate(R.layout.view_inbox_header, lv, false);
	        
	        InboxHeaderListener ihl = new InboxHeaderListener();
	        View v = null;
	        for(int i = 0; i < header.getChildCount(); i++) {
	            v = header.getChildAt(i);
	            if(v instanceof Button) v.setOnClickListener(ihl);
	        }
	        lv.addHeaderView(header);
	        
	        Log.v(TAG, "PM Activity Started");
	        
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
		    	pmva = new PMViewArrayAdapter(getActivity(), R.layout.view_pm, pmlist);
				lv.setAdapter(pmva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	PMView pm = (PMView) parent.getItemAtPosition(position);
		            	//Intent intent = 
						//		new Intent(PrivateMessageInboxActivity.this, 
						//				PrivateMessageViewActivity.class);
						//intent.putExtra("link", pm.getLink());
						//startActivity(intent);
		            	Bundle args = new Bundle();
		            	args.putString("link", pm.getLink());
		            	
		            	// Create new fragment and transaction
						Fragment newFragment = new PrivateMessageViewFragment();
						newFragment.setArguments(args);
						FragmentTransaction transaction = getFragmentManager().beginTransaction();

						// Replace whatever is in the fragment_container view with this fragment,
						// and add the transaction to the back stack
						transaction.add(R.id.content_frame, newFragment);
						transaction.addToBackStack(null);

						// Commit the transaction
						transaction.commit();
		            }
		        });
				registerForContextMenu(lv);
            }
    	});
	}
     
    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo) {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	AdapterContextMenuInfo info = 
				(AdapterContextMenuInfo) menuInfo;
        int position = info.position;
        PMView pmv = (PMView)lv.getItemAtPosition(position);
		menu.setHeaderTitle(pmv.getTitle());
		if(!this.showOutbound)
			menu.add(0, position, 0, R.string.reply);
		menu.add(0, position, 0, R.string.delete);
	}

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
	public boolean onContextItemSelected(final MenuItem item) {
    	final PMView pmv = 
    			(PMView)lv.getItemAtPosition(item.getItemId());
       	if(item.getTitle().equals(getString(R.string.reply))) {
       		replyPm(pmv);
       	}
    	else if(item.getTitle().equals(getString(R.string.delete))) {
   			// Lets make sure the user didn't accidentally click this
			DialogInterface.OnClickListener dialogClickListener = 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
				    	case DialogInterface.BUTTON_POSITIVE:
				    		deletePm(pmv);
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
    	}
    	else {
    		return false;
    	}
       	
       	return true;
	}
    
    /**
     * Handler for replying to a private message
     * @param arg0	The view associated with the private message
     */
    private void replyPm(PMView pmv) {
    	Log.v(TAG, "Reply PM Clicked");
		final String link = pmv.getLink();
		if(link != null && !link.equals("")) {
			Log.v(TAG, "User Clicked: " + link);
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
					//Intent intent = 
					//		new Intent(PrivateMessageInboxActivity.this, 
					//				PrivateMessageViewActivity.class);
					//intent.putExtra("link", link);
					//startActivity(intent);
					
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
    }
    
    /**
     * Handler for deleting a private message
     * @param arg0	The view associated with the private message
     */
    private void deletePm(PMView pmv) {
    	Log.v(TAG, "Delete PM Clicked");
    	final String link = pmv.getLink(); //tv.getText().toString());
    	
    	if(link != null && !link.equals("")) {
    		final String id = link.substring(link.lastIndexOf("id=") + 3);
			Log.v(TAG, "User Clicked: " + id);

			DeletePmTask dpm = new DeletePmTask(getActivity(), token, id, showOutbound);
			dpm.execute();
		}
    }
    
    /**
     * Convert an integer month to a string month
     * @param m	The integer month to convert
     * @return	A string representation of a month
     */
    private String getMonthForInt(int m) {
    	Calendar cal = Calendar.getInstance();
    	
    	// For API >9
    	//cal.set(Calendar.MONTH, m - 1);
    	
    	// For API 9
    	cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
    	
    	return String.format(Locale.US,"%tB",cal);
    }
    
    /**
     * Construct view by grabbing all private messages.  This is only done
     * if the view is called for the first time.  If there was a savedinstance
     * of the view then this is not called
     */
    private void constructView() {
    	//this.showOutbound = 
    	//		this.getIntent().getBooleanExtra(showOutboundExtra, false);
    	
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
 				Document doc = 
 						VBForumFactory.getInstance().get(getActivity(), 
 								showOutbound? WebUrls.pmSentUrl : WebUrls.pmInboxUrl);
 				
 				if( doc != null) {
	 				token = HtmlFormUtils.getInputElementValueByName(doc, "securitytoken");
	 				String current_month = getMonthForInt(0);
	 				Elements collapse = doc.select(
	 						showOutbound? "tbody[id^=collapseobj_pmf-1]" : 
	 							"tbody[id^=collapseobj_pmf0]");
	 				
	 				publishProgress(getString(R.string.asyncDialogGrabPMs));
	 				for(Element coll : collapse) {
	 					Elements trs = coll.select("tr");
	 					for(Element tr : trs) {
	 						Elements alt1s = tr.getElementsByClass("alt1Active");
	 						for(Element alt1 : alt1s) {
	 							
	 							Elements divs = alt1.select("div");
	 							
	 							// First grab our link
	 							Elements linkElement = divs.get(0).select("a[rel=nofollow]");
	 							String pmLink = linkElement.attr("href");
	 							
	 							// There should be two divs here with text in it
	 							// the first is 'MM-DD-YYYY Subject'
	 							String dateSubject = divs.get(0).text();
	 							String[] dateSubjectSplit = dateSubject.split(" ", 2);
	 							
	 							// The second is HH:MM AMPM User
	 							String timeTimeUser = divs.get(1).text();
	 							String[] timeTimeUserSplit = timeTimeUser.split(" ", 3);
	 							
	 							// Create new pm
	 							PMView pm = new PMView();
	 							pm.setDate(dateSubjectSplit[0]);
	 							
	 							// Check the month before we go further
	 							String this_month = getMonthForInt(
	    								Integer.parseInt(pm.getDate().split("-")[0]));
	 							if(!current_month.equals(this_month)) {
	 								current_month = this_month;
	 								PMView pm_m = new PMView();
	 								pm_m.setTitle(String.format("%s - %s", 
	 										this_month, showOutbound? "Sent Items" : "Inbox"));
	 								pmlist.add(pm_m);
	 							}
	 							
	 							pm.setTime(timeTimeUserSplit[0] + timeTimeUserSplit[1]);
	 							pm.setTitle(dateSubjectSplit[1]);
	 							pm.setUser(timeTimeUserSplit[2]);
	 							pm.setLink(pmLink); 
	 							pm.setToken(token);
	 							
	 							Log.v(TAG, "Adding PM From: " + pm.getUser());
	 							pmlist.add(pm);
	 						}
	 					}
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
				loadingDialog.dismiss();
			}
        };
        updaterTask.execute();
    }
    
    class InboxHeaderListener implements OnClickListener {
	    /*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {		
			Fragment _fragment = null;
			Bundle args = new Bundle();
			switch(arg0.getId()) {	
			case R.id.inboxButtonNewPm:
				Log.v(TAG, "New PM Clicked");
				_fragment = new NewPrivateMessageFragment();
				break;
				
			case R.id.inboxButtonInbox:
				Log.v(TAG, "New PM Clicked");
				_fragment = new PrivateMessageInboxFragment();
				args.putBoolean(showOutboundExtra, false);
				break;
				
			case R.id.inboxButtonSent:
				Log.v(TAG, "New PM Clicked");
				_fragment = new PrivateMessageInboxFragment();
				args.putBoolean(showOutboundExtra, true);
				break;
			}
			
			_fragment.setArguments(args);
			FragmentTransaction transaction = getFragmentManager().beginTransaction();

			// Replace whatever is in the fragment_container view with this fragment,
			// and add the transaction to the back stack
			transaction.add(R.id.content_frame, _fragment);
			transaction.addToBackStack(null);

			// Commit the transaction
			transaction.commit();
		}
    }
}
