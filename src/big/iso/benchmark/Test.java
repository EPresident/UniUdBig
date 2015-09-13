package big.iso.benchmark;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import big.iso.Isomorphism;

/**
 * Checks the performances of the "Bigraph Isomorphism" algorithm. 
 *  
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	/*public static void main(String[] args){
			int n=30;
			for(int i=0; i<20;i++){
				RandomBigGen bigRandom = new RandomBigGen(n,2,2);
				Bigraph b1 = bigRandom.gen();
				Bigraph b2 = b1.clone();
				
				Isomorphism iso = new Isomorphism();
				iso.areIsomorph(b1, b2);
				long[] times = iso.getTimes();
				
				System.out.println(n+" nodes Loading Time");
				double tempL = (double)times[0]/1000000000.0;
				System.out.println(Double.toString(tempL).replace('.', ','));
				System.out.println("\n");
				System.out.println(n+" nodes Working Time");
				double tempR = (double)times[1]/1000000000.0;
				System.out.println(Double.toString(tempR).replace('.', ','));
				
			}
	}*/
	
	
	public static void main(String[] args){
		double[] numbers = {
				73.045917685,
				68.46172690925,
				80.373867241,
				58.359412471,
				88.889758874,
				59.47550058264,
				88.73202062605,
				74.325554381
				};
		for(double num : numbers){
			System.out.println(prepare(num));
		}
		
	}
	
	
	public static double prepare(double x){
		return (71*(Math.pow(x, 3)))/(40459500)-(7981*(Math.pow(x, 2)))/(4009500)+(8891*x)/(36450)+(1564870/890109);
	}
	
}
