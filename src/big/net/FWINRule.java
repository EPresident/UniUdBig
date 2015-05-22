package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * Class for the entry of a packet in a firewall. The packet must have a token
 * (TokenIN) linked to the list of permissions of the firewall.
 *
 * The TokenIN can be generated by the "NewTokenINRuleFar" class.
 *
 * @see NewTokenINRuleFar
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class FWINRule extends RewRuleWProps {

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

    public FWINRule() {
        super(redex, reactum, map);
    }

    protected List<String> getAuxProperties() {
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
        Root r = builder.addRoot();
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        builder.addSite(firewall);//Site 0
        //Packet
        OuterName idR = builder.addOuterName("idR");
        OuterName idS = builder.addOuterName("idS");
        Node packet = builder.addNode("packet", r, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 1
        //Token
        Node tokenIN = builder.addNode("tokenIN", packet, listFWIN);
        tokenIN.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "tokenIN")));

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r = builder.addRoot();
        //Firewall
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        builder.addSite(firewall);//Site 0
        //Packet
        OuterName idR = builder.addOuterName("idR");
        OuterName idS = builder.addOuterName("idS");
        Node packet = builder.addNode("packet", firewall, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 1

        return builder.makeBigraph();
    }
}
