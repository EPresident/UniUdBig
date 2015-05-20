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
import it.uniud.mads.jlibbig.core.std.Bigraph;
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
    private Bigraph state;
    private List<BSGLink> links;
    /**
     * Hash code is pre-computed and stored here.
     */
    private int hashCode;
    
    protected BSGNode(Bigraph big, BigHashFunction bhf){
        state=big;
        links=new LinkedList<>();
        hashCode=bhf.bigHash(big);
    }
    
    /**
     * Link this node to another one in the graph, storing the name of the 
     * rewriting rule that caused the transition.
     * @param reactum The BSGNode reached by this link.
     * @param rr Name of the rewriting rule applied. The name <u>must</u> be
     * used consistently for the graph to recognise cycles, i.e. the same name
     * must be <b>always</b> used for the same rewriting rule.
     * @param reactum Bigraph resulting from the application of the rewriting
     * rule.
     */
    protected void addLink(BSGNode reactum, String rr){
        links.add(new BSGLink(reactum,rr));
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
    protected class BSGLink{
        protected BSGNode destNode;
        protected String rewRule;
        
        protected BSGLink(BSGNode bsgn, String rr){
            destNode=bsgn;
            rewRule=rr;
        }
    }
}
