package big.net;

import it.uniud.mads.jlibbig.core.std.*;


public class ForwardRule {

	
	public static RewritingRule getRule(Signature signature){
		Bigraph redex = getRedex(signature);
		Bigraph reactum = getReactum(signature);
		InstantiationMap map = new InstantiationMap(1,0);
		return new RewritingRule(redex, reactum, map);
	}
	
	
	public static Bigraph getRedex(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		Node packet = builder.addNode("packet",r1,id1,id2);
		builder.addSite(packet);
		
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		
		//Points are link entities connected by the hyper-edges composing the link graphs. 
		//Points are inner names or ports depending on whereas they belong to an inner interface or to a node.
		builder.relink( sn1.getPort(0) , sn2.getPort(1) );
		builder.relink( sn1.getPort(1) , sn2.getPort(0) );
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		Node packet = builder.addNode("packet",r2,id1,id2);
		builder.addSite(packet);
		
		builder.relink( down2 , sn1.getPort(0) );
		builder.relink( down1,  sn2.getPort(0) );
		
		return builder.makeBigraph();
	}
	
}
