package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for the entry of a packet in a host from the host's domain. The host
 * must be the receiver of the packet, and the domain is the receiver's one.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Domain2HostRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(2, 0, 1);
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("PacketType");
    }

    public Domain2HostRule() {
        super(redex, reactum, map);
    }

    protected static List<String> getAuxProperties() {
        return auxProperties;
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        //Host
        Node dest = builder.addNode("host", r1);
        dest.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiver")));
        builder.addSite(dest);//Site 0
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
        builder.addSite(packet);//Site 1

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        //Host
        Node dest = builder.addNode("host", r1);
        dest.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "receiver")));
        builder.addSite(dest);//Site 0
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
        builder.addSite(packet);//Site 1

        return builder.makeBigraph();
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
