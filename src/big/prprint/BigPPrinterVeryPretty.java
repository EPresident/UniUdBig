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
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.Root;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.regex.Pattern;

/**
 * The Prettiest PrettyPrinter for JLibBig bigraphs EVER. This pretty-printer
 * gathers information about the bigraph and then builds its own data-structure,
 * a PrintTree: a PrintTree is an n-ary tree where every node represents a node
 * in the bigraph, except for the root (of the tree), which is a "fake node"
 * with no meaning.
 *
 * BigPPrinterVeryPretty needs to be instantiated before usage. Pretty printing
 * is achieved by invoking the prettyPrint method.
 *
 * When pretty-printing, each Node of the Bigraph has a name assigned to itself.
 * By default that name is the ID-code provided by the JLibBig representation.
 * <b>If a Node has a "Name" Property, that name is used for that Node</b> when
 * pretty-printing (format: Name[ID]).
 *
 * DONE: printing node hierarchy, node properties. TODO: printing edge and
 * outer/inner-names info.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigPPrinterVeryPretty {

    private Bigraph pprtBig;

    /**
     * Produce a readable text-based representation of the given Bigraph. The
     * name givend to the Bigraph when prety printing is defaulted as "unnamed
     * bigraph"
     *
     * @param big a JLibBig Bigraph
     * @return A (more) readable representation of the Bigraph.
     */
    public String prettyPrint(Bigraph big) {
        return prettyPrint(big, "unnamed bigraph");
    }

    /**
     * Produce a readable text-based representation of the given Bigraph.
     *
     * @param big a JLibBig Bigraph
     * @param bigName name assigned to the Bigraph when pretty-printing ( the
     * Bigraph instance isn't modified )
     * @return A (more) readable representation of the Bigraph.
     */
    public String prettyPrint(Bigraph big, String bigName) {
        pprtBig = big;
        // StringBuilder is more efficient than String concatenation!
        StringBuilder pprt = new StringBuilder();
        // Initialize PrintTree (support data-structure)
        PrintTree pT = new PrintTree();
        // Analyze node hierarchy (place graph)
        int rid = 0;  // number assigned to the roots
        for (Root r : big.getRoots()) {
            // Populate the PrintTree
            TreeNode bigRoot = new TreeNode("root", Integer.toString(rid), 0);
            pT.addNode(pT.root, bigRoot);
            for (Child c : r.getChildren()) {
                // Iterate on each child
                buildTree(pT, bigRoot, c, 1);
            }
            rid++;
        }

        buildLinks(pT);

        // Print the PrintTree
        pprt.append("----- Printing Bigraph ").append(bigName).append(" -----\n");
        pprt.append(pT.toString());
        pprt.append("----- Done Printing ").append(bigName).append(" ------\n");
        return pprt.toString();
    }

    /**
     * Populate the PrintTree by analysing recurively the objects provided by
     * JLibBig.
     *
     * @param pT PrintTree instance in use.
     * @param parent Parent (in the PrintTree) of the (bigraph) node currently
     * analyzed.
     * @param c The (bigraph) node currently analyzed.
     * @param indent Length of indentation used when pretty printing ( can also
     * be thought as the height of the node in the PrintTree )
     */
    private void buildTree(PrintTree pT, TreeNode parent, Child c, int indent) {
        // Cast the Child instance into a Node to access more methods
        Node n = (Node) c;
        // Populate the PrintTree
        TreeNode bigNode = new TreeNode("node", getName(n), indent);
        buildNode(bigNode, n);
        pT.addNode(parent, bigNode);
        // Call recursively for each child
        for (Child cc : n.getChildren()) {
            buildTree(pT, bigNode, cc, indent + 1);
        }
    }

    /**
     * Add additional attributes to a TreeNode ( properties, ports, etc... ),
     * which are not handled by the class constructor.
     *
     * @param tn TreeNode instance to work on.
     * @param n Bigraph Node instance to analyze.
     */
    private void buildNode(TreeNode tn, Node n) {
        tn.id = getId(n); // add Id

        // Scan and add properties
        LinkedList<String> portNames = new LinkedList<>();
        Pattern pPortName = Pattern.compile("Port\\dName");
        for (Property p : n.getProperties()) {
            if (pPortName.matcher(p.getName()).matches()) {
                // Port name special property detected, store the value
                portNames.add(p.get().toString());
            } else if (!(p.getName().equals("Owner") || p.getName().equals("Name"))) {
                /* Skip Owner and Name property:
                 *  - Owner is too lengthy to print, and it's redundant;
                 *  - Name is already used to name the TreeNodes
                 */
                tn.addAttribute(p.getName(), p.get().toString());
            }
        }

        // Add ports to the PrintTree
        Iterator<String> it = portNames.iterator();
        for (Port p : n.getPorts()) {
         //   if (it.hasNext()) {
            //  tn.addAttribute(it.next(), "");
            //  } else {
            tn.addAttribute("Port", "");
            //   }
        }

    }

    /**
     * Scans Edges and Outer/Inner-names to find links between Ports
     *
     * @param pT PrintTree built during the Bigraph analysis
     */
    private void buildLinks(PrintTree pT) {
        // Scan edges
        for (Edge e : pprtBig.getEdges()) {
            for (Point p : e.getPoints()) {
                // Point.toString() output: portNumber@nodeID:controlType
                String[] portId = p.toString().split(":")[0].split("@");
                int port = Integer.parseInt(portId[0]);
                String id = portId[1];

                StringBuilder sb = new StringBuilder();
                for (Point pt : e.getPoints()) {
                    // Point.toString() output: portNumber@nodeID:controlType
                    String poI = pt.toString().split(":")[0];
                    if (!poI.split("@")[1].equals(id)) {
                        sb.append(poI).append(",");
                    }
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }

                TreeNode tn = pT.findNodeByID(id);
                tn.linkPort(port, sb.toString());

            }
        }

        // scan outernames
        for (OuterName o : pprtBig.getOuterNames()) {
            for (Point p : o.getPoints()) {
                // Point.toString() output: portNumber@nodeID:controlType
                String[] portId = p.toString().split(":")[0].split("@");
                int port = Integer.parseInt(portId[0]);
                String id = portId[1];
                TreeNode tn = pT.findNodeByID(id);
                tn.linkPort(port, o.getName());
                //System.out.println("link "+port+"@"+tn.toString()+" to "+o.getName());
            }
        }
    }

    /**
     * Append whitespaces (to a StringBuilder) to indent the text.
     *
     * @param indent Level of indentation (number of double-blanks to print).
     * @param pprt StringBuilder to use.
     */
    private void prIndent(int indent, StringBuilder pprt) {
        for (int i = 0; i < indent; i++) {
            pprt.append("   ");
        }
    }

    /**
     * Finds the name to assign to a new TreeNode, by analyzing a Bigraph Node.
     *
     * @param n The Bigraph Node to analyze.
     * @return If the Bigraph Node has the "Name" Property, returns
     * <i>Name[ID]</i>; else it only returns the ID provided by JLibBig.
     */
    private String getName(Node n) {
        // Node.toString() output is      ID:ControlType
        StringBuilder sb = new StringBuilder();
        String id = getId(n);

        if (n.getProperty("Name") != null) {
            sb.append(n.getProperty("Name").get());
            sb.append("[").append(id).append("]");
        } else {
            sb.append(id);
        }
        return sb.toString();
    }

    /**
     * Returns the ID used by JLibBig for Nodes
     *
     * @param n The Node to work on
     * @return The ID used by JLibBig for Node n
     */
    private String getId(Node n) {
        return n.toString().split(":")[0];
    }

    /**
     * Auxiliary data-structure used in pretty printing. It's an n-ary tree
     * where all nodes, except the root, represent Bigraph Roots and Nodes.
     * PrintTree.toString() returns the pretty-printed Bigraph.
     */
    private class PrintTree {

        private static final String FRN = "PrintTree fake root";
        private TreeNode root;

        public PrintTree() {
            root = new TreeNode(FRN, FRN, 0);
        }

        @Override
        public String toString() {
            return root.printTree();
        }

        /**
         * Add a TreeNode to the PrintTree.
         *
         * @param parent Parent of the new TreeNode in the tree.
         * @param child The TreeNode to add to the tree.
         */
        protected void addNode(TreeNode parent, TreeNode child) {
            parent.children.add(child);
        }

        /**
         * Returns the TreeNode with the specified ID, or null if it doesn't
         * exist.
         *
         * @param ID Default name given to Nodes by JLibBig.
         * @return The TreeNode with the specified ID, if present, or null.
         */
        protected TreeNode findNodeByID(String ID) {
            LinkedList<TreeNode> nodes = new LinkedList<>(linearizeTree());
            for (TreeNode tn : nodes) {
                if (tn.id.equals(ID)) {
                    return tn;
                }
            }
            return null;
        }

        /**
         * Puts references to all TreeNodes in a list.
         *
         * @return
         */
        protected Collection<TreeNode> linearizeTree() {
            LinkedList<TreeNode> nodes = new LinkedList<>();
            for (TreeNode tn : root.children) {
                nodes.add(tn);
                linearizeTree(tn, nodes);
            }
            return nodes;
        }

        private void linearizeTree(TreeNode root,
                Collection<TreeNode> nodes) {
            for (TreeNode tn : root.children) {
                nodes.add(tn);
                linearizeTree(tn, nodes);
            }
        }

    } // End of PrintTree class

    private class TreeNode {

        private String type, name, id;
        private int indent;
        /**
         * Attributes can represent properties, ports, etc...
         */
        private LinkedList<Attribute> attributes;
        private LinkedList<TreeNode> children;

        private TreeNode(String t, String n, int i) {
            type = t;
            name = n;
            id = "?";
            indent = i;
            attributes = new LinkedList<>();
            children = new LinkedList<>();
        }

        protected <T> void addAttribute(String propName, T propValue) {
            attributes.add(new Attribute(propName, propValue));
        }

        /**
         * Links (symbolically) a port to another Node
         *
         * @param portNum Number of the port
         * @param dest Name (or ID) and port of the destination(s). Example:
         * portName@nodeName
         */
        protected void linkPort(int portNum, String dest) {
            int currentPort = 0;
            for (Attribute att : attributes) {
                if (att.name.equals("Port")) {
                    if (currentPort == portNum) {
                        att.value = att.value.toString().concat(dest);
                        return;
                    } else {
                        currentPort++;
                    }
                }

            }
        }

        /**
         * Prints this TreeNode and the sub-tree rooted into it.
         */
        private String printTree() {
            StringBuilder sb = new StringBuilder();
            if (!name.equals(PrintTree.FRN)) {
                sb.append(this).append("\n");;
            }
            if (!children.isEmpty()) {

                for (TreeNode n : children) {
                    sb.append(n.printTree());
                }
            }
            return sb.toString();
        }

        // toString only prints this TreeNode
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            prIndent(indent, sb);
            sb.append(type).append(" ").append(name).append(" {");
            for (Attribute a : attributes) {
                sb.append(" ").append(a).append(";");
            }
            sb.append("}");
            return sb.toString();
        }

        /**
         * Subclass to represent ports, properties, etc...
         *
         * @param <T> Type of the value, usually String
         */
        private class Attribute<T> {

            private String name;
            private T value;

            private Attribute(String name, T value) {
                this.name = name;
                this.value = value;
            }

            @Override
            public String toString() {
                StringBuilder sb = new StringBuilder();
                sb.append(name).append(": ").append(value);
                return sb.toString();
            }
        } // End Attribute class
    }   // End TreeNode class    

}
