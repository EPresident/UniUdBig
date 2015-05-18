package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

/**
 * Class for the entry of a packet in a host from the host's domain. The host must be the receiver of the 
 * packet, and the domain is the receiver's one. 
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Domain2HostRule extends RewRuleWProps {

	
	public static Bigraph getRedex(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		//Host
		Node dest = builder.addNode("host",r1);
		dest.attachProperty(new SharedProperty<String>(
							new SimpleProperty<String>("NodeType","receiver")));
		builder.addSite(dest);//Site 0
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
		builder.addSite(packet);//Site 1
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		//Host
		Node dest = builder.addNode("host",r1);
		dest.attachProperty(new SharedProperty<String>(
							new SimpleProperty<String>("NodeType","receiver")));
		builder.addSite(dest);//Site 0
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
