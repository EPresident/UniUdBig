package big.examples.philosophers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;

import big.bsg.BSGNode;
import big.bsg.BSGNode.BSGLink;
import big.bsg.BigStateGraph;
import big.mc.ModelChecker;
import big.prprint.BigPPrinterVeryPretty;

/**
 * Some tests with the bigraphical encoding of the "dining philosophers problem". If there is a deadlock, then the model checker terminates
 * and return false. If there aren't any deadlocks, then MCbig continues and generates infinite states. Therefore, this is only a 
 * semi-decision procedure.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	static BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
	
	public static void main(String[] args){
		Dinner problem = new Dinner();
		System.out.println("Insert the number of philosophers: ");
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		int n = Integer.parseInt(str);
		System.out.println("STRATEGY: Every philosopher takes first the left fork.");
		System.out.println("Are deadlocks avoided?\t"+((problem.deadlockDanger(n)) ? "YES" : "NO"));
		System.out.println("Number of states of the Model Checker:\t"+problem.getGraphSize());
		System.out.println("\n\n\n");
		
		System.out.println("STRATEGY: all the forks are enumerated. Every philosopher takes first the fork with the lower index.\n");
		DinnerNoDead bigProblem = new DinnerNoDead();
		System.out.println("Are deadlocks avoided?\t"+((bigProblem.deadlockDanger(n)) ? "YES" : "NO"));
		System.out.println("Number of states of the Model Checker:\t"+bigProblem.getGraphSize());
		
		
		System.out.println("\n\n\n");
		System.out.println("Do you want to visit the first graph?\t(yes,no)");
		//scan = new Scanner(System.in);
		if(scan.nextLine().equals("yes"))
			visitGraph(problem.getMC());
		
		System.out.println("\n\n\n");
		System.out.println("Do you want to visit the second graph?\t(yes,no)");
		//scan = new Scanner(System.in);
		if(scan.nextLine().equals("yes"))
			visitGraph(bigProblem.getMC());
		
	}
	
	
	
	public static void visitGraph(ModelChecker mc){
		BigStateGraph bsg = mc.getGraph();
		int applcations = 0;
        BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
        System.out.println(bsg.getGraphSize() + " - " + applcations);
        BSGNode currentNode = bsg.getRoot();
        String prName = "Root";
        List<BSGLink> links = bsg.getRoot().getLinks();
        int i=0;
        while (true) {
            System.out.println(pp.prettyPrint(currentNode.getState(), prName));
            i = 0;
            System.out.println("Choose a branch: ");
            for (BSGLink bsgl : links) {
                System.out.println(i + "- " + bsgl.rewRule);
                i++;
            }
            int choice = 0;
            try {
                String in = input.readLine();
                if (in.isEmpty()) {
                    System.out.println("Exiting.");
                    System.exit(0);
                }
                choice = Integer.parseInt(in);
            } catch (IOException ioex) {
                System.err.println(ioex.getMessage() + "\n Halting simulation.");
                System.exit(1);
            } catch (NumberFormatException nfex) {
                System.err.println("Expected a number as input: " + nfex.getMessage());
                System.exit(1);
            }
            prName = links.get(choice).rewRule;
            currentNode = links.get(choice).destNode;
            links = currentNode.getLinks();
        }
	}
	
	
}
