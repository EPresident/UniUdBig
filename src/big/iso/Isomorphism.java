package big.iso;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Handle;
import it.uniud.mads.jlibbig.core.std.Matcher;
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

import big.match.OpenMatcher;

/**
 * Class that checks if two bigraphs are isomorph.
 * Here the term "isomorph" takes this meaning: two bigraphs A and B are isomorph if and only if 
 * they are support equivalent.
 * 
 * The problem is treated as a flux problem.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Isomorphism {
	
	private OpenMatcher matcher;
	
	public Isomorphism(OpenMatcher matcher){
		this.matcher = matcher;
	}
	
	public boolean areIsomorph(Bigraph a, Bigraph b){
		Solver linkSolver = new Solver("Link Graph Isomorphism");
		Solver placeSolver = new Solver("Place Graph Isomorphism");
		//Aux Variables:
		BoolVar zeroVar = (BoolVar) VF.fixed(0, linkSolver);
		BoolVar oneVar = (BoolVar) VF.fixed(1, linkSolver);
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
				BoolVar ppVar = VF.bool("PP___"+aPoint.toString()+"___"+bPoint.toString(), linkSolver);
				if(aPoint.isInnerName() && bPoint.isPort())
					ppVar = (BoolVar) VF.fixed(0, linkSolver);
				if(aPoint.isPort() && bPoint.isInnerName())
					ppVar = (BoolVar) VF.fixed(0, linkSolver);
				if(aPoint.isPort() && bPoint.isPort()){
					Port aPort = (Port) aPoint;
					Port bPort = (Port) bPoint;
					if(aPort.getNumber()!=bPort.getNumber())
						ppVar = (BoolVar) VF.fixed(0, linkSolver);
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
				BoolVar hhVar = VF.bool("HH___"+aHandle.toString()+"___"+bHandle.toString(), linkSolver);
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
				if(aCardinality != bCardinality){
					//hhVar = (BoolVar) VF.fixed(0, linkSolver);
					linkSolver.post(ICF.arithm(hhVar, "=", zeroVar));
				}
			}
		}
		
		//Second Constraint : link graph structure preservation
		for(Point aPoint : aPoints){
			HashMap<Point, BoolVar> col = pointVars.get(aPoint);
			for(Point bPoint : bPoints){
				BoolVar ppVar = col.get(bPoint);
				BoolVar hhVar = handleVars.get(aPoint.getHandle()).get(bPoint.getHandle());
				linkSolver.post(ICF.arithm(ppVar, "<=", hhVar));
			}
		}
		
		//Third Constraint : output flow constraint from A points
		for(Point aPoint : aPoints){
			HashMap<Point, BoolVar> col = pointVars.get(aPoint);
			BoolVar[] outVars = col.values().toArray(aux);
			linkSolver.post(ICF.sum(outVars, oneVar));
		}
		
		//Fourth Constraint : input flow constraint in B points
		for(Point bPoint : bPoints){
			LinkedList<BoolVar> invVars = new LinkedList<>();
			for(Point aPoint: aPoints){
				HashMap<Point, BoolVar> col = pointVars.get(aPoint);
				invVars.add(col.get(bPoint));
			}
			BoolVar[] colVars = invVars.toArray(aux);
			linkSolver.post(ICF.sum(colVars, oneVar));
		}
		
		//Fifth Constraint : output flow from A handles
		for(Handle aHandle : aHandles){
			HashMap<Handle,BoolVar> col = handleVars.get(aHandle);
			BoolVar[] colVars = col.values().toArray(aux);
			linkSolver.post(ICF.sum(colVars, oneVar));
		}
		
		//Sixth Constraint : input flow in B handles
		for(Handle bHandle : bHandles){
			LinkedList<BoolVar> invVars = new LinkedList<>();
			for(Handle aHandle : aHandles){
				HashMap<Handle, BoolVar> col = handleVars.get(aHandle);
				invVars.add(col.get(bHandle));
			}
			BoolVar[] colVars = invVars.toArray(aux);
			linkSolver.post(ICF.sum(colVars, oneVar));
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
					BoolVar var = VF.bool("NN___"+aNode.toString()+"___"+bNode.toString(), placeSolver);
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
					//Property check
					if(aNode.isNode() && bNode.isNode()){
						Node aaNode = (Node) aNode;
						Node bbNode = (Node) bNode;
						if(!matcher.areMatchable(a, aaNode, b, bbNode)){
							BoolVar propVar = placeVars.get(h).get(aNode).get(bNode);
							placeSolver.post(ICF.arithm(propVar, "=", zeroVar));
						}
					}
					if(aNode.isParent() && bNode.isParent()){//if h<depth
						Parent aParent = (Parent) aNode;
						Parent bParent = (Parent) bNode;
						if(aParent.getChildren().size() != bParent.getChildren().size()){
							BoolVar auxVar = placeVars.get(h).get(aNode).get(bNode);
							placeSolver.post(ICF.arithm(auxVar, "=", zeroVar));
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
							placeSolver.post(ICF.arithm(var1, "<=", var2));
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
				placeSolver.post(ICF.sum(varArray, oneVar));
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
				placeSolver.post(ICF.sum(varArray, oneVar));
			}
		}
		
		return linkSolver.findSolution() && placeSolver.findSolution();
	}
	
}