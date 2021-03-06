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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.fragment.FragmentUtils;
import com.normalexception.app.rx8club.html.LoginFactory;

public class LogoffDialog {
	
	private AlertDialog.Builder builder;
	private Activity _ctx = null;
	
	/**
	 * Create a dialog used to ensure that the user
	 * wanted to logoff
	 * @param ctx	The source base activity
	 */
	public LogoffDialog(final Activity ctx) {
		_ctx = ctx;
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		FragmentUtils.returnToLoginPage(ctx, FragmentUtils.LogoutReason.USER);
		   				break;
		        }
		    }
		};
		builder = new AlertDialog.Builder(ctx);
		builder
			.setMessage(R.string.dialogLogoffConfirm)
			.setPositiveButton(R.string.Yes, dialogClickListener)
		    .setNegativeButton(R.string.No, dialogClickListener);
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		if(LoginFactory.getInstance().isGuestMode())
			FragmentUtils.returnToLoginPage(_ctx, FragmentUtils.LogoutReason.USER);
		else
			builder.show();
	}
}
