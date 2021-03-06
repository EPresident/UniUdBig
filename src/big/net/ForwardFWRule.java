package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class for forwarding packets if the first router has a firewall. The packet
 * must have a TokenOUT, that can be generated by the "NewTokenRuleFWOUT" class.
 *
 * This class doesn't have to implement the "createRightProperty()" method,
 * because the reactum doesn't introduce new nodes.
 *
 * @see NewTokenRuleFWOUT
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class ForwardFWRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(1, 0);
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("HostType");
    }

    public ForwardFWRule() {
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
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r1, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        OuterName linkR = builder.addOuterName("linkR");
        OuterName localS = builder.addOuterName("localS");
        Node router1 = builder.addNode("stackNode", firewall, linkR, localS);
        router1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router1")));
        //Second Domain
        Root r2 = builder.addRoot();
        OuterName localR = builder.addOuterName("localR");
        Node router2 = builder.addNode("stackNode", r2, linkR, localR);
        router2.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router2")));

        //Packet
        OuterName idS = builder.addOuterName("idS");
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", firewall, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 0
        //Token
        Node tokenOUT = builder.addNode("tokenOUT", packet, listFWOUT);
        tokenOUT.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "tokenOUT")));

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        //First Domain
        Root r1 = builder.addRoot();
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r1, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        OuterName linkR = builder.addOuterName("linkR");
        OuterName localS = builder.addOuterName("localS");
        Node router1 = builder.addNode("stackNode", firewall, linkR, localS);
        router1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router1")));
        //Second Domain
        Root r2 = builder.addRoot();
        OuterName localR = builder.addOuterName("localR");
        Node router2 = builder.addNode("stackNode", r2, linkR, localR);
        router2.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router2")));

        //Packet
        OuterName idS = builder.addOuterName("idS");
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", r2, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 0

        return builder.makeBigraph();
    }

}
