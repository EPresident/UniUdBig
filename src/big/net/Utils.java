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
	

	/**
	 * Generate net signature.
	 *
	 * @return Signature for net bigraphs.
	 */
	public static Signature getNetSignature() {
		if(NET_SIGNATURE == null){
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
	 * @param signature
	 *            The signature for "net" bigraphs
	 * @return The Bigraph described above.
	 * 
	 */
	public static Bigraph clientServerPacketExchange(Signature signature) {
		
			/*
			 * ----------------------------------------------------- Http packet
			 * exchange with Firewalls.
			 * -----------------------------------------------------
			 */
			BigraphBuilder builder = new BigraphBuilder(signature);
			Root r = builder.addRoot();
			// Client
			Node domainC = builder.addNode("domain", r);
			domainC.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("DomainName", "Client_Domain")));
			Node client = builder.addNode("host", domainC);
			client.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("HostName", "Client")));
			OuterName http_idc = builder.addOuterName("http_idc");
			OuterName tcp_idc = builder.addOuterName("tcp_idc");
			Node http_C = builder.addNode("stackNode", client, http_idc,
					tcp_idc);
			http_C.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Http_Client")));
			OuterName ip_idc = builder.addOuterName("192.168.0.1");
			Node tcp_C = builder.addNode("stackNode", client, tcp_idc, ip_idc);
			tcp_C.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Tcp_Client")));
			OuterName localC = builder.addOuterName("localC");
			Node ip_C = builder.addNode("stackNode", client, ip_idc, localC);
			ip_C.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Ip_Client")));

			// Firewall
			OuterName listFWCIN = builder
					.addOuterName("IN_CLIENT%192.168.0.1%192.168.0.2%192.168.0.3");
			OuterName listFWCOUT = builder
					.addOuterName("OUT_CLIENT%158.130.0.1%158.130.0.2%158.130.0.3");
			Node firewallC = builder.addNode("firewall", domainC, listFWCIN,
					listFWCOUT);
			firewallC.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("FirewallName",
							"Client_Firewall")));
			// Router's Client
			OuterName linkR1 = builder.addOuterName("linkR1");
			Node routerINC = builder.addNode("stackNode", firewallC, linkR1,
					localC);
			routerINC
					.attachProperty(new SharedProperty<String>(
							new SimpleProperty<String>("RouterName",
									"Client_Router_IN")));

			// Second Domain
			Node domain2 = builder.addNode("domain", r);
			domain2.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("DomainName", "Second_Domain")));
			// Second Router
			OuterName local2 = builder.addOuterName("local2");
			Node router2 = builder
					.addNode("stackNode", domain2, linkR1, local2);
			router2.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("RouterName", "router2")));
			// Third Router
			OuterName linkR2 = builder.addOuterName("linkR2");
			Node router3 = builder
					.addNode("stackNode", domain2, linkR2, local2);
			router3.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("RouterName", "Third_Router")));

			// Server's Domain
			Node domainS = builder.addNode("domain", r);
			domainS.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("DomainName", "Server_Domain")));
			// Server
			Node server = builder.addNode("host", domainS);
			server.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("HostName", "Server")));
			OuterName http_ids = builder.addOuterName("http_ids");
			OuterName tcp_ids = builder.addOuterName("tcp_ids");
			Node http_S = builder.addNode("stackNode", server, http_ids,
					tcp_ids);
			http_S.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Http_Server")));
			OuterName ip_ids = builder.addOuterName("158.130.0.1");
			Node tcp_S = builder.addNode("stackNode", server, tcp_ids, ip_ids);
			tcp_S.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Tcp_Server")));
			OuterName localS = builder.addOuterName("localS");
			Node ip_S = builder.addNode("stackNode", server, ip_ids, localS);
			ip_S.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ProtocolName", "Ip_server")));
			// Server's Router
			OuterName listFWSIN = builder
					.addOuterName("IN_SERVER%192.168.0.1%192.168.0.2%192.168.0.3");
			OuterName listFWSOUT = builder
					.addOuterName("OUT_SERVER%158.130.0.1%158.130.0.2%158.130.0.3");
			Node firewallS = builder.addNode("firewall", domainS, listFWSIN,
					listFWSOUT);
			firewallS.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("FirewallName",
							"Server's Firewall")));
			Node routerS = builder.addNode("stackNode", firewallS, linkR2,
					localS);
			routerS.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("RouterName", "Server_Router")));

			// Packet
			Node http_packet = builder.addNode("packet", client, http_idc,
					http_ids);
			http_packet.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("PacketName", "Http_Packet")));
			Node http_payload = builder.addNode("payload", http_packet);
			http_payload.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("PacketName", "Http_Payload")));

			return builder.makeBigraph();
		
	}

	
	/**
	 * Generate an array with instances of the rewriting rules for network
	 * bigraphs, plus firewall rules.
	 *
	 * @return An array of RewritingRule instances for net bigraphs.
	 */
	public static RewritingRule[] getNetFWRules() {
		
			return new RewritingRule[] { new EncapRule(), new DFRule(),
					new Domain2HostRule(), new DecapRule(), new ForwardRule(),
					new DFRuleFW(), new Domain2HostFWRule(), new FWINRule(),
					new FWOUTRule(), new ForwardFWRule(),
					new NewTokenINRuleFar(), new NewTokenINRuleNear(),
					new NewTokenOUTRuleFar(), new NewTokenOUTRuleNear() };
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static Bigraph getBigTest1(Signature signature) {
		
		/*
		 * ----------------------------------------------------- Http packet
		 * exchange with Firewalls.
		 * -----------------------------------------------------
		 */
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r = builder.addRoot();
		// Client
		Node domainC = builder.addNode("domain", r);
		domainC.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Client_Domain")));
		Node client = builder.addNode("host", domainC);
		client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName", "Client")));
		OuterName http_idc = builder.addOuterName("http_idc");
		OuterName tcp_idc = builder.addOuterName("tcp_idc");
		Node http_C = builder.addNode("stackNode", client, http_idc,
				tcp_idc);
		http_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Http_Client")));
		OuterName ip_idc = builder.addOuterName("192.168.0.1");
		Node tcp_C = builder.addNode("stackNode", client, tcp_idc, ip_idc);
		tcp_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Tcp_Client")));
		OuterName localC = builder.addOuterName("localC");
		Node ip_C = builder.addNode("stackNode", client, ip_idc, localC);
		ip_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Ip_Client")));

		// Firewall
		OuterName listFWCIN = builder
				.addOuterName("IN_CLIENT%192.168.0.1%192.168.0.2%192.168.0.3");
		OuterName listFWCOUT = builder
				.addOuterName("OUT_CLIENT%158.130.0.1%158.130.0.2%158.130.0.3");
		Node firewallC = builder.addNode("firewall", domainC, listFWCIN,
				listFWCOUT);
		firewallC.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("FirewallName",
						"Client_Firewall")));
		// Router's Client
		OuterName linkR1 = builder.addOuterName("linkR1");
		Node routerINC = builder.addNode("stackNode", firewallC, linkR1,
				localC);
		routerINC
				.attachProperty(new SharedProperty<String>(
						new SimpleProperty<String>("RouterName",
								"Client_Router_IN")));

		// Second Domain
		Node domain2 = builder.addNode("domain", r);
		domain2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Second_Domain")));
		// Second Router
		OuterName local2 = builder.addOuterName("local2");
		Node router2 = builder
				.addNode("stackNode", domain2, linkR1, local2);
		router2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "router2")));
		// Third Router
		OuterName linkR2 = builder.addOuterName("linkR2");
		Node router3 = builder
				.addNode("stackNode", domain2, linkR2, local2);
		router3.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "Third_Router")));

		// Server's Domain
		Node domainS = builder.addNode("domain", r);
		domainS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Server_Domain")));
		// Server
		Node server = builder.addNode("host", domainS);
		server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName", "Server")));
		OuterName http_ids = builder.addOuterName("http_ids");
		OuterName tcp_ids = builder.addOuterName("tcp_ids");
		Node http_S = builder.addNode("stackNode", server, http_ids,
				tcp_ids);
		http_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Http_Server")));
		OuterName ip_ids = builder.addOuterName("158.130.0.1");
		Node tcp_S = builder.addNode("stackNode", server, tcp_ids, ip_ids);
		tcp_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Tcp_Server")));
		OuterName localS = builder.addOuterName("localS");
		Node ip_S = builder.addNode("stackNode", server, ip_ids, localS);
		ip_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Ip_server")));
		// Server's Router
		OuterName listFWSIN = builder
				.addOuterName("IN_SERVER%192.168.0.1%192.168.0.2%192.168.0.3");
		OuterName listFWSOUT = builder
				.addOuterName("OUT_SERVER%158.130.0.1%158.130.0.2%158.130.0.3");
		Node firewallS = builder.addNode("firewall", domainS, listFWSIN,
				listFWSOUT);
		firewallS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("FirewallName",
						"Server's Firewall")));
		Node routerS = builder.addNode("stackNode", firewallS, linkR2,
				localS);
		routerS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "Server_Router")));

		// Packet
		Node ip_packet = builder.addNode("packet", client, ip_idc, ip_ids);
		ip_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "192.168.0.1_packet")));
		Node tcp_packet = builder.addNode("packet", ip_packet, tcp_idc, tcp_ids);
		tcp_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Tcp_Packet")));
		Node http_packet = builder.addNode("packet", tcp_packet, http_idc,
				http_ids);
		http_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Http_Packet")));
		Node http_payload = builder.addNode("payload", http_packet);
		http_payload.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Http_Payload")));
		
		return builder.makeBigraph();
	
	}
	
	
	

	public static Bigraph getBigTest2(Signature signature) {
		
		/*
		 * ----------------------------------------------------- Http packet
		 * exchange with Firewalls.
		 * -----------------------------------------------------
		 */
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r = builder.addRoot();
		// Client
		Node domainC = builder.addNode("domain", r);
		domainC.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Client_Domain")));
		Node client = builder.addNode("host", domainC);
		client.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName", "Client")));
		OuterName http_idc = builder.addOuterName("http_idc");
		OuterName tcp_idc = builder.addOuterName("tcp_idc");
		Node http_C = builder.addNode("stackNode", client, http_idc,
				tcp_idc);
		http_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Http_Client")));
		OuterName ip_idc = builder.addOuterName("192.168.0.1");
		Node tcp_C = builder.addNode("stackNode", client, tcp_idc, ip_idc);
		tcp_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Tcp_Client")));
		OuterName localC = builder.addOuterName("localC");
		Node ip_C = builder.addNode("stackNode", client, ip_idc, localC);
		ip_C.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Ip_Client")));

		// Firewall
		OuterName listFWCIN = builder
				.addOuterName("IN_CLIENT%192.168.0.1%192.168.0.2%192.168.0.3");
		OuterName listFWCOUT = builder
				.addOuterName("OUT_CLIENT%158.130.0.1%158.130.0.2%158.130.0.3");
		Node firewallC = builder.addNode("firewall", domainC, listFWCIN,
				listFWCOUT);
		firewallC.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("FirewallName",
						"Client_Firewall")));
		// Router's Client
		OuterName linkR1 = builder.addOuterName("linkR1");
		Node routerINC = builder.addNode("stackNode", firewallC, linkR1,
				localC);
		routerINC
				.attachProperty(new SharedProperty<String>(
						new SimpleProperty<String>("RouterName",
								"Client_Router_IN")));

		// Second Domain
		Node domain2 = builder.addNode("domain", r);
		domain2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Second_Domain")));
		// Second Router
		OuterName local2 = builder.addOuterName("local2");
		Node router2 = builder
				.addNode("stackNode", domain2, linkR1, local2);
		router2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "router2")));
		// Third Router
		OuterName linkR2 = builder.addOuterName("linkR2");
		Node router3 = builder
				.addNode("stackNode", domain2, linkR2, local2);
		router3.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "Third_Router")));

		// Server's Domain
		Node domainS = builder.addNode("domain", r);
		domainS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("DomainName", "Server_Domain")));
		// Server
		Node server = builder.addNode("host", domainS);
		server.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("HostName", "Server")));
		OuterName http_ids = builder.addOuterName("http_ids");
		OuterName tcp_ids = builder.addOuterName("tcp_ids");
		Node http_S = builder.addNode("stackNode", server, http_ids,
				tcp_ids);
		http_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Http_Server")));
		OuterName ip_ids = builder.addOuterName("158.130.0.1");
		Node tcp_S = builder.addNode("stackNode", server, tcp_ids, ip_ids);
		tcp_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Tcp_Server")));
		OuterName localS = builder.addOuterName("localS");
		Node ip_S = builder.addNode("stackNode", server, ip_ids, localS);
		ip_S.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("ProtocolName", "Ip_server")));
		// Server's Router
		OuterName listFWSIN = builder
				.addOuterName("IN_SERVER%192.168.0.1%192.168.0.2%192.168.0.3");
		OuterName listFWSOUT = builder
				.addOuterName("OUT_SERVER%158.130.0.1%158.130.0.2%158.130.0.3");
		Node firewallS = builder.addNode("firewall", domainS, listFWSIN,
				listFWSOUT);
		firewallS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("FirewallName",
						"Server's Firewall")));
		Node routerS = builder.addNode("stackNode", firewallS, linkR2,
				localS);
		routerS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("RouterName", "Server_Router")));

		// Packet
		Node ip_packet = builder.addNode("packet", firewallC, ip_idc, ip_ids);
		ip_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "192.168.0.1_packet")));
		Node tcp_packet = builder.addNode("packet", ip_packet, tcp_idc, tcp_ids);
		tcp_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Tcp_Packet")));
		Node http_packet = builder.addNode("packet", tcp_packet, http_idc,
				http_ids);
		http_packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Http_Packet")));
		Node http_payload = builder.addNode("payload", http_packet);
		http_payload.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketName", "Http_Payload")));
		
		return builder.makeBigraph();
	
	}
	
}
