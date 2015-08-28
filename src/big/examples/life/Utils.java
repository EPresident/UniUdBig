/*
 * Copyright (C) 2015 EPresident <prez_enquiry@hotmail.com>
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
package big.examples.life;

import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public final class Utils {

    public static Node[] addEmptyLinks(Node parent, BigraphBuilder builder) {
        Node[] out = new Node[8];

        for (Node n : out) {
            n = builder.addNode("link", parent);
        }
        return out;
    }

    public static Node[] addNeighbors(int live, Node subject, Node root, BigraphBuilder builder) {
        Node[] out = new Node[8];

        for (int i = 0; i < 8; i++) {
            OuterName link = builder.addOuterName("linkN" + i),
                    state = builder.addOuterName("stateN" + i);
            if (i < live) {
                out[i] = builder.addNode("liveCell", root, link, state);
            } else {
                out[i] = builder.addNode("deadCell", root, link, state);
            }
            builder.addSite(out[i]);
            Node l = builder.addNode("link", out[i], subject.getPort(0).getHandle());
            builder.addSite(l);
        }
        return out;
    }

    public static Node[] addDeadNeighbors(int num, Node subject, Node root, BigraphBuilder builder) {
        Node[] out = new Node[num];

        for (int i = 0; i < num; i++) {
            OuterName link = builder.addOuterName("linkN" + i),
                    state = builder.addOuterName("stateN" + i);
            out[i] = builder.addNode("deadCell", root, link, state);
            builder.addSite(out[i]);
            Node l = builder.addNode("link", out[i], subject.getPort(0).getHandle());
            builder.addSite(l);
        }
        return out;
    }

    public static Node[] addLiveNeighbors(int num, Node subject, Node root, BigraphBuilder builder) {
        Node[] out = new Node[num];

        for (int i = 0; i < num; i++) {
            OuterName link = builder.addOuterName("linkN" + i),
                    state = builder.addOuterName("stateN" + i);
            out[i] = builder.addNode("liveCell", root, link, state);
            builder.addSite(out[i]);
            Node l = builder.addNode("link", out[i], subject.getPort(0).getHandle());
            builder.addSite(l);
        }
        return out;
    }

}
