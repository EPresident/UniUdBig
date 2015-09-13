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
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Edge;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 * Hash function that computes the place and link graph into an integer.
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class PlaceLinkBHF implements BigHashFunction {

    // Override interface method
    @Override
    public int bigHash(Bigraph big) {
        /*
            For each node in the place graph, 1 is added to the hash code, plus
            1 for each nesting level; in other words each child has the value 
            of the parent + 1.
            For each Point in each Edge and OuterName, the hash code is 
            increased by one.
        */
        int hash = 0;
        for (Root r : big.getRoots()) {
            hash += 1;
            for (Child c : r.getChildren()) {
                hash += 2 + hashRecursive(c, 3);
            }
        }

        for(Edge e: big.getEdges()){
            int val=1;
            hash+=1;
            for(Point p: e.getPoints()){
                hash+=val++;
            }
        }
        
        for(OuterName o: big.getOuterNames()){
            int val=1;
            hash+=1;
            for(Point p: o.getPoints()){
                hash+=val++;
            }
        }
        
        return hash;
    }

    /**
     * Recursive function that returns the number to be added to the hash code.
     * @param value The value of the current level of nodes. It increases with
     * the nesting level, i.e. each child has the value of the parent + 1.
     */
    private int hashRecursive(Child c, int value) {
        Node n = (Node) c;
        int ret=0;
        for(Child ch:n.getChildren()){
            ret+=value+hashRecursive(ch, value+1);
        }
        return ret;
    }
}
