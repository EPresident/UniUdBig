package big.iso;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Node;

import java.util.HashSet;
import java.util.Set;

public class PropertyIsomorphism extends Isomorphism{
	
	/**
	 * Checks is two nodes, the first of "a" and the second of "b", are matchable. This version checks also
	 * the properties of the two nodes. It's a specialization of the class "Isomorphism".
	 * 
	 * @param a first bigraph
	 * @param aNode node of the first bigraph
	 * @param b second bigraph
	 * @param bNode node of the first bigraph
	 * @return
	 */
	protected boolean areMatchable(Bigraph a, Node aNode, Bigraph b, Node bNode){
		if(!aNode.getControl().equals(bNode.getControl())){
			return false;
		}
		
		if(aNode.getProperties().size() != bNode.getProperties().size())
			return false;
		
		Set<Property<?>> aProps = new HashSet<>(aNode.getProperties());
		Set<Property<?>> bProps = new HashSet<>(bNode.getProperties());
		aProps.remove(aNode.getProperty("Owner"));
		bProps.remove(bNode.getProperty("Owner"));
		
		return aProps.equals(bProps);
	}
	
	
}
