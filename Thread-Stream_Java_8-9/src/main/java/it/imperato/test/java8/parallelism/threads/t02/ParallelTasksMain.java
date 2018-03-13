package it.imperato.test.java8.parallelism.threads.t02;

import it.imperato.test.java8.parallelism.t01.ParallelismJava;
import it.imperato.test.java8.parallelism.threads.Task01;
import it.imperato.test.java8.parallelism.threads.Task02;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class ParallelTasksMain {

    private static Logger log  = LogManager.getLogger(ParallelTasksMain.class);

    public void execute() throws InterruptedException, ExecutionException {

        log.info("########## invokeAll: waits until all results are computed before it returns.");
        List<Callable<String>> callables = new LinkedList<Callable<String>>();
        callables.add(new Task01(" !!my-task-1"));
        callables.add(new Task02(" !!my-task-2"));

        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<Future<String>> list = null;
        list = executor.invokeAll(callables);
        //executor.submit(new Task01(" !!my-task-1"));
        //executor.submit(new Task02(" !!my-task-2"));
        executor.submit(new Task02(" !!my-task-3"));

        log.info("########## loop/submit: it returns a Future with the pending result immediately (then code can traverse the list with futures in order).");
        ExecutorService executor2 = Executors.newFixedThreadPool(20);
        List<Future<String>> resExecutor2 = new ArrayList<>();
        for(int i=1; i<=20; i++) {
            resExecutor2.add(executor2.submit(new Task01("@@@@@__(exc-2)_"+i)));
        }
        try {
            for(Future<String> result: resExecutor2) {
                log.info("@ResExecutor2@: "+result.get());
            }
        } catch (Exception e) {
            log.error("ERROR "+e.getMessage());
        }

        log.info("########## newFixedThreadPool n-threads: we can have n-parallel task.");
        ExecutorService executor3 = Executors.newFixedThreadPool(15);
        for (int i = 0; i < 10; i++) {
            executor.submit(new Task01(" #__my-task-1b"+i));
        }

        // thread result by Future
        String res = "";
        if(list!=null) {
            for (Future<String> future : list) {
                res += future.get();
            }
        }
        // thread pool close
        executor.shutdown();

        // log result
        log.info( res );
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        new ParallelTasksMain().execute();
    }

}
