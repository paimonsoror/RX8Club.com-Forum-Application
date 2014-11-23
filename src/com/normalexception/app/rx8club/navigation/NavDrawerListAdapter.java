package com.normalexception.app.rx8club.navigation;

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
        		(LoginFactory.getInstance().isGuestMode() || 
        				!LoginFactory.getInstance().isLoggedIn())) {
			Log.d(TAG, "Loading Guest Mode Menu Options");
			convertView = null;
			LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.null_item, parent, false);
            return convertView;
		} else {
			Log.d(TAG, "Loading User Mode Menu Options");
	        LayoutInflater mInflater = (LayoutInflater)
	                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        convertView = mInflater.inflate(R.layout.drawer_list_item, parent, false);
	         
	        ImageView imgIcon = (ImageView) ViewHolder.get(convertView, R.id.drawer_icon);
	        TextView txtTitle = (TextView) ViewHolder.get(convertView, R.id.drawer_title);
	        
	        imgIcon.setImageResource(navDrawerItems.get(position).getIcon());        
	        txtTitle.setText(navDrawerItems.get(position).getTitle());
	        
	        return convertView;
		}
	}

}