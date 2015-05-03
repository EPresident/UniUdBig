package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;
/**
 * Class for the encapsulation reaction. Doesn't matter what protocols are involved.
 * Pay attention: in the real bigraph, the Protocol Nodes must have two ports. The first has to be the "id"
 * of the protocol. For example, if the Protocol Node is the http layer than the first port ( 0@... ) has to
 * link "http_layer" with the OuterName "http_id".
 * Furthermore, the second port is the id of the underlying layer. In the above example, the second port
 * ( 1@... ) must link "http_layer" with the OuterName "tcp_id".
 * 
 * Another warning: in each rule, you have to customize the content of the "auxProperties" list. This list 
 * contains the new properties of the nodes in the redex and the reactum of THIS rule. They are auxiliary 
 * properties, necessary for preserving all the properties after the rule.
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Domain2HostRule extends RewritingRule{
	
	private Bigraph bigraph;
	private Bigraph redex;
	private Bigraph reactum;
	private Map<String, Node[]> rr;//Link from reactum node to redex nodes.
	private static LinkedList<String> auxProperties;
	
	public Domain2HostRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
		super(redex, reactum, map);
		this.redex = redex;
		this.reactum = reactum;
		rr = new HashMap<String, Node[]>();
		createAssociations();
		this.auxProperties = new LinkedList<String>();
		auxProperties.add("NodeType");
		auxProperties.add("PacketType");
	}
	
	@Override
	public Iterable<Bigraph> apply(Bigraph b){
		this.bigraph = b;
		Iterable<Bigraph> bgl = super.apply(b);
		
		return bgl;
	}
	
	
	@Override
	public void instantiateReactumNode(Node original, Node instance, Match match){
		for(Property p : original.getProperties()){//Original = node of the reactum
			Node[] array = rr.get(p.get());
			if(array != null){
				Node n = array[1]; //Node of the redex
				if(n != null){
					Node img = match.getImage(n);//Node of the original bigraph
					if(img != null){
						copyProperties(img,instance);
					}
				}
			}
		}
		
	} 

	
	public static Bigraph getRedex(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		builder.addSite(r1);//Site 0
		//Host
		Node dest = builder.addNode("host",r1);
		dest.attachProperty(new SharedProperty<String>(
							new SimpleProperty<String>("NodeType","receiver")));
		builder.addSite(dest);//Site 1
		//Protocol
		OuterName idD = builder.addOuterName("idD");
		OuterName downD = builder.addOuterName("downD");
		Node nodeD = builder.addNode("stackNode", dest, idD, downD);
		nodeD.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","receiverProtocol")));
		//Packet
		OuterName idS = builder.addOuterName("ids");
		Node packet = builder.addNode("packet", r1, idS, idD);
		packet.attachProperty(new SharedProperty<String>(
								new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 2
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		builder.addSite(r1);//Site 0
		//Host
		Node dest = builder.addNode("host",r1);
		dest.attachProperty(new SharedProperty<String>(
							new SimpleProperty<String>("NodeType","receiver")));
		builder.addSite(dest);//Site 1
		//Protocol
		OuterName idD = builder.addOuterName("idD");
		OuterName downD = builder.addOuterName("downD");
		Node nodeD = builder.addNode("stackNode", dest, idD, downD);
		nodeD.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","receiverProtocol")));
		//Packet
		OuterName idS = builder.addOuterName("ids");
		Node packet = builder.addNode("packet", dest, idS, idD);
		packet.attachProperty(new SharedProperty<String>(
								new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 2
		
		return builder.makeBigraph();
	}
	
	
	
	private void copyProperties(Node from, Node to){
		for(Property p : from.getProperties()){
			if( !p.getName().equals("Owner") ){
				if( !p.getName().equals("NodeType") ){
					to.attachProperty(p);
				}
			}
		}
	}
	
	
	private void createAssociations(){
		
		for(Node n1 : this.reactum.getNodes()){
			for(Property p1 : n1.getProperties()){
				if( !p1.getName().equals("Owner") ){
					Node[] array = new Node[2];
					array[0] = n1;
					rr.put(p1.get().toString(), array);
				}
			}
		}
		
		for(Node n2 : this.redex.getNodes()){
			for(Property p2 : n2.getProperties()){
				if( !p2.getName().equals("Owner") ){
					Node[] array = rr.get(p2.get());
					if(array != null){
						array[1] = n2;
						rr.put(p2.get().toString(), array);
					}
				}
			}
		}
		
	}
	
	
	
	public static void clearAuxProperties(Bigraph bg){
		//Deletes auxiliary properties, such as NodeType and PacketType.
		boolean pass = false;
		for(Node n: bg.getNodes()){
				CopyOnWriteArrayList<Property> cow = new CopyOnWriteArrayList<Property>(n.getProperties());
				Property[] a = new Property[0];
				Property[] ap = cow.toArray( a );
				for(int i=0;i<ap.length;i++){
					String name = ap[i].getName();
					if( !name.equals("Owner") ){
						for(String str : auxProperties){
							if(name.equals(str)){
								pass = true;
							}
						}
						if(pass){
							n.detachProperty(ap[i]);
						}
						pass = false;
					}
				}
			}
	}
	
	
	
	
	
}

