package com.normalexception.app.rx8club.view.thread;

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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.category.ThreadTypeFactory;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.utils.DateDifference;
import com.normalexception.app.rx8club.utils.SpecialNumberFormatter;

public class ThreadView extends RelativeLayout {
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
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
    
    private boolean isNewThread = false;

    public ThreadView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_thread_children, this, true);
		setupChildren();
	}

	public ThreadView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_thread_children, this, true);
		setupChildren();
	}

	public ThreadView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_thread_children, this, true);
		setupChildren();
	}

	/**
	 * Setup the children we contain in this view
	 */
	private void setupChildren() {
		vTitle 	   = (TextView) findViewById(R.id.tv_title);
        vPostCount = (TextView) findViewById(R.id.tv_postCount);
        vPostCountL= (TextView) findViewById(R.id.tv_postCount_label);
        vPostUser  = (TextView) findViewById(R.id.tv_postUser);
        vLastUser  = (TextView) findViewById(R.id.tv_lastUser);
        vLastUserL = (TextView) findViewById(R.id.tv_lastUser_label);
        vLastDate  = (TextView) findViewById(R.id.tv_lastUserDate);
        vMyCount   = (TextView) findViewById(R.id.tv_myCount);
        vMyCountL  = (TextView) findViewById(R.id.tv_myCount_label);
        vViewCount = (TextView) findViewById(R.id.tv_viewCount);
        vViewCountL= (TextView) findViewById(R.id.tv_viewCount_label);
        vForum     = (TextView) findViewById(R.id.tv_forum);
        vImage 	   = (ImageView) findViewById(R.id.tv_image);
        vAttachment= (ImageView) findViewById(R.id.tv_attachment);
        vForumC    = (LinearLayout) findViewById(R.id.tv_forum_details);
	}
	
	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static ThreadView inflate(ViewGroup parent) {
		ThreadView threadView = (ThreadView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_thread, parent, false);
		return threadView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param m			The model we are going to use to populate the view
	 * @param newThread True if the thread is a newly updated thread view
	 */
	public void setThread(final ThreadModel m, boolean newThread) {
		this.isNewThread = newThread; 
		
		 // Set default text color
        vTitle.setTextColor(Color.BLACK);
        
        // Set all display components to visible to start
        hideThreadDetails(false);
        
        if(!m.isStub()) {
	        // Check if the thread title is a for sale type thread
	        String threadTitle = m.getTitle();
	        Pattern pattern = Pattern.compile("(\\{\\s\\w*\\s\\})(.*)", Pattern.CASE_INSENSITIVE);
	        Matcher matcher = pattern.matcher(threadTitle);
	        
	        if(matcher.matches()) {
	        	Log.d(TAG, "Found a FS Type Thread...");
	        	vTitle.setText(Html.fromHtml("<font color='red'>"
	        			+ matcher.group(1) + "</font>" 
	        			+ matcher.group(2)));
	        } else {   
	        	vTitle.setText(       threadTitle);
	        }
	        vPostUser.setText(    m.getStartUser());
	        vLastUser.setText(    m.getLastUser());
	        
	        String differenceTime = DateDifference.getPrettyDate(m.getLastPostTime());
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
				setMode(this, true, Color.CYAN);
			} else if(m.isLocked())
				setMode(this, false, Color.DKGRAY);
			else 
				setMode(this, false, Color.GRAY);
			
			// If this is a favorite view then just hide the 
			// details.  We dont care
			if (m.isFavorite())
				hideThreadDetails(true);
			
			// If this is an announcement, then display without
			// any particular details
			if (m.isAnnouncement()) {
				setMode(this, true, Color.CYAN);
				hideThreadDetails(true);
			}
			
			// Display the attachment icon if we have an attachment
			if (!m.hasAttachment())
				vAttachment.setVisibility(View.GONE);
			else 
				vAttachment.setVisibility(View.VISIBLE);
        } else {
        	// If it is a stub, then at this time it means that we have 
        	// reached the end of our newest posts.  So show the user a 
        	// message letting them know
        	vTitle.setText(
        		MainApplication.getAppContext().getString(R.string.constantNoUpdate));
        	vTitle.setTextColor(Color.WHITE);
        	setMode(this, true, Color.BLACK);
        	hideThreadDetails(true);
        }
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
		vAttachment.setVisibility(hide? View.GONE : View.VISIBLE);
		
		vImage.setVisibility(hide? View.GONE : View.VISIBLE);
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
		vMyCount  .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vViewCount.setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		vForum    .setTextColor(isSpecial? Color.BLACK : Color.WHITE);
		
		// Get a bit fancy here to make the difference of last post 
		// stand out a bit
		vLastDate .setTextColor(isSpecial? Color.BLACK : Color.rgb(0,0,128));
	}
}
