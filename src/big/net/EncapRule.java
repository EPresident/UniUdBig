package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;
/**
 * Class for the encapsulation reaction. Doesn't matter what protocols are involved.
 * @author Luca Geatti
 *
 */
public class EncapRule extends RewritingRule{
	
	private Bigraph bigraph;
	private Bigraph redex;
	private Bigraph reactum;
	private Map<String, Node[]> rr;//Link from reactum node to redex nodes.
	private static LinkedList<String> auxProperties;
	
	public EncapRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
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
		for(Property p : original.getProperties()){
			Node[] array = rr.get(p.get());
			if(array != null){
				Node n = array[1];
				if(n != null){
					Node img = match.getImage(n);
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
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","EncapSender")));
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		Node packet = builder.addNode("packet",r1,id1,id2);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packetIn")));
		builder.addSite(packet);
		
		builder.addSite(r1);
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapReceiver")));
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","EncapSender")));
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		Node packetOut = builder.addNode("packet", r1, down1, down2);
		packetOut.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packetOut")));
		
		Node packetIn = builder.addNode("packet",packetOut,id1,id2);
		packetIn.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packetIn")));
		
		builder.addSite(packetIn);
		
		builder.addSite(r1);
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","EncapReceiver")));
		
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
							System.out.println(ap[i].getName());
							n.detachProperty(ap[i]);
						}
						pass = false;
					}
				}
			}
	}
	
	
}

