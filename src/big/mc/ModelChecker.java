package big.mc;

import big.brs.RuleApplication;
import big.bsg.BigStateGraph;
import big.predicate.Predicate;
import big.sim.Sim;

/**
 * Simple model checker that checks a property (expressed in the logic of the
 * big.predicate package) for all nodes of the graph. The user can specify the
 * way the nodes are visited. The strategy employed by the BRS affects the
 * performance of the model checker. Currently, if one state satisfies the given
 * predicate, the model checker returns true.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 * @author Elia Calligaris <calligaris.elia@spes.uniud.it>
 */
public class ModelChecker {

	private final Sim sim;
	private final Predicate predicate;
	private final static int DEFAULT_MAX_APPLICATIONS = 100000;

	/**
	 * @param s
	 *            Simulator to employ.
	 * @param p
	 *            The predicate whose validity must be checked.
	 */
	public ModelChecker(Sim s, Predicate p) {
		sim = s;
		predicate = p;
	}

	/**
	 * Start checking if the predicate is satisfied by some state. Rule
	 * applications are capped to the default value.
	 *
	 * @return true if the predicate holds in at least one state, false
	 *         otherwise.
	 */
	public boolean modelCheck() {
		return modelCheck(DEFAULT_MAX_APPLICATIONS);
	}

	/**
	 * Start checking if the predicate is satisfied by some state.
	 *
	 * @param maxApplications
	 *            Maximum number of rewriting rule applications.
	 * @return true if the predicate holds in at least one state, false
	 *         otherwise.
	 */
	public boolean modelCheck(int maxApplications) {
		int applications = 0;
		while (!sim.simOver() && applications < maxApplications) {
			for (RuleApplication ra : sim.stepAndGet()) {
				if (predicate.isSatisfied(ra.getBig())) {
					return true;
				}
				applications++;
			}
		}
		return false;
	}
	
	/**
	 * Returns the entire graph computed by Sim.
	 * @return
	 */
	public BigStateGraph getGraph(){
		return sim.getGraph();
	}
}
