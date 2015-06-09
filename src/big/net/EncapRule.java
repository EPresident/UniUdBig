package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;

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
public final class EncapRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(1,0);
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("PacketType");
    }

    public EncapRule() {
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
            if (p.get().equals("packetOut")) {
                createRightProperty(original, instance, match);
            }
        }

    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        OuterName id1 = builder.addOuterName("id1");
        OuterName down1 = builder.addOuterName("down1");
        Node sn1 = builder.addNode("stackNode", r1, id1, down1);
        sn1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "EncapSender")));
        OuterName id2 = builder.addOuterName("id2");
        OuterName down2 = builder.addOuterName("down2");
        Node packet = builder.addNode("packet", r1, id1, id2);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packetIn")));
        builder.addSite(packet);

        Root r2 = builder.addRoot();
        Node sn2 = builder.addNode("stackNode", r2, id2, down2);
        sn2.attachProperty(new SharedProperty<String>(
                new SimpleProperty("NodeType", "EncapReceiver")));

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        OuterName id1 = builder.addOuterName("id1");
        OuterName down1 = builder.addOuterName("down1");
        Node sn1 = builder.addNode("stackNode", r1, id1, down1);
        sn1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "EncapSender")));
        OuterName id2 = builder.addOuterName("id2");
        OuterName down2 = builder.addOuterName("down2");
        Node packetOut = builder.addNode("packet", r1, down1, down2);
        packetOut.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packetOut")));

        Node packetIn = builder.addNode("packet", packetOut, id1, id2);
        packetIn.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packetIn")));

        builder.addSite(packetIn);

        Root r2 = builder.addRoot();
        Node sn2 = builder.addNode("stackNode", r2, id2, down2);
        sn2.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "EncapReceiver")));

        return builder.makeBigraph();
    }

    private void createRightProperty(Node original, Node instance, Match match) {
        //First step : find the neighbor node of the new node ("EncapSender" is a neighbor of "packetOut").
        //Second step : find the image neighbor in the real bigraph.
        //Third step : Take the name of the second OuterName (on the port 1).
        Handle handle = original.getPort(0).getHandle();
        Iterator<? extends Point> it = handle.getPoints().iterator();
        while (it.hasNext()) {
            Point next = it.next();
            if (!next.equals(original.getPort(0))) {
                if (next.isPort()) {
                    Port np = (Port) next;
                    Node neighbor = np.getNode();// a node of the reactum
                    Property propN = neighbor.getProperty("NodeType");// a node of the reactum
                    if (propN != null) {
                        Node neighborRedex = rr.get(propN.get())[1];// a node of the redex
                        if (neighborRedex != null) {
                            Node imgNeigh = match.getImage(neighborRedex);// a node of the real bigraph
                            if (imgNeigh != null) {
                                Port down = imgNeigh.getPort(1);
                                if (down != null) {
                                    Handle downH = (Handle) down.getHandle();
                                    if (downH != null && downH.isOuterName()) {
                                        OuterName downId = (OuterName) downH;
                                        String valueP = downH.toString().split("_")[0] + "_packet";
                                        instance.attachProperty(new SharedProperty<String>(
                                                new SimpleProperty<String>("PacketName", valueP)));

                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}
