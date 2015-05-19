package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class similar to "DFRule". It works with firewalls. If the router of the
 * sender's domain has a firewall, the packet must have a TokenIN to enter in
 * the router's firewall. The TokenIN can be generated by the "NewTokenRule"
 * class.
 *
 * An important recommendation: in the redex and in the reactum, ALL the nodes
 * should have a property. This allows you to keep all the properties of the
 * original bigraph even after the rule is performed.
 *
 * @see DFRuleFW
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class DFRuleFW extends RewRuleWProps {

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

    public DFRuleFW() {
        super(redex, reactum, map);
    }

    protected static List<String> getAuxProperties() {
        return auxProperties;
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        for (Property p : original.getProperties()) {//Original = node of the reactum
            Node[] array = rr.get(p.get());
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
        Root r1 = builder.addRoot();

        //Sender
        Node sender = builder.addNode("host", r1);
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Sender")));
        builder.addSite(sender); //Site 0
        OuterName idS = builder.addOuterName("idS");
        OuterName localS = builder.addOuterName("localS");
        Node stackNode = builder.addNode("stackNode", sender, idS, localS);
        stackNode.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "stack_node")));
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r1, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Firewall")));
        //Router
        OuterName linkR = builder.addOuterName("linkR");
        Node router = builder.addNode("stackNode", firewall, linkR, localS);
        router.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router")));
        //Packet
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", sender, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet); //Site 1
        //Token
        Node tokenIN = builder.addNode("tokenIN", packet, listFWIN, listFWOUT);
        tokenIN.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "token")));

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        //Sender
        Node sender = builder.addNode("host", r1);
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Sender")));
        builder.addSite(sender); //Site 0
        OuterName idS = builder.addOuterName("idS");
        OuterName localS = builder.addOuterName("localS");
        Node stackNode = builder.addNode("stackNode", sender, idS, localS);
        stackNode.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "stack_node")));
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r1, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Firewall")));
        //Router
        OuterName linkR = builder.addOuterName("linkR");
        Node router = builder.addNode("stackNode", firewall, linkR, localS);
        router.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "router")));
        //Packet
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", firewall, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet); //Site 1

        return builder.makeBigraph();
    }

}
