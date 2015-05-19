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
package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.LinkedList;

/**
 * Utility methods and constants for "network" Bigraphs
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Utils {

    private static Signature NET_SIGNATURE = null;
    private static Bigraph BIG_PKT_XCG = null;
    private static RewritingRule[] NET_RULES = null;

    /**
     * Generate net signature.
     *
     * @return Signature for net bigraphs.
     */
    public static Signature getNetSignature() {
        if (NET_SIGNATURE == null) {
            LinkedList<Control> it = new LinkedList<>();
            Control ctrl_host = new Control("host", true, 0);
            it.add(ctrl_host);
            Control ctrl_stackNode = new Control("stackNode", true, 2);
            it.add(ctrl_stackNode);
            Control ctrl_domain = new Control("domain", true, 1);
            it.add(ctrl_domain);
            Control ctrl_firewall = new Control("firewall", true, 2);
            it.add(ctrl_firewall);
            Control ctrl_tokenIN = new Control("tokenIN", true, 1);
            it.add(ctrl_tokenIN);
            Control ctrl_tokenOUT = new Control("tokenOUT", true, 1);
            it.add(ctrl_tokenOUT);
            Control ctrl_packet = new Control("packet", true, 2);
            it.add(ctrl_packet);
            Control ctrl_payload = new Control("payload", true, 0);
            it.add(ctrl_payload);
            Control ctrl_router = new Control("router", true, 0);
            it.add(ctrl_router);
            NET_SIGNATURE = new Signature(it);
        }
        return NET_SIGNATURE;
    }

    /**
     * Generate a bigraph representing a http packet exchange between a client
     * and a server.
     *
     * @return The Bigraph described above.
     */
    public static Bigraph clientServerPacketExchange() {
        return clientServerPacketExchange(getNetSignature());
    }

    /**
     * Generate a bigraph representing a http packet exchange between a client
     * and a server.
     *
     * @param signature The signature for "net" bigraphs
     * @return The Bigraph described above.
     */
    public static Bigraph clientServerPacketExchange(Signature signature) {
        if (BIG_PKT_XCG == null) {
            BigraphBuilder builder = new BigraphBuilder(signature);
            Root r = builder.addRoot();
            OuterName localLinkC = builder.addOuterName("localLinkC");
            OuterName localLinkS = builder.addOuterName("localLinkS");
            Node bigDomain = builder.addNode("domain", r);
            bigDomain.attachProperty(new SharedProperty<>(new SimpleProperty<>("Name", "BigDomain")));
            Node myDomain = builder.addNode("domain", bigDomain);
            myDomain.attachProperty(new SharedProperty<>(new SimpleProperty<>("Name", "myDomain")));
            Node serverDomain = builder.addNode("domain", bigDomain);
            serverDomain.attachProperty(new SharedProperty<>(new SimpleProperty<>("Name", "googleDomain")));
            //Client's outernames
            OuterName http_ic = builder.addOuterName("http_id_client");
            OuterName tcp_ic = builder.addOuterName("tcp_id_client");
            OuterName ipv4_ic = builder.addOuterName("ipv4_id_client");
            OuterName eth_ic = builder.addOuterName("eth_id_client");
            OuterName eth_dc = builder.addOuterName("eth_down_client");
            //RouterClient's outername
            OuterName ipv4_irci = builder.addOuterName("ipv4_router_client_in");
            OuterName eth_irci = builder.addOuterName("eth_id_router_client_in");
            OuterName ipv4_irco = builder.addOuterName("ipv4_router_client_out");
            OuterName eth_irco = builder.addOuterName("eth_id_router_client_out");

            //Server's outernames
            OuterName http_is = builder.addOuterName("http_id_server");
            OuterName tcp_is = builder.addOuterName("tcp_id_server");
            OuterName ipv4_is = builder.addOuterName("ipv4_id_server");
            OuterName eth_is = builder.addOuterName("eth_id_server");
            OuterName eth_ds = builder.addOuterName("eth_down_server");
            //RouterServer's outername
            OuterName ipv4_irsi = builder.addOuterName("ipv4_id_router_google_in");
            OuterName eth_irsi = builder.addOuterName("eth_id_router_google_in");
            OuterName ipv4_irso = builder.addOuterName("ipv4_id_router_google_out");
            OuterName eth_irso = builder.addOuterName("eth_id_router_google_out");

            //LINK ROUTER
            OuterName ipv4_irc = builder.addOuterName("ipv4_irc");
            OuterName ipv4_irs = builder.addOuterName("ipv4_irs");
            OuterName linkIPRouters = builder.addOuterName("LinkIPRouters");

            //Client
            Node client = builder.addNode("host", myDomain);
            client.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "myClient")));
            Node http_client = builder.addNode("stackNode", client, http_ic, tcp_ic);
            http_client.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "http_my_client")));
            Node tcp_client = builder.addNode("stackNode", client, tcp_ic, ipv4_ic);
            tcp_client.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "tcp_my_client")));
            Node ipv4_client = builder.addNode("stackNode", client, ipv4_ic, localLinkC);
            ipv4_client.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "ipv4_my_client")));
        //Node eth_client = builder.addNode("stackNode", client, eth_ic, link1);
            //eth_client.attachProperty(new SharedProperty<String>(
            //new SimpleProperty<String>("ProtocolName","eth_my_client")));

            //Client's router
            Node ipv4_router_client = builder.addNode("stackNode", myDomain, linkIPRouters, localLinkC);
            ipv4_router_client.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "ipv4_router_client")));

            //Server
            Node server = builder.addNode("host", serverDomain);
            server.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "server_google")));
            Node http_server = builder.addNode("stackNode", server, http_is, tcp_is);
            http_server.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "http_server_google")));
            Node tcp_server = builder.addNode("stackNode", server, tcp_is, ipv4_is);
            tcp_server.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "tcp_server_google")));
            Node ipv4_server = builder.addNode("stackNode", server, ipv4_is, localLinkS);
            ipv4_server.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "ipv4_server_google")));
        //Node eth_server = builder.addNode("stackNode", server, eth_is, link1);
            //eth_server.attachProperty(new SharedProperty<String>(
            //new SimpleProperty<String>("ProtocolName","eth_server_google")));

            //Server's Router
            Node ipv4_router_server = builder.addNode("stackNode", serverDomain, linkIPRouters, localLinkS);
            ipv4_router_server.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "ipv4_router_server")));

            //Packet
            Node http_packet = builder.addNode("packet", client, http_ic, http_is);
            http_packet.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "http_packet")));
            Node http_payload = builder.addNode("payload", http_packet);
            http_payload.attachProperty(new SharedProperty<>(
                    new SimpleProperty<>("Name", "http_payload")));

            BIG_PKT_XCG = builder.makeBigraph();
        }
        return BIG_PKT_XCG;
    }

    /**
     * Generate an array with instances of the rewriting rules for network
     * bigraphs.
     *
     * @return An array of RewritingRule instances for net bigraphs.
     */
    public static RewritingRule[] getNetRules() {
        if (NET_RULES == null) {
            NET_RULES = new RewritingRule[]{new EncapRule(), new DFRule(),
                new Domain2HostRule(), new DecapRule()};
        }
        return NET_RULES;
    }
}
