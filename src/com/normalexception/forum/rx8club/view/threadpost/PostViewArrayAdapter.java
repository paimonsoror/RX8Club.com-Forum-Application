package com.normalexception.forum.rx8club.view.threadpost;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.normalexception.forum.rx8club.R;

public class PostViewArrayAdapter extends ArrayAdapter<PostView> {
	private Context activity;
	private List<PostView> data;

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
