package com.normalexception.forum.rx8club.view.thread;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.list.ThreadTypeFactory;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.utils.DateDifference;
import com.normalexception.forum.rx8club.utils.DateDifference.TimeField;
import com.normalexception.forum.rx8club.utils.SpecialNumberFormatter;
import com.normalexception.forum.rx8club.view.ViewHolder;

/**
 * A custom view adapter for a thread view object
 */
public class ThreadViewArrayAdapter extends ArrayAdapter<ThreadView> {
	
	private Context activity;
    private List<ThreadView> data; 
    private boolean isNewThread = false;
    
    private TextView vTitle 	= null;
    private TextView vPostCount = null;
    private TextView vPostCountL= null;
    private TextView vPostUser 	= null;
    private TextView vLastUser 	= null;
    private TextView vLastUserL = null;
    private TextView vLastDate  = null;
    private TextView vMyCount 	= null;
    private TextView vMyCountL  = null;
    private TextView vViewCount = null;
    private TextView vViewCountL= null;
    private ImageView vImage 	= null;
    private ImageView vAttachment = null;
    private TextView vForum     = null;
    private LinearLayout vForumC= null;
 
    public ThreadViewArrayAdapter(Context context, int textViewResourceId,
			List<ThreadView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}
    
    /**
     * Set if the view is an instance of New Posts
     * @param isNewThread	If true, view is New Posts view
     */
    public void setIsNewThread(boolean isNewThread) {
    	this.isNewThread = isNewThread;
    }
    
    /**
     * If true, this view is the New Posts view
     * @return	New Posts view if true
     */
    public boolean isNewThread() {
    	return this.isNewThread;
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return data == null? 0 : data.size();
	}
    
    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override  
    public ThreadView getItem(int position) {     
        return data.get(position);  
    } 

	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {		
		View vi = convertView;
        if(vi == null) {
        	LayoutInflater vinf =
                    (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = vinf.inflate(R.layout.view_thread, null);
        }
 
        ThreadView m = new ThreadView();
        m = data.get(position);
         
        vTitle 	   = (TextView) ViewHolder.get(vi,R.id.tv_title);
        vPostCount = (TextView) ViewHolder.get(vi,R.id.tv_postCount);
        vPostCountL= (TextView) ViewHolder.get(vi,R.id.tv_postCount_label);
        vPostUser  = (TextView) ViewHolder.get(vi,R.id.tv_postUser);
        vLastUser  = (TextView) ViewHolder.get(vi,R.id.tv_lastUser);
        vLastUserL = (TextView) ViewHolder.get(vi,R.id.tv_lastUser_label);
        vLastDate  = (TextView) ViewHolder.get(vi,R.id.tv_lastUserDate);
        vMyCount   = (TextView) ViewHolder.get(vi,R.id.tv_myCount);
        vMyCountL  = (TextView) ViewHolder.get(vi,R.id.tv_myCount_label);
        vViewCount = (TextView) ViewHolder.get(vi,R.id.tv_viewCount);
        vViewCountL= (TextView) ViewHolder.get(vi,R.id.tv_viewCount_label);
        vForum     = (TextView) ViewHolder.get(vi, R.id.tv_forum);
        vImage 	   = (ImageView) ViewHolder.get(vi,R.id.tv_image);
        vAttachment= (ImageView) ViewHolder.get(vi,R.id.tv_attachment);
        vForumC    = (LinearLayout) ViewHolder.get(vi, R.id.tv_forum_details);
        
        // Set all display components to visible to start
        hideThreadDetails(false);
        
        vTitle.setText(       m.getTitle());
        vPostUser.setText(    m.getStartUser());
        vLastUser.setText(    m.getLastUser());
        
        String differenceTime = getLastPostDifference(m.getLastPostTime());
        vLastDate.setText(   differenceTime);

        vPostCount.setText(   
        		SpecialNumberFormatter.collapseNumber(m.getPostCount()));
        vMyCount.setText(
        		SpecialNumberFormatter.collapseNumber(m.getMyPosts()));
        vViewCount.setText(
        		SpecialNumberFormatter.collapseNumber(m.getViewCount()));
        
        boolean hasPosts = !vMyCount.getText().equals("0");
        Bitmap scaledimg = 
    			ThreadTypeFactory.getBitmap(
    					null, 15, 13, m.isLocked(), m.isSticky(), hasPosts, 
    					m.isAnnouncement());
        vImage.setImageBitmap(scaledimg);      
        
        // Hide a few things if we are a guest
        if(LoginFactory.getInstance().isGuestMode()) {
        	vMyCountL.setVisibility(View.GONE);
        	vMyCount.setVisibility(View.GONE);
        }
        
        vForumC.setVisibility(this.isNewThread? View.VISIBLE : View.GONE);
        vForum.setText(m.getForum());
		
        // Set up our color scheme for the threads if the
        // thread is a sticky or if it is locked.  Else
        // lets use the default
		if (m.isSticky()) {
			setMode(vi, true, Color.CYAN);
		} else if(m.isLocked())
			setMode(vi, false, Color.DKGRAY);
		else 
			setMode(vi, false, Color.GRAY);
		
		// If this is a favorite view then just hide the 
		// details.  We dont care
		if (m.isFavorite())
			hideThreadDetails(true);
		
		// If this is an announcement, then display without
		// any particular details
		if (m.isAnnouncement()) {
			setMode(vi, true, Color.CYAN);
			hideThreadDetails(true);
		}
		
		// Display the attachment icon if we have an attachment
		if (!m.hasAttachment())
			vAttachment.setVisibility(View.GONE);
		else 
			vAttachment.setVisibility(View.VISIBLE);
		
        return vi;
	}
	
	/**
	 * Convenient method for grabbing the difference between the
	 * last post and today
	 * @param lastpost	The last post time
	 * @return			The difference string
	 */
	private String getLastPostDifference(String lastpost) {
		String differenceTime = "";
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.getDefault());
        	Date lastDate = sdf.parse(lastpost);
        	long diffs[]  = DateDifference.getTimeDifference(lastDate, new Date());
        	if(diffs[TimeField.DAY.ordinal()] != 0) {
        		if(diffs[TimeField.DAY.ordinal()] > 365) {
        			differenceTime = String.format(Locale.getDefault(), " (%d years ago)", 
	        				diffs[TimeField.DAY.ordinal()] / 365);
        		} else {
	        		differenceTime = String.format(Locale.getDefault(), " (%d days ago)", 
	        				diffs[TimeField.DAY.ordinal()]);
        		}
        	} else if(diffs[TimeField.HOUR.ordinal()] == 0)
        		differenceTime = String.format(Locale.getDefault(), " (%dmins ago)", 
        				diffs[TimeField.MINUTE.ordinal()]);
        	else
        		differenceTime = String.format(Locale.getDefault(), " (%dhrs%s ago)", 
        				diffs[TimeField.HOUR.ordinal()], 
        				diffs[TimeField.MINUTE.ordinal()] > 0? "+" : "");
        } catch (ParseException pe) { }
        
