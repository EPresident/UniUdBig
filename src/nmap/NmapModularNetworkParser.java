/*
 * Copyright (C) 2015 Elia Calligaris <calligaris.elia@spes.uniud.it> 
 * and Luca Geatti <geatti.luca@spes.uniud.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package nmap;

import nmap.utill.Tag;
import java.io.File;
import java.util.LinkedList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import nmap.network.Host;
import nmap.network.Network;
import nmap.modules.HostParser;
import nmap.modules.TypedHostParser;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Parser that turns a valid Nmap scan file in a Network. Instead of parsing
 * directly into a Network, it creates Tag objects first, which are then
 * processed by specialized components into Hosts.
 *
 * @author Elia Calligaris
 */
public class NmapModularNetworkParser extends DefaultHandler {

    /**
     * Tells us whether interesting tags are ahead or not (mainly those inside a
     * host tag)
     */
    private boolean parseEnabled = false;
    /**
     * Lists of first level Tag objects parsed in the last parsing run.
     */
    private LinkedList<Tag> interestingTags;
    private XMLReader xmlReader;
    /**
     * Interesting tags to parse. Child tags will be parsed as well.
     */
    private static final String[] interestingTagNames = {"host"};
    private Stack<Tag> tagStack;

    public NmapModularNetworkParser() {
        // Initialize Parser
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser saxParser = spf.newSAXParser();
            xmlReader = saxParser.getXMLReader();
            xmlReader.setContentHandler(this);
        } catch (Exception ex) {
            // Swallow exceptions
            // Gulp!
        }
    }

    /**
     * Parse a Nmap scan file into a Network.
     *
     * @param file Valid XML scan file to read.
     * @return a Network with the detected Hosts
     * @throws Exception
     */
    public void parseFile(String file) throws Exception {
        interestingTags = new LinkedList<>();
        xmlReader.parse(convertToFileURL(file));
        Network net=new Network("testnet",new TypedHostParser().parse(interestingTags));
        System.out.println(net);
    }

    public static void main(String[] args) throws Exception {
        // Test
        NmapModularNetworkParser nnp = new NmapModularNetworkParser();
        nnp.parseFile("users_dimi_uniud_it.xml");
        nnp.parseFile("rete_2.xml");
    }

    /**
     * Turns a filename into an URL for the XMLReader to use.
     */
    private static String convertToFileURL(String filename) {
        String path = new File(filename).getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }

        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        return "file:" + path;
    }

    @Override
    public void startDocument() throws SAXException {
        // initialize utility variables
        parseEnabled = false;
        tagStack = new Stack<>();
    }

    @Override
    public void endDocument() throws SAXException {
        // Do nothing
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (isInteresting(localName)) {
            // Interesting tag found: start reading nested tags
            parseEnabled = true;
        }
        if (parseEnabled) {
            Tag t = new Tag(localName, attributes);
            // Add tag reference to parent tag, if present
            if (!tagStack.isEmpty()) {
                tagStack.peek().addChild(t);
            }
            tagStack.push(t);
        } else {
            // This tag is outside a Host tag, so it's not interesting.
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (isInteresting(localName) && parseEnabled) {
            // Done parsing an interesting tag
            parseEnabled = false;
            // Add the parsed tag to the list
            interestingTags.add(tagStack.pop());
        } else if (parseEnabled) {
            // done parsing a descendant tag
            tagStack.pop();
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Do nothing
    }

    /**
     * Returns true if a tag is deemed interesting. Interesting tag names are
     * stored in the interestingTags array.
     *
     * @param name Name of the tag
     * @return true if the tag is within the array.
     */
    private boolean isInteresting(String name) {
        boolean ans = false;
        int i = 0;
        while (ans == false && i < interestingTagNames.length) {
            if (name.equals(interestingTagNames[i])) {
                ans = true;
            }
            i++;
        }
        return ans;

    }

    private class Stack<T> {

        private LinkedList<T> stack;

        public Stack() {
            stack = new LinkedList<>();
        }

        boolean isEmpty() {
            return stack.size() == 0;
        }

        void push(T t) {
            stack.push(t);
        }

        T pop() {
            return stack.pop();
        }

        T peek() {
            return stack.peek();
        }
    }

}
