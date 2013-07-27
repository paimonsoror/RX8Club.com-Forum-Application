package com.normalexception.forum.rx8club.view.threadpost;

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
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;

/**
 * Custom adapter to handle PostView objects
 */
public class PostViewArrayAdapter extends ArrayAdapter<PostView> {
	private Context activity;
	private List<PostView> data;

	/**
	 * Custom adapter to handle PostView's
	 * @param context				The source context
	 * @param textViewResourceId	The resource id
	 * @param objects				The list of objects
	 */
	public PostViewArrayAdapter(Context context, int textViewResourceId,
			List<PostView> objects) {
		super(context, textViewResourceId, objects);
		activity = context;
		data = objects;
	}
	
	 /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getItem(int)
     */
    @Override  
    public PostView getItem(int position) {     
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
            vi = vinf.inflate(R.layout.view_newreply, null);
        }
        
        PostView cv = data.get(position);
        
        ((TextView)vi.findViewById(R.id.nr_username)).setText(cv.getUserName());
        ((TextView)vi.findViewById(R.id.nr_userTitle)).setText(cv.getUserTitle());
        ((TextView)vi.findViewById(R.id.nr_userPosts)).setText(cv.getUserPostCount());
        ((TextView)vi.findViewById(R.id.nr_userJoin)).setText(cv.getJoinDate());
        ((TextView)vi.findViewById(R.id.nr_postDate)).setText(cv.getPostDate());
        ((TextView)vi.findViewById(R.id.nr_postText)).setText(Html.fromHtml(cv.getUserPost()));
    	
        return vi;
	}
}
