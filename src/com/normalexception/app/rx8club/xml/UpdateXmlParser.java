package com.normalexception.app.rx8club.xml;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class UpdateXmlParser {
	
    // We don't use namespaces
    private static final String ns = null;
   
    /**
     * This is the start of the parsing an XML file for its feed contents
     * @param in	The input stream for the XML
     * @return		A set of Entry's that are found in the XML file
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }
    
    /**
     * Read the feed by walking through the root 'entry'
     * @param parser	The XML parser object
     * @return			A list of entries in the XML file
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        parser.require(XmlPullParser.START_TAG, ns, "feed");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("entry")) {
                entries.add(readEntry(parser));
            } else {
                skip(parser);
            }
        }  
        return entries;
    }
    
    /**
     * An Entry object that contains data from our XML file
     */
    public static class Entry {
    	public final int id;
        public final String version, summary, link;
        private Entry(String version, int id, String summary, String link) {
            this.version = version;
            this.id = id;
            this.summary = summary;
            this.link = link;
        }
    }
      
    /**
     * Parses the contents of an entry. It is expected that we are going to
     * parse out an ID, version, summary, and a link
     * @param parser	The XML object that is being parsed.
     * @return			An entry object that was parsed from the XML
     * @throws XmlPullParserException
     * @throws IOException
     */
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "entry");
        String version = null, summary = null, link = null;
        int id = 0;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("id")) {
            	id = readId(parser);
            } else if (name.equals("version")) {
                version = readVersion(parser);
            } else if (name.equals("summary")) {
            	summary = readSummary(parser);
            } else if (name.equals("link")) {
            	link = readLink(parser);
            } else {
                skip(parser);
            }
        }
        return new Entry(version, id, summary, link);
    }
    
    /**
     * Read the link node from the XML
     * @param parser	The XML Parser object
     * @return			The link string from the file
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readValue(parser, "link");
    }

    /**
     * Read the id node from the XML
     * @param parser	The XML Parser object
     * @return			The id string from the file
     * @throws IOException
     * @throws XmlPullParserException
     */
    private int readId(XmlPullParser parser) throws IOException, XmlPullParserException {
    	return Integer.parseInt(readValue(parser, "id"));
    }

    /**
     * Read the summary node from the XML
     * @param parser	The XML Parser object
     * @return			The summary string from the file
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readValue(parser, "summary");
    }

    /**
     * Read the version node from the XML
     * @param parser	The XML Parser object
     * @return			The version string from the file
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readVersion(XmlPullParser parser) throws IOException, XmlPullParserException {
        return readValue(parser, "version");
    }

    /**
     * Convenience method for reading a value from the XML object
     * @param parser	The XML Parser object
     * @param tag		The tag we are reading from the XML file
     * @return			The tag's string from the file
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readValue(XmlPullParser parser, String tag) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, tag);
        String summary = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return summary;
    }

    /**
     * Read text object from the XML tree
     * @param parser	The xml parser object
     * @return			String read from the XML tree
     * @throws IOException
     * @throws XmlPullParserException
     */
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
    
    /**
     * Convenience method to skip a tag in the XML tree
     * @param parser	The XML parser object
     * @throws XmlPullParserException
     * @throws IOException
     */
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
}
