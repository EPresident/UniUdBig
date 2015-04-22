package big.net;

import it.uniud.mads.jlibbig.core.attachedProperties.*;
import it.uniud.mads.jlibbig.core.std.*;

import java.util.*;

public class EncapRule extends RewritingRule{
	
	private Bigraph bigraph;
	
	public EncapRule(Bigraph redex, Bigraph reactum, InstantiationMap map){
		super(redex, reactum, map);
	}
	
	@Override
	public Iterable<Bigraph> apply(Bigraph b){
		bigraph = b;
		return super.apply(b);
	}
	
	
	@Override
	public void instantiateReactumNode(Node original, Node instance, Match match){
		
		Iterator<? extends Node> iter = this.bigraph.getNodes().iterator();
		while(iter.hasNext()){
			Node n = iter.next(); //appartiene al bigrafo
			Node preImg = match.getPreImage(n); //appartiene al match
			//n1 e n2 apparterranno al reactum che sto costruendo.
			if(preImg != null){
				Iterator<Property<?>> i = n.getProperties().iterator();
				//Tutte le proprietà di n le copio in preImg
				while( i.hasNext() ){
					Property p = i.next();
					if( !p.getName().toString().equals("Owner")){
						preImg.attachProperty(new SharedProperty<String>(
								new SimpleProperty( p.getName() , p.get() )));
					}
				}
				
				//Le proprietà di preImg le copio nel suo corrispettivo del reactum. Come faccio?
				//uso le proprietà definite in questa classe.
				Iterator<Property<?>> it = preImg.getProperties().iterator();
				while(it.hasNext()){
					Property p = it.next();
					if( !p.getName().toString().equals("Owner") ){
						if( original.getProperty(p.getName()) != null ){
							if( p.get().toString().equals( original.getProperty(p.getName()).get() )){
								Iterator<Property<?>> itP = preImg.getProperties().iterator();
								while(itP.hasNext()){
									Property prop = itP.next();
									if( !prop.getName().equals("Owner"))
										instance.attachProperty(prop);
								}
							}
						}
					}
				}
			}
		}
		
		
	} 
	
	
	public static Bigraph getRedex(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapSender")));
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		Node packet = builder.addNode("packet",r1,id1,id2);
		packet.attachProperty(new SharedProperty<String>(
				new SimpleProperty("PacketType","packetIn")));
		builder.addSite(packet);
		
		builder.addSite(r1);
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapReceiver")));
		
		return builder.makeBigraph();
	}
	
	
	

	public static Bigraph getReactum(Signature signature){
		BigraphBuilder builder = new BigraphBuilder(signature);
		Root r1 = builder.addRoot();
		OuterName id1 = builder.addOuterName("id1");
		OuterName down1 = builder.addOuterName("down1");
		Node sn1 = builder.addNode("stackNode", r1, id1, down1);
		sn1.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapSender")));
		OuterName id2 = builder.addOuterName("id2");
		OuterName down2 = builder.addOuterName("down2");
		Node packetOut = builder.addNode("packet", r1, down1, down2);
		packetOut.attachProperty(new SharedProperty<String>(
				new SimpleProperty("PacketType","packetOut")));
		Node packetIn = builder.addNode("packet",packetOut,id1,id2);
		packetIn.attachProperty(new SharedProperty<String>(
				new SimpleProperty("PacketType","packetIn")));
		builder.addSite(packetIn);
		
		builder.addSite(r1);
		Root r2 = builder.addRoot();
		Node sn2 = builder.addNode("stackNode",r2,id2,down2);
		sn2.attachProperty(new SharedProperty<String>(
				new SimpleProperty("NodeType","EncapReceiver")));
		
		return builder.makeBigraph();
	}
	
}



