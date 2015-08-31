package big.examples;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.InstantiationMap;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

/**
 * Methods to get faster some examples and their signatures and rules.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Utils {
	
	/**
	 * Returns the bigraph equivalent to the multiplication (n1*n2).
	 * 
	 * @param n1 first number
	 * @param n2 second number
	 * @param signature the signature to pass to the builder
	 * @return the bigraph equivalent to the multiplication (n1*n2)
	 */
	public static Bigraph getMultSample(int n1, int n2, Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        for (int i = 0; i < n1; i++) {
            builder.addNode("one", num1);
        }
        Node num2 = builder.addNode("num", mul);
        for (int i = 0; i < n2; i++) {
            builder.addNode("one", num2);
        }
        return builder.makeBigraph();
	}
	
	public static Signature getMultSignature(){
		Control mul_ctrl = new Control("mul", true, 0);
		Control num_ctrl = new Control("num", false, 0);
		Control one_ctrl = new Control("one", false, 0);
		return new Signature(mul_ctrl, num_ctrl, one_ctrl);
	}
	
	public static RewritingRule[] getMultRules(Signature signature){
		RewritingRule[] rules = new RewritingRule[2];
		//First Rule
		Bigraph redex_recursive = makeRedexRecursive(signature);
        Bigraph reactum_recursive = makeReactumRecursive(signature);
        int[] map_r = {0, 1, 2, 1};
        InstantiationMap map_recursive = new InstantiationMap(3, map_r);
        rules[0] = new RewritingRule(redex_recursive, reactum_recursive, map_recursive);
		//Second Rule
        Bigraph redex_base = makeRedexBase(signature);
        Bigraph reactum_base = makeReactumBase(signature);
        int[] map_b = {0};
        InstantiationMap map_base = new InstantiationMap(2, map_b);
        rules[1] = new RewritingRule(redex_base, reactum_base, map_base);
		
		return rules;
	}
	

    public static Bigraph makeRedexRecursive(Signature signature) {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        builder.addSite(num1);
        Node one = builder.addNode("one", num1);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(num2);
        builder.addSite(mul);
        return builder.makeBigraph();
    }

    public static Bigraph makeReactumRecursive(Signature signature) {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        builder.addSite(num1);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(num2);
        builder.addSite(mul);
        builder.addSite(mul);
        return builder.makeBigraph();
    }

    public static Bigraph makeRedexBase(Signature signature) {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        Node num2 = builder.addNode("num", mul);
        builder.addSite(mul);
        builder.addSite(num2);
        return builder.makeBigraph();
    }

    public static Bigraph makeReactumBase(Signature signature) {
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node num1 = builder.addNode("num", r1);
        builder.addSite(num1);
        return builder.makeBigraph();
    }
	
	
}
