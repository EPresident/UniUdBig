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
        signature = new Signature(it);
        
        
        /*
         * -----------------------------------------------------
         * Http packet exchange
         * -----------------------------------------------------
         */
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r = builder.addRoot();
        //Client
        Node domainC = builder.addNode("domain", r);
        domainC.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("DomainName","Client_Domain")));
        Node client = builder.addNode("host", domainC);
        client.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("HostName","Client")));
        OuterName http_idc = builder.addOuterName("http_idc");
        OuterName tcp_idc = builder.addOuterName("tcp_idc");
        Node http_C = builder.addNode("stackNode", client, http_idc, tcp_idc);
        http_C.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Http_Client")));
        OuterName ip_idc = builder.addOuterName("192.168.0.1");
        Node tcp_C = builder.addNode("stackNode", client, tcp_idc, ip_idc);
        tcp_C.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Tcp_Client")));
        OuterName localC = builder.addOuterName("localC");
        Node ip_C = builder.addNode("stackNode", client, ip_idc, localC);
        ip_C.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Ip_Client")));
        
        //Firewall
        OuterName listFWCIN = builder.addOuterName("IN_CLIENT%192.168.0.1%192.168.0.2%192.168.0.3");
        OuterName listFWCOUT = builder.addOuterName("OUT_CLIENT%158.130.0.1%158.130.0.2%158.130.0.3");
        Node firewallC = builder.addNode("firewall", domainC, listFWCIN, listFWCOUT);
        firewallC.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("FirewallName","Client_Firewall")));
        //Router's Client
        OuterName linkR1 = builder.addOuterName("linkR1");
        Node routerINC = builder.addNode("stackNode", firewallC, linkR1, localC);
        routerINC.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("RouterName","Client_Router_IN")));
        
        //Second Domain
        Node domain2 = builder.addNode("domain",r);
        domain2.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("DomainName","Second_Domain")));
        //Second Router
        OuterName local2 = builder.addOuterName("local2");
        Node router2 = builder.addNode("stackNode", domain2, linkR1, local2);
        router2.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("RouterName","router2")));
        //Third Router
        OuterName linkR2 = builder.addOuterName("linkR2");
        Node router3 = builder.addNode("stackNode", domain2, linkR2, local2);
        router3.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("RouterName","Third_Router")));
        
        //Server's Domain
        Node domainS = builder.addNode("domain", r);
        domainS.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("DomainName","Server_Domain")));
        //Server
        Node server = builder.addNode("host", domainS);
        server.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("HostName","Server")));
        OuterName http_ids = builder.addOuterName("http_ids");
        OuterName tcp_ids = builder.addOuterName("tcp_ids");
        Node http_S = builder.addNode("stackNode", server, http_ids, tcp_ids);
        http_S.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Http_Server")));
        OuterName ip_ids = builder.addOuterName("158.130.0.1");
        Node tcp_S = builder.addNode("stackNode", server, tcp_ids, ip_ids);
        tcp_S.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Tcp_Server")));
        OuterName localS = builder.addOuterName("localS");
        Node ip_S = builder.addNode("stackNode", server, ip_ids, localS);
        ip_S.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("ProtocolName","Ip_server")));
        //Server's Router 
        OuterName listFWSIN = builder.addOuterName("IN_SERVER%192.168.0.1%192.168.0.2%192.168.0.3");
        OuterName listFWSOUT = builder.addOuterName("OUT_SERVER%158.130.0.1%158.130.0.2%158.130.0.3");
        Node firewallS = builder.addNode("firewall", domainS, listFWSIN , listFWSOUT);
        firewallS.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("FirewallName","Server's Firewall")));
        Node routerS = builder.addNode("stackNode", firewallS, linkR2, localS);
        routerS.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("RouterName","Server_Router")));
        
        //Packet
        Node http_packet = builder.addNode("packet", client, http_idc, http_ids);
        http_packet.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("PacketName","Http_Packet")));
        Node http_payload = builder.addNode("payload", http_packet);
        http_payload.attachProperty(new SharedProperty<String>(
        		new SimpleProperty<String>("PacketName","Http_Payload")));
        
        
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
                						EncapRule.getInstMap());
        NewTokenINRuleNear ntirn = new NewTokenINRuleNear(NewTokenINRuleNear.getRedex(signature),
        		NewTokenINRuleNear.getReactum(signature),
        		NewTokenINRuleNear.getInstMap());
        while (matcher.match(bigraph, EncapRule.getRedex(signature)).iterator().hasNext() ) {
        	if( matcher.match(bigraph, NewTokenINRuleNear.getRedex(signature)).iterator().hasNext() ){
        		if(ntirn.isApplicable(bigraph, signature)){
        			break;
        		}
        	}
            Iterator<Bigraph> iterator = encap.apply(bigraph).iterator();
            bigraph = iterator.next();
            EncapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Encap"));
        }
        
        if(matcher.match(bigraph, NewTokenINRuleNear.getRedex(signature)).iterator().hasNext() ){
        	if(ntirn.isApplicable(bigraph, signature)){
        		Iterator<Bigraph> iterator = ntirn.apply(bigraph).iterator();
            	bigraph = iterator.next();
            	NewTokenINRuleNear.clearAuxProperties(bigraph);
            	System.out.println(pp.prettyPrint(bigraph, "New TokenIN Rule Near"));
        	}
        }
        
        DFRuleFW dfr = new DFRuleFW( DFRuleFW.getRedex(signature),
        		DFRuleFW.getReactum(signature),
        		DFRuleFW.getInstMap());
        if(matcher.match(bigraph, DFRuleFW.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = dfr.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	DFRuleFW.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Direct Forward with Firewall"));
        }
        
        NewTokenOUTRuleFar ntorf = new NewTokenOUTRuleFar(NewTokenOUTRuleFar.getRedex(signature),
        		NewTokenOUTRuleFar.getReactum(signature),
        		NewTokenOUTRuleFar.getInstMap());
        if(matcher.match(bigraph, NewTokenOUTRuleFar.getRedex(signature)).iterator().hasNext()){
        	if(ntorf.isApplicable(bigraph, signature)){
        		Iterator<Bigraph> iterator = ntorf.apply(bigraph).iterator();
        		bigraph = iterator.next();
        		NewTokenOUTRuleFar.clearAuxProperties(bigraph);
        		System.out.println(pp.prettyPrint(bigraph, "New Token OUT Far"));
        	}
        	
        }
        
        ForwardFWRule frf = new ForwardFWRule( ForwardFWRule.getRedex(signature),
        		ForwardFWRule.getReactum(signature),
        		ForwardFWRule.getInstMap());
        if(matcher.match(bigraph, ForwardFWRule.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = frf.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	ForwardFWRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Router-Router Forward with Firewall"));
        }
        
        ForwardRule fr = new ForwardRule( ForwardRule.getRedex(signature),
        		ForwardRule.getReactum(signature),
        		ForwardRule.getInstMap());
        if(matcher.match(bigraph, ForwardRule.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = fr.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	bigraph = iterator.next();
        	ForwardRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Forward"));
        }
        
        NewTokenOUTRuleFar ntorf2 = new NewTokenOUTRuleFar(NewTokenOUTRuleFar.getRedex(signature),
        		NewTokenOUTRuleFar.getReactum(signature),
        		NewTokenOUTRuleFar.getInstMap());
        if(matcher.match(bigraph, NewTokenOUTRuleFar.getRedex(signature)).iterator().hasNext()){
        	if( ntorf2.isApplicable(bigraph, signature) ){
	        	Iterator<Bigraph> iterator = ntorf2.apply(bigraph).iterator();
	        	bigraph = iterator.next();
	        	NewTokenOUTRuleFar.clearAuxProperties(bigraph);
	        	System.out.println(pp.prettyPrint(bigraph, "New Token OUT with Firewall"));
	        }
        }
        
        FWOUTRule fwor = new FWOUTRule( FWOUTRule.getRedex(signature),
        		FWOUTRule.getReactum(signature),
        		FWOUTRule.getInstMap());
        if(matcher.match(bigraph, FWOUTRule.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = fwor.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	FWOUTRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Firewall Out"));
        }
        
        Domain2HostRule d2hr = new Domain2HostRule( Domain2HostRule.getRedex(signature),
        		Domain2HostRule.getReactum(signature),
        		Domain2HostRule.getInstMap());
        if(matcher.match(bigraph, Domain2HostRule.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = d2hr.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	Domain2HostRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Domain2Host Rule"));
        }
        
        DecapRule decap = new DecapRule( DecapRule.getRedex(signature),
        		DecapRule.getReactum(signature),
        		DecapRule.getInstMap());
        while(matcher.match(bigraph, DecapRule.getRedex(signature)).iterator().hasNext()){
        	Iterator<Bigraph> iterator = decap.apply(bigraph).iterator();
        	bigraph = iterator.next();
        	DecapRule.clearAuxProperties(bigraph);
        	System.out.println(pp.prettyPrint(bigraph, "Decap Rule"));
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
    
    
   
    protected static void runTest2(){
    	
    }
    
}
