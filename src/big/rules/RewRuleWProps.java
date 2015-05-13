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
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * This class allows to instantiate RewritingRules that propagate the properties
 * attached to the redex when applied.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public abstract class RewRuleWProps extends RewritingRule {

    protected Bigraph bigraph;
    protected Bigraph redex;
    protected Bigraph reactum;
    protected Map<String, Node[]> rr;//Link from reactum node to redex nodes.
    protected static LinkedList<String> auxProperties;

    public RewRuleWProps(Bigraph redex, Bigraph reactum, InstantiationMap map) {
        super(redex, reactum, map);
        this.redex = redex;
        this.reactum = reactum;
        rr = new HashMap<>();
        createAssociations();
        auxProperties = new LinkedList<>();
        auxProperties.add("NodeType");
        auxProperties.add("PacketType");
    }

    @Override
    public Iterable<Bigraph> apply(Bigraph b) {
        this.bigraph = b;
        Iterable<Bigraph> bgl = super.apply(b);

        return bgl;
    }

    @Override
    public void instantiateReactumNode(Node original, Node instance, Match match) {
        for (Property p : original.getProperties()) {//Original = node of the reactum
            Node[] array = rr.get(p.get());
            if (array != null) {
                Node n = array[1]; //Node of the redex
                if (n != null) {
                    Node img = match.getImage(n);//Node of the original bigraph
                    if (img != null) {
                        copyProperties(img, instance);
                    }
                }
            }
        }

    }

    public abstract Bigraph getRedex(Signature signature);

    public abstract Bigraph getReactum(Signature signature);

    protected void copyProperties(Node from, Node to) {
        for (Property p : from.getProperties()) {
            if (!p.getName().equals("Owner") && !p.getName().equals("NodeType")) {
                to.attachProperty(p);
            }
        }
    }

    protected final void createAssociations() {

        for (Node n1 : this.reactum.getNodes()) {
            for (Property p1 : n1.getProperties()) {
                if (!p1.getName().equals("Owner")) {
                    Node[] array = new Node[2];
                    array[0] = n1;
                    rr.put(p1.get().toString(), array);
                }
            }
        }

        for (Node n2 : this.redex.getNodes()) {
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

    public static void clearAuxProperties(Bigraph bg) {
        //Deletes auxiliary properties, such as NodeType and PacketType.
        boolean pass = false;
        for (Node n : bg.getNodes()) {
            CopyOnWriteArrayList<Property> cow = new CopyOnWriteArrayList<Property>(n.getProperties());
            Property[] a = new Property[0];
            Property[] ap = cow.toArray(a);
            for (int i = 0; i < ap.length; i++) {
                String name = ap[i].getName();
                if (!name.equals("Owner")) {
                    for (String str : auxProperties) {
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
}
