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
package nmap.modules;

import java.util.ArrayList;
import nmap.network.Host;
import org.xml.sax.Attributes;
import nmap.utill.Tag;

/**
 *
 * @author Elia Calligaris
 */
public class HostParser {

    public java.util.List<Host> parse(java.util.List<Tag> tags) throws WrongTagException {
        ArrayList<Host> ans = new ArrayList<>();
        for (Tag t : tags) {
            ans.add(parseHost(t));
        }
        return ans;
    }

    protected void parse(Tag t, Host host) {
        switch (t.getName()) {
            case "status":
                parseStatus(t, host);
                break;
            case "address":
                parseAddress(t, host);
                break;
            case "hostname":
                parseHostname(t, host);
                break;
            case "service":
                parseService(t, host);
                break;
            case "hop":
                parseHop(t, host);
                break;
            case "osmatch":
                parseOSMatch(t, host);
                break;
            case "osclass":
                parseOSClass(t, host);
                break;
            default:
                parseChildren(t, host);
        }
    }

    protected Host parseHost(Tag h) throws WrongTagException {
        testTagType(h.getName(), "host");
        Host host = new Host(h.getAttributeValue("comment"));
        for (Tag ch : h.getChildren()) {
            parse(ch, host);
        }
        return host;
    }

    /**
     * Parse the host's state
     */
    protected void parseStatus(Tag s, Host h) {
        boolean up = false;
        if (s.getAttributeValue("state").equals("up")) {
            up = true;
        }
        h.setStatus(up);
    }

    /**
     * Parse the host's IP address
     */
    protected void parseAddress(Tag a, Host h) {
        if (a.getAttributeValue("addrtype").equals("ipv4")) {
            h.setIpAddress(a.getAttributeValue("addr"));
        } else if (a.getAttributeValue("addrtype").equals("mac")) {
            h.setMacAddress(a.getAttributeValue("addr"));
        }
    }

    /**
     * Parse the hostnames
     */
    protected void parseHostname(Tag hn, Host h) {
        h.setHostname(hn.getAttributeValue("name"));
    }

    protected void parseService(Tag s, Host h) {
        String product = s.getAttributeValue("product");
        // TODO type inference
    }

    /**
     * Parse the traceroute entries to find neighboring nodes
     */
    protected void parseHop(Tag s, Host h) {
        /* int ttl=Integer.parseInt(s.getAttributeValue("ttl"));
         if(ttl==1){
            
         }*/
    }

    protected void parseOSMatch(Tag os, Host h) {
        String match = os.getAttributeValue("name");
        String accuracy = os.getAttributeValue("accuracy");
        // TODO here
    }
    
    protected void parseOSClass(Tag os, Host h) {
        System.out.println("Parsing osclass: "+os);
        String type = os.getAttributeValue("type");
        String vendor=os.getAttributeValue("vendor");
        String family = os.getAttributeValue("osfamily");
        String accuracy = os.getAttributeValue("accuracy");
        // TODO here
    }

    protected void parseChildren(Tag t, Host h) {
        for (Tag child : t.getChildren()) {
            parse(child, h);
        }
    }

    /**
     * Tests if the given tag name matches with the required one.
     *
     * @param type Tag name to test.
     * @param requiredType Tag name required.
     * @throws WrongTagException
     */
    protected void testTagType(String type, String requiredType) throws WrongTagException {
        if (!type.equals("host")) {
            StringBuilder err = new StringBuilder();
            err.append("Expected a ").append(requiredType).append(" Tag, instead the Tag type is ").append(type);
            throw new WrongTagException(err.toString());
        }
    }
}
