/*
 * No license ATM
 */
package nmap.network;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Abstraction of a Network scanned through Nmap.
 *
 * @author Elia Calligaris
 */
public class Network {

    private ArrayList<Host> hosts;
    private String name;

    public Network(Collection<Host> hosts) {
        this("My network", hosts);
    }

    public Network(String name, Collection<Host> hosts) {
        this.hosts = new ArrayList<>();
        this.hosts.addAll(hosts);
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        String s = "Begin Network \"" + name + "\": \n";
        for (Host h : hosts) {
            s += "--->" + h + "\n";
        }
        return s + "End Network.";
    }
}
