package big.examples.philosophers;

import java.util.Scanner;

/**
 * Some tests with the bigraphical encoding of the "dining philosophers problem". If there is a deadlock, then the model checker terminates
 * and return false. If there aren't any deadlocks, then MCbig continues and generates infinite states. Therefore, this is only a 
 * semi-decision procedure.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	public static void main(String[] args){
		Dinner problem = new Dinner();
		System.out.println("Insert the number of philosophers: ");
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		int n = Integer.parseInt(str);
		System.out.println("STRATEGY: Every philosopher takes first the left fork.");
		System.out.println("Are deadlocks avoided?\t"+((problem.deadlockDanger(n)) ? "YES" : "NO"));
		System.out.println("Number of states of the Model Checker:\t"+problem.getGraphSize());
		System.out.println("\n\n\n\n\n\n");
		
		System.out.println("STRATEGY: all the forks are enumerated. Every philosopher takes first the fork with the lower index.\n");
		DinnerNoDead bigProblem = new DinnerNoDead();
		System.out.println("Are deadlocks avoided?\t"+((bigProblem.deadlockDanger(n)) ? "YES" : "NO"));
		System.out.println("Number of states of the Model Checker:\t"+bigProblem.getGraphSize());
	}
	
	
	/*public static void main(String[] args){
		Dinner dinner = new Dinner();
		Bigraph problem = dinner.getProblem(5);
		System.out.println("Problem:\n"+problem+"\n\n\n");
		
		//Rules
		InstantiationMap map = new InstantiationMap(0);
		TakeLeftFork takeL = new TakeLeftFork(dinner.takeLeftFork().getRedex(),dinner.takeLeftFork().getReactum(),map);
		TakeRightFork takeR = new TakeRightFork(dinner.takeRightFork().getRedex(),dinner.takeRightFork().getReactum(),map);
		DropLeftFork dropL = new DropLeftFork(dinner.dropLeftFork().getRedex(),dinner.dropLeftFork().getReactum(),map);
		DropRightFork dropR = new DropRightFork(dinner.dropRightFork().getRedex(),dinner.dropRightFork().getReactum(),map);
		
		Bigraph r1 = null;
		Bigraph r11 = null;
		int i=0;
		for(Bigraph res : takeL.apply(problem)){
			if(i==0)
				r11=res;
			r1 = res;
			i++;
		}
		System.out.println("R1:\n"+r1+"\n\n\n");
		
		Bigraph r2 = null;
		for(Bigraph res: takeR.apply(r1)){
			r2 = res;
		}
		System.out.println("R2:\n"+r2+"\n\n\n");
		
		Bigraph r3 = null;
		for(Bigraph res: dropL.apply(r2)){
			r3 = res;
		}
		System.out.println("R3:\n"+r3+"\n\n\n");
		
		Bigraph r4 = null;
		for(Bigraph res: dropR.apply(r3)){
			r4 = res;
		}
		System.out.println("R4:\n"+r4+"\n\n\n");
		System.out.println("---------------"+"\n\n\n");
		
		System.out.println("R1:\n"+r1+"\n\n");
		System.out.println("R1:\n"+r11+"\n\n");
		
		
		
		
		Isomorphism iso = new Isomorphism();
		System.out.println("Isomorph: "+iso.areIsomorph(r1, r11));
		PropertyIsomorphism isoP = new PropertyIsomorphism();
		System.out.println("PropertyIsomorphism: "+isoP.areIsomorph(r1, r1));
		
	}*/
	
}
