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
package big.bsg;

import it.uniud.mads.jlibbig.core.std.Bigraph;

import java.util.LinkedList;
import java.util.List;

import big.bsg.BSGNode.BSGLink;
import big.iso.Isomorphism;
import big.match.PropertyMatcher;

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
    private Isomorphism iso;

    public BigStateGraph(Bigraph big, BigHashFunction bhf, Isomorphism isomorphism) {
        hashFunc = bhf;
        root = new BSGNode(big, hashFunc);
        nodes = new LinkedList<>();
        nodes.add(root);
        current = root;
        this.iso = isomorphism;
    }
    
    public BigStateGraph(Bigraph big, Isomorphism isomorphism ) {
        this(big, PLACELINK_HASH, isomorphism);
    }
    
    public BigStateGraph(Bigraph big) {
        this(big, PLACELINK_HASH, new Isomorphism());
    }

    /**
     * Applies a rewriting rule, generating a new state. If the state already
     * exists, a cycle is created in the graph. It checks ALL previous nodes of
     * the state's graph.
     *
     * @param redex BSGNode to whom the rule is applied.
     * @param rewritingRule Name of the rewriting rule. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     *
     * @return The new state reached (as a BSGNode) or null if there's
     * duplicate.
     */
    public BSGNode applyRewritingRule(BSGNode redex, String rewritingRule, Bigraph reactum) {
        for (BSGNode previous : nodes) {
            BSGNode dupNode = findDuplicate(reactum, previous);
            if (dupNode != null) {// Found a duplicate
                //redex.addLink(dupNode, rewritingRule);
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
     * as a BSGNode.
     */
    private BSGNode findDuplicate(Bigraph subject, BSGNode object) {
        // Use the hash table to find possible duplicates
        int redexHash = object.getHashCode();
        int reactumHash = PLACELINK_HASH.bigHash(subject);
        if (redexHash != reactumHash) {
            // No duplicates detected
            return null;
        } else {
            // Hash collision suggests possible duplicate (or isomorphism)
            // Check isomorphism
            if (iso.areIsomorph(object.getState(), subject)) {
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

    public List<BSGNode> getNodes() {
        return nodes;
    }

    /**
     * @deprecated
     * Use DotLangPrinter instead.
     * @return the Dot Language representation of this graph.
     */
    public String toDotLang() {
        LinkedList<BSGNode> queue = new LinkedList<>();
        queue.add(root);
        StringBuilder sb = new StringBuilder("digraph stateGraph{\n\n");
        while (!queue.isEmpty()) {
            BSGNode curr = queue.pop();
            for (BSGLink l : curr.getLinks()) {
                queue.addLast(l.destNode);
                sb.append(curr.getHashCode()).append("->")
                        .append(l.destNode.getHashCode()).append("[label=")
                        .append(l.rewRule).append("];\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

}
