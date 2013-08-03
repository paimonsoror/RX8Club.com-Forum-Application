package com.normalexception.forum.rx8club.view.category;

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
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.activities.list.CategoryActivity;
import com.normalexception.forum.rx8club.utils.SpecialNumberFormatter;
import com.normalexception.forum.rx8club.view.ViewHolder;

/**
 * Custom category view array adapter
 */
public class CategoryViewArrayAdapter extends ArrayAdapter<CategoryView> {
	private Context activity;
	private List<CategoryView> data;

	/**
	 * A custom adapter that handles Category View objects
	 * @param context				The source context
	 * @param textViewResourceId	The resource ID
	 * @param objects				The objects in the list
	 */
	public CategoryViewArrayAdapter(Context context, int textViewResourceId,
			List<CategoryView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}
	
	 /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override  
    public CategoryView getItem(int position) {     
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
            vi = vinf.inflate(R.layout.view_category, null);
        }
        
        final CategoryView cv = data.get(position);    
        
        ((TextView) ViewHolder.get(vi, R.id.cv_title)).setText(cv.getTitle());
        ((TextView) ViewHolder.get(vi, R.id.cv_desc)).setText(cv.getDescription());
        
        if(cv.getPostCount() == null || cv.getThreadCount() == null) {
        	setMode(vi, true);
        } else {
        	setMode(vi, false);
        	((TextView) ViewHolder.get(vi, R.id.cv_postCount)).setText(
        			SpecialNumberFormatter.collapseNumber(
        					Double.parseDouble(cv.getPostCount().replace(",","")), 0));
        	((TextView) ViewHolder.get(vi, R.id.cv_threadCount)).setText(
        			SpecialNumberFormatter.collapseNumber(
        					Double.parseDouble(cv.getThreadCount().replace(",","")), 0));
        	
        	// First we need to register a listener for a regular 
        	// click.  We simply launch the link here
        	vi.setOnClickListener(new OnClickListener() {
    			@Override
	            public void onClick(View v) {
					if(cv.getLink() != null) {
						Intent intent = 
								new Intent(activity, CategoryActivity.class);
						intent.putExtra("link", cv.getLink());
						activity.startActivity(intent);
					}
				}
        	});
        	
        	// If the forum has sub forums, we can register a 
        	// long click listener that will pop up a menu
        	if(cv.getSubCategories().size() > 0) {
        		((TextView) ViewHolder.get(vi, R.id.cv_subCount))
        			.setText(Integer.toString(cv.getSubCategories().size()));
        		vi.setOnLongClickListener(new OnLongClickListener() {
        	        @Override
        	        public boolean onLongClick(View v) {
        	        	SubCategoryDialog scd = 
        	        			new SubCategoryDialog(activity, cv.getSubCategories());
        	        	scd.registerToExecute();
        	        	scd.show();
        	        	return true;
        	        }
        		});
        	}
        	else {
        		((TextView) ViewHolder.get(vi, R.id.cv_subCount_label)).setVisibility(View.GONE);
        		((TextView) ViewHolder.get(vi, R.id.cv_subCount)).setVisibility(View.GONE);
        	}
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

		((TextView)  ViewHolder.get(vi, R.id.cv_postCount)).setVisibility(showMode);
		((TextView)  ViewHolder.get(vi, R.id.cv_postCount_label)).setVisibility(showMode);
		((TextView)  ViewHolder.get(vi, R.id.cv_threadCount)).setVisibility(showMode);
		((TextView)  ViewHolder.get(vi, R.id.cv_threadCount_label)).setVisibility(showMode);
		((TextView)  ViewHolder.get(vi, R.id.cv_subCount)).setVisibility(showMode);
		((TextView)  ViewHolder.get(vi, R.id.cv_subCount_label)).setVisibility(showMode);
		((ImageView) ViewHolder.get(vi, R.id.cv_image)).setVisibility(showMode);
    	vi.setBackgroundColor(colorMode);
    	
    	((TextView) ViewHolder.get(vi, R.id.cv_title)).setTextColor(textColor);
	}
}
