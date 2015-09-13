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

import big.bsg.BigStateGraph;
import big.bsg.BSGNode;
import big.brs.RuleApplication;
import big.net.Utils;
import big.prprint.BigPPrinterVeryPretty;
import big.bsg.BSGNode.BSGLink;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

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
        RewritingRule[] rules = Utils.getNetFWRules();
        Sim sim = new BreadthFirstSim(bigraph, rules);

        //System.out.println(BigStateGraph.areIsomorph(bigraph, new BigraphBuilder(bigraph).makeBigraph()));
        int i = 1000;
        int applcations = 0;
        do {
            for (RuleApplication ra : sim.stepAndGet()) {
                //  System.out.println(pp.prettyPrint(ra.big,ra.ruleName));
                applcations++;
            }
            System.out.println(applcations + " applications");
            i--;
        } while (i > 0 && !sim.simOver());

        BigStateGraph bsg = sim.getGraph();
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(bsg.getGraphSize() + " - " + applcations);
        System.out.println(bsg.toDotLang());
        BSGNode currentNode = bsg.getRoot();
        String prName = "Root";
        List<BSGLink> links = bsg.getRoot().getLinks();
        while (true) {
            System.out.println(pp.prettyPrint(currentNode.getState(), prName));
            i = 0;
            System.out.println("Choose a branch: ");
            for (BSGLink bsgl : links) {
                System.out.println(i + "- " + bsgl.rewRule);
                i++;
            }
            int choice = 0;
            try {
                String in = input.readLine();
                if (in.isEmpty()) {
                    System.out.println("Exiting.");
                    System.exit(0);
                }
                choice = Integer.parseInt(in);
            } catch (IOException ioex) {
                System.err.println(ioex.getMessage() + "\n Halting simulation.");
                System.exit(1);
            } catch (NumberFormatException nfex) {
                System.err.println("Expected a number as input: " + nfex.getMessage());
                System.exit(1);
            }
            prName = links.get(choice).rewRule.getClass().getSimpleName();
            currentNode = links.get(choice).destNode;
            links = currentNode.getLinks();
        }

    }
}
