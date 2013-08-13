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

import android.content.Context;

import com.normalexception.forum.rx8club.R;

public class Cache {
	protected File cacheDir = null;
	
	/**
	 * Report the location of the external cache
	 * @param context	The source context
	 * @return			The location of the external cache
	 */
	protected File getExternalCache(Context context) {
		File cacheDir = null;
		//Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(
        		android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(
            		android.os.Environment.getExternalStorageDirectory(),
            		context.getString(R.string.folder_rx8club));
        else
            cacheDir=context.getCacheDir();
        if(!cacheDir.exists())
            cacheDir.mkdirs();
        
        return cacheDir;
	}
	 
	/**
	 * Clear the cache directory
	 */
    public void clear() {
        File[] files = cacheDir.listFiles();
        if(files==null)
            return;
        for(File f:files)
            f.delete();
    }
}
