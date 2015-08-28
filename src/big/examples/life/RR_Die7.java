/*
 * Copyright (C) 2015 EPresident <prez_enquiry@hotmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package big.examples.life;

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.Root;
import java.util.LinkedList;
import java.util.List;
import static big.examples.life.Utils.*;
import it.uniud.mads.jlibbig.core.std.OuterName;

/**
 * Rewriting Rule for cell death
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RR_Die7 extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        int[] v = new int[17];
        for (int i = 0; i < 17; i++) {
            v[i]=i;
        }
        map = new InstantiationMap(17, v);
        auxProperties = new LinkedList<>();
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // State
        Node alpha = builder.addNode("computeNextStates", r);
        OuterName linkU = builder.addOuterName("linkU");
        Node u = builder.addNode("nextStateUncomputed", r, linkU);
        OuterName linkL = builder.addOuterName("linkL");
        Node L = builder.addNode("nextStateLive", r, linkL);
        OuterName linkD = builder.addOuterName("linkD");
        Node D = builder.addNode("nextStateDead", r, linkD);

        // Subject
        Node subject = builder.addNode("liveCell", r, null, linkU);
        builder.addSite(subject);

        // Neighbors
        Node[] neighbors = addNeighbors(7, subject, (Node) r, builder);

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // State
        Node alpha = builder.addNode("computeNextStates", r);
        OuterName linkU = builder.addOuterName("linkU");
        Node u = builder.addNode("nextStateUncomputed", r, linkU);
        OuterName linkL = builder.addOuterName("linkL");
        Node L = builder.addNode("nextStateLive", r, linkL);
        OuterName linkD = builder.addOuterName("linkD");
        Node D = builder.addNode("nextStateDead", r, linkD);

        // Subject
        Node subject = builder.addNode("liveCell", r, null, linkD);
        builder.addSite(subject);

        // Neighbors
        Node[] neighbors = addNeighbors(7, subject, (Node) r, builder);

        return builder.makeBigraph();
    }

    public RR_Die7() {
        super(redex, reactum, map);
    }

    protected List<String> getAuxProperties() {
        return auxProperties;
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        // no implementation
    }

}
