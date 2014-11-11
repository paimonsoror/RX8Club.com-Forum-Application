package com.normalexception.app.rx8club.fragment.utils;

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

import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.EditText;

import com.normalexception.app.rx8club.R;

public class VinDecoderDialog extends AlertDialog.Builder {
	private Context ctx = null;
	private final int MAX_VIN = 17;

	/**
	 * Dialog to decode a VIN number to readible text
	 * @param ctx	The application context
	 */
	public VinDecoderDialog(Context ctx) {
		super(ctx);
		this.ctx = ctx;
		
		// Set an EditText view to get user input 
		final EditText input = new EditText(ctx);
		
		setTitle(R.string.util_vinTitle);
		
		setMessage(R.string.util_vinMessage);
		
		// Ad text view
		setView(input);
		
		setPositiveButton(R.string.util_vinDecode, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				decode(value);
			}
		});
		setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {}
		});
	}
	
	/**
	 * Decode the VIN for the user and display on screen
	 * @param value	The value of the text input
	 */
	private void decode(String value) {
		String decodedString = "";
		if (value == null || value.length() == 0) return;
		if (value.length() != MAX_VIN) return;
		
		final String NS = "Not Sure";
		value = value.toUpperCase(Locale.US);
		
		// 012   34     5       6      7     8     9    10  
		// JM1 | FE   | 1     | 7    | 2   | 2   | A  | 0    | 123456
		// Maz | Chas | Restr | Body | Eng | Chk | MY | Plnt | SerNo
		String car = value.substring(0, 3).equals("JM1")?  "Mazda" : NS;
		String model = value.substring(3, 5).equals("FE")? "RX8" : NS;
		String restraint = value.charAt(5) == '1'? "D/P Airbag, Side Curtain AB" : NS;
		String body = value.charAt(6) == '7'?      "Coupe" : NS;
		String engine = 
				value.charAt(7) == '2'? "13B-High power, for California" :
				value.charAt(7) == '3'? "13B-High power" :
				value.charAt(7) == '4'? "13B-High power, for Federal/Canada" :
				value.charAt(7) == 'M'? "13B-Standard power, for California" :
				value.charAt(7) == 'N'? "13B-Standard power" :
				value.charAt(7) == 'P'? "13B-Standard power, for Federal/Canada" : NS;
		String modelYr = 
				Integer.toString(Character.digit(value.charAt(9), 16));
		String plant = 
				value.charAt(10) == '0' ? "Hiroshima" :
				value.charAt(10) == '1' ? "Hofu" : NS;
		String serialNo = value.substring(11);
		
		// Format the string
		decodedString = String.format(
				"Make: %s\n" +
				"Model: %s\n" +
				"Restraint: %s\n" +
				"Body: %s\n" +
				"Engine: %s\n" +
				"Model Year: %s\n" +
				"Plant: %s\n" +
				"Serial Number: %s", 
				car, model, restraint, body, engine, 
				modelYr, plant, serialNo);
		
		// Display the results in a dialog
		AlertDialog.Builder ab = new AlertDialog.Builder(ctx);
		ab.setMessage(decodedString);
		ab.show();
	}
}
