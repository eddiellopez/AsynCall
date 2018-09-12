package eddiellopez.com.asyncall;

import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Thread uiThread;
    private ExecutorService mExecutorService;

    @Before
    public void setUp() {
        uiThread = Looper.getMainLooper().getThread();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @After
    public void tearDown() {
        mExecutorService.shutdown();
    }

    @Test
    public void rawVariant() {
        AsynCall.OnResultListener<String> resultListener
                = result -> assertThat(result, is(not(uiThread.getName())));
        Callable<String> task = () -> Thread.currentThread().getName();

        // Raw variant without builder
        new AsynCall<>(resultListener).exec(task);
    }

    @Test
    public void defaultPoolExecutor() {
        // The task to run
        Callable<Thread> task = Thread::currentThread;
        // The result listener
        AsynCall.OnResultListener<Thread> resultListener = threadName -> {
            // Task should've been executed in a pool thread
            assertThat("Thread can't be UI Thread",
                    threadName, is(not(uiThread.getName())));
            // Result should be delivered in the UI thread
            assertThat(Thread.currentThread(), is(uiThread));
        };
        // The call
        new AsynCall.Builder<Thread>()
                .withTask(task)
                .withResultListener(resultListener)
                .build()
                .start();
    }

    @Test
    public void useRunnable() {
        new AsynCall.Builder<Integer>()
                .withTask(() -> assertThat(Thread.currentThread(), is(not(uiThread))))
                .build()
                .start();
    }

    @Test
    public void building() {
        new AsynCall.Builder<String>()
                .withTask(Thread::currentThread)
                .withTask(() -> Log.d("TEST", Thread.currentThread().getName()))
                .withResultListener(result -> fail())
                .build()
                .start();
    }

    @Test
    public void executor() {
        new AsynCall.Builder<>()
                .withTask(Thread::currentThread)
                .withResultListener(thread -> assertThat(uiThread, is(not(thread))))
                .withExecutor(mExecutorService)
                .build()
                .start();
    }

    @Test
    public void observer() {
        // TODO: 9/12/18 Create Test Lifecycle Owner
    }
}
