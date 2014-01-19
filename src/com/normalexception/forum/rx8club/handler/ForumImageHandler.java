package com.normalexception.forum.rx8club.handler;

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
import java.net.MalformedURLException;

import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.bitmap.RegisteredBitmap;
import com.normalexception.forum.rx8club.utils.Utils;

/**
 * Handler designed to display the images within a textview
 */
public class ForumImageHandler implements ImageGetter {
	private Context c;
	private TextView container;
	private int id;
	
	private final Logger TAG = Logger.getLogger(this.getClass());

	/**
	 * Construct the URLImageParser which will execute 
	 * AsyncTask and refresh the container
	 * @param t	The source view
	 * @param d 
	 * @param c	The source context
	 */
	public ForumImageHandler(TextView t, int d, Context c) {
		this.c = c;
		this.container = t;
		this.id = d;
	}

	/**
	 * Grab the reference to a URLDrawable which will
	 * be updated with images.
	 * @param	source	   The source of the image
	 * @param   onlyOpaque True if the image will only be opaque
	 */
	public Drawable getDrawable(String source) {
		LevelListDrawable d = new LevelListDrawable();
        Drawable empty = c.getResources().getDrawable(R.drawable.placeholder_50);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());

        new ImageGetterAsyncTask().execute(source, d);

        return d;
	}

	/**
	 * Inner class designed to grab an image async.
	 */
	public class ImageGetterAsyncTask extends AsyncTask<Object, Void, Bitmap>  {
		private LevelListDrawable mDrawable;

		@Override
		protected void onPreExecute() {			
        	
		}
		
        @Override
        protected Bitmap doInBackground(Object... params) {
            String source = (String) params[0];
            source = Utils.resolveUrl(source);
            mDrawable = (LevelListDrawable) params[1];
            try {
                return (new RegisteredBitmap(id, source, false)).getBitmap();
            } catch (FileNotFoundException e) {
            	Log.e(TAG, e.getMessage(), e);
            } catch (MalformedURLException e) {
            	Log.e(TAG, e.getMessage(), e);
            } catch (IOException e) {
            	Log.e(TAG, e.getMessage(), e);
            } catch (OutOfMemoryError e) {
            	Log.e(TAG, e.getMessage(), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            Log.d(TAG, "Post processing bitmap " + bitmap);
            if (bitmap != null) {
            	int newSize[] = {bitmap.getWidth(), bitmap.getHeight()};
            	while(newSize[0] >= 400) {
            		newSize[0] /= 2;
            		newSize[1] /= 2;
            	}
            	bitmap = Bitmap.createScaledBitmap(bitmap, newSize[0], newSize[1], false);
                BitmapDrawable d = new BitmapDrawable(bitmap);
                mDrawable.addLevel(1, 1, d);
                mDrawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
                mDrawable.setLevel(1);
                // i don't know yet a better way to refresh TextView
                // mTv.invalidate() doesn't work as expected
                CharSequence t = container.getText();
                container.setText(t);
            }
        }
	}
}