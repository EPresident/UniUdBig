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
 * Class encapsulating the behavior of a BRS, e.g. which rules are applied where,
 * when, and how many times.
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public interface BRSStrategy {
    /**
     * Apply a RewritingRule to (all redex matches within) a Bigraph.
     * @param to Bigraph to match the rule on
     * @return An Iterable with the resulting Bigraph(s)
     */
    public List<Bigraph> apply(Bigraph to);
    /**
     * Apply a RewritingRule to (all redex matches within) a Bigraph.
     * @param to Bigraph to match the rule on
     * @return An Iterable of RuleApplication objects, i.e. the resulting 
     * Bigraphs paired with the rules that were applied on them.
     */
    public List<RuleApplication> apply_RA(Bigraph to);
    public void setRules(RewritingRule[] rs);
}
