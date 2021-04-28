package eddiellopez.com.asynccall;

import org.junit.Test;

import java.util.concurrent.Executors;

import static eddiellopez.com.asynccall.TaskVerificationMatcher.completed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class EmptyThreaderTest {

    @Test(expected = NullPointerException.class)
    public void noTask() {
        // Given a threader without a task:
        final Threader<Void> threader = new EmptyThreader(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                null,
                null
        );

        // When started, we expect an exception:
        threader.start();
    }

    @Test
    public void callable() {
        final VerificationTask task = new VerificationTask();
        // Given a threader with a task.
        final Threader<Void> threader = new EmptyThreader(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                task::complete,
                null
        );
        // When started...
        threader.start();
        // We expect the task to be executed and completed.
        assertThat(task, is(completed()));
    }

    @Test
    public void resultListener() {
        final VerificationTask task = new VerificationTask();

        final OnEmptyResultListener emptyResultListener = mock(OnEmptyResultListener.class);

        // Given a threader with a task and a result listener
        final Threader<Void> threader = new EmptyThreader(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                task::complete,
                emptyResultListener
        );
        // When started...
        threader.start();
        // We expect the task to be executed and completed.
        assertThat(task, is(completed()));
        verify(emptyResultListener, after(200)).onResult();
    }

    @Test
    public void start() {
    }

    @Test
    public void getDeliveryProcedure() {
    }
}