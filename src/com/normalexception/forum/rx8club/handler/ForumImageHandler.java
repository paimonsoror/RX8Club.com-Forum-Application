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

import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.View;

/**
 * Handler designed to display the images within a textview
 */
public class ForumImageHandler implements ImageGetter {
	Context c;
	View container;

	/**
	 * Construct the URLImageParser which will execute 
	 * AsyncTask and refresh the container
	 * @param t	The source view
	 * @param c	The source context
	 */
	public ForumImageHandler(View t, Context c) {
		this.c = c;
		this.container = t;
	}

	/**
	 * Grab the reference to a URLDrawable which will
	 * be updated with images.
	 * @param	source	The source of the image
	 */
	public Drawable getDrawable(String source) {
		URLDrawable urlDrawable = new URLDrawable();

		// get the actual source
		ImageGetterAsyncTask asyncTask = 
				new ImageGetterAsyncTask( urlDrawable);

		asyncTask.execute(source);

		return urlDrawable;
	}

	/**
	 * Inner class designed to grab an image async.
	 */
	public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable>  {
		URLDrawable urlDrawable;

		public ImageGetterAsyncTask(URLDrawable d) {
			this.urlDrawable = d;
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Drawable doInBackground(String... params) {
			String source = params[0];
			return fetchDrawable(source);
		}

		/*
		 * (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Drawable result) {
			// set the correct bound according to the result from HTTP call
			Log.d("height",""+result.getIntrinsicHeight());
			Log.d("width",""+result.getIntrinsicWidth());
			urlDrawable.setBounds(0, 0, 0+result.getIntrinsicWidth(), 0+result.getIntrinsicHeight()); 

			// change the reference of the current drawable to the result
			// from the HTTP call
			urlDrawable.drawable = result;

			// redraw the image by invalidating the container
			ForumImageHandler.this.container.invalidate();
		}

		/***
		 * Get the Drawable from URL
		 * @param urlString	The url to grab the image from
		 * @return			The drawable container that holds the image
		 */
		public Drawable fetchDrawable(String urlString) {
			try {
				URL aURL = new URL(urlString);
				final URLConnection conn = aURL.openConnection(); 
				conn.connect(); 
				final BufferedInputStream bis = new BufferedInputStream(conn.getInputStream()); 
				final Bitmap bm = BitmapFactory.decodeStream(bis);
				Drawable drawable = new BitmapDrawable(bm);
				drawable.setBounds(0,0,bm.getWidth(),bm.getHeight());
				return drawable;
			} catch (Exception e) {
				return null;
			} 
		}
	}
}