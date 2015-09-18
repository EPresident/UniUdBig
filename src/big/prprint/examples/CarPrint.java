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
package big.prprint.examples;

import static big.examples.car.Car.SIGNATURE;
import big.examples.car.RR_Move;
import big.prprint.BigPPrinterVeryPretty;
import big.prprint.DotLangPrinter;
import big.sim.BreadthFirstSim;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class CarPrint {

    public static void main(String[] args) {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root root = bb.addRoot();

        Node p0 = bb.addNode("place", root);
        Node p1 = bb.addNode("place", root);
        Node p2 = bb.addNode("place", root);

        bb.addNode("road", p0, p1.getPort(0).getHandle());
        bb.addNode("road", p1, p2.getPort(0).getHandle());
        bb.addNode("road", p1, p0.getPort(0).getHandle());
        bb.addNode("road", p2, p0.getPort(0).getHandle());


        Node target = bb.addNode("target", p2);

        Node car = bb.addNode("car", p0, target.getPort(0).getHandle());

        for (int i = 0; i < 5; i++) {
            bb.addNode("fuel", car);
        }

        Bigraph big = bb.makeBigraph(true);
        BreadthFirstSim bfs = new BreadthFirstSim(big, new RewritingRule[]{new RR_Move()});
        BigPPrinterVeryPretty pprt = new BigPPrinterVeryPretty();
        DotLangPrinter dlp = new DotLangPrinter();
        System.out.println(big.toString());
        System.out.println(pprt.prettyPrint(big,"Car Example"));
        bfs.fullSim(1000);
        System.out.println(bfs.getGraph().toDotLang());
        dlp.printDotFile(big, "Car_Example", "car_example");
    }
}
