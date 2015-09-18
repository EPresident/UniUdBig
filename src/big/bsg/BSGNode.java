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
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.LinkedList;
import java.util.List;

/**
 * BigStateGraph Node. Each node stores a state (bigraph) and links to other
 * nodes, plus the hash of the state.
 * @see BigStateGraph
 * 
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BSGNode {
    private final Bigraph state;
    private final List<BSGLink> links;
    /**
     * Hash code is pre-computed and stored here.
     */
    private final int hashCode;
    
    protected BSGNode(Bigraph big, int hash){
        state=big;
        links=new LinkedList<>();
        hashCode=hash;
    }
    
    /**
     * Link this node to another one in the graph, storing the name of the 
     * rewriting rule that caused the transition.
     * @param reactum The BSGNode reached by this link.
     * @param rr Name of the rewriting rule applied. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param backwards True if this links forms a cycle in the graph.
     */
    protected void addLink(BSGNode reactum, RewritingRule rr, boolean backwards){
        links.add(new BSGLink(reactum,rr, backwards));
    }

    public List<BSGLink> getLinks() {
        return links;
    }

    public Bigraph getState() {
        return state;
    }

    public int getHashCode() {
        return hashCode;
    }
        
    /**
     * Encapsulates a link as a (Destination Node, Rule Applied) couple.
     */
    public class BSGLink{
        private final BSGNode destNode;
        private final RewritingRule rewRule;
        private final boolean backwards;
        
        public BSGLink(BSGNode bsgn, RewritingRule rr, boolean back){
            destNode=bsgn;
            rewRule=rr;
            backwards = back;
        }

        public BSGNode getDestNode() {
            return destNode;
        }

        public RewritingRule getRewRule() {
            return rewRule;
        }

        public boolean isBackwards() {
            return backwards;
        }
                
    }
}
