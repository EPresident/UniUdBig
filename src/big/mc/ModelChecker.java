package big.mc;

import big.predicate.Predicate;
import big.sim.BSGNode;
import big.sim.BigStateGraph;

/**
 * Simple model checker that checks a property (expressed in any type of logic) for all nodes of the graph.
 * The user can specify the way the nodes are visited.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class ModelChecker {
	
	private Visit visit;
	
	public ModelChecker(BigStateGraph bsg, Predicate predicate, Visit visit){
		this.visit = visit;
		
		this.visit.setBSG(bsg);
		this.visit.setPredicate(predicate);
	}
	
	public boolean go(){
		return visit.startFromRoot();
	}
	
	public boolean go(BSGNode first){
		return visit.start(first);
	}
}
