package it.imperato.test.asyncutils.runtime;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public final class MultiException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private final List<Throwable> exceptions;

    public MultiException(final List<Throwable> inputThrowableList) {
        final List<Throwable> throwableList = new ArrayList<>();
        for (final Throwable th : inputThrowableList) {
            if (th instanceof MultiException) {
                final MultiException me = (MultiException) th;
                throwableList.addAll(me.exceptions);
            } else {
                throwableList.add(th);
            }
        }
        this.exceptions = throwableList;
    }

    @Override
    public void printStackTrace() {
        printStackTrace(System.out);
    }

    @Override
    public void printStackTrace(final PrintStream printStream) {
        super.printStackTrace(printStream);

        final int numExceptions = exceptions.size();
        printStream.println("  Number of exceptions: " + numExceptions);
        final int numExceptionsToDisplay = Math.min(5, numExceptions);
        printStream.println("  Printing " + numExceptionsToDisplay
                + " stack traces...");

        for (int i = 0; i < numExceptionsToDisplay; i++) {
            final Throwable exception = exceptions.get(i);
            exception.printStackTrace(printStream);
        }
    }

    @Override
    public String toString() {
        return exceptions.toString();
    }

    public List<Throwable> getExceptions() {
        return exceptions;
    }
}
