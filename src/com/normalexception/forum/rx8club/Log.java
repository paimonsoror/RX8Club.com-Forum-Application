package com.normalexception.forum.rx8club;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.bugsense.trace.BugSenseHandler;
import com.normalexception.forum.rx8club.cache.impl.LogFile;

import de.mindpipe.android.logging.log4j.LogConfigurator;

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

/**
 * Wrapper class for the android Log class.  This allows us to either change
 * our logging mechanism in the future, and control the lowest logging level
 */
public class Log {
	// The log int definitions
	public static final int VERBOSE = 0,
			DEBUG = 1,
			INFO = 2,
			WARN = 3,
			ERROR = 4;

	// The log level
	private static int mLevel = VERBOSE;
	
	/////////////////////////////////////////////////////////
	// Log4J Specific Methods
	/////////////////////////////////////////////////////////
	
	private static final long MAX_SIZE = 1024 * 1024;
	
	public static void configure() {
        final LogConfigurator logConfigurator = 
        		new LogConfigurator();
        LogFile lf = new LogFile(MainApplication.getAppContext());
        logConfigurator.setFileName(lf.getLogFile());
        logConfigurator.setRootLevel(toLog4jLevel(mLevel));
        logConfigurator.setMaxFileSize(MAX_SIZE);
        logConfigurator.setMaxBackupSize(0);
        logConfigurator.setFilePattern("%d{ISO8601} [%t] %-5p %c %x - %m%n");
        logConfigurator.configure();
    }
	
	private static Level toLog4jLevel(final int level) {
		switch(level) {
		case VERBOSE:
			return Level.TRACE;
		case DEBUG:
			return Level.DEBUG;
		case INFO:
			return Level.INFO;
		case WARN:
			return Level.WARN;
		case ERROR:
			return Level.ERROR;
		default:
			return Level.TRACE;
		}
	}
	
	/////////////////////////////////////////////////////////
	/////////////////////////////////////////////////////////

	/**
	 * Set the level of the logger
	 * @param level	The level to set for the logger
	 */
	public static void setLevel(int level) {
		mLevel = level;
	}

	/**
	 * Log a 'verbose' level message
	 * @param tag		The log tag
	 * @param message	The log message
	 */
	public final static void v(String tag, String message){
		if( mLevel > VERBOSE ) return;
		android.util.Log.v(tag, message);
	}
	public final static void v(Logger log, String message) {
		log.trace(message);
	}

	/**
	 * Log a 'debug' level message
	 * @param tag		The log tag
	 * @param message	The log message
	 */
	public final static void d(String tag, String message){
		if( mLevel > DEBUG ) return;
		android.util.Log.d(tag, message);
	}
	public final static void d(Logger log, String message) {
		log.debug(message);
	}

	/**
	 * Log an 'info' level message
	 * @param tag		The log tag
	 * @param message	The log message
	 */
	public final static void i(String tag, String message){
		if( mLevel > INFO ) return;
		android.util.Log.d(tag, message);
	}
	public final static void i(Logger log, String message) {
		log.info(message);
	}

	/**
	 * Log a 'warn' level message
	 * @param tag		The log tag
	 * @param message	The log message
	 */
	public final static void w(String tag, String message){
		if( mLevel > WARN ) return;
		android.util.Log.w(tag, message);
	}
	public final static void w(Logger log, String message) {
		log.warn(message);
	}

	/**
	 * Log an 'error' level message
	 * @param tag		The log tag
	 * @param message	The log message
	 * @param ex		The exception thrown
	 */
	public final static void e(String tag, String message, Throwable ex){
		if( mLevel > ERROR ) return;
		android.util.Log.e(tag, message);
		if(ex != null) {
			ex.printStackTrace();
			
			// We can only report Exceptions to BugSense and not
			// Throwables, so cast if possible
			if(ex instanceof Exception)
				BugSenseHandler.sendException((Exception)ex);
		}
	}
	public final static void e(Logger log, String message, Throwable ex) {
		log.error(message, ex);
	}
}
