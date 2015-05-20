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
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.util.ESat;

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
     * rule to the current state.
     *
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     */
    public void applyRewritingRule(String rewritingRule, Bigraph reactum) {
        current = applyRewritingRule(current, rewritingRule, reactum);
    }

    /**
     * Internal method that applies a rewriting rule, generating a new state. If
     * the state already exists, a cycle is created in the graph.
     *
     * @param redex BSGNode to whom the rule is applied.
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     * @return The new current node.
     */
    private BSGNode applyRewritingRule(BSGNode redex, String rewritingRule, Bigraph reactum) {
        // Find duplicate node (if present)
        BSGNode dup = findDuplicate(redex, rewritingRule, reactum);
        if (dup == null) {
            // Generate a new state, build links
            BSGNode newNode = new BSGNode(reactum, hashFunc);
            nodes.add(newNode);
            hashTable.put(newNode.getHashCode(), newNode);
            redex.addLink(newNode, rewritingRule);
            return newNode;
        } else {
            // Create a cycle
            redex.addLink(dup, rewritingRule);
            return dup;
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
    private BSGNode findDuplicate(BSGNode redex, String rewritingRule, Bigraph reactum) {
        // Use the hash table to find possible duplicates
        /*
         The "Matcher" equality check has been removed while the
         isomorphism check is being implemented in the library.
         */
        BSGNode dup = hashTable.get(redex.getHashCode());
        if (dup == null) {
            // No duplicates detected
            return null;
        } else {
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
     * @param a First Bigraph.
     * @param b Second Bigraph.
     * @return <i>true</i> if the Bigraphs are isomorph, <i>false</i> otherwise.
     */
    protected static boolean areIsomorph(Bigraph a, Bigraph b) {
        Solver chocoSolver = new Solver("Link graph isomorphism");
        // Link graph
        //<editor-fold desc="Creazione lista handle e point">
        ArrayList<Handle> handlesA = new ArrayList<>();
        for (Handle h : a.getOuterNames()) {
            handlesA.add(h);
        }
        for (Handle h : a.getEdges()) {
            handlesA.add(h);
        }
        ArrayList<Handle> handlesB = new ArrayList<>();
        for (Handle h : b.getOuterNames()) {
            handlesB.add(h);
        }
        for (Handle h : b.getEdges()) {
            handlesB.add(h);
        }
        ArrayList<Point> pointsA = new ArrayList<>();
        for (Point p : a.getInnerNames()) {
            pointsA.add(p);
        }
        for (Node n : a.getNodes()) {
            for (Point p : n.getPorts()) {
                pointsA.add(p);
            }
        }
        ArrayList<Point> pointsB = new ArrayList<>();
        for (Point p : b.getInnerNames()) {
            pointsB.add(p);
        }
        for (Node n : b.getNodes()) {
            for (Point p : n.getPorts()) {
                pointsB.add(p);
            }
        }
        int nPtsA = pointsA.size(), nPtsB = pointsB.size(),
                nHdlsA = handlesA.size(), nHdlsB = handlesB.size();
        //</editor-fold>
        
        if (nPtsA != nPtsB || nHdlsA != nHdlsB) {
            // Mismatching point/handle cardinality
            return false;
        }
        BoolVar[][] ptsVars = VF.boolMatrix("ptsVars", nPtsA, nPtsA, chocoSolver);
        BoolVar[][] hdlsVars = VF.boolMatrix("hdlsVars", nHdlsA, nHdlsA, chocoSolver);
        IntVar one = VF.fixed(1, chocoSolver);

        for (BoolVar[] bs : ptsVars) {
            chocoSolver.post(ICF.sum(bs, one));
        }
        for (BoolVar[] bs : hdlsVars) {
            chocoSolver.post(ICF.sum(bs, one));
        }

        return chocoSolver.findSolution();
    }
}
