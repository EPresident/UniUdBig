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
package big.hash;

import static big.net.Test.signature;
import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.LinkedList;

/**
 * Test class. Please ignore.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigHashTest {

    public static void main(String[] args) {
        Bigraph big = setupTestBigraph();
        BigHashFunction pg_bhf=new PlaceGraphBHF(), pl_bhf=new PlaceLinkBHF();
        System.out.println("PlaceGraphBHF: "+pg_bhf.bigHash(big));
        System.out.println("PlaceLinkBHF: "+pl_bhf.bigHash(big));
    }

    private static Bigraph setupTestBigraph() {
        LinkedList<Control> it = new LinkedList<>();
        Control ctrl_host = new Control("host", true, 0);
        it.add(ctrl_host);
        Control ctrl_stackNode = new Control("stackNode", true, 2);
        it.add(ctrl_stackNode);
        Control ctrl_domain = new Control("domain", true, 2);
        it.add(ctrl_domain);
        Control ctrl_firewall = new Control("firewall", true, 1);
        it.add(ctrl_firewall);
        Control ctrl_packet = new Control("packet", true, 2);
        it.add(ctrl_packet);
        Control ctrl_payload = new Control("payload", true, 0);
        it.add(ctrl_payload);
        signature = new Signature(it);

        //Http packet exchange:
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r = builder.addRoot();
        Node domain = builder.addNode("domain", r);
        //Server
        Node server = builder.addNode("host", domain);
        server.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("HostName", "server_google")));
        OuterName http_id_server = builder.addOuterName("http_id_server");
        OuterName tcp_id_server = builder.addOuterName("tcp_id_server");
        OuterName tcp_down_server = builder.addOuterName("tcp_down_server");
        Node http_server = builder.addNode("stackNode", server, http_id_server, tcp_id_server);
        http_server.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("ProtocolName", "http_server")));
        Node tcp_server = builder.addNode("stackNode", server, tcp_id_server, tcp_down_server);
        tcp_server.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("ProtocolName", "tcp_server_google")));

        //Client
        Node client = builder.addNode("host", domain);
        client.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("HostName", "myClient")));
        OuterName http_id_client = builder.addOuterName("http_id_client");
        OuterName tcp_id_client = builder.addOuterName("tcp_id_client");
        OuterName tcp_down_client = builder.addOuterName("tcp_down_client");
        Node http_client = builder.addNode("stackNode", client, http_id_client, tcp_id_client);
        http_client.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("ProtocolName", "http_my_client")));
        Node tcp_client = builder.addNode("stackNode", client, tcp_id_client, tcp_down_client);
        tcp_client.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("ProtocolName", "tcp_my_client")));
        //Packet
        Node http_packet = builder.addNode("packet", client, http_id_client, http_id_server);
        http_packet.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("PacketName", "http_packet")));
        Node http_payload = builder.addNode("payload", http_packet);
        http_payload.attachProperty(new SharedProperty<>(
                new SimpleProperty<>("PayloadName", "http_payload")));
        //TCP link
        builder.relink(tcp_server.getPort(0), tcp_client.getPort(1));
        builder.relink(tcp_server.getPort(1), tcp_client.getPort(0));

        return builder.makeBigraph();
    }
}
