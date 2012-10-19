package com.normalexception.forum.rx8club.view;

import com.normalexception.forum.rx8club.MainApplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class PostButtonView extends TextView implements OnClickListener {
	public static final int EDITBUTTON = 0;
	public static final int DELETEBUTTON = 1;
	
	private int postId;
	private int buttonType;
	
	public PostButtonView(Context context) {
		super(context);
	}
	
	public PostButtonView(Context context, int button) {
		super(context);
		this.setOnClickListener(this);
		postId = 0;
		buttonType = button;
	}
	
	public int getPostId() {
		return postId;
	}
	
	public void setPostId(int post) {
		this.postId = post;
	}
	
	public int getButtonType() {
		return buttonType;
	}

	@Override
	public void onClick(View arg0) {
		PostButtonView btn = (PostButtonView)arg0;
		switch(btn.getButtonType()) {
		case EDITBUTTON:
			Toast.makeText(MainApplication.getAppContext(), 
					"Edit Post " + this.getPostId(), 
					Toast.LENGTH_SHORT).show();
			break;
		case DELETEBUTTON:
			DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	Toast.makeText(MainApplication.getAppContext(), 
								"Delete Post " + getPostId(), 
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
		}
	}
}
