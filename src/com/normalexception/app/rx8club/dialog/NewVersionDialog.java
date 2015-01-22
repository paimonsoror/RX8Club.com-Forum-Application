package com.normalexception.app.rx8club.dialog;

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
import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;

public class NewVersionDialog {

	private AlertDialog.Builder builder;
	
	private String response = "";
	
	/**
	 * Create a dialog that lets the user know that an update is
	 * available for download
	 * @param ctx		The application root context
	 * @param versionid	The version id  that is available
	 * @param whatsnew	Text to display to the user
	 * @param link		The link to the download
	 */
	public NewVersionDialog(final Context ctx, final String versionid, 
			final String whatsnew, final String link) {
		// Set up the input
		final TextView input = new TextView(ctx);
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		Intent browserIntent = 
			    			new Intent(Intent.ACTION_VIEW, Uri.parse(link));
			    		ctx.startActivity(browserIntent);
		   				break;
			    	case DialogInterface.BUTTON_NEGATIVE:
			    		dialog.dismiss();
			    		break;
		        }
		    }
		};
		builder = new AlertDialog.Builder(ctx);
		
		LinearLayout linearLayout = new LinearLayout(ctx);  
	    linearLayout.setOrientation(LinearLayout.VERTICAL);

		// Specify the type of input expected 
		input.setText(R.string.newVersionAvailable);
	    linearLayout.addView(input);
	    
	    TextView summary = new TextView(ctx);
	    summary.setText(whatsnew);
	    linearLayout.addView(summary);
	    
		builder.setView(linearLayout);		
		
		builder
			.setTitle(String.format("Version %s Available", versionid))
			.setPositiveButton(R.string.download, dialogClickListener)
		    .setNegativeButton(R.string.cancel, dialogClickListener);
	}
	
	public String getResponse() {
		return this.response;
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		builder.show();
	}
}

