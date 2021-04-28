package eddiellopez.com.asynccall;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

class TaskVerificationMatcher extends BaseMatcher<VerificationTask> {

    public static TaskVerificationMatcher completed() {
        return new TaskVerificationMatcher();
    }

    @Override
    public boolean matches(Object item) {
        if (item instanceof VerificationTask) {
            final VerificationTask task = (VerificationTask) item;
            try {
                return task.awaitForCompletion(3);
            } catch (InterruptedException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(String.valueOf(VerificationTask.COMPLETED_ON_TIME));
    }
}
