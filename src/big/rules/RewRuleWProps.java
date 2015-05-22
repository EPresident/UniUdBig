/*
 * Copyright (C) 2015 Elia Calligaris <calligaris.elia@spes.uniud.it> 
 * and Luca Geatti <geatti.luca@spes.uniud.it>
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
package big.rules;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class allows to instantiate RewritingRules that propagate the properties
 * attached to the redex when applied.
 *
 * <b>Warning</b>: this class may become deprecated if a better way to propagate
 * properties is found.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public abstract class RewRuleWProps extends RewritingRule {

    protected Bigraph bigraph;
    protected Map<String, Node[]> rr;//Link from reactum node to redex nodes.

    public RewRuleWProps(Bigraph redex, Bigraph reactum, InstantiationMap map) {
        super(redex, reactum, map);
        rr = new HashMap<>();
        createAssociations();
    }

    @Override
    public Iterable<Bigraph> apply(Bigraph to) {
        this.bigraph = to;
        Iterable<Bigraph> bgl = new RRWPIterable(super.apply(to));
        return bgl;
    }

    @Override
    public abstract void instantiateReactumNode(Node original, Node instance, Match match);

    protected void copyProperties(Node from, Node to) {
        for (Property p : from.getProperties()) {
            if (!p.getName().equals("Owner") && !p.getName().equals("NodeType")) {
                to.attachProperty(p);
            }
        }
    }

    protected final void createAssociations() {

        for (Node n1 : getReactum().getNodes()) {
            for (Property p1 : n1.getProperties()) {
                if (!p1.getName().equals("Owner")) {
                    Node[] array = new Node[2];
                    array[0] = n1;
                    rr.put(p1.get().toString(), array);
                }
            }
        }

        for (Node n2 : getRedex().getNodes()) {
            for (Property p2 : n2.getProperties()) {
                if (!p2.getName().equals("Owner")) {
                    Node[] array = rr.get(p2.get());
                    if (array != null) {
                        array[1] = n2;
                        rr.put(p2.get().toString(), array);
                    }
                }
            }
        }

    }

    public void clearAuxProperties(Bigraph bg) {
    	//Deletes auxiliary properties, such as NodeType and PacketType.
        boolean pass = false;
        for (Node n : bg.getNodes()) {
            CopyOnWriteArrayList<Property> cow = new CopyOnWriteArrayList<Property>(n.getProperties());
            Property[] a = new Property[0];
            Property[] ap = cow.toArray(a);
            for (int i = 0; i < ap.length; i++) {
                String name = ap[i].getName();
                if (!name.equals("Owner")) {
                	for (String str : getAuxProperties()) {
                		if (name.equals(str)) {
                        	pass = true;
                        }
                    }
                    if (pass) {
                    	n.detachProperty(ap[i]);
                    }
                    pass = false;
                }
            }
        }
    }
    
    protected abstract List<String> getAuxProperties();

    protected String getOuterNameImage(String name, Match match, int nPort) {
        String str = "";
        Node redexN = rr.get(name)[1];
        if (redexN != null) {
            Node imgNode = match.getImage(redexN);
            if (imgNode != null) {
                Port port = imgNode.getPort(nPort);
                if (port != null) {
                    Handle handle = port.getHandle();
                    if (handle != null && handle.isOuterName()) {
                        OuterName outer = (OuterName) handle;
                        str += outer.getName();
                    }
                }
            }
        }
        return str;
    }

    public boolean isApplicable(Bigraph bigraph) {
        // Defaulted
        return true;
    }
    
    public String getName(){
        return this.getClass().getSimpleName();
    }

    /**
     * Wrapper for the Iterable provided by RewritingRule.apply(to)
     */
    private class RRWPIterable implements Iterable<Bigraph> {

        private final Iterable<Bigraph> iterable;

        RRWPIterable(Iterable<Bigraph> it) {
            iterable = it;
        }

        @Override
        public Iterator<Bigraph> iterator() {
            return new RRWPIterator(iterable.iterator());
        }

        /**
         * Iterator wrapper used to lazily clear aux properties.
         */
        private class RRWPIterator implements Iterator<Bigraph> {

            private final Iterator<Bigraph> iterator;

            public RRWPIterator(Iterator<Bigraph> it) {
                iterator = it;
            }

            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public Bigraph next() {
                Bigraph big = iterator.next();
                clearAuxProperties(big);
                return big;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("remove() not supported.");
            }

        }

    }
}
