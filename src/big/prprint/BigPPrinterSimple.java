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
package big.prprint;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Edge;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 * Pretty Printer module for Bigraphs ( using JLibBig )
 * Prints the bigraph by indenting the nodes, which are printed
 * using their respective default toString method.
 * Can be operated through the <i>prettyPrint</i> static method.
 * 
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigPPrinterSimple {

    public static String prettyPrint(Bigraph big) {
        return prettyPrint(big, "unnamed bigraph");
    }

    public static String prettyPrint(Bigraph big, String bigName) {
        StringBuilder pprt = new StringBuilder();
        // Start of print
        pprt.append("----- Printing ").append(bigName).append(" -----\nPlace Graph:\n");

        for (Root r : big.getRoots()) {
            pprt.append("> ").append(r).append("\n");
            for (Child c : r.getChildren()) {
                printTree(c, pprt, 2);
            }
        }
        
        pprt.append("\nOuterNames: \n");
        for(OuterName o : big.getOuterNames()){
            pprt.append(o).append(":").append(o.getPoints()).append("\n");
        }        
        pprt.append("\nEdges: \n");        
        for(Edge e: big.getEdges()){
            pprt.append(e).append(":").append(e.getPoints()).append("\n");
        }

        // End of print
        pprt.append("----- Done printing ").append(bigName).append(" -----\n");
        return pprt.toString();
    }

    private static void printTree(Child c, StringBuilder pprt, int indent) {
        Node n = (Node) c;
        // Pretty print current node
        prIndent(indent, pprt);
        pprt.append(n).append(" {");
        if (!n.getPorts().isEmpty()) {
            pprt.append(" Ports: ").append(n.getPorts()).append(";");
        }
        if (n.getProperties().size() > 1) {
            pprt.append(" Properties: ");
            for (Property p : n.getProperties()) {
                if (!p.getName().equals("Owner")) {
                    pprt.append(p);
                }
            }
            pprt.append(";");
        }

        pprt.append("} \n");
        // Pretty print children
        for (Child cc : n.getChildren()) {
            printTree(cc, pprt, indent + 1);
        }
    }

    private static void prIndent(int indent, StringBuilder pprt) {
        for (int i = 0; i < indent; i++) {
            pprt.append("  ");
        }
    }
    
}
