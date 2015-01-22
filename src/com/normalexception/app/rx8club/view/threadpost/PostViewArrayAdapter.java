package com.normalexception.app.rx8club.view.threadpost;

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.normalexception.app.rx8club.Log;

/**
 * Custom adapter to handle PostView objects
 */
public class PostViewArrayAdapter extends ArrayAdapter<PostModel> {
	private List<PostModel> data;
	
	private OnClickListener listener;
	
	private Logger TAG =  LogManager.getLogger(this.getClass());

	/**
	 * Custom adapter to handle PostView's
	 * @param context				The source context
	 * @param textViewResourceId	The resource id
	 * @param objects				The list of objects
	 */
	public PostViewArrayAdapter(Fragment context, int textViewResourceId,
			List<PostModel> objects, OnClickListener listener) {
		super(context.getActivity(), textViewResourceId, objects);
		this.data = objects;
		this.listener = listener;
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
    public PostModel getItem(int position) {     
        return data.get(position);  
    } 
    
    /*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {	
        PostView postView = (PostView)convertView;
        if (null == postView) {
        	Log.d(TAG, "Inflating New PostView");
        	postView = PostView.inflate(parent);
        }
        postView.setPost(getItem(position), position, listener);
        return postView;
	}
}
