package com.normalexception.app.rx8club.utils;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.normalexception.app.rx8club.Log;

public class DateDifference {
	
	private static Logger TAG =  LogManager.getLogger(DateDifference.class);
	
	public static enum TimeField {
		DAY,
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND;
    }
	
	/**
     * Calculate the absolute difference between two Date without
     * regard for time offsets
     * @param d1 	Date one
     * @param d2 	Date two
     * @param field The field we're interested in out of
     * 				day, hour, minute, second, millisecond
     * @return 		The value of the required field
     */
    public static long getTimeDifference(Date d1, Date d2, TimeField field) {
        return DateDifference.getTimeDifference(d1, d2)[field.ordinal()];
    }
	
	/**
     * Calculate the absolute difference between two Date without
     * regard for time offsets
     * @param d1 Date one
     * @param d2 Date two
     * @return The fields day, hour, minute, second and millisecond
     *                      0     1       2       3               4
     */
    public static long[] getTimeDifference(Date d1, Date d2) {
        long[] result = new long[5];
        Calendar cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("UTC"));
        cal.setTime(d1);

        long t1 = cal.getTimeInMillis();
        cal.setTime(d2);

        long diff = Math.abs(cal.getTimeInMillis() - t1);
        final int ONE_DAY = 1000 * 60 * 60 * 24;
        final int ONE_HOUR = ONE_DAY / 24;
        final int ONE_MINUTE = ONE_HOUR / 60;
        final int ONE_SECOND = ONE_MINUTE / 60;

        long d = diff / ONE_DAY;
        diff %= ONE_DAY;

        long h = diff / ONE_HOUR;
        diff %= ONE_HOUR;

        long m = diff / ONE_MINUTE;
        diff %= ONE_MINUTE;

        long s = diff / ONE_SECOND;
        long ms = diff % ONE_SECOND;
        result[0] = d;
        result[1] = h;
        result[2] = m;
        result[3] = s;
        result[4] = ms;

        return result;
    }
    
    /**
	 * Convenient method for grabbing the difference between the
	 * last post and today
	 * @param preFormatted	The last post time
	 * @return				The difference string
	 */
    public static String getPrettyDate(String preFormatted) {
    	String differenceTime = "";
        try {
        	SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm a", Locale.getDefault());
        	Date lastDate = sdf.parse(preFormatted);
        	long diffs[]  = DateDifference.getTimeDifference(lastDate, new Date());
        	long years    = diffs[TimeField.DAY.ordinal()] / 365;
        	if(diffs[TimeField.DAY.ordinal()] != 0) {
        		if(years > 0) {
        			differenceTime = String.format(Locale.getDefault(), " (%d year%s ago)", 
        					years,
	        				years > 1? "s" : "");
        		} else {
	        		differenceTime = String.format(Locale.getDefault(), " (%d day%s ago)", 
	        				diffs[TimeField.DAY.ordinal()],
	        				diffs[TimeField.DAY.ordinal()] > 1? "s" : "");
        		}
        	} else if(diffs[TimeField.HOUR.ordinal()] == 0) {
        		if(diffs[TimeField.MINUTE.ordinal()] <= 1)
        			differenceTime = " (Just now)";
        		else
        			differenceTime = String.format(Locale.getDefault(), " (%dmin%s ago)", 
        				diffs[TimeField.MINUTE.ordinal()],
        				diffs[TimeField.MINUTE.ordinal()] > 1? "s" : "");
        	} else
        		differenceTime = String.format(Locale.getDefault(), " (%s%dhr%s ago)", 
        				diffs[TimeField.MINUTE.ordinal()] > 0? "over " : "",
                				diffs[TimeField.HOUR.ordinal()],
    	        				diffs[TimeField.HOUR.ordinal()] > 1? "s" : "" );
        } catch (ParseException pe) { 
        	Log.e(TAG, "Couldn't parse the date for the thread", pe);
        }
        
        return differenceTime;    	
    }
}
