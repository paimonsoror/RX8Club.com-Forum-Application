package com.normalexception.forum.rx8club.view.threaditem;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.thread.NewThreadActivity;

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
        
        ((Button)vi.findViewById(R.id.newThreadButton))
        	.setOnClickListener((NewThreadActivity)activity);
               
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
