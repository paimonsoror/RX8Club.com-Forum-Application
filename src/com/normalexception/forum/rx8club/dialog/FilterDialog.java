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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.filter.ThreadFilter;
import com.normalexception.forum.rx8club.filter.ThreadFilter.RuleType;
import com.normalexception.forum.rx8club.filter.ThreadFilterFactory;

public class FilterDialog {
	
	private AlertDialog.Builder builder;
	
	/**
	 * Create a dialog for the user to enter a thread filter
	 * @param ctx	The source base activity
	 */
	public FilterDialog(final Context ctx) {
		// Set up the input
		final EditText input = new EditText(ctx);		
		final Spinner  spinner = new Spinner(ctx);
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		ThreadFilterFactory.getInstance().addFilter(new ThreadFilter(
			    				RuleType.values()[spinner.getSelectedItemPosition()], 
			    				input.getText().toString()));
			    		Activity a = (Activity)ctx;
			    		a.finish();
			    		a.startActivity(a.getIntent());
		   				break;
			    	case DialogInterface.BUTTON_NEGATIVE:
			    		break;
		        }
		    }
		};
		builder = new AlertDialog.Builder(ctx);
		
		// Specify the type of input expected 
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ctx,
		        R.array.filterValues, android.R.layout.simple_spinner_item);
		
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
		LinearLayout llay = new LinearLayout(ctx);
		llay.setOrientation(LinearLayout.VERTICAL);
		llay.addView(spinner);
		llay.addView(input);
		
		builder.setView(llay);
		
		builder
			.setTitle(R.string.dialogSignature)
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
