package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

/**
 * Class for the encapsulation reaction.
 * This class doesn't have to implement the "createRightProperty()" method, because the reactum doesn't
 * introduce new nodes.
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class ForwardRule extends RewRuleWProps{
	
	public ForwardRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
		super(redex, reactum, map);
	}	
	
        @Override
	public Bigraph getRedex(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName id2 = builder.addOuterName("id2");
		
		Node sn1 = builder.addNode("stackNode", r1, id1, id2);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","EncapSender")));
		Node packet = builder.addNode("packet",r1,id1,id2);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);
		
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,id1);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapReceiver")));
		
		return builder.makeBigraph();
	}
	
	
	

        @Override
	public Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName id2 = builder.addOuterName("id2");
		
		Node sn1 = builder.addNode("stackNode", r1, id1, id2);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","EncapSender")));
		
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,id1);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapReceiver")));
		Node packet = builder.addNode("packet",r2,id1,id2);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet);
		
		return builder.makeBigraph();
	}

}
