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
package big.sim;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;

import java.util.LinkedList;
import java.util.List;

import big.brs.BRS;
import big.brs.BreadthFirstStrat;
import big.brs.RuleApplication;
import big.bsg.BSGNode;
import big.bsg.BigStateGraph;
import big.iso.Isomorphism;

/**
 * Simulator using a breadth-first approach.
 * 
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BreadthFirstSim extends Sim {
    
    private final LinkedList<BSGNode> nodeQueue;
    
    public BreadthFirstSim(Bigraph big, RewritingRule[] rwrls){
        super(new BigStateGraph(big), new BRS(new BreadthFirstStrat(), rwrls));
        nodeQueue = new LinkedList<>();
        nodeQueue.add(bsg.getRoot());
    }
    
    public BreadthFirstSim(Bigraph big, RewritingRule[] rwrls, Isomorphism iso){
        super(new BigStateGraph(big,iso), new BRS(new BreadthFirstStrat(), rwrls));
        nodeQueue = new LinkedList<>();
        nodeQueue.add(bsg.getRoot());
    }
    
    
    @Override
    public void step() {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        for (RuleApplication ra : ras) {
            BSGNode newNode = bsg.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
        }
    }

    @Override
    public List<RuleApplication> stepAndGet() {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        LinkedList<RuleApplication> lra = new LinkedList<>();
        for (RuleApplication ra : ras) {
        	BSGNode newNode = bsg.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
            lra.add(ra);
        }
        return lra;
    }

    @Override
    public boolean simOver() {
        return nodeQueue.isEmpty();
    }

}
