package big.examples.nfa;

import java.util.Scanner;


/**
 * Some tests with a simple NFA. The NFA is implemented in "MyNFA" class, and it recognizes 
 * the language (a(a+b))*.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	public static void main(String[] args){
		MyNFA nfa = new MyNFA();
		Scanner scan = new Scanner(System.in);
		String str = scan.nextLine();
		System.out.println("Does this NFA recognize the string \""+
							str+"\"?\t"+(nfa.recognizes(str) ? "YES" : "NO" ));
	}
	
}