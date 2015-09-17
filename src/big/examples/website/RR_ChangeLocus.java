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
public class RR_ChangeLocus extends RewritingRule {

    private static final Bigraph redex = generateRedex(),
            reactum = generateReactum();
    private static final InstantiationMap map = new InstantiationMap(4,0,1,2,3);

    public RR_ChangeLocus() {
        super(redex, reactum, map);
    }

    private static Bigraph generateRedex() {
        BigraphBuilder bb = new BigraphBuilder(Website.SIGNATURE);

        Root userRoot = bb.addRoot();
        Node user = bb.addNode("user", userRoot);
        
        Root root = bb.addRoot();
        bb.addSite(root);
        OuterName pageLink = bb.addOuterName("pageLink");
        Node page = bb.addNode("page", root, null, pageLink);
        bb.addSite(page);
        Node locus1 = bb.addNode("locus", page, user.getPort(0).getHandle());
        bb.addSite(locus1);
        Node locus2 = bb.addNode("locus", page);
        bb.addSite(locus2);

        return bb.makeBigraph(true);
    }
    
        private static Bigraph generateReactum() {
        BigraphBuilder bb = new BigraphBuilder(Website.SIGNATURE);

        Root userRoot = bb.addRoot();
        Node user = bb.addNode("user", userRoot);
        
        Root root = bb.addRoot();
        bb.addSite(root);
        OuterName pageLink = bb.addOuterName("pageLink");
        Node page = bb.addNode("page", root, null, pageLink);
        bb.addSite(page);
        Node locus1 = bb.addNode("locus", page);
        bb.addSite(locus1);
        Node locus2 = bb.addNode("locus", page, user.getPort(0).getHandle());
        bb.addSite(locus2);

        return bb.makeBigraph(true);
    }
}
