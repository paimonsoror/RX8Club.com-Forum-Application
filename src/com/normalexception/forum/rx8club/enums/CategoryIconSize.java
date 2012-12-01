package com.normalexception.forum.rx8club.enums;

public enum CategoryIconSize {
	/*
	 * Low DPI
	 */
	LDPI(12), 
	
	/*
	 * Medium DPI
	 */
	MDPI(12), 
	
	/*
	 * High DPI
	 */
	HDPI(24), 
	
	/*
	 * Extra High DPI
	 */
	XHDPI(48);
	
	private int value;    
	
	/**
	 * Constructor to the enumerator
	 * @param value	The pixel value of the size
	 */
	private CategoryIconSize(int value) {
		this.value = value;
	}
	
	/**
	 * Report the value of the enumerator
	 * @return	The pixel size of the enumerator
	 */
	public int getValue() {
		return value;
	} 
}
