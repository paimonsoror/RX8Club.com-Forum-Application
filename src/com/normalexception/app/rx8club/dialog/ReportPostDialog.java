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

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.text.InputType;
import android.widget.EditText;
import android.widget.Toast;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;
import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.html.HtmlFormUtils;

/**
 * Dialog used to report posts to admins
 */
public class ReportPostDialog {
	private AlertDialog.Builder builder;
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	/**
	 * Create a dialog for the user to report a post
	 * @param ctx			The source base activity
	 * @param securitytoken	The security token for the user
	 * @param postId		The id of the post to report
	 */
	public ReportPostDialog(final Context ctx, final String securitytoken, final String postId) {
		// Set up the input
		final EditText input = new EditText(ctx);
		input.setLines(2);
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		if(input.getText().toString().isEmpty()) {
			    			Toast.makeText(MainApplication.getAppContext(), 
			    					R.string.pleaseEnterReport, 
			    					Toast.LENGTH_SHORT).show();
			    		} else {
				    		AsyncTask<Void,String,Void> reportTask = new AsyncTask<Void,String,Void>() {
				            	@Override
				    			protected Void doInBackground(Void... params) {	
				    				try {
										HtmlFormUtils.reportPost(securitytoken, postId, input.getText().toString());
									} catch (IOException e) {
										Log.e(TAG, "Error reporting post", e);
									}
				    		    	return null;
				    			}
				    			
				    			@Override
				    		    protected void onPostExecute(Void result) {
				    				Toast.makeText(MainApplication.getAppContext(), 
				    						R.string.postReported, 
				    						Toast.LENGTH_SHORT).show();
				    			}
				            };
				            reportTask.execute();
			    		}
		   				break;
			    	case DialogInterface.BUTTON_NEGATIVE:
			    		break;
		        }
		    }
		};
		builder = new AlertDialog.Builder(ctx);
		
		// Specify the type of input expected 
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setText("");
		builder.setView(input);
		
		builder
			.setTitle(R.string.nrReportReason)
			.setPositiveButton(R.string.submit, dialogClickListener)
		    .setNegativeButton(R.string.cancel, dialogClickListener);
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		builder.show();
	}
}
