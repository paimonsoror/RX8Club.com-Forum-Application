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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.InputType;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.normalexception.forum.rx8club.Log;
import com.normalexception.forum.rx8club.R;
import com.normalexception.forum.rx8club.html.HtmlFormUtils;

public class MoveThreadDialog {
	
	private AlertDialog.Builder builder;
	private int selection = -1;
	private String newTitle = "";
	
	private Logger TAG =  LogManager.getLogger(this.getClass());
	
	/**
	 * Constructor for method that is used to move a thread from one
	 * forum to another	
	 * @param ctx			The source context/activity	
	 * @param securitytoken	The security token for the session
	 * @param src_thread	The source thread
	 * @param tTitle		The new thread title
	 * @param options		The options from the move dialog
	 */
	public MoveThreadDialog(final Context ctx, final String securitytoken, 
			final String src_thread, String tTitle, final Map<String,Integer> options) {
		builder = new AlertDialog.Builder(ctx);
		
		// Set up the input
		final TextView lbl_title = new TextView(ctx);
		final EditText title     = new EditText(ctx);
		final TextView lbl_dest  = new TextView(ctx);
		final Spinner  destination = new Spinner(ctx);
		
		// Lets make sure the user didn't accidentally click this
		DialogInterface.OnClickListener dialogClickListener = 
				new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which){
			    	case DialogInterface.BUTTON_POSITIVE:
			    		newTitle = title.getText().toString();
			    		String selectText = destination.getSelectedItem().toString();
			    		selection = options.get(selectText);
			    		
						try {
							HtmlFormUtils.adminMoveThread(securitytoken, src_thread, 
				    				newTitle, Integer.toString(selection));
						} catch (Exception e) {
							Log.e(TAG, "Error Submitting Form For Move", e);
						}
						
						((Activity)ctx).finish();
		   				break;
			    	case DialogInterface.BUTTON_NEGATIVE:
			    		break;
		        }
		    }
		};
		
		// Specify the type of input expected
		lbl_title.setText("Thread Title");
		lbl_title.setTextColor(Color.WHITE);
		lbl_dest .setText("Desination");
		lbl_dest .setTextColor(Color.WHITE);
		title.setInputType(InputType.TYPE_CLASS_TEXT);
		title.setText(tTitle);
		
		List<String> values = new ArrayList<String>();
		values.addAll(options.keySet());
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
				ctx, android.R.layout.simple_spinner_item, values);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		destination.setAdapter(dataAdapter);
		
		LinearLayout ll = new LinearLayout(ctx);
		ll.setOrientation(LinearLayout.VERTICAL);
		ll.addView(lbl_title);
		ll.addView(title);
		ll.addView(lbl_dest);
		ll.addView(destination);
				
		builder.setView(ll);
		
		builder
			.setTitle(R.string.dialogMoveThread)
			.setPositiveButton(R.string.Move, dialogClickListener)
		    .setNegativeButton(R.string.cancel, dialogClickListener);
	}
	
	/**
	 * Show the dialog
	 */
	public void show() {
		builder.show();
	}
}
