/*
 * No license ATM
 */
package nmap.network;

import java.util.ArrayList;
import java.util.List;

/**
 * Nmap representation of a node.
 *
 * @author Elia Calligaris
 */
public class Host {

    private String ipAddress, macAddress, hostname, comment, os;
    private HostType type;

    public enum HostType {

        SWITCH, GENERIC, SERVER
    };
    private boolean isUp;
    /**
     * Addresses (assumed IPv4) of neighboring nodes (1 hop)
     */
    private ArrayList<String> neighbors;

    public Host(String hostname, String comment, String ipAddr, boolean up, List<String> neighbors, HostType hostType) {
        this.hostname = hostname;
        this.comment = comment;
        ipAddress = ipAddr;
        isUp = up;
        this.neighbors = new ArrayList<>(neighbors);
        type = hostType;
    }

    public Host() {
        this("", "", "x.x.x.x", false, new ArrayList<String>(), HostType.GENERIC);
    }

    public Host(String comment) {
        this("", comment, "x.x.x.x", false, new ArrayList<String>(), HostType.GENERIC);
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setStatus(boolean up) {
        this.isUp = up;
    }

    public void addNeighbor(String ipAddr) {
        neighbors.add(ipAddr);
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public void setType(HostType type) {
        this.type = type;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append("Host: ");
        if (hostname != null) {
            if (!hostname.equals("") && !hostname.equals("null")) {
                out.append(hostname).append(" ");
            }
        }
        out.append("<").append(ipAddress).append(">");
        if (macAddress != null) {
            if (!macAddress.equals("") && !macAddress.equals("null")) {
                out.append(" <").append(macAddress).append(">");
            }
        }
        out.append(" (").append(type.toString());
        if (comment != null) {
            if (!comment.equals("") && !comment.equals("null")) {
                out.append(" - ").append(comment);
            }
        }
        String status = "DOWN";
        if (isUp) {
            status = "UP";
        }
        out.append(") - Status: ").append(status);
        if (neighbors.size() > 0) {
            out.append(" - Neighbors: ");
            for (String addr : neighbors) {
                out.append(addr).append(" ");
            }
        }
        if (os != null) {
            if (!os.equals("") && !os.equals("null")) {
                out.append(" - OS: ").append(os);
            }
        }

        return out.toString();
    }

    @Override
    public Host clone() throws CloneNotSupportedException {
        return new Host(this.hostname, this.comment, this.ipAddress, this.isUp, this.neighbors, this.type);
    }

}
