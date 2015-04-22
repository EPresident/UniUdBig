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

import java.util.LinkedList;
import nmap.network.Host;
import nmap.utill.Tag;

/**
 * HostParser that tried to determine a Host's type
 *
 * @author Elia Calligaris
 */
public class TypedHostParser extends HostParser {

    private LinkedList<OSMatch> osMatches;
    private LinkedList<OSClass> osClasses;
    private LinkedList<String> services;
    private String vendor;
    private final static String[] SWITCH_KEYWORDS = {"router", "switch", "gateway"},
            SWITCH_VENDORS = {"Netgear", "Cisco", "D-Link", "Trust", "TP-Link"};

    public TypedHostParser() {
        osMatches = new LinkedList<>();
        osClasses = new LinkedList<>();
        services = new LinkedList<>();
    }

    @Override
    protected Host parseHost(Tag h) throws WrongTagException {
        testTagType(h.getName(), "host");
        // reset type hints
        osMatches.clear();
        osClasses.clear();
        services.clear();
        vendor="";
        Host host = new Host(h.getAttributeValue("comment"));
        for (Tag ch : h.getChildren()) {
            parse(ch, host);
        }
        guessOS(host);
        guessType(host);
        return host;
    }

    private void guessType(Host h) {
        // Service analysis
        int switchHints = 0;
        for (String s : services) {
            boolean hint = false;
            for (String k : SWITCH_KEYWORDS) {
                if (s.contains(k)) {
                    hint = true;
                    break;
                }
            }
            if (hint) {
                switchHints += 1;
            }
        }

        // Vendor analysis
        boolean hint=false;
        for(String v: SWITCH_VENDORS){
            if(vendor.contains(v)){
                hint=true;
                break;
            }
        }
        if(hint){
            switchHints+=1;
        }
        
        if (switchHints > 0) {
            h.setType(Host.HostType.SWITCH);
        }
    }

    private void guessOS(Host h) {
        if (!osClasses.isEmpty()) {
            OSClass osc = osClasses.getLast();
            //System.out.println("xx" + osc.family + " " + osc.vendor + " " + osc.type + " " + osc.accuracy);
            h.setOs(osc.family);
        } else {
            h.setOs("");
        }
    }

    @Override
    protected void parseOSMatch(Tag os, Host h) {
        String match = os.getAttributeValue("name");
        String accuracy = os.getAttributeValue("accuracy");
        osMatches.add(new OSMatch(match, Integer.parseInt(accuracy)));
        parseChildren(os, h);
    }

    @Override
    protected void parseOSClass(Tag os, Host h) {
        String type = os.getAttributeValue("type");
        String vendor = os.getAttributeValue("vendor");
        String family = os.getAttributeValue("osfamily");
        String accuracy = os.getAttributeValue("accuracy");
        osClasses.add(new OSClass(type, vendor, family, Integer.parseInt(accuracy)));
    }

    @Override
    protected void parseService(Tag s, Host h) {
        String product = s.getAttributeValue("product");
        services.add(product);
    }

    @Override
    protected void parseAddress(Tag a, Host h) {
        if (a.getAttributeValue("addrtype").equals("ipv4")) {
            h.setIpAddress(a.getAttributeValue("addr"));
        } else if (a.getAttributeValue("addrtype").equals("mac")) {
            h.setMacAddress(a.getAttributeValue("addr"));
            vendor=a.getAttributeValue("vendor");
        }
    }

    private class OSMatch {

        private String name;
        private int accuracy;

        OSMatch(String n, int a) {
            name = n;
            accuracy = a;
        }
    }

    private class OSClass {

        private String type, vendor, family;
        private int accuracy;

        OSClass(String t, String v, String f, int a) {
            type = t;
            vendor = v;
            family = f;
            accuracy = a;
        }
    }
}
