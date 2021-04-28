package eddiellopez.com.asynccall;

import org.junit.Test;
import org.mockito.Mockito;

import java.util.concurrent.Executors;

import static eddiellopez.com.asynccall.TaskVerificationMatcher.completed;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResultThreaderTest {

    @Test(expected = NullPointerException.class)
    public void noCallable() {
        // Given a threader without a callable task:
        final Threader<String> threader = new ResultThreader<>(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                null,
                null
        );

        // When started, we expect a NullPointer exception:
        threader.start();
    }

    @Test
    public void callable() {
        final VerificationTask task = new VerificationTask();
        // Given a threader with a task.
        final Threader<String> threader = new ResultThreader<>(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                () -> {
                    task.complete();
                    return "Result";
                },
                null
        );
        // When started...
        threader.start();
        // We expect the task to be executed and completed.
        assertThat(task, is(completed()));
    }

    @Test
    public void consumableResultListener() {
        final VerificationTask task = new VerificationTask();

        //noinspection unchecked
        final OnConsumableResultListener<String> resultListener = mock(OnConsumableResultListener.class);

        // Given a threader with a task and a result listener
        final Threader<String> threader = new ResultThreader<>(
                Executors.newSingleThreadExecutor(),
                null,
                null,
                () -> {
                    task.complete();
                    return "Done.";
                },
                resultListener
        );
        // When started...
        threader.start();
        // We expect the task to be executed and completed.
        assertThat(task, is(completed()));
        verify(resultListener, after(200)).onResult(anyString());
    }

    @Test
    public void start() {
    }

    @Test
    public void getDeliveryProcedure() {
    }
}