package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

/**
 * Class for the Direct Forward of a packet. The packet goes from a Host to the receiver's Domain. 
 * This rule matches only if the two routers of the two domains (the sender's and the receiver's ones) are
 * directly linked.
 * 
 * This class doesn't have to implement the "createRightProperty()" method, because the reactum doesn't
 * introduce new nodes.
 * 
 * An important recommendation: in the redex and in the reactum, ALL the nodes should have a property. This
 * allows you to keep all the properties of the original bigraph even after the rule is performed.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class DFRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;

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
		
		//Sender's Domain
		Root r1 = builder.addRoot();
		//Host
		Node sender = builder.addNode("host", r1);
		sender.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Sender")));
		builder.addSite(sender); //Site 0
		//Stack Node
		OuterName idS = builder.addOuterName("idS");
		OuterName localS = builder.addOuterName("localS");
		Node sn1 = builder.addNode("stackNode", sender, idS, localS);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","sn1")));
		//Packet
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", sender, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet); //Site 1
		//Router Sender
		OuterName linkR = builder.addOuterName("linkR");
		Node routerS = builder.addNode("stackNode", r1, linkR, localS);
		routerS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","routerS")));
		
		//Receiver's Domain
		Root r2 = builder.addRoot();
		//Router Receiver
		OuterName localR = builder.addOuterName("localR");
		Node routerR = builder.addNode("stackNode", r2, linkR, localR);
		routerR.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","routerR")));
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		
		//Sender's Domain
		Root r1 = builder.addRoot();
		//Host
		Node sender = builder.addNode("host", r1);
		sender.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","Sender")));
		builder.addSite(sender); //Site 0
		//Stack Node
		OuterName idS = builder.addOuterName("idS");
		OuterName localS = builder.addOuterName("localS");
		Node sn1 = builder.addNode("stackNode", sender, idS, localS);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","sn1")));
		
		//Router Sender
		OuterName linkR = builder.addOuterName("linkR");
		Node routerS = builder.addNode("stackNode", r1, linkR, localS);
		routerS.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","routerS")));
		
		//Receiver's Domain
		Root r2 = builder.addRoot();
		//Packet
		OuterName idR = builder.addOuterName("idR");
		Node packet = builder.addNode("packet", r2, idS, idR);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("PacketType","packet")));
		builder.addSite(packet); //Site 1
		//Router Receiver
		OuterName localR = builder.addOuterName("localR");
		Node routerR = builder.addNode("stackNode", r2, linkR, localR);
		routerR.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("NodeType","routerR")));
		
		return builder.makeBigraph();
	}
	
	
	public static InstantiationMap getInstMap(){
		return new InstantiationMap(2, 0 , 1);
	}
	
	

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        builder.addSite(r1);//Site 0
        //Domain
        Node domain = builder.addNode("domain", r1);
        builder.addSite(domain);//Site 1
        domain.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "domSender")));
        //Sender
        Node sender = builder.addNode("host", domain);
        builder.addSite(sender);//Site 2
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "senderHost")));
        OuterName localLink = builder.addOuterName("localLink");
        OuterName idSender = builder.addOuterName("idSender");
        //Stack
        Node stackSender = builder.addNode("stackNode", sender, idSender, localLink);
        stackSender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "stackSender")));
        OuterName linkR = builder.addOuterName("linkR");

        //Router
        Node routerS = builder.addNode("stackNode", domain, linkR, localLink);
        routerS.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "routerS")));

        Root r2 = builder.addRoot();
        builder.addSite(r2);//Site 3
        OuterName localLinkOUT = builder.addOuterName("localLinkOUT");
        Node routerD = builder.addNode("stackNode", r2, linkR, localLinkOUT);
        routerD.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "RouterD")));

        //Packet
        OuterName destPackOut = builder.addOuterName("destPackOUT");
        Node packet = builder.addNode("packet", sender, idSender, destPackOut);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 4
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "IPPAcket")));

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        builder.addSite(r1);//Site 0
        //Domain
        Node domain = builder.addNode("domain", r1);
        builder.addSite(domain);//Site 1
        domain.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "domSender")));
        //Sender
        Node sender = builder.addNode("host", domain);
        builder.addSite(sender);//Site 2
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "senderHost")));
        OuterName localLink = builder.addOuterName("localLink");
        OuterName idSender = builder.addOuterName("idSender");
        //Stack
        Node stackSender = builder.addNode("stackNode", sender, idSender, localLink);
        stackSender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "stackSender")));
        OuterName linkR = builder.addOuterName("linkR");

        //Router
        Node routerS = builder.addNode("stackNode", domain, linkR, localLink);
        routerS.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "routerS")));

        Root r2 = builder.addRoot();
        builder.addSite(r2);//Site 3
        OuterName localLinkOUT = builder.addOuterName("localLinkOUT");
        Node routerD = builder.addNode("stackNode", r2, linkR, localLinkOUT);
        routerD.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "RouterD")));

        //Packet
        OuterName destPackOut = builder.addOuterName("destPackOUT");
        Node packet = builder.addNode("packet", r2, idSender, destPackOut);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 4
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "IPPAcket")));

        return builder.makeBigraph();
    }

}
