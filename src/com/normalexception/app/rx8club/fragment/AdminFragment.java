package com.normalexception.app.rx8club.fragment;

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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.dialog.DeleteThreadDialog;
import com.normalexception.app.rx8club.fragment.thread.ThreadFragment;
import com.normalexception.app.rx8club.task.AdminTask;
import com.normalexception.app.rx8club.view.ViewHolder;

public class AdminFragment extends Fragment implements OnClickListener {
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	private	ThreadFragment ta = null;
	
	public static AdminFragment newInstance() {
		AdminFragment af = new AdminFragment();
		return af;
	}
    
	/*
	 * (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	// Inflate our fragment
        View view = inflater.inflate(R.layout.fragment_admin, container, false);
        ta = (ThreadFragment) getParentFragment();
        return view;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
    	super.onActivityCreated(savedInstanceState);
    	ViewHolder.get(getView(), R.id.threadCopyButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.threadDeleteButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.threadLockButton).setOnClickListener(this);
    	ViewHolder.get(getView(), R.id.threadMoveButton).setOnClickListener(this);
    }
    
    /*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View arg0) {
		Log.d(TAG, String.format("sid:%s, tid:%s", ta.getSecurityToken(), ta.getThreadNumber()));
		AdminTask lt = null;
		
		switch(arg0.getId()) {
		case R.id.threadCopyButton:
			Log.d(TAG, "Thread Copy Button Pressed");
			break;
		case R.id.threadDeleteButton:
			Log.d(TAG, "Thread Delete Button Pressed");
			lt = new AdminTask(ta, ta.getSecurityToken(), ta.getThreadNumber(), AdminTask.DELETE_THREAD);
			break;
		case R.id.threadLockButton:
			Log.d(TAG, "Thread Lock Button Pressed");
			lt = new AdminTask(ta, ta.getSecurityToken(), ta.getThreadNumber(), AdminTask.LOCK_THREAD);
			break;
		case R.id.threadMoveButton:
			Log.d(TAG, "Thread Move Button Pressed");
			lt = new AdminTask(ta, ta.getSecurityToken(), ta.getThreadNumber(), AdminTask.MOVE_THREAD);
			break;
		}
		
		if(lt != null) {
			final AdminTask flt = lt;
			final String desc = lt.getDescription();
			final Activity sourceActivity = this.getActivity();
			DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			    @Override
			    public void onClick(DialogInterface dialog, int which) {
			        switch (which){
			        case DialogInterface.BUTTON_POSITIVE:
			        	if(flt.getType() == AdminTask.DELETE_THREAD) {
			    			DeleteThreadDialog dtd = new DeleteThreadDialog(sourceActivity, flt);
			    			dtd.show();
			        	} else {        	
			        		flt.execute();
			        	}
			            break;
			        }
			    }
			};

			AlertDialog.Builder builder = 
					new AlertDialog.Builder(ta.getActivity());
			builder
				.setMessage("Are you sure you want to " + desc + "?")
				.setPositiveButton("Yes", dialogClickListener)
			    .setNegativeButton("No", dialogClickListener)
			    .show();
		}
	}
}
