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
import java.util.Date;
import java.util.List;

import android.content.Context;

public class ViewListCache<T> {
	
	private File cacheDir;
	private Context context;
	private String CACHEFILENAME = null;
	private File cacheFile = null;
	
	private static final long MS_IN_DAY = 86400000;
	
	/**
	 * To help increase the speed of the app, we are going to go ahead
	 * and cache the view list.  The cache expires each day, so if
	 * the forums update it wont take long for the app to get the
	 * updates
	 * @param ctx		The source context
	 * @param filename	The name of the cache file
	 */
	public ViewListCache(Context ctx, String filename) {
		this.context = ctx;
		this.CACHEFILENAME = filename;
		
		//Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
        		android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(
            		android.os.Environment.getExternalStorageDirectory(),"LazyList");
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
	}
	
	/**
	 * Cache the contents of the list
	 * @param contents	The contents to cache
	 */
	public synchronized void cacheContents(List<T> contents) {
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
		  e.printStackTrace();
		} finally {
			if (f != null) try { f.close(); } catch (Exception e) {}
			if (os != null) try { os.close(); } catch (Exception e) {}
		}
	}
	
	/**
	 * Check if the cache is expired
	 * @return	True if cache is expired
	 */
	public boolean isCacheExpired() {
		boolean result = true;
		cacheFile = getCacheFile();
		if(cacheFile.exists()) {
			long today = new Date().getTime();
			if(cacheFile.lastModified() - today > MS_IN_DAY)
				result = true;
			else
				result = false;
		}
		return result;
	}
	
	/**
	 * Get the contents of the cached file
	 * @return	The contents of the cached file as a list
	 */
	public List<T> getCachedContents() {
		List<T> cv = null;
		try {
			File storageFile = getCacheFile();
			FileInputStream fis = new FileInputStream(storageFile);
			ObjectInputStream is = new ObjectInputStream(fis);
			cv = (List<T>) is.readObject();
			is.close();
		} catch (FileNotFoundException e) {
			// Its ok, first time there wont be a file
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cv;
	}
	
	/**
	 * Convenient method of grabbing the cache file
	 * @return	The cache file object
	 */
	private File getCacheFile() {
		if(cacheFile == null)
			cacheFile = new File(cacheDir, CACHEFILENAME);
		return cacheFile;
	}
}
