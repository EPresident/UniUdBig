package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;

/**
 * This class adds a TokenIN to a packet if and only if the packet has the
 * permissions. This rule checks the sender's field of the packet and the
 * firewall's list.
 *
 * This rule is applicable only if the packet and the firewall are in the same
 * root.
 *
 * @see FWINRule
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class NewTokenINRuleFar extends RewRuleWProps {

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
    private final String DELIMITER = "%";
    private Matcher matcher;

    public NewTokenINRuleFar() {
        super(redex, reactum, map);
        this.matcher = new Matcher();
    }

    protected List<String> getAuxProperties() {
        return auxProperties;
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        for (Property p : original.getProperties()) {// Original = node of the
            // reactum
            Node[] array = rr.get(p.get().toString());
            if (array != null) {
                Node n = array[1]; // Node of the redex
                if (n != null) {
                    Node img = match.getImage(n);// Node of the original bigraph
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
        // Sender
        OuterName idS = builder.addOuterName("idS");
        OuterName downR = builder.addOuterName("downR");
        Node sender = builder.addNode("stackNode", r1, idS, downR);
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "sender")));

        // Firewall
        Root r2 = builder.addRoot();
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r2, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        builder.addSite(firewall);// Site 0

        // Packet
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", r2, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);// Site 1

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        // Sender
        OuterName idS = builder.addOuterName("idS");
        OuterName downR = builder.addOuterName("downR");
        Node sender = builder.addNode("stackNode", r1, idS, downR);
        sender.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "sender")));

        // Firewall
        Root r2 = builder.addRoot();
        OuterName listFWIN = builder.addOuterName("listFWIN");
        OuterName listFWOUT = builder.addOuterName("listFWOUT");
        Node firewall = builder.addNode("firewall", r2, listFWIN, listFWOUT);
        firewall.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "firewall")));
        builder.addSite(firewall);// Site 0

        // Packet
        OuterName idR = builder.addOuterName("idR");
        Node packet = builder.addNode("packet", r2, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);// Site 1

        // Token
        Node tokenIN = builder.addNode("tokenIN", packet, listFWIN);
        tokenIN.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "tokenIN")));

        return builder.makeBigraph();
    }

    private void createRightProperty(Node original, Node instance, Match match) {
        // First step : find the neighbor node of the new node ("EncapSender" is
        // a neighbor of "packetOut").
        // Second step : find the image neighbor in the real bigraph.
        // Third step : Take the name of the second OuterName (on the port 1).

    }

    @Override
    public boolean isApplicable(Bigraph bigraph) {
        Iterator<? extends Match> iter = this.matcher
                .match(bigraph, this.redex).iterator();
        if (iter.hasNext()) {
            Match match = iter.next();
            String listFW = getOuterNameImage("firewall", match, 0);
            LinkedList<String> permissions = prepareList(listFW);
            String idR = getOuterNameImage("sender", match, 0);
            if (checkPermission(idR, permissions)) {
                return true;
            }
        }
        return false;
    }

    private LinkedList<String> prepareList(String str) {
        LinkedList<String> list = new LinkedList<String>();
        String[] array = str.split(DELIMITER);
        for (int i = 0; i < array.length; i++) {
            list.add(array[i]);
        }
        return list;
    }

    private boolean checkPermission(String x, LinkedList<String> list) {
        for (String s : list) {
            if (s.equals(x)) {
                return true;
            }
        }
        return false;
    }

}
