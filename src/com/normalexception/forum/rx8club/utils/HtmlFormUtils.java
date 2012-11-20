package com.normalexception.forum.rx8club.utils;

import org.jsoup.nodes.Document;

public class HtmlFormUtils {	

    /**
     * Report the value inside of an input element
     * @param pan	The panel where all of the input elements reside
     * @param name	The name of the input to get the value for
     * @return		The string value of the input
     */
    public static String getInputElementValue(Document pan, String name) {
    	return pan.select("input[name=" + name + "]").attr("value");
    }

}
