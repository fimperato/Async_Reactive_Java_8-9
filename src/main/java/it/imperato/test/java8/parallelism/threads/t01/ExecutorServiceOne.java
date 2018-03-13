package it.imperato.test.java8.parallelism.threads.t01;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ExecutorServiceOne {

    private static Logger log  = LogManager.getLogger(ExecutorServiceOne.class);

    public static void main(String[] args){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.submit(new Runnable(){
            public void run(){
                log.info("Executor 1 Thread: " + Thread.currentThread().getName());
            }
        });

        try {
            log.info("Try to shutdown executor..");
            int[] intArray = new int[100];
            // test
            for (int i=0; i < intArray.length; i++) {
                intArray[i] = ThreadLocalRandom.current().nextInt(0, 2);
            }
            int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
            if(randomNum>0) {
                executor.shutdown();
            }
            executor.awaitTermination(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error("ERROR in shutdown executor service: "+e.getMessage());
        } finally {
            if (!executor.isTerminated()) {
                log.info("Shutdown requested .. in timeout: force shutdown now.");
                executor.shutdownNow();
                log.info("Shutdown done.");
            }
        }
    }
}
