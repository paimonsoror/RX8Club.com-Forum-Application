package com.normalexception.forum.rx8club.filter;

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
import java.util.ArrayList;
import java.util.List;

import android.content.Context;

import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;

/**
 * A factory that handles all of the favorites that the user
 * has saved in memory
 */
public class ThreadFilterFactory {
	
	private static ThreadFilterFactory _instance;
	private        List<ThreadFilter>  _filters;
	
	/**
	 * Singleton constructor.  Will load the 
	 * favoites list from memory if it exists, else
	 * it will create a new one
	 */
	protected ThreadFilterFactory() {
		if(!loadFromMemory())
			_filters = new ArrayList<ThreadFilter>();
	}
	
	/**
	 * Get an instance of the factory, and create
	 * one if it doesn't already exist
	 * @return	An instance of the factory
	 */
	public static ThreadFilterFactory getInstance() {
		if(_instance == null)
			_instance = new ThreadFilterFactory();
		
		return _instance;
	}
	
	/**
	 * True if there are filters available
	 * @return	True if filters exist
	 */
	public boolean hasFilters() {
		return _filters.size() > 0;
	}
	
	/**
	 * Report the filters
	 * @return	A list of the favorites
	 */
	public List<ThreadFilter> getThreadFilters() {
		return _filters;
	}

	/**
	 * Load the filters from memory
	 * @return	True if data loaded
	 */
	@SuppressWarnings("unchecked")
	private boolean loadFromMemory() {
		boolean rtn = false;
		try {
			File storageFile = getFilterStorageDir();
			FileInputStream fis = new FileInputStream(storageFile);
			ObjectInputStream is = new ObjectInputStream(fis);
			_filters = (ArrayList<ThreadFilter>) is.readObject();
			is.close();
			rtn = true;
		} catch (FileNotFoundException e) {
			// Its ok, first time there wont be a file
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
	/**
	 * Save the filters to memory
	 */
	private void saveToMemory() {
		FileOutputStream outputStream;
		Context ctx = MainApplication.getAppContext();
		try {
		  outputStream = ctx.openFileOutput(
				  MainApplication.getAppContext().getString(R.string.file_filtercache), 
				  Context.MODE_PRIVATE);
		  ObjectOutputStream os = new ObjectOutputStream(outputStream);
		  os.writeObject(_filters);
		  os.close();
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
	
	/**
	 * Add a filters to the list
	 * @param thread	The thread to add
	 */
	public void addFilter(ThreadFilter thread) {
		_filters.add(thread);
		saveToMemory();
	}
	
	/**
	 * Remove a filters from our list and then
	 * save the data
	 * @param thread	The thread to remove
	 */
	public void removeFilter(ThreadFilter thread) {
		int index = 0;
		for(ThreadFilter mThread : _filters) {
			if(mThread.getRule() == thread.getRule() && 
					mThread.getSubject().equals(thread.getSubject()))
				_filters.remove(index);
			index++;
		}
		this.saveToMemory();
	}
	
	/**
	 * Get the storage directory for the application
	 * @return	The filters file on the internal memory
	 */
	private File getFilterStorageDir() {
		Context ctx = MainApplication.getAppContext(); 
	    File file = new File(ctx.getFilesDir(), 
	    		MainApplication.getAppContext().getString(R.string.file_filtercache));
	    return file;
	}
}
