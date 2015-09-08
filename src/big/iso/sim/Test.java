package big.iso.sim;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import big.iso.Isomorphism;

/**
 * Checks the performances of the "Bigraph Isomorphism" algorithm. 
 *  
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	public static void main(String[] args){
		for(int n=10; n<=100; n=n+10){
			RandomBigGen bigRandom = new RandomBigGen(n,2,2);
			Bigraph b1 = bigRandom.gen();
			Bigraph b2 = b1.clone();
			
			Isomorphism iso = new Isomorphism();
			iso.areIsomorph(b1, b2);
			long[] times = iso.getTimes();
			
			System.out.println(n+" nodes:");
			System.out.println("\tLoading Time: "+(double)times[0]/1000000000.0+" seconds");
			System.out.println("\tWorking Time: "+(double)times[1]/1000000000.0+" seconds");
		}
	}
	
}
