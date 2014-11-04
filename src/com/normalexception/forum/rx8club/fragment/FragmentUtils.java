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
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.fragment.category.CategoryFragment;
import com.normalexception.forum.rx8club.html.LoginFactory;
import com.normalexception.forum.rx8club.html.VBForumFactory;

public class FragmentUtils {
	
	private static Logger TAG =  LogManager.getLogger(FragmentUtils.class);

	public static void registerHandlerToViewObjects(OnClickListener och, ViewGroup vh) {
		View v = null;
        for(int i = 0; i < vh.getChildCount(); i++) {
            v = vh.getChildAt(i);
            if(v instanceof Button) v.setOnClickListener(och);
            if(v instanceof ImageView) v.setOnClickListener(och);
        }
	}
	
	/**
	 * Check if the user can create a new thread.  If not, report back a
	 * false boolean value
	 * @param address The page to check permission to
	 * @param params  Parameters to the url
	 * @return		  True if user has permission
	 */
	public static boolean doesUserHavePermissionToPage(Activity src, String address, String... params) {
		boolean result = false;
		for(String param : params)
			address += param;
		
		Document output = 
				VBForumFactory.getInstance().get(src,  address);
		Elements eles = null;
		
		eles = output.select("div[class=ib-padding]");
		Log.v(TAG, "doesUserHavePermissionToPage:Mobile Check = " + eles.size());
		if(eles.isEmpty()) {
			eles = output.select("td[class=panelsurround]");
			Log.v(TAG, "doesUserHavePermissionToPage:Standard Check = " + eles.size());
		}
		if(eles != null)
			result = !eles.text().contains("do not have permission to access this page");
		return result;
	}
	
	public static void returnToLoginPage(Activity src, boolean clearPrefs, boolean timeout) {
		LoginFactory.getInstance().logoff(clearPrefs);
		
		Fragment newFragment = new AboutFragment();		
		FragmentTransaction transaction = 
				((Activity)src).getFragmentManager().beginTransaction();

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		transaction.replace(R.id.content_frame, newFragment);

		// Commit the transaction
		transaction.commit();
	}
}
