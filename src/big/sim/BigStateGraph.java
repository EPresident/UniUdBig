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
import big.sim.BSGNode.BSGLink;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Matcher;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
     * Pseudo-hash function used to determine if two states (bigraphs) are
     * similar enough to warrant a full equality check.
     */
    private BigHashFunction hashFunc;
    public static final BigHashFunction PLACE_HASH = new PlaceGraphBHF(),
            PLACELINK_HASH = new PlaceLinkBHF();

    public BigStateGraph(Bigraph big, BigHashFunction bhf) {
        hashFunc = bhf;
        root = new BSGNode(big, hashFunc);
        nodes = new LinkedList<>();
        nodes.add(root);
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
            redex.addLink(newNode, rewritingRule);
            newNode.addLink(redex, rewritingRule);
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
     * equality test is made by using a Matcher (see comments below).
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
        // Search adjacent states in the graph
        // TODO A more intelligent search might be required to find all cycles
        for (BSGLink link : redex.getLinks()) {
            //if (link.rewRule.equals(rewritingRule)) {
            // Check if hash matches
            if (link.destNode.getHashCode() == hashFunc.bigHash(reactum)) {
                // Hash matches, make a more detailed check
                /*
                  A Matcher is used to match the reactum to the state currently
                  analyzed. If they are indeed equal, the Matcher will return a
                  Match with no parameters.
                */
                Matcher matcher = new Matcher();
                Iterator<? extends Match> it = matcher.match(link.destNode.getState(), reactum).iterator();
                if (it.hasNext()) {
                    Match match = it.next();
                    if (match.getParam().isEmpty()) {
                        // Duplicate found!
                        // FIXME System.out.println("Duplicate found!");
                        return link.destNode;
                    }
                }
            }
            // }
        }
        // No duplicates detected
        return null;
    }

}
