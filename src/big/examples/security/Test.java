package big.examples.security;

import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.RewritingRule;

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
 * Some tests with the Building problem.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	static BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
	
	public static void main(String[] args){
		MyBuilding mb = new MyBuilding();
		System.out.println("1. First building: the policy doesn't ensure the safety");
		System.out.println("Are the tokens inside the building safe?\t"+(mb.isSecure()?"YES":"NO"));
		
		System.out.println("\n\n\n");
		
		MySecureBuilding msb = new MySecureBuilding();
		System.out.println("2. Second building: the policy ensures the safety");
		System.out.println("Are the tokens inside the building safe?\t"+(msb.isSecure()?"YES":"NO"));
		
		System.out.println("\n\n\n");
		System.out.println("Do you want to visit the first graph?\t(yes,no)");
		Scanner scanner = new Scanner(System.in);
		if(scanner.nextLine().equals("yes"))
			visitGraph(mb.getMC());
		
		System.out.println("\n\n\n");
		System.out.println("Do you want to visit the second graph?\t(yes,no)");
		Scanner scan = new Scanner(System.in);
		if(scan.nextLine().equals("yes"))
			visitGraph(msb.getMC());
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
	
	
	/*public static void main(String[] args){
		MyBuilding mb = new MyBuilding();
		Bigraph building = mb.getInitBigraph();
		System.out.println(pp.prettyPrint(building, "Init")+"\n\n\n");
		
		RewritingRule secure_enter_room = mb.enter_room();
		Bigraph r1 = null;
		for(Bigraph res : secure_enter_room.apply(building)){
			System.out.println("ok");
			r1 = res;
		}
		System.out.println(pp.prettyPrint(r1, "R1"));
		
		RewritingRule comp_connect = mb.comp_connect();
		Bigraph r2 = null;
		for(Bigraph res : comp_connect.apply(r1)){
			System.out.println("ok");
			r2 = res;
		}
		System.out.println(pp.prettyPrint(r2, "R2"));
		
		RewritingRule transfer_comp = mb.transfer_comp();
		Bigraph r3 = null;
		for(Bigraph res : transfer_comp.apply(r2)){
			System.out.println("ok");
			r3 = res;
		}
		System.out.println(pp.prettyPrint(r3, "R3"));
		
		RewritingRule secure_leave_room = mb.leave_room();
		Bigraph r4 = null;
		for(Bigraph res : secure_leave_room.apply(r3)){
			System.out.println("ok");
			r4 = res;
		}
		System.out.println(pp.prettyPrint(r4, "R4"));
		
		RewritingRule call = mb.call();
		Bigraph r5 = null;
		for(Bigraph res : call.apply(r4)){
			System.out.println("ok");
			r5 = res;
		}
		System.out.println(pp.prettyPrint(r5, "R5"));
		
		RewritingRule transf_token = mb.transf_token();
		Bigraph r6 = null;
		for(Bigraph res : transf_token.apply(r5)){
			System.out.println("ok");
			r6 = res;
		}
		System.out.println(pp.prettyPrint(r6, "R6"));
		
		System.out.println(mb.getAim());
	}*/
	
}
