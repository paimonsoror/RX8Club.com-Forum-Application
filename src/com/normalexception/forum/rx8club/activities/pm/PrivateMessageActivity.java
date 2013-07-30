package com.normalexception.forum.rx8club.activities.pm;

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

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.WebUrls;
import com.normalexception.forum.rx8club.activities.ForumBaseActivity;
import com.normalexception.forum.rx8club.utils.HtmlFormUtils;
import com.normalexception.forum.rx8club.utils.VBForumFactory;
import com.normalexception.forum.rx8club.view.pm.PMView;
import com.normalexception.forum.rx8club.view.pm.PMViewArrayAdapter;

public class PrivateMessageActivity extends ForumBaseActivity implements OnClickListener {

	private static String TAG = "PrivateMessageActivity";

	private String token;
	
	private ArrayList<PMView> pmlist;
	private PMViewArrayAdapter pmva;
	
	private ListView lv;
	
	private static final int NEW_PM = 1;
	
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        super.setTitle("RX8Club.com Forums");
        setContentView(R.layout.activity_basiclist);
        
        pmlist = new ArrayList<PMView>();
        lv = (ListView)findViewById(R.id.mainlistview);
        
        Button bv = new Button(this);
        bv.setId(NEW_PM);
        bv.setOnClickListener(this);
        bv.setText("New PM");
        bv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
        lv.addHeaderView(bv);
        
        Log.v(TAG, "PM Activity Started");
        
        if(savedInstanceState == null)
        	constructView();
        else {
        	updateList();
        }
    }
    
    private void updateList() {
		final Activity a = this;
    	runOnUiThread(new Runnable() {
            public void run() {
		    	pmva = new PMViewArrayAdapter(a, R.layout.view_pm, pmlist);
				lv.setAdapter(pmva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	PMView pm = (PMView) parent.getItemAtPosition(position);
		            	Intent intent = 
								new Intent(PrivateMessageActivity.this, 
										PrivateMessageViewActivity.class);
						intent.putExtra("link", pm.getLink());
						startActivity(intent);
		            }
		        });
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
		menu.setHeaderTitle(((TextView)v).getText());
		menu.add(0, v.getId(), 0, "Reply");
		menu.add(0, v.getId(), 0, "Delete");
	}

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
     */
    @Override
	public boolean onContextItemSelected(final MenuItem item) {
       	if(item.getTitle()=="Reply") {
       		View vw = findViewById(item.getItemId());
       		replyPm(vw);
       	}
    	else if(item.getTitle()=="Delete") {
   			// Lets make sure the user didn't accidentally click this
			DialogInterface.OnClickListener dialogClickListener = 
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which){
				    	case DialogInterface.BUTTON_POSITIVE:
				    		View vw = findViewById(item.getItemId());
				    		deletePm(vw);
			   				break;
			        }
			    }
			};
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder
				.setMessage("Are you sure you want to delete PM?")
				.setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener)
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
    private void replyPm(View arg0) {
    	/**Log.v(TAG, "Reply PM Clicked");
		TextView tv = (TextView)arg0;
		final String link = linkMap.get(tv.getId()); //tv.getText().toString());
		if(link != null && !link.equals("")) {
			Log.v(TAG, "User Clicked: " + link);
			
			// Open new thread
			new Thread("RefreshDisplayList") {
				public void run() {
					Intent intent = 
							new Intent(PrivateMessageActivity.this, 
									PrivateMessageViewActivity.class);
					intent.putExtra("link", link);
					startActivity(intent);
				}
			}.start();	
		}*/
    }
    
    /**
     * Handler for deleting a private message
     * @param arg0	The view associated with the private message
     */
    private void deletePm(View arg0) {
    	/**
    	Log.v(TAG, "Delete PM Clicked");
    	PMView tv = (PMView)arg0;
    	final String link = linkMap.get(tv.getId()); //tv.getText().toString());
    	
    	if(link != null && !link.equals("")) {
    		final String id = link.substring(link.lastIndexOf("id=") + 3);
			Log.v(TAG, "User Clicked: " + id);

			DeletePmTask dpm = new DeletePmTask(this, token, id);
			dpm.execute();
		}*/
    }
    
    /**
     * Convert an integer month to a string month
     * @param m	The integer month to convert
     * @return	A string representation of a month
     */
    private String getMonthForInt(int m) {
    	Calendar cal = Calendar.getInstance();
    	cal.set(Calendar.MONTH, m - 1);
    	
    	// For API 9
    	//cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.US);
    	
    	return String.format(Locale.US,"%tB",cal);
    }
    
    /**
     * Construct view by grabbing all private messages.  This is only done
     * if the view is called for the first time.  If there was a savedinstance
     * of the view then this is not called
     */
    private void constructView() {
    	loadingDialog = ProgressDialog.show(this, "Loading", "Please wait...", true);
    	final ForumBaseActivity src = this;
    	
        updaterThread = new Thread("NewPostsThread") {
 			public void run() { 		
 				Document doc = VBForumFactory.getInstance().get(src, WebUrls.pmUrl);
 				
 				token = HtmlFormUtils.getInputElementValue(doc, "securitytoken");
 				String current_month = getMonthForInt(0);
 				Elements collapse = doc.select("tbody[id^=collapseobj_pmf0]");
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
 								pm_m.setTitle(this_month);
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
 				loadingDialog.dismiss();
 			}
        };
        updaterThread.start();
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		super.onClick(arg0);
		
		switch(arg0.getId()) {	
		case NEW_PM:
			Log.v(TAG, "New PM Clicked");
			Intent intent = new Intent(PrivateMessageActivity.this, 
					NewPrivateMessageActivity.class);
			startActivity(intent);
			break;
		}
	}

    /*
     * (non-Javadoc)
     * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#enforceVariants(int, int)
     */
	@Override
	protected void enforceVariants(int currentPage, int lastPage) {
	}
}
