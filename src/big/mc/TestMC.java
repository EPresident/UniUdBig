package big.mc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;
import big.bsg.BSGNode;
import big.bsg.BigStateGraph;
import big.bsg.BSGNode.BSGLink;
import big.net.Utils;
import big.predicate.IsoPredicate;
import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.sim.BreadthFirstSim;

/**
 * Some tests with the model checker.
 *
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class TestMC {

    public static BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();

    public static void main(String[] args) {
        runTest2(2,4);
    }

    public static void runTest1() {
        Bigraph net = Utils.clientServerPacketExchange();
        System.out.println(pp.prettyPrint(net, "Bigrafo iniziale"));
        
        RewritingRule[] rules = Utils.getNetFWRules();

        //Make attention to ALL the properties, both names and values.
        Bigraph aim = Utils.getAimHttpPayload();
        Predicate atom = new WarioPredicate(aim, new TruePredicate(), new TruePredicate(), new TruePredicate());

        ModelChecker mc = new ModelChecker(new BreadthFirstSim(net, rules), atom);
        //ModelChecker mc = new ModelChecker(net, brs, atom, new BreadthFirstSim());

        System.out.println("Has the goal been reached ? \t" + (mc.modelCheck() ? "YES" : "NO"));
    }
    
    
    public static void runTest2(int n1, int n2){
    	Signature signature = big.examples.Utils.getMultSignature();
    	Bigraph bigraph = big.examples.Utils.getMultSample(n1, n2, signature);
        System.out.println(pp.prettyPrint(bigraph, "Bigrafo iniziale"));
        
    	RewritingRule[] rules = big.examples.Utils.getMultRules(signature);
    	
    	Bigraph aim;
    	BigraphBuilder builder = new BigraphBuilder(signature);
    	Root root = builder.addRoot();
    	Node num = builder.addNode("num", root);
    	for(int i=0; i<n1*n2; i++){
    		builder.addNode("one", num);
    	}
    	aim = builder.makeBigraph();
    	System.out.println(pp.prettyPrint(aim, "Aim"));
        
    	Predicate atom = new IsoPredicate(aim);
    	
        ModelChecker mc = new ModelChecker(new BreadthFirstSim(bigraph, rules), atom);
        
        System.out.println("Has the goal been reached ? \t" + (mc.modelCheck() ? "YES" : "NO"));
    }

}
