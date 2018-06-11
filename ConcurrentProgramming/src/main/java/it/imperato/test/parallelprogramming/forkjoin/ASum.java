package it.imperato.test.parallelprogramming.forkjoin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Array Sum (Parallel tasks)
 * 
 * @author francesco
 *
 */
public class ASum extends RecursiveAction {

	private static final long serialVersionUID = 1L;
	
	private static Logger log = LogManager.getLogger(ASum.class);
    
	int SOGLIA_FORK = 2;
	
	int[] A = null; // input array
	int LO, HI; // subrange
	static int SUM; // return value
	
	public ASum(int[] arr) {
		this.A = arr;
	}
	
	@Override
	protected void compute() {
		log.info("Compute start.");
		List<ASum> tasks = new ArrayList<>();        
		
		if(this.A.length>SOGLIA_FORK) {
			int middle = this.A.length / 2;
	        int[] newArrTask = new int[middle];
	        for (int i = 0; i < middle; i++) {
				newArrTask[i] = this.A[middle+i];
			}
	        ASum arrSumTask = new ASum(newArrTask);
	        tasks.add(arrSumTask);
	        arrSumTask.fork();
	        
	        int[] primaParteArr = new int[middle];
	        for (int i = 0; i < middle; i++) {
	        	primaParteArr[i] = this.A[i];
			}
        	this.A = primaParteArr;
	        
		}
		
		// compute della prima parte del fork
		for (int i = 0; i < this.A.length; i++) {
			SUM += A[i];
		}
		
		if (tasks.size() > 0) {
            for (ASum task : tasks) {
                task.join();
            }
        }
		
	} // compute()
	
	
	public static void main (String[] args) {
        long time = System.currentTimeMillis();
        
        int[] arr = new int[]{1,5,10,20}; // input array
        ASum arrSum = new ASum(arr);
        
        // Chiamo i task in parallelo:
        ForkJoinPool.commonPool().invoke(arrSum);
        
        log.info("Somma totale con elaborazione parallela: " + SUM);           
        log.info("Time elaborazione parallela : "+(System.currentTimeMillis()-time));        
    }
}