/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.predicate;

import it.uniud.mads.jlibbig.core.std.Bigraph;

/**
 * Interface that represents logical predicates about bigraphs.
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public interface Predicate {
    public boolean isSatisfied(Bigraph big);
}
