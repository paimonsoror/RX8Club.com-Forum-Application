package com.normalexception.forum.rx8club.state;

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

import android.content.Intent;

public class AppState {
	
	private static AppState _instance;
	private        Intent   _intent;
	private        State    _state;
	
	public enum State { NO_KNOWN_STATE,
						ABOUT,
						LOGIN,
						PROFILE,
						SEARCH,
						USER_CP,
		                MAIN_PAGE, 
		                CATEGORY, 
		                NEW_POSTS,
		                EDIT_POST,
		                THREAD, 
		                NEW_THREAD,
		                NEW_PM,
		                FAVORITES,
		                PMINBOX,
		                PMVIEW,
		                LOGVIEW,
		                UTIL_COMPRESSION};
	/**
	 * Report an instance of the AppState object
	 * @return	An instance of the AppState object or
	 * 			create a new one if one doesn't exist
	 */
	public static AppState getInstance() {
		if(_instance == null)
			_instance = new AppState();
		
		return _instance;
	}
	
	/**
	 * Initialize our AppState
	 */
	private AppState() {
		_state = State.NO_KNOWN_STATE;
	}
	
	/**
	 * Set the current state of our application
	 * @param state		The current state of the application
	 * @param intent	The current intent
	 */
	public void setCurrentState(State state, Intent intent) {
		_state = state;
		_intent= intent;
	}
	
	/**
	 * Report the current state of the application
	 * @return	The current state of the application
	 */
	public State getCurrentState() {
		return _state;
	}
	
	/**
	 * Report the current intent within the app state
	 * @return	The application intent
	 */
	public Intent getCurrentIntent() {
		return _intent;
	}
}
