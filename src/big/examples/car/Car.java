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

import big.mc.ModelChecker;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.sim.BreadthFirstSim;
import big.sim.Sim;
import big.sim.TrueRandomSim;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import it.uniud.mads.jlibbig.core.std.SignatureBuilder;

/**
 * Example class. We want to represent a car with a limited fuel supply, and a
 * series of locations which "consume" a set amount of fuel when traversed. One
 * of these locations is the destination of the car.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Car {

    public static final Signature SIGNATURE = generateSignature();
    private static final RewritingRule[] RULES = {new RR_Move()};

    public static void main(String[] args) {
        BigPPrinterVeryPretty pprt = new BigPPrinterVeryPretty();
        Bigraph state = generateLevel(8);
        Sim sim = new TrueRandomSim(state, RULES);
        Predicate p = new WarioPredicate(goalReached(), new TruePredicate(),
                new TruePredicate(), new TruePredicate());
        ModelChecker mc = new ModelChecker(new BreadthFirstSim(state, RULES), p);
        System.out.println(pprt.prettyPrint(state, "State 0"));

        /*int i = 1;
         while(!sim.simOver()){
         List<RuleApplication> ras = sim.stepAndGet();
         if(ras.size()>0){
         state = ras.get(0).getBig();
         System.out.println("Applied "+ras.get(0).getRuleName());
         System.out.println(pprt.prettyPrint(state,"State "+(i++)));
         }else{
         state = new BigraphBuilder(SIGNATURE).makeBigraph();
         }
         }*/
        System.out.print("Is the destination reachable? ");
        if (mc.modelCheck()) {
            System.out.println("YES.");
        } else {
            System.out.println("NO");
        }
        // System.out.println(pprt.prettyPrint(mc.getGraph().getLastNodeUsed().getState(),"Last Node Computed"));
    }

    private void modelCheckerTest() {
        Bigraph state = generateLevel(8);
        Predicate p = new WarioPredicate(goalReached(), new TruePredicate(),
                new TruePredicate(), new TruePredicate());
        ModelChecker mc = new ModelChecker(new BreadthFirstSim(state, RULES), p);
        System.out.print("Is the destination reachable? ");
        if (mc.modelCheck()) {
            System.out.println("YES.");
        } else {
            System.out.println("NO");
        }
    }

    private static Signature generateSignature() {
        SignatureBuilder sb = new SignatureBuilder();

        sb.add("car", true, 1);
        sb.add("fuel", true, 0);
        sb.add("place", true, 1);
        sb.add("road", true, 1);
        sb.add("target", true, 1);

        return sb.makeSignature("Car Signature");
    }

    public static Bigraph generateLevel(int fuel) {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root root = bb.addRoot();

        Node p0 = bb.addNode("place", root);
        Node p1 = bb.addNode("place", root);
        Node p2 = bb.addNode("place", root);
        Node p3 = bb.addNode("place", root);
        Node p4 = bb.addNode("place", root);
        Node p5 = bb.addNode("place", root);
        Node p6 = bb.addNode("place", root);
        Node p7 = bb.addNode("place", root);
        Node p8 = bb.addNode("place", root);

        bb.addNode("road", p0, p1.getPort(0).getHandle());
        bb.addNode("road", p0, p3.getPort(0).getHandle());
        bb.addNode("road", p1, p2.getPort(0).getHandle());
        bb.addNode("road", p1, p4.getPort(0).getHandle());
        bb.addNode("road", p2, p5.getPort(0).getHandle());
        bb.addNode("road", p3, p4.getPort(0).getHandle());
        bb.addNode("road", p3, p7.getPort(0).getHandle());
        bb.addNode("road", p4, p5.getPort(0).getHandle());
        bb.addNode("road", p4, p1.getPort(0).getHandle());
        bb.addNode("road", p5, p6.getPort(0).getHandle());
        bb.addNode("road", p5, p7.getPort(0).getHandle());
        bb.addNode("road", p5, p8.getPort(0).getHandle());
        bb.addNode("road", p6, p8.getPort(0).getHandle());
        bb.addNode("road", p6, p5.getPort(0).getHandle());
        bb.addNode("road", p7, p2.getPort(0).getHandle());

        Node target = bb.addNode("target", p7);

        Node car = bb.addNode("car", p0, target.getPort(0).getHandle());

        for (int i = 0; i < fuel; i++) {
            bb.addNode("fuel", car);
        }

        return bb.makeBigraph(true);
    }

    private static Bigraph goalReached() {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root root = bb.addRoot();

        OuterName from = bb.addOuterName("from");
        Node place = bb.addNode("place", root, from);
        bb.addSite(place);
        Node tgt = bb.addNode("target", place);
        Node car = bb.addNode("car", place, tgt.getPort(0).getHandle());
        bb.addSite(car);

        return bb.makeBigraph(true);
    }
}
