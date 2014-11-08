package com.normalexception.forum.rx8club.fragment;

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

import android.support.v4.app.Fragment;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.MainApplication;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.state.AppState;

/**
 * Activity used to display information about the application.
 * 
 * Required Intent Parameters:
 * None
 */
public class AboutFragment extends Fragment {

	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.view_about, container, false);
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainApplication.setState(AppState.State.ABOUT, this);
        TextView version = ((TextView)view.findViewById(R.id.versionNumber));
        try {
			version.setText("Version Number: " + 
					getActivity().getPackageManager().getPackageInfo(
							getActivity().getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "Version Not Found", e);
		}
    }
}
