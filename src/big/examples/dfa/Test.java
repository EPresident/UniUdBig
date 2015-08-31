package big.examples.dfa;

/**
 * Some tests with a simple DFA. The DFA is implemented in "MyDFA" class, and it recognizes 
 * the language (a(a+b))*.
 * 
 * @author Luca Geatti <geatti.luca@spes.uniud.it>
 *
 */
public class Test {
	
	public static void main(String[] args){
		MyDFA dfa = new MyDFA();
		String str = "abaa";
		System.out.println("Does this DFA recognize the string \""+
							str+"\"?\t"+(dfa.belongsTo(str) ? "YES" : "NO" ));
	}
	
}
