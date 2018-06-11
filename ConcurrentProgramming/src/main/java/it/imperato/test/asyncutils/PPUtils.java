package it.imperato.test.asyncutils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import it.imperato.test.asyncutils.config.SystemProperty;
import it.imperato.test.asyncutils.runtime.BaseTask;
import it.imperato.test.asyncutils.runtime.BaseTask.FinishTask;
import it.imperato.test.asyncutils.runtime.BaseTask.FutureTask;
import it.imperato.test.asyncutils.runtime.IsolatedManager;
import it.imperato.test.asyncutils.runtime.Runtime;

public final class PPUtils {
	
    protected static final String MISSING_FINISH = "Already other async called";

    private PPUtils() {
    }

    private static final IsolatedManager isolatedManager = new IsolatedManager();

    /**
     * Effettua la composizione dei singoli task gestiti da async.
     * I metodi async e finish garantiscono che i tutti i runnable degli async sono finiti al momento della composizione finale.
     * 
     * @param runnable
     */
    public static void finish(final Runnable runnable) {
        final BaseTask currentTask = Runtime.currentTask();
        final FinishTask newTask = new FinishTask(runnable);
        if (currentTask == null) {
            Runtime.submitTask(newTask);
        } else {
            newTask.compute();
        }
        newTask.awaitCompletion();
    }

    /**
     * Gestisce ogni task async in cui è suddivisibile ad es. il task iniziale.
     * Tutti i singoli task async saranno poi inviabili al metodo finish per la composizione finale.
     * 
     * @param runnable
     */
    public static void async(final Runnable runnable) {
        final FutureTask<Void> newTask = createFutureTask(runnable);
        newTask.fork();
    }

    public static void forasync(
            final int startInc,
            final int endInc,
            final ProcedureInt1D body) {
        assert (startInc <= endInc);

        for (int i = startInc; i <= endInc; i++) {
            final int loopIndex = i;
            final Runnable loopRunnable = () -> body.apply(loopIndex);
            async(loopRunnable);
        }
    }

    public static void forasync2d(final int startInc0, final int endInc0,
            final int startInc1, final int endInc1, final ProcedureInt2D body) {
        assert (startInc0 <= endInc0);
        assert (startInc1 <= endInc1);

        for (int i = startInc0; i <= endInc0; i++) {
            final int iCopy = i;

            for (int j = startInc1; j <= endInc1; j++) {
                final int jCopy = j;

                final Runnable loopRunnable = () -> body.apply(iCopy, jCopy);
                async(loopRunnable);
            }
        }
    }

    public static void forall(final int startInc, final int endInc,
            final ProcedureInt1D body) {
        finish(() -> {
            forasync(startInc, endInc, body);
        });
    }

    public static void forall2d(final int startInc0, final int endInc0,
            final int startInc1, final int endInc1, final ProcedureInt2D body) {
        finish(() -> {
            forasync2d(startInc0, endInc0, startInc1, endInc1, body);
        });
    }

    public static void forasyncChunked(final int start, final int endInclusive,
            final int chunkSize, final ProcedureInt1D body) {
        assert (start <= endInclusive);

        for (int i = start; i <= endInclusive; i += chunkSize) {
            final int iCopy = i;

            async(() -> {
                int end = iCopy + chunkSize - 1;
                if (end > endInclusive) {
                    end = endInclusive;
                }
                for (int innerI = iCopy; innerI <= end; innerI++) {
                    body.apply(innerI);
                }
            });
        }
    }

    public static void forasyncChunked(final int start, final int endInclusive,
            final ProcedureInt1D body) {
        forasyncChunked(start, endInclusive,
                getChunkSize(endInclusive - start + 1, numThreads() * 2), body);
    }

    public static void forasync2dChunked(final int start0,
            final int endInclusive0, final int start1, final int endInclusive1,
            final int chunkSize, final ProcedureInt2D body) {
        assert (start0 <= endInclusive0);
        assert (start1 <= endInclusive1);

        final int outerNIters = endInclusive0 - start0 + 1;
        final int innerNIters = endInclusive1 - start1 + 1;
        final int numIters = outerNIters * innerNIters;

        forasyncChunked(0, numIters - 1, chunkSize, (i) -> {
            int outer = i / innerNIters;
            int inner = i % innerNIters;

            body.apply(start0 + outer, start1 + inner);
        });
    }

    public static void forasync2dChunked(final int start0,
            final int endInclusive0, final int start1, final int endInclusive1,
            final ProcedureInt2D body) {
        final int numIters = (endInclusive0 - start0 + 1)
            * (endInclusive1 - start1 + 1);
        forasync2dChunked(start0, endInclusive0, start1, endInclusive1,
                getChunkSize(numIters, numThreads() * 2), body);
    }

