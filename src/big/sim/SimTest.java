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

import big.net.Utils;
import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;

/**
 * Test class for Bigraphic Reactive Systems simulation using BigStateGraph
 *
 * @see BigStateGraph
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class SimTest {

    public static void main(String[] args) {
        Bigraph bigraph = Utils.clientServerPacketExchange();
        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        System.out.println(pp.prettyPrint(bigraph, "Bigrafo iniziale"));
        RewritingRule[] rules = Utils.getNetRules();
        RewritingRule[] rules2 = Utils.getNetFWRules();
        BRS brs = new BRS(new BreadthFirstStrat(), rules);
        BigStateGraph bsg = new BigStateGraph(bigraph);
        Sim sim = new Sim(bsg, brs);
        do {
            for (RuleApplication ra : sim.stepAndGet()) {
                System.out.println(pp.prettyPrint(ra.big,ra.ruleName));
            }
        } while (sim.hasNext());

    }
}
