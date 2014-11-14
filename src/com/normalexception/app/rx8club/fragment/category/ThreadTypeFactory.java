package com.normalexception.app.rx8club.fragment.category;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;

public class ThreadTypeFactory {
	
	private static HashMap<Type,Bitmap> sBitmapCache = new HashMap<Type,Bitmap>();
	
	// Three different types of images
	private static enum Type { 
		NORMAL, 				// Normal thread
		POSTED,					// Posted thread
		LOCKED, 				// Locked thread
		POSTED_LOCKED,          // Locked w/ Posts
		STICKY,					// Sticky thread
		POSTED_STICKY,			// Sticky w/ Posts
		ANNOUNCEMENT
	};
	
	private static Logger TAG = LogManager.getLogger(ThreadTypeFactory.class.getName());
	
	/**
	 * Report a bitmap, and also cache the bitmap if it isn't already loaded
	 * @param src				The source activity
	 * @param width				The width of the bitmap
	 * @param height			The height of the bitmap
	 * @param isLocked			True if the thread is locked
	 * @param isSticky  		True if the thread is sticky
	 * @param hasPosts  		True if user has posts within thread
	 * @param isAnnouncement	True if announcement
	 * @return					A reference to the bitmap
	 */
	public static Bitmap getBitmap(Activity src, int width, int height, 
			boolean isLocked, boolean isSticky, boolean hasPosts, 
			boolean isAnnouncement) {
		Bitmap scaledimg;
		
		Resources rsc = (src == null)? 
				MainApplication.getAppContext().getResources() : src.getResources();
		
		Type theType = Type.NORMAL;
		int  res     = R.drawable.envelope;
		
		if(isLocked) {
			if(hasPosts) {
				theType = Type.POSTED_LOCKED;
				res     = R.drawable.lock_a;
			} else {
				theType = Type.LOCKED;
				res     = R.drawable.lock;
			}
    	
		} else if (isSticky) {
			if(hasPosts) {
				theType = Type.POSTED_STICKY;
				res     = R.drawable.push_pin_a;
			} else {
				theType = Type.STICKY;
				res     = R.drawable.push_pin;
			}
		} else if (isAnnouncement) {
			theType = Type.ANNOUNCEMENT;
			res = R.drawable.exclamation;
		} else {
			if (hasPosts) {
				theType = Type.POSTED;
				res     = R.drawable.open_envelope;
			} else {
				theType = Type.NORMAL;
				res     = R.drawable.envelope;
			}
		}
		
		scaledimg = sBitmapCache.get(theType);
		if(scaledimg == null) {
			scaledimg = 
				Bitmap.createScaledBitmap(
						BitmapFactory.decodeResource(
								rsc, res), 
								width, height, true);
			Log.d(TAG, "Caching Icon");
			sBitmapCache.put(theType, scaledimg);
		}
		
		return scaledimg;
	}
}
