package it.imperato.test.asyncutils;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import it.imperato.test.asyncutils.runtime.ActorMessageWrapper;

public abstract class Actor {

    private final ConcurrentLinkedQueue<ActorMessageWrapper> queue =
        new ConcurrentLinkedQueue<ActorMessageWrapper>();

    private final AtomicInteger queueSize = new AtomicInteger(0);

    public abstract void process(Object msg);

    public final void send(final Object msg) {
        ActorMessageWrapper wrapper = new ActorMessageWrapper(msg);

        final int oldQueueSize = queueSize.getAndIncrement();
        queue.add(wrapper);

        if (oldQueueSize == 0) {
            PPUtils.async(() -> {

                boolean done = false;
                while (!done) {

                    ActorMessageWrapper curr;
                    do {
                        curr = queue.poll();
                    } while (curr == null);

                    process(curr.getMessage());

                    final int newQueueSize = queueSize.decrementAndGet();
                    done = (newQueueSize == 0);
                }
            });
        }
    }
}
