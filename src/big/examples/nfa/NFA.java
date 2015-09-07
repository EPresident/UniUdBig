package big.examples.nfa;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.Collection;

import big.mc.ModelChecker;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.sim.BreadthFirstSim;

/**
 * An abstract class for the bigraphical encoding of a Non-Deterministic Finite Automata (NFA). It gives the
 * signature and the rule to get the encoding. Moreover, it provides a Model Checker to decide if the 
 * NFA recognizes a specific word.
 * 
 * Extend this class to build your own NFA. In particular, implement the "setNFA()" method to build 
 * the bigraphical encoding of your NFA with its language.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public abstract class NFA {
	
	protected BigraphBuilder builder;
	
	public NFA(){
		Signature signature = NFA.getNFASignature();
		this.builder = new BigraphBuilder(signature);
	}
	
	/**
	 * Signature for the bigraphical encoding of NFAs.
	 * 
	 * @return the signature of the NFA bigraphical encoding
	 */
	private static Signature getNFASignature(){
		Control ctrl_state = new Control("state",true,1);
		Control ctrl_trans = new Control("trans",true,2);
		Control ctrl_active = new Control("active",true,0);
		Control ctrl_final = new Control("final",true,0);
		Control ctrl_str = new Control("string", true, 0);
		Control ctrl_input = new Control("input",true,3);
		return new Signature(ctrl_state, ctrl_trans, ctrl_active, ctrl_final, ctrl_str, ctrl_input);
	}
	
	/**
	 * Public method that returns the SAME signature of the builder.
	 */
	public Signature getSignature(){
		return this.builder.getSignature();
	}
	
	/**
	 * Checks if this NFA recognizes the word "str".
	 * 
	 * @param str
	 * @return
	 */
	public boolean recognizes(String str){
		Bigraph nfa = getBigProblem(str);
		
		RewritingRule rule = getNFARule();
		RewritingRule[] rules = {rule};
		
		Bigraph aim = getAim(this.builder.getSignature());
		Predicate trueP = new TruePredicate();
		Predicate atom = new WarioPredicate(aim, trueP, trueP, trueP);
		
		ModelChecker mc = new ModelChecker(new BreadthFirstSim(nfa, rules), atom);
		return mc.modelCheck();
	}
	
	

	/**
	 * It builds the problem, that is: the NFA with the string, in the same bigraph.
	 * 
	 * @param signature
	 * @return
	 */
	private Bigraph getBigProblem(String str){
		setNFA();
		setString(str);
		
		return builder.makeBigraph();
	}
	
	
	
	/**
	 * Builds the bigraphical encoding of the string.
	 * @param str
	 * @param builder
	 * @return
	 */
	private void setString(String str){
		//str += "&";
		Root root = (Root) builder.getRoots().toArray()[0];
		Node stringNode = builder.addNode("string", root);
		char[] strChar = str.toCharArray();
		Node[] nodes = new Node[strChar.length];
		Collection<? extends OuterName> outers = builder.getOuterNames();
		//Nodes creation
		for(int i=0; i<str.length(); i++){
			for(OuterName out : outers){
				if(out.getName().toCharArray()[0] == strChar[i]){
					Node nodeI = builder.addNode("input", stringNode);
					nodes[i] = nodeI;
				}
			}
		}
		//Edges creation
		for(int i=0; i<str.length(); i++){
			for(OuterName out : outers){
				if(out.getName().toCharArray()[0] == strChar[i]){
					builder.relink(out, nodes[i].getPort(2));
				}
				if(i!=0){
					builder.relink(nodes[i].getPort(1), nodes[i-1].getPort(0));
				}
			}
			
		}
	}
	
	
	/**
	 * Builds the final state for the model checker. The word is accepted if and only if the final bigraph
	 * is composed by an epsilon-state which contains the active-node.
	 * 
	 * @param signature
	 * @return
	 */
	private Bigraph getAim(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root root = builder.addRoot();
		builder.addNode("string", root);//Node String
		OuterName x = builder.addOuterName("x");
		Node state = builder.addNode("state", root, x);
		builder.addNode("active",state);//Node active
		builder.addNode("final",state);//Node final
		builder.addSite(state);//Site 0
		return builder.makeBigraph();
	}
	
	
	
	/**
	 * The only rewriting rule for the NFA bigraphical encoding.
	 * 
	 * @param signature
	 * @return
	 */
	private RewritingRule getNFARule(){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		//Redex
		Root root = builder.addRoot();
		OuterName x = builder.addOuterName("x");
		OuterName sigma = builder.addOuterName("sigma");
		OuterName y = builder.addOuterName("y");
		OuterName z = builder.addOuterName("z");
		Node s1 = builder.addNode("state",root, x);
		builder.addSite(s1);//Site 0
		builder.addNode("active", s1);//Node active
		builder.addNode("trans", s1, sigma,z);//Node t (trans)
		Node str = builder.addNode("string", root);
		builder.addSite(str);//Site 1
		Node c = builder.addNode("input", str, y);
		builder.relink(sigma, c.getPort(2));
		Node s2 = builder.addNode("state", root, z);
		builder.addSite(s2);//Site 2
		Bigraph redex = builder.makeBigraph();
		
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		x = builder.addOuterName("x");
		sigma = builder.addOuterName("sigma");
		y = builder.addOuterName("y");
		z = builder.addOuterName("z");
		s1 = builder.addNode("state",root, x);
		builder.addSite(s1);//Site 0
		builder.addNode("trans", s1, sigma,z);//Node t (trans)
		str = builder.addNode("string", root);
		builder.addSite(str);//Site 1
		s2 = builder.addNode("state", root, z);
		builder.addSite(s2);//Site 2
		builder.addNode("active", s2);//Node active
		Bigraph reactum = builder.makeBigraph();
		
		//Instantiation Map
		int[] map = {0,1,2};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	
	
	
	/**
	 * Sets the NFA.
	 */
	protected abstract void setNFA();
	
}