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
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Represents a graph where each node is a different possible state of the
 * starting Bigraph. The state is modified through the application of the
 * rewriting rules.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigStateGraph {

    /**
     * Nodes of the graph.
     */
    private final HashMap<Integer, LinkedList<BSGNode>> nodes;
    private final BSGNode root;
    /**
     * Last node added to the graph.
     */
    private BSGNode lastAdded;
    /**
     * Hash function used to determine if two states (bigraphs) are similar
     * enough to warrant a full equality check.
     */
    private BigHashFunction hashFunc;
    public static final BigHashFunction PLACE_HASH = new PlaceGraphBHF(),
            PLACELINK_HASH = new PlaceLinkBHF(), CARD_HASH = new CardinalityBHF();
    private final Isomorphism iso;
    /**
     * Tells if the graph is acyclic or not.
     */
    private final boolean acyclic;
   // private int collisions = 0;

    public BigStateGraph(Bigraph big, boolean acyclic, BigHashFunction bhf,
            Isomorphism isomorphism, int hashCapacity) {
        hashFunc = bhf;
        root = new BSGNode(big, hashFunc.bigHash(big));
        /*nodes = new HashMap<>(hashCapacity);
         nodes.put(root.getHashCode(), root);*/
        nodes = new HashMap<>();
        LinkedList<BSGNode> bucket = new LinkedList<>();
        bucket.add(root);
        nodes.put(root.getHashCode(), bucket);
        lastAdded = root;
        this.iso = isomorphism;
        this.acyclic = acyclic;
    }

    public BigStateGraph(Bigraph big, Isomorphism isomorphism) {
        this(big, true, PLACELINK_HASH, isomorphism, 100);
    }

    public BigStateGraph(Bigraph big, boolean acyclic) {
        this(big, acyclic, PLACELINK_HASH, new Isomorphism(), 100);
    }

    public BigStateGraph(Bigraph big) {
        this(big, false, PLACELINK_HASH, new Isomorphism(), 100);
    }

    /**
     * Applies a rewriting rule, generating a new state. If the state already
     * exists and the graph is cyclic, a cycle link will be added.
     *
     * @param redex BSGNode to whom the rule is applied.
     * @param rewRule The rewriting rule used.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     *
     * @return The new state reached (as a BSGNode) or null if there's
     * duplicate.
     */
    public BSGNode applyRewritingRule(BSGNode redex, RewritingRule rewRule, Bigraph reactum) {
        int hash = hashFunc.bigHash(reactum);
        BSGNode newNode = new BSGNode(reactum, hash);
        if (nodes.containsKey(hash)) {
            // Possible duplicate found
            LinkedList<BSGNode> bucket = nodes.get(hash);
            // Check isomorphism
            for (BSGNode bsgn : bucket) {
                if (iso.areIsomorph(newNode.getState(), bsgn.getState())) {
                    if (!acyclic) {
                        // Cycle link
                        redex.addLink(bsgn, rewRule, true);
                    }
                    return null;
                }
            }
            // Hash collision but no isomorphism
            bucket.add(newNode);
            redex.addLink(newNode, rewRule, false);
            return newNode;
        } else {
            LinkedList<BSGNode> bucket = new LinkedList<>();
            bucket.add(newNode);
            nodes.put(hash, bucket);
            redex.addLink(newNode, rewRule, false);
            return newNode;
        }

    }

    public BSGNode getRoot() {
        return root;
    }

    public boolean isAcyclic() {
        return acyclic;
    }

    /**
     * Returns the last node a rule has been applied to.
     *
     * @return The last node a rule has been applied to, or the root.
     */
    public BSGNode getLastNodeUsed() {
        return lastAdded;
    }

    public int getGraphSize() {
        return nodes.size();
    }

    public List<BSGNode> getNodes() {
        LinkedList<BSGNode> llist = new LinkedList<>();
        for (LinkedList<BSGNode> ns : nodes.values()) {
            if (ns != null) {
                llist.addAll(ns);
            }
        }
        return llist;
    }

    /**
     * @return the Dot Language representation of this graph.
     */
    public String toDotLang() {
        LinkedList<BSGNode> queue = new LinkedList<>();
        queue.add(root);
        StringBuilder sb = new StringBuilder("digraph stateGraph{\n\n");
        while (!queue.isEmpty()) {
            BSGNode curr = queue.pop();
            for (BSGLink l : curr.getLinks()) {
                if (!l.isBackwards()) {
                    queue.addLast(l.getDestNode());
                }
                sb.append(curr.getHashCode()).append("->")
                        .append(l.getDestNode().getHashCode()).append("[label=")
                        .append(l.getRewRule().getClass().getSimpleName()).append("];\n");
            }
        }
        sb.append("\n}");
        return sb.toString();
    }

}
