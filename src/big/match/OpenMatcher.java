package big.match;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;

/**
 * Auxiliary and temporary class for implementing the "areMatchable()" method.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public abstract class OpenMatcher extends Matcher{
	
	public abstract boolean areMatchable(Bigraph a, Node aNode, Bigraph b, Node bNode);
	
}
