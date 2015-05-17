package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class adds a TokenOUT to a packet if and only if the packet has the permissions. This rule checks
 * the receiver's field of the packet and the firewall's list.
 * 
 * This rule is applicable only if the packet's receivers is in the firewall. 
 * 
 * @see FWOUTRule
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class NewTokenOUTRuleNear extends RewritingRule {

	private final String DELIMITER = "%";
	private Bigraph bigraph;
	private Bigraph redex;
	private Bigraph reactum;
	private Map<String, Node[]> rr;// Link from reactum node to redex nodes.
	private static LinkedList<String> auxProperties;
	private Matcher matcher;

	public NewTokenOUTRuleNear(Bigraph redex, Bigraph reactum,
			InstantiationMap map) {
		super(redex, reactum, map);
		this.redex = redex;
		this.reactum = reactum;
		rr = new HashMap<String, Node[]>();
		createAssociations();
		this.auxProperties = new LinkedList<String>();
		auxProperties.add("NodeType");
		auxProperties.add("PacketType");
		this.matcher = new Matcher();
	}

	@Override
	public Iterable<Bigraph> apply(Bigraph b) {
		this.bigraph = b;
		Iterable<Bigraph> bgl = super.apply(b);

		return bgl;
	}

	@Override
	public void instantiateReactumNode(Node original, Node instance, Match match) {
		for (Property p : original.getProperties()) {// Original = node of the
														// reactum
			Node[] array = rr.get(p.get());
			if (array != null) {
				Node n = array[1]; // Node of the redex
				if (n != null) {
					Node img = match.getImage(n);// Node of the original bigraph
					if (img != null) {
						copyProperties(img, instance);
					}
				}
			}
		}

	}

	public static Bigraph getRedex(Signature signature) {
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r = builder.addRoot();
		//Firewall
		OuterName listFWIN = builder.addOuterName("listFWIN");
		OuterName listFWOUT = builder.addOuterName("listFWOUT");
		Node firewall = builder.addNode("firewall", r, listFWIN, listFWOUT);
		firewall.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","firewall")));
		builder.addSite(firewall);//Site 0
		//Packet
		OuterName idS = builder.addOuterName("idS");
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", firewall, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 1
		//Receiver
		OuterName downR = builder.addOuterName("downR");
		Node receiver = builder.addNode("stackNode", firewall, idR, downR);
		receiver.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","receiver")));
		
		return builder.makeBigraph();
	}

	public static Bigraph getReactum(Signature signature) {
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r = builder.addRoot();
		//Firewall
		OuterName listFWIN = builder.addOuterName("listFWIN");
		OuterName listFWOUT = builder.addOuterName("listFWOUT");
		Node firewall = builder.addNode("firewall", r, listFWIN, listFWOUT);
		firewall.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","firewall")));
		builder.addSite(firewall);//Site 0
		//Packet
		OuterName idS = builder.addOuterName("idS");
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", firewall, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 1
		//Receiver
		OuterName downR = builder.addOuterName("downR");
		Node receiver = builder.addNode("stackNode", firewall, idR, downR);
		receiver.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","receiver")));
		//TokenOUT
		Node tokenOUT = builder.addNode("tokenOUT", packet, listFWOUT);
		tokenOUT.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","tokenOUT")));
		
		return builder.makeBigraph();
	}
	
	

	public static InstantiationMap getInstMap() {
		return new InstantiationMap(2, 0, 1);
	}

	private void copyProperties(Node from, Node to) {
		for (Property p : from.getProperties()) {
			if (!p.getName().equals("Owner")) {
				if (!p.getName().equals("NodeType")) {
					to.attachProperty(p);
				}
			}
		}
	}

	private void createAssociations() {

		for (Node n1 : this.reactum.getNodes()) {
			for (Property p1 : n1.getProperties()) {
				if (!p1.getName().equals("Owner")) {
					Node[] array = new Node[2];
					array[0] = n1;
					rr.put(p1.get().toString(), array);
				}
			}
		}

		for (Node n2 : this.redex.getNodes()) {
			for (Property p2 : n2.getProperties()) {
				if (!p2.getName().equals("Owner")) {
					Node[] array = rr.get(p2.get());
					if (array != null) {
						array[1] = n2;
						rr.put(p2.get().toString(), array);
					}
				}
			}
		}

	}

	public static void clearAuxProperties(Bigraph bg) {
		// Deletes auxiliary properties, such as NodeType and PacketType.
		boolean pass = false;
		for (Node n : bg.getNodes()) {
			CopyOnWriteArrayList<Property> cow = new CopyOnWriteArrayList<Property>(
					n.getProperties());
			Property[] a = new Property[0];
			Property[] ap = cow.toArray(a);
			for (int i = 0; i < ap.length; i++) {
				String name = ap[i].getName();
				if (!name.equals("Owner")) {
					for (String str : auxProperties) {
						if (name.equals(str)) {
							pass = true;
						}
					}
					if (pass) {
						n.detachProperty(ap[i]);
					}
					pass = false;
				}
			}
		}
	}

	private void createRightProperty(Node original, Node instance, Match match) {
		// First step : find the neighbor node of the new node ("EncapSender" is
		// a neighbor of "packetOut").
		// Second step : find the image neighbor in the real bigraph.
		// Third step : Take the name of the second OuterName (on the port 1).

	}

	public boolean isApplicable(Bigraph bigraph, Signature signature) {
		Iterator<? extends Match> iter = this.matcher
				.match(bigraph, this.redex).iterator();
		if (iter.hasNext()) {
			Match match = iter.next();
			String listFW = getOuterNameImage("firewall", match, 1);
			LinkedList<String> permissions = prepareList(listFW);
			String idR = getOuterNameImage("sender", match, 0);
			if (checkPermission(idR, permissions)) {
				return true;
			}
		}
		return false;
	}

	private String getOuterNameImage(String name, Match match, int nPort) {
		String str = "";
		Node redexN = rr.get(name)[1];
		if (redexN != null) {
			Node imgNode = match.getImage(redexN);
			if (imgNode != null) {
				Port port = imgNode.getPort(nPort);
				if (port != null) {
					Handle handle = port.getHandle();
					if (handle != null && handle.isOuterName()) {
						OuterName outer = (OuterName) handle;
						str += outer.getName();
					}
				}
			}
		}
		return str;
	}

	private LinkedList<String> prepareList(String str) {
		LinkedList<String> list = new LinkedList<String>();
		String[] array = str.split(DELIMITER);
		for (int i = 0; i < array.length; i++) {
			list.add(array[i]);
		}
		return list;
	}

	private boolean checkPermission(String x, LinkedList<String> list) {
		for (String s : list) {
			if (s.equals(x)) {
				return true;
			}
		}
		return false;
	}

}
