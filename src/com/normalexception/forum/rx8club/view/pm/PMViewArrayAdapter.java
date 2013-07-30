package com.normalexception.forum.rx8club.view.pm;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;

public class PMViewArrayAdapter extends ArrayAdapter<PMView> {
	private Context activity;
	private List<PMView> data;

	/**
	 * A custom adapter that handles Category View objects
	 * @param context				The source context
	 * @param textViewResourceId	The resource ID
	 * @param objects				The objects in the list
	 */
	public PMViewArrayAdapter(Context context, int textViewResourceId,
			List<PMView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}
	
	 /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override  
    public PMView getItem(int position) {     
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
            vi = vinf.inflate(R.layout.view_pm, null);
        }
        
        PMView pm = data.get(position);
        
        ((TextView) vi.findViewById(R.id.pm_subject)).setText(pm.getTitle());
        
        if(pm.getUser() == null || pm.getDate() == null) {
        	setMode(vi, true);
        } else {
        	setMode(vi, false);
        	((TextView) vi.findViewById(R.id.pm_from)).setText(pm.getUser());
        	((TextView) vi.findViewById(R.id.pm_date)).setText(
        			String.format("%s, %s", pm.getDate(), pm.getTime())
        	);
        }
        
        return vi;
	}
	
	/**
	 * Set the mode of the category line
	 * @param vi		The view line
	 * @param isTitle	If we are going to represent a title
	 */
	private void setMode(View vi, boolean isTitle) {
		int showMode = isTitle? View.GONE : View.VISIBLE;
		int colorMode= isTitle? Color.DKGRAY : Color.GRAY;
		int textColor= isTitle? Color.WHITE : Color.BLACK;

		((TextView) vi.findViewById(R.id.pm_from)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.pm_fromlabel)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.pm_date)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.pm_datelabel)).setVisibility(showMode);
    	((ImageView)vi.findViewById(R.id.pm_image)).setVisibility(showMode);
    	vi.setBackgroundColor(colorMode);
    	
    	((TextView) vi.findViewById(R.id.pm_subject)).setTextColor(textColor);
	}
}