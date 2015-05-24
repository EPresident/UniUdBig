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
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Root;
import java.util.ArrayList;
import java.util.HashMap;
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
     */
    protected static boolean areIsomorph(Bigraph a, Bigraph b) {
        Solver chocoSolver1 = new Solver("Link graph isomorphism"),
                chocoSolver2 = new Solver("Place graph isomorphism");
        // Link graph
        //<editor-fold desc="Link graph isomorphism">
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
        // Flow

        int[] flowHdlsA = new int[nHdlsA];
        for (int i = 0; i < nHdlsA; i++) {
            flowHdlsA[i] = handlesA.get(i).getPoints().size();
        }
        int[] flowHdlsB = new int[nHdlsB];
        for (int i = 0; i < nHdlsB; i++) {
            flowHdlsB[i] = handlesB.get(i).getPoints().size();
        }
        // Rows
        BoolVar[][] ptsVarsR = VF.boolMatrix("ptsVars", nPtsA, nPtsA, chocoSolver1);
        BoolVar[][] hdlsVarsR = VF.boolMatrix("hdlsVars", nHdlsA, nHdlsA, chocoSolver1);
        // Columns
        BoolVar[][] ptsVarsC = new BoolVar[nPtsA][nPtsA];
        BoolVar[][] hdlsVarsC = new BoolVar[nHdlsA][nHdlsA];
        IntVar one = VF.fixed(1, chocoSolver1);

        for (int i = 0; i < nPtsA; i++) {
            for (int j = 0; j < nPtsA; j++) {
                ptsVarsC[j][i] = ptsVarsR[i][j];
            }
        }
        for (int i = 0; i < nHdlsA; i++) {
            for (int j = 0; j < nHdlsA; j++) {
                hdlsVarsC[j][i] = hdlsVarsR[i][j];
            }
        }
        for (BoolVar[] bs : ptsVarsR) {
            chocoSolver1.post(ICF.sum(bs, one));
        }
        for (int i = 0; i < nHdlsA; i++) {
            chocoSolver1.post(ICF.sum(hdlsVarsR[i], one));
            chocoSolver1.post(ICF.scalar(hdlsVarsR[i], flowHdlsA, VF.fixed(flowHdlsB[i], chocoSolver1)));
        }
        for (BoolVar[] bs : ptsVarsC) {
            chocoSolver1.post(ICF.sum(bs, one));
        }
        for (int i = 0; i < nHdlsA; i++) {
            chocoSolver1.post(ICF.sum(hdlsVarsC[i], one));
            chocoSolver1.post(ICF.scalar(hdlsVarsR[i], flowHdlsB, VF.fixed(flowHdlsA[i], chocoSolver1)));
        }
        //</editor-fold>

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
                nPlcEntA, chocoSolver2);
        BoolVar[][] placeEntVarsC = new BoolVar[nPlcEntA][nPlcEntA];
        IntVar one2 = VF.fixed(1, chocoSolver2);

        for (int i = 0; i < nPlcEntA; i++) {
            for (int j = 0; j < nPlcEntA; j++) {
                placeEntVarsC[j][i] = placeEntVarsR[i][j];
            }
        }

        // Constraints
        for (int i = 0; i < nPlcEntA; i++) {
            chocoSolver2.post(ICF.sum(placeEntVarsR[i], one2));
            chocoSolver2.post(ICF.scalar(placeEntVarsR[i], flowB, VF.fixed(flowA[i], chocoSolver2)));
        }
        for (int i = 0; i < nPlcEntA; i++) {
            chocoSolver2.post(ICF.sum(placeEntVarsC[i], one2));
            chocoSolver2.post(ICF.scalar(placeEntVarsC[i], flowA, VF.fixed(flowB[i], chocoSolver2)));
        }
        //</editor-fold>

        return chocoSolver1.findSolution() && chocoSolver2.findSolution();
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
