package big.examples.nfa;

import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 * Example of a NFA that recognizes the language (a(a+b))*
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class MyNFA extends NFA{
	

	/**
	 * Example of a NFA that recognizes the language (a(a+b))*
	 * @param signature
	 * @return
	 */
	protected void setNFA(){
		Root root = builder.addRoot();
		OuterName a = builder.addOuterName("a");
		OuterName b = builder.addOuterName("b");
		//State S1
		Node s1 = builder.addNode("state", root);
		Node t_0a1 = builder.addNode("trans", s1, a);
		builder.addNode("active", s1);//Node active
		builder.addNode("final", s1);//Node final
		//State S2
		Node s2 = builder.addNode("state", root);
		Node t_1a0 = builder.addNode("trans", s2, a);
		Node t_1ba = builder.addNode("trans", s2, b);
		
		//Edges
		builder.relink(t_0a1.getPort(1), s2.getPort(0));
		builder.relink(t_1a0.getPort(1), t_1ba.getPort(1), s1.getPort(0));
		
	}
	
}