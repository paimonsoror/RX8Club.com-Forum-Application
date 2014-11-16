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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.WebUrls;
import com.normalexception.app.rx8club.cache.Cache;
import com.normalexception.app.rx8club.handler.AvatarLoader;
import com.normalexception.app.rx8club.preferences.PreferenceHelper;
import com.normalexception.app.rx8club.utils.Utils;

public class PMPostView extends RelativeLayout {

	private AvatarLoader imageLoader; 

	private TextView username;
	private TextView userTitle;
	private TextView userPosts;
	private TextView userJoin;
	private TextView postDate;
	private TextView likeText;
	private TextView postBox;

	private ImageView reportbutton;
	private ImageView downButton;
	private ImageView avatar;
	private ImageView quoteButton;
	private ImageView editButton;
	private ImageView pmButton;
	private ImageView deleteButton;

	private WebView postText;

	public PMPostView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.view_newreply_children, this, true);
		imageLoader=new AvatarLoader(context);
		setupChildren();
	}

	public PMPostView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.view_newreply_children, this, true);
		imageLoader=new AvatarLoader(context);
		setupChildren();
	}

	public PMPostView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		LayoutInflater.from(context).inflate(R.layout.view_newreply_children, this, true);
		imageLoader=new AvatarLoader(context);
		setupChildren();
	}

	/**
	 * Setup the children we contain in this view
	 */
	private void setupChildren() {
		username = (TextView) findViewById(R.id.nr_username);
		userTitle = (TextView) findViewById(R.id.nr_userTitle);
		userPosts = (TextView) findViewById(R.id.nr_userPosts);
		userJoin = (TextView) findViewById(R.id.nr_userJoin);
		postDate = (TextView) findViewById(R.id.nr_postDate);
		likeText = (TextView) findViewById(R.id.nr_likeText);
		postBox = (TextView) findViewById(R.id.postBox);

		reportbutton = (ImageView) findViewById(R.id.nr_reportbutton);
		avatar = (ImageView) findViewById(R.id.nr_image);
		downButton = (ImageView) findViewById(R.id.nr_downButton);
		quoteButton = (ImageView) findViewById(R.id.nr_quoteButton);
		editButton = (ImageView) findViewById(R.id.nr_editButton);
		pmButton = (ImageView) findViewById(R.id.nr_pmButton);
		deleteButton = (ImageView) findViewById(R.id.nr_deleteButton);

		postText = (WebView) findViewById(R.id.nr_postText);
	}

	/**
	 * Inflate the view, this technically only gets called the first time the
	 * view is accessed
	 * @param parent	The parent of the view
	 * @return			An inflated object
	 */
	public static PMPostView inflate(ViewGroup parent) {
		PMPostView pmView = (PMPostView)LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_newpmreply, parent, false);
		return pmView;
	}
	
	/**
	 * Setup our view here.  After the view has been inflated and all of the
	 * view objects have been initialized, we can inflate our view here
	 * @param post	The model we are going to use to populate the view
	 */
	public void setPMPost(final PMPostModel post) {
		likeText.setVisibility(View.GONE);
		username.setText(post.getUserName());
		userTitle.setText(post.getUserTitle());
		userPosts.setText(post.getUserPostCount());
		userJoin.setText(post.getJoinDate());
		postDate.setText(post.getPostDate());

		// Lets make sure we remove any font formatting that was done within
		// the text
		String trimmedPost = 
				post.getUserPost().replaceAll("(?i)<(/*)font(.*?)>", "");

		// Show attachments if the preference allows it
		if(PreferenceHelper.isShowAttachments(getContext())) 
			trimmedPost = appendAttachments(trimmedPost, post.getAttachments());

		// Set html Font color
		trimmedPost = Utils.postFormatter(trimmedPost, getContext());
		postText.setBackgroundColor(Color.DKGRAY);
		postText.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		postText.getSettings().setAppCachePath(
				Cache.getExternalCacheDir(getContext()).getAbsolutePath());
		postText.getSettings().setAllowFileAccess(false);
		postText.getSettings().setAppCacheEnabled(true);
		postText.getSettings().setJavaScriptEnabled(false);
		postText.getSettings().setSupportZoom(false);
		postText.getSettings().setSupportMultipleWindows(false);
		postText.getSettings().setUserAgentString(WebUrls.USER_AGENT);
		postText.getSettings().setDatabaseEnabled(false);
		postText.getSettings().setDomStorageEnabled(false);
		postText.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		postText.setOnTouchListener(new View.OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				return (event.getAction() == MotionEvent.ACTION_MOVE);
			}
		});

		postText.loadDataWithBaseURL(WebUrls.rootUrl, trimmedPost, "text/html", "utf-8", ""); 

		// Load up the avatar of hte user, but remember to remove
		// the dateline at the end of the file so that we aren't
		// creating multiple images for a user.  The image still
		// gets returned without a date
		if(PreferenceHelper.isShowAvatars(getContext())) {
			String nodate_avatar = 
					post.getUserImageUrl().indexOf('?') == -1? 
							post.getUserImageUrl() : 
								post.getUserImageUrl().substring(0, post.getUserImageUrl().indexOf('?'));

							if(!nodate_avatar.isEmpty()) {	
								imageLoader.DisplayImage(nodate_avatar, avatar);
							} else {
								avatar.setImageResource(R.drawable.rotor_icon);
							}
		}

		// Set click listeners
		quoteButton.setVisibility(View.GONE);
		editButton.setVisibility(View.GONE);
		pmButton.setVisibility(View.GONE);
		deleteButton.setVisibility(View.GONE);
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

}
