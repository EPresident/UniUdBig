package big.examples.philosophers;

import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import it.uniud.mads.jlibbig.core.std.Site;
import big.iso.PropertyIsomorphism;
import big.mc.ModelChecker;
import big.predicate.IsoPredicate;
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
public class DinnerNoDead {
	
	protected BigraphBuilder builder;
	protected int num_nodes = 0;
	
	public DinnerNoDead(){
		Signature signature = DinnerNoDead.getProblemSignature();
		this.builder = new BigraphBuilder(signature);
	}
	
	/**
	 * generates the signature for the bigraphical encoding of the "Dining philosophers problem"
	 * @return
	 */
	private static Signature getProblemSignature(){
		Control fork_ctrl = new Control("F", true, 1);
		Control phil_ctrl = new Control("P", true, 2);
		Control last_ctrl = new Control("L", true, 0);
		return new Signature(fork_ctrl, phil_ctrl, last_ctrl);
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
				builder.addNode("L", phil);
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
		Site site = builder.addSite(phil);
		Node leftFork = builder.addNode("F", phil, lf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		site = builder.addSite(phil);
		leftFork = builder.addNode("F", root, lf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {0};
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
		Site site = builder.addSite(phil);
		Node rightFork = builder.addNode("F", phil, rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		site = builder.addSite(phil);
		rightFork = builder.addNode("F", root, rf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {0};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	/**
	 * Rule for the last node
	 * @return
	 */
	private RewritingRule takeFirstLast(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node last = builder.addNode("L", phil);
		Node fork = builder.addNode("F", root,rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		last = builder.addNode("L", phil);
		fork = builder.addNode("F", phil,rf);
		Bigraph reactum = builder.makeBigraph();
		//Rewriting Rule
		int[] map = {};
		return new RewritingRule(redex, reactum, map);
	}
	
	
	/**
	 * Rule for the last node
	 * @return
	 */
	private RewritingRule takeSecondLast(){
		//Redex
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		OuterName lf = builder.addOuterName("lf");
		OuterName rf = builder.addOuterName("rf");
		Node phil = builder.addNode("P", root, lf, rf);
		Node last = builder.addNode("L", phil);
		Node leftFork = builder.addNode("F", root, lf);
		Node rightFork = builder.addNode("F", phil, rf);
		Bigraph redex = builder.makeBigraph();
		//Reactum
		builder = new BigraphBuilder(this.builder.getSignature());
		root = builder.addRoot();
		lf = builder.addOuterName("lf");
		rf = builder.addOuterName("rf");
		phil = builder.addNode("P", root, lf, rf);
		last = builder.addNode("L", phil);
		leftFork = builder.addNode("F", phil, lf);
		rightFork = builder.addNode("F", phil, rf);
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
		
		RewritingRule[] rules = new RewritingRule[6];
		InstantiationMap map1 = new InstantiationMap(1,0);
		InstantiationMap map2 = new InstantiationMap(0);
		rules[0] = new DiningRule(takeLeftFork().getRedex(),takeLeftFork().getReactum(),map2);
		rules[1] = new DiningRule(takeRightFork().getRedex(),takeRightFork().getReactum(),map2);
		rules[2] = new DiningRule(dropLeftFork().getRedex(),dropLeftFork().getReactum(),map1);
		rules[3] = new DiningRule(dropRightFork().getRedex(),dropRightFork().getReactum(),map1);
		rules[4] = new DiningRule(takeFirstLast().getRedex(),takeFirstLast().getReactum(),map2);
		rules[5] = new DiningRule(takeSecondLast().getRedex(),takeSecondLast().getReactum(),map2);
		

		PropertyIsomorphism isoP = new PropertyIsomorphism();
		Predicate atom = new IsoPredicate(getAim(n));
		ModelChecker mc = new ModelChecker(new BreadthFirstSim(problem,rules,isoP), atom);
		
		boolean result = mc.modelCheck();
		num_nodes = mc.getGraph().getGraphSize();
		
		return !result;
	}
	
	
	private Bigraph getAim(int n){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		Node[] nodes = new Node[n];
		OuterName[] outers = new OuterName[n];
		for(int i=0; i<n; i++){
			OuterName fL = builder.addOuterName("F"+i);
			outers[i] = fL;
			Node phil = builder.addNode("P", root, fL);
			phil.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("PhilName", "P_"+i)));
			nodes[i] = phil;
			Node fork = builder.addNode("F", phil, fL);
			fork.attachProperty(new SharedProperty<String>(
					new SimpleProperty<String>("ForkName", "F_"+i)));
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
