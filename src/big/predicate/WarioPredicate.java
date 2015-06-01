/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.predicate;

import it.uniud.mads.jlibbig.core.std.Bigraph;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class WarioPredicate implements Predicate {

    private final Predicate p1, p2, p3;
    private final Bigraph B;

    public WarioPredicate(Bigraph big1, Predicate a, Predicate b, Predicate c) {
        p1 = a;
        p2 = b;
        p3 = c;
        B = big1;
    }

    @Override
    public boolean isSatisfied(Bigraph big) {
        // return "Wario";
        return false;
    }

}
