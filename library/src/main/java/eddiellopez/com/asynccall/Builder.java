package eddiellopez.com.asynccall;

import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
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
    private LifecycleOwner lifecycleOwner;
    private OnExceptionHandler onExceptionHandler;
    private ThreaderFactory threaderFactory = new ThreaderFactory();

    /**
     * Specifies the calla to run asynchronously.
     * Overrides any previously configured calla.
     *
     * @param task The callable task.
     * @return This builder.
     */
    public Builder<T> async(@NonNull Callable<T> task) {
        this.callable = task;
        return this;
    }

    /**
     * Specifies a result listener that delivers a result.
     *
     * @param listener The result listener.
     * @return This builder.
     */
    public Builder<T> onResult(@Nullable OnConsumableResultListener<T> listener) {
        this.onConsumableResultListener = listener;
        return this;
    }

    /**
     * Specifies an {@link Executor}. The default {@link AsyncTask#THREAD_POOL_EXECUTOR}
     * will be used if none specified.
     *
     * @param executor The executor.
     * @return This builder.
     */
    public Builder<T> withExecutorService(@NonNull ExecutorService executor) {
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
    public Builder<T> observe(@NonNull LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        return this;
    }

    /**
     * Provides an exception handler that will handle exceptions on the asynchronous task.
     * If not provided, the responsibility of exception handling falls on the client.
     *
     * @param onExceptionHandler The handler, called when an exception happens in the task.
     * @return This builder.
     */
    public Builder<T> except(@NonNull OnExceptionHandler onExceptionHandler) {
        this.onExceptionHandler = onExceptionHandler;
        return this;
    }

    /**
     * Builds and starts.
     * After a task is started, it shouldn't be reused.
     */
    public void start() {
        if (callable == null) {
            throw new NullPointerException("A Threader cannot be started without a task!");
        }

        if (executor == null) {
            throw new NullPointerException("A Threader cannot be started without an Executor");
        }

        getThreaderFactory().from(
                executor,
                onExceptionHandler,
                lifecycleOwner,
                callable,
                onConsumableResultListener
        ).start();
    }

    @VisibleForTesting
    ThreaderFactory getThreaderFactory() {
        return threaderFactory;
    }

    @VisibleForTesting
    void setThreaderFactory(ThreaderFactory threaderFactory) {
        this.threaderFactory = threaderFactory;
    }
}
