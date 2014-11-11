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

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.normalexception.app.rx8club.Log;

public class PasswordUtils {

	public static String TAG = "PasswordUtils";
	
	/**
	 * Convert the password to an md5 password
	 * @param password	A plaintext password
	 * @return			The md5 encoded password
	 * @throws NoSuchAlgorithmException
	 */
	public static String hexMd5(String password) {
		Log.v(TAG, "Creating MD5 Password");
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(password.getBytes());
			BigInteger hash = new BigInteger(1, md5.digest());
			return pad(hash.toString(16), 32, '0');
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "Error Encoding Password ", e);
			return password;
		}
	}
	
	/**
	 * Check if already MD5 encoded
	 * @param s	The string to check
	 * @return	True if MD5 encoded
	 */
	public static boolean isValidMD5(String s) {
	    return s.matches("[a-fA-F0-9]{32}");
	}
	    
	/**
	 * Pad the md5 password
	 * @param s			The string to pad
	 * @param length	The total length to pad to
	 * @param pad		The pad character
	 * @return			The padded string
	 */
	private static String pad(String s, int length, char pad) {
		Log.v(TAG, "Padding MD5 Password");
		StringBuffer buffer = new StringBuffer(s);
		while (buffer.length() < length) {
			buffer.insert(0, pad);
		}
		return buffer.toString();
	}
}
