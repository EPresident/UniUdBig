package big.examples.philosophers;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import big.mc.ModelChecker;
import big.predicate.NotPredicate;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.sim.BreadthFirstSim;

/**
 * Class for modeling the problem of the "dining philosophers" with a user-defined number of
 * philosophers.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Dinner {
	
	protected BigraphBuilder builder;
	protected int num_nodes = 0;
	
	public Dinner(){
		Signature signature = Dinner.getProblemSignature();
		this.builder = new BigraphBuilder(signature);
	}
	
	/**
	 * generates the signature for the bigraphical encoding of the "Dining philosophers problem"
	 * @return
	 */
	private static Signature getProblemSignature(){
		Control fork_ctrl = new Control("F", false, 1);
		Control phil_ctrl = new Control("P", false, 2);
		return new Signature(fork_ctrl, phil_ctrl);
	}
	
	/**
	 * returns the SAME signature of the builder.
	 * @return
	 */
	public Signature getSignature(){
		return this.builder.getSignature();
	}
	
	/**
	 * Creates the problem, that is the philosophers and the forks.
	 * @param n
	 * @return the bigraphical encoding of the problem
	 */
	private Bigraph getProblem(int n){
		Root root = builder.addRoot();
		Node[] nodes = new Node[n];
		OuterName[] outers = new OuterName[n];
		for(int i=0; i<n; i++){
			OuterName fL = builder.addOuterName("F"+i);
			outers[i] = fL;
			Node phil = builder.addNode("P", root, fL);
			nodes[i] = phil;
			Node fork = builder.addNode("F", root, fL);
			if(i>0){
				builder.relink(fL, nodes[i-1].getPort(1));
			}
			if(i==n-1){
				builder.relink(outers[0], phil.getPort(1));
			}
		}
		return builder.makeBigraph();
	}
	
	
	/**
	 * Rule for taking the left fork
	 * @return
	 */
	private RewritingRule takeLeftFork(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node fork = builder.addNode("F", root,lf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		fork = builder.addNode("F", phil,lf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	/**
	 * Rule for taking the right fork
	 * @return
	 */
	private RewritingRule takeRightFork(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node leftFork = builder.addNode("F", phil, lf);
		Node rightFork = builder.addNode("F", root, rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		leftFork = builder.addNode("F", phil, lf);
		rightFork = builder.addNode("F", phil, rf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	/**
	 * Rule for dropping the left fork
	 * @return
	 */
	private RewritingRule dropLeftFork(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node leftFork = builder.addNode("F", phil, lf);
		Node rightFork = builder.addNode("F", phil, rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		leftFork = builder.addNode("F", root, lf);
		rightFork = builder.addNode("F", phil, rf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {};
		return new RewritingRule(redex, reactum, map);
	}
	
	/**
	 * Rule for dropping the right fork
	 * @return
	 */
	private RewritingRule dropRightFork(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node rightFork = builder.addNode("F", phil, rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		rightFork = builder.addNode("F", root, rf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {};
		return new RewritingRule(redex, reactum, map);
	}
	
	/**
	 * Makes use of MCbig to check if there are deadlocks. It's a bit tricky: it always return false if there is a deadlock, but if there
	 * are not any ones, then it may not terminated. Therefore, this is only a semi-decision procedure.
	 * @param n
	 * @return
	 */
	public boolean deadlockDanger(int n){
		Bigraph problem = getProblem(n);
		
		RewritingRule[] rules = new RewritingRule[4];
		rules[0] = takeLeftFork();
		rules[1] = takeRightFork();
		rules[2] = dropLeftFork();
		rules[3] = dropRightFork();
		
		Predicate trueP = new TruePredicate();
		Predicate falseP = new NotPredicate(trueP);
		ModelChecker mc = new ModelChecker(new BreadthFirstSim(problem,rules), falseP);
		
		boolean result = mc.modelCheck();
		num_nodes = mc.getGraph().getGraphSize();
		
		return result;
	}
	

	/**
	 * Returns the number of nodes of the state graph used by the model checker
	 * @return
	 */
	public int getGraphSize(){
		if(num_nodes == 0){
			throw new RuntimeException("This method must be called after deadlockDanger() ");
		}
		return num_nodes;
	}
	
	
}
