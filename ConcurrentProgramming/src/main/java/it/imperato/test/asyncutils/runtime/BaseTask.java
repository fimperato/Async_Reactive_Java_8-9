package it.imperato.test.asyncutils.runtime;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Estensione della classe CountedCompleter con relative utility
 * 
 *
 */
public abstract class BaseTask extends CountedCompleter<Void> {

	private static final long serialVersionUID = 1L;

    public BaseTask() {
        super();
    }

    public abstract FinishTask immediatelyEnclosingFinishOfTask();

    public static final class FinishTask extends BaseTask {

		private static final long serialVersionUID = 1L;

        protected static final AtomicLong TASK_COUNTER = new AtomicLong();

        /*
         * Contiene il runnable del task passato
         */
        private final Runnable runnable;

        private final CountDownLatch countDownLatch;

        private List<Throwable> exceptionList;

        public FinishTask(final Runnable setRunnable) {
            super();
            this.runnable = setRunnable;
            this.countDownLatch = new CountDownLatch(1);
            this.exceptionList = null;
            TASK_COUNTER.incrementAndGet();
        }

        @Override
        public void compute() {
            // Make this the current task for the current runtime thread
            Runtime.pushTask(this);
            try {
                // Execute the body
                runnable.run();
            } catch (final Throwable th) {
                pushException(th);
            } finally {
                tryComplete();
                Runtime.popTask();
                awaitCompletion();
            }
        }

        @Override
        public void onCompletion(final CountedCompleter<?> caller) {
            countDownLatch.countDown();
        }

        public void awaitCompletion() {
            try {
                countDownLatch.await();
            } catch (final InterruptedException ex) {
                pushException(ex);
            }

            final List<Throwable> finalExceptionList = exceptions();
            if (!finalExceptionList.isEmpty()) {
                if (finalExceptionList.size() == 1) {
                    final Throwable t = finalExceptionList.get(0);
                    if (t instanceof Error) {
                        throw (Error) t;
                    } else if (t instanceof RuntimeException) {
                        throw (RuntimeException) t;
                    }
                }
                throw new MultiException(finalExceptionList);
            }
        }

        @Override
        public FinishTask immediatelyEnclosingFinishOfTask() {
            return this;
        }

        private List<Throwable> exceptions() {
            if (exceptionList == null) {
                exceptionList = new ArrayList<>();
            }
            return exceptionList;
        }

        public void pushException(final Throwable throwable) {
            synchronized (this) {
                this.exceptions().add(throwable);
            }
        }
    }

    public static final class FutureTask<R> extends BaseTask {

		private static final long serialVersionUID = 1L;

        protected static final AtomicLong TASK_COUNTER = new AtomicLong();

        private final Runnable runnable;

        private final FinishTask immediatelyEnclosingFinish;

        private final AtomicBoolean cancellationFlag = new AtomicBoolean(false);

        private final CompletableFuture<R> completableFuture =
            new CompletableFuture<R>() {
                @Override
                public boolean cancel(final boolean mayInterruptIfRunning) {
                    return cancellationFlag.compareAndSet(false, true)
                        && super.cancel(mayInterruptIfRunning);
                }
            };

        public FutureTask(
            final Callable<R> setRunnable,
            final FinishTask setImmediatelyEnclosingFinish,
            final boolean rethrowException) {
            super();
            if (setImmediatelyEnclosingFinish == null) {
                throw new IllegalStateException(
                        "Async is not executing inside a finish!");
            }
            this.runnable = () -> {
                try {
                    final R result = setRunnable.call();
                    completableFuture.complete(result);
                } catch (final Exception ex) {
                    completableFuture.completeExceptionally(ex);
                    if (rethrowException) {
                        if (ex instanceof RuntimeException) {
                            throw (RuntimeException) ex;
                        } else {
                            throw new RuntimeException(
                                    "Error in executing callable", ex);
                        }
                    }
                }
            };
            this.immediatelyEnclosingFinish = setImmediatelyEnclosingFinish;
            this.immediatelyEnclosingFinish.addToPendingCount(1);
            TASK_COUNTER.incrementAndGet();
        }

        @Override
        public void compute() {
            Runtime.pushTask(this);
            try {
                if (!cancellationFlag.get()) {
                    // execute the body
                    runnable.run();
                }
            } catch (final Throwable th) {
                immediatelyEnclosingFinish.pushException(th);
            } finally {
                immediatelyEnclosingFinish.tryComplete();
                Runtime.popTask();
            }
        }

        @Override
        public FinishTask immediatelyEnclosingFinishOfTask() {
            return immediatelyEnclosingFinish;
        }

        public CompletableFuture<R> future() {
            return completableFuture;
        }
    }
}
