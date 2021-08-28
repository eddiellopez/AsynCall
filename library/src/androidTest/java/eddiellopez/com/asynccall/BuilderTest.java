package eddiellopez.com.asynccall;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.Lifecycle;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class BuilderTest {

    private static final String TAG = "AsyncInstrumentedTest";
    private Thread uiThread;
    private ExecutorService executorService;


    @Before
    public void setUp() {
        uiThread = Looper.getMainLooper().getThread();
        executorService = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() {
        executorService.shutdown();
    }

    @Test
    public void asyncFromAnyThread() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given a task:
        final Callable<String> task = () -> Thread.currentThread().getName();

        // Given the following result listener:
        final OnConsumableResultListener<String> resultListener = threadName -> {
            // The task should have been executed in a pool thread:
            assertThat("Thread can't be UI Thread",
                    threadName, is(not(uiThread.getName())));
            // Result should not be delivered in the UI Thread
            assertThat("Results shall always be delivered on the UI Thread",
                    Thread.currentThread(), is(not(uiThread)));
            latch.countDown();
        };

        //  When started from any worker thread (this test thread):
        new Builder<String>()
                .withExecutorService(executorService)
                .async(task)
                .onResult(resultListener)
                .start();

        // We always expect the operation to be completed.
        try {
            boolean countedDown = latch.await(2, TimeUnit.SECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
    }

    @Test
    public void asyncFromUiThread() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given a task:
        final Callable<String> task = () -> Thread.currentThread().getName();

        // Given the following result listener:
        final OnConsumableResultListener<String> resultListener = threadName -> {
            // The task should have been executed in a pool thread:
            assertThat("Thread can't be UI Thread",
                    threadName, is(not(uiThread.getName())));
            // Result should be delivered in the UI Thread
            assertThat("Results shall always be delivered on the UI Thread",
                    Thread.currentThread(), is(uiThread));
            latch.countDown();
        };

        runOnTheUiThread(() -> {
            //  When started from the UI thread.
            new Builder<String>()
                    .withExecutorService(executorService)
                    .async(task)
                    .onResult(resultListener)
                    .start();
        });

        // We always expect the operation to be completed.
        try {
            boolean countedDown = latch.await(2, TimeUnit.SECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
    }

    @Test
    public void noResultCallback() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given an async operation without result listener:
        new Builder<Integer>()
                .withExecutorService(executorService)
                .async(() -> {
                    // The task should never run in the UI Thread.
                    assertThat("A task should never run in the UI Thread!",
                            Thread.currentThread(), is(not(uiThread)));
                    latch.countDown();
                    return 0;
                })
                .start();

        // We always expect the operation to be completed.
        try {
            boolean countedDown = latch.await(2, TimeUnit.SECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
    }

    @Test
    public void onExceptionUnHandled() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given an task that throws exception:
        final Callable<Integer> exceptionTask = () -> {
            // The task should never run in the UI Thread.
            assertThat("A task should never run in the UI Thread!",
                    Thread.currentThread(), is(not(uiThread)));

            throw new UnsupportedOperationException("An Exception happened!");
        };

        new Builder<Integer>()
                .withExecutorService(executorService)
                .async(exceptionTask)
                .start();

        // We don't expect the operation to be completed.
        try {
            boolean countedDown = latch.await(4, TimeUnit.SECONDS);
            assertThat(countedDown, is(false));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
    }

    @Test
    public void onExceptionHandled() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given an task that throws exception:
        final Callable<Integer> exceptionTask = () -> {
            // The task should never run in the UI Thread.
            assertThat("A task should never run in the UI Thread!",
                    Thread.currentThread(), is(not(uiThread)));

            throw new UnsupportedOperationException("An Exception happened!");
        };
        new Builder<Integer>()
                .withExecutorService(executorService)
                .async(exceptionTask)
                .except(exception -> {
                    // We expect this exception to be handled here.
                    latch.countDown();
                })
                .start();

        // We always expect the operation to be completed.
        try {
            boolean countedDown = latch.await(2, TimeUnit.SECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
    }

    @Test
    public void buildingMultipleInput() {
        final CountDownLatch latch = new CountDownLatch(1);

        // Given we build with several actions and listeners:
        // We expect only the last pair is used.
        new Builder<String>()
                .withExecutorService(executorService)
                .async(() -> {
                    fail("Should never run");
                    return "Failure";
                })
                .async(() -> {
                    fail("Should never run");
                    return "Another Failure";
                })
                .onResult(result -> fail("This should've never been called!"))
                .async(() -> "Success")
                .onResult(result -> {
                    assertThat(result, is("Success"));
                    assertThat("Results are delivered in a worked thread by default!",
                            Thread.currentThread(), is(not(uiThread)));
                    latch.countDown();
                })
                .start();

        try {
            boolean countedDown = latch.await(2, TimeUnit.SECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
        assertThat("Latch should have been counted down!", latch.getCount(), is(0L));
    }

    @Test
    public void asyncStarted() {
        // Given the lifecycle state is RESUMED:
        final TestLifecycleOwner testLifecycleOwner = new TestLifecycleOwner(Lifecycle.State.INITIALIZED);
        testLifecycleOwner
                .stateUp() // CREATED
                .stateUp() // STARTED
                .stateUp(); // RESUMED

        assertThat("Test classes shouldn't fail tests!",
                testLifecycleOwner.getLifecycle().getCurrentState(), is(Lifecycle.State.RESUMED));

        final CountDownLatch latch = new CountDownLatch(1);

        new Builder<String>()
                .withExecutorService(executorService)
                .async(() -> {
                    try {
                        // Give enough time to change the lifecycle in the test thread.
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "observer: Interrupted!", e);
                    }

                    return "Done";
                })
                .onResult(result -> {
                    // See await at the end of this test.
                    latch.countDown();
                    assertThat("This thread should not be the UI Thread!",
                            uiThread, is(not(Thread.currentThread())));
                    assertThat("Failed to match the countdown. " +
                            "lifecycle owner is stopped!", 0, is(1));
                })
                .observe(testLifecycleOwner)
                .start();

        // When the lifecycle moves to STARTED:
        testLifecycleOwner.stateDown();
        assertThat(
                "Test classes shouldn't fail tests!",
                testLifecycleOwner.getLifecycle().getCurrentState(),
                is(Lifecycle.State.STARTED)
        );

        // STARTED, shouldn't affect AsyncCall, we expect the result to be delived.
        try {
            boolean countedDown = latch.await(3000, TimeUnit.MILLISECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
    }

    @Test
    public void asyncStopped() {
        // Given the lifecycle state is RESUMED:
        final TestLifecycleOwner testLifecycleOwner = new TestLifecycleOwner(Lifecycle.State.INITIALIZED);
        testLifecycleOwner
                .stateUp() // CREATED
                .stateUp() // STARTED
                .stateUp(); // RESUMED

        assertThat("Test classes shouldn't fail tests!",
                testLifecycleOwner.getLifecycle().getCurrentState(), is(Lifecycle.State.RESUMED));

        // Hold this thought...
        final CountDownLatch latch = new CountDownLatch(1);

        new Builder<String>()
                .withExecutorService(executorService)
                .async(() -> {
                    try {
                        // Give enough time to change the lifecycle in the test thread.
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "observer: Interrupted!", e);
                    }

                    return "Done";
                })
                .onResult(result -> {
                    // See await at the end of this test.
                    latch.countDown();
                    assertThat("This thread should not be the UI Thread!",
                            uiThread, is(not(Thread.currentThread())));
                    assertThat("Failed, this should never run since the " +
                            "lifecycle owner is stopped!", 0, is(1));
                })
                .observe(testLifecycleOwner)
                .start();

        // Given we step down to STOPPED:
        testLifecycleOwner.stateDown();
        assertThat(
                "Test classes shouldn't fail tests!",
                testLifecycleOwner.getLifecycle().getCurrentState(),
                is(Lifecycle.State.STARTED)
        );
        testLifecycleOwner.stateDown();
        assertThat(
                "Test classes shouldn't fail tests!",
                testLifecycleOwner.getLifecycle().getCurrentState(),
                is(Lifecycle.State.CREATED)
        );

        // STOPPED should prevent delivery, we expect a result.
        try {
            boolean countedDown = latch.await(3000, TimeUnit.MILLISECONDS);
            assertThat(countedDown, is(true));
        } catch (InterruptedException e) {
            // Nothing to do here
        }
    }

    private void runOnTheUiThread(Runnable runnable) {
        Handler.createAsync(Looper.getMainLooper()).post(runnable);
    }
}
