package big.examples.philosophers;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.LinkedList;
import java.util.List;

import big.rules.RewRuleWProps;

public class DiningRule extends RewRuleWProps {

	private final static LinkedList<String> auxProperties;
	private static Signature signature;

	static {
		auxProperties = new LinkedList<>();
		auxProperties.add("NodeType");
	}

	public DiningRule(Bigraph redex, Bigraph reactum, InstantiationMap map) {
		super(redex, reactum, map);
	}

	protected List<String> getAuxProperties() {
		return auxProperties;
	}
	
	public Signature getSignature(){
		return signature;
	}

	@Override
	public void instantiateReactumNode(Node original, Node instance, Match match) {
		for (Property p : original.getProperties()) {// Original = node of the
														// reactum
			Node[] array = rr.get(p.get().toString());
			if (array != null) {
				Node n = array[1]; // Node of the redex
				if (n != null) {
					Node img = match.getImage(n);// Node of the original bigraph
					if (img != null) {
						copyProperties(img, instance);
					}
				}
			}

		}

	}
}
