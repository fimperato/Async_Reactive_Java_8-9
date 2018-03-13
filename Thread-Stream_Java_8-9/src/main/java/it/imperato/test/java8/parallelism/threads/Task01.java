package it.imperato.test.java8.parallelism.threads;

import it.imperato.test.java8.parallelism.threads.t01.ExecutorServiceOne;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;

public class Task01 implements Callable<String> {

    private static Logger log  = LogManager.getLogger(Task01.class);

    private String task = "task01: ";

    public Task01(String taskAdd) {
        this.task += taskAdd;
    }

    @Override
    public String call() throws Exception {
        log.info("task 01 called.."+task);
        int randomTaskSleep = ThreadLocalRandom.current().nextInt(0, 4);
        log.warn("thread "+task+" sleep for: "+randomTaskSleep+" seconds..............");
        Thread.sleep(randomTaskSleep*1000);

        log.info("task 01 done.."+task);
        return this.task;
    }
}