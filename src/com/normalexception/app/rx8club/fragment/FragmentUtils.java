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

import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.html.LoginFactory;
import com.normalexception.app.rx8club.html.VBForumFactory;

public class FragmentUtils {
	
	private static Logger TAG =  LogManager.getLogger(FragmentUtils.class);
	
	public static enum LogoutReason {
		TIMEOUT,
		USER,
		ERROR
	}
	
	/**
	 * Dump arguments that were passed into a fragment for debug purposes
	 * @param args	Fragment argument bundle
	 */
	public static void dumpArgs(Bundle args) {
		Set<String> k_args = args.keySet();
		Log.d(TAG, "> Dumping Fragment Arguments");
		for(String key : k_args) {
			Log.d(TAG, String.format("%s = %s", key, args.get(key)));
		}
		Log.d(TAG, "> End Dump");
	}

	/**
	 * Convenient method to register an onclicklistener to all views within a 
	 * viewgroup
	 * @param och	The handler to assign
	 * @param vh	The view group to assign to
	 */
	public static void registerHandlerToViewObjects(OnClickListener och, ViewGroup vh) {
		View v = null;
		Log.v(TAG, String.format("Registering %d Listening Objects", vh.getChildCount()));
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
	
	/**
	 * Convenient method for returning to the Login page of the application
	 * @param src			The source activity requesting to return
	 * @param clearPrefs	Clear preferences if true
	 * @param timeout		True if timeout triggered
	 */
	public static void returnToLoginPage(Activity src, LogoutReason reason) {
		boolean clearPrefs = 
				(reason == LogoutReason.USER)? 
						true : (reason == LogoutReason.TIMEOUT)? 
								false : false;
		LoginFactory.getInstance().logoff(clearPrefs);
		FragmentUtils.fragmentTransaction((FragmentActivity)src, new LoginFragment(true), true, false);
	}
	
	/**
	 * Perform a transaction to transition from one fragment to another
	 * @param source		The source fragment
	 * @param destination	The destination fragment
	 * @param replace		If true, perform a replace, if false, add
	 * @param backstack		If true, add to backstack
	 */
	public static void fragmentTransaction(FragmentActivity source, Fragment destination, 
			boolean replace, boolean backstack) {
		FragmentUtils.fragmentTransaction(source, destination, replace, backstack, null);
	}
	
	/**
	 * Perform a transaction to transition from one fragment to another
	 * @param source		The source fragment
	 * @param destination	The destination fragment
	 * @param replace		If true, perform a replace, if false, add
	 * @param backstack		If true, add to backstack
	 * @param args			The bundle arguments to add
	 */
	public static void fragmentTransaction(FragmentActivity source, Fragment destination, 
			boolean replace, boolean backstack, Bundle args) {	
		FragmentTransaction transaction = 
				source.getSupportFragmentManager().beginTransaction();
		
		if(args != null)
			destination.setArguments(args);

		// Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack
		if(replace)
			transaction.replace(R.id.content_frame, destination);
		else
			transaction.add(R.id.content_frame, destination);
		
		if(backstack)
			transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}
	
	/**
     * Update the pagination text
     * @param thisPage	the page we are currently on
     * @param finalPage	the last page of the category/thread
     */
    public static String[] updatePagination(final Fragment src, String thisPage, String finalPage) {
    	try {
    		if(!thisPage.equals("") && !finalPage.equals("")) {
		    	final boolean first, prev, next, last;
		    	String myThisPage = thisPage;
		    	String myFinalPage = finalPage;
		
				final TextView pagination = (TextView)src.getView().findViewById(R.id.paginationText);
		    	String label = pagination.getText().toString();            	
		    	label = label.replace("X", thisPage);
		    	label = label.replace("Y", finalPage);
		    	final String finalizedLabel = label;
		    	
		    	// Set up our buttons
		    	prev = Integer.parseInt(thisPage) == 1?
		    			false : true;
		    	first = Integer.parseInt(thisPage) == 1?
		    			false : true;
		    	next = Integer.parseInt(finalPage) > Integer.parseInt(thisPage)?
		    			true : false;
		    	last = Integer.parseInt(thisPage) == Integer.parseInt(finalPage)?
		    			false : true;
		    	
		    	// Update GUI components
		    	src.getActivity().runOnUiThread(new Runnable() {
		    		public void run() {
		    			src.getView().findViewById(R.id.previousButton).setEnabled(prev);
		    			src.getView().findViewById(R.id.firstButton).setEnabled(first);
		    			src.getView().findViewById(R.id.nextButton).setEnabled(next);
		    			src.getView().findViewById(R.id.lastButton).setEnabled(last);
						pagination.setText(finalizedLabel);
		    		}
		    	});
		    	
		    	return new String[]{myThisPage, myFinalPage};
    		}
    	} catch (Exception e) { 
    		Log.e(TAG, "Error Parsing Pagination", e);
    	}
    	
    	return null;
    }
}
