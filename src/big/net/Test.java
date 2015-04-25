package big.net;

import static big.prprint.BigPPrinterSimple.prettyPrint;
import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;

public class Test {

    public static Signature signature;

    public static void main(String[] args) {
        runTest1();
    }

    protected static void runTest1() {
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
        OuterName http_id_server = builder.addOuterName("http_id_server");
        OuterName tcp_id_server = builder.addOuterName("tcp_id_server");
        OuterName ipv4_id_server = builder.addOuterName("ipv4_id_server");
        OuterName eth_id_server = builder.addOuterName("eth_id_server");
        OuterName http_id_client = builder.addOuterName("http_id_client");
        OuterName tcp_id_client = builder.addOuterName("tcp_id_client");
        OuterName ipv4_id_client = builder.addOuterName("ipv4_id_client");
        OuterName eth_id_client = builder.addOuterName("eth_id_client");
        
        //Server
        Node server = builder.addNode("host", domain);
        server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName","server_google")));
        Node http_server = builder.addNode("stackNode", server, http_id_server, tcp_id_server);
        http_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","http_server_google")));
        Node tcp_server = builder.addNode("stackNode", server, tcp_id_server, ipv4_id_server);
        tcp_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","tcp_server_google")));
        Node ipv4_server = builder.addNode("stackNode", server, ipv4_id_server, eth_id_server);
        ipv4_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","ipv4_server_google")));
        Node eth_server = builder.addNode("stackNode", server, eth_id_server, eth_id_client);
        eth_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","eth_server_google")));
        
        //Client
        Node client = builder.addNode("host", domain);
        client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName","myClient")));
        Node http_client = builder.addNode("stackNode", client, http_id_client, tcp_id_client);
        http_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","http_my_client")));
        Node tcp_client = builder.addNode("stackNode", client, tcp_id_client, ipv4_id_client);
        tcp_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","tcp_my_client")));
        Node ipv4_client = builder.addNode("stackNode", client, ipv4_id_client, eth_id_client);
        ipv4_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","ipv4_my_client")));
        Node eth_client = builder.addNode("stackNode", client, eth_id_client, eth_id_server);
        eth_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","eth_my_client")));
        
        //Packet
        Node http_packet = builder.addNode("packet", client, http_id_client, http_id_server);
        http_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName","http_packet")));
        Node http_payload = builder.addNode("payload", http_packet);
        http_payload.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PayloadName","http_payload")));
        
        Bigraph bigraph = builder.makeBigraph();
        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        System.out.println(pp.prettyPrint(bigraph, "Bigrafo iniziale"));
        
        
        Matcher matcher = new Matcher();
        EncapRule encap = new EncapRule(EncapRule.getRedex(signature),
                						EncapRule.getReactum(signature), 
                						new InstantiationMap(2, 0, 1));
        while (matcher.match(bigraph, EncapRule.getRedex(signature)).iterator().hasNext()
        		&& !matcher.match(bigraph, ForwardRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = encap.apply(bigraph).iterator();
            bigraph = iterator.next();
            EncapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Encap"));
        }
        ForwardRule forward = new ForwardRule(ForwardRule.getRedex(signature),
        									ForwardRule.getReactum(signature), 
        									new InstantiationMap(1, 0));
        while (matcher.match(bigraph, ForwardRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = forward.apply(bigraph).iterator();
            bigraph = iterator.next();
            ForwardRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Forward"));
        }
        DecapRule decap = new DecapRule(DecapRule.getRedex(signature),
        								DecapRule.getReactum(signature),
        								new InstantiationMap(1,0));
        while (matcher.match(bigraph, DecapRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = decap.apply(bigraph).iterator();
            bigraph = iterator.next();
            DecapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Decap"));
        }

        for (Node n : bigraph.getNodes()) {
            StringBuilder sb = new StringBuilder();
            sb.append(n.toString()).append(" ");
            for (Property p : n.getProperties()) {
                if (!p.getName().equals("Owner")) {
                    sb.append(p.getName()).append(": ").append(p.get()+"\t");
                }
            }
            System.out.println(sb.toString());
        }

    }

    protected static void runTest2() {
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

        //Http packet exchange:
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
        builder.relink(tcp_server.getPort(0), tcp_client.getPort(1));
        builder.relink(tcp_server.getPort(1), tcp_client.getPort(0));

        client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Client")));
        http_client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato HTTP del Client")));
        tcp_client.attachProperty(new SharedProperty(new SimpleProperty<>("Name", "Strato TCP del Client")));
        http_payload.attachProperty(new SharedProperty(new SimpleProperty<>("Description", "Payload di un pacchetto HTTP GET")));

        Bigraph bigraph = builder.makeBigraph();

        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        //System.out.println(prettyPrint(bigraph));
        System.out.println(pp.prettyPrint(bigraph, "Test bigraph"));

    }
    
    
    
}
