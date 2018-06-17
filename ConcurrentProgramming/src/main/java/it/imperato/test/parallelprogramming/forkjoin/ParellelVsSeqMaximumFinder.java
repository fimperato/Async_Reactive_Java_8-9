package it.imperato.test.parallelprogramming.forkjoin;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Stopwatch;

import it.imperato.test.utils.Utils;
import it.imperato.test.utils.thread.TestBaseThread;

public class ParellelVsSeqMaximumFinder extends RecursiveTask<Integer> {

	private static Logger log = LogManager.getLogger(ParellelVsSeqMaximumFinder.class);
	
	private static final long serialVersionUID = 1L;

	private static final int SEQUENTIAL_THRESHOLD = 100_000_000;

	private final int[] data;
	private final int start;
	private final int end;


	public static void main(String[] args) throws Exception {
	    // create a random data set
	    final int[] data = new int[(int) 5e8];
	    
	    long startTime = System.nanoTime();
	    log.info("## DATA INIT START TIME is: " + startTime);
	    final Random random = new Random();
	    for (int i = 0; i < data.length; i++) {
	      data[i] = random.nextInt(100);
	    }
	    log.info("data length: "+data.length);
	
	    long endTime = System.nanoTime();
	    log.info("## DATA INIT END TIME is: " + endTime);
    	long timeInNanos = endTime - startTime;
	    log.info("DATA INIT time is: " + timeInNanos/1e9);
	    
	    Thread.sleep(100);
	    
	    // sequential:
	    Runnable sequential = () -> {
	    	try {
				sequentialComputing(data, 0, data.length);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("");
			}
	    };
	    
	    Runnable parallel = () -> {
	    	try {
	    		parallelComputing(data);
	    	} catch (Exception e) {
				e.printStackTrace();
				log.error("");
	    	}
	    };
	    
	    for(int j=0;j<5;j++) {
		    Thread tsequential = new Thread(sequential);
		    Thread tparallel = new Thread(parallel);
	    	log.info("\n\n Run n."+j);
	    	tsequential.start();
	    	tparallel.start();
		    Thread.sleep(500);
	    }
	    
	}

	private static void parallelComputing(final int[] data) {
		long startTime = System.nanoTime();
	    // submit the task to the pool
	    final ForkJoinPool pool = new ForkJoinPool(4);
	    final ParellelVsSeqMaximumFinder finder = new ParellelVsSeqMaximumFinder(data);
	    log.debug(pool.invoke(finder));

    	long endTime = System.nanoTime();
	    long timeInNanos = endTime - startTime;
	    log.info("time is: " + timeInNanos/1e9);
    	try {
			Utils.printResult("parallelComputing", timeInNanos, "max", finder.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			log.error("Exception in parallelComputing "+e.getMessage());
		}
	}
	
	public ParellelVsSeqMaximumFinder(int[] data, int start, int end) {
	    this.data = data;
	    this.start = start;
	    this.end = end;
	}

	public ParellelVsSeqMaximumFinder(int[] data) {
	    this(data, 0, data.length);
	}
	
	@Override
	protected Integer compute() {
	    final int length = end - start;
	    if (length < SEQUENTIAL_THRESHOLD) {
	      return computeDirectly();
	    }
	    final int split = length / 2;
	    final ParellelVsSeqMaximumFinder left = new ParellelVsSeqMaximumFinder(data, start, start + split);
	    left.fork();
	    final ParellelVsSeqMaximumFinder right = new ParellelVsSeqMaximumFinder(data, start + split, end);
	    return Math.max(right.compute(), left.join());
	}
	
	public Integer computeDirectly() {
	    log.debug(Thread.currentThread() + " computing: " + start + " to " + end);
	    int max = Integer.MIN_VALUE;
	    for (int i = start; i < end; i++) {
	      if (data[i] > max) {
	        max = data[i];
	      }
	    }
	    return max;
	}
	
	public static Integer sequentialComputing(int[] data, int start, int end) throws Exception {
	    log.info(Thread.currentThread() + " sequentialComputing ");
	    long startTime = System.nanoTime();
	    log.info("## START TIME is: " + startTime);
	    Stopwatch timerStopwatch = Stopwatch.createStarted();
		
	    int max = Integer.MIN_VALUE;
	    for (int i = start; i < end; i++) {
	      if (data[i] > max) {
	        max = data[i];
	      }
	    }	    

	    long endTime = System.nanoTime();
	    log.info("## END TIME is: " + endTime);
    	long timeInNanos = endTime - startTime;
	    log.info("time is: " + timeInNanos/1e9);
		log.info("time is by Stopwatch: " + timerStopwatch.stop());
    	Utils.printResult("sequentialComputing", timeInNanos, "max", max);
	    return max;
	}

}
