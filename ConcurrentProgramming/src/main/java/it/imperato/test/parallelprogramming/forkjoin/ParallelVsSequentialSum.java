package it.imperato.test.parallelprogramming.forkjoin;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.utils.Utils;

/**
 * Comparazione tempi tra calcolo sequenziale e parallelo per 1 milione di numeri 
 * da elaborare (semplice somma come elaborazione)
 * 
 * 
 */
public class ParallelVsSequentialSum {

	private static Logger log = LogManager.getLogger(ParallelVsSequentialSum.class);
	
	private static int DEFAULT_N = (int) 5e7; // 50 milioni
    private static String ERROR_MSG = "ERRORE";
    
    public static void main(String[] argv) throws Exception {
    	
//        int length = (int) 5e7; // 50 milioni
//
//        double[] arr = Utils.generateArraySeq(length);
//
//        for (int numRun = 0; numRun < 5; numRun++) {
//        	log.info("\n\n Run {} \n", numRun);
//            seqArraySum(arr);
//            parArraySum(arr);
//        }
        
        int n;
    	if(argv.length !=0) {
    		try {
    			n = Integer.parseInt(argv[0]);
    			if(n <=0 ) {
    				// valor incorrecto de n
    				log.error(ERROR_MSG);
    				n = DEFAULT_N;
    			}
    		} catch (Throwable e) {
    			log.error(ERROR_MSG);
    			n = DEFAULT_N;
    		}
    	} else { // argv.length == 0
    		n = DEFAULT_N;
    	}
    	double[] X = new double[n]; 
    	
    	for(int i=0; i <n; i++) {
    		X[i] = (i + 1);
    	}
    	
    	// definizione del numero di 'workers' da utilizzare per il ForKJoinPool.commonPool()
    	System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "4");
    	
    	for (int numRun = 0; numRun < 5; numRun++) {
        	log.info("\n\n Run {} \n", numRun);
    		seqArraySum(X);
    		parArraySum(X);
    	}
    }
    	
    public static double seqArraySum(double[] X) {
    	long startTime = System.nanoTime();
    	double sum = 0;
    	for (int i=0; i < X.length; i++) {
    		sum += 1/X[i];
    	}
    	long timeInNanos = System.nanoTime() - startTime;
    	Utils.printResult("sequentialComputing", timeInNanos, "sum", sum);
    	return sum;
    }

    /**
     * <p>parArraySum</p>
     * Versione parallelizzata.
     * 
     * @param X array di double
     * @return sum of 1/X[i] for 0 <= i < X.length
    */
    public static double parArraySum(double[] X) {
    	int nThreads = Runtime.getRuntime().availableProcessors();
        log.info("availableProcessors :"+nThreads);
        ForkJoinPool forkJoinPool = new ForkJoinPool(nThreads);
        
    	long startTime = System.nanoTime();
    	SumArray t = new SumArray(X, 0, X.length);
//    	ForkJoinPool.commonPool().invoke(t);
    	forkJoinPool.invoke(t);
    	double sum = t.ans;
    	long timeInNanos = System.nanoTime() - startTime;
    	Utils.printResult("parallelComputing", timeInNanos, "sum", sum);
    	return sum;
    }
    
    private static class SumArray extends RecursiveAction {

		private static final long serialVersionUID = 1L;
		
		static int SEQUENTIAL_THRESHOLD = 5;
		
    	int lo;
    	int hi;
    	double arr[];
    	double ans = 0;
    	
    	SumArray(double[] a, int l, int h) {
    		lo =l;
    		hi = h;
    		arr = a;
    	}
    	
    	protected void compute() {
    		if (hi - lo <= SEQUENTIAL_THRESHOLD) {
    			for (int i = lo; i < hi; ++i)
    				ans += 1 / arr[i];
    		} else {
    			SumArray left = new SumArray(arr, lo, (hi + lo) /2);
    			SumArray right = new SumArray(arr, (hi + lo) /2, hi);
    			left.fork();
    			right.compute();
    			left.join();
    			ans = left.ans + right.ans;
    		}
    	} 
    } 
    
}
