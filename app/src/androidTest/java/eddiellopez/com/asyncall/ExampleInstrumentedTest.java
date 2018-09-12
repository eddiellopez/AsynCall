package eddiellopez.com.asyncall;

import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    private Thread uiThread;

    @Before
    public void setUp() {
        uiThread = Looper.getMainLooper().getThread();
    }

    @Test
    public void defaultPoolExecutor() {
        new AsynCall.Builder<String>()
                .withTask(() -> {
                    Thread.currentThread().getName();
                })
                .withResultListener(threadName -> assertThat("Thread can't be UI Thread",
                        threadName, is(not(uiThread.getName()))))
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
}
