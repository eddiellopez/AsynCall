package eddiellopez.com.asynccall;


import static androidx.lifecycle.Lifecycle.Event.ON_START;
import static androidx.lifecycle.Lifecycle.Event.ON_STOP;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * Base class for threader.
 * <p>
 * Contains the execution and lifecycle logic.
 *
 * @param <T> The type of the threader.
 */
abstract class Threader<T> implements LifecycleObserver {

    @NonNull
    private final ExecutorService executor;

    @Nullable
    private final LifecycleOwner lifecycleOwner;

    private final OnExceptionHandler onExceptionHandler;

    private final AtomicBoolean deliver = new AtomicBoolean(true);

    private final DeliveryProcedure<T> deliveryProcedure;

    /**
     * The basic threader.
     *
     * @param executor           The executor.
     * @param onExceptionHandler The exception handler.
     * @param deliveryProcedure  The delivery procedure.
     * @param lifecycleOwner     The lifecycle owner.
     */
    protected Threader(
            @NonNull ExecutorService executor,
            @Nullable OnExceptionHandler onExceptionHandler,
            @Nullable LifecycleOwner lifecycleOwner,
            @NonNull DeliveryProcedure<T> deliveryProcedure
    ) {
        this.executor = executor;
        this.onExceptionHandler = onExceptionHandler;
        this.lifecycleOwner = lifecycleOwner;
        this.deliveryProcedure = deliveryProcedure;

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


    protected void submit(@NonNull Callable<T> callable) {
        // Deliver in the UI Thread if requested in the UI Thread.
        final boolean calledOnUiThread = isUiThread();

        executor.execute(() -> {
            try {
                // Run the action.
                final T result = callable.call();
                if (deliver.get()) {
                    finishExecution(calledOnUiThread, () -> deliveryProcedure.deliver(result));
                }

            } catch (Exception e) {
                // Check if there is an exception handling configured.
                if (onExceptionHandler != null) {
                    finishExecution(calledOnUiThread, () -> onExceptionHandler.onFailure(e));
                }
                // Can't be caught outside!. A fatal exception that crashes the app.
            }
        });
    }

    protected void finishExecution(boolean calledOnUiThread, Runnable deliver) {
        if (calledOnUiThread) {
            // Deliver in the UI Thread.
            new Handler(Looper.getMainLooper()).post(deliver);
        } else {
            // Deliver in the calling thread.
            deliver.run();
        }

        // Finally, always stop observing the lifecycle
        stopObservingLifecycle();
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
