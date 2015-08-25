/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package big.match;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Match;

/**
 * Interface that allows to match a host with multiple guests.
 *
 * @author EPresident <prez_enquiry@hotmail.com>
 */
public interface MultiMatcher {

    /**
     * Match an agent with multiple redexes.
     *
     * @param agent agent/host bigraph.
     * @param redexes redex/guest bigraph.
     * @return A list of lists of matches found.
     */
    public Iterable<Iterable<? extends Match>> match(Bigraph agent, Bigraph... redexes);
}
