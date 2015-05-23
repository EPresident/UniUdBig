package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for forwarding packets. The packet in the first domain goes in the
 * second one, if and only if there are two router directly linked. This is a
 * basic rule that allows you to add "n" domains and to forward packets from one
 * to the other.
 *
 * This class doesn't have to implement the "createRightProperty()" method,
 * because the reactum doesn't introduce new nodes.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class ForwardRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(2, 0, 1);
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("HostType");
    }

    public ForwardRule() {
        super(redex, reactum, map);
    }

    protected List<String> getAuxProperties() {
        return auxProperties;
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        for (Property p : original.getProperties()) {//Original = node of the reactum
            Node[] array = rr.get(p.get().toString());
            if (array != null) {
                Node n = array[1]; //Node of the redex
                if (n != null) {
                    Node img = match.getImage(n);//Node of the original bigraph
                    if (img != null) {
                        copyProperties(img, instance);
                    }
                }
            }
        }

    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        //First Domain
        Root r1 = builder.addRoot();
        Node domain1 = builder.addNode("domain", r1);
        domain1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "domain1")));
        builder.addSite(domain1);//Site 0
        //Router1
        OuterName linkR = builder.addOuterName("linkR");
        OuterName localS = builder.addOuterName("localS");
        Node router1 = builder.addNode("stackNode", domain1, linkR, localS);
        router1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Router1")));

        //Second Domain
        Root r2 = builder.addRoot();
        //Router2
        OuterName localR = builder.addOuterName("localR");
        Node router2 = builder.addNode("stackNode", r2, linkR, localR);
        router2.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Router2")));
        //Packet
        OuterName idS = builder.addOuterName("idS");
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", domain1, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 1

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        //First Domain
        Root r1 = builder.addRoot();
        Node domain1 = builder.addNode("domain", r1);
        domain1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "domain1")));
        builder.addSite(domain1);//Site 0
        //Router1
        OuterName linkR = builder.addOuterName("linkR");
        OuterName localS = builder.addOuterName("localS");
        Node router1 = builder.addNode("stackNode", domain1, linkR, localS);
        router1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Router1")));

        //Second Domain
        Root r2 = builder.addRoot();
        //Router2
        OuterName localR = builder.addOuterName("localR");
        Node router2 = builder.addNode("stackNode", r2, linkR, localR);
        router2.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Router2")));
        //Packet
        OuterName idS = builder.addOuterName("idS");
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", r2, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 1

        return builder.makeBigraph();
    }

}
