package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Class for the decapsulation reaction. Doesn't matter what protocols are
 * involved. Pay attention to the "auxProperty" list.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class DecapRule extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(1, 0);
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("PacketType");
    }

    public DecapRule() {
        super(redex, reactum, map);
    }

    protected List<String> getAuxProperties() {
        return auxProperties;
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r = builder.addRoot();
        //StackNode
        OuterName idR = builder.addOuterName("idR");
        OuterName downR = builder.addOuterName("downR");
        Node snR = builder.addNode("stackNode", r, idR, downR);
        snR.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Receiver")));
        //Packet
        OuterName idS = builder.addOuterName("idS");
        Node packet = builder.addNode("packet", r, idS, idR);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);//Site 0

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r = builder.addRoot();
        //StackNode
        OuterName idR = builder.addOuterName("idR");
        OuterName downR = builder.addOuterName("downR");
        Node snR = builder.addNode("stackNode", r, idR, downR);
        snR.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "Receiver")));
        //Packet
        OuterName idS = builder.addOuterName("idS");
        builder.addSite(r);//Site 0

        return builder.makeBigraph();
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

}
