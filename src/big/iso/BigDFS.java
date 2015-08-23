package big.iso;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Parent;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Does a DFS Visit on the place graph of the specified bigraph. The only one public method is "getNodesAtHeight()", that returns an hashmap
 * that has for each height the nodes that are in that height in the tree/forest.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class BigDFS {
	
	private HashMap<PlaceEntity,Integer> distance;
	private HashMap<PlaceEntity,Character> color;	
	private Bigraph bigraph;
	
	public BigDFS(Bigraph bigraph){
		this.distance = new HashMap<>();
		this.color = new HashMap<>();
		this.bigraph = bigraph;
		for(PlaceEntity node : bigraph.getRoots() ){
			distance.put(node, 0);
			color.put(node, 'B');
		}
		for(PlaceEntity node : bigraph.getNodes() ){
			distance.put(node, 0);
			color.put(node, 'B');
		}
		for(PlaceEntity node : bigraph.getSites() ){
			distance.put(node, 0);
			color.put(node, 'B');
		}
	}
	
	private HashMap start(){
		for(PlaceEntity root : bigraph.getRoots()){
			if(color.get(root)=='B'){
				int d=0;
				DFSVisit(root, d);
			}
		}
		return this.distance;
	}
	
	private void DFSVisit(PlaceEntity node, Integer d){
		color.put(node, 'G');
		distance.put(node, d);
		d++;
		if(node.isParent()){
			Parent current = (Parent) node;
			for(PlaceEntity near : current.getChildren()){
				if(color.get(near)=='B'){
					DFSVisit(near, d);
				}
			}
		}
		color.put(node, 'N');
	}
	
	
	public HashMap<Integer, LinkedList<PlaceEntity>> getNodesAtHeight(){
		start();
		HashMap<Integer, LinkedList<PlaceEntity>> reversMap = new HashMap<>();
		for(PlaceEntity node : distance.keySet()){
			LinkedList<PlaceEntity> list  = reversMap.get(distance.get(node));
			if(list != null){
				list.add(node);
			}else{
				LinkedList<PlaceEntity> nList = new LinkedList<>();
				nList.add(node);
				reversMap.put(distance.get(node), nList);
			}
		}
		return reversMap;
	}
	
}
