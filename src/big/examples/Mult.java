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
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.Iterator;

/**
 * Esempio di moltiplicazione implementata con bigrafi.
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 */
public class Mult {

    public static Signature signature;

    public static void main(String[] args) {
		Control mul_ctrl = new Control("mul", true, 0);
		Control num_ctrl = new Control("num", false, 0);
		Control one_ctrl = new Control("one", false, 0);
		signature = new Signature(mul_ctrl, num_ctrl, one_ctrl);
//recursive
        Bigraph redex_recursive = makeRedexRecursive();
        Bigraph reactum_recursive = makeReactumRecursive();
        int[] map_r = {0, 1, 2, 1};
        InstantiationMap map_recursive = new InstantiationMap(3, map_r);
        RewritingRule mult_recursive = new RewritingRule(redex_recursive, reactum_recursive, map_recursive);
//base
        Bigraph redex_base = makeRedexBase();
        Bigraph reactum_base = makeReactumBase();
        int[] map_b = {0};
        InstantiationMap map_base = new InstantiationMap(2, map_b);
        RewritingRule mult_base = new RewritingRule(redex_base, reactum_base, map_base);
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
        while (matcher.match(bigraph, redex_recursive).iterator().hasNext()
                && !matcher.match(bigraph, redex_base).iterator().hasNext()) {
            Iterator<Bigraph> iterator = mult_recursive.apply(bigraph).iterator();
            bigraph = iterator.next();
            System.out.println("caso ricorsivo");
        }
        if (matcher.match(bigraph, redex_base).iterator().hasNext()) {
            bigraph = mult_base.apply(bigraph).iterator().next();
            System.out.println("caso base");
        }
        System.out.println(bigraph + "\n\n ok");
    }

    public static Bigraph makeRedexRecursive() {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        builder.addSite(num1);
        Node one = builder.addNode("one", num1);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(num2);
        builder.addSite(mul);
        return builder.makeBigraph();
    }

    public static Bigraph makeReactumRecursive() {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        builder.addSite(num1);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(num2);
        builder.addSite(mul);
        builder.addSite(mul);
        return builder.makeBigraph();
    }

    public static Bigraph makeRedexBase() {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(mul);
        builder.addSite(num2);
        return builder.makeBigraph();
    }

    public static Bigraph makeReactumBase() {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node num1 = builder.addNode("num", r1);
        builder.addSite(num1);
        return builder.makeBigraph();
    }

}
