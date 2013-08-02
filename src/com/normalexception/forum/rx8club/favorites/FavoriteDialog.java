package com.normalexception.forum.rx8club.favorites;

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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.normalexception.forum.rx8club.activities.thread.ThreadActivity;
import com.normalexception.forum.rx8club.view.thread.ThreadView;

/**
 * Convenience class to create the dialog and register the handlers
 * to handle the favorites listing
 */
public class FavoriteDialog {

	private Context ctx = null;
	private AlertDialog.Builder builder = null;
	private CharSequence[] cs_favoriteslist = null;
	private final FavoriteThreads ft = 
			FavoriteFactory.getInstance().getFavorites();
	private DialogInterface.OnClickListener listener = null;
	
	private final String TITLE = "Favorites";
	
	/**
	 * Initialize our dialog builder, and the list of 
	 * favorites
	 * @param ctx	The source context
	 */
	public FavoriteDialog(Context ctx) {	
		this.ctx = ctx;
		builder = new AlertDialog.Builder(ctx);
		
		List<String>    ls = new ArrayList<String>();
		for(ThreadView tv : ft)
			ls.add(tv.getTitle());
		cs_favoriteslist = 
			ls.toArray(new CharSequence[ls.size()]);
	}
	
	/**
	 * Register the dialog to handle a removal of data
	 * from our favorites list.  This is used in our 
	 * Preferences handler
	 */
	public void registerToRemove() {
		listener = new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	          	// The 'which' argument contains the index position
	           	// of the selected item
	    		FavoriteFactory
	    			.getInstance()
	    			.removeFavorite(ft.get(which));
	        }
	  	};
	}
	
	/**
	 * Register the dialog to handle the execution of the 
	 * favorites list item.  This is used in our banner 
	 * fragment handler
	 */
	public void registerToExecute() {
		listener = new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	          	// The 'which' argument contains the index position
	           	// of the selected item
	    		ThreadView tv = ft.get(which);
	    		Intent _intent = 
						new Intent(ctx, ThreadActivity.class);
				_intent.putExtra("link", tv.getLink());
				_intent.putExtra("title", tv.getTitle());
				ctx.startActivity(_intent);
	        }
	  	};
	}
	
	/**
	 * Set up our items, and show the dialog
	 */
	public void show() {
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setItems(cs_favoriteslist, listener	)
		       .setTitle(TITLE);
		
		// 3. Get the AlertDialog from create()
		builder.create().show();
	}
}
