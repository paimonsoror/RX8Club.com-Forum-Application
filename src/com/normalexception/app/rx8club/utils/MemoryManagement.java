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

import java.text.DecimalFormat;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import com.normalexception.app.rx8club.Log;
import com.normalexception.app.rx8club.MainApplication;

public class MemoryManagement {

	/**
	 * Report the current available RAM in our device
	 * @return The current available RAM
	 */
	public static void printCurrentMemoryInformation() {
		Double allocated = getAllocatedHeapSize();
        Double available = getMaxHeapSize();
        Double free = getFreeHeapSize();
        
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        Log.d("Utils", "Heap Native: Allocated " + df.format(allocated) + 
        		"MB of " + 
        		df.format(available) + "MB (" + df.format(free) + "MB free)");
        
        Log.d("Utils", "Memory: Allocated: " + df.format(
        		Double.valueOf(Runtime.getRuntime().totalMemory()/1048576)) + 
        		"MB of " + 
        		df.format(Double.valueOf(Runtime.getRuntime().maxMemory()/1048576)) + 
        		"MB (" + 
        		df.format(Double.valueOf(Runtime.getRuntime().freeMemory()/1048576)) +
        		"MB Free)");
	}
	
	/**
	 * Report the max VM size
	 * @return	The free virtual machines memory
	 */
	public static int getVMSize() {
		final ActivityManager am = 
				(ActivityManager) MainApplication.getAppContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		final int memoryClassBytes = am.getMemoryClass() * 1024 * 1024;
		return memoryClassBytes;
	}
	
	/**
	 * Report the free heap size
	 * @return	The free heap size as a double
	 */
	public static double getFreeHeapSize() {
		return 
        		Double.valueOf(Debug.getNativeHeapFreeSize())/1048576.0;
	}
	
	/**
	 * The maximum heap size
	 * @return	The max heap size as a double
	 */
	public static double getMaxHeapSize() {
		return 
				Double.valueOf(Debug.getNativeHeapSize())/1048576.0;	
	}
	
	/**
	 * Report the total allocated heap size
	 * @return	The allocate heap size as a double
	 */
	public static double getAllocatedHeapSize() {
		return 
				Double.valueOf(Debug.getNativeHeapAllocatedSize())/Double.valueOf((1048576));
	}
}
