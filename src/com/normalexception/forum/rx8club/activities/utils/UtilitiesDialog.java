package com.normalexception.forum.rx8club.activities.utils;

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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.ContextThemeWrapper;

import com.normalexception.forum.rx8club.R;

public class UtilitiesDialog {

	private AlertDialog.Builder builder = null;
	private DialogInterface.OnClickListener listener = null;
	private CharSequence[] utilitiesList = null;
	
	private final int TITLE = R.string.menuUtilities;
	
	/**
	 * Display a dialog that will contain some RX8
	 * specific utilities that users can use to diagnose
	 * issues
	 * @param ctx	The source context
	 */
	public UtilitiesDialog(final Context ctx) {
		builder = new AlertDialog.Builder(
				new ContextThemeWrapper(ctx, R.style.AlertDialogCustom));
		utilitiesList = ctx.getResources().getStringArray(R.array.utilities);
		listener = new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int which) {
	    		switch(which) {
	    		case 0:
	    			Intent intent = new Intent(ctx, CompressionActivity.class);
	    			ctx.startActivity(intent);
	    			break;
	    		case 1:					
	    			builder = new VinDecoderDialog(ctx);
	    			builder.show();
	    			break;
	    		default: break;
	    		}
	    	}
		};
	}
	
	/**
	 * Set up our items, and show the dialog
	 */
	public void show() {
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setItems(utilitiesList, listener)
		       .setTitle(TITLE);
		
		// 3. Get the AlertDialog from create()
		builder.create().show();
	}
}
