package big.net;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

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
    
    static{
        redex=generateRedex();
        reactum=generateReactum();
        map=new InstantiationMap(1,0);
    }

    public DecapRule() {
        super(redex, reactum, map);
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(Utils.getNetSignature());
        Root r1 = builder.addRoot();
        OuterName id1 = builder.addOuterName("id1");
        OuterName down1 = builder.addOuterName("down1");
        Node sn1 = builder.addNode("stackNode", r1, id1, down1);
        sn1.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("NodeType", "EncapSender")));

        Root r2 = builder.addRoot();
        OuterName id2 = builder.addOuterName("id2");
        OuterName down2 = builder.addOuterName("down2");
        Node sn2 = builder.addNode("stackNode", r2, id2, down2);
        sn2.attachProperty(new SharedProperty<String>(
                new SimpleProperty("NodeType", "EncapReceiver")));

        Node packet = builder.addNode("packet", r2, id1, id2);
        packet.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("PacketType", "packet")));
        builder.addSite(packet);

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

        Root r2 = builder.addRoot();
        OuterName id2 = builder.addOuterName("id2");
        OuterName down2 = builder.addOuterName("down2");
        Node sn2 = builder.addNode("stackNode", r2, id2, down2);
        sn2.attachProperty(new SharedProperty<String>(
                new SimpleProperty("NodeType", "EncapReceiver")));
        builder.addSite(r2);

        return builder.makeBigraph();
    }

}
