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

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.normalexception.forum.rx8club.activities.list.CategoryActivity;

public class SubCategoryDialog {

	private Context ctx = null;
	private AlertDialog.Builder builder = null;
	private List<SubCategoryView> scv = null;

	private DialogInterface.OnClickListener listener = null;
	
	private final String TITLE = "Sub Forums";
	
	/**
	 * Initialize our dialog builder, and the list of 
	 * favorites
	 * @param ctx	The source context
	 * @param scv	The sub category list
	 */
	public SubCategoryDialog(Context ctx, List<SubCategoryView> scv) {	
		this.ctx = ctx;
		builder = new AlertDialog.Builder(ctx);
		this.scv = scv;
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
	    		SubCategoryView tv = scv.get(which);
	    		Intent intent = 
						new Intent(ctx, CategoryActivity.class);
				intent.putExtra("link", tv.getLink());
				ctx.startActivity(intent);
	        }
	  	};
	}
	
	/**
	 * Set up our items, and show the dialog
	 */
	public void show() {
		List<String> forums = new ArrayList<String>();
		
		for(SubCategoryView sc : scv)
			forums.add(sc.getTitle());
		
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setItems(forums.toArray(new CharSequence[forums.size()]), listener	)
		       .setTitle(TITLE);
		
		// 3. Get the AlertDialog from create()
		builder.create().show();
	}
}
