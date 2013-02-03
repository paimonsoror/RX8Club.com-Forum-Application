package com.normalexception.forum.rx8club.activities.pm;

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

import android.os.Parcel;
import android.os.Parcelable;

public class PrivateMessageParcel implements Parcelable {
	public String user, time, subject, date, link;
	
	/*
	 * Must implement this or else the Android framework will
	 * throw an exeption
	 */
	public static final Parcelable.Creator<PrivateMessageParcel> CREATOR
		= new Parcelable.Creator<PrivateMessageParcel>() {
	    public PrivateMessageParcel createFromParcel(Parcel in) {
	        return new PrivateMessageParcel(in);
	    }
	
	    public PrivateMessageParcel[] newArray(int size) {
	        return new PrivateMessageParcel[size];
	    }
	};
	
	/**
	 * Default constructor
	 */
	public PrivateMessageParcel() {
	}
	
	/**
	 * Constructor to a private message
	 * @param in	The parcel to copy
	 */
	private PrivateMessageParcel(Parcel in) {
		user 	= in.readString();
		time 	= in.readString();
		subject = in.readString();
		date 	= in.readString();
		link 	= in.readString();
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// Log for debug purposes
		return String.format("[%s %s] %s | %s",
					date,
					time,
					user,
					subject);
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#describeContents()
	 */
	@Override
	public int describeContents() {
		return 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(user);
		dest.writeString(time);
		dest.writeString(subject);
		dest.writeString(date);
		dest.writeString(link);
	}
}
