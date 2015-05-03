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
package big.sim;

import big.net.EncapRule;
import big.net.ForwardRule;
import static big.net.Test.signature;
import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Test class for Bigraphic Reactive Systems simulation using BigStateGraph
 * @see BigStateGraph
 * 
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class SimTest {

    public static void main(String[] args) {
        LinkedList<Control> it = new LinkedList<>();
        Control ctrl_host = new Control("host", true, 0);
        it.add(ctrl_host);
        Control ctrl_stackNode = new Control("stackNode", true, 2);
        it.add(ctrl_stackNode);
        Control ctrl_domain = new Control("domain", true, 0);
        it.add(ctrl_domain);
        Control ctrl_firewall = new Control("firewall", true, 1);
        it.add(ctrl_firewall);
        Control ctrl_packet = new Control("packet", true, 2);
        it.add(ctrl_packet);
        Control ctrl_payload = new Control("payload", true, 0);
        it.add(ctrl_payload);
        signature = new Signature(it);

        // Encap test:
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r = builder.addRoot();
        Node domain = builder.addNode("domain", r);
        //Server
        Node server = builder.addNode("host", domain);
        OuterName http_id_server = builder.addOuterName("http_id_server");
        OuterName tcp_id_server = builder.addOuterName("tcp_id_server");
        OuterName tcp_down_server = builder.addOuterName("tcp_down_server");
        Node http_server = builder.addNode("stackNode", server, http_id_server, tcp_id_server);
        Node tcp_server = builder.addNode("stackNode", server, tcp_id_server, tcp_down_server);
        // Node tcp_server = builder.addNode("stackNode", server, tcp_id_server, null);

        server.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Server Google")));
        http_server.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato HTTP del Server")));
        http_server.attachProperty(new SharedProperty(new SimpleProperty<>("Port0Name", "ID")));
        http_server.attachProperty(new SharedProperty(new SimpleProperty<>("Port1Name", "DOWN")));
        tcp_server.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato TCP del Server")));

        //Client
        Node client = builder.addNode("host", domain);
        OuterName http_id_client = builder.addOuterName("http_id_client");
        OuterName tcp_id_client = builder.addOuterName("tcp_id_client");
        OuterName tcp_down_client = builder.addOuterName("tcp_down_client");
        Node http_client = builder.addNode("stackNode", client, http_id_client, tcp_id_client);
        Node tcp_client = builder.addNode("stackNode", client, tcp_id_client, tcp_down_client);

        //Packet
        Node http_packet = builder.addNode("packet", client, http_id_client, http_id_server);
        Node http_payload = builder.addNode("payload", http_packet);
        //TCP link
        // builder.relink(tcp_server.getPort(0), tcp_client.getPort(1));
        // builder.relink(tcp_server.getPort(1), tcp_client.getPort(0));

        client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Client")));
        http_client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato HTTP del Client")));
        tcp_client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato TCP del Client")));
        http_payload.attachProperty(new SharedProperty(new SimpleProperty<>("Description", "Payload di un pacchetto HTTP GET")));
        http_packet.attachProperty(new SharedProperty<>(new SimpleProperty<>("Name", "Pacchetto HTTP GET")));

        Bigraph bigraph = builder.makeBigraph();
        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        BigStateGraph bsg = new BigStateGraph(bigraph);

        Matcher matcher = new Matcher();
        EncapRule encap = new EncapRule(EncapRule.getRedex(signature),
                EncapRule.getReactum(signature),
                new InstantiationMap(2, 0, 1));
        int encaps = 1;
        while (matcher.match(bigraph, EncapRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = encap.apply(bigraph).iterator();
            bigraph = iterator.next();
            bsg.applyRewritingRule("Encap", bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Encap" + encaps));
            EncapRule.clearAuxProperties(bigraph);
            encaps++;
        }
        ForwardRule forward = new ForwardRule(ForwardRule.getRedex(signature),
                ForwardRule.getReactum(signature),
                new InstantiationMap(1, 0));
        int forwards = 1;
        while (matcher.match(bigraph, ForwardRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = forward.apply(bigraph).iterator();
            bigraph = iterator.next();
            bsg.applyRewritingRule("Forward", bigraph);
            ForwardRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Forward"+forwards));
            forwards++;
        }

    }
}
