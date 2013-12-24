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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import ch.boye.httpclientandroidlib.HttpResponse;
import ch.boye.httpclientandroidlib.client.HttpClient;
import ch.boye.httpclientandroidlib.client.methods.HttpGet;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.cache.FileCache;
import com.normalexception.forum.rx8club.cache.MemoryCache;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.httpclient.ClientUtils;
import com.normalexception.forum.rx8club.utils.Utils;

public class ImageLoader {

	private MemoryCache memoryCache = new MemoryCache();
	private FileCache fileCache;
	private Map<ImageView, String> imageViews = 
			Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	private ExecutorService executorService; 
	private final String TAG = "ImageLoader";

	// The default icon should an image not be found
	private final int stub_id = R.drawable.rotor_icon;
	
	/**
	 * Constructor
	 * @param context	Source context
	 */
	public ImageLoader(Context context){
		fileCache=new FileCache(context);
		executorService=Executors.newFixedThreadPool(5);
	}

	/**
	 * Once we get the image, we actually want to display it
	 * @param url		The url of the image
	 * @param imageView	The view to put the image in
	 */
	public void DisplayImage(String url, ImageView imageView)
	{
		imageViews.put(imageView, url);
		Bitmap bitmap=memoryCache.get(url);
		if(bitmap!=null)
			imageView.setImageBitmap(bitmap);
		else
		{
			queuePhoto(url, imageView);
			imageView.setImageResource(stub_id);
		}
	}

	/**
	 * Queue up the next photo to load
	 * @param url		The url of hte photo
	 * @param imageView	The view to load it to
	 */
	private void queuePhoto(String url, ImageView imageView)
	{
		PhotoToLoad p=new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	/**
	 * Grab the image url as a bitmap
	 * @param url	The url to grab the bitmap from
	 * @return		The image as a bitmap object
	 */
	private Bitmap getBitmap(String url)
	{
		File f=fileCache.getFile(url);

		// First check our cache to see if the
		// image exists
		Bitmap b = decodeFile(f);
		if(b!=null)
			return b;

		// If we got here, the image wasn't cached
		try {
			Bitmap bitmap=null;
			HttpClient httpclient = 
					LoginFactory.getInstance().getClient();
			HttpGet hget = ClientUtils.getHttpGet(url);
			HttpResponse response = 
					httpclient.execute(hget, LoginFactory.getInstance().getHttpContext());

			InputStream is = response.getEntity().getContent();
			OutputStream os = new FileOutputStream(f);
			Utils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
		} catch (IllegalStateException iex) {
			// No big deal, the image doesn't exist
			return null;
		} catch (Exception ex){
			Log.e(TAG, ex.getMessage(), ex);
			return null;
		}
	}

	/**
	 * Decode the image from memory, and reduce the file size
	 * @param f	The file
	 * @return	The image as a bitmap
	 */
	private Bitmap decodeFile(File f){
		try {
			//decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f),null,o);

			//Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE=70;
			int width_tmp=o.outWidth, height_tmp=o.outHeight;
			int scale=1;
			while(true){
				if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
					break;
				width_tmp/=2;
				height_tmp/=2;
				scale*=2;
			}

			//decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize=scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {}
		return null;
	}

	//Task for the queue
	private class PhotoToLoad
	{
		public String url;
		public ImageView imageView;
		public PhotoToLoad(String u, ImageView i){
			url=u;
			imageView=i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;
		PhotosLoader(PhotoToLoad photoToLoad){
			this.photoToLoad=photoToLoad;
		}

		@Override
		public void run() {
			if(imageViewReused(photoToLoad))
				return;
			Bitmap bmp=getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if(imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
			Activity a=(Activity)photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad){
		String tag=imageViews.get(photoToLoad.imageView);
		if(tag==null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	//Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable
	{
		Bitmap bitmap;
		PhotoToLoad photoToLoad;
		public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
		public void run()
		{
			if(imageViewReused(photoToLoad))
				return;
			if(bitmap!=null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageResource(stub_id);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

}
