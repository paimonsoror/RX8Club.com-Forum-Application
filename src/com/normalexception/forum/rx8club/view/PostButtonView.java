package com.normalexception.forum.rx8club.view;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.EditPostActivity;

/**
 * An extension of a TextView that is used in the ThreadActivity to 
 * hold the edit and delete images.  This also stores the post id.
 */
public class PostButtonView extends TextView implements OnClickListener {
	public static final int EDITBUTTON = 0;
	public static final int DELETEBUTTON = 1;
	public static final int QUOTEBUTTON = 2;
	
	private String postId;
	private int buttonType;
	private String token;
	private String user;
	private String title;
	private String page;
	private int index;
	
	/**
	 * Constructor that just calls the super constructor
	 * @param context	The source context
	 */
	public PostButtonView(Context context) {
		super(context);
	}
	
	/**
	 * Similar to the default constructor, except it contains
	 * and identifier for the type of button that is used
	 * @param context	The source context
	 * @param button	The type of button that this view represents
	 * @param token		The security token for the session
	 */
	public PostButtonView(Context context, int button,
						  int index, String token, String user, 
						  String title, String page) {
		super(context);
		this.setOnClickListener(this);
		buttonType = button;
		this.token = token;
		this.user = user;
		this.index = index;
		this.title = title;
		this.page = page;
	}
	
	/**
	 * Report the post id
	 * @return	The post id
	 */
	public String getPostId() {
		return postId;
	}
	
	/**
	 * Set the post id
	 * @param post	The post id to set
	 */
	public void setPostId(String post) {
		this.postId = post;
	}
	
	/**
	 * Report the button type
	 * @return	The type of button represented by this view
	 */
	public int getButtonType() {
		return buttonType;
	}
	
	/**
	 * Report the security token
	 * @return	The security token
	 */
	public String getSecurityToken() {
		return token;
	}
	
	/**
	 * Report the user that owns the post
	 * @return	The user that owns the post
	 */
	public String getUser() {
		return user;
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		PostButtonView btn = (PostButtonView)arg0;
		switch(btn.getButtonType()) {
		case EDITBUTTON:
			Intent _intent = new Intent(getContext(), EditPostActivity.class);
			_intent.putExtra("postid", postId);
			_intent.putExtra("securitytoken", token);
			_intent.putExtra("pagenumber", this.page);
			_intent.putExtra("pagetitle", this.title);
			getContext().startActivity(_intent);
			break;
		case DELETEBUTTON:
			DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	Toast.makeText(MainApplication.getAppContext(), 
								"Delete Post Coming Soon...", 
								Toast.LENGTH_SHORT).show();
			            break;
			        }
			    }
			};

			AlertDialog.Builder builder = 
					new AlertDialog.Builder(getContext());
			builder
				.setMessage("Are you sure you want to delete your post?")
				.setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener)
			    .show();
			break;
		case QUOTEBUTTON:
			Activity threadActivity = (Activity)getContext();
			String txt = ((TextView)threadActivity.findViewById(index)).getText().toString();
			String finalText = String.format("[quote=%s]%s[/quote]",
					getUser(), txt);
			((TextView)threadActivity.findViewById(R.id.postBox)).setText(
					finalText
			);
			((TextView)threadActivity.findViewById(R.id.postBox)).requestFocus();
			break;
		}
	}
}
