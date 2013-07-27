package com.normalexception.forum.rx8club.activities.list;

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

import java.util.HashMap;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;

public class ThreadTypeFactory {
	
	private static HashMap<Type,Bitmap> sBitmapCache = new HashMap<Type,Bitmap>();
	
	// Three different types of images
	private static enum Type { 
		NORMAL, 				// Normal thread
		LOCKED, 				// Locked thread
		STICKY 					// Sticky thread
	};
	
	private static final String TAG = "ThreadTypeFactory";
	
	/**
	 * Report a bitmap, and also cache the bitmap if it isn't already loaded
	 * @param src		The source activity
	 * @param width		The width of the bitmap
	 * @param height	The height of the bitmap
	 * @param isLocked	True if the thread is locked
	 * @param isSticky  True if the thread is sticky
	 * @return			A reference to the bitmap
	 */
	public static Bitmap getBitmap(Activity src, int width, int height, 
			boolean isLocked, boolean isSticky) {
		Bitmap scaledimg;
		
		Resources rsc = (src == null)? 
				MainApplication.getAppContext().getResources() : src.getResources();
		
		if(isLocked) {
			scaledimg = sBitmapCache.get(Type.LOCKED);
			if(scaledimg == null) {
				scaledimg = 
					Bitmap.createScaledBitmap(
							BitmapFactory.decodeResource(
									rsc, R.drawable.lock), 
									width, height, true);
				Log.d(TAG, "Caching Locked Icon");
				sBitmapCache.put(Type.LOCKED, scaledimg);
			}
    	
		} else if (isSticky) {
			scaledimg = sBitmapCache.get(Type.STICKY);
			if(scaledimg == null) {
				scaledimg =  
					Bitmap.createScaledBitmap(
							BitmapFactory.decodeResource(
									rsc, R.drawable.sticky), 
									width, height, true);
				Log.d(TAG, "Caching Sticky Icon");
				sBitmapCache.put(Type.STICKY, scaledimg);
			}
		} else {
			scaledimg = sBitmapCache.get(Type.NORMAL);
			if(scaledimg == null) {
				scaledimg = 
						Bitmap.createScaledBitmap(
								BitmapFactory.decodeResource(
										rsc, R.drawable.arrow_icon), 
										width, height, true);
				Log.d(TAG, "Caching Normal Icon");
				sBitmapCache.put(Type.NORMAL, scaledimg);
			}
		}
		
		return scaledimg;
	}
}
