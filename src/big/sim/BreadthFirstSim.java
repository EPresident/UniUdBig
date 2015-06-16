/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.sim;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BreadthFirstSim implements SimStrategy {

    @Override
    public void step(LinkedList<BSGNode> nodeQueue, BRS brs, BigStateGraph graph) {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        for (RuleApplication ra : ras) {
            BSGNode newNode = graph.applyRewritingRule(node, ra.ruleName, ra.big);
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
        }
    }

    @Override
    public List<RuleApplication> stepAndGet(LinkedList<BSGNode> nodeQueue, BRS brs, BigStateGraph graph) {
        BSGNode node = nodeQueue.pop();
        Iterable<RuleApplication> ras = brs.apply_RA(node.getState());
        LinkedList<RuleApplication> lra = new LinkedList<>();
        for (RuleApplication ra : ras) {
            BSGNode newNode = graph.applyRewritingRule(node, ra.ruleName, ra.big);
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
            lra.add(ra);
        }
        return lra;
    }

}
