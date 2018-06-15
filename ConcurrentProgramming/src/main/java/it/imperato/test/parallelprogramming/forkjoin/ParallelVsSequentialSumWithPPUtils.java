package it.imperato.test.parallelprogramming.forkjoin;

import java.util.concurrent.atomic.AtomicReference;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.imperato.test.asyncutils.PPUtils;
import it.imperato.test.utils.Utils;

/**
 * Comparazione tempi tra calcolo sequenziale e parallelo per 1 milione di numeri 
 * da elaborare (semplice somma come elaborazione)
 * 
 * 
 */
public class ParallelVsSequentialSumWithPPUtils {

	private static Logger log = LogManager.getLogger(ParallelVsSequentialSumWithPPUtils.class);
	
	private static double sum1;
    private static double sum2;
    
    public static void main(String[] args) throws Exception {
    	
        int length = (int) 5e7; // 50 milioni

        double[] arr = Utils.generateArraySeq(length);

        for (int numRun = 0; numRun < 5; numRun++) {
        	log.info("\n Run {} \n", numRun);
//            PPUtils.finish(() -> {
                seqArraySum(arr);
                parArraySum(arr);
//            });
}
    }
    	
	public static double seqArraySum(final double[] arr) {
        final long startTime = System.nanoTime();
        double sum1 = 0;
        double sum2 = 0;
        // Compute sum of lower half of array
        for (int i = 0; i < arr.length / 2; i++) {
            sum1 += 1 / arr[i];
        }
        // Compute sum of upper half of array
        for (int i = arr.length / 2; i < arr.length; i++) {
            sum2 += 1 / arr[i];
        }
        // Combine sum1 and sum2
        final double sum = sum1 + sum2;
        final long timeInNanos = System.nanoTime() - startTime;

        Utils.printResult("task: sequential", timeInNanos, "sum", sum);
        // Task T0 waits for Task T1 (join)
        return sum;
	}
	
	public static double parArraySum(double[] arr) throws Exception {
        // Start of Task T0 (main program)
        final long startTime = System.nanoTime();
        sum1 = 0;
        sum2 = 0;
        PPUtils.finish(() -> {
        	PPUtils.async(() -> {
                // Compute sum of lower half of array
                for (int i = 0; i < arr.length / 2; i++) {
                    sum1 += 1 / arr[i];
                }
            });
            // Compute sum of upper half of array
            for (int i = arr.length / 2; i < arr.length; i++) {
                sum2 += 1 / arr[i];
            }
        });
        // Combine sum1 and sum2
        final double sum = sum1 + sum2;
        final long timeInNanos = System.nanoTime() - startTime;
        
        Utils.printResult("parArraySum", timeInNanos, "sum", sum);
        // Task T0 waits for Task T1 (join)
        return sum;
	}

    public static double parArraySum_v2(double[] arr) {
    	long startTime = System.nanoTime();
        AtomicReference<Double> atomicReference = new AtomicReference<Double>(0.0d);
        AtomicReference<Double> atomicReference2 = new AtomicReference<Double>(0.0d);

        // find sum of lower-half
        PPUtils.finish(() -> {
	        PPUtils.async(() -> {
	            for (int i = 0; i < arr.length / 2; i++) {
	                atomicReference.set(atomicReference.get() + 1 / arr[i]);
	            }
	        });
	
	        PPUtils.async(() -> {
		        // find sum of upper-half
		        for (int i = arr.length / 2 + 1; i < arr.length; i++) {
		        	atomicReference2.set(atomicReference2.get() + 1 / arr[i]);
		        }
	        });
	       
   		});

        // Combine both sums
        double sum = atomicReference2.get() + atomicReference.get();

        long endTime = System.nanoTime();
        long timeInNanos = endTime - startTime;
        Utils.printResult("task: parallel", timeInNanos, "sum", sum);

        return sum;
    }

    
}
