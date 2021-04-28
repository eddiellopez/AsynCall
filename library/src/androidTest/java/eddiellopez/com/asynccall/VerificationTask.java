package eddiellopez.com.asynccall;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

class VerificationTask {

    enum Status {
        IN_PROGRESS,
        COMPLETED,
        TIME_ELAPSED
    }

    public static VerificationTask COMPLETED_ON_TIME = new VerificationTask(Status.COMPLETED);

    private final CountDownLatch countDownLatch;

    private Status status = Status.IN_PROGRESS;

    public VerificationTask() {
        countDownLatch = new CountDownLatch(1);
    }

    private VerificationTask(Status initialStatus) {
        status = initialStatus;
        countDownLatch = new CountDownLatch(1);
    }

    public boolean awaitForCompletion(long seconds) throws InterruptedException {
        if (countDownLatch.await(seconds, TimeUnit.SECONDS)) {
            status = Status.COMPLETED;
            return true;
        } else {
            status = Status.TIME_ELAPSED;
            return false;
        }
    }

    public void complete() {
        countDownLatch.countDown();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return "VerificationTask{" +
                "status=" + status +
                '}';
    }
}
