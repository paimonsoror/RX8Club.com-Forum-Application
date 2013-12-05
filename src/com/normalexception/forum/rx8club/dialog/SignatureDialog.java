package com.normalexception.forum.rx8club.dialog;

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
import android.text.InputType;
import android.widget.EditText;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.html.LoginFactory;

public class SignatureDialog {

	private AlertDialog.Builder builder;
	
	/**
	 * Create a dialog for the user to enter their custom sig
	 * @param ctx	The source base activity
	 */
	public SignatureDialog(final Context ctx) {
		// Set up the input
		final EditText input = new EditText(ctx);
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		LoginFactory.getInstance().saveSignature(input.getText().toString());
		   				break;
			    	case DialogInterface.BUTTON_NEUTRAL:
			    		LoginFactory.getInstance().saveSignature(
			    				ctx.getString(R.string.app_signature));
			    		break;
			    	case DialogInterface.BUTTON_NEGATIVE:
			    		break;
		        }
		    }
		};
		builder = new AlertDialog.Builder(ctx);
		
		// Specify the type of input expected 
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setText(LoginFactory.getInstance().getSignature());
		builder.setView(input);
		
		builder
			.setTitle(R.string.dialogSignature)
			.setPositiveButton(R.string.submit, dialogClickListener)
			.setNeutralButton(R.string.Default, dialogClickListener)
		    .setNegativeButton(R.string.cancel, dialogClickListener);
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		builder.show();
	}
}
