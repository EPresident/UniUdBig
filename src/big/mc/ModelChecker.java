package big.mc;

import big.predicate.Predicate;
import big.brs.BRS;
import big.brs.RuleApplication;
import big.sim.Sim;
import big.sim.SimStrategy;
import it.uniud.mads.jlibbig.core.std.Bigraph;

/**
 * Simple model checker that checks a property (expressed in any type of logic)
 * for all nodes of the graph. The user can specify the way the nodes are
 * visited. The strategy employed by the BRS affects the performance of the
 * model checker. Currently, if one state satisfies the given predicate, the
 * model checker returns true.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 * @author Elia Calligaris <calligaris.elia@spes.uniud.it>
 */
public class ModelChecker {

    private final Sim sim;
    private final Predicate predicate;

    /**
     * @param root The starting Bigraph (i.e. root of the state graph)
     * @param brs The BRS (rules + application strategy) to use.
     * @param p The predicate whose validity must be checked.
     * @param ss The simulation strategy (graph building order)to employ.
     */
    public ModelChecker(Bigraph root, BRS brs, Predicate p, SimStrategy ss) {
        sim = new Sim(root, brs, ss);
        predicate = p;
    }

    /**
     * @param s Simulator to employ.
     * @param p The predicate whose validity must be checked.
     */
    public ModelChecker(Sim s, Predicate p) {
        sim = s;
        predicate = p;
    }

    public boolean modelCheck() {
        while (sim.hasNext()) {
            for (RuleApplication ra : sim.stepAndGet()) {
                if (predicate.isSatisfied(ra.getBig())) {
                    return true;
                }
            }
        }
        return false;
    }
}
