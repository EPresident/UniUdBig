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
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Class for simulating Conway's Game of Life with bigraphs.
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class GameOfLife {
    public static final Signature SIGNATURE = generateSignature();
    
    private int rows, columns;
    private ArrayList<Bigraph> nodes;
    
    public GameOfLife(int rows, int cols){
        this.rows = rows;
        columns = cols;
    }
    
    public static void main(String[] args){
        
    }
    
    private static Signature generateSignature(){
        LinkedList<Control> controls = new LinkedList<>();
        // Cells
        controls.add(new Control("deadCell", true, 1));
        controls.add(new Control("liveCell", true, 1));
        
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
}
