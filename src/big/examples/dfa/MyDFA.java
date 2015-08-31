package big.examples.dfa;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

/**
 * Example of a DFA that recognizes the language (a(a+b))*
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class MyDFA extends DFA{
	

	/**
	 * Example of a DFA that recognizes the language (a(a+b))*
	 * @param signature
	 * @return
	 */
	protected void setDFA(){
		Root root = builder.addRoot();
		OuterName eps = builder.addOuterName("&");
		OuterName a = builder.addOuterName("a");
		OuterName b = builder.addOuterName("b");
		//State S1
		Node s1 = builder.addNode("state", root);
		Node t_0a1 = builder.addNode("trans", s1, a);
		Node t_0epsE = builder.addNode("trans", s1, eps);
		Node active = builder.addNode("active", s1);
		//State S2
		Node s2 = builder.addNode("state", root);
		Node t_1a0 = builder.addNode("trans", s2, a);
		Node t_1ba = builder.addNode("trans", s2, b);
		//State S_e
		Node sE = builder.addNode("state", root);
		
		//Edges
		builder.relink(t_0a1.getPort(1), s2.getPort(0));
		builder.relink(t_0epsE.getPort(1), sE.getPort(0));
		builder.relink(t_1a0.getPort(1), t_1ba.getPort(1), s1.getPort(0));
		
	}
	
}
