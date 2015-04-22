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
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Stupid simple parser for Nmap-generated XML scan files.
 * @author Elia Calligaris
 */
public class NmapDumpParser extends DefaultHandler {

    private short indent = 0;
    private boolean print = false;

    public static void main(String[] args) throws Exception {
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser saxParser = spf.newSAXParser();
        XMLReader xmlReader = saxParser.getXMLReader();
        xmlReader.setContentHandler(new NmapDumpParser());
        xmlReader.parse(convertToFileURL("sample_nmap.xml"));
    }

    private String indent(String s) {
        String sInd = "";
        for (int i = 0; i < indent; i++) {
            sInd += "-";
        }
        return sInd + s;
    }

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
        indent = 0;
        print = false;
        System.out.println("Begin document");
    }

    @Override
    public void endDocument() throws SAXException {
        // Do nothing
        System.out.println("Document end.");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (localName.equals("host")) {
            print = true;
        }
        if (print) {
            int nAttributes = attributes.getLength();
            String attr = "";
            for (int i = 0; i < nAttributes; i++) {
                attr += attributes.getQName(i) + "=" + attributes.getValue(i) + " ";
            }
            System.out.println(indent("< " + localName + " - " + attr + ">"));
        }
        indent++;
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        indent--;
        if (localName.equals("host")) {
            print = false;
        }
        if (print) {
            System.out.println(indent("</" + localName + ">"));
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (print) {
            System.out.println(indent(new String(ch)));
        }
    }
}
