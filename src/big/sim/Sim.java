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

import big.brs.BRS;
import big.bsg.BigStateGraph;
import big.brs.RuleApplication;
import java.util.List;


/**
 * Base class for simulating the possible evolutions of a bigraphical reactive 
 * system. The data management and logic is up to the implementations.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public abstract class Sim {
    
    protected final BigStateGraph bsg;
    protected final BRS brs;
    
    protected Sim(BigStateGraph graph, BRS br){
        bsg = graph;
        brs=br;
    }

    /**
     * Makes a step in the simulation, i.e. it applies the BRS to the current
     * state.
     */
    public abstract void step();

    /**
     * Makes a step in the simulation and returns the result(s) of applied
     * rules.
     *
     * @return A List of RuleApplications (state+rule)
     */
    public abstract List<RuleApplication> stepAndGet();
    
    /**
     * Returns true if the simulation is over, which means that all possible
     * states have been created.
     * Implementation is optional: some Sims might not know when all states have
     * been computed.
     *
     * @return true if the simulation is over.
     */
    public abstract boolean simOver() throws UnsupportedOperationException;

    /**
     * Computes the entire state graph.
     *
     * @param max Maximum number of iterations (0 means unlimited).
     * @return A BigStateGraph with all possible states.
     */
    public BigStateGraph fullSim(int max) {
        while (max > 0 && !simOver()) {
            this.step();
            max--;
        }
        return getGraph();
    }

    /**
     * @return The state graph computed so far.
     */
    public BigStateGraph getGraph(){
        return bsg;
    }

}
