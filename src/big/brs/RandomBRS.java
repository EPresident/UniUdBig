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
package big.brs;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * This BRS chooses an application at random.
 * 
 * TODO add dynamic bound adjustment
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RandomBRS implements BRS {

    private final RewritingRule[] rules;
    private final Random randGen;
    private final int randBound;

    /**
     * This BRS generates a random integer i, and applies the provided rules;
     * then the i-th application is selected and returned. If the number of
     * applications is less than i, the one with the largest index is chosen.
     *
     * @param rrs Rewriting Rules this BRS employs.
     * @param randBound Max bound for the random generation of i.
     */
    public RandomBRS(RewritingRule[] rrs, int randBound) {
        rules = rrs;
        randGen = new Random();
        this.randBound = randBound;
    }

    @Override
    public List<Bigraph> apply(Bigraph to) {
        int rand = randGen.nextInt(randBound);
        int i = 0;
        LinkedList<Bigraph> queue = new LinkedList<>();
        for (RewritingRule r : rules) {
            for (Bigraph big : r.apply(to)) {
                queue.add(big);
                if (++i >= rand) {
                    return queue;
                }
            }
        }
        return queue;
    }

    @Override
    public List<RuleApplication> apply_RA(Bigraph to) {
        LinkedList<RuleApplication> queue = new LinkedList<>();
        int rand = randGen.nextInt(randBound);
        int i = 0;
        for (RewritingRule r : rules) {
            for (Bigraph big : r.apply(to)) {
                queue.add(new RuleApplication(big, r));
                if (++i >= rand) {
                    return queue;
                }
            }
        }
        return queue;
    }
}