        return differenceTime;
	}

	
	/**
	 * Set the mode of the thread view object.  If the view is a 
	 * special view, we want to set a different font color and 
	 * background color
	 * @param vi		The source view
	 * @param isSpecial	If true, this is a special view
	 * @param bgColor	The bg color to set
	 */
	private void setMode(View vi, boolean isSpecial, int bgColor) {
		vi.setBackgroundColor(bgColor);
		vPostCount.setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vPostUser .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vLastUser .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vLastDate .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vMyCount  .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vViewCount.setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vForum    .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
	}
	
	/**
	 * Hide the thread details, which is really only used on special
	 * occasions like the favorites list
	 */
	public void hideThreadDetails(boolean hide) {
		vPostCount .setVisibility(hide? View.GONE : View.VISIBLE);
		vPostCountL.setVisibility(hide? View.GONE : View.VISIBLE);

		vLastUser .setVisibility(hide? View.GONE : View.VISIBLE);
		vLastUserL.setVisibility(hide? View.GONE : View.VISIBLE);
		
		vLastDate.setVisibility(hide? View.GONE : View.VISIBLE);
		
		vMyCount  .setVisibility(hide? View.GONE : View.VISIBLE);
		vMyCountL .setVisibility(hide? View.GONE : View.VISIBLE);
		
		vViewCount .setVisibility(hide? View.GONE : View.VISIBLE);
		vViewCountL.setVisibility(hide? View.GONE : View.VISIBLE);
		
		vForumC.setVisibility(hide? View.GONE : View.VISIBLE);
	}
}
