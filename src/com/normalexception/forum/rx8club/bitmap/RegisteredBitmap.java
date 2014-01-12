package com.normalexception.forum.rx8club.bitmap;

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

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;

import com.normalexception.forum.rx8club.Log;

public class RegisteredBitmap {
	
	private int id = 0;
	private Bitmap bmp = null;
	private static String TAG = "RegisteredBitmap";
	
	private static Map<String, SoftReference<RegisteredBitmap>> archive = 
			new HashMap<String, SoftReference<RegisteredBitmap>>(); 
	
	/**
	 * Create a registered bitmap.  This is a container for a bitmap that 
	 * links a bitmap to an ID.  The id is a random number that is associated
	 * to a thread.  This creates a bond between a thread and its bitmaps.
	 * @param id		 The id that represents a thread
	 * @param source	 The source where the image exists
	 * @param onlyOpaque True if image will only be opaque
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public RegisteredBitmap(int id, String source, boolean onlyOpaque) 
			throws MalformedURLException, IOException {
		this.id = id;
		
		SoftReference<RegisteredBitmap> temp = archive.get(source);
		
		if(temp == null || temp.get() == null || temp.get().getBitmap() == null) {
			Log.d(TAG, String.format("Creating New Bitmap (%d)", id));
			
            bmp = BitmapDecoder.decodeSource(source, onlyOpaque);
			archive.put(source, new SoftReference<RegisteredBitmap>(this));
		} else {
			Log.d(TAG, "Reporting Cached Bitmap");
			bmp = temp.get().getBitmap();
		}
	}
	
	/**
	 * Report the bitmap
	 * @return	The bitmap object
	 */
	public Bitmap getBitmap() {
		return this.bmp;
	}
}
