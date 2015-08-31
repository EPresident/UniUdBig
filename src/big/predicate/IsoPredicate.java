package big.predicate;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import big.iso.Isomorphism;

/**
 * Predicate that return true if and only if the two bigraphs are isomorphs.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class IsoPredicate implements Predicate{
	
	private Bigraph aim;
	private Isomorphism iso;
	
	public IsoPredicate(Bigraph aim){
		this.aim = aim;
		this.iso = new Isomorphism();
	}
	
	public boolean isSatisfied(Bigraph big){
		return iso.areIsomorph(aim, big);
	}
	
}
