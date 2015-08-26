package big.iso;

import big.match.PropertyMatcher;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Control;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

/**
 * Some tests with the isomorphism algorithm.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class IsoTest{
	
	public static void main(String[] args){
		Bigraph[] exe1 = getExample3();
		
		Bigraph b1 = exe1[0];
		Bigraph b2 = exe1[1];
		
		PropertyMatcher matcher = new PropertyMatcher();
		Isomorphism iso = new Isomorphism(matcher);
		
		System.out.println(iso.areIsomorph(b1, b2));
		
		/*BigDFS visit = new BigDFS(b1);
		HashMap<Integer, LinkedList<PlaceEntity>> nodesAt = visit.getNodesAtHeight();
		for(Integer index : nodesAt.keySet()){
			System.out.print(index+"\t");
			for(PlaceEntity node : nodesAt.get(index)){
				System.out.print(node+"\t");
			}
			System.out.println();
		}*/
	}
	
	
	
	private static Bigraph[] getExample1(){
		Bigraph[] bigArray = new Bigraph[2];
		
		Control quad_ctrl = new Control("quadrato",true,2);
		Control trian_ctrl = new Control("triangolo",true,1);
		Signature signature = new Signature(quad_ctrl, trian_ctrl);
		//First Bigraph
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root root = builder.addRoot();
		OuterName out1 = builder.addOuterName("out1");
		OuterName out2 = builder.addOuterName("out2");
		Node quadrato = builder.addNode("quadrato", root, out1);
		Node triangolo = builder.addNode("triangolo", root);
		builder.relink(quadrato.getPort(1), triangolo.getPort(0));
		bigArray[0] = builder.makeBigraph();
		//Second Bigraph
		builder = new BigraphBuilder(signature);
		root = builder.addRoot();
		out1 = builder.addOuterName("out1");
		out2 = builder.addOuterName("out2");
		quadrato = builder.addNode("quadrato", root, out2);
		triangolo = builder.addNode("triangolo", root);
		builder.relink(quadrato.getPort(1), triangolo.getPort(0));
		bigArray[1] = builder.makeBigraph();
		
		return bigArray;
	}
	
	
	private static Bigraph[] getExample2(){
		Bigraph[] bigArray = new Bigraph[2];
		
		Control mul_ctrl = new Control("mul", true, 0);
        Control num_ctrl = new Control("num", false, 0);
        Control one_ctrl = new Control("one", false, 0);
        Signature signature = new Signature(mul_ctrl, num_ctrl, one_ctrl);
        //First Bigraph
        BigraphBuilder builder = new BigraphBuilder(signature);
        Root r1 = builder.addRoot();
        Node mul = builder.addNode("mul", r1);
        Node num1 = builder.addNode("num", mul);
        for (int i = 0; i < 2; i++) {
            builder.addNode("one", num1);
        }
        Node num2 = builder.addNode("num", mul);
        for (int i = 0; i < 4; i++) {
            builder.addNode("one", num2);
        }
        bigArray[0] = builder.makeBigraph();
        //Second Bigraph
        builder = new BigraphBuilder(signature);
        r1 = builder.addRoot();
        mul = builder.addNode("mul", r1);
        num1 = builder.addNode("num", mul);
        for (int i = 0; i < 4; i++) {
            builder.addNode("one", num1);
        }
        num2 = builder.addNode("num", mul);
        for (int i = 0; i < 2; i++) {
            builder.addNode("one", num2);
        }
        bigArray[1] = builder.makeBigraph();
        
        return bigArray;
	}
	
	
	
	
	private static Bigraph[] getExample3(){
		Bigraph[] bigArray = new Bigraph[2];
		
		Control quad_ctrl = new Control("quadrato",true,1);
		Control trian_ctrl = new Control("triangolo",true,1);
		Control cerchio_ctrl = new Control("cerchio",true,1);
		Signature signature = new Signature(quad_ctrl, trian_ctrl, cerchio_ctrl);
		//First Bigraph
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root root = builder.addRoot();
		Node q1 = builder.addNode("quadrato",root);
		Node t1 = builder.addNode("triangolo",q1);
		Node c1 = builder.addNode("cerchio", root);
		builder.relink(q1.getPort(0), c1.getPort(0));
		bigArray[0] = builder.makeBigraph();
		
		//Second Bigraph
		builder = new BigraphBuilder(signature);
		root = builder.addRoot();
		c1 = builder.addNode("cerchio", root);
		q1 = builder.addNode("quadrato",root);
		t1 = builder.addNode("triangolo",q1);
		builder.relink(c1.getPort(0), t1.getPort(0));
		bigArray[1] = builder.makeBigraph();
		
		return bigArray;
	}
	
}












