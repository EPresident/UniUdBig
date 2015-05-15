package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

/**
 * Class for the encapsulation reaction. This class doesn't have to implement
 * the "createRightProperty()" method, because the reactum doesn't introduce new
 * nodes.
 *
 * An important recommendation: in the redex and in the reactum, ALL the nodes
 * should have a property. This allows you to keep all the properties of the
 * original bigraph even after the rule is performed.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class DFRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(5, 0, 1, 2, 3, 4);
    }

    public DFRule() {
        super(redex, reactum, map);
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
