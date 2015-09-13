package big.examples.security;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Site;
import big.rules.LabelledRule;

/**
 * Subclass that ensures the safety into the building. Note that the rules "enter_room()" and "leave_room()" are now replaced by 
 * "secure_enter_room()" and "secure_leave_room()".
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public abstract class SecureBuilding extends Building{
	
	
	/**
	 * Returns an array with all the rules of the BRS. You can extend it, modifying the BRS.
	 * 
	 * @return
	 */
	protected RewritingRule[] getRules(){
		RewritingRule[] rules = new RewritingRule[6];
		rules[0] = call();// System.out.println(rules[0]+" call");
		rules[1] = transf_token();//System.out.println(rules[1]+" transf_token");
		rules[2] = comp_connect();//System.out.println(rules[2]+" comp_connect");
		rules[3] = transfer_comp();//System.out.println(rules[3]+" transfer_comp");
		rules[4] = secure_enter_room();//System.out.println(rules[4]+" secure_enter_room");
		rules[5] = secure_leave_room();//System.out.println(rules[5]+" secure_leave_room");
		
		return rules;
	}
	
	
	
	/**
	 * Sets and gets the building for the problem.
	 * @return
	 */
	protected abstract Bigraph getInitBigraph();
	
	/**
	 * Rule that permits Alice to enter the room and ensures the safety of the building.
	 * @return
	 */
	protected RewritingRule secure_enter_room(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName x = builder.addOuterName("x");
		Root root = builder.addRoot();
		Node room = builder.addNode("Room", root);
		Site s0 = builder.addSite(room);
		Node alice = builder.addNode("Alice", root);
		Node phone = builder.addNode("Phone", alice);
		Site s1 = builder.addSite(phone);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		x = builder.addOuterName("x");
		root = builder.addRoot();
		room = builder.addNode("Room", root);
		s0 = builder.addSite(room);
		alice = builder.addNode("Alice", room);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {0};
		return new LabelledRule(redex, reactum, map, "secure_enter_room");
	}
	
	/**
	 * Rule that permits Alice to leave the room and ensures the safety of the building.
	 * @return
	 */
	public RewritingRule secure_leave_room(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		Node room = builder.addNode("Room", root);
		Site s0 = builder.addSite(room);
		Node alice = builder.addNode("Alice", room);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		room = builder.addNode("Room", root);
		s0 = builder.addSite(room);
		alice = builder.addNode("Alice", root);
		Node phone = builder.addNode("Phone", alice);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {0};
		return new LabelledRule(redex, reactum, map, "secure_leave_room");
	}
}
