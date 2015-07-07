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
import big.brs.RuleApplication;
import java.util.List;

import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import java.util.LinkedList;

/**
 * Class for simulating the evolution of bigraphic systems. This class might be
 * superflous: strategies could be used directly.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Sim {

    private final SimStrategy simStrat;
    private final LinkedList<BSGNode> nodeQueue;
    private final BigStateGraph graph;
    private final BRS brs;

    /**
     * Creates a new Sim with the default breadth-first strategy.
     *
     * @param br The BRS to use
     * @param root Starting Bigraph
     */
    public Sim(Bigraph root, BRS br) {
        this(root, br, new BreadthFirstSim());
    }

    public Sim(Bigraph root, BRS brs, SimStrategy ss) {
        simStrat = ss;
        graph = new BigStateGraph(root);
        this.brs = brs;
        nodeQueue = new LinkedList<>();
        nodeQueue.add(graph.getRoot());
    }

    private final BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();

    /**
     * Makes a step in the simulation, i.e. it applies the BRS to the current
     * state.
     */
    public void step() {
        simStrat.step(nodeQueue, brs, graph);
    }

    /**
     * Makes a step in the simulation and returns the result(s) of applied
     * rules.
     *
     * @return A List of RuleApplications (state+rule)
     */
    public List<RuleApplication> stepAndGet() {
        return simStrat.stepAndGet(nodeQueue, brs, graph);
    }

    /**
     * Returns true if the simulation is over, which means that all possible
     * states have been created.
     *
     * @return true if the simulation is over.
     */
    public boolean hasNext() {
        return !nodeQueue.isEmpty();
    }

    /**
     * Computes the entire state graph.
     *
     * @param max Maximum number of iterations.
     * @return
     */
    public BigStateGraph fullSim(int max) {
        while (max > 0 && this.hasNext()) {
            this.stepAndGet();
            max--;
        }
        return graph;
    }

    public BigStateGraph getGraph() {
        return graph;
    }

}
