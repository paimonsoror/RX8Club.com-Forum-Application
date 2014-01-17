package com.normalexception.forum.rx8club.activities.fragments;

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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.dialog.EmoticonDialog;
import com.normalexception.forum.rx8club.html.VBForumLocale;
import com.normalexception.forum.rx8club.html.VBForumLocale.Style;
import com.normalexception.forum.rx8club.view.ViewHolder;

/**
 * Styler found on the threads
 */
public class StylerFragment extends Fragment implements OnClickListener {
	
	private String TAG = this.getClass().getName();

    public static int RESULT_LOAD_IMAGE = 1;
    
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Inflate our fragment
        View view = inflater.inflate(R.layout.fragment_styler, container, false);        
        return view;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ViewHolder.get(getView(), R.id.boldButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.italicButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.underlineButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.linkCodeButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.imageCodeButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.quoteCodeButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.attachButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.emoticonButton).setOnClickListener(this);
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		TextView tv = 
				(TextView)ViewHolder.get(arg0.getRootView(), R.id.postBox);
		
		if(tv == null) {
			Log.e(TAG, "TextView is null...", null);
			return;
		}
		
		String val = "";
		
		switch(arg0.getId()) {
		case R.id.boldButton:
			val = VBForumLocale.getStyle(Style.BOLD);
			break;
		case R.id.italicButton:
			val = VBForumLocale.getStyle(Style.ITALIC);
			break;
		case R.id.underlineButton:
			val = VBForumLocale.getStyle(Style.UNDERLINE);
			break;
		case R.id.linkCodeButton:
			val = VBForumLocale.getStyle(Style.URL);
			break;
		case R.id.imageCodeButton:
			val = VBForumLocale.getStyle(Style.IMAGE);
			break;
		case R.id.quoteCodeButton:
			val = VBForumLocale.getStyle(Style.QUOTE);
			break;
		case R.id.emoticonButton:
			EmoticonDialog ed = new EmoticonDialog(getActivity(), tv);
			ed.show();
			break;
		case R.id.attachButton:
			/*Intent i = new Intent(
					Intent.ACTION_PICK, 
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);						 
			startActivityForResult(i, RESULT_LOAD_IMAGE);*/
			Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            getActivity().startActivityForResult(Intent.createChooser(intent,
                    "Select Picture"), RESULT_LOAD_IMAGE);
			break;
		}
		
		if(!val.equals(""))
			tv.setText(tv.getText().toString() + val);
	}

}