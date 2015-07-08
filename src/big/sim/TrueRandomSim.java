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
 * Builds the graph (pseudo)randomly. This strategy has no way of telling when 
 * all possible states have been generated.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class TrueRandomSim extends Sim {

    private final Random randomGen;
    private final LinkedList<BSGNode> nodeQueue;

    public TrueRandomSim(Bigraph big, RewritingRule[] rwrls) {
        super(new BigStateGraph(big), new BRS(new BreadthFirstStrat(), rwrls));
        nodeQueue = new LinkedList<>();
        nodeQueue.add(bsg.getRoot());
        randomGen = new Random();
    }

    @Override
    public void step() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<RuleApplication> stepAndGet() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean simOver() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
