package io.elastest.qe.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class CountDownLatchWithException {

    public class AbortedException extends Exception {

        private static final long serialVersionUID = 1L;

        public AbortedException(String errorMessage) {
            super(errorMessage);
        }
    }

    private CountDownLatch latch;
    private AtomicBoolean aborted = new AtomicBoolean(false);
    private List<String> errorMessages = new ArrayList<>();

    public CountDownLatchWithException(int countDown) {
        this.latch = new CountDownLatch(countDown);
    }

    public void await() throws AbortedException {
        try {
            this.latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
            if (aborted.get()) {
                throw new AbortedException(getErrorMessage());
            }
        }
        if (aborted.get()) {
            throw new AbortedException(getErrorMessage());
        }
    }

    public synchronized void abort(String errorMessage) {
        this.aborted.set(true);
        this.errorMessages.add(errorMessage);
        while (this.latch.getCount() > 0) {
            this.countDown();
        }
    }

    public void countDown() {
        latch.countDown();
    }

    public String getErrorMessage() {
        String errorMessage = "Aborted";

        if (errorMessages != null && errorMessages.size() > 0) {
            if (errorMessages.size() == 1) {
                return "Aborted by error => " + errorMessages.get(0);
            } else {
                errorMessage = "Aborted by multiple errors => "
                        + errorMessages.toString();
            }
        }

        return errorMessage;
    }

}
