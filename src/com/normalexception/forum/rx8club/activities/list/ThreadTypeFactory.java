package com.normalexception.forum.rx8club.activities.list;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.normalexception.forum.rx8club.R;

public class ThreadTypeFactory {
	
	public static Bitmap getBitmap(Activity src, int width, int height, 
			boolean isLocked, boolean isSticky) {
		Bitmap scaledimg;
		
		if(isLocked) {
			scaledimg = 
				Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(
								src.getResources(), R.drawable.lock), 
								width, height, true);
    	
		} else if (isSticky) {
			scaledimg =  
				Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(
								src.getResources(), R.drawable.sticky), 
								width, height, true);
			
		} else {
			scaledimg = 
					Bitmap.createScaledBitmap(
							BitmapFactory.decodeResource(
									src.getResources(), R.drawable.arrow_icon), 
									width, height, true);
		}
		
		return scaledimg;
	}
}
