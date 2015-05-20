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

/**
 * This class associates a RewritingRule (and/or its user-defined name) and the
 * result of its application to a Bigraph.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RuleApplication {

    protected Bigraph big;
    protected RewritingRule rule;
    protected String ruleName;

    protected RuleApplication(Bigraph b, RewritingRule rr, String s) {
        big = b;
        rule = rr;
        ruleName = s;
    }

    protected RuleApplication(Bigraph b, RewRuleWProps rr) {
        big = b;
        rule = rr;
        ruleName = rr.getName();
    }

    /*protected RuleApplication(Bigraph b, String s) {

    }*/
}
