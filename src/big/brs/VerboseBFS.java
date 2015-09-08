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

import big.prprint.BigPPrinterVeryPretty;
import big.rules.RewRuleWProps;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.util.LinkedList;
import java.util.List;

/**
 * Applies the rules following a breadth-first approach. This means that, for
 * each node/state, all possible nodes/states (deriving from the application of
 * the rules) are computed. This strategy prints the rules it applies on
 * standard output.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class VerboseBFS implements BRSStrategy {

    private RewritingRule[] rules;

    public VerboseBFS() {

    }

    @Override
    public List<Bigraph> apply(Bigraph to) {
        LinkedList<Bigraph> queue = new LinkedList<>();
        for (RewritingRule r : rules) {
            int matches = 0;
            for (Bigraph big : r.apply(to)) {
                queue.add(big);
                matches++;
            }
            System.out.println("VerboseDFS: Applied rule " + r.getClass().getSimpleName() + ", " + matches + " matches.");
        }
        return queue;
    }

    @Override
    public void setRules(RewritingRule[] rs) {
        rules = rs;
    }

    @Override
    public List<RuleApplication> apply_RA(Bigraph to) {
        System.out.println("VerboseBFS: apply_RA()...");
        LinkedList<RuleApplication> queue = new LinkedList<>();
        for (RewritingRule r : rules) {
            int matches = 0;
            System.out.println("Applying rule "+r.getClass().getSimpleName());
            if (r instanceof RewRuleWProps) {
                System.out.println("VerboseBFS: rrwp.");
                RewRuleWProps rrwp = (RewRuleWProps) r;
                if (rrwp.isApplicable(to)) {
                    System.out.println("VerboseBFS: rule applicable.");
                    for (Bigraph big : rrwp.apply(to)) {
                        queue.add(new RuleApplication(big, rrwp));
                        matches++;
                        System.out.println("VerboseBFS: matches - "+matches);
                        
                    //    System.out.println(new BigPPrinterVeryPretty().prettyPrint(big));
                    }
                }
                System.out.println("VerboseDFS: Applied rule " + rrwp.getName() + ", " + matches + " matches.");
            } else {
                System.out.println("VerboseBFS: normal rule.");
                for (Bigraph big : r.apply(to)) {
                    queue.add(new RuleApplication(big, r, r.toString()));
                    matches++;
                }
                System.out.println("VerboseDFS: Applied rule " + r.getClass().getSimpleName() + ", " + matches + " matches.");
            }
        }
        return queue;
    }

}
