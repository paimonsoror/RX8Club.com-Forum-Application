package com.normalexception.app.rx8club.navigation;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.view.ViewHolder;

public class NavDrawerListAdapter extends BaseAdapter {
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private Context context;
	private ArrayList<NavDrawerItem> navDrawerItems;
	
	public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems){
		this.context = context;
		this.navDrawerItems = navDrawerItems;
	}

	@Override
	public int getCount() {
		return navDrawerItems.size();
	}

	@Override
	public Object getItem(int position) {		
		return navDrawerItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "Grabbing Slide Navigation View");
		if(!navDrawerItems.get(position).isGuestEnabled() && 
        		LoginFactory.getInstance().isGuestMode()) {
			convertView = null;
			LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.null_item, parent, false);
            return convertView;
		} else {
			if (convertView == null) {
	            LayoutInflater mInflater = (LayoutInflater)
	                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
	        }
	         
	        ImageView imgIcon = (ImageView) ViewHolder.get(convertView, R.id.drawer_icon);
	        TextView txtTitle = (TextView) ViewHolder.get(convertView, R.id.drawer_title);
	         
	        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
	        txtTitle.setText(navDrawerItems.get(position).getTitle());
	        
	        return convertView;
		}
	}

}