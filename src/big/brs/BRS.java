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
import java.util.List;

/**
 * Bigraphic Reactive System. Class for applying a set of rewriting rules to a
 * bigraph.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class BRS {

    private final RewritingRule[] rules;
    private BRSStrategy strategy;

    public BRS(BRSStrategy ss, RewritingRule[] rs) {
        rules = rs;
        strategy = ss;
        strategy.setRules(rules);
    }

    public BRSStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(BRSStrategy strategy) {
        this.strategy = strategy;
        this.strategy.setRules(rules);
    }

    /**
     * Apply a RewritingRule to (all redex matches within) a Bigraph.
     * @param to Bigraph to match the rule on
     * @return An Iterable with the resulting Bigraph(s)
     */
    public List<Bigraph> apply(Bigraph to) {
        return strategy.apply(to);
    }

    /**
     * Apply a RewritingRule to (all redex matches within) a Bigraph.
     * @param to Bigraph to match the rule on
     * @return An Iterable of RuleApplication objects, i.e. the resulting 
     * Bigraphs paired with the rules that were applied on them.
     */
    public List<RuleApplication> apply_RA(Bigraph to){
        return strategy.apply_RA(to);
    }
}
