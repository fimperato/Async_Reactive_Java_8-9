package it.imperato.test.java8.parallelism.threads;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;

public class Task02 implements Callable<String> {

    private static Logger log  = LogManager.getLogger(Task02.class);

    private String task = "task02: ";

    public Task02(String taskAdd) {
        this.task += taskAdd;
    }

    @Override
    public String call() throws Exception {
        log.info("task 02 called - done.."+task);
        return this.task;
    }
}