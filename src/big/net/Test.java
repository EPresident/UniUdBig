package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.SharedProperty;
import it.uniud.mads.jlibbig.core.attachedProperties.SimpleProperty;
import it.uniud.mads.jlibbig.core.std.Bigraph;
import it.uniud.mads.jlibbig.core.std.BigraphBuilder;
import it.uniud.mads.jlibbig.core.std.Match;
import it.uniud.mads.jlibbig.core.std.Matcher;
import it.uniud.mads.jlibbig.core.std.Node;
import it.uniud.mads.jlibbig.core.std.OuterName;
import it.uniud.mads.jlibbig.core.std.Root;
import it.uniud.mads.jlibbig.core.std.Signature;

import java.util.Iterator;

import big.predicate.Predicate;
import big.predicate.TruePredicate;
import big.predicate.WarioPredicate;
import big.prprint.BigPPrinterVeryPretty;
import big.sim.PropertyMatcher;

public class Test {

    public static Signature signature;

    public static void main(String[] args) {
        runTest2();
    }

    protected static void runTest1() {
    	BigPPrinterVeryPretty pp = new BigPPrinterVeryPretty();
    	Bigraph bigraph = Utils.clientServerPacketExchange();
    	
    	//Start of the reactions
    	EncapRule encap = new EncapRule();
    	if(encap.isApplicable(bigraph)){//TCP
    		if(encap.apply(bigraph).iterator().hasNext()){
    			bigraph = encap.apply(bigraph).iterator().next();
    			System.out.println("ENCAP");
    		}
    	}
    	if(encap.isApplicable(bigraph)){//IP
    		if(encap.apply(bigraph).iterator().hasNext()){
    			bigraph = encap.apply(bigraph).iterator().next();
    			System.out.println("ENCAP");
    		}
    	}
    	NewTokenINRuleNear ntirn = new NewTokenINRuleNear();
    	if(ntirn.isApplicable(bigraph)){
    		Iterator<Bigraph> iter = ntirn.apply(bigraph).iterator();
    		iter.next();
    		bigraph = iter.next();
    		System.out.println("New Token IN");
    	}
    	DFRuleFW dfrfw = new DFRuleFW();
    	if(dfrfw.isApplicable(bigraph)){
    		if(dfrfw.apply(bigraph).iterator().hasNext()){
    			bigraph = dfrfw.apply(bigraph).iterator().next();
    			System.out.println("Direct Forward with Firewall");
    		}
    	}
    	
    	NewTokenOUTRuleFar ntorf = new NewTokenOUTRuleFar();
    	if(ntorf.isApplicable(bigraph)){
    		if(ntorf.apply(bigraph).iterator().hasNext()){
    			bigraph = ntorf.apply(bigraph).iterator().next();
    			System.out.println("New Token OUT");
    		}
    	}
    	
    	ForwardFWRule ffwr = new ForwardFWRule();
    	if(ffwr.isApplicable(bigraph)){
    		if(ffwr.apply(bigraph).iterator().hasNext()){
    			bigraph = ffwr.apply(bigraph).iterator().next();
    			System.out.println("Forward with Firewall");
    		}
    	}
    	
    	ForwardRule fw = new ForwardRule();
    	if(fw.isApplicable(bigraph)){
    		if(fw.apply(bigraph).iterator().hasNext()){
    			bigraph = fw.apply(bigraph).iterator().next();
    			System.out.println("Simple Forward");
    		}
    	}
    	
    	if(ntorf.isApplicable(bigraph)){
    		if(ntorf.apply(bigraph).iterator().hasNext()){
    			bigraph = ntorf.apply(bigraph).iterator().next();
    			System.out.println("New Token OUT");
    		}
    	}
    	
    	FWOUTRule fwor = new FWOUTRule();
    	if(fwor.isApplicable(bigraph)){
    		if(fwor.apply(bigraph).iterator().hasNext()){
    			bigraph = fwor.apply(bigraph).iterator().next();
    			System.out.println("Firewall OUT");
    		}
    	}
    	
    	Domain2HostRule d2hr = new Domain2HostRule();
    	if(d2hr.isApplicable(bigraph)){
    		if(d2hr.apply(bigraph).iterator().hasNext()){
    			bigraph = d2hr.apply(bigraph).iterator().next();
    			System.out.println("Domain to Host");
    		}
    	}
    	
    	DecapRule decap = new DecapRule();
    	if( decap.isApplicable(bigraph) ){
    		while(decap.apply(bigraph).iterator().hasNext()){
    			bigraph = decap.apply(bigraph).iterator().next();
    			System.out.println("DECAP");
    		}
    	}
    	
    	
    	System.out.println(pp.prettyPrint(bigraph));
    	
    	
    }
    
    
   
    protected static void runTest2(){
    	Bigraph bigraph = Utils.clientServerPacketExchange();
    	Bigraph aim = Utils.getAimHttpPayload();
    	
    	//Sfruttando Wario, posso cercare di creare un Aim molto piu semplice, cioè per esempio un redex
    	//con solamente il server come nodo. Poi controllo il nel Contesto gli attributi del dominio 
    	//(google_domain) e nei parametri il tipo di pacchetto (http_payload).
    	Predicate atom = new WarioPredicate( aim , new TruePredicate(), new TruePredicate(), new TruePredicate() );
    	System.out.println( atom.isSatisfied(bigraph) );
    	
    }
   
    
}





