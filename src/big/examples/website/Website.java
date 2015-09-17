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

import big.prprint.BigPPrinterVeryPretty;
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
        new RR_ChangePage()};

    public static void main(String[] args) {
        Bigraph state = generateECommerceSite();
        BigPPrinterVeryPretty pprnt = new BigPPrinterVeryPretty();
        System.out.println(pprnt.prettyPrint(state, "ECommerce site"));

    }

    private static Signature generateSignature() {
        SignatureBuilder sb = new SignatureBuilder();

        sb.add("user", true, 1);
        sb.add("page", true, 2); // Ports: user focus, links to the page
        sb.add("locus", true, 1); // Port: user focus
        sb.add("info", false, 0);
        sb.add("link", false, 1); // Port: page linked
        sb.add("field", true, 1); // Port: page linked
        sb.add("validInput", false, 0);
        sb.add("invalidInput", false, 0);
        sb.add("goal", false, 0);

        return sb.makeSignature();
    }

    private static Bigraph generateECommerceSite() {
        BigraphBuilder bb = new BigraphBuilder(SIGNATURE);

        Root root = bb.addRoot();
        Node user = bb.addNode("user", root);
        // home
        OuterName homeLink = bb.addOuterName("homeLink");
        Node home = bb.addNode("page", root, user.getPort(0).getHandle(), homeLink);
        // articles
        int articles = 3;
        Node[] articlePages = new Node[articles];
        OuterName[] articleLinks = new OuterName[articles];
        for (int i = 0; i < articlePages.length; i++) {
            articleLinks[i] = bb.addOuterName("article" + i + "Link");
            articlePages[i] = bb.addNode("page", root, null, articleLinks[i]);
        }

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
        }

        return bb.makeBigraph(true);
    }
}
