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
public class RR_ChangePage extends RewritingRule {

    private static final Bigraph redex = generateRedex(),
            reactum = generateReactum();
    private static final InstantiationMap map = new InstantiationMap(4, 0, 1, 2, 3);

    public RR_ChangePage() {
        super(redex, reactum, map);
    }

    private static Bigraph generateRedex() {
        BigraphBuilder bb = new BigraphBuilder(Website.SIGNATURE);

        Root userRoot = bb.addRoot();
        Node user = bb.addNode("user", userRoot);
        
        Root root0 = bb.addRoot();
        bb.addSite(root0);
        OuterName page1Link = bb.addOuterName("page1Link");
        Node page1 = bb.addNode("page", root0, null, page1Link);
        bb.addSite(page1);
        Root root1 = bb.addRoot();
        bb.addSite(root1);
        OuterName page2Link = bb.addOuterName("page2Link");
        Node page2 = bb.addNode("page", root1, null, page2Link);
        bb.addSite(page2);
        Node locus = bb.addNode("locus", page1, user.getPort(0).getHandle());
        Node link = bb.addNode("link", locus, page2Link);

        return bb.makeBigraph(true);
    }

    private static Bigraph generateReactum() {
        BigraphBuilder bb = new BigraphBuilder(Website.SIGNATURE);

        Root userRoot = bb.addRoot();
        Node user = bb.addNode("user", userRoot);
        
        Root root0 = bb.addRoot();
        bb.addSite(root0);
        OuterName page1Link = bb.addOuterName("page1Link");
        Node page1 = bb.addNode("page", root0, null, page1Link);
        bb.addSite(page1);
        Root root1 = bb.addRoot();
        bb.addSite(root1);
        OuterName page2Link = bb.addOuterName("page2Link");
        Node page2 = bb.addNode("page", root1, user.getPort(0).getHandle(), page2Link);
        bb.addSite(page2);
        Node locus = bb.addNode("locus", page1);
        Node link = bb.addNode("link", locus, page2Link);

        return bb.makeBigraph(true);
    }
}
