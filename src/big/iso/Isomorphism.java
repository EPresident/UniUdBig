package big.iso;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.Parent;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Site;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.BoolVar;
import org.chocosolver.solver.variables.VF;

/**
 * Class that checks if two bigraphs are isomorphs.
 * Here the term "isomorph" takes this meaning: two bigraphs A and B are isomorph if and only if 
 * they are support equivalent.
 * 
 * The problem is treated as a flow problem.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Isomorphism{
	
	private final static boolean SIM = true;
	private long startLoadingTime = 0;
	private long endLoadingTime = 0;
	private long startWorkingTime = 0;
	private long endWorkingTime = 0;
	
	
	public boolean areIsomorph(Bigraph a, Bigraph b){
		if(SIM)
			this.startLoadingTime = System.nanoTime();
		Solver solver = new Solver("Bigraph Isomorphism");
		//Aux Variables:
		BoolVar zeroVar = (BoolVar) VF.fixed(0, solver);
		BoolVar oneVar = (BoolVar) VF.fixed(1, solver);
		BoolVar[] aux = new BoolVar[1];
		
		/*
		 * Link Graph Variables
		 */
		//bigraph A
		Collection<? extends Node> aNodes = a.getNodes();
		Collection<Point> aPoints = new HashSet<>();//points are unordered
		aPoints.addAll(a.getInnerNames());
		for(Node node : aNodes)
			aPoints.addAll(node.getPorts());
		Collection<Handle> aHandles = new HashSet<>();//handle are unordered
		aHandles.addAll(a.getOuterNames());
		aHandles.addAll(a.getEdges());
		//bigraph B
		Collection<? extends Node> bNodes = b.getNodes();
		Collection<Point> bPoints = new HashSet<>();//points are unordered
		bPoints.addAll(b.getInnerNames());
		for(Node node : bNodes)
			bPoints.addAll(node.getPorts());
		Collection<Handle> bHandles = new HashSet<>();//handle are unordered
		bHandles.addAll(b.getOuterNames());
		bHandles.addAll(b.getEdges());
		
		//Preliminary Check
		if(aNodes.size() != bNodes.size())
			return false;
		if(aPoints.size() != bPoints.size())
			return false;
		if(aHandles.size() != bHandles.size())
			return false;
			
		//Variables from points of A to points of B
		HashMap<Point,HashMap<Point, BoolVar>> pointVars = new HashMap<>();
		for(Point aPoint : aPoints){
			HashMap<Point, BoolVar> col = new HashMap<>();
			for(Point bPoint : bPoints){
				BoolVar ppVar = VF.bool("PP___"+aPoint.toString()+"___"+bPoint.toString(), solver);
				if(aPoint.isInnerName() && bPoint.isPort())
					ppVar = (BoolVar) VF.fixed(0, solver);
				if(aPoint.isPort() && bPoint.isInnerName())
					ppVar = (BoolVar) VF.fixed(0, solver);
				if(aPoint.isPort() && bPoint.isPort()){//Not the same number of port
					Port aPort = (Port) aPoint;
					Port bPort = (Port) bPoint;
					if(aPort.getNumber()!=bPort.getNumber())
						ppVar = (BoolVar) VF.fixed(0, solver);
				}
				col.put(bPoint, ppVar);
			}
			pointVars.put(aPoint, col);
		}
		
		//Variables from handle of A to handles of B
		HashMap<Handle, HashMap<Handle,BoolVar>> handleVars = new HashMap<>();
		for(Handle aHandle : aHandles){
			HashMap<Handle, BoolVar> col = new HashMap<>();
			for(Handle bHandle : bHandles){
				BoolVar hhVar = VF.bool("HH___"+aHandle.toString()+"___"+bHandle.toString(), solver);
				if(aHandle.isEdge() && bHandle.isOuterName())
					hhVar = (BoolVar) VF.fixed(0, solver);
				if(bHandle.isOuterName() && bHandle.isEdge())
					hhVar = (BoolVar) VF.fixed(0, solver);
				col.put(bHandle, hhVar);
			}
			handleVars.put(aHandle, col);
		}
		
		
		
		/*
		 * Link Graph constraints
		 */
		
		//First Constraint : link graph structure preservation
		for(Handle aHandle : aHandles){
			int aCardinality = aHandle.getPoints().size();
			HashMap<Handle, BoolVar> col = handleVars.get(aHandle);
			for(Handle bHandle : bHandles){
				int bCardinality = bHandle.getPoints().size();
				BoolVar hhVar = col.get(bHandle);
				if(aCardinality != bCardinality){//not the same number of children
					//hhVar = (BoolVar) VF.fixed(0, solver);
					solver.post(ICF.arithm(hhVar, "=", zeroVar));
				}
				/*if( (aHandle.isEdge() && bHandle.isOuterName()) || (aHandle.isOuterName() && bHandle.isEdge()) ){//Outername vs Edge
					solver.post(ICF.arithm(hhVar, "=", zeroVar));
				}*/
			}
		}
		
		//Second Constraint : link graph structure preservation
		for(Point aPoint : aPoints){
			HashMap<Point, BoolVar> col = pointVars.get(aPoint);
			for(Point bPoint : bPoints){
				BoolVar ppVar = col.get(bPoint);
				BoolVar hhVar = handleVars.get(aPoint.getHandle()).get(bPoint.getHandle());
				solver.post(ICF.arithm(ppVar, "<=", hhVar));
				/*if( (aPoint.isPort() && bPoint.isInnerName()) || (aPoint.isInnerName() && bPoint.isPort()) ){//Points vs Innernames
					solver.post(ICF.arithm(ppVar, "=", zeroVar));
				}*/
			}
		}
		
		//Third Constraint : output flow constraint from A points
		for(Point aPoint : aPoints){
			HashMap<Point, BoolVar> col = pointVars.get(aPoint);
			BoolVar[] outVars = col.values().toArray(aux);
			solver.post(ICF.sum(outVars, oneVar));
		}
		
		//Fourth Constraint : input flow constraint in B points
		for(Point bPoint : bPoints){
			LinkedList<BoolVar> invVars = new LinkedList<>();
			for(Point aPoint: aPoints){
				HashMap<Point, BoolVar> col = pointVars.get(aPoint);
				invVars.add(col.get(bPoint));
			}
			BoolVar[] colVars = invVars.toArray(aux);
			solver.post(ICF.sum(colVars, oneVar));
		}
		
		//Fifth Constraint : output flow from A handles
		for(Handle aHandle : aHandles){
			HashMap<Handle,BoolVar> col = handleVars.get(aHandle);
			BoolVar[] colVars = col.values().toArray(aux);
			solver.post(ICF.sum(colVars, oneVar));
		}
		
		//Sixth Constraint : input flow in B handles
		for(Handle bHandle : bHandles){
			LinkedList<BoolVar> invVars = new LinkedList<>();
			for(Handle aHandle : aHandles){
				HashMap<Handle, BoolVar> col = handleVars.get(aHandle);
				invVars.add(col.get(bHandle));
			}
			BoolVar[] colVars = invVars.toArray(aux);
			solver.post(ICF.sum(colVars, oneVar));
		}
		
		
		
		
		
		/*
		 * Place Graph Variables
		 */
		//bigraph a
		Collection<? extends Root> aRoots = a.getRoots();
		Collection<? extends Site> aSites = a.getSites();
		//bigraph b
		Collection<? extends Root> bRoots = b.getRoots();
		Collection<? extends Site> bSites = b.getSites();
		
		//preliminary check
		if(aRoots.size() != bRoots.size())
			return false;
		if(aSites.size() != bSites.size())
			return false;
		//variables between nodes at the same height
		BigDFS aVisit = new BigDFS(a);
		HashMap<Integer, LinkedList<PlaceEntity>> aNodesHeight = aVisit.getNodesAtHeight();
		BigDFS bVisit = new BigDFS(b);
		HashMap<Integer, LinkedList<PlaceEntity>> bNodesHeight = bVisit.getNodesAtHeight();
		
		//if the two heights are different, then there must be not a isomorphism.
		int aHeight = Collections.max(aNodesHeight.keySet());
		int bHeight = Collections.max(bNodesHeight.keySet());
		if(aHeight != bHeight)
			return false;
		//Creating the variables
		HashMap<Integer, HashMap<PlaceEntity, HashMap<PlaceEntity, BoolVar>>> placeVars = new HashMap<>();
		for(Integer h : aNodesHeight.keySet()){
			HashMap<PlaceEntity, HashMap<PlaceEntity, BoolVar>> varMap = new HashMap<>();
			for(PlaceEntity aNode : aNodesHeight.get(h)){
				HashMap<PlaceEntity, BoolVar> col = new HashMap<>();
				for(PlaceEntity bNode : bNodesHeight.get(h)){
					BoolVar var = VF.bool("NN___"+aNode.toString()+"___"+bNode.toString(), solver);
					col.put(bNode, var);
					varMap.put(aNode, col);
					placeVars.put(h, varMap);
				}
			}
		}
		
		
		/*
		 * Place Graph Constraints
		 */
		
		//First Constraint : base case
		for(Integer h : aNodesHeight.keySet()){
			for(PlaceEntity aNode : aNodesHeight.get(h)){
				for(PlaceEntity bNode : bNodesHeight.get(h)){
					if(aNode.isNode() && bNode.isNode()){
						Node aaNode = (Node) aNode;
						Node bbNode = (Node) bNode;
						//Property check
						if(!areMatchable(a, aaNode, b, bbNode)){
							BoolVar propVar = placeVars.get(h).get(aNode).get(bNode);
							solver.post(ICF.arithm(propVar, "=", zeroVar));
						}
						//Control check
						if(!aaNode.getControl().equals(bbNode.getControl())){
							BoolVar propVar = placeVars.get(h).get(aNode).get(bNode);
							solver.post(ICF.arithm(propVar, "=", zeroVar));
						}
					}
					if(aNode.isParent() && bNode.isParent()){//if h<depth
						Parent aParent = (Parent) aNode;
						Parent bParent = (Parent) bNode;
						if(aParent.getChildren().size() != bParent.getChildren().size()){
							BoolVar auxVar = placeVars.get(h).get(aNode).get(bNode);
							solver.post(ICF.arithm(auxVar, "=", zeroVar));
						}
					}
				}
			}
		}
		
		
		//Second Constraint : inductive step
		for(Integer h : placeVars.keySet()){
			if(h>0){
				for(PlaceEntity aNode : aNodesHeight.get(h)){
					for(PlaceEntity bNode : bNodesHeight.get(h)){
						if(aNode.isChild() && bNode.isChild()){
							Child aChild = (Child) aNode;
							Child bChild = (Child) bNode;
							BoolVar var1 = placeVars.get(h).get(aChild).get(bChild);
							BoolVar var2 = placeVars.get(h-1).get(aChild.getParent()).get(bChild.getParent());
							solver.post(ICF.arithm(var1, "<=", var2));
						}
					}
				}
			}
		}
		
		
		//Third Constraint : output flow from A nodes
		for(Integer h : placeVars.keySet()){
			for(PlaceEntity aNode : aNodesHeight.get(h)){
				HashMap<PlaceEntity, BoolVar> col = placeVars.get(h).get(aNode);
				LinkedList<BoolVar> varList = new LinkedList<>();
				for(PlaceEntity bNode : col.keySet()){
					varList.add(col.get(bNode));
				}
				BoolVar[] varArray = varList.toArray(aux);
				solver.post(ICF.sum(varArray, oneVar));
			}
		}
		
		//Fourth Constraint : input flow to B nodes
		for(Integer h : placeVars.keySet()){
			for(PlaceEntity bNode : bNodesHeight.get(h)){
				LinkedList<BoolVar> varList = new LinkedList<>();
				for(PlaceEntity aNode : aNodesHeight.get(h)){
					HashMap<PlaceEntity, BoolVar> col = placeVars.get(h).get(aNode);
					BoolVar element = col.get(bNode);
					if(bNode != null){
						varList.add(element);
					}
				}
				BoolVar[] varArray = varList.toArray(aux);
				solver.post(ICF.sum(varArray, oneVar));
			}
		}
		
		
		
		/*
		 * Coherence Constraints
		 */
		for(Integer h : placeVars.keySet()){
			for(PlaceEntity aNode : aNodesHeight.get(h)){
				for(PlaceEntity bNode : bNodesHeight.get(h)){
					if(aNode.isNode() && bNode.isNode()){
						Node aN = (Node) aNode;
						Node bN = (Node) bNode;
						if(aN.getPorts().size() == bN.getPorts().size()){
							for(int i=0; i<aN.getPorts().size(); i++){
								Port aP = aN.getPort(i);
								Port bP = bN.getPort(i);
								BoolVar pVar = pointVars.get(aP).get(bP);
								BoolVar nVar = placeVars.get(h).get(aN).get(bN);
								solver.post(ICF.arithm(pVar, "=", nVar));
							}
						}
					}
				}
			}
		}
		if(SIM)
			this.endLoadingTime = System.nanoTime();
		
		if(SIM)
			this.startWorkingTime = System.nanoTime();
		boolean result = solver.findSolution();
		if(SIM)
			this.endWorkingTime = System.nanoTime();
		
		return result;
	}
	
	
	
	/**
	 * Checks is two nodes, the first of "a" and the second of "b", are matchable. The default version
	 * checks only if the two controls are equals. At any time, you can extend this class and override
	 * this method to specify the way in which the two nodes are matchable.
	 * 
	 * @param a first bigraph
	 * @param aNode node of the first bigraph
	 * @param b second bigraph
	 * @param bNode node of the first bigraph
	 * @return
	 */
	protected boolean areMatchable(Bigraph a, Node aNode, Bigraph b, Node bNode){
		 return aNode.getControl().equals(bNode.getControl());
	}
	
	
	/**
	 * Return the loading and working time, only in the case this class is on SIM modality.
	 * 
	 * @return [0]--> loading time
	 * 		   [1]--> work time
	 */
	public long[] getTimes(){
		if(!SIM)
			throw new RuntimeException("The Isomorphism class is not on SIM modality.");
		long[] times = new long[2];
		times[0] = endLoadingTime - startLoadingTime;
		times[1] = endWorkingTime - startWorkingTime;
		return times;
	}
	
}