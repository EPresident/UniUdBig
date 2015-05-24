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

import java.util.LinkedList;
import java.util.List;

/**
 * Class for simulating the evolution of bigraphic systems.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Sim {

    private final BigStateGraph graph;
    private final BRS brs;
    private final LinkedList<BSGNode> nodeQueue;

    public Sim(BigStateGraph bs, BRS br) {
        graph = bs;
        brs = br;
        nodeQueue = new LinkedList<>();
        nodeQueue.add(graph.getRoot());
    }

    public void step() {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        for (RuleApplication ra : ras) {
            nodeQueue.add(graph.applyRewritingRule(node, ra.ruleName, ra.big));
        }
    }

    public List<RuleApplication> stepAndGet() {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        LinkedList<RuleApplication> lra = new LinkedList<>();
        for (RuleApplication ra : ras) {
            BSGNode newNode = graph.applyRewritingRule(node, ra.ruleName, ra.big);
            if(newNode!=null){
                nodeQueue.add(newNode);
            }
            lra.add(ra);
        }
        System.out.println("StepAndGet: "+lra.size()+" new applications");
        return lra;
    }
    
    public boolean hasNext(){
        return !nodeQueue.isEmpty();
    }
}
