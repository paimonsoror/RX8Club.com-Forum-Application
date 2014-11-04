package com.normalexception.forum.rx8club.favorites;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.widget.Toast;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.view.thread.ThreadView;

/**
 * A factory that handles all of the favorites that the user
 * has saved in memory
 */
public class FavoriteFactory {
	
	private static FavoriteFactory _instance;
	private        FavoriteThreads _favorites;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	/**
	 * Singleton constructor.  Will load the 
	 * favoites list from memory if it exists, else
	 * it will create a new one
	 */
	protected FavoriteFactory() {
		if(!loadFromMemory())
			_favorites = new FavoriteThreads();
	}
	
	/**
	 * Get an instance of the factory, and create
	 * one if it doesn't already exist
	 * @return	An instance of the factory
	 */
	public static FavoriteFactory getInstance() {
		if(_instance == null)
			_instance = new FavoriteFactory();
		
		return _instance;
	}
	
	/**
	 * Report the favorites
	 * @return	A list of the favorites
	 */
	public FavoriteThreads getFavorites() {
		return _favorites;
	}
	
	/**
	 * Report the total number of favorites
	 * @return	The total number of favorites
	 */
	public int getCount() {
		return _favorites.size();
	}

	/**
	 * Load the favorites from memory
	 * @return	True if data loaded
	 */
	private boolean loadFromMemory() {
		boolean rtn = false;
		try {
			Log.v(TAG, "Checking for favorites file In memory");
			File storageFile = getFavoritesStorageDir();
			FileInputStream fis = new FileInputStream(storageFile);
			ObjectInputStream is = new ObjectInputStream(fis);
			_favorites = (FavoriteThreads) is.readObject();
			is.close();
			rtn = true;
		} catch (FileNotFoundException e) {
			// Its ok, first time there wont be a file
			Log.v(TAG, "No favorites file cache exists");
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return rtn;
	}
	
	/**
	 * Save the favorites to memory
	 */
	private void saveToMemory() {
		FileOutputStream outputStream;
		Context ctx = MainApplication.getAppContext();
		try {
		  Log.v(TAG, "Saving favorites document to memory");
		  outputStream = ctx.openFileOutput(
				  MainApplication.getAppContext().getString(R.string.file_favorites), 
				  Context.MODE_PRIVATE);
		  ObjectOutputStream os = new ObjectOutputStream(outputStream);
		  os.writeObject(_favorites);
		  os.close();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		}
	}
	
	/**
	 * Add a favorite to the list
	 * @param thread	The thread to add
	 */
	public void addFavorite(ThreadView thread) {
		Log.v(TAG, String.format("Adding %s to favorites", thread.getTitle()));
		_favorites.add(thread);
		saveToMemory();
		Toast.makeText(
				MainApplication.getAppContext(), 
				R.string.dialogFavoriteAdded, 
				Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Remove a favorite from our list and then
	 * save the data
	 * @param thread	The thread to remove
	 */
	public void removeFavorite(ThreadView thread) {
		for(ThreadView thr : _favorites){
			if(thr.getTitle().equals(thread.getTitle())) {
				_favorites.remove(thr);
				break;
			}
		}
		this.saveToMemory();
	}
	
	/**
	 * Get the storage directory for the application
	 * @return	The favorites file on the internal memory
	 */
	private File getFavoritesStorageDir() {
		Context ctx = MainApplication.getAppContext(); 
	    File file = new File(ctx.getFilesDir(), "favorites.dat");
	    return file;
	}
}
