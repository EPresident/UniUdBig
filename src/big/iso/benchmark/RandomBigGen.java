package big.iso.benchmark;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Parent;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.Collection;
import java.util.Random;

/**
 * Class for creating random bigraphs.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class RandomBigGen {
	
	private BigraphBuilder builder;
	private int nNodes;
	private int nPorts;
	private int nChildren;
	
	/**
	 * Istantiates the bigraph random generation
	 * @param nNodes number of nodes of the new bigraph
	 * @param nChildren max number of children of each node
	 * @param nPorts number of ports of each node
	 */
	public RandomBigGen(int nNodes, int nChildren,int nPorts){
		this.builder = new BigraphBuilder(RandomBigGen.getSignature(nPorts));
		this.nNodes = nNodes;
		this.nPorts = nPorts;
		this.nChildren = nChildren;
	}
	
	/**
	 * Simple signature for the new bigraph
	 * @param nPorts
	 * @return
	 */
	private static Signature getSignature(int nPorts){
		Control node_ctrl = new Control("node",true,nPorts);
		return new Signature(node_ctrl);
	}
	
	/**
	 * Creates the new random bigraph
	 * @return the new random bigraph
	 */
	public Bigraph gen(){
		BigraphBuilder builder = new BigraphBuilder(this.builder.getSignature());
		//Random Set of Outernames
		for(int k=0; k<nNodes*nPorts; k++){
			builder.addOuterName();
		}
		Collection<? extends OuterName> outers = builder.getOuterNames();
		//Random PlaceGraph
		Root root = builder.addRoot();
		Random random = new Random();
		int nChild = random.nextInt(nChildren+1);//max number of children for each node
		Parent current = root;
		int n=0;
		if(nChild==0)
			nChild++;
		while(n < nNodes){
			Random r1 = new Random();
			Random r2 = new Random();
			int numChild = r1.nextInt(nChild+1);//effective number of children of the current node
			int randomChild = r2.nextInt(numChild+1);
			for(int k=0;k<numChild;k++){
				Node node = builder.addNode("node", current);
				n++;
				//Random LinkGraph
				Random r3 = new Random();
				int nPortsOcc = r3.nextInt(nPorts+1);
				for(int j=0;j<nPortsOcc;j++){
					//Takes a random outername
					r3 = new Random();
					int randomOutNum = r3.nextInt(outers.size()+1);
					OuterName handle = null;
					int z=0;
					for(OuterName out : outers){
						if(z==randomOutNum){
							handle = out;
						}
						z++;
					}
					if(handle != null)
						builder.relink(handle,node.getPort(j));
				}
				if(k==randomChild){
					current = node;
				}
			}	
		}
		return builder.makeBigraph();
	}
	
}
