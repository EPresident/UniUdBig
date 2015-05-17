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

import big.net.DFRule;
import big.net.DecapRule;
import big.net.Domain2HostRule;
import big.net.EncapRule;
import big.net.Utils;
import big.prprint.BigPPrinterVeryPretty;
import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import java.util.Iterator;

/**
 * Test class for Bigraphic Reactive Systems simulation using BigStateGraph
 *
 * @see BigStateGraph
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class SimTest {

    public static void main(String[] args) {
        Signature signature = Utils.getNetSignature();
        Bigraph bigraph = Utils.clientServerPacketExchange(signature);
        BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
        System.out.println(pp.prettyPrint(bigraph, "Bigrafo iniziale"));

        /*
         * ---------------------------------------------------------------
         * Start of the reactions.
         * ---------------------------------------------------------------
         */
        Matcher matcher = new Matcher();
        EncapRule encap = new EncapRule(EncapRule.getRedex(signature),
                EncapRule.getReactum(signature),
                EncapRule.getInstMap());
        while (matcher.match(bigraph, EncapRule.getRedex(signature)).iterator().hasNext()) {
            if (matcher.match(bigraph, DFRule.getRedex(signature)).iterator().hasNext()) {
                break;
            }
            Iterator<Bigraph> iterator = encap.apply(bigraph).iterator();
            bigraph = iterator.next();
            EncapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Encap"));
        }
        DFRule ip_forward = new DFRule(DFRule.getRedex(signature),
                DFRule.getReactum(signature),
                DFRule.getInstMap());
        if (matcher.match(bigraph, DFRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = ip_forward.apply(bigraph).iterator();
            bigraph = iterator.next();
            DFRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Direct Forward"));
        }

        Domain2HostRule d2h = new Domain2HostRule(Domain2HostRule.getRedex(signature),
                Domain2HostRule.getReactum(signature),
                Domain2HostRule.getInstMap());
        if (matcher.match(bigraph, Domain2HostRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = d2h.apply(bigraph).iterator();
            bigraph = iterator.next();
            Domain2HostRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Domain2Host Rule"));
        }

        DecapRule decap = new DecapRule(DecapRule.getRedex(signature),
                DecapRule.getReactum(signature),
                DecapRule.getInstMap());
        while (matcher.match(bigraph, DecapRule.getRedex(signature)).iterator().hasNext()) {
            Iterator<Bigraph> iterator = decap.apply(bigraph).iterator();
            bigraph = iterator.next();
            DecapRule.clearAuxProperties(bigraph);
            System.out.println(pp.prettyPrint(bigraph, "Decap"));
        }

    }
}
