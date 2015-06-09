package big.mc;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import big.net.Utils;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.sim.BRS;
import big.sim.BigStateGraph;
import big.sim.BreadthFirstStrat;
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
		Sim sim = new Sim(brs, net);
		BigStateGraph graph = sim.getGraph(1000);
		
		//Make attention to ALL the properties, both names and values.
		Bigraph aim = Utils.getAimHttpPayload();
		Predicate atom = new WarioPredicate( aim , new TruePredicate(), new TruePredicate(), new TruePredicate() );
		Visit dfs = new DFSVisit();
		
		ModelChecker mc = new ModelChecker( graph , atom , dfs );
		
		
		System.out.println("\n"+"Graph Size : "+graph.getGraphSize() );
		System.out.println("Has the goal been reached ? \t" + ( mc.go() ? "YES": "NO") );
		
	}

}
