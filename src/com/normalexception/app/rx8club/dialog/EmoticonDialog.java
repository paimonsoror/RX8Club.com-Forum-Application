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
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.normalexception.app.rx8club.R;

public class EmoticonDialog {
	private AlertDialog.Builder builder = null;

	private DialogInterface.OnClickListener listener = null;
	private ListAdapter adapter = null;
	
	private final int TITLE = R.string.bannerEmote;
	
	/**
	 * List of all emoticons that we will display to the user
	 */
	final Item[] items = {
		    new Item("Smile",     "smile",       R.drawable.emote_smile),
		    new Item("Frown",     "frown",       R.drawable.emote_frown),
		    new Item("Cool",      "cool",        R.drawable.emote_cool),
		    new Item("Scratch",   "scratchhead", R.drawable.emote_scratchhead),
		    new Item("Horns",     "ylsuper",     R.drawable.emote_ylsuper),
		    new Item("Kiss",      "kiss",        R.drawable.emote_kiss),
		    new Item("Wall Bash", "banghead",    R.drawable.emote_banghead),
		    new Item("Cross Fingers", "fingersx",R.drawable.emote_fingersx),
		    new Item("Sweating",  "sweatdrop",   R.drawable.emote_sweatdrop),
		    new Item("Nod",       "yesnod",      R.drawable.emote_yesnod),
		    new Item("Thumbs Up", "icon_tup",    R.drawable.emote_icon_tup)
		};
	
	/**
	 * Inner class that will contain the emoticon item
	 */
	private static class Item{
	    public final String text;
	    public final int icon;
	    public final String code;
	    
	    /**
	     * Basic emoticon item
	     * @param text	The text to display
	     * @param code	The code to add to the text box
	     * @param icon	The icon to display
	     */
	    public Item(String text, String code, Integer icon) {
	        this.text = text;
	        this.code = code;
	        this.icon = icon;
	    }
	    
	    @Override
	    public String toString() {
	        return text;
	    }
	}
	
	/**
	 * Initialize our dialog builder, and the list of 
	 * emoticons
	 * @param ctx	The source context
	 * @param tv	The reference to the textview we will populate
	 */
	public EmoticonDialog(Context ctx, final TextView tv) {	
		builder = new AlertDialog.Builder(
				new ContextThemeWrapper(ctx, R.style.AlertDialogCustom));
		
		// The list adapter that contains all of the emoticons that 
		// we will display to the user
		adapter = new ArrayAdapter<Item>(ctx, 
				android.R.layout.select_dialog_item,
				android.R.id.text1,
				items)
		{
	        public View getView(int position, View convertView, ViewGroup parent) {
	            //User super class to create the View
	            View v = super.getView(position, convertView, parent);
	            TextView tv = (TextView)v.findViewById(android.R.id.text1);

	            //Put the image on the TextView
	            tv.setCompoundDrawablesWithIntrinsicBounds(items[position].icon, 0, 0, 0);

	            //Add margin between image and text (support various screen densities)
	            int dp5 = (int) (5 * 
	            		this.getContext().getResources().getDisplayMetrics().density + 0.5f);
	            tv.setCompoundDrawablePadding(dp5);

	            return v;
	        }
	    };
	    
	    // Listener for a selection
	    listener = new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	            tv.setText(tv.getText().toString() + String.format(":%s:", items[item].code));
	        }
	    };
	}
	
	/**
	 * Set up our items, and show the dialog
	 */
	public void show() {
		// 2. Chain together various setter methods to set the dialog characteristics
		builder.setAdapter(adapter, listener	)
		       .setTitle(TITLE);
		
		// 3. Get the AlertDialog from create()
		builder.create().show();
	}
}
