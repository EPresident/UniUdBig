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
import it.uniud.mads.jlibbig.core.std.OuterName;

/**
 * Rewriting Rule for cell death when the are fewer than one live cell
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RR_Die1m extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        int[] v = new int[15];
        for (int i = 0; i < v.length; i++) {
            v[i] = i;
        }
        map = new InstantiationMap(v.length, v);
        auxProperties = new LinkedList<>();
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // State
        Node alpha = builder.addNode("alpha", r);
        OuterName linkU = builder.addOuterName("linkU");
        Node u = builder.addNode("u", r, linkU);
        OuterName linkD = builder.addOuterName("linkD");
        Node D = builder.addNode("D", r, linkD);

        // Subject
        OuterName linkS = builder.addOuterName("linkS");
        Node subject = builder.addNode("cell", r, linkS, linkU);       
        builder.addSite(subject);

        // Neighbors
        // add dead cell 1
        OuterName linkN = builder.addOuterName("linkN0");
        OuterName stateN = builder.addOuterName("stateN0");
        Node n = builder.addNode("cell", r, linkN, stateN);
        Node lh = builder.addNode("linkHolder", n);
        Node l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        builder.addSite(lh);      
        // add dead cell 2
        linkN = builder.addOuterName("linkN1");
        stateN = builder.addOuterName("stateN1");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);       
        builder.addSite(lh);
        // add dead cell 3
        linkN = builder.addOuterName("linkN2");
        stateN = builder.addOuterName("stateN2");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 4
        linkN = builder.addOuterName("linkN3");
        stateN = builder.addOuterName("stateN3");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 5
        linkN = builder.addOuterName("linkN4");
        stateN = builder.addOuterName("stateN4");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 6
        linkN = builder.addOuterName("linkN5");
        stateN = builder.addOuterName("stateN5");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 7
        linkN = builder.addOuterName("linkN6");
        stateN = builder.addOuterName("stateN6");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);      

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // State
        Node alpha = builder.addNode("alpha", r);
        OuterName linkU = builder.addOuterName("linkU");
        Node u = builder.addNode("u", r, linkU);
        OuterName linkD = builder.addOuterName("linkD");
        Node D = builder.addNode("D", r, linkD);

        // Subject
        OuterName linkS = builder.addOuterName("linkS");
        Node subject = builder.addNode("cell", r, linkS, linkD);       
        builder.addSite(subject);

        // Neighbors
        // add dead cell 1
        OuterName linkN = builder.addOuterName("linkN0");
        OuterName stateN = builder.addOuterName("stateN0");
        Node n = builder.addNode("cell", r, linkN, stateN);
        Node lh = builder.addNode("linkHolder", n);
        Node l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        builder.addSite(lh);      
        // add dead cell 2
        linkN = builder.addOuterName("linkN1");
        stateN = builder.addOuterName("stateN1");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);       
        builder.addSite(lh);
        // add dead cell 3
        linkN = builder.addOuterName("linkN2");
        stateN = builder.addOuterName("stateN2");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 4
        linkN = builder.addOuterName("linkN3");
        stateN = builder.addOuterName("stateN3");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 5
        linkN = builder.addOuterName("linkN4");
        stateN = builder.addOuterName("stateN4");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 6
        linkN = builder.addOuterName("linkN5");
        stateN = builder.addOuterName("stateN5");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);
        // add dead cell 7
        linkN = builder.addOuterName("linkN6");
        stateN = builder.addOuterName("stateN6");
        n = builder.addNode("cell", r, linkN, stateN);
        lh = builder.addNode("linkHolder", n);
        builder.addSite(lh);
        l = builder.addNode("link", lh, linkS);
        builder.addSite(l);      

        return builder.makeBigraph();
    }

    public RR_Die1m() {
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
