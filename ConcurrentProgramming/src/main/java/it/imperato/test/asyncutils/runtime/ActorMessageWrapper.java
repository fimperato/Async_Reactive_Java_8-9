package it.imperato.test.asyncutils.runtime;


public final class ActorMessageWrapper {

    private final Object msg;

    private ActorMessageWrapper next;

    public ActorMessageWrapper(final Object setMsg) {
        this.msg = setMsg;
        this.next = null;
    }

    public void setNext(final ActorMessageWrapper setNext) {
        this.next = setNext;
    }

    public ActorMessageWrapper getNext() {
        return next;
    }

    public Object getMessage() {
        return msg;
    }
}
