package it.imperato.test.parallelprogramming.forkjoin;

import java.util.Arrays;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ParallelVsSeqSortingMain {
	
	private static Logger log = LogManager.getLogger(ParallelVsSeqSortingMain.class);
	
	public static void main(String[] args) {
	      long[] array = new long[(int) 3e7];
	      for (int i = 0; i < array.length; i++)
	         array[i] = (long) (Math.random()*10000000);
	      long[] array2 = new long[array.length];
	      System.arraycopy(array, 0, array2, 0, array.length);

	      long startTime = System.currentTimeMillis();
	      Arrays.sort(array, 0, array.length-1);
	      log.info("sequential sort completed in {} seconds", 
	                        (System.currentTimeMillis()-startTime)/1e3);
	      
	      // check
	      for (int i = 0; i < array.length; i++) {
	    	  log.debug(array[i]);
	      }


	      ForkJoinPool pool = new ForkJoinPool();
	      startTime = System.currentTimeMillis();
	      pool.invoke(new ParallelVsSeqSortTask(array2));
	      log.info("parallel sort completed in {} seconds", 
	                        (System.currentTimeMillis()-startTime)/1e3);
	      
	      // check
	      for (int i = 0; i < array2.length; i++) {
	    	  log.debug(array2[i]);
	      }
	}
	
	static class ParallelVsSeqSortTask extends RecursiveAction {
	   
		private static final long serialVersionUID = 1L;
		
		private final long[] array; 
	    private final int lo, hi;
	
	    ParallelVsSeqSortTask(long[] array, int lo, int hi) {
	         this.array = array; 
	         this.lo = lo; 
	         this.hi = hi;
	    }
	
	    ParallelVsSeqSortTask(long[] array) { 
	    	this(array, 0, array.length); 
	    }
	    
	    private final static int THRESHOLD = 1000;
	
	    @Override
	    protected void compute() {
	
	    	if (hi-lo < THRESHOLD) {
	            sortSequentially(lo, hi);
	    	}
	        else 
	        {
	            int mid = (lo+hi) >>> 1;
	            invokeAll(new ParallelVsSeqSortTask(array, lo, mid),
	                      new ParallelVsSeqSortTask(array, mid, hi));
	            merge(lo, mid, hi);
	        }
	    }
	
	    private void sortSequentially(int lo, int hi) {
	    	Arrays.sort(array, lo, hi);
	    }
	
	    private void merge(int lo, int mid, int hi) {
	    	long[] buf = Arrays.copyOfRange(array, lo, mid);
	        for (int i = 0, j = lo, k = mid; i < buf.length; j++) {
	            array[j] = (k == hi || buf[i] < array[k]) ? buf[i++] : array[k++];
	        }
	    }
	}
   
}