/*
 * Il metodo instantiateReactum(Match match) prende il reactum della RewritingRule che ho già definito
 * e lo sostituisce al redex del match come parametro.
 * 
 * Il metodo apply(Bigraph to) infatti richiama il metodo instantiateReactum().
 * In particolare crea un oggetto di tipo RewriteIterable che è un insieme di Bigrafi, uno per ogni match
 * trovato. Infatti, la classe RewriteIterable contiene al suo interno una lista di Match. Per ognuno
 * di questi verrà creato un bigrafo. VEDI LINEA 242. 
 * Nella classe RewriteIterator, che è l'iteratore per RewriteIterable, si scorre tutta la lista di match
 * mAble e si creano i bigrafi risultanti dall'applicazione della regola. Infatti: se si sono trovati
 * dei match (ricordiamo che sono delle TRIPLE <Context, Redex, Parameters>) , allora mTor non è vuota e
 * posso creare il nuovo bigrafi basandomi su quel match M. A tal scopo esiste il metodo next. 
 * Args è la lista di tutti i bigrafi creati con quel redex, cioè con i vari match trovati.
 * PROCEDIMENTO:
 * - prendo un match (riga 267)
 * - istanzio il reactum, che ho definito prima quando ho creato la regola di riscrittura (riga 271).
 * - un match è una tripla <Context,Redex,Parameters>. Il redex è formato dal bigrafo F giustapposto (x) 
 * 	 all'identità della sua interfaccia I-->   R=FxidI.   Quindi prendo l'identità e la giustappongo
 *   (destra o sinistra è indifferente) al bigrafo istanziato dal reactum.
 * - il passo importante è questo. Come faccio a "SOSTITUIRE" il reactum che ho istanziato nel bigrafo
 *   "to" specificato in apply()? Lo prendo dal contesto del match M che sto considerando. Infatti il
 *   contesto è proprio tutto il restante bigrafo "to" con dei siti al posto dei match. Per cui faccio
 *   l'outerCompose() tra il bigrafo del reactum e il context.
 * - args, cioè la lista degli argomenti (che sono bigrafi), la riempio con i parametri del match M, se
 *   li ha, cioè quei bigrafi che "stanno dentro" al reactum. Infatti nella riga 275 chiamo
 *   match.getParam() e aggiungo se ci sono i bigrafi parametri ad args.
 * - se ci sono parametri, allora args non è vuoto, quindi entro nel secondo if: qua si ultima il bigrafo
 *   che si stava costruendo aggiungendo i bigrafi parametri, infatti faccio un compose() nella riga 282.
 *	
 * A questo punto ho creato finalmente il bigrafo finale.
 * 
 * 
 * Posso estendere il metodo instantiateReactumNode(), che è chiamato dal metodo instantiateReactum(),
 * per far sì che durante la istanziazione del reactum anche le attached properties vengano copiate.
 * 
 * Il metodo instantiateReactum(Match match) viene chiamato con il parametro match (che è un match
 * trovato) solo per passarlo al metodo instantiateReactumNode(). Infatti grazie al match posso ricavare
 * il contesto, i parametri e soprattutto l'immagine.
 * Guarda metodi della classe Match come getImage(Node n) e getPreImage(Node n).
 * Riga 175 : n1 è il nodo che fa sempre parte del reactum
 * Riga 176 : n2 è una replica di n1
 * In sostanza quello che si fa in instantiateReactum() è COPIARE il bigrafo reactum: per farlo infatti
 * devo copiare tutti i nomi, nodi, archi ecc...
 * Quindi in instantiateReactumNode(Node original, Node istance, Match match), n1=original, n2=istance.
 * Per cui io prenderò proprietà del nodo original, le cercherò nell'occorrenza cioè in match che è un
 * bigrafo, e modificherà il nodo istance.
 * 
 * n1 fa parte del reactum, quindi posso prendere come esempio n1 = http_server_google.
 * n2 è una sua replica che farà parte del reactum istanziato.
 * match possiede sia il contesto in cui il redex è stato trovato, sia il redex, sia i parametri.
 * Per cui posso prendere un nodo del reactum cioè n1, trovare la sua preimmagine in match, prendere la
 * proprietà di questo nodo, e attaccarla ad n2.
 */