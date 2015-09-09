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
package big.examples.car;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class RR_Move extends RewritingRule {

    private static final Bigraph redex = generateRedex(),
            reactum = generateReactum();
    private static final InstantiationMap map = new InstantiationMap(3, 0, 1, 2);

    public RR_Move() {
        super(redex, reactum, map);
    }

    private static Bigraph generateRedex() {
        BigraphBuilder bb = new BigraphBuilder(Car.SIGNATURE);

        Root root = bb.addRoot();

        OuterName fromD = bb.addOuterName("fromD");
        Node dest = bb.addNode("place", root, fromD);
        bb.addSite(dest);

        OuterName fromS = bb.addOuterName("fromS");
        Node start = bb.addNode("place", root, fromS);
        Node road = bb.addNode("road", start, fromD);
        bb.addSite(start);

        OuterName tgt = bb.addOuterName("target");
        Node car = bb.addNode("car", start, tgt);
        bb.addNode("fuel", car);
        bb.addSite(car);

        return bb.makeBigraph(true);
    }

    private static Bigraph generateReactum() {

        BigraphBuilder bb = new BigraphBuilder(Car.SIGNATURE);

        Root root = bb.addRoot();

        OuterName fromD = bb.addOuterName("fromD");
        Node dest = bb.addNode("place", root, fromD);
        bb.addSite(dest);

        OuterName fromS = bb.addOuterName("fromS");
        Node start = bb.addNode("place", root, fromS);
        Node road = bb.addNode("road", start, fromD);
        bb.addSite(start);

        OuterName tgt = bb.addOuterName("target");
        Node car = bb.addNode("car", dest, tgt);
        bb.addSite(car);

        return bb.makeBigraph(true);
    }

}
