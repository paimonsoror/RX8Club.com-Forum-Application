package com.normalexception.app.rx8club.bitmap;

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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.preferences.PreferenceHelper;

public class BitmapDecoder {
	
	private static Logger TAG =  LogManager.getLogger(BitmapDecoder.class);
	
	/**
	 * Decode our bitmap while taking memory into consideraiton.  Here we
	 * want to decode the bitmap with a small sample size so taht we conserve
	 * as much memory as possible
	 * @param source	The input stream from the url
	 * @param useMin	Use min pixels to decode.  This will remove transparencies
	 * @return
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public static Bitmap decodeSource(final String source, final boolean useMin) 
			throws MalformedURLException, IOException {
		Log.d(TAG, "Decoding " + source);
		Bitmap mBitmap = null;
		try {
			InputStream is = new URL(source).openStream();
			final double sample_size = 
					PreferenceHelper.getThreadImageSize(MainApplication.getAppContext());
	        mBitmap = decodeSampledBitmapFromResourceMemOpt(is, (int)sample_size,
	        		(int)sample_size, useMin);
	        try {
	        	Log.d(TAG, String.format("Bitmap size %d bytes", 
	        		mBitmap.getRowBytes() * mBitmap.getHeight()));
	        } catch (NullPointerException e) {
	        	// Just incase image is empty
	        	Log.d(TAG, "Couldn't get bitmap size");
	        }
		} catch (FileNotFoundException ex) {
			Log.d(TAG, "-- File Not Found: " + source);
		}
        return mBitmap;
	}
	
	/**
	 * Now we want to decode the sampled bitmap from the stream.
	 * @param inputStream	The input stream containing the image
	 * @param reqWidth		The required sample width
	 * @param reqHeight		The required sample height
	 * @param useMin		The control to use min pixels
	 * @return				Return a bitmap image
	 */
	private static Bitmap decodeSampledBitmapFromResourceMemOpt(
            InputStream inputStream, int reqWidth, int reqHeight, 
            boolean useMin) {

        byte[] byteArr = new byte[0];
        byte[] buffer = new byte[1024];
        int len;
        int count = 0;

        try {
            while ((len = inputStream.read(buffer)) > -1) {
                if (len != 0) {
                    if (count + len > byteArr.length) {
                        byte[] newbuf = new byte[(count + len) * 2];
                        System.arraycopy(byteArr, 0, newbuf, 0, count);
                        byteArr = newbuf;
                    }

                    System.arraycopy(buffer, 0, byteArr, count, len);
                    count += len;
                }
            }

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(byteArr, 0, count, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inJustDecodeBounds = false;
            options.inPreferredConfig = useMin? 
            		Bitmap.Config.RGB_565 : Bitmap.Config.ARGB_8888;

            return BitmapFactory.decodeByteArray(byteArr, 0, count, options);

        } catch (Exception e) {
            Log.e(TAG, "Error Decoding Bitmap", e);
            return null;
        }
    }
	
	/**
	 * Calculate a sampling ratio based on the required width and height of our image
	 * @param options	The bitmap options 
	 * @param reqWidth	The required width
	 * @param reqHeight The required height
	 * @return			A sampling input size
	 */
	private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }


	    return inSampleSize;
	}
}
