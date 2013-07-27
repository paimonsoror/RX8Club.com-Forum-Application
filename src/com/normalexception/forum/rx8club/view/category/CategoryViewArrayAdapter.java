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
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;

public class CategoryViewArrayAdapter extends ArrayAdapter<CategoryView> {
	private Context activity;
	private List<CategoryView> data;

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
        
        CategoryView cv = data.get(position);
        
        ((TextView) vi.findViewById(R.id.cv_title)).setText(cv.getTitle());
        
        if(cv.getPostCount() == null || cv.getThreadCount() == null) {
        	setMode(vi, true);
        } else {
        	setMode(vi, false);
        	((TextView) vi.findViewById(R.id.cv_postCount)).setText(cv.getPostCount());
        	((TextView) vi.findViewById(R.id.cv_threadCount)).setText(cv.getThreadCount());
        }
        
        return vi;
	}
	
	private void setMode(View vi, boolean isTitle) {
		int showMode = isTitle? View.GONE : View.VISIBLE;
		int colorMode= isTitle? Color.DKGRAY : Color.GRAY;
		int textColor= isTitle? Color.WHITE : Color.BLACK;

		((TextView) vi.findViewById(R.id.cv_postCount)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.cv_postCount_label)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.cv_threadCount)).setVisibility(showMode);
    	((TextView) vi.findViewById(R.id.cv_threadCount_label)).setVisibility(showMode);
    	((ImageView)vi.findViewById(R.id.cv_image)).setVisibility(showMode);
    	vi.setBackgroundColor(colorMode);
    	
    	((TextView) vi.findViewById(R.id.cv_title)).setTextColor(textColor);
	}
}
