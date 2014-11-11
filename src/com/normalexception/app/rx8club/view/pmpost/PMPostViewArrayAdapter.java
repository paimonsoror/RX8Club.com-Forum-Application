package com.normalexception.app.rx8club.view.pmpost;

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
import android.graphics.Color;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.handler.AvatarLoader;
import com.normalexception.app.rx8club.handler.ForumImageHandler;
import com.normalexception.app.rx8club.preferences.PreferenceHelper;
import com.normalexception.app.rx8club.utils.Utils;
import com.normalexception.app.rx8club.view.ViewHolder;
import com.normalexception.app.rx8club.view.threadpost.PostView;

public class PMPostViewArrayAdapter extends ArrayAdapter<PMPostView> {
	private Context activity;
	private List<PMPostView> data;
	private AvatarLoader imageLoader;

	/**
	 * Custom adapter to handle PMItemView's
	 * @param context				The source context
	 * @param textViewResourceId	The resource id
	 * @param objects				The list of objects
	 */
	public PMPostViewArrayAdapter(Context context, int textViewResourceId,
			List<PMPostView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
		imageLoader = new AvatarLoader(activity.getApplicationContext());
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
    public PMPostView getItem(int position) {     
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
            vi = vinf.inflate(R.layout.view_newreply, null);
        }
        
        final PostView cv = data.get(position);
        

        ((TextView) ViewHolder.get(vi,R.id.nr_likeText)).setVisibility(View.GONE);
        ((TextView) ViewHolder.get(vi,R.id.nr_username)).setText(cv.getUserName());
        ((TextView) ViewHolder.get(vi,R.id.nr_userTitle)).setText(cv.getUserTitle());
        ((TextView) ViewHolder.get(vi,R.id.nr_userPosts)).setText(cv.getUserPostCount());
        ((TextView) ViewHolder.get(vi,R.id.nr_userJoin)).setText(cv.getJoinDate());
        ((TextView) ViewHolder.get(vi,R.id.nr_postDate)).setText(cv.getPostDate());
        
        TextView postText = ((TextView) ViewHolder.get(vi,R.id.nr_postText));
        ForumImageHandler fih = new ForumImageHandler(postText, Utils.randomInt(0, 9999), activity);        
        postText.setText(Html.fromHtml(cv.getUserPost(), fih, null));
        
        // Lets make sure we remove any font formatting that was done within
        // the text
        String trimmedPost = 
        		cv.getUserPost().replaceAll("(?i)<(/*)font(.*?)>", "");
       
        // Show attachments if the preference allows it
        if(PreferenceHelper.isShowAttachments(activity)) 
        	trimmedPost = appendAttachments(trimmedPost, cv.getAttachments());
        
        postText.setText(Html.fromHtml(trimmedPost, fih, null));
        postText.setTextColor(Color.WHITE);
        postText.setLinkTextColor(Color.WHITE);
        
        // Set the text size based on our preferences
        int font_size = PreferenceHelper.getFontSize(activity);
        postText.setTextSize(font_size);
        
        // Load up the avatar of hte user, but remember to remove
        // the dateline at the end of the file so that we aren't
        // creating multiple images for a user.  The image still
        // gets returned without a date
        if(PreferenceHelper.isShowAvatars(activity)) {
	        String nodate_avatar = 
	        		cv.getUserImageUrl().indexOf('?') == -1? 
	        				cv.getUserImageUrl() : 
	        					cv.getUserImageUrl().substring(0, cv.getUserImageUrl().indexOf('?'));
	        
	        if(!nodate_avatar.isEmpty()) {
	        	ImageView avatar = ((ImageView)ViewHolder.get(vi,R.id.nr_image));
	        	imageLoader.DisplayImage(nodate_avatar, avatar);
	        }
        }
        
        // Set click listeners
        ((ImageView) ViewHolder.get(vi,R.id.nr_quoteButton)).setVisibility(View.GONE);
        ((ImageView) ViewHolder.get(vi,R.id.nr_editButton)).setVisibility(View.GONE);
        ((ImageView) ViewHolder.get(vi,R.id.nr_pmButton)).setVisibility(View.GONE);
        ((ImageView) ViewHolder.get(vi,R.id.nr_deleteButton)).setVisibility(View.GONE);
        
        return vi;
	}
	
	/**
	 * Append attachments to the end of this post if they exist
	 * @param trimmedPost	The current post
	 * @param attachments	The attachments of the post
	 * @return				An appended html string
	 */
	private String appendAttachments(String trimmedPost,
			List<String> attachments) {
		if(attachments == null || attachments.isEmpty())
			return trimmedPost;
		
		// Create an html string for the attachments
		String attachString = "";
		for(String attachment : attachments)
			attachString += 
				String.format("<a href=\"%s\"><img src=\"%s\"></a>&nbsp;", 
						attachment, attachment);
		
		// Now append to the original text
		trimmedPost = 
				String.format("%s<br><br><b>Attachments:</b><br>%s", 
						trimmedPost, attachString);
		
		return trimmedPost;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#areAllItemsEnabled()
	 */
	@Override
	public boolean  areAllItemsEnabled() {
	    return false;			
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
	        return false;
	}
}