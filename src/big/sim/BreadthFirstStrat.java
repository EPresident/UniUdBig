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

import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.LinkedList;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BreadthFirstStrat implements BRSStrategy {

    private final LinkedList<Bigraph> queue;
    private RewritingRule[] rules;

    public BreadthFirstStrat() {
        queue = new LinkedList<>();
    }

    @Override
    public Iterable<Bigraph> apply(Bigraph to) {
        for (RewritingRule r : rules) {
            RewRuleWProps rrwp = (RewRuleWProps) r;
            for(Bigraph big : rrwp.apply(to)){
                queue.add(big);
            }
        }
        return queue;
    }

    @Override
    public void setRules(RewritingRule[] rs) {
        rules = rs;
    }

}