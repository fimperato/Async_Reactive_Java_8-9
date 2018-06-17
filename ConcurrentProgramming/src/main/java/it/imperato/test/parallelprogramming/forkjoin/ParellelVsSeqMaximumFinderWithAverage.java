package it.imperato.test.parallelprogramming.forkjoin;

import java.util.ArrayList;
import java.util.List;
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

public class ParellelVsSeqMaximumFinderWithAverage extends RecursiveTask<Integer> {

	private static Logger log = LogManager.getLogger(ParellelVsSeqMaximumFinderWithAverage.class);
	
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
	    
        ExecutorService executor = Executors.newFixedThreadPool(5);
        Callable<String> callable = new TestBaseThread();
        Future<String> value = executor.submit(callable);
        log.info("TestBaseThread returned value is : "+value.get());
        executor.shutdown();
        
	    
	    Thread.sleep(100);
	    
	    // sequential:
	    Callable<Long> sequential = () -> {
	    	Long resultTime = null;
	    	try {
	    		resultTime = sequentialComputing(data, 0, data.length);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("");
			}
	    	return resultTime;
	    };
	    
	    // parallel:
	    Callable<Long> parallel = () -> {
	    	Long resultTime = null;
	    	try {
	    		resultTime = parallelComputing(data);
	    	} catch (Exception e) {
				e.printStackTrace();
				log.error("");
	    	}
	    	return resultTime;
	    };
	    
	    ExecutorService compareResultExecutor = Executors.newFixedThreadPool(5);
	    List<Future<Long>> seqresults = new ArrayList<Future<Long>>();
	    List<Future<Long>> parallelresults = new ArrayList<Future<Long>>();
        for(int j=0;j<100;j++) {
	        Future<Long> futureResSequential = compareResultExecutor.submit(sequential);
	        Future<Long> parallelResSequential = compareResultExecutor.submit(parallel);
	    	seqresults.add(futureResSequential);
	    	parallelresults.add(parallelResSequential);
		    Thread.sleep(500);
	    }
	    compareResultExecutor.shutdown();
	    Long averageSeqResult = 0L;
	    int nulli=0;
	    for(Future<Long> seqresult : seqresults) {
	    	if(seqresult.get()!=null) {
	    		averageSeqResult += seqresult.get();
	    	} else {
	    		nulli++;
	    	}
	    }
	    averageSeqResult = averageSeqResult/(seqresults.size()-nulli);
    	log.info("\n\n Average seconds sequential : "+averageSeqResult/1e9);
    	
	    Long averageParallelResult = 0L;
	    nulli=0;
	    for(Future<Long> parallelresult : parallelresults) {
	    	if(parallelresult.get()!=null) {
	    		averageParallelResult += parallelresult.get();
	    	} else {
	    		nulli++;
	    	}
	    }
	    averageParallelResult = averageParallelResult/(parallelresults.size()-nulli);
    	log.info("\n\n Average seconds parallel : "+averageParallelResult/1e9);
	}

	private static Long parallelComputing(final int[] data) {
		long startTime = System.nanoTime();
	    // submit the task to the pool
	    final ForkJoinPool pool = new ForkJoinPool(4);
	    final ParellelVsSeqMaximumFinderWithAverage finder = new ParellelVsSeqMaximumFinderWithAverage(data);
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
    	return timeInNanos;
	}
	
	public ParellelVsSeqMaximumFinderWithAverage(int[] data, int start, int end) {
	    this.data = data;
	    this.start = start;
	    this.end = end;
	}

	public ParellelVsSeqMaximumFinderWithAverage(int[] data) {
	    this(data, 0, data.length);
	}
	
	@Override
	protected Integer compute() {
	    final int length = end - start;
	    if (length < SEQUENTIAL_THRESHOLD) {
	      return computeDirectly();
	    }
	    final int split = length / 2;
	    final ParellelVsSeqMaximumFinderWithAverage left = new ParellelVsSeqMaximumFinderWithAverage(data, start, start + split);
	    left.fork();
	    final ParellelVsSeqMaximumFinderWithAverage right = new ParellelVsSeqMaximumFinderWithAverage(data, start + split, end);
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
	
	public static Long sequentialComputing(int[] data, int start, int end) throws Exception {
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
	    return timeInNanos;
	}

}
