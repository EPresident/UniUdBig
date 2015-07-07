package big.mc;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import big.net.Utils;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.brs.BRS;
import big.bsg.BigStateGraph;
import big.sim.BreadthFirstSim;
import big.brs.BreadthFirstStrat;
import big.sim.RandomSim;
import big.sim.Sim;

/**
 * Some tests with the model checker.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class TestMC {

    public static BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();

    public static void main(String[] args) {
        runTest1();
    }

    public static void runTest1() {
        Bigraph net = Utils.clientServerPacketExchange();
        System.out.println(pp.prettyPrint(net, "Bigrafo iniziale"));

        RewritingRule[] rules = Utils.getNetFWRules();
        BRS brs = new BRS(new BreadthFirstStrat(), rules);

        //Make attention to ALL the properties, both names and values.
        Bigraph aim = Utils.getAimHttpPayload();
        Predicate atom = new WarioPredicate(aim, new TruePredicate(), new TruePredicate(), new TruePredicate());

        ModelChecker mc = new ModelChecker(net, brs, atom, new RandomSim());
        //ModelChecker mc = new ModelChecker(net, brs, atom, new BreadthFirstSim());

        System.out.println("Has the goal been reached ? \t" + (mc.modelCheck() ? "YES" : "NO"));

    }

}
