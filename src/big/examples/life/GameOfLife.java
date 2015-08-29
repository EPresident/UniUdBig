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
package big.examples.life;

import big.prprint.BigPPrinterVeryPretty;
import big.prprint.DotLangPrinter;
import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.LinkedList;
import java.util.Random;

/**
 * Class for simulating Conway's Game of Life with bigraphs.
 *
 * TODO aggiungere gerarchia di sottotipi per i controlli di link Ridefinire
 * Matcher per passargli comparatore di controlli
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GameOfLife {

    public static final Signature SIGNATURE = generateSignature();

    private int rows, columns;
    private Bigraph gol;
    private final ControlHierarchy ctrlH = new ControlHierarchy();

    public GameOfLife(int rows, int cols, long seed, double minDensity, boolean wrapAround) {
        System.out.println("Creating GoL");
        System.out.println("rows: " + rows + "; cols: " + cols);
        this.rows = rows;
        columns = cols;
        Random randGen = new Random(seed);
        int liveCellsN;
        do {
            liveCellsN = randGen.nextInt(rows * cols);
        } while ((double) liveCellsN / ((double) rows * (double) cols) < minDensity);
        // TODO: Generate Bigraph...

        System.out.println("Building Bigraph");
        //<editor-fold desc="Build Bigraph">
        BigraphBuilder builder = new BigraphBuilder(SIGNATURE);
        Root root = builder.addRoot();
        System.out.println("Building state");
        // Add state nodes
        Node alpha = builder.addNode("computeNextStates", root);
        alpha.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("Name", "alpha")));

        Node beta = builder.addNode("update", root);
        beta.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("Name", "beta")));
        Node U = builder.addNode("nextStateUncomputed", root);
        U.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("Name", "u")));
        Node L = builder.addNode("nextStateLive", root);
        L.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("Name", "L")));
        Node D = builder.addNode("nextStateDead", root);
        D.attachProperty(new SharedProperty<String>(
                new SimpleProperty<String>("Name", "D")));

        System.out.println("building live cells");
        Node[][] cells = new Node[rows][columns];
        // Generate live cells at random
        int liveCellsI[] = new int[liveCellsN],
                liveCellsJ[] = new int[liveCellsN];
        // Generate live cells coords
        for (int i = 0; i < liveCellsN; i++) {
            liveCellsI[i] = randGen.nextInt(cols);
            liveCellsJ[i] = randGen.nextInt(rows);
        }
        // Create live cells
        for (int i = 0; i < liveCellsN; i++) {
            if (cells[liveCellsI[i]][liveCellsJ[i]] == null) {
                cells[liveCellsI[i]][liveCellsJ[i]]
                        = builder.addNode("liveCell", root, null, U.getPort(0).getHandle());
                cells[liveCellsI[i]][liveCellsJ[i]].attachProperty(new SharedProperty<String>(
                        new SimpleProperty<String>("Name", "Live_Cell")));
            }
        }
        System.out.println("Building dead cells");
        // Create dead cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j] == null) {
                    cells[i][j] = builder.addNode("deadCell", root, null, U.getPort(0).getHandle());
                    cells[i][j].attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Dead_Cell")));
                }
            }
        }
        System.out.println("Creating links");
        // Create links
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (wrapAround) {
                    // north
                    Node linkN = builder.addNode("link", cells[i][j],
                            cells[i][(j + 1) % rows].getPort(0).getHandle());
                    linkN.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    Node dir = builder.addNode("north", linkN);
                    dir.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "North")));
                    // north-east
                    Node linkNE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][(j + 1) % rows].getPort(0).getHandle());
                    linkNE.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("northEast", linkNE);
                    dir.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "NorthEast")));
                    // east
                    Node linkE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][j].getPort(0).getHandle());
                    linkE.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("east", linkE);
                    dir.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "East")));
                    // south-east
                    Node linkSE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][(j - 1 + rows) % rows].getPort(0).getHandle());
                    linkSE.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("southEast", linkSE);
                    dir.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "SouthEast")));
                    // south
                    Node linkS = builder.addNode("link", cells[i][j],
                            cells[i][(j - 1 + rows) % rows].getPort(0).getHandle());
                    linkS.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("south", linkS);
                    dir.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("Name", "South")));
                    // south-west
                    Node linkSW = builder.addNode("link", cells[i][j],
                            cells[(i - 1 + columns) % columns][(j - 1 + rows) % rows].getPort(0).getHandle());
                    linkSW.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir =builder.addNode("southWest", linkSW);
                    dir.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("Name", "SouthWest")));
                    // west
                    Node linkW = builder.addNode("link", cells[i][j],
                            cells[(i - 1 + columns) % columns][j].getPort(0).getHandle());
                    linkW.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("west", linkW);
                    dir.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("Name", "West")));
                    // north-west
                    Node linkNW = builder.addNode("link", cells[i][j],
                            cells[(i - 1 + columns) % columns][(j + 1) % rows].getPort(0).getHandle());
                    linkNW.attachProperty(new SharedProperty<String>(
                            new SimpleProperty<String>("Name", "Link")));
                    dir = builder.addNode("northEast", linkNW);
                    dir.attachProperty(new SharedProperty<String>(
				new SimpleProperty<String>("Name", "NorthWest")));
                } else {
                    // north
                    Node linkN = builder.addNode("link", cells[i][j]);
                    builder.addNode("north", linkN);
                    // north-east
                    Node linkNE = builder.addNode("link", cells[i][j]);
                    builder.addNode("northEast", linkNE);
                    // east
                    Node linkE = builder.addNode("link", cells[i][j]);
                    builder.addNode("east", linkE);
                    // south-east
                    Node linkSE = builder.addNode("link", cells[i][j]);
                    builder.addNode("southEast", linkSE);
                    // south
                    Node linkS = builder.addNode("link", cells[i][j]);
                    builder.addNode("south", linkS);
                    // south-west
                    Node linkSW = builder.addNode("link", cells[i][j]);
                    builder.addNode("southWest", linkSW);
                    // west
                    Node linkW = builder.addNode("link", cells[i][j]);
                    builder.addNode("west", linkW);
                    // north-west
                    Node linkNW = builder.addNode("link", cells[i][j]);
                    builder.addNode("northEast", linkNW);
                }
            }
        }
        //</editor-fold>
        gol = builder.makeBigraph();
    }

    public GameOfLife() {
        this(3, 3, 2163162267L, 0, true);
    }

    public static void main(String[] args) {
        GameOfLife gol = new GameOfLife();
        DotLangPrinter dlp = new DotLangPrinter();
        BigPPrinterVeryPretty pprt = new BigPPrinterVeryPretty();
        //dlp.printDotFile(gol.gol, "GameOfLife", "gol");
        System.out.println(pprt.prettyPrint(gol.gol, "GoL"));
    }

    private void generateRandomGoL() {
    }

    private static Signature generateSignature() {
        LinkedList<Control> controls = new LinkedList<>();
        // Cells
        controls.add(new Control("deadCell", true, 2));
        controls.add(new Control("liveCell", true, 2));

        // Links
        controls.add(new Control("link", true, 1));
        controls.add(new Control("north", true, 0));
        controls.add(new Control("northEast", true, 0));
        controls.add(new Control("east", true, 0));
        controls.add(new Control("southEast", true, 0));
        controls.add(new Control("south", true, 0));
        controls.add(new Control("southWest", true, 0));
        controls.add(new Control("west", true, 0));
        controls.add(new Control("northWest", true, 0));

        // Status controls
        controls.add(new Control("nextStateUncomputed", true, 1));
        controls.add(new Control("nextStateDead", true, 1));
        controls.add(new Control("nextStateLive", true, 1));
        controls.add(new Control("computeNextStates", true, 0)); // alpha
        controls.add(new Control("update", true, 0)); // beta
        return new Signature(controls);
    }

    public boolean isSubType(Control c1, Control c2) {
        return ctrlH.isSubType(c1, c2);
    }

    private class ControlHierarchy {

        public boolean isSubType(Control c1, Control c2) {
            if (c2.getName().equals("link")) {
                switch (c1.getName()) {
                    case "north":
                    case "northEast":
                    case "east":
                    case "southEast":
                    case "south":
                    case "southWest":
                    case "west":
                    case "northWest":
                        return true;
                    default:
                        return false;
                }
            }
            return false;
        }
    }
}
