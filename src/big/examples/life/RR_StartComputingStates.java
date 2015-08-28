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

/**
 * Rewriting Rule for switching the state from  update cells to 
 * compute next cell states.
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RR_StartComputingStates extends RewRuleWProps {

    private static final Bigraph redex, reactum;
    private static final InstantiationMap map;
    private final static LinkedList<String> auxProperties;

    static {
        redex = generateRedex();
        reactum = generateReactum();
        map = new InstantiationMap(1, 0);
        auxProperties = new LinkedList<>();
    }

    private static Bigraph generateRedex() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // 
        Node beta = builder.addNode("update", r);
        Node L = builder.addNode("nextStateLive", r);
        Node D = builder.addNode("nextStateDead", r);

        return builder.makeBigraph();
    }

    private static Bigraph generateReactum() {
        BigraphBuilder builder = new BigraphBuilder(GameOfLife.SIGNATURE);
        Root r = builder.addRoot();

        // State
        Node alpha = builder.addNode("computeNextStates", r);
        Node L = builder.addNode("nextStateLive", r);
        Node D = builder.addNode("nextStateDead", r);
        
        return builder.makeBigraph();
    }

    public RR_StartComputingStates() {
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
