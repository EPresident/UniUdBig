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
public class NotPredicate implements Predicate {

    private final Predicate p;

    public NotPredicate(Predicate a) {
        p = a;
    }

    @Override
    public boolean isSatisfied(Bigraph big) {
        return !p.isSatisfied(big);
    }

}
