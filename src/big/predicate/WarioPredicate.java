/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.predicate;

import java.util.Iterator;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Match;
import big.match.PropertyMatcher;
import it.uniud.mads.jlibbig.core.std.Matcher;

/**
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public class WarioPredicate implements Predicate {

    private final Predicate p1, p2, p3;
    private final Bigraph B;
    private static final PropertyMatcher PROP_MATCHER = new PropertyMatcher(); 
    private final Matcher matcher;

    
    public WarioPredicate(Bigraph big1, Predicate a, Predicate b, Predicate c) {
        this(Matcher.DEFAULT, big1, a, b, c);
    }
    
    public WarioPredicate(Matcher m, Bigraph big1, Predicate a, Predicate b, Predicate c) {
        p1 = a;
        p2 = b;
        p3 = c;
        B = big1;
        matcher = m;
    }

    @Override
    public boolean isSatisfied(Bigraph big) {
        for(Match m : matcher.match(big, B)){
            if(p1.isSatisfied(m.getContext()) && p2.isSatisfied(m.getRedex()) && p3.isSatisfied(m.getParam())){
            	return true;
            }
        }
        return false;
    }

}
