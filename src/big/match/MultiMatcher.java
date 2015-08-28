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
     * All Matches are returned as an Iterable: to see which Match belongs to
     * which redex/guest, the Match API must be used (in particular 
     * Match.getRedex()).
     * 
     * @param agent agent/host bigraph.
     * @param redexes redex/guest bigraph.
     * @return A list of lists of matches found.
     */
    public Iterable<? extends Match> match(Bigraph agent, Bigraph... redexes);
}
