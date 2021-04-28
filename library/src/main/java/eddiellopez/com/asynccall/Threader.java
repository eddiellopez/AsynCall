package eddiellopez.com.asynccall;


import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;


/**
 * Base class for threader.
 * <p>
 * Contains the execution and lifecycle logic.
 *
 * @param <T> The type of the threader.
 */
public abstract class Threader<T> implements LifecycleObserver {

    private static final String TAG = "Threader";

    @NonNull
    private final ExecutorService executor;

    @Nullable
    private final LifecycleOwner lifecycleOwner;

    private final OnExceptionHandler onExceptionHandler;

    private final AtomicBoolean deliver = new AtomicBoolean(true);

    /**
     * The basic threader.
     *
     * @param executor           The executor.
     * @param onExceptionHandler The exception handler.
     * @param lifecycleOwner     The lifecycle owner.
     */
    protected Threader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner
    ) {
        this.executor = executor;
        this.onExceptionHandler = onExceptionHandler;
        this.lifecycleOwner = lifecycleOwner;

        // Observe lifecycle events
        observeLifecycle();
    }

    /**
     * Starts executing the task.
     *
     * @throws IllegalStateException If no task was specified.
     */
    @UiThread
    public abstract void start();

    /**
     * Returns the code to execute for delivering the result.
     * For example, the callback to call.
     * <p>
     * Note this will only be called, when a lifecycle owner is under observation, if
     * in a resumed state.
     *
     * @param t The result.
     * @return The code to execute.
     */
    @NonNull
    protected abstract Runnable getDeliveryProcedure(T t);

    protected void submit(@NonNull Callable<T> callable) {
        executor.execute(() -> {
            try {
                // Run the action.
                final T result = callable.call();

                if (isUiThread()) {
                    // Deliver in the UI Thread
                    new Handler(Looper.getMainLooper()).post(() -> deliverResult(result));
                } else {
                    // Deliver in calling thread
                    deliverResult(result);
                }
            } catch (Exception e) {
                // Check if there is an exception handling setup
                if (onExceptionHandler != null) {
                    onExceptionHandler.onFailure(e);
                } else {
                    // Just throw the exception!?
                    throw new RuntimeException(e); // TODO: 3/9/21 Test this
                }
            }
        });
    }

    private void observeLifecycle() {
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().addObserver(this);
        }
    }

    private void stopObservingLifecycle() {
        if (lifecycleOwner != null) {
            lifecycleOwner.getLifecycle().removeObserver(this);
        }
    }

    // TODO: 3/9/21 Test lifecycle
    protected void deliverResult(T t) {
        if (deliver.get()) {
            getDeliveryProcedure(t).run();
        }

        // Finally always stop observing the lifecycle
        stopObservingLifecycle();
    }

    private boolean isUiThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Looper.getMainLooper().isCurrentThread();
        } else {
            return Looper.getMainLooper().getThread() == Thread.currentThread();
        }
    }

    @OnLifecycleEvent(ON_STOP)
    void onStopped() {
        // Don't deliver if the lifecycle owner stops.
        deliver.set(false);
    }

    @OnLifecycleEvent(ON_START)
    void onStarted() {
        deliver.set(true);
    }

}
