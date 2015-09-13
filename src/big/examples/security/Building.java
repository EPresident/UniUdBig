package big.examples.security;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import it.uniud.mads.jlibbig.core.std.Site;

import java.util.LinkedList;

import big.mc.ModelChecker;
import big.predicate.NotPredicate;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.rules.LabelledRule;
import big.sim.BreadthFirstSim;

/**
 * Class for modelling a secure building and testing safety properties in it. At any time you extend this class and build your own building. 
 * Moreover, you can set your own rules that could become the building a secure one.
 * The following are the methods that you can override:
 * - getInitBigraph()
 * - getRules()
 * - getAim()
 * - ... all the rules ...
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public abstract class Building {
	
	protected BigraphBuilder builder;
	
	public Building(){
		Signature signature = Building.setSignature(); 
		this.builder = new BigraphBuilder(signature);
	}
	
	/**
	 * Signature for the problem
	 * @return
	 */
	private static Signature setSignature(){
		Control build_ctrl = new Control("Building", true, 0);
		Control room_ctrl = new Control("Room", true, 0);
		Control alice_ctrl = new Control("Alice", true, 0);
		Control bob_ctrl = new Control("Bob", true, 0);
		Control comp_ctrl = new Control("Computer", true, 1);
		Control phone_ctrl = new Control("Phone", false, 1);
		Control token_ctrl = new Control("Token", false, 0);
		return new Signature(build_ctrl, room_ctrl, alice_ctrl, bob_ctrl, comp_ctrl, phone_ctrl, token_ctrl);
	}
	
	/**
	 * Public method that returns the SAME signature of the builder.
	 */
	public Signature getSignature(){
		return this.builder.getSignature();
	}
	
	/**
	 * Return the SAME model checker used for computing the graph.
	 * @return
	 */
	public ModelChecker getMC(){
		Bigraph building = getInitBigraph();
		RewritingRule[] rules = getRules();
		
		Predicate trueP = new TruePredicate();
		Predicate falseP = new NotPredicate(trueP); //Necessary to compute the entire graph.
		ModelChecker modelC = new ModelChecker(new BreadthFirstSim(building, rules), falseP);
		
		modelC.modelCheck();
		
		return modelC;
	}
	
	/**
	 * Checks if the tokens in the building are safe.
	 * 
	 * @return
	 */
	public boolean isSecure(){
		Bigraph building = getInitBigraph();
		RewritingRule[] rules = getRules();
		
		Bigraph aim = getAim();
		Predicate trueP = new TruePredicate();
		Predicate atom = new WarioPredicate(aim, trueP, trueP, trueP);
		ModelChecker mc = new ModelChecker(new BreadthFirstSim(building, rules), atom);
		
		boolean result = !mc.modelCheck();
		
		return result;
	}
	
	
	/**
	 * Sets and gets the building for the problem.
	 * @return
	 */
	protected abstract Bigraph getInitBigraph();
	
	
	/**
	 * Returns an array with all the rules of the BRS. You can extend it, modifying the BRS.
	 * 
	 * @return
	 */
	protected RewritingRule[] getRules(){
		LinkedList<RewritingRule> list = new LinkedList<>();
		list.add(call());
		list.add(transf_token());
		list.add(comp_connect());
		list.add(transfer_comp());
		list.add(enter_room());
		list.add(leave_room());
		RewritingRule[] aux = {call()};
		RewritingRule[] rules = list.toArray(aux);
		return rules;
	}
	
	
	/**
	 * Rule that established a link between two phones. One of these must have a token.
	 * 
	 * @return 
	 */
	protected RewritingRule call(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName pl = builder.addOuterName("phoneLink");
		Root r1 = builder.addRoot();
		Node phone1 = builder.addNode("Phone", r1);
		Node token = builder.addNode("Token", phone1);
		Root r2 = builder.addRoot();
		Node phone2 = builder.addNode("Phone",r2);
		Site site = builder.addSite(phone2);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		pl = builder.addOuterName("phoneLink");
		r1 = builder.addRoot();
		phone1 = builder.addNode("Phone", r1, pl);
		token = builder.addNode("Token", phone1);
		r2 = builder.addRoot();
		phone2 = builder.addNode("Phone",r2, pl);
		site = builder.addSite(phone2);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {0};
		return new LabelledRule(redex, reactum, map, "call");
	}
	
	
	/**
	 * Rule that transfer the token from one phone to the other. The two phone must be linked.
	 * 
	 * @return
	 */
	protected RewritingRule transf_token(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName pl = builder.addOuterName("phoneLink");
		Root r1 = builder.addRoot();
		Node phone1 = builder.addNode("Phone", r1, pl);
		Node token = builder.addNode("Token", phone1);
		Root r2 = builder.addRoot();
		Node phone2 = builder.addNode("Phone",r2, pl);
		Site site = builder.addSite(phone2);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		pl = builder.addOuterName("phoneLink");
		r1 = builder.addRoot();
		phone1 = builder.addNode("Phone", r1);
		r2 = builder.addRoot();
		phone2 = builder.addNode("Phone",r2);
		token = builder.addNode("Token", phone2);
		site = builder.addSite(phone2);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {0};
		return new LabelledRule(redex, reactum, map, "transf_token");
	}
	
	
	/**
	 * Rule for connecting a phone to a computer.
	 * 
	 * @return
	 */
	protected RewritingRule comp_connect(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName x = builder.addOuterName("x");
		OuterName y = builder.addOuterName("y");
		Root root = builder.addRoot();
		Node room = builder.addNode("Room",root);
		Node computer = builder.addNode("Computer", room, x);
		Node alice = builder.addNode("Alice", room);
		Node token = builder.addNode("Token", computer);
		Node phone = builder.addNode("Phone",alice,y);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		x = builder.addOuterName("x");
		y = builder.addOuterName("y");
		root = builder.addRoot();
		room = builder.addNode("Room",root);
		computer = builder.addNode("Computer", room, x);
		alice = builder.addNode("Alice", room);
		token = builder.addNode("Token", computer);
		phone = builder.addNode("Phone",alice,x);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {};
		return new LabelledRule(redex, reactum, map, "comp_connect");
	}
	
	/**
	 * Rule that transfer a token from a computer to a phone (linked to the computer).
	 * @return
	 */
	protected RewritingRule transfer_comp(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName x = builder.addOuterName("x");
		Root r1 = builder.addRoot();
		Node computer = builder.addNode("Computer", r1, x);
		Node token = builder.addNode("Token", computer);
		Root r2 = builder.addRoot();
		Node phone = builder.addNode("Phone",r2,x);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		x = builder.addOuterName("x");
		r1 = builder.addRoot();
		computer = builder.addNode("Computer", r1, x);
		r2 = builder.addRoot();
		phone = builder.addNode("Phone",r2);
		token = builder.addNode("Token", phone);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {};
		return new LabelledRule(redex,reactum,map,"transfer_comp");
	}
	
	
	/**
	 * Rule that permits Alice to enter the room.
	 * 
	 * @return
	 */
	protected RewritingRule enter_room(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		Node room = builder.addNode("Room", root);
		Site s0 = builder.addSite(room);
		Node alice = builder.addNode("Alice", root);
		Site s1 = builder.addSite(alice);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		BigraphBuilder builder2 = new BigraphBuilder(this.builder.getSignature());
		root = builder2.addRoot();
		room = builder2.addNode("Room", root);
		s0 = builder2.addSite(room);
		alice = builder2.addNode("Alice", room);
		s1 = builder2.addSite(alice);
		Bigraph reactum = builder2.makeBigraph();
		
		int[] map = {0,1};
		return new LabelledRule(redex, reactum, map, "enter_room");
	}
	
	/**
	 * Rule that permits Alice to leave the room.
	 * 
	 * @return
	 */
	protected RewritingRule leave_room(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		Node room = builder.addNode("Room", root);
		Site s1 = builder.addSite(room);
		Node alice = builder.addNode("Alice", room);
		Site s0 = builder.addSite(alice);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		room = builder.addNode("Room", root);
		s1 = builder.addSite(room);
		alice = builder.addNode("Alice", root);
		s0 = builder.addSite(alice);
		Bigraph reactum = builder.makeBigraph();
		
		int[] map = {0,1};
		return new LabelledRule(redex, reactum, map, "leave_room");
	}
	
	
	/**
	 * Returns the bigraph for the WarioPredicate that the model checker will verify.
	 * 
	 * @return
	 */
	protected Bigraph getAim(){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		OuterName x = builder.addOuterName("x");
		Root root = builder.addRoot();
		Node bob = builder.addNode("Bob", root);
		Node phone = builder.addNode("Phone", bob, x);
		Node token = builder.addNode("Token", phone);
		Site site = builder.addSite(phone);
		return builder.makeBigraph();
	}
	
	
}








