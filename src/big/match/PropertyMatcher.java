package big.match;

import it.uniud.mads.jlibbig.core.attachedProperties.Property;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * In this type of matcher, two nodes are matchable if and only if they have the same properties.
 * In JLibbig, the properties are ordered pair: <name, value>. Two properties are equals if name1=name2
 * and value1=value2.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class PropertyMatcher extends Matcher implements MultiMatcher{
	
	@Override
	public boolean areMatchable(Bigraph a, Node aNode, Bigraph b, Node bNode){
		if(aNode.getControl() != bNode.getControl()){
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

    @Override
    public Iterable<? extends Match> match(Bigraph agent, Bigraph... redexes) {
        LinkedList<Match> out = new LinkedList<>();
        for(Bigraph redex : redexes){
            for(Match m : match(agent,redex)){
                out.add(m);
            }
        }
        return out;
    }
	
	
}
