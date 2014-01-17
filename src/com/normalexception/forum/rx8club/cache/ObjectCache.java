package com.normalexception.forum.rx8club.cache;

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
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import android.content.Context;

import com.normalexception.forum.rx8club.Log;

/**
 * Basic object cache.  This extends the expirablecache class
 * by adding common methods for writing and reading generic
 * objects to class
 * @param <T>	The type of object we are going to read/write
 */
public class ObjectCache<T> extends ExpirableCache {
	
	private String TAG = this.getClass().getName();
	
	/**
	 * Constructor to an object cache
	 * @param ctx	The context
	 */
	public ObjectCache(Context ctx){
		super(ctx);
	}

	/**
	 * Cache the contents of the list
	 * @param contents	The contents to cache
	 */
	public synchronized void cacheContents(T contents) {
		FileOutputStream f = null;
		 ObjectOutputStream os = null;
		try {
		  cacheFile = getCacheFile();
		  f = new FileOutputStream(cacheFile);
		  os = new ObjectOutputStream(f);
		  os.writeObject(contents);
		  os.close();
		  f.close();
		} catch (Exception e) {
		  Log.e(TAG, e.getMessage(), e);
		} finally {
			if (f != null) try { f.close(); } catch (Exception e) {}
			if (os != null) try { os.close(); } catch (Exception e) {}
		}
	}
	
	/**
	 * Get the contents of the cached file
	 * @return	The contents of the cached file as a list
	 */
	@SuppressWarnings("unchecked")
	public T getCachedContents() {
		T cv = null;
		try {
			File storageFile = getCacheFile();
			FileInputStream fis = new FileInputStream(storageFile);
			ObjectInputStream is = new ObjectInputStream(fis);
			cv = (T) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			// Its ok, first time there wont be a file
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return cv;
	}
}
