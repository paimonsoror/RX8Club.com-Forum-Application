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

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.list.ThreadTypeFactory;
import com.normalexception.forum.rx8club.utils.LoginFactory;
import com.normalexception.forum.rx8club.utils.SpecialNumberFormatter;
import com.normalexception.forum.rx8club.view.ViewHolder;

/**
 * A custom view adapter for a thread view object
 */
public class ThreadViewArrayAdapter extends ArrayAdapter<ThreadView> {
	
	private Context activity;
    private List<ThreadView> data; 
    
    private TextView vTitle 	= null;
    private TextView vPostCount = null;
    private TextView vPostUser 	= null;
    private TextView vLastUser 	= null;
    private TextView vMyCount 	= null;
    private TextView vMyCountL  = null;
    private TextView vViewCount = null;
    ImageView vImage 	= null;
    
    private final String TAG = "ThreadViewArrayAdapter";
 
    public ThreadViewArrayAdapter(Context context, int textViewResourceId,
			List<ThreadView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}
    
    /*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return data.size();
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
        vPostUser  = (TextView) ViewHolder.get(vi,R.id.tv_postUser);
        vLastUser  = (TextView) ViewHolder.get(vi,R.id.tv_lastUser);
        vMyCount   = (TextView) ViewHolder.get(vi,R.id.tv_myCount);
        vMyCountL  = (TextView) ViewHolder.get(vi,R.id.tv_myCount_label);
        vViewCount = (TextView) ViewHolder.get(vi,R.id.tv_viewCount);
        vImage 	   = (ImageView) ViewHolder.get(vi,R.id.tv_image);
        
        vTitle.setText(       m.getTitle());
        vPostUser.setText(    m.getStartUser());
        vLastUser.setText(    m.getLastUser());
        
        Log.d(TAG, 
        		String.format("%s %s %s", m.getPostCount(), m.getMyPosts(), m.getViewCount()));
        vPostCount.setText(   
        		SpecialNumberFormatter.collapseNumber(m.getPostCount()));
        vMyCount.setText(
        		SpecialNumberFormatter.collapseNumber(m.getMyPosts()));
        vViewCount.setText(
        		SpecialNumberFormatter.collapseNumber(m.getViewCount()));
        
        boolean hasPosts = !vMyCount.getText().equals("0");
        Bitmap scaledimg = 
    			ThreadTypeFactory.getBitmap(
    					null, 15, 13, m.isLocked(), m.isSticky(), hasPosts);
        vImage.setImageBitmap(scaledimg);      
        
        // Hide a few things if we are a guest
        if(LoginFactory.getInstance().isGuestMode()) {
        	vMyCountL.setVisibility(View.GONE);
        	vMyCount.setVisibility(View.GONE);
        }
		
		if (m.isSticky()) {
			setMode(vi, true, Color.CYAN);
		} else if(m.isLocked())
			setMode(vi, false, Color.DKGRAY);
		else 
			setMode(vi, false, Color.GRAY);
		
        return vi;
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
	}
}