    public static void forallChunked(final int start, final int endInclusive,
            final int chunkSize, final ProcedureInt1D body) {
        finish(() -> {
            forasyncChunked(start, endInclusive, chunkSize, body);
        });
    }

    public static void forallChunked(final int start, final int endInclusive,
            final ProcedureInt1D body) {
        forallChunked(start, endInclusive,
                getChunkSize(endInclusive - start + 1, numThreads() * 2), body);
    }

    public static void forall2dChunked(final int start0,
            final int endInclusive0, final int start1, final int endInclusive1,
            final int chunkSize, final ProcedureInt2D body) {
        finish(() -> {
            forasync2dChunked(start0, endInclusive0, start1, endInclusive1,
                chunkSize, body);
        });
    }

    public static void forall2dChunked(final int start0, final int endInclusive0,
            final int start1, final int endInclusive1,
            final ProcedureInt2D body) {
        final int numIters = (endInclusive0 - start0 + 1)
            * (endInclusive1 - start1 + 1);
        forall2dChunked(start0, endInclusive0, start1, endInclusive1,
                getChunkSize(numIters, numThreads() * 2), body);
    }

    public static <R> Future<R> future(final Callable<R> body) {
        final FutureTask<R> newTask = createFutureTask(body, false);
        newTask.fork();
        return newTask.future();
    }

    public static void asyncAwait(final Runnable runnable,
            final Future<? extends Object>... futures) {
        final FutureTask<Void> newTask = createFutureTask(runnable);
        CompletableFuture.
            allOf(wrapToCompletableFutures(futures)).
            whenComplete((a, b) -> newTask.fork());
    }

    public static <R> Future<R> futureAwait(final Callable<R> runnable,
            final Future<? extends Object>... futures) {
        final FutureTask<R> newTask = createFutureTask(runnable, false);
        CompletableFuture.
            allOf(wrapToCompletableFutures(futures)).
            whenComplete((a, b) -> newTask.fork());
        return newTask.future();
    }

    private static FutureTask<Void> createFutureTask(final Runnable runnable) {
        final BaseTask currentTask = Runtime.currentTask();
        if (currentTask == null) {
            throw new IllegalStateException(MISSING_FINISH);
        }
        return createFutureTask(
            () -> {
                runnable.run();
                return null;

            },
            true);
    }

    private static <R> FutureTask<R> createFutureTask(
        final Callable<R> body, final boolean rethrowException) {
        final BaseTask currentTask = Runtime.currentTask();
        if (currentTask == null) {
            throw new IllegalStateException(MISSING_FINISH);
        }
        return new FutureTask<>(body, currentTask.immediatelyEnclosingFinishOfTask(), rethrowException);
    }

    private static CompletableFuture<?>[] wrapToCompletableFutures(
            final Future<? extends Object>... futures) {
        final CompletableFuture<?>[] result =
            new CompletableFuture[futures.length];
        for (int i = 0; i < futures.length; i++) {
            final Future<? extends Object> future = futures[i];
            if (future instanceof CompletableFuture) {
                result[i] = (CompletableFuture) future;
            } else {
                throw new IllegalArgumentException("Future at index " + i
                        + " is not an instance of CompletableFuture!");
            }
        }
        return result;
    }

    public static void forseq(final int start, final int endInclusive,
            final ProcedureInt1D body) {
        assert (start <= endInclusive);

        for (int i = start; i <= endInclusive; i++) {
            body.apply(i);
        }
    }

    public static void forseq2d(final int start0, final int endInclusive0,
            final int start1, final int endInclusive1,
            final ProcedureInt2D body) {
        assert (start0 <= endInclusive0);
        assert (start1 <= endInclusive1);

        for (int i = start0; i <= endInclusive0; i++) {
            for (int j = start1; j <= endInclusive1; j++) {
                body.apply(i, j);
            }
        }
    }

    public static int numThreads() {
        return Integer.parseInt(SystemProperty.numWorkers.getPropertyValue());
    }

    private static int getChunkSize(final int nElements, final int nChunks) {
        return (nElements + nChunks - 1) / nChunks;
    }

    public static void isolated(final Runnable runnable) {
        isolatedManager.acquireAllLocks();
        try {
            runnable.run();
        } finally {
            isolatedManager.releaseAllLocks();
        }
    }

    public static void isolated(final Object obj, final Runnable runnable) {
        Object[] objArr = new Object[1];
        objArr[0] = obj;

        isolatedManager.acquireLocksFor(objArr);
        try {
            runnable.run();
        } finally {
            isolatedManager.releaseLocksFor(objArr);
        }
    }

    public static void isolated(final Object obj1, final Object obj2,
            final Runnable runnable) {
        Object[] objArr = new Object[2];
        objArr[0] = obj1;
        objArr[1] = obj2;

        isolatedManager.acquireLocksFor(objArr);
        try {
            runnable.run();
        } finally {
            isolatedManager.releaseLocksFor(objArr);
        }
    }
}
