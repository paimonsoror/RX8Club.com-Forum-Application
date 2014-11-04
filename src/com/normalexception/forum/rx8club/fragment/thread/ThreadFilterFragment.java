package com.normalexception.forum.rx8club.fragment.thread;

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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.dialog.FilterDialog;
import com.normalexception.forum.rx8club.filter.ThreadFilter;
import com.normalexception.forum.rx8club.filter.ThreadFilterFactory;
import com.normalexception.forum.rx8club.state.AppState;
import com.normalexception.forum.rx8club.view.thread.ThreadRuleViewArrayAdapter;

public class ThreadFilterFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private ListView lv;
	private ThreadRuleViewArrayAdapter cva;
	private final int NEW_FILTER = 5000;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);    	
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			MainApplication.setState(AppState.State.THREAD, this);

			Log.v(TAG, "Thread Filter Activity Started");
			
			lv = (ListView)view.findViewById(R.id.mainlistview);
			
			Button bv = new Button(getActivity());
	        bv.setId(NEW_FILTER);
	        bv.setOnClickListener(new ThreadFilterListener());
	        bv.setText("New Filter");
	        bv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
	        lv.addFooterView(bv);
	        
	        updateList();
		} catch (Exception e) {
			Log.e(TAG, "Fatal Error In Thread Activity! " + e.getMessage(), e);
		}
	}
	
	/**
	 * Update the view's list with the appropriate data
	 */
	private void updateList() {
    	getActivity().runOnUiThread(new Runnable() {
            public void run() {
            	final ListView lv = (ListView)getView().findViewById(R.id.mainlistview);
		    	cva = new ThreadRuleViewArrayAdapter(getActivity(), R.layout.view_rule, 
		    			ThreadFilterFactory.getInstance().getThreadFilters());
				lv.setAdapter(cva);
				lv.setOnItemClickListener(new OnItemClickListener() {
		            @Override
		            public void onItemClick(AdapterView<?> parent, View view,
		                    int position, long id) {
		            	final ThreadFilter item = cva.getItem(position);
		            	new AlertDialog.Builder(getActivity())
		            	.setMessage(String.format("Remove Filter: %s?", item.getSubject()))
		            	.setIcon(android.R.drawable.ic_dialog_alert)
		            	.setPositiveButton(android.R.string.yes, 
		            			new DialogInterface.OnClickListener() {
				            	    public void onClick(DialogInterface dialog, int whichButton) {						            	
						            	cva.remove(item);
						            	ThreadFilterFactory.getInstance().removeFilter(item);
				            	    }}
		            			)
		            	 .setNegativeButton(android.R.string.no, null).show();
		            }
		        });
            }
    	});
	}
	
	class ThreadFilterListener implements OnClickListener {
		/*
		 * (non-Javadoc)
		 * @see com.normalexception.forum.rx8club.activities.ForumBaseActivity#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {
			switch(arg0.getId()) {
				case NEW_FILTER:
					FilterDialog fd = new FilterDialog(getActivity());
					fd.show();
				break;
			}
		}
	}
}
