package big.examples.security;

import java.util.LinkedList;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.RewritingRule;
import it.uniud.mads.jlibbig.core.std.Root;

/**
 * An example of a secure building.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class MySecureBuilding extends SecureBuilding{
	
	/**
	 * Sets and gets the building for the problem.
	 * @return
	 */
	protected Bigraph getInitBigraph(){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		Root root = builder.addRoot();
		Node bob = builder.addNode("Bob",root);
		Node bobPhone = builder.addNode("Phone", bob);
		Node building = builder.addNode("Building", root);
		Node alice = builder.addNode("Alice", building);
		Node alicePhone = builder.addNode("Phone", alice);
		Node room1 = builder.addNode("Room", building);
		Node computer1 = builder.addNode("Computer", room1);
		Node token1 = builder.addNode("Token", computer1);
		Node room2 = builder.addNode("Room", building);
		Node computer2 = builder.addNode("Computer", room2);
		Node token2 = builder.addNode("Token", computer2);
		return builder.makeBigraph();
	}
	
	
}
