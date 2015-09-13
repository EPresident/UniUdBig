package big.rules;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.RewritingRule;

/**
 * Class for a particular type of RewritingRules. They are normal rules but with a label for their name. This is particularly useful 
 * for the model checker: if a user wants to visit the graph, he can choose the path following the name of the rules. 
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class LabelledRule extends RewritingRule{
	
	protected String str;
	
	public LabelledRule(Bigraph redex, Bigraph reactum, InstantiationMap map, String string){
		super(redex, reactum, map);
		this.str = string;
	}
	
	public LabelledRule(Bigraph redex, Bigraph reactum, int[] map, String string){
		super(redex, reactum, map);
		this.str = string;
	}
	
	public LabelledRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
		super(redex, reactum, map);
		this.str = "noRuleName";
	}
	
	public LabelledRule(Bigraph redex, Bigraph reactum, int[] map){
		super(redex, reactum, map);
		this.str = "noRuleName";
	}
	
	/**
	 * Return the name of the rule.
	 * @return
	 */
	public String getName(){
		return this.str;
	}
	
	/**
	 * Instead of printing the name of the object (name@123456), it prints the name of the property.
	 */
	public String toString(){
		return this.str;
	}
	
}
