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

import android.app.AlertDialog;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.normalexception.app.rx8club.R;
import com.normalexception.app.rx8club.view.ViewHolder;

/**
 * Utility to calculate normalized compression
 */
public class CompressionFragment extends Fragment {
	private View v = null;
	
	private static final double KPaToPSI    = 0.145037738;
	private static final double MeterToFeet = 3.2808399;
	private static final double Compression = 1.039;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);    	
        return rootView;
    }
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //super.setState(AppState.State.UTIL_COMPRESSION, this.getIntent());
        
        //setContentView(R.layout.activity_basiclist);
        
        RelativeLayout rl = (RelativeLayout) getView().findViewById(R.id.content_frame);
        
		v = getActivity().getLayoutInflater().inflate(R.layout.view_compression, null);
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
	            LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, R.id.mainlisttitle);
		
		ScrollView sv = new ScrollView(getActivity());
		sv.addView(v);
		sv.setLayoutParams(params);
		
		rl.addView(sv);
		
		ViewHolder.get(v, R.id.compressionNormalize).setOnClickListener(new CompressionListener());
    }
    
    class CompressionListener implements OnClickListener {
	    /*
		 * (non-Javadoc)
		 * @see android.view.View.OnClickListener#onClick(android.view.View)
		 */
		@Override
		public void onClick(View arg0) {			
			if(checkIfInputsValid()) {
				double rotor[] = new double[] { 
						Double.parseDouble( // Rotor 1
								((TextView)ViewHolder.get(v, R.id.compressionRotorOne))
									.getText()
									.toString()),
						Double.parseDouble( // Rotor 2
								((TextView)ViewHolder.get(v, R.id.compressionRotorTwo))
									.getText()
									.toString()),
						Double.parseDouble( // Rotor 3
								((TextView)ViewHolder.get(v, R.id.compressionRotorThree))
									.getText()
									.toString())};
				
				int rpm = 
						Integer.parseInt(
								((TextView)ViewHolder.get(v, R.id.compressionRotorCrank))
									.getText()
									.toString());
				double altitude =
						Integer.parseInt(
								((TextView)ViewHolder.get(v, R.id.compressionRotorAltitude))
									.getText()
									.toString());
				
				boolean isAltInMeters = 
						((Spinner)ViewHolder.get(v, R.id.compressionAltitudeSpinner))
							.getSelectedItemPosition() != 0;
				boolean isCrankInKpa =
						((Spinner)ViewHolder.get(v, R.id.compressionUnitValues))
							.getSelectedItemPosition() != 0;
				
				// Convert the crank unit if it is in KPa
				if(isCrankInKpa)
					for(int i = 0; i < rotor.length; i++) 
						rotor[i] *= KPaToPSI;
				
				// Convert the altitude if it is in meters
				if(isAltInMeters) 
					altitude *= MeterToFeet;
				
				// Now get the altitude comp value
				double altitude_compensation = 
						1 / ((5.983051463 * Math.exp(-0.0000435184*altitude)) / 5.9);
				
				// Now get the rpm comp value
				double rpm_compensation = 1 / ((0.019955 * rpm + 3.493182)/8.5);
		
				// Total comp value
				double totalComp = Compression * altitude_compensation * rpm_compensation;
		
				new AlertDialog.Builder(getActivity())
				.setTitle(R.string.util_compressionTitle)
				.setMessage(String.format("#1: %.1f, #2: %.1f, #3 %.1f", 
						rotor[0] * totalComp, rotor[1] * totalComp, rotor[2] * totalComp)).show();
			} else {
				getActivity().runOnUiThread(new Runnable() {
		            public void run() {
		            	Toast.makeText(getActivity(), 
		            			R.string.fillForm, Toast.LENGTH_SHORT).show();
		            }
				});
			}
		}
    }

	/**
	 * Check if the screen's input are valid
	 * @return	True if the values are valid
	 */
	private boolean checkIfInputsValid() {
		return 
			((TextView)ViewHolder.get(v, R.id.compressionRotorOne)).getText().length() > 0 &&
			((TextView)ViewHolder.get(v, R.id.compressionRotorTwo)).getText().length() > 0 &&
			((TextView)ViewHolder.get(v, R.id.compressionRotorThree)).getText().length() > 0 &&
			((TextView)ViewHolder.get(v, R.id.compressionRotorCrank)).getText().length() > 0 &&
			((TextView)ViewHolder.get(v, R.id.compressionRotorAltitude)).getText().length() > 0;
	}
}
