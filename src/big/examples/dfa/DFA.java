package big.examples.dfa;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import it.uniud.mads.jlibbig.core.std.Site;

import java.util.Collection;

import big.mc.ModelChecker;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.sim.BreadthFirstSim;

/**
 * An abstract class for the bigraphical encoding of a Deterministic Finite Automata (DFA). It gives the
 * signature and the rule to get the encoding. Moreover, it provides a Model Checker to decide if the 
 * word specify in a string belongs to the language of the DFA.
 * 
 * Extend this class to build your own DFA. Implement the "setDFA()" method to build the bigraphical 
 * encoding of your DFA with its language.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public abstract class DFA {
	
	protected BigraphBuilder builder;
	
	public DFA(){
		Signature signature = DFA.getDFASignature();
		this.builder = new BigraphBuilder(signature);
	}
	
	/**
	 * Signature for the bigraphical encoding of DFAs.
	 * 
	 * @return the signature of the DFA bigraphical encoding
	 */
	public static Signature getDFASignature(){
		Control ctrl_state = new Control("state",true,1);
		Control ctrl_trans = new Control("trans",true,2);
		Control ctrl_active = new Control("active",true,0);
		Control ctrl_input = new Control("input",true,3);
		return new Signature(ctrl_state, ctrl_trans, ctrl_active, ctrl_input);
	}
	
	/**
	 * Checks if the word "str" belongs to the language of the DFA.
	 * 
	 * @param str
	 * @return
	 */
	public boolean belongsTo(String str){
		Bigraph dfa = getBigProblem(str);
		
		RewritingRule rule = getDFARule();
		RewritingRule[] rules = {rule};
		
		Bigraph aim = getAim(this.builder.getSignature());
		Predicate trueP = new TruePredicate();
		Predicate atom = new WarioPredicate(aim, trueP, trueP, trueP);
		
		ModelChecker mc = new ModelChecker(new BreadthFirstSim(dfa, rules), atom);
		return mc.modelCheck();
	}
	
	

	/**
	 * It builds the problem, that is: the DFA with the string, in the same bigraph.
	 * 
	 * @param signature
	 * @return
	 */
	private Bigraph getBigProblem(String str){
		setDFA();
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
		str += "&";
		Root root = (Root) builder.getRoots().toArray()[0];
		char[] strChar = str.toCharArray();
		Node[] nodes = new Node[strChar.length];
		Collection<? extends OuterName> outers = builder.getOuterNames();
		//Nodes creation
		for(int i=0; i<str.length(); i++){
			for(OuterName out : outers){
				if(out.getName().toCharArray()[0] == strChar[i]){
					Node nodeI = builder.addNode("input", root);
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
	private static Bigraph getAim(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root root = builder.addRoot();
		OuterName x = builder.addOuterName("x");
		Node sE = builder.addNode("state", root, x);
		Node active = builder.addNode("active",sE);
		return builder.makeBigraph();
	}
	

	
	/**
	 * The only rewriting rule for the DFA bigraphical encoding.
	 * 
	 * @param signature
	 * @return
	 */
	private RewritingRule getDFARule(){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		//Redex
		Root root = builder.addRoot();
		OuterName x = builder.addOuterName("x");
		OuterName sigma = builder.addOuterName("sigma");
		OuterName y = builder.addOuterName("y");
		OuterName z = builder.addOuterName("z");
		Node s1 = builder.addNode("state",root, x);
		Site site1 = builder.addSite(s1);
		Node active = builder.addNode("active", s1);
		Node t = builder.addNode("trans", s1, sigma,z);
		Node c = builder.addNode("input", root, y);
		builder.relink(sigma, c.getPort(2));
		Node s2 = builder.addNode("state", root, z);
		Site site2 = builder.addSite(s2);
		Bigraph redex = builder.makeBigraph();
		
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		x = builder.addOuterName("x");
		sigma = builder.addOuterName("sigma");
		y = builder.addOuterName("y");
		z = builder.addOuterName("z");
		s1 = builder.addNode("state",root, x);
		site1 = builder.addSite(s1);
		t = builder.addNode("trans", s1, sigma,z);
		s2 = builder.addNode("state", root, z);
		site2 = builder.addSite(s2);
		active = builder.addNode("active", s2);
		Bigraph reactum = builder.makeBigraph();
		
		//Instantiation Map
		int[] map = {0,1};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	
	
	
	/**
	 * Sets the DFA.
	 */
	protected abstract void setDFA();
	
}
