package eddiellopez.com.asynccall;

import android.os.AsyncTask;

import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;

import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

/**
 * Builds {@link Threader} objects.
 */
public class Builder<T> {

    private ExecutorService executor;
    private OnConsumableResultListener<T> onConsumableResultListener;
    private Callable<T> callable;
    private Runnable runnable;
    private LifecycleOwner lifecycleOwner;
    private OnEmptyResultListener onEmptyResultListener;
    private OnExceptionHandler onExceptionHandler;

    /**
     * Specifies the task to run asynchronously.
     * Overrides any previously configured task.
     *
     * @param task The callable task.
     * @return This builder.
     */
    public Builder<T> async(Callable<T> task) {
        this.callable = task;
        this.runnable = null;
        return this;
    }

    /**
     * A convenience method to specify a task for which a result is not expected.
     * Overrides any previously configured task.
     *
     * @param task The task as a runnable.
     * @return This builder.
     */
    public Builder<T> async(Runnable task) {
        this.runnable = task;
        this.callable = null;
        return this;
    }

    /**
     * Specifies a result listener that delivers a result.
     * See also {@link #post(OnEmptyResultListener)}.
     *
     * @param listener The result listener.
     * @return This builder.
     */
    public Builder<T> post(@Nullable OnConsumableResultListener<T> listener) {
        this.onConsumableResultListener = listener;
        return this;
    }

    /**
     * Specifies a result listener that does not expects a result.
     * Note that a single listener will be called. See also {@link #post(OnConsumableResultListener)}.
     *
     * @param listener The result listener.
     * @return This builder.
     */
    public Builder<T> post(@Nullable OnEmptyResultListener listener) {
        this.onEmptyResultListener = listener;
        return this;
    }

    /**
     * Specifies an {@link Executor}. The default {@link AsyncTask#THREAD_POOL_EXECUTOR}
     * will be used if none specified.
     *
     * @param executor The executor.
     * @return This builder.
     */
    public Builder<T> withExecutorService(ExecutorService executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Observes a lifecycle component to determine if the result should be delivered.
     * If the owner is STOPPED, the result won't be delivered.
     *
     * @param lifecycleOwner The lifecycle owner
     * @return This builder.
     */
    public Builder<T> observe(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        return this;
    }

    public Builder<T> except(OnExceptionHandler onExceptionHandler) {
        this.onExceptionHandler = onExceptionHandler;
        return this;
    }

    /**
     * Builds and starts.
     */
    public void start() {
        if (callable != null) {
            new ResultThreader<>(
                    executor,
                    onExceptionHandler,
                    lifecycleOwner,
                    callable,
                    onConsumableResultListener
            ).start();
        } else if (runnable != null) {
            new EmptyThreader(
                    executor,
                    onExceptionHandler,
                    lifecycleOwner,
                    runnable,
                    onEmptyResultListener
            ).start();
        } else {
            throw new NullPointerException("A Threader cannot be started without a task!");
        }
    }
}
