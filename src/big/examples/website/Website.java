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
package big.examples.website;

import big.mc.ModelChecker;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.sim.BreadthFirstSim;
import big.sim.RandomSim;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import it.uniud.mads.jlibbig.core.std.SignatureBuilder;

/**
 * Example depicting a user interacting with a website.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class Website {

    public static Signature SIGNATURE = generateSignature();
    public static RewritingRule[] RULES = {new RR_BrowsePage(), new RR_ChangeLocus(),
        new RR_ChangePage(), /*new RR_ChangePageFromForm(), new RR_AddValidInputToLinkForm(),
        new RR_AddInvalidInputToLinkForm1()*/};

    public static void main(String[] args) {
        Bigraph state = generateECommerceSite(3);
        BigPPrinterVeryPretty pprnt = new BigPPrinterVeryPretty();
        System.out.println(pprnt.prettyPrint(state, "ECommerce site"));
        modelCheckerTest();
    }

    private static Signature generateSignature() {
        SignatureBuilder sb = new SignatureBuilder();

        sb.add("user", true, 1);
        sb.add("page", true, 2); // Ports: user focus, links to the page
        sb.add("locus", true, 1); // Port: user focus
        sb.add("info", false, 0);
        sb.add("link", false, 1); // Port: page linked
        sb.add("linkForm", true, 1); // Port: page linked
        sb.add("validInput", false, 0);
        sb.add("invalidInput", false, 0);
        sb.add("goal", false, 0);

        return sb.makeSignature();
    }

    private static boolean modelCheckerTest() {
        Bigraph state = generateECommerceSite(3);
        Predicate p = new WarioPredicate(goalReached(), new TruePredicate(),
                new TruePredicate(), new TruePredicate());
        ModelChecker mc = new ModelChecker(new RandomSim(state, RULES), p);
        System.out.print("Is a transaction possible? ");
        if (mc.modelCheck(100)) {
            System.out.println("YES.");
            return true;
        } else {
            System.out.println("NO");
            return false;
        }
    }

    private static Bigraph generateECommerceSite(int articles) {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root root = bb.addRoot();
        Node user = bb.addNode("user", root);
        // home
        OuterName homeLink = bb.addOuterName("homeLink");
        Node home = bb.addNode("page", root, user.getPort(0).getHandle(), homeLink);
        // articles
        Node[] articlePages = new Node[articles];
        OuterName[] articleLinks = new OuterName[articles];
        for (int i = 0; i < articlePages.length; i++) {
            articleLinks[i] = bb.addOuterName("article" + i + "Link");
            articlePages[i] = bb.addNode("page", root, null, articleLinks[i]);
        }
        // checkout
        OuterName checkoutLink = bb.addOuterName("checkoutLink");
        Node checkout = bb.addNode("page", root, null, checkoutLink);
        // checkout (logged in)
        OuterName checkoutAuthLink = bb.addOuterName("checkoutAuthLink");
        Node checkoutAuth = bb.addNode("page", root, null, checkoutAuthLink);
        // goal
        OuterName goalLink = bb.addOuterName("goalLink");
        Node goal = bb.addNode("page", root, null, goalLink);

        // fill home
        Node locus = bb.addNode("locus", home);
        bb.addNode("info", locus);
        locus = bb.addNode("locus", home);
        bb.addNode("info", locus);
        for (int i = 0; i < articles; i++) {
            locus = bb.addNode("locus", home);
            bb.addNode("link", locus, articleLinks[i]);
        }
        // fill articles
        for (int i = 0; i < articles; i++) {
            locus = bb.addNode("locus", articlePages[i]);
            bb.addNode("info", locus);
            locus = bb.addNode("locus", articlePages[i]);
            bb.addNode("link", locus, homeLink);
            locus = bb.addNode("locus", articlePages[i]);
            bb.addNode("link", locus, checkoutLink);
        }
        
        // fill checkout
        locus = bb.addNode("locus", checkout);
        bb.addNode("info", locus);
        locus = bb.addNode("locus", checkout);
        bb.addNode("linkForm", locus, checkoutAuthLink);
        locus = bb.addNode("locus", checkout);
        bb.addNode("link", locus, homeLink);
        bb.addNode("goal", goal);
        // fill checkout (logged in)
        locus = bb.addNode("locus", checkoutAuth);
        bb.addNode("info", locus);
        locus = bb.addNode("locus", checkoutAuth);
        bb.addNode("link", locus, homeLink);
        locus = bb.addNode("locus", checkoutAuth);
        bb.addNode("linkForm", locus, goalLink);
        //fill goal
        locus = bb.addNode("locus", goal);
        bb.addNode("info", locus);
        locus = bb.addNode("locus", goal);
        bb.addNode("link", locus, homeLink);
        bb.addNode("goal", goal);

        return bb.makeBigraph(true);
    }

    private static Bigraph goalReached() {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root userRoot = bb.addRoot();
        Node user = bb.addNode("user", userRoot);
        //bb.addSite(userRoot);
        
        Root root = bb.addRoot();
        bb.addSite(root);
        OuterName pageLink = bb.addOuterName("pageLink");
        Node page = bb.addNode("page", root, user.getPort(0).getHandle(), pageLink);
        bb.addNode("goal", page);
        bb.addSite(page);

        return bb.makeBigraph(true);
    }
}
