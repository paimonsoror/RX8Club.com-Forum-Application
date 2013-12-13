package com.normalexception.forum.rx8club.view.thread;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.filter.ThreadFilter;
import com.normalexception.forum.rx8club.view.ViewHolder;

public class ThreadRuleViewArrayAdapter extends ArrayAdapter<ThreadFilter> {

	private Context activity;
    private List<ThreadFilter> data; 
    
    private TextView vSubject 	= null;
    
	public ThreadRuleViewArrayAdapter(Context context, int textViewResourceId,
			List<ThreadFilter> objects) {
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
    public ThreadFilter getItem(int position) {     
        return data.get(position - 1 /* Compensate for our PTR view */);  
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
            vi = vinf.inflate(R.layout.view_rule, null);
        }
        
        ThreadFilter tr = data.get(position);
        
        vSubject = (TextView) ViewHolder.get(vi,R.id.rule_subject);
        vSubject.setText(tr.getRule().toString() + " : " + tr.getSubject());
        
        return vi;
	}
}
