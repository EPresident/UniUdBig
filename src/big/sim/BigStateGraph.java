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

import big.hash.BigHashFunction;
import big.hash.PlaceGraphBHF;
import big.hash.PlaceLinkBHF;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Edge;
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.InnerName;
import it.uniud.mads.jlibbig.core.std.LinkEntity;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.Root;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;

/**
 * Represents a graph where each node is a different possible state of the
 * starting Bigraph. The state is modified through the application of the
 * rewriting rules.
 *
 * TODO: smarter, more fault-tollerant rew-rule naming system.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigStateGraph {

    /**
     * Nodes of the graph.
     */
    private final List<BSGNode> nodes;
    private final BSGNode root;
    /**
     * Last node added to the graph.
     */
    private BSGNode current;
    /**
     * Hash function used to determine if two states (bigraphs) are similar
     * enough to warrant a full equality check.
     */
    private BigHashFunction hashFunc;
    public static final BigHashFunction PLACE_HASH = new PlaceGraphBHF(),
            PLACELINK_HASH = new PlaceLinkBHF();
    /**
     * Hash table used to quickly detect (possible) duplicates on the fly.
     */
    HashMap<Integer, BSGNode> hashTable;

    public BigStateGraph(Bigraph big, BigHashFunction bhf) {
        hashFunc = bhf;
        root = new BSGNode(big, hashFunc);
        nodes = new LinkedList<>();
        hashTable = new HashMap<>();
        nodes.add(root);
        hashTable.put(root.getHashCode(), root);
        current = root;
    }

    public BigStateGraph(Bigraph big) {
        this(big, PLACELINK_HASH);
    }

    /**
     * Adds a new state to the state graph, through application of a rewriting
     * rule to the current state(i.e. the last one added/selected).
     *
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     * @return The new state reached (as a BSGNode).
     */
    public BSGNode applyRewritingRule(String rewritingRule, Bigraph reactum) {
        return current = applyRewritingRule(current, rewritingRule, reactum);
    }

    /**
     * Applies a rewriting rule, generating a new state. If the state already
     * exists, a cycle is created in the graph.
     *
     * @param redex BSGNode to whom the rule is applied.
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     * @return The new state reached (as a BSGNode).
     */
    public BSGNode applyRewritingRule(BSGNode redex, String rewritingRule, Bigraph reactum) {
        // Find duplicate node (if present)
        BSGNode dup = findDuplicate(redex, reactum);
        if (dup == null) {
            // Generate a new state, build links
            BSGNode newNode = new BSGNode(reactum, hashFunc);
            nodes.add(newNode);
            hashTable.put(newNode.getHashCode(), newNode);
            redex.addLink(newNode, rewritingRule);
            return newNode;
        } else {
            // Create a cycle
            //System.out.println("iso - ");
            // redex.addLink(dup, rewritingRule);
            //return dup;
            return null;
        }
    }

    /**
     * Searches for duplicate states in the graph, i.e. detects cycles. A coarse
     * selection is made by using the bigraph hash function, a more accurate
     * equality test will be made with an isomorphism-checking function.
     *
     * @param redex BSGNode to whom the rule is applied.
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     * @return A BSGNode that has the same state of the reactum, or null.
     */
    private BSGNode findDuplicate(BSGNode redex, Bigraph reactum) {
        // Use the hash table to find possible duplicates
        BSGNode dup = hashTable.get(redex.getHashCode());
        if (dup == null) {
            // No duplicates detected
            return null;
        } else {
            // Hash collision suggests possible duplicate (or isomorphism)
            // Check isomorphism
            if (areIsomorph(redex.getState(), reactum)) {
                return dup;
            }
            return null;
        }
    }

    public BSGNode getRoot() {
        return root;
    }

    /**
     * Returns the last node a rule has been applied to.
     *
     * @return The last node a rule has been applied to, or the root.
     */
    public BSGNode getLastNodeUsed() {
        return current;
    }

    /**
     * Checks if two Bigraphs are isomorph.
     *
     * @param a First Bigraph.
     * @param b Second Bigraph.
     * @return <i>true</i> if the Bigraphs are isomorph, <i>false</i> otherwise.
     * @author EPresident <prez_enquiry@hotmail.com>
     * 		   Luca Geatti <geatti.luca@spes.uniud.it>
     */
 public static boolean areIsomorph(Bigraph a, Bigraph b){
    	Solver linkSolver = new Solver("Link Graph Isomorphism");
    	Solver placeSolver = new Solver("Place Graph Isomorphism");
    	
    	/*
    	 * LinkGraph Variables and Constraints 
    	 */
    	IntVar[] aux = new IntVar[1];
    	Collection<? extends Node> nodesA = a.getNodes();
    	Collection<? extends InnerName> innersA = a.getInnerNames();
    	Collection<Port> portsA = new HashSet<>();
    	Collection<? extends OuterName> outersA = a.getOuterNames();
    	Collection<? extends Edge> edgesA = a.getEdges();
    	
    	Collection<? extends Node> nodesB = b.getNodes();
    	Collection<? extends InnerName> innersB = b.getInnerNames();
    	Collection<Port> portsB = new HashSet<>();
    	Collection<? extends OuterName> outersB = b.getOuterNames();
    	Collection<? extends Edge> edgesB = b.getEdges();
    	
    	for(Node nodeA : a.getNodes()){
    		portsA.addAll(nodeA.getPorts());
    	}
    	
    	for(Node nodeB : b.getNodes()){
    		portsB.addAll(nodeB.getPorts());
    	}
    	
    	
    	if(nodesA.size()!=nodesB.size()  ||  innersA.size()!=innersB.size() || outersA.size()!=outersB.size()
    			|| portsA.size()!=portsB.size() || edgesA.size()!= edgesB.size() ){
    		return false;
    	}
    	
    	
    	//Fixed Values for the structure of a's LinkGraph.
    	//Map from Points of A to their IntegerVariables.
    	HashMap<LinkEntity, HashMap<LinkEntity, IntVar>> linksA = new HashMap<LinkEntity, HashMap<LinkEntity, IntVar>>();
    	ArrayList<IntVar> linkVarsA = new ArrayList<IntVar>();
    	for(OuterName outer : outersA){//outernames of "a"
    		HashMap<LinkEntity, IntVar> map = new HashMap<LinkEntity, IntVar>();
    		for(Point point : outer.getPoints()){// points of "a"
    			IntVar var = VF.fixed(1, linkSolver);
    			map.put(point, var);
    			linkVarsA.add(var);
    		}
    		linksA.put(outer, map);
    	}
    	for(Edge e : edgesA){
    		HashMap<LinkEntity, IntVar> map = new HashMap<LinkEntity, IntVar>();
    		for(Point point: e.getPoints()){
    			IntVar var = VF.fixed(1, linkSolver);
    			map.put(point, var);
    			linkVarsA.add(var);
    		}
    		linksA.put(e, map);
    	}
    	
    	

    	HashMap<LinkEntity, HashMap<LinkEntity,IntVar>> pointsA = new HashMap<LinkEntity, HashMap<LinkEntity,IntVar>>();
    	LinkedList<IntVar> pointsABVars = new LinkedList<IntVar>();
    	int pointNumA = 0;
    	//Inners of "a"
    	for(InnerName innerA : innersA){
    		int pointNumB = 0;
    		//Variables for the edge from Points of "a" to Points of "b"
    		HashMap<LinkEntity, IntVar> pointsAB = new HashMap<LinkEntity, IntVar>();
    		for(InnerName innerB : innersB){
    			IntVar var = VF.bool("PA_PB_"+pointNumA+"_"+pointNumB, linkSolver);
    			pointsAB.put(innerB, var);
    			pointsABVars.add(var);
    			pointNumB++;
    		}
    		
    		pointsA.put(innerA, pointsAB);
    		pointNumA++;
    	}
    	
    	//Points of "a"
    	for(Port port : portsA){
	    		int pointNumB = 0;
	    		HashMap<LinkEntity, IntVar> pointsAB = new HashMap<LinkEntity, IntVar>();
	    		for(Node nodeB : nodesB){
	    			for(Port portB : nodeB.getPorts()){
	    				IntVar var = VF.bool("PA_PB_"+pointNumA+"_"+pointNumB, linkSolver);
	    				if(port.getNumber() != portB.getNumber()){
	    					var = VF.fixed(0, linkSolver);
	    				}
	    				pointsAB.put(portB, var);
	    				pointsABVars.add(var);
	    				pointNumB++;
	    			}
	    		}
	    		pointsA.put(port, pointsAB);
	    		pointNumA++;
    	}
    	
    	
    	
    	//Outers of "b"
    	HashMap<LinkEntity, HashMap<LinkEntity,IntVar>> handlesB = new HashMap<LinkEntity, HashMap<LinkEntity,IntVar>>();
    	HashMap<LinkEntity, HashMap<LinkEntity,IntVar>> fluxBA = new HashMap<LinkEntity, HashMap<LinkEntity,IntVar>>();
    	ArrayList<IntVar> handleABVars = new ArrayList<IntVar>();
    	int handleNumB = 0;
    	for(OuterName outer : outersB){
    		int handleNumA = 0;
    		HashMap<LinkEntity, IntVar> handlesAB = new HashMap<LinkEntity, IntVar>();
    		HashMap<LinkEntity, IntVar> handlesFluxAB = new HashMap<LinkEntity, IntVar>();
    		for(OuterName outerA : outersA){
    			IntVar var = VF.enumerated("HB_HA_"+handleNumB+"_"+handleNumA, 0, portsA.size()+innersA.size(), linkSolver);
    			IntVar flux = VF.bool("HB_HA_FLUX_"+handleNumB+"_"+handleNumA, linkSolver);
    			handlesAB.put(outerA, var);
    			handlesFluxAB.put(outerA, flux);
    			handleABVars.add(var);
    			handleNumA++;
    		}
    		handlesB.put(outer, handlesAB);
    		fluxBA.put(outer, handlesFluxAB);
    		handleNumB++;
    	}
    	
    	for(Edge e: edgesB){
    		int handleNumA = 0;
    		HashMap<LinkEntity, IntVar> handlesAB = new HashMap<LinkEntity, IntVar>();
    		HashMap<LinkEntity, IntVar> handlesFluxAB = new HashMap<LinkEntity, IntVar>();
    		for(Edge eB : edgesA){
    			IntVar var = VF.enumerated("HB_HA_"+handleNumB+"_"+handleNumA, 0, portsA.size()+innersA.size(),linkSolver);
    			IntVar flux = VF.bool("HB_HA_FLUX_"+handleNumB+"_"+handleNumA, linkSolver);
    			handlesAB.put(eB, var);
    			handleABVars.add(var);
    			handlesFluxAB.put(eB, flux);
    			handleNumA++;
    		}
    		handlesB.put(e, handlesAB);
    		fluxBA.put(e, handlesFluxAB);
    		handleNumB++;
    	}
    	
    	

    	
    	
    	/*
    	 * First Constraint (M1) --Source Constraint
    	 */
    	int outFluxNum = 0;
    	for(InnerName innerA : innersA){
    		HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
    		IntVar[] outFlux = map.values().toArray(aux);
    		//Constraint
    		linkSolver.post( ICF.sum(outFlux, VF.fixed(1, linkSolver)));
    		outFluxNum++;
    	}
    	
    	for(Port port : portsA){
    		HashMap<LinkEntity, IntVar> map = pointsA.get(port);
    		IntVar[] outFlux = map.values().toArray(aux);
    		//Constraint
    		linkSolver.post( ICF.sum(outFlux, VF.fixed(1, linkSolver)) );
    		outFluxNum++;
    	}
    	
    	
    	
    	/*
    	 * Second Constraint (M2) --Source Constraint
    	 */
    	for(InnerName innerB : innersB){
    		ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
    		for(InnerName innerA : innersA){
    			IntVar var = pointsA.get(innerA).get(innerB);
    			fluxIN.add(var);
    		}
    		//Constraint
    		IntVar[] fluxINArray = fluxIN.toArray(aux);
    		linkSolver.post( ICF.sum(fluxINArray, VF.fixed(1, linkSolver)) );
    	}
    	
    	for(Port portB : portsB){
    		ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
    		for(Port portA : portsA){
    			IntVar var = pointsA.get(portA).get(portB);
    			if(portA.getNumber() != portB.getNumber()){
    				var = VF.fixed(0, linkSolver);
    			}
    			fluxIN.add(var);
    		}
    		//Constraint
    		IntVar[] fluxINArray = fluxIN.toArray(aux);
       		linkSolver.post( ICF.sum(fluxINArray, VF.fixed(1, linkSolver)) );
    	}
    	
    	
    	
    	
    	/*
    	 * Third Constraint (M3)
    	 */
    	int sumNum=0;
    	for(OuterName outerB : outersB){
    		ArrayList<IntVar> fluxOUT = new ArrayList<IntVar>();
    		for(InnerName innerA : innersA){
    			HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
    			for(Point innerB : outerB.getPoints()){
    				if(innerB.isInnerName()){
    					IntVar var = map.get(innerB);
    					fluxOUT.add(var);
    				}
    			}
    		}
    		for(Port portA : portsA){
    			HashMap<LinkEntity, IntVar> map = pointsA.get(portA);
    			for(Point portB : outerB.getPoints()){
    				if(portB.isPort()){
    					IntVar var = map.get(portB);
    					fluxOUT.add(var);
    				}
    			}
    		}
    		if(!fluxOUT.isEmpty()){
	    		//Constraint
	    		IntVar[] fluxIN = handlesB.get(outerB).values().toArray(aux);
	    		IntVar[] fluxOUTArray = fluxOUT.toArray(aux);
	    		IntVar sum = VF.enumerated("SUM_"+sumNum, 0, innersA.size()+portsA.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxIN, sum) );
	    		linkSolver.post( ICF.sum(fluxOUTArray, sum) );
    		}
    		
    		
    		sumNum++;
    	}
    	
    	for(Edge eB : edgesB){
    		ArrayList<IntVar> fluxOUT = new ArrayList<IntVar>();
    		for(InnerName innerA : innersA){
    			HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
    			for(Point innerB : eB.getPoints()){
    				if(innerB.isInnerName()){
    					IntVar var = map.get(innerB);
    					fluxOUT.add(var);
    				}
    			}
    		}
    		for(Port portA : portsA){
    			HashMap<LinkEntity, IntVar> map = pointsA.get(portA);
    			for(Point portB : eB.getPoints()){
    				if(portB.isPort()){
    					IntVar var = map.get(portB);
    					fluxOUT.add(var);
    				}
    			}
    		}
    		if(!fluxOUT.isEmpty()){
	    		//Constraint
	    		IntVar[] fluxIN = handlesB.get(eB).values().toArray(aux);
	    		IntVar[] fluxOUTArray = fluxOUT.toArray(aux);
	    		IntVar sum = VF.enumerated("SUM_"+sumNum, 0, outersA.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxIN, sum) );
	    		linkSolver.post( ICF.sum(fluxOUTArray, sum) );
    		}
    		sumNum++;
    	}
    	
    	
    	/*
    	 * Fourth Constraint (M4) --The most important: it closes the flow --Sink Constraint
    	 * Seventh Constraint (M7)
    	 */
    	
    	int sinkNum = 0;
    	for(OuterName outerA : outersA){
    		//Left Flow
    		ArrayList<IntVar> fluxLeft = new ArrayList<IntVar>();
    		HashMap<LinkEntity, IntVar> map = linksA.get(outerA);
    		for(Port portA : portsA){
    			IntVar var = map.get(portA);
    			if(var != null){
    				fluxLeft.add(var);
    			}
    		}
    		for(InnerName innerA : innersA){
    			IntVar var = map.get(innerA);
    			if(var != null){
    				fluxLeft.add(var);
    			}
    		}
    		
    		//Bottom Flow
    		ArrayList<IntVar> fluxBottom = new ArrayList<IntVar>();
    		for(OuterName outerB : outersB){
    			HashMap<LinkEntity, IntVar> mapB = handlesB.get(outerB);
    			IntVar var = mapB.get(outerA);
    			if(var != null){
    				fluxBottom.add(var);
    			}
    			HashMap<LinkEntity, IntVar> flowB = fluxBA.get(outerB);
    			IntVar flow = flowB.get(outerA);
    			if(flow != null){	
    				linkSolver.post( ICF.times(var, flow, var) );
    			}
    		}
    		for(Edge eB : edgesB){
    			HashMap<LinkEntity, IntVar> mapB = handlesB.get(eB);
    			IntVar var = mapB.get(outerA);
    			if(var != null){
    				fluxBottom.add(var);
    			}
    			HashMap<LinkEntity, IntVar> flowB = fluxBA.get(eB);
    			IntVar flow = flowB.get(outerA);
    			if(flow != null){	
    				linkSolver.post( ICF.times(var, flow, var) );
    			}
    		}
    		
    		
	    	if( !fluxLeft.isEmpty() && !fluxBottom.isEmpty()){
	    		//Constraints M4
	    		IntVar[] fluxLeftArray = fluxLeft.toArray(aux);
	    		IntVar sumLeft = VF.enumerated("SumLeft"+sinkNum, 0, innersA.size()+portsA.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxLeftArray,"=" , sumLeft) );
	    		
	    		IntVar[] fluxBottomArray = fluxBottom.toArray(aux);
	    		IntVar sumBottom = VF.enumerated("SumBottom"+sinkNum, 0, outersB.size()+edgesB.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxBottomArray, sumBottom) );
	    		
	    		linkSolver.post( ICF.arithm(sumLeft, "=", sumBottom) );
	    		
	    	}	
    		
    		sinkNum++;
    	}
    	
    	for(Edge eA : edgesA){
    		//Left Flow
    		ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
    		HashMap<LinkEntity, IntVar> map = linksA.get(eA);
    		for(Port portA : portsA){
    			IntVar var = map.get(portA);
    			if(var != null)
    				fluxIN.add(var);
    		}
    		for(InnerName innerA : innersA){
    			IntVar var = map.get(innerA);
    			if(var != null)
    				fluxIN.add(var);
    		}
    		
    		//Bottom Flow
    		ArrayList<IntVar> fluxBottom = new ArrayList<IntVar>();
    		for(OuterName outerB : outersB){
    			HashMap<LinkEntity, IntVar> mapB = handlesB.get(outerB);
    			IntVar var = mapB.get(eA);
    			if(var != null){	
    				fluxBottom.add(var);
    			}
    			HashMap<LinkEntity, IntVar> flowB = fluxBA.get(outerB);
    			IntVar flow = flowB.get(eA);
    			if(flow != null){	
    				linkSolver.post( ICF.times(var, flow, var) );
    			}
    		}
    		for(Edge eB : edgesB){
    			HashMap<LinkEntity, IntVar> mapB = handlesB.get(eB);
    			IntVar var = mapB.get(eA);
    			if(var != null){
    				fluxBottom.add(var);
    			}
    			HashMap<LinkEntity, IntVar> flowB = fluxBA.get(eB);
    			IntVar flow = flowB.get(eA);
    			if(flow != null){	
    				linkSolver.post( ICF.times(var, flow, var) );
    			}
    		}
    		
    		if( !fluxIN.isEmpty() && !fluxBottom.isEmpty()){
	    		//Constraints
	    		IntVar[] fluxINArray = fluxIN.toArray(aux);
	    		IntVar sumLeft = VF.enumerated("SumLeft"+sinkNum, 0, innersA.size()+pointsA.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxINArray , sumLeft) );
	    		
	    		IntVar[] fluxBottomArray = fluxBottom.toArray(aux);
	    		IntVar sumBottom = VF.enumerated("SumBottom"+sinkNum, 0, outersB.size()+edgesB.size(), linkSolver);
	    		linkSolver.post( ICF.sum(fluxBottomArray, sumBottom) );
	    		
	    		linkSolver.post( ICF.arithm(sumLeft, "=", sumBottom) );
    		}
    		sinkNum++;
    	}
    	
    	
    	
    	/*
    	 * Fifth Constraint (M5) --the total flow out of the handles of "b" must be exactly 1.
    	 */
    	for(OuterName outerB : outersB){
    		HashMap<LinkEntity, IntVar> map = fluxBA.get(outerB);
    		IntVar[] flows = map.values().toArray(aux);
    		//Constraint
    		if(flows.length>0){
    			linkSolver.post( ICF.sum(flows, VF.fixed(1, linkSolver)) );
    		}
    	}
    	for(Edge eB : edgesB){
    		HashMap<LinkEntity, IntVar> map = fluxBA.get(eB);
    		IntVar[] flows = map.values().toArray(aux);
    		//Constraint
    		if(flows.length>0){
    			linkSolver.post( ICF.sum(flows, VF.fixed(1, linkSolver)) );
    		}
    	}
    	
    	
    	/*
    	 * Sixth Constraint (M6) --the total flow into the handles of "a" must be exactly 1.
    	 */
    	for(OuterName outerA : outersA){
    		ArrayList<IntVar> flows = new ArrayList<IntVar>();
    		for(OuterName outerB : outersB){
    			HashMap<LinkEntity, IntVar> map = fluxBA.get(outerB);
    			IntVar var = map.get(outerA);
    			if(var != null){
    				flows.add(var);
    			}
    		}
    		if(!flows.isEmpty()){
    			IntVar[] flowsArray = flows.toArray(aux);
        		linkSolver.post( ICF.sum(flowsArray, VF.fixed(1, linkSolver)) );
    		}
    	}
    	for(Edge eA : edgesA){
    		ArrayList<IntVar> flows = new ArrayList<IntVar>();
    		for(Edge eB : edgesB){
    			HashMap<LinkEntity, IntVar> map = fluxBA.get(eB);
    			IntVar var = map.get(eA);
    			if(var != null){
    				flows.add(var);
    			}
    		}
    		if(!flows.isEmpty()){
    			IntVar[] flowsArray = flows.toArray(aux);
        		linkSolver.post( ICF.sum(flowsArray, VF.fixed(1, linkSolver)) );
    		}
    	}
    	
    	
    	/*
    	 * Place Graph Variables and Constraints
    	 */
    	//<editor-fold desc="Place graph isomorphism">
        LinkedList<PlaceEntity> placeEntA = new LinkedList<>();
        for (Root r : a.getRoots()) {
            placeEntA.add(r);
        }
        placeEntA.addAll(a.getNodes());

        LinkedList<PlaceEntity> placeEntB = new LinkedList<>();
        for (Root r : b.getRoots()) {
            placeEntB.add(r);
        }
        placeEntB.addAll(b.getNodes());

        int nPlcEntA = placeEntA.size(), nPlcEntB = placeEntB.size();

        if (nPlcEntA != nPlcEntB) {
            // Mismatching place entity cardinality
            return false;
        }

        // FIXME: very inefficient
        int[] flowA = new int[nPlcEntA], flowB = new int[nPlcEntB];
        for (int i = 0; i < nPlcEntA; i++) {
            flowA[i] = getPlaceFlow(placeEntA.get(i));
            flowB[i] = getPlaceFlow(placeEntB.get(i));
        }

        BoolVar[][] placeEntVarsR = VF.boolMatrix("placeEntVars", nPlcEntA,
                nPlcEntA, placeSolver);
        BoolVar[][] placeEntVarsC = new BoolVar[nPlcEntA][nPlcEntA];
        IntVar one2 = VF.fixed(1, placeSolver);

        for (int i = 0; i < nPlcEntA; i++) {
            for (int j = 0; j < nPlcEntA; j++) {
                placeEntVarsC[j][i] = placeEntVarsR[i][j];
            }
        }

        // Constraints
        for (int i = 0; i < nPlcEntA; i++) {
        	placeSolver.post(ICF.sum(placeEntVarsR[i], one2));
        	placeSolver.post(ICF.scalar(placeEntVarsR[i], flowB, VF.fixed(flowA[i], placeSolver)));
        }
        for (int i = 0; i < nPlcEntA; i++) {
        	placeSolver.post(ICF.sum(placeEntVarsC[i], one2));
        	placeSolver.post(ICF.scalar(placeEntVarsC[i], flowA, VF.fixed(flowB[i], placeSolver)));
        }
        //</editor-fold>

    	
    	return linkSolver.findSolution() && placeSolver.findSolution();
     }
    
    
    

    private static int getPlaceFlow(PlaceEntity pe) {
        int flow = 1;
        if (pe.isRoot()) {
            Root r = (Root) pe;
            for (Child c : r.getChildren()) {
                flow += getPlaceFlow(c);
            }
            return flow;
        }
        if (pe.isNode()) {
            Node n = (Node) pe;
            for (Child c : n.getChildren()) {
                flow += getPlaceFlow(c);
            }
            return flow;
        }
        System.err.println("suspicious...");
        return 1;
    }
    
    
    public int getGraphSize() {
        return nodes.size();
    }
}
