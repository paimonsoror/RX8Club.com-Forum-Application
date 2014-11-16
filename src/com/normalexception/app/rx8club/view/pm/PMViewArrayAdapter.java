package com.normalexception.app.rx8club.view.pm;

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

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.normalexception.app.rx8club.Log;

public class PMViewArrayAdapter extends ArrayAdapter<PMModel> {
	private List<PMModel> data;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());

	/**
	 * A custom adapter that handles PM View objects
	 * @param context				The source context
	 * @param textViewResourceId	The resource ID
	 * @param objects				The objects in the list
	 */
	public PMViewArrayAdapter(Fragment context, int textViewResourceId,
			List<PMModel> objects) {
		super(context.getActivity(), textViewResourceId, objects);
		data = objects;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getCount()
	 */
	@Override
	public int getCount() {
		return data == null? 0 : data.size();
	}
	
	 /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override  
    public PMModel getItem(int position) {     
        return data.get(position);  
    } 
    
    /*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {		
		PMView pmView = (PMView)convertView;
        if (null == pmView) {
        	Log.d(TAG, "Inflating New PMView");
        	pmView = PMView.inflate(parent);
        }
        pmView.setPM(getItem(position));
        return pmView;
	}
}