package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

/**
 * Class for the encapsulation reaction. Doesn't matter what protocols are
 * involved. Pay attention: in the real bigraph, the Protocol Nodes must have
 * two ports. The first has to be the "id" of the protocol. For example, if the
 * Protocol Node is the http layer than the first port ( 0@... ) has to link
 * "http_layer" with the OuterName "http_id". Furthermore, the second port is
 * the id of the underlying layer. In the above example, the second port ( 1@...
 * ) must link "http_layer" with the OuterName "tcp_id".
 *
 * Another warning: in each rule, you have to customize the content of the
 * "auxProperties" list. This list contains the new properties of the nodes in
 * the redex and the reactum of THIS rule. They are auxiliary properties,
 * necessary for preserving all the properties after the rule.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Domain2HostRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(3, 0, 1, 2);
    }

    public Domain2HostRule() {
        super(redex, reactum, map);
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        builder.addSite(r1);//Site 0
        //Host
        Node dest = builder.addNode("host", r1);
        dest.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiver")));
        builder.addSite(dest);//Site 1
        //Protocol
        OuterName idD = builder.addOuterName("idD");
        OuterName downD = builder.addOuterName("downD");
        Node nodeD = builder.addNode("stackNode", dest, idD, downD);
        nodeD.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiverProtocol")));
        //Packet
        OuterName idS = builder.addOuterName("ids");
        Node packet = builder.addNode("packet", r1, idS, idD);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 2

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        builder.addSite(r1);//Site 0
        //Host
        Node dest = builder.addNode("host", r1);
        dest.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiver")));
        builder.addSite(dest);//Site 1
        //Protocol
        OuterName idD = builder.addOuterName("idD");
        OuterName downD = builder.addOuterName("downD");
        Node nodeD = builder.addNode("stackNode", dest, idD, downD);
        nodeD.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiverProtocol")));
        //Packet
        OuterName idS = builder.addOuterName("ids");
        Node packet = builder.addNode("packet", dest, idS, idD);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 2

        return builder.makeBigraph();
    }

}
