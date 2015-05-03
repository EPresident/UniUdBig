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
        Control ctrl_domain = new Control("domain", true, 1);
        it.add(ctrl_domain);
        Control ctrl_firewall = new Control("firewall", true, 1);
        it.add(ctrl_firewall);
        Control ctrl_packet = new Control("packet", true, 2);
        it.add(ctrl_packet);
        Control ctrl_payload = new Control("payload", true, 0);
        it.add(ctrl_payload);
        Control ctrl_router = new Control("router", true, 0);
        it.add(ctrl_router);
        signature = new Signature(it);
        
        
        /*
         * -----------------------------------------------------
         * Http packet exchange
         * -----------------------------------------------------
         */
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r = builder.addRoot();
        OuterName localLinkC = builder.addOuterName("localLinkC");
        OuterName localLinkS = builder.addOuterName("localLinkS");
        Node bigDomain = builder.addNode("domain",r);
        bigDomain.attachProperty(new SharedProperty<String>
        						( new SimpleProperty<String>("DomainName","BigDomain") ));
        Node myDomain = builder.addNode("domain", bigDomain);
        myDomain.attachProperty(new SharedProperty<String>
								( new SimpleProperty<String>("DomainName","myDomain") ));
        Node serverDomain = builder.addNode("domain", bigDomain);
        serverDomain.attachProperty(new SharedProperty<String>
								( new SimpleProperty<String>("DomainName","googleDomain") ));
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
        client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName","myClient")));
        Node http_client = builder.addNode("stackNode", client, http_ic, tcp_ic);
        http_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","http_my_client")));
        Node tcp_client = builder.addNode("stackNode", client, tcp_ic, ipv4_ic);
        tcp_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","tcp_my_client")));
        Node ipv4_client = builder.addNode("stackNode", client, ipv4_ic, localLinkC);
        ipv4_client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","ipv4_my_client")));
        //Node eth_client = builder.addNode("stackNode", client, eth_ic, link1);
        //eth_client.attachProperty(new SharedProperty<String>(
				//new SimpleProperty<String>("ProtocolName","eth_my_client")));
        
        //Client's router
        Node ipv4_router_client = builder.addNode("stackNode", myDomain, linkIPRouters, localLinkC);
        ipv4_router_client.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("Router","ipv4_router_client")));
        
        
        
        //Server
        Node server = builder.addNode("host", serverDomain);
        server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName","server_google")));
        Node http_server = builder.addNode("stackNode", server, http_is, tcp_is);
        http_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","http_server_google")));
        Node tcp_server = builder.addNode("stackNode", server, tcp_is, ipv4_is);
        tcp_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","tcp_server_google")));
        Node ipv4_server = builder.addNode("stackNode", server, ipv4_is, localLinkS);
        ipv4_server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName","ipv4_server_google")));
        //Node eth_server = builder.addNode("stackNode", server, eth_is, link1);
        //eth_server.attachProperty(new SharedProperty<String>(
				//new SimpleProperty<String>("ProtocolName","eth_server_google")));
        
        //Server's Router
        Node ipv4_router_server = builder.addNode("stackNode", serverDomain, linkIPRouters, localLinkS);
        ipv4_router_server.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("Router","ipv4_router_server")));
       
        
        //Packet
        Node http_packet = builder.addNode("packet", client, http_ic, http_is);
        http_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName","http_packet")));
        Node http_payload = builder.addNode("payload", http_packet);
        http_payload.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PayloadName","http_payload")));
        
        
        Bigraph bigraph = builder.makeBigraph();
        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        System.out.println(pp.prettyPrint(bigraph, "Bigrafo iniziale"));
        
        
        
        /*
         * ---------------------------------------------------------------
         * Start of the reactions.
         * ---------------------------------------------------------------
         */
        
        Matcher matcher = new Matcher();
        EncapRule encap = new EncapRule(EncapRule.getRedex(signature),
                						EncapRule.getReactum(signature), 
                						new InstantiationMap(2, 0, 1));
        while (matcher.match(bigraph, EncapRule.getRedex(signature)).iterator().hasNext() ) {
        	if( matcher.match(bigraph, DFRule.getRedex(signature)).iterator().hasNext() ){
        		break;
        	}
            Iterator<Bigraph> iterator = encap.apply(bigraph).iterator();
            bigraph = iterator.next();
            EncapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Encap"));
        }
        DFRule ip_forward = new DFRule(DFRule.getRedex(signature),
        							DFRule.getReactum(signature), 
        							new InstantiationMap(5, 0, 1, 2, 3, 4));
        if (matcher.match(bigraph, DFRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = ip_forward.apply(bigraph).iterator();
            bigraph = iterator.next();
            DFRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Direct Forward"));
        }
        
        Domain2HostRule d2h = new Domain2HostRule( Domain2HostRule.getRedex(signature),
        						Domain2HostRule.getReactum(signature),
        						new InstantiationMap(3, 0, 1, 2) );
        if( matcher.match(bigraph, Domain2HostRule.getRedex(signature)).iterator().hasNext() ){
        	Iterator<Bigraph> iterator = d2h.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	Domain2HostRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Domain2Host Rule"));
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
    
    
    
    
    
    
}
