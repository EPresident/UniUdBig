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
		System.out.println("Strategy: Every philosopher takes first the left fork.\n"
				+ "Deadlock are NOT avoided so the procedure must return False...");
		System.out.println("Are deadlocks avoided?\t"+((problem.deadlockDanger(n)) ? "YES" : "NO"));
		System.out.println("\n\n\n\n\n\n");
		
		System.out.println("Strategy: all the forks are enumerated.\n"
				+ "Every philosopher takes first the fork with the lower index.\n"
				+ "Deadlock are avoided so the procedure may not terminate...");
		DinnerNoDead bigProblem = new DinnerNoDead();
		System.out.println("Are deadlocks avoided?\t"+((bigProblem.deadlockDanger(n)) ? "YES" : "NO"));
	}
	
}
