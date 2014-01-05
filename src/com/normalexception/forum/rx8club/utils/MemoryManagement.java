package com.normalexception.forum.rx8club.utils;

import java.text.DecimalFormat;

import android.os.Debug;

import com.normalexception.forum.rx8club.Log;

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
