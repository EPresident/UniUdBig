package big.mc;

import big.predicate.Predicate;
import big.sim.BSGNode;
import big.sim.BSGNode.BSGLink;
import big.sim.BigStateGraph;


/**
 * This is a VISIT, not a strategy. 
 * There are two ways (methods) to start a visit:
 * 		- startFromRoot(), it starts the visit from the root of the BigStateGraph. It produces a tree.
 * 		- start(BSGNode), it starts the visit from the node specified. It produces a forest (generally).
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class DFSVisit implements Visit{
	
	private BigStateGraph bsg;
	private Predicate predicate;
	
	public DFSVisit(){}
	
	
	/**
	 * The visit begins from the specified node.
	 * 
	 * @param first The node from which you want to start
	 * @return true if at least one node satisfies the predicates, false otherwise.
	 */
	public boolean start(BSGNode first){
		if(DFS_Visit(first))
			return true;
		for(BSGNode node : bsg.getNodes()){
			if(checkPredicates(node))
				return true;
			if( DFS_Visit( node ))
				return true;
		}
		return false;
	}
	
	/**
	 * The visit begins from the root of the BigStateGraph.
	 * 
	 * @return true if at least one node satisfies the predicates, false otherwise.
	 */
	public boolean startFromRoot(){
		if(checkPredicates( bsg.getRoot() )){
			return true;
		}
		return DFS_Visit( bsg.getRoot() );
	}
	
	/**
	 * Recursive method for visiting in deep.
	 * 
	 * @param node
	 * @return
	 */
	private boolean DFS_Visit( BSGNode node ){
		node.setColor('G');
		for(BSGLink link : node.getLinks()){
			BSGNode next = link.destNode;
			if(next.getColor() == 'W'){
				//Checks the predicates
				boolean sat = checkPredicates(next);
				if( sat){
					System.out.println("ok");
					return true;
				}
				if( DFS_Visit(next))
					return true;
			}
		}
		node.setColor('B');
		return false;
	}
	
	
	/**
	 * Makes white all nodes for a possible future visit.
	 */
	public void resetColors(){
		for(BSGNode node : bsg.getNodes()){
			node.setColor('W');
		}
	}
	
	
	
	/**
	 * Checks if the node satisfies the predicates.
	 * 
	 * @param node
	 * @return
	 */
	private boolean checkPredicates(BSGNode node){
		return predicate.isSatisfied(node.getState());
	}
	
	
	/**
	 * Sets the graph on which the visit will be applied.
	 * 
	 * @param bsg
	 */
	public void setBSG(BigStateGraph bsg){
		this.bsg = bsg;
	}
	
	
	/**
	 * Sets the praticates that will be tested.
	 * 
	 * @param predicate
	 */
	public void setPredicate(Predicate predicate){
		this.predicate = predicate;
	}
	
}
