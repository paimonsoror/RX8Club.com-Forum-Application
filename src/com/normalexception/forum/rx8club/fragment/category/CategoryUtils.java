package com.normalexception.forum.rx8club.fragment.category;

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
import android.util.DisplayMetrics;

import com.normalexception.forum.rx8club.enums.CategoryIconSize;

public class CategoryUtils {

	/**
     * Depending on the screen DPI, we will rescale the thread
     * buttons to make sure that they are not too small or 
     * too large
     */
    public static int setScaledImageSizes(Activity src) {
    	switch(src.getResources().getDisplayMetrics().densityDpi) {
    	case DisplayMetrics.DENSITY_LOW:
    	case DisplayMetrics.DENSITY_MEDIUM:
    		return CategoryIconSize.LDPI.getValue();

    	case DisplayMetrics.DENSITY_HIGH:
    		return CategoryIconSize.HDPI.getValue();

    	case DisplayMetrics.DENSITY_XHIGH:
    	default:
    		return CategoryIconSize.XHDPI.getValue();
    	}
    } 
}
