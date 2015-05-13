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

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class for simulating the application of several rewriting rules
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BigSim {

    public final ArrayList<RewritingRule> rules;

    public enum SimStrategy {

        DEPTH_FIRST, BREADTH_FIRST
    }
    private final SimStrategy strategy;
    private final Bigraph start;
    private Bigraph current;
    private int ruleIndex;

    public BigSim(Bigraph big, SimStrategy ss) {
        rules = new ArrayList<>();
        strategy = ss;
        start = big;
        current = start;
        ruleIndex = 0;
    }

    public void addRule(RewritingRule rr) {
        rules.add(rr);
    }

    public void reset() {
        current = start;
        ruleIndex = 0;
    }

    public void sim() {
        
    }

    public void step() {
        Iterator<Bigraph> it = rules.get(ruleIndex).apply(current).iterator();
        switch (strategy) {
            case DEPTH_FIRST:
                if (it.hasNext()) {
                    current = it.next();
                }
                ruleIndex=(ruleIndex+1)%rules.size();
                break;
            case BREADTH_FIRST:
                System.err.println("Not implemented yet.");
                break;
            default:
                System.err.println("Unknown simulation strategy: " + strategy);
        }
    }

    public void steps(int num) {

    }
}
