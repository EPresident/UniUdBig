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

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public interface SimStrategy {

    /**
     * Makes a step in the simulation, i.e. it applies the BRS to the current
     * state.
     */
    public void step(LinkedList<BSGNode> nodeQueue, BRS brs, BigStateGraph graph);

    /**
     * Makes a step in the simulation and returns the result(s) of applied
     * rules.
     *
     * @return A List of RuleApplications (state+rule)
     */
    public List<RuleApplication> stepAndGet(LinkedList<BSGNode> nodeQueue, 
            BRS brs, BigStateGraph graph);

}
