package com.normalexception.app.rx8club.task;

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
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.dialog.NewVersionDialog;
import com.normalexception.app.rx8club.xml.UpdateXmlParser;
import com.normalexception.app.rx8club.xml.UpdateXmlParser.Entry;

public class ApplicationUpdateTask extends AsyncTask<String, Void, String> {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	private int gotId = -1;
	private String gotVersion = null, gotSummary = null, gotLink = null;
	private Context _ctx = null;
	private boolean user = false;
	
	private static final String UPDATE_PREF = "update_pref";
	
	/**
	 * Constructor to the update dialog
	 * @param ctx	The source context
	 */
	public ApplicationUpdateTask(Context ctx) {
		_ctx = ctx;
	}
	
	/**
	 * First we will check to see if the user requested this check
	 * or if the application was just started.  Then, we will check
	 * the server for updates
	 * @param	params[0]	The url to check for an update
	 * @param	params[1]	True if hte user requested an update check
	 */
    @Override
    protected String doInBackground(String... params) {
        try {
        	Log.d(TAG, "Checking for new version");
        	user = Boolean.parseBoolean(params[1]);
            return loadXmlFromNetwork(params[0]);
        } catch (IOException e) {
        	Log.e(TAG, "Error With Version Check", e);
        } catch (XmlPullParserException e) {
        	Log.e(TAG, "Error With Version Check", e);
        }
        
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
    	int storedId = getStoredId();
    	Log.v(TAG, String.format("Read stored ID %d From Shared Preferences", storedId));
    	if(!gotVersion.equals(MainApplication.getVersion()) && (storedId != gotId || user)) {
    		(new NewVersionDialog(_ctx, gotVersion, gotSummary, gotLink)).show();
    		Log.v(TAG, String.format("Storing ID %d Into Shared Preferences", gotId));
    		storedId(gotId);
    	} else {
    		if(user)
    			Toast.makeText(_ctx, "No Update Available", Toast.LENGTH_SHORT).show();
    	}
    }
    
    /**
     * Read the stored id which reflects the id of the last "update" message
     * that the server had.  This way we dont bug the user every time he
     * opens up the app if he already denied an update
     * @return	The stored id of the last update message
     */
    private int getStoredId() {
    	SharedPreferences prefs = _ctx.getSharedPreferences(UPDATE_PREF, 0); 
    	int restoredText = prefs.getInt("idName", -1);
    	return restoredText;
    }
    
    /**
     * Stored the integer value id of the last message into memory so that 
     * we dont keep popping up the message
     * @param id	The id we are storing in memory
     */
    private void storedId(final int id) {
    	SharedPreferences.Editor editor = 
    			_ctx.getSharedPreferences(UPDATE_PREF, 0).edit();
		editor.putInt("idName", id);
		editor.commit();
    }
    
    /**
     * Read the XML file from the network
     * @param urlString	The url to read update information from
     * @return			Entries list as a string which isn't used
     * @throws XmlPullParserException
     * @throws IOException
     */
    private String loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        UpdateXmlParser updateXml = new UpdateXmlParser();
        List<Entry> entries = null;
            
        try {
            stream = downloadUrl(urlString);        
            entries = updateXml.parse(stream);
            
            for(Entry e : entries) {
            	gotId      = e.id;
            	gotVersion = e.version;
            	gotSummary = e.summary;
            	gotLink    = e.link;
            }
        } finally {
            if (stream != null) {
                stream.close();
            } 
         }
        
        return entries.toString();
    }

    /**
     *  Given a string representation of a URL, sets up a connection and gets
     * @param urlString	The url we are reading the update information from
     * @return			An input stream object to parse
     * @throws IOException
     */
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}