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
import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Edge;
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.InnerName;
import it.uniud.mads.jlibbig.core.std.LinkEntity;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Parent;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Site;

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
	private PropertyMatcher matcher;
	private BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
	
	
	public BigStateGraph(Bigraph big, BigHashFunction bhf) {
		hashFunc = bhf;
		root = new BSGNode(big, hashFunc);
		nodes = new LinkedList<>();
		nodes.add(root);
		current = root;
		this.matcher = new PropertyMatcher();
	}

	public BigStateGraph(Bigraph big) {
		this(big, PLACELINK_HASH);
	}
	
	

	/**
	 * Applies a rewriting rule, generating a new state. If the state already
	 * exists, a cycle is created in the graph. It checks ALL previous nodes of
	 * the state's graph.
	 * 
	 * @param redex
	 *            BSGNode to whom the rule is applied.
	 * @param rewritingRule
	 *            Name of the rewriting rule. The name <u>must</u> be used
	 *            consistently for the graph to recognise cycles, i.e. the same
	 *            name must be <b>always</b> used for the same rewriting rule.
	 * @param reactum
	 *            Bigraph resulting from the application of the rewriting rule.
	 * @param prevNodeQueue
	 *            List of all previous nodes of the state's graph.
	 * 
	 * @return The new state reached (as a BSGNode) or null if there's  duplicate.
	 */
	public BSGNode applyRewritingRule(BSGNode redex, String rewritingRule, Bigraph reactum) {
		for (BSGNode previous : nodes) {
			BSGNode dupNode = findDuplicate(reactum, previous);
			if (dupNode != null) {// Found a duplicate
				redex.addLink(dupNode, rewritingRule);
				return null;
			}
		}
		// Generate a new state, build links
		BSGNode newNode = new BSGNode(reactum, hashFunc);
		nodes.add(newNode);
		redex.addLink(newNode, rewritingRule);
		
		return newNode;
	}
	
	
	
	/**
	 * 
	 * @param subject bigraph on which we want to verify the isomorphism
	 * @param object BSGNode that we test
	 * @return null if there are no duplicates or, if there are, the duplicate
	 * 			as a BSGNode.
	 */
	private BSGNode findDuplicate(Bigraph subject, BSGNode object) {
		 // Use the hash table to find possible duplicates
        int redexHash = object.getHashCode();
        int reactumHash = PLACELINK_HASH.bigHash(subject);
        if ( redexHash != reactumHash ) {
        	// No duplicates detected
            return null;
        } else {
        	// Hash collision suggests possible duplicate (or isomorphism)
            // Check isomorphism
        	if (matcher.areIsomorph(object.getState(), subject)) {
            	return object;
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

	
	
	public int getGraphSize() {
		return nodes.size();
	}
	
	public List<BSGNode> getNodes(){
		return nodes;
	}
	
}
