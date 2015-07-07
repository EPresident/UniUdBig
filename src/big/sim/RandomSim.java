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

import big.bsg.BigStateGraph;
import big.bsg.BSGNode;
import big.brs.BRS;
import big.brs.BreadthFirstStrat;
import big.brs.RuleApplication;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
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
public class RandomSim extends Sim {

    private final Random randomGen;
    private final LinkedList<BSGNode> nodeQueue;

    public RandomSim(Bigraph big, RewritingRule[] rwrls) {
        super(new BigStateGraph(big), new BRS(new BreadthFirstStrat(), rwrls));
        nodeQueue = new LinkedList<>();
        nodeQueue.add(bsg.getRoot());
        randomGen = new Random();
    }

    @Override
    public void step() {
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
            BSGNode newNode = bsg.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
        }
    }

    @Override
    public List<RuleApplication> stepAndGet() {
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
            BSGNode newNode = bsg.applyRewritingRule(node, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                nodeQueue.add(newNode);
            }
            lra.add(ra);
        }
        System.out.println(lra.size()+" applications");
        return lra;
    }

    @Override
    public boolean simOver() {
        return nodeQueue.isEmpty();
    }

}
