package it.imperato.test.parallelprogramming.example;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.utils.CalcUtil;

public class FactorialTaskWithCountedCompleter {

    private static Logger log  = LogManager.getLogger(FactorialTaskWithCountedCompleter.class);
    
    public static void main (String[] args) {
        List<BigInteger> list = new ArrayList<>();
        for (int i = 3; i <= 15; i++) {
            list.add(new BigInteger(Integer.toString(i)));
        }

        BigInteger sum = ForkJoinPool.commonPool().invoke(new FactorialTask(null,
                                                new AtomicReference<>(BigInteger.ZERO),
                                                list));
        
        log.info("Sum of the factorials = " + sum);
    }
    
    /**
     * 
     * CountedCompleter è una terza implementazione di ForkJoinTask. 
     * 
     * Differenze rispetto le altre due implementazioni, RecursiveAction e RecursiveTask: 
     * 1. Dà la possibilità di triggerare una azione dopo il completamento del lavoro. 
     * Ciò può essere predisposto con l'implementazione del metodo onCompletion(CountedCompleter).
     * 
     * 2. CountedCompleter introduce l'aspetto del pending task: ogni volta che un nuovo sub task è dichiarato come da eseguire, 
     * il metodo addToPendingCount(int) viene invocato. Grazie a questo viene cambiato un counter interno di pending task. 
     * Questo count determina se l'esecuzione del task è completata. Se non lo è, è decrementato. 
     * Se è completa e il metodo tryComplete viene chiamato (o propagateCompletion), un completion event viene inviato a tutti i CounterCompleter task.
     * 
     * RecursiveTask e RecursiveAction non hanno invece bisogno di una chiamata esplicita per la fine del job, la combinazione fork-join è sufficiente.  
     * CountedCompleter ha un comportamento differente perchè ha necessità di una chiamata esplicita a tryComplete() o propagateCompletion() 
     * prima di ritornare, altrimenti il task è considerato non completo e indeterminatamente in stato di run.
     * 
     * NOTA: In molti casi CountedCompleter non torna valori, ma se ne torna uno, allora il metodo getRawResult(T) è overridden.
     * 
     */
    
    private static class FactorialTask extends CountedCompleter<BigInteger> {
    	
		private static final long serialVersionUID = 1L;
		
		
		private static int SEQUENTIAL_THRESHOLD = 2;
        private List<BigInteger> integerList;
        private AtomicReference<BigInteger> result;

        private FactorialTask (CountedCompleter<BigInteger> parent,
                            AtomicReference<BigInteger> result,
                            List<BigInteger> integerList) {
            super(parent);
            this.integerList = integerList;
            this.result = result;
        }

        @Override
        public BigInteger getRawResult () {
            return result.get();
        }

       @Override
       public void compute() {

    	   if (integerList.size() <= SEQUENTIAL_THRESHOLD) {
    		   
               sumFactorials();
               
               // deve essere chiamato il tryComplete per ogni chiamata di addToPendingCount(1);
               tryComplete();
               
               // in alternativa potrebbe essere chiamato , simile al tryComplete per il decremento del pendingCount ma che non invoca il metodo onCompletion
               // propagateCompletion();
               
          } else {
        	  
               int middle = integerList.size() / 2;
               
               List<BigInteger> newList = integerList.subList(middle, integerList.size());
               integerList = integerList.subList(0, middle);
               
               // deve essere aggiunto uno al pending count, in quanto abbiamo aggiunto un task con fork
               addToPendingCount(1);
               
               FactorialTask task = new FactorialTask(this, result, newList);
               task.fork();
               this.compute();
               
          }
    	   
           
            
       }


       private void addFactorialToResult (BigInteger factorial) {
    	   result.getAndAccumulate(factorial, (b1, b2) -> b1.add(b2));
       }

       private void sumFactorials () {
    	   
    	   for (BigInteger i : integerList) {
    		   addFactorialToResult(CalcUtil.calculateFactorial(i));
    	   }
       }
       
       /**
        * 
        * CountedCompleter invoca il metodo seguente, onCompletion, a seguito del completamento del job, 
        * con il pending count a zero, e con tryComplete invocato.
        * 
        */
       @Override
       public void onCompletion(CountedCompleter<?> caller) {
    	   log.info("Azione triggerata alla fine del lavoro del CounterComplete Task..");
    	   
    	   // Eventuale clean o elaborazione sul risultato finale
    	   
    	   log.info("Passo il risultato: "+getRawResult()+" come risultato del task.");
       }
       
    }
    
    
}
