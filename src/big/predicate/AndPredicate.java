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
public class AndPredicate implements Predicate{
    private final Predicate p1,p2;
    
    public AndPredicate(Predicate a, Predicate b){
        p1=a;
        p2=b;
    }
    
    @Override
    public boolean isSatisfied(Bigraph big) {
        return p1.isSatisfied(big) && p2.isSatisfied(big);
    }
    
}
