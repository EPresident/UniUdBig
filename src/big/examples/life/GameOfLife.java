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
        this.rows = rows;
        columns = cols;
        Random randGen = new Random(seed);
        int liveCellsN;
        do {
            liveCellsN = randGen.nextInt(rows * cols);
        } while (liveCellsN / (rows * cols) < minDensity);
        // TODO: Generate Bigraph...

        //<editor-fold desc="Build Bigraph">
        BigraphBuilder builder = new BigraphBuilder(SIGNATURE);
        Root root = builder.addRoot();
        // Add state nodes
        builder.addNode("computeNextStates", root);
        builder.addNode("update", root);
        Node U = builder.addNode("nextStateUncomputed", root);
        builder.addNode("nextStateLive", root);
        builder.addNode("nextStateDead", root);
        
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
                cells[liveCellsI[i]][liveCellsJ[i]] = 
                        builder.addNode("deadCell", root, null, U.getPort(0).getHandle());
            }
        }
        // Create dead cells
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (cells[i][j] == null) {
                    cells[i][j] = builder.addNode("deadCell", root,null, U.getPort(0).getHandle());
                }
            }
        }
        // Create links
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (wrapAround) {
                    // north
                    Node linkN = builder.addNode("link", cells[i][j],
                            cells[i][(j + 1) % rows].getPort(0).getHandle());
                    builder.addNode("north", linkN);
                    // north-east
                    Node linkNE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][(j + 1) % rows].getPort(0).getHandle());
                    builder.addNode("northEast", linkNE);
                    // east
                    Node linkE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][j].getPort(0).getHandle());
                    builder.addNode("east", linkE);
                    // south-east
                    Node linkSE = builder.addNode("link", cells[i][j],
                            cells[(i + 1) % columns][(j - 1) % rows].getPort(0).getHandle());
                    builder.addNode("southEast", linkSE);
                    // south
                    Node linkS = builder.addNode("link", cells[i][j],
                            cells[i][(j - 1) % rows].getPort(0).getHandle());
                    builder.addNode("south", linkS);
                    // south-west
                    Node linkSW = builder.addNode("link", cells[i][j],
                            cells[(i - 1) % columns][(j - 1) % rows].getPort(0).getHandle());
                    builder.addNode("southWest", linkSW);
                    // west
                    Node linkW = builder.addNode("link", cells[i][j],
                            cells[(i - 1) % columns][j].getPort(0).getHandle());
                    builder.addNode("west", linkW);
                    // north-west
                    Node linkNW = builder.addNode("link", cells[i][j],
                            cells[(i - 1) % columns][(j + 1) % rows].getPort(0).getHandle());
                    builder.addNode("northEast", linkNW);

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

    public static void main(String[] args) {

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
