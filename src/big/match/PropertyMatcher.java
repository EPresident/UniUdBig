package big.match;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Child;
import it.uniud.mads.jlibbig.core.std.Edge;
import it.uniud.mads.jlibbig.core.std.InnerName;
import it.uniud.mads.jlibbig.core.std.LinkEntity;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Parent;
import it.uniud.mads.jlibbig.core.std.PlaceEntity;
import it.uniud.mads.jlibbig.core.std.Point;
import it.uniud.mads.jlibbig.core.std.Port;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Site;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.ICF;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;

/**
 * This class adds to the Matcher of JLibbig the possibility to control if two bigraphs 
 * are isomorphic. The method "areMatchable" is override: here is implemented with the aim
 * of checking all the properties of two nodes.
 * The user can override it.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class PropertyMatcher extends OpenMatcher{
	
	

	/**
	 * Checks if Bigraph A and Bigraph B are isomorph.
	 * @param a
	 * @param b
	 * @return
	 */
	public boolean areIsomorph(Bigraph a, Bigraph b) {
		Solver linkSolver = new Solver("Link Graph Isomorphism");
		Solver placeSolver = new Solver("Place Graph Isomorphism");
		
		/*
		 * LinkGraph Variables and Constraints
		 */
		IntVar[] aux = new IntVar[1];
		IntVar one = VF.fixed(1, linkSolver);
		
		Collection<? extends Node> nodesA = a.getNodes();
		Collection<? extends InnerName> innersA = a.getInnerNames();
		Collection<Port> portsA = new HashSet<>();
		Collection<? extends OuterName> outersA = a.getOuterNames();
		Collection<? extends Edge> edgesA = a.getEdges();

		Collection<? extends Node> nodesB = b.getNodes();
		Collection<? extends InnerName> innersB = b.getInnerNames();
		Collection<Port> portsB = new HashSet<>();
		Collection<? extends OuterName> outersB = b.getOuterNames();
		Collection<? extends Edge> edgesB = b.getEdges();

		for (Node nodeA : a.getNodes()) {
			portsA.addAll(nodeA.getPorts());
		}

		for (Node nodeB : b.getNodes()) {
			portsB.addAll(nodeB.getPorts());
		}
		
		

		if (nodesA.size() != nodesB.size() || innersA.size() != innersB.size()
				|| outersA.size() != outersB.size()
				|| portsA.size() != portsB.size()
				|| edgesA.size() != edgesB.size()) {
			return false;
		}
		
		// Fixed Values for the structure of a's LinkGraph.
		// Map from Points of A to their IntegerVariables.
		HashMap<LinkEntity, HashMap<LinkEntity, IntVar>> linksA = new HashMap<LinkEntity, HashMap<LinkEntity, IntVar>>();
		ArrayList<IntVar> linkVarsA = new ArrayList<IntVar>();
		for (OuterName outer : outersA) {// outernames of "a"
			HashMap<LinkEntity, IntVar> map = new HashMap<LinkEntity, IntVar>();
			for (Point point : outer.getPoints()) {// points of "a"
				IntVar var = VF.fixed(1, linkSolver);
				map.put(point, var);
				linkVarsA.add(var);
			}
			linksA.put(outer, map);
		}
		for (Edge e : edgesA) {
			HashMap<LinkEntity, IntVar> map = new HashMap<LinkEntity, IntVar>();
			for (Point point : e.getPoints()) {
				IntVar var = VF.fixed(1, linkSolver);
				map.put(point, var);
				linkVarsA.add(var);
			}
			linksA.put(e, map);
		}

		HashMap<LinkEntity, HashMap<LinkEntity, IntVar>> pointsA = new HashMap<LinkEntity, HashMap<LinkEntity, IntVar>>();
		LinkedList<IntVar> pointsABVars = new LinkedList<IntVar>();
		int pointNumA = 0;
		// Inners of "a"
		for (InnerName innerA : innersA) {
			int pointNumB = 0;
			// Variables for the edges from Points of "a" to Points of "b"
			HashMap<LinkEntity, IntVar> pointsAB = new HashMap<LinkEntity, IntVar>();
			for (InnerName innerB : innersB) {
				IntVar var = VF.bool("PA_PB_" + pointNumA + "_" + pointNumB,
						linkSolver);
				pointsAB.put(innerB, var);
				pointsABVars.add(var);
				pointNumB++;
			}

			pointsA.put(innerA, pointsAB);
			pointNumA++;
		}

		// Points of "a"
		for (Port port : portsA) {
			int pointNumB = 0;
			HashMap<LinkEntity, IntVar> pointsAB = new HashMap<LinkEntity, IntVar>();
			for (Node nodeB : nodesB) {
				for (Port portB : nodeB.getPorts()) {
					IntVar var = VF.bool(
							"PA_PB_" + pointNumA + "_" + pointNumB, linkSolver);
					if (port.getNumber() != portB.getNumber()) {
						var = VF.fixed(0, linkSolver);
					}
					if( ! areMatchable( a , port.getNode() , b , portB.getNode()) ){
						var = VF.fixed(0, linkSolver);
					}
					pointsAB.put(portB, var);
					pointsABVars.add(var);
					pointNumB++;
				}
			}
			pointsA.put(port, pointsAB);
			pointNumA++;
		}

		// Outers of "b"
		HashMap<LinkEntity, HashMap<LinkEntity, IntVar>> handlesB = new HashMap<LinkEntity, HashMap<LinkEntity, IntVar>>();
		HashMap<LinkEntity, HashMap<LinkEntity, IntVar>> fluxBA = new HashMap<LinkEntity, HashMap<LinkEntity, IntVar>>();
		ArrayList<IntVar> handleABVars = new ArrayList<IntVar>();
		int handleNumB = 0;
		for (OuterName outer : outersB) {
			int handleNumA = 0;
			HashMap<LinkEntity, IntVar> handlesAB = new HashMap<LinkEntity, IntVar>();
			HashMap<LinkEntity, IntVar> handlesFluxAB = new HashMap<LinkEntity, IntVar>();
			for (OuterName outerA : outersA) {
				IntVar var = VF.enumerated("HB_HA_" + handleNumB + "_"
						+ handleNumA, 0, outer.getPoints().size(),/* portsA.size() + innersA.size(),*/
						linkSolver);
				IntVar flux = VF.bool("HB_HA_FLUX::" + handleNumB + "_"
						+ handleNumA, linkSolver);
				handlesAB.put(outerA, var);
				handlesFluxAB.put(outerA, flux);
				handleABVars.add(var);
				handleNumA++;
			}
			handlesB.put(outer, handlesAB);
			fluxBA.put(outer, handlesFluxAB);
			handleNumB++;
		}

		for (Edge eB : edgesB) {
			int handleNumA = 0;
			HashMap<LinkEntity, IntVar> handlesAB = new HashMap<LinkEntity, IntVar>();
			HashMap<LinkEntity, IntVar> handlesFluxAB = new HashMap<LinkEntity, IntVar>();
			for (Edge eA : edgesA) {
				IntVar var = VF.enumerated("HB_HA_" + handleNumB + "_"
						+ handleNumA, 0, eB.getPoints().size(),/*portsA.size() + innersA.size(),*/
						linkSolver);
				IntVar flux = VF.bool("HB_HA_FLUX_" + handleNumB + "_"
						+ handleNumA, linkSolver);
				handlesAB.put(eA, var);
				handleABVars.add(var);
				handlesFluxAB.put(eA, flux);
				handleNumA++;
			}
			handlesB.put(eB, handlesAB);
			fluxBA.put(eB, handlesFluxAB);
			handleNumB++;
		}

		/*
		 * First Constraint (M1) --Source Constraint
		 */
		int outFluxNum = 0;
		for (InnerName innerA : innersA) {
			HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
			IntVar[] outFlux = map.values().toArray(aux);
			// Constraint
			linkSolver.post(ICF.sum(outFlux, one));
			outFluxNum++;
		}

		for (Port port : portsA) {
			HashMap<LinkEntity, IntVar> map = pointsA.get(port);
			IntVar[] outFlux = map.values().toArray(aux);
			// Constraint
			linkSolver.post(ICF.sum(outFlux, one));
			outFluxNum++;
		}

		/*
		 * Second Constraint (M2) --Source Constraint
		 */
		for (InnerName innerB : innersB) {
			ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
			for (InnerName innerA : innersA) {
				IntVar var = pointsA.get(innerA).get(innerB);
				fluxIN.add(var);
			}
			// Constraint
			IntVar[] fluxINArray = fluxIN.toArray(aux);
			linkSolver.post(ICF.sum(fluxINArray, one));
		}

		for (Port portB : portsB) {
			ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
			for (Port portA : portsA) {
				IntVar var = pointsA.get(portA).get(portB);
				if (portA.getNumber() != portB.getNumber()) {
					var = VF.fixed(0, linkSolver);
				}
				fluxIN.add(var);
			}
			// Constraint
			IntVar[] fluxINArray = fluxIN.toArray(aux);
			linkSolver.post(ICF.sum(fluxINArray, one));
		}

		/*
		 * Third Constraint (M3)
		 */
		int sumNum = 0;
		for (OuterName outerB : outersB) {
			ArrayList<IntVar> fluxOUT = new ArrayList<IntVar>();
			for (InnerName innerA : innersA) {
				HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
				for (Point innerB : outerB.getPoints()) {
					if (innerB.isInnerName()) {
						IntVar var = map.get(innerB);
						fluxOUT.add(var);
					}
				}
			}
			for (Port portA : portsA) {
				HashMap<LinkEntity, IntVar> map = pointsA.get(portA);
				for (Point portB : outerB.getPoints()) {
					if (portB.isPort()) {
						IntVar var = map.get(portB);
						fluxOUT.add(var);
					}
				}
			}
			if (!fluxOUT.isEmpty()) {
				// Constraint
				IntVar[] fluxIN = handlesB.get(outerB).values().toArray(aux);
				IntVar[] fluxOUTArray = fluxOUT.toArray(aux);
				IntVar sum = VF.enumerated("SUM_" + sumNum, 0, outerB.getPoints().size(),/*innersA.size()+ portsA.size(),*/ linkSolver);
				
				linkSolver.post(ICF.sum(fluxIN, sum));
				linkSolver.post(ICF.sum(fluxOUTArray, sum));
			}

			sumNum++;
		}

		for (Edge eB : edgesB) {
			ArrayList<IntVar> fluxOUT = new ArrayList<IntVar>();
			for (InnerName innerA : innersA) {
				HashMap<LinkEntity, IntVar> map = pointsA.get(innerA);
				for (Point innerB : eB.getPoints()) {
					if (innerB.isInnerName()) {
						IntVar var = map.get(innerB);
						fluxOUT.add(var);
					}
				}
			}
			for (Port portA : portsA) {
				HashMap<LinkEntity, IntVar> map = pointsA.get(portA);
				for (Point portB : eB.getPoints()) {
					if (portB.isPort()) {
						IntVar var = map.get(portB);
						fluxOUT.add(var);
					}
				}
			}
			if (!fluxOUT.isEmpty()) {
				// Constraint
				IntVar[] fluxIN = handlesB.get(eB).values().toArray(aux);
				IntVar[] fluxOUTArray = fluxOUT.toArray(aux);
				IntVar sum = VF.enumerated("SUM_" + sumNum, 0, eB.getPoints().size() ,/*outersA.size(),*/ linkSolver);
				linkSolver.post(ICF.sum(fluxIN, sum));
				linkSolver.post(ICF.sum(fluxOUTArray, sum));
			}
			sumNum++;
		}
		
		
		
		/*
		 * Fourth Constraint (M4) --The most important: it closes the flow
		 * --Sink Constraint Seventh Constraint (M7)
		 */

		int sinkNum = 0;
		for (OuterName outerA : outersA) {
			// Left Flow
			ArrayList<IntVar> fluxLeft = new ArrayList<IntVar>();
			HashMap<LinkEntity, IntVar> map = linksA.get(outerA);
			for (Port portA : portsA) {
				IntVar var = map.get(portA);
				if (var != null) {
					fluxLeft.add(var);
				}
			}
			for (InnerName innerA : innersA) {
				IntVar var = map.get(innerA);
				if (var != null) {
					fluxLeft.add(var);
				}
			}

			// Bottom Flow
			ArrayList<IntVar> fluxBottom = new ArrayList<IntVar>();
			for (OuterName outerB : outersB) {
				HashMap<LinkEntity, IntVar> mapB = handlesB.get(outerB);
				IntVar var = mapB.get(outerA);
				if (var != null) {
					fluxBottom.add(var);
				}
				//Flow
				HashMap<LinkEntity, IntVar> flowB = fluxBA.get(outerB);
				IntVar flow = flowB.get(outerA);
				if (flow != null) {
					linkSolver.post(ICF.times(var, flow, var));
				}
			}
			//Bottom Flow
			/*for (Edge eB : edgesB) {
				HashMap<LinkEntity, IntVar> mapB = handlesB.get(eB);
				IntVar var = mapB.get(outerA);
				if (var != null) {
					fluxBottom.add(var);
				}
				//Flow
				HashMap<LinkEntity, IntVar> flowB = fluxBA.get(eB);
				IntVar flow = flowB.get(outerA);
				if (flow != null) {
					linkSolver.post(ICF.times(var, flow, var));
				}
			}*/

			if (!fluxLeft.isEmpty() && !fluxBottom.isEmpty()) {
				// Constraints M4
				IntVar[] fluxLeftArray = fluxLeft.toArray(aux);
				IntVar sumLeft = VF.enumerated("SumLeft" + sinkNum, 0, outerA.getPoints().size() ,
						/*innersA.size() + portsA.size(),*/ linkSolver);
				linkSolver.post(ICF.sum(fluxLeftArray, "=", sumLeft));

				IntVar[] fluxBottomArray = fluxBottom.toArray(aux);
				IntVar sumBottom = VF.enumerated("SumBottom" + sinkNum, 0, outerA.getPoints().size(),
						/*outersB.size() + edgesB.size(),*/ linkSolver);
				linkSolver.post(ICF.sum(fluxBottomArray, sumBottom));

				linkSolver.post(ICF.arithm(sumLeft, "=", sumBottom));

			}

			sinkNum++;
		}

		for (Edge eA : edgesA) {
			// Left Flow
			ArrayList<IntVar> fluxIN = new ArrayList<IntVar>();
			HashMap<LinkEntity, IntVar> map = linksA.get(eA);
			for (Port portA : portsA) {
				IntVar var = map.get(portA);
				if (var != null)
					fluxIN.add(var);
			}
			for (InnerName innerA : innersA) {
				IntVar var = map.get(innerA);
				if (var != null)
					fluxIN.add(var);
			}

			// Bottom Flow
			ArrayList<IntVar> fluxBottom = new ArrayList<IntVar>();
			/*for (OuterName outerB : outersB) {
				HashMap<LinkEntity, IntVar> mapB = handlesB.get(outerB);
				IntVar var = mapB.get(eA);
				if (var != null) {
					fluxBottom.add(var);
				}
				HashMap<LinkEntity, IntVar> flowB = fluxBA.get(outerB);
				IntVar flow = flowB.get(eA);
				if (flow != null) {
					linkSolver.post(ICF.times(var, flow, var));
				}
			}*/
			
			for (Edge eB : edgesB) {
				HashMap<LinkEntity, IntVar> mapB = handlesB.get(eB);
				IntVar var = mapB.get(eA);
				if (var != null) {
					fluxBottom.add(var);
				}
				HashMap<LinkEntity, IntVar> flowB = fluxBA.get(eB);
				IntVar flow = flowB.get(eA);
				if (flow != null) {
					linkSolver.post(ICF.times(var, flow, var));
				}
			}

			if (!fluxIN.isEmpty() && !fluxBottom.isEmpty()) {
				// Constraints
				IntVar[] fluxINArray = fluxIN.toArray(aux);
				IntVar sumLeft = VF.enumerated("SumLeft" + sinkNum, 0, eA.getPoints().size(),
						/*innersA.size() + pointsA.size(),*/ linkSolver);
				linkSolver.post(ICF.sum(fluxINArray, sumLeft));

				IntVar[] fluxBottomArray = fluxBottom.toArray(aux);
				IntVar sumBottom = VF.enumerated("SumBottom" + sinkNum, 0, eA.getPoints().size() ,
						/*outersB.size() + edgesB.size(),*/ linkSolver);
				linkSolver.post(ICF.sum(fluxBottomArray, sumBottom));

				linkSolver.post(ICF.arithm(sumLeft, "=", sumBottom));
			}
			sinkNum++;
		}
		
		
		/*
		 * Fifth Constraint (M5) --the total flow out of the handles of "b" must
		 * be exactly 1.
		 */
		for (OuterName outerB : outersB) {
			HashMap<LinkEntity, IntVar> map = fluxBA.get(outerB);
			IntVar[] flows = map.values().toArray(aux);
			// Constraint
			if (flows.length > 0) {
				linkSolver.post(ICF.sum(flows, one));
			}
		}
		for (Edge eB : edgesB) {
			HashMap<LinkEntity, IntVar> map = fluxBA.get(eB);
			IntVar[] flows = map.values().toArray(aux);
			// Constraint
			if (flows.length > 0) {
				linkSolver.post(ICF.sum(flows, one));
			}
		}
		
		
		/*
		 * Sixth Constraint (M6) --the total flow into the handles of "a" must
		 * be exactly 1.
		 */
		for (OuterName outerA : outersA) {
			ArrayList<IntVar> flows = new ArrayList<IntVar>();
			for (OuterName outerB : outersB) {
				HashMap<LinkEntity, IntVar> map = fluxBA.get(outerB);
				IntVar var = map.get(outerA);
				if (var != null) {
					flows.add(var);
				}
			}
			if (!flows.isEmpty()) {
				IntVar[] flowsArray = flows.toArray(aux);
				linkSolver.post(ICF.sum(flowsArray, one));
			}
		}
		for (Edge eA : edgesA) {
			ArrayList<IntVar> flows = new ArrayList<IntVar>();
			for (Edge eB : edgesB) {
				HashMap<LinkEntity, IntVar> map = fluxBA.get(eB);
				IntVar var = map.get(eA);
				if (var != null) {
					flows.add(var);
				}
			}
			if (!flows.isEmpty()) {
				IntVar[] flowsArray = flows.toArray(aux);
				linkSolver.post(ICF.sum(flowsArray, one));
			}
		}
		
		
		
		
		
		
		
		
		/*
		 * Place Graph Variables and Constraints
		 */
		
		int aNum=0;
		HashMap<PlaceEntity, HashMap<PlaceEntity, IntVar> > placeVars = new HashMap<>();
		LinkedList<PlaceEntity> aEntities = new LinkedList<>();
		LinkedList<PlaceEntity> bEntities = new LinkedList<>();
		//Fills bEntities
		for(Root bRoot : b.getRoots()){
			bEntities.add(bRoot);
		}
		for(Node bNode : b.getNodes()){
			bEntities.add(bNode);
		}
		for(Site bSite : b.getSites()){
			bEntities.add(bSite);
		}
		
		//Roots
		for(Root rootA : a.getRoots()){
			aEntities.add(rootA);
			HashMap<PlaceEntity, IntVar> bVars = new HashMap<>();
			int bNum=0;
			for(Root rootB : b.getRoots()){
				IntVar var = VF.bool("AB.root:"+aNum+"_"+bNum, placeSolver);
				bVars.put(rootB, var);
				bNum++;
			}
			placeVars.put(rootA, bVars);
			aNum++;
		}
		//Nodes
		for(Node nodeA : a.getNodes()){
			aEntities.add(nodeA);
			HashMap<PlaceEntity, IntVar> bVars = new HashMap<>();
			int bNum=0;
			for(Node nodeB : b.getNodes()){
				//Properties Check
				IntVar var;
				if(areMatchable(a, nodeA, b, nodeB)){
					var = VF.bool("AB.node:"+aNum+"_"+bNum, placeSolver);
				}else{
					var = VF.fixed(false, placeSolver);
				}
				bVars.put(nodeB, var);
				bNum++;
			}
			placeVars.put(nodeA, bVars);
			aNum++;
		}
		//Site
		for(Site siteA : a.getSites()){
			aEntities.add(siteA);
			HashMap<PlaceEntity, IntVar> bVars = new HashMap<>();
			int bNum=0;
			for(Site siteB : b.getSites()){
				IntVar var = VF.bool("AB.site:"+aNum+"_"+bNum, placeSolver);
				bVars.put(siteB, var);
				bNum++;
			}
			placeVars.put(siteA, bVars);
			aNum++;
		}
		
		
		
		/*
		 * First Constraint : MP1
		 */
		for(PlaceEntity aEntity : aEntities){
			HashMap<PlaceEntity, IntVar> bVars = placeVars.get(aEntity);
			ArrayList<IntVar> outVars = new ArrayList<>();
			for(PlaceEntity bEntity : bEntities){
				IntVar var = bVars.get(bEntity);
				if(var != null){
					outVars.add(var);
				}
			}
			if (!outVars.isEmpty()) {
				IntVar[] outV = outVars.toArray(aux);
				placeSolver.post(ICF.sum(outV, VF.fixed(1, placeSolver)));
			}
		}
		
		
		
		/*
		 * Second Constraint : MP2
		 */
		for(PlaceEntity bEntity : bEntities){
			ArrayList<IntVar> inVars = new ArrayList<IntVar>();
			for(PlaceEntity aEntity : aEntities){
				HashMap<PlaceEntity, IntVar> aVars = placeVars.get(aEntity);
				IntVar var = aVars.get(bEntity);
				if(var != null){
					inVars.add(var);
				}
			}
			if(!inVars.isEmpty()){
				IntVar[] inV = inVars.toArray(aux);
				placeSolver.post(ICF.sum(inV, VF.fixed(1, placeSolver)));
			}
		}
		
		
		
		/*
		 * Third Constraint : P6
		 */
		//Nodes
		for(Node aNode : a.getNodes()){
			Parent aPar = aNode.getParent();
			HashMap<PlaceEntity, IntVar> mapChild = placeVars.get(aNode);
			HashMap<PlaceEntity, IntVar> mapParent = placeVars.get(aPar);
			for(Child bChild : b.getNodes()){
				Parent bPar = bChild.getParent();
				IntVar varChild = mapChild.get(bChild);
				IntVar varPar = mapParent.get(bPar);
				if(varChild != null && varPar != null){
					placeSolver.post(ICF.arithm(varChild, "<=", varPar));
				}
			}
			for(Child bChild : b.getSites()){
				Parent bPar = bChild.getParent();
				IntVar varChild = mapChild.get(bChild);
				IntVar varPar = mapParent.get(bPar);
				if(varChild != null && varPar != null){
					placeSolver.post(ICF.arithm(varChild, "<=", varPar));
				}
			}
		}
		//Sites
		for(Site aNode : a.getSites()){
			Parent aPar = aNode.getParent();
			HashMap<PlaceEntity, IntVar> mapChild = placeVars.get(aNode);
			HashMap<PlaceEntity, IntVar> mapParent = placeVars.get(aPar);
			for(Child bChild : b.getSites()){
				Parent bPar = bChild.getParent();
				IntVar varChild = mapChild.get(bChild);
				IntVar varPar = mapParent.get(bPar);
				if(varChild != null && varPar != null){
					placeSolver.post(ICF.arithm(varChild, "<=", varPar));
				}
			}
		}
		
		
		
		
		/*
		 * Fourth Constraint : P10 
		 */
		//Nodes
		int aPar = 0;
		for(Node aNode : a.getNodes()){
			HashMap<PlaceEntity, IntVar> map = placeVars.get(aNode);
			Collection<? extends Child> aChildren = aNode.getChildren();
			int bPar = 0;
			for(Node bNode : b.getNodes()){
				IntVar varParent = map.get(bNode);
				if(varParent != null){
					Collection<? extends Child> bChildren = bNode.getChildren();
					ArrayList<IntVar> childrenVars = new ArrayList<>();
					for(PlaceEntity aEntity : aChildren){
						HashMap<PlaceEntity, IntVar> mapChild = placeVars.get(aEntity);
						for(PlaceEntity bEntity : bChildren){
							IntVar var = mapChild.get(bEntity);
							if(var!=null){
								childrenVars.add(var);
							}
						}
					}
					//Constraint P10
					IntVar cardinalityA = VF.fixed(aChildren.size(), placeSolver);
					IntVar firstMember = VF.enumerated("AB_child_parent:"+aPar+"_"+bPar, 0, aChildren.size() , placeSolver);
					placeSolver.post( ICF.times(varParent, cardinalityA, firstMember) );
					
					//Constraint P11
					IntVar cardinalityB = VF.fixed(bChildren.size(), placeSolver);
					IntVar firstMember2 = VF.enumerated("AB_child_parent:"+aPar+"_"+bPar, 0, bChildren.size() , placeSolver);
					placeSolver.post( ICF.times(varParent, cardinalityB, firstMember2) );
					
					if(!childrenVars.isEmpty()){
						IntVar[] varsChildren = childrenVars.toArray(aux);
						IntVar sumChildren = VF.enumerated("Sum_"+bPar, 0, aEntities.size(), placeSolver);
						placeSolver.post(ICF.sum(varsChildren, sumChildren));
						
						placeSolver.post( ICF.arithm(firstMember, "<=" ,sumChildren) );//P10
						placeSolver.post( ICF.arithm(firstMember2, "<=" ,sumChildren) );//P11
					}
					
				}
				bPar ++;
			}
			aPar++;
		}
		
		//Roots
		for(Root aRoot : a.getRoots()){
			HashMap<PlaceEntity, IntVar> map = placeVars.get(aRoot);
			Collection<? extends Child> aChildren = aRoot.getChildren();
			int bPar = 0;
			for(Root bRoot : b.getRoots()){
				IntVar varParent = map.get(bRoot);
				if(varParent != null){
					Collection<? extends Child> bChildren = bRoot.getChildren();
					ArrayList<IntVar> childrenVars = new ArrayList<>();
					for(PlaceEntity aEntity : aChildren){
						HashMap<PlaceEntity, IntVar> mapChild = placeVars.get(aEntity);
						for(PlaceEntity bEntity : bChildren){
							IntVar var = mapChild.get(bEntity);
							if(var!=null){
								childrenVars.add(var);
							}
						}
					}
					//Constraint P10
					IntVar cardinality = VF.fixed(aChildren.size(), placeSolver);
					IntVar firstMember = VF.enumerated("AB_child_parent:"+aPar+"_"+bPar, 0, aChildren.size() , placeSolver);
					placeSolver.post( ICF.times(varParent, cardinality, firstMember) );
					
					//Constraint P11
					IntVar cardinalityB = VF.fixed(bChildren.size(), placeSolver);
					IntVar firstMember2 = VF.enumerated("AB_child_parent:"+aPar+"_"+bPar, 0, bChildren.size() , placeSolver);
					placeSolver.post( ICF.times(varParent, cardinalityB, firstMember2) );
					
					if(!childrenVars.isEmpty()){
						IntVar[] varsChildren = childrenVars.toArray(aux);
						IntVar sumChildren = VF.enumerated("Sum_"+bPar, 0, aEntities.size(), placeSolver);
						placeSolver.post(ICF.sum(varsChildren, sumChildren));
						
						placeSolver.post( ICF.arithm(firstMember, "<=" ,sumChildren) );//P10
						placeSolver.post( ICF.arithm(firstMember2, "<=" ,sumChildren) );//P11
					}
				}
				bPar ++;
			}
			aPar++;
		}
		
		/*
		ArrayList<IntVar> listVars = new ArrayList<>();
		for(Variable variable : linkSolver.getVars()){
			listVars.add( (IntVar) variable );
		}
		IntVar[] varsLinkSolver = listVars.toArray(aux);
		//linkSolver.set( ISF.custom( new VariableSelectorWithTies( new FirstFail(), new Random(123L)), new IntDomainMin(), varsLinkSolver ));
		linkSolver.set( ISF.lexico_LB(varsLinkSolver) );
		*/
		
		return linkSolver.findSolution() && placeSolver.findSolution();
	}
	
	
	
	
	@Override
	public boolean areMatchable(Bigraph a, Node aNode, Bigraph b, Node bNode){
		if(aNode.getControl() != bNode.getControl()){
			return false;
		}
		boolean matchable = false;
		for(Property<?> aProp : aNode.getProperties()){
			for(Property<?> bProp : bNode.getProperties()){
				if( aProp.get() != null && bProp.get() != null ){
					if(aProp.getName().equals(bProp.getName()) && !aProp.getName().equals("Owner")){
						if(aProp.get().equals(bProp.get())){
							matchable = true;
						}
					}
				}
			}
		}
		return matchable;
	}
	
	
}
