package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;
/**
 * Class for forwarding packets. The packet in the first domain goes in the second one, if and only if 
 * there are two router directly linked. This is a basic rule that allows you to add "n" domains and to 
 * forward packets from one to the other.
 * 
 * This class doesn't have to implement the "createRightProperty()" method, because the reactum doesn't
 * introduce new nodes.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class ForwardRule extends RewritingRule{

	private Bigraph bigraph;
	private Bigraph redex;
	private Bigraph reactum;
	private Map<String, Node[]> rr;//Link from reactum node to redex nodes.
	private static LinkedList<String> auxProperties;	
	
	public ForwardRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
		super(redex, reactum, map);
		this.redex = redex;
		this.reactum = reactum;
		rr = new HashMap<String, Node[]>();
		createAssociations();
		this.auxProperties = new LinkedList<String>();
		auxProperties.add("NodeType");
		auxProperties.add("HostType");
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
		//First Domain
		Root r1 = builder.addRoot();
		Node domain1 = builder.addNode("domain", r1);
		domain1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","domain1")));
		builder.addSite(domain1);//Site 0
		//Router1
		OuterName linkR = builder.addOuterName("linkR");
		OuterName localS = builder.addOuterName("localS");
		Node router1 = builder.addNode("stackNode", domain1, linkR, localS);
		router1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Router1")));
		
		//Second Domain
		Root r2 = builder.addRoot();
		//Router2
		OuterName localR = builder.addOuterName("localR");
		Node router2 = builder.addNode("stackNode", r2, linkR, localR);
		router2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Router2")));
		//Packet
		OuterName idS = builder.addOuterName("idS");
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", domain1, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 1
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		//First Domain
		Root r1 = builder.addRoot();
		Node domain1 = builder.addNode("domain", r1);
		domain1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","domain1")));
		builder.addSite(domain1);//Site 0
		//Router1
		OuterName linkR = builder.addOuterName("linkR");
		OuterName localS = builder.addOuterName("localS");
		Node router1 = builder.addNode("stackNode", domain1, linkR, localS);
		router1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Router1")));
		
		//Second Domain
		Root r2 = builder.addRoot();
		//Router2
		OuterName localR = builder.addOuterName("localR");
		Node router2 = builder.addNode("stackNode", r2, linkR, localR);
		router2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Router2")));
		//Packet
		OuterName idS = builder.addOuterName("idS");
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", r2, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);//Site 1
		
		return builder.makeBigraph();
	}
	

	public static InstantiationMap getInstMap(){
		return new InstantiationMap(2, 0, 1);
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
