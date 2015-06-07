package big.mc;

import big.predicate.Predicate;
import big.sim.BSGNode;
import big.sim.BigStateGraph;

/**
 * Interface for any type of visit, like DFS, BFS or one user-defined.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public interface Visit {
	
	public boolean start(BSGNode first);
	
	public boolean startFromRoot();
	
	public void setBSG(BigStateGraph bsg);
	
	public void setPredicate(Predicate predicate);
}
