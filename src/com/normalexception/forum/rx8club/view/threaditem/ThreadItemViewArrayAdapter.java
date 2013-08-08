package com.normalexception.forum.rx8club.view.threaditem;

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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.thread.EditPostActivity;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;
import com.normalexception.forum.rx8club.view.ViewHolder;

public class ThreadItemViewArrayAdapter extends ArrayAdapter<ThreadItemView> {
	private Context activity;
	private List<ThreadItemView> data;

	/**
	 * Custom adapter to handle PMItemView's
	 * @param context				The source context
	 * @param textViewResourceId	The resource id
	 * @param objects				The list of objects
	 */
	public ThreadItemViewArrayAdapter(Context context, int textViewResourceId,
			List<ThreadItemView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
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
    public ThreadItemView getItem(int position) {     
        return data.get(position);  
    } 
    
    /*
	 * (non-Javadoc)
	 * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
	 */
	public View getView(int position, View convertView, ViewGroup parent) {		
		View vi = convertView;
        if(vi == null) {
        	LayoutInflater vinf =
                    (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            vi = vinf.inflate(R.layout.view_newthread, null);
        }
        
        // Because we use this class when handling edits, we need to make
        // sure that we cast this properly and then rename our submit button
        if(activity instanceof NewThreadActivity) {
        	((Button) ViewHolder.get(vi,R.id.newThreadButton))
        		.setOnClickListener((NewThreadActivity)activity);
        } else if (activity instanceof EditPostActivity) {
        	((Button) ViewHolder.get(vi,R.id.newThreadButton))
    			.setOnClickListener((EditPostActivity)activity);
        	((Button) ViewHolder.get(vi,R.id.newThreadButton))
        		.setText("Submit Changes");
        	
        	ThreadItemView ti = getItem(position);
        	((EditText) ViewHolder.get(vi,R.id.postPost))
        		.setText(ti.getPost());
        }
               
        return vi;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#areAllItemsEnabled()
	 */
	@Override
	public boolean  areAllItemsEnabled() {
	    return true;			
	}

	/*
	 * (non-Javadoc)
	 * @see android.widget.BaseAdapter#isEnabled(int)
	 */
	@Override
	public boolean isEnabled(int position) {
	        return false;
	}
}
