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
 * Choose an execution path (pseudo)randomly. This strategy has no way of
 * telling when all possible states have been generated. It just follows one
 * path until no rules can be applied (i.e. a leaf is reached in the BSG).
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class TrueRandomSim extends Sim {

    private final Random randomGen;
    private BSGNode currentNode;

    public TrueRandomSim(Bigraph big, RewritingRule[] rwrls) {
        super(new BigStateGraph(big), new BRS(new BreadthFirstStrat(), rwrls));
        currentNode = bsg.getRoot();
        randomGen = new Random();
    }
    
    public TrueRandomSim(Bigraph big, BRS brs) {
        super(new BigStateGraph(big), brs);
        currentNode = bsg.getRoot();
        randomGen = new Random();
    }

    public TrueRandomSim(Bigraph big, RewritingRule[] rwrls, long seed) {
        super(new BigStateGraph(big), new BRS(new BreadthFirstStrat(), rwrls));
        currentNode = bsg.getRoot();
        randomGen = new Random(seed);
    }

    @Override
    public void step() {
        List<RuleApplication> ras = brs.apply_RA(currentNode.getState());
        int size = ras.size();
        if (size > 0) {
            int rand;
            if (size > 1) {
                rand = randomGen.nextInt(size - 1);
            }else{
                // not much choice...
                rand = 0;
            }
            RuleApplication ra = ras.get(rand);
            BSGNode newNode = bsg.applyRewritingRule(currentNode, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                currentNode = newNode;
            }
        } else {
            currentNode = null;
        }
    }

    @Override
    public List<RuleApplication> stepAndGet() {
        List<RuleApplication> ras = brs.apply_RA(currentNode.getState());
        int size = ras.size();
        if (size > 0) {
            int rand = randomGen.nextInt(size - 1);
            RuleApplication ra = ras.get(rand);
            BSGNode newNode = bsg.applyRewritingRule(currentNode, ra.getRuleName(), ra.getBig());
            if (newNode != null) {
                currentNode = newNode;
            }
            LinkedList<RuleApplication> ret = new LinkedList<>();
            ret.add(ra);
            return ret;
        } else {
            currentNode = null;
            return new LinkedList<>();
        }
    }

    @Override
    public boolean simOver() {
        return currentNode == null;
    }

    /**
     * Gets the state computed in the last step() or stepAndGet() call.
     *
     * @return A Bigraph representing the current state, or null.
     */
    public Bigraph getCurrentState() {
        if (currentNode != null) {
            return currentNode.getState();
        }
        return null;
    }

}
