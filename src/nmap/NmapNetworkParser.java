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

import java.io.File;
import java.util.LinkedList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import nmap.network.Host;
import nmap.network.Network;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * SAX Parser that turns a valid Nmap scan file in a Network.
 *
 * @see nmap.network.Network
 * @author Elia Calligaris
 */
public class NmapNetworkParser extends DefaultHandler {

    /**
     * Tells us whether interesting tags are ahead or not (those inside a host
     * tag)
     */
    private boolean parseHost = false;
    /**
     * Lists of Host objects parsed in the last parsing run.
     */
    private LinkedList<Host> hosts;
    private XMLReader xmlReader;
    /**
     * Dummy Host used during parsing, later cloned and added to the list.
     */
    private Host dummyHost;
    /**
     * List where hints to the host's type are stored pending analysis
     */
    private LinkedList<String> typeHints=new LinkedList<>();

    public NmapNetworkParser() {
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
    public Network parseFile(String file) throws Exception {
        hosts = new LinkedList<>();
        xmlReader.parse(convertToFileURL(file));
        return new Network(hosts);
    }

    public static void main(String[] args) throws Exception {
        // Test
        NmapNetworkParser nnp = new NmapNetworkParser();
        System.out.println(nnp.parseFile("sample_nmap.xml"));
        //System.out.println(nnp.parseFile("users_dimi_uniud_it.xml"));
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
        parseHost = false;
    }

    @Override
    public void endDocument() throws SAXException {
        // Do nothing
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("host")) {
            // Host tag found: start reading nested tags
            parseHost = true;
        }
        if (parseHost) {
            // Depending on the tag, perform different parsing operations
            switch (localName) {
                case "host":
                    parseHost(attributes);
                    break;
                case "status":
                    parseStatus(attributes);
                    break;
                case "address":
                    parseAddress(attributes);
                    break;
                case "hop":
                    parseHop(attributes);
                    break;
                case "hostname":
                    parseHostName(attributes);
                    break;
                case "service":
                    parseService(attributes);
                    break;
                case "osmatch":
                    parseOSMatch(attributes);
                    break;
                default: // do nothing
            }
        } else {
            // This tag is outside a Host tag, so it's not interesting.
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (localName.equals("host")) {
            // Dope parsing a host
            parseHost = false;
            System.out.println(typeHints);
            // Add the parsed host to the list
            try {
                hosts.add(dummyHost.clone());
            } catch (CloneNotSupportedException ex) {
                ex.printStackTrace(System.err);
            } finally {
                dummyHost = null;
                typeHints.clear();
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        // Do nothing
    }

    /**
     * Get the comment from a host tag
     */
    private void parseHost(Attributes attributes) {
        String c = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("comment")) {
                c = attributes.getValue(i);
            }
        }
        dummyHost = new Host(c);
    }

    /**
     * Parse the host's state
     */
    private void parseStatus(Attributes attributes) {
        boolean b = false;
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("state")) {
                if (attributes.getValue(i).equals("up")) {
                    b = true;
                }
            }
        }
        dummyHost.setStatus(b);
    }

    /**
     * Parse the host's IP address
     */
    private void parseAddress(Attributes attributes) {
        String addr = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("addr")) {
                addr = attributes.getValue(i);
            }
        }
        dummyHost.setIpAddress(addr);
    }

    /**
     * Parse the traceroute entries to find neighboring nodes
     */
    private void parseHop(Attributes attributes) {
        /* String addr = "";
         boolean neighbor = false;
         for (int i = 0; i < attributes.getLength(); i++) {
         if (attributes.getQName(i).equals("ipaddr")) {
         addr = attributes.getValue(i);
         }
         if (attributes.getQName(i).equals("ttl")) {
         if (Integer.parseInt(attributes.getValue(i)) == 1) {
         neighbor = true;
         }
         }
         }
         if (neighbor) {
         dummyHost.addNeighbor(addr);
         }*/
    }

    private void parseHostName(Attributes attributes) {
        String name = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("name")) {
                name = attributes.getValue(i);
            }
        }
        dummyHost.setHostname(name);
    }

    private void parseService(Attributes attributes) {
        String name = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("product")) {
                name = attributes.getValue(i);
            }
        }
        typeHints.add(name);
    }

    private void parseOSMatch(Attributes attributes) {
        String name = "";
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.getQName(i).equals("name")) {
                name = attributes.getValue(i);
            }
        }
        typeHints.add(name);
    }
    
    private void guessType(){
        
    }
}
