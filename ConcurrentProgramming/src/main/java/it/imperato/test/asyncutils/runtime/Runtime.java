package it.imperato.test.asyncutils.runtime;

import it.imperato.test.asyncutils.config.Configuration;
import it.imperato.test.asyncutils.config.SystemProperty;

import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

public final class Runtime {

    private Runtime() {
    }

    private static final ThreadLocal<Stack<BaseTask>> threadLocalTaskStack =
        new ThreadLocal<Stack<BaseTask>>() {
            @Override
            protected Stack<BaseTask> initialValue() {
                return new Stack<>();
            }
        };

    private static ForkJoinPool taskPool = new ForkJoinPool(
            Configuration.readIntProperty(SystemProperty.numWorkers));

    public static void resizeWorkerThreads(final int numWorkers)
            throws InterruptedException {
        taskPool.shutdown();
        boolean terminated = taskPool.awaitTermination(10, TimeUnit.SECONDS);
        assert (terminated);

        SystemProperty.numWorkers.set(numWorkers);
        taskPool = new ForkJoinPool(numWorkers);
    }

    public static BaseTask currentTask() {
        final Stack<BaseTask> taskStack = Runtime.threadLocalTaskStack.get();
        if (taskStack.isEmpty()) {
            return null;
        } else {
            return taskStack.peek();
        }
    }

    public static void pushTask(final BaseTask task) {
        Runtime.threadLocalTaskStack.get().push(task);
    }

    public static void popTask() {
        Runtime.threadLocalTaskStack.get().pop();
    }

    public static void submitTask(final BaseTask task) {
        taskPool.execute(task);
    }

    public static void showRuntimeStats() {
        System.out.println("Runtime Stats (" + Configuration.BUILD_INFO
                + "): ");
        System.out.println("   " + taskPool.toString());
        System.out.println("   # finishes = "
                + BaseTask.FinishTask.TASK_COUNTER.get());
        System.out.println("   # asyncs = "
                + BaseTask.FutureTask.TASK_COUNTER.get());
    }
}
