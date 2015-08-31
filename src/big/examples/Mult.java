/*
 * Copyright (C) 2015 Elia Calligaris <calligaris.elia@spes.uniud.it> 
 * and Luca Geatti <geatti.luca@spes.uniud.it>
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

package big.examples;


import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.Iterator;

import big.prprint.BigPPrinterVeryPretty;

/**
 * Esempio di moltiplicazione implementata con bigrafi.
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 */
public class Mult {

    public static Signature signature;

    public static void main(String[] args) {
    	signature = Utils.getMultSignature();
    	RewritingRule[] rules = Utils.getMultRules(signature);
//recursive
		RewritingRule mult_recursive = rules[0];
//base
        RewritingRule mult_base = rules[1];
//reality
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        for (int i = 0; i < 2; i++) {
            builder.addNode("one", num1);
        }
        Node num2 = builder.addNode("num", mul);
        for (int i = 0; i < 4; i++) {
            builder.addNode("one", num2);
        }
        Bigraph bigraph = builder.makeBigraph();
//See "RewritingRule.java" line 240.
//Iterator<Bigraph> iterator = mult_recursive.apply(bigraph).iterator();
        Matcher matcher = new Matcher();
        while (matcher.match(bigraph, mult_recursive.getRedex()).iterator().hasNext()
                && !matcher.match(bigraph, mult_base.getRedex()).iterator().hasNext()) {
            Iterator<Bigraph> iterator = mult_recursive.apply(bigraph).iterator();
            bigraph = iterator.next();
            System.out.println("caso ricorsivo");
        }
        if (matcher.match(bigraph, mult_base.getRedex()).iterator().hasNext()) {
            bigraph = mult_base.apply(bigraph).iterator().next();
            System.out.println("caso base");
        }
        BigPPrinterVeryPretty printer = new BigPPrinterVeryPretty();
        System.out.println(printer.prettyPrint(bigraph, "Result"));
    }


}
