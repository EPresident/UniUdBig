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
public class RR_AddInvalidInputToLinkForm extends RewritingRule {

    private static final Bigraph redex = generateRedex(),
            reactum = generateReactum();
    private static final InstantiationMap map = new InstantiationMap(2, 0, 1);

    public RR_AddInvalidInputToLinkForm() {
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
        Node locus = bb.addNode("locus", page1, user.getPort(0).getHandle());
        OuterName formLink = bb.addOuterName("formLink");
        Node form = bb.addNode("linkForm", locus, formLink);
        bb.addSite(form);

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
        Node locus = bb.addNode("locus", page1, user.getPort(0).getHandle());
        OuterName formLink = bb.addOuterName("formLink");
        Node form = bb.addNode("linkForm", locus, formLink);
        bb.addNode("invalidInput", form);

        return bb.makeBigraph(true);
    }
}
