/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.sim;

import big.bsg.BigStateGraph;
import big.bsg.BSGNode;
import big.brs.BRS;
import big.brs.RuleApplication;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Builds the graph (pseudo)randomly. For each node, it builds all possible
 * branches (a la breadth first), and then chooses one at random to continue
 * with (by placing it in front of the queue). If all the possible branches
 * weren't computed for each visited node, it would become hard to tell when the
 * simulation is over.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RandomSim implements SimStrategy {

    private final Random randomGen;

    public RandomSim() {
        randomGen = new Random();
    }

    @Override
    public void step(LinkedList<BSGNode> nodeQueue, BRS brs, BigStateGraph graph) {
        BSGNode node = nodeQueue.pop();
        List<RuleApplication> ras = brs.apply_RA(node.getState());

        // Random choice
        if (ras.size() > 0) {
            int rand = randomGen.nextInt(ras.size());
            RuleApplication chosen = ras.get(rand);
            ras.remove(rand);
            ras.add(chosen);
        }

        for (RuleApplication ra : ras) {
            BSGNode newNode = graph.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
        }
    }

    @Override
    public List<RuleApplication> stepAndGet(LinkedList<BSGNode> nodeQueue, BRS brs, BigStateGraph graph) {
        BSGNode node = nodeQueue.pop();
        List<RuleApplication> ras = brs.apply_RA(node.getState());
        LinkedList<RuleApplication> lra = new LinkedList<>();
        
        // Random choice
        if (ras.size() > 0) {
            int rand = randomGen.nextInt(ras.size());
            RuleApplication chosen = ras.get(rand);
            ras.remove(rand);
            ras.add(chosen);
        }
        
        for (RuleApplication ra : ras) {
            BSGNode newNode = graph.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
            lra.add(ra);
        }
        System.out.println(lra.size()+" applications");
        return lra;
    }

}